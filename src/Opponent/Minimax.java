package Opponent;

import Engine.Board.Board;
import Engine.Board.Move;
import Engine.Board.Utility;
import Engine.Pieces.Piece;
import Engine.Player.MoveTransition;
import GUI.MoveLog;
import Engine.Player.Player;
import Opponent.TableEntry.NodeType;
import Engine.Board.Move.MoveFactory;

import java.util.*;

/**
 * Minimax
 *
 * <p>
 * Minimax is a search algorithm capable of choosing optimal moves in a zero-sum
 * game. A zero-sum game is one in which the respective gains and losses of the
 * two players (or teams) cancel each other out. This property ensures that a
 * loss for one player results in an equivalent gain for the other.
 *
 * <p>
 * The goal of minimax is to choose a move which minimizes the maximum loss
 * for the current player. The two players present in the game are designated
 * maximizer and minimizer and a sub-tree is created for each of the current
 * player's legal moves. At each level of recursion, the current player
 * alternates. at each node, all legal moves are tried out individually. When
 * the algorithm reaches a terminal node, the board is evaluated and scored,
 * and this score is returned up the tree. Each internal node selects only the
 * best score for its current player (minimum for minimizer, maximum for
 * maximizer). In this way, a single score is propagated back up each tree to
 * represent each of the initiating player's moves. Here, the highest-scoring
 * move is chosen for the initiating player. This process ensures that only
 * the wort-case scenario is considered for the initiating player-- A very
 * smart opponent.
 *
 * <p>
 * This minimax algorithm uses alpha-beta pruning to cut off large portions of
 * the search tree that need not be visited. If a part of the tree is guaranteed
 * to be cut off by a min or max node, then there is no need to look any further.
 * We can make this guarantee by setting the values of variables 'alpha' and 'beta'
 * as a score is returned from a child node up into a parent node. This process
 * relies on the fact that each search tree is navigated both iteratively and
 * recursively. For Example, after returning from a child min-node into a parent
 * max-node, alpha is set, and the algorithm enters the next child min-node. The
 * value that this min-node returns must be between alpha and beta. Beta is now
 * set during the execution of the min-node. If beta is less than alpha (since
 * this is a min-node), it will be impossible to find a value that is both greater
 * than alpha and less than beta. At this point, there is no need to continue
 * searching past this node, so we simply return beta.
 *
 * <p>
 * This alpha-beta optimization works best if the moves are sorted beforehand.
 * If the algorithm is presented with higher-scoring moves first, it will be
 * enabled to prune as much of the current tree as possible. For this reason,
 * moves are typically ordered with attack moves before passive moves. The
 * attack moves are then sorted by the value of the attacked piece. E.g. an
 * attack on a queen will come before an attack on a rook etc.
 *
 * <p>
 * This minimax algorithm is equipped with a dynamic transposition table,
 * allowing it to avoid repetitious calculations. If the current board has an
 * exact score associated with it in the transposition table, then that score
 * may be used in place of a deeper search. The table used in this particular
 * algorithm is a {@code HashMap} with Zobrist hash-keys. This {@code HashMap}
 * allows for constant-time retrieval of mapped scores.
 *
 * @author Ellie Moore
 * @version 06.26.2020
 */
public final class Minimax implements MoveStrategy {

    /*
     * Static Move comparator for use in sorting AttackMoves.
     * This comparator compares two generic Moves. Type must be verified
     * beforehand in order to avoid a Null Pointer Exception.
     */
    protected static final Comparator<Move> ATTACK_COMPARATOR;
    static {
        ATTACK_COMPARATOR = (o1, o2) -> Integer.compare(
                (o2.getAttackedPiece().getPieceValue()),
                (o1.getAttackedPiece().getPieceValue())
        );
    }

    /**
     * The depth of the search. This variable represents the depth limit for
     * both the initial search and quiescence search.
     */
    private int depth;

    /**
     * A transposition table for use in mapping {@code Board} states to scores.
     * This table enables {@code Minimax} to avoid searching for a score that
     * it has already calculated.
     */
    private HashMap<Long, TableEntry> transpositions;

    /**
     * A {@code List} of execution times for use in debugging and calculating
     * the average execution time for {@code Minimax}.
     */
    protected final ArrayList<Double> executionTimes;

    /**
     * A {@code QuiescenceSearch}. This is an additional search which starts
     * from every violent, terminal node of minimax.
     */
    protected final QuiescenceSearch quiescenceSearch;

    /**
     * A {@code PromotionSearch}. This is an additional search used to encourage
     * pawn promotion once the board has lost its power pieces.
     */
    protected final PromotionSearch promotionSearch;

    /**
     * A standard {@code BoardEvaluator}. This is an evaluation tool which can
     * be used to assess the state of the current board, producing an integer
     * score.
     */
    protected final BoardEvaluator evaluator;

    /**
     * A public constructor for {@code Minimax}.
     *
     * @param depth the depth of the search.
     */
    public Minimax(final int depth,
                   final int quiescenceDepth,
                   final int promotionDepth){
        this.depth = depth;
        this.quiescenceSearch = new QuiescenceSearch(quiescenceDepth);
        this.promotionSearch = new PromotionSearch(promotionDepth);
        this.transpositions = new HashMap<>(100000);
        this.executionTimes = new ArrayList<>();
        this.evaluator = new BoardEvaluator(){};
    }

    /**
     * Exposes the depth.
     *
     * @return the depth
     */
    public int getDepth(){
        return this.depth;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString(){
        return "Minimax";
    }

    /**
     * A method to calculate the average execution time.
     *
     * @return the average execution time.
     */
    public double getAverageExecutionTime(){
        double sum = 0.0;
        for(double d: executionTimes){
            sum += d;
        }
        return executionTimes.isEmpty()? 0: sum/executionTimes.size();
    }

    /**
     * @inheritDoc
     */
    @Override
    public Move execute(Board board, MoveLog log, boolean allMovesAreExhausted) {

        transpositions = new HashMap<>(100000);

        // Print depth and number of moves to terminal/console.
        System.out.println(board.currentPlayer() + " thinking with depth = " + depth);
        System.out.println("Considering " + board.currentPlayer().getLegalMoves().size() + " moves.");

        // Record start time.
        final long startTime = System.currentTimeMillis();

        // Determine whether or not the player is weak enough to search for promotion.
        //final boolean boardLacksPowerPieces = board.currentPlayer().getPieceValueScore() <= 11400;

        /*
         * Initialize alpha and beta. Set 'highestValue' and 'lowestValue' to java min and max
         * integer values respectively. Either 'highestValue' or 'lowestValue' will be replaced
         * as the first move is scored, depending on the alliance of the current player.
         */
        final int alpha;
        final int beta;
        int highestValue = alpha = Integer.MIN_VALUE;
        int lowestValue = beta = Integer.MAX_VALUE;

        // Get the current player's legal moves.
        final Collection<Move> legalMoves = board.currentPlayer().getLegalMoves();

        /*
         * Calculate the Zobrist hash code for the current board. This code will be used as a
         * base for future hash codes, and allow for constant-time score lookup via transposition
         * table.
         */
        final long hash = board.zobristHash();

        // Initialize 'bestMove' to null.
        Move bestMove = null;

        /*
         * Iterate through all of the current players legal moves and try out each one.
         * Search a minimax tree for each move and return the move with the minimum (if
         * current player is minimizer) or maximum (if current player is maximizer) score.
         */
        for(final Move move: legalMoves){

            // If the move is not repeating a move made recently in the game.
            if(allMovesAreExhausted || !isRepeating(move, log) ||
               board.currentPlayer().getLegalMoves().size() <= 1) {

                // Make the move.
                final MoveTransition moveTransition = board.currentPlayer().makeMove(move, true);

                // If the move is legal and doesn't leave the current player in check.
                if(moveTransition.getMoveStatus().isDone()) {

                    // Search to score the move. White is maximizer, black is minimizer.
                    // Update the board's hash code upon entering into the search tree.
                    final int currentValue = board.currentPlayer().getAlliance().isWhite() ?
                            min(
                                    moveTransition.getTransitionBoard(),
                                    depth - 1, alpha, beta, move, updateHash(hash, move)
                            ) :
                            max(
                                    moveTransition.getTransitionBoard(),
                                    depth - 1, alpha, beta, move, updateHash(hash, move)
                            );

                    // Select the best move.
                    if (board.currentPlayer().getAlliance().isWhite() &&
                            currentValue >= highestValue) {
                        highestValue = currentValue;
                        bestMove = move;
                    } else if (board.currentPlayer().getAlliance().isBlack() &&
                            currentValue <= lowestValue) {
                        lowestValue = currentValue;
                        bestMove = move;
                    }

                }
            }
        }
        System.out.println(transpositions.size());

        // If a best move isn't found, try again. But this time include
        // all moves, even those that are found in the move log.
        if(bestMove == null) return execute(board, log, true);

        // Calculate and stow execution time.
        final double time = ((double)(System.currentTimeMillis() - startTime))/1000.0;
        executionTimes.add(time);

        // Print execution time to terminal/console.
        System.out.println(String.format("%.2f seconds", time));

        return bestMove;

    }

    private static boolean isRepeating(Move move, MoveLog log){
        for (Move m: log.getMoves()) if (m.equals(move)) return true;
        return false;
    }

    private static boolean isEndGame(final Board board){
        final boolean hasEscapeMoves = board.currentPlayer().hasEscapeMoves();
        return board.currentPlayer().isInCheckMate(hasEscapeMoves) ||
                board.currentPlayer().isInStaleMate(hasEscapeMoves);
    }

    // Order moves pre-search to prune as many sub-trees as possible.
    private static List<Move> orderedMoves(final Board board, final Move tableMove, final int depth, final int entryDepth) {
        final List<Move> attackMoves = new ArrayList<>();
        final List<Move> passiveMoves = new ArrayList<>();
        final Move foundMove = MoveFactory.produce(
                board, tableMove.getCurrentCoordinate(), tableMove.getDestinationCoordinate()
        );
        for (Move move : board.currentPlayer().getLegalMoves()) {
            if (!move.equals(foundMove)) {
                if (move.isAttack()) attackMoves.add(move);
                else passiveMoves.add(move);
            }
        }
        /*if (tableMove != MoveFactory.NULL_MOVE) {
            System.out.println(board.currentPlayer().getLegalMoves());
            System.out.println(board.currentPlayer().getAlliance());
            System.out.println(tableMove.getMovedPiece().getPieceAlliance());
            System.out.println(tableMove);
            System.out.println(foundMove);
            System.out.println("node depth " + depth);
            System.out.println("entry depth " + entryDepth);
        }*/
        attackMoves.sort(ATTACK_COMPARATOR);
        if(foundMove != MoveFactory.NULL_MOVE){
            if(foundMove.isAttack())attackMoves.add(0, foundMove);
            else passiveMoves.add(0, foundMove);
        }
        return (List<Move>) Utility.concat(attackMoves, passiveMoves);
    }

    /*
     * This method uses XOR to update the board's hash code, XORing the
     * piece out of the hash code and then back in at its destination.
     */
    private static long updateHash(final long hash, final Move move){
        long h = hash;
        Piece movedPiece = move.getMovedPiece();
        h ^= movedPiece.zobristHash(move.getCurrentCoordinate());
        h ^= movedPiece.zobristHash(move.getDestinationCoordinate());
        return h;
    }

    private final class Pack {
        final int score;
        final Move move;
        public Pack(final int score, final Move move){
            this.score = score;
            this.move = move;
        }
    }

    private int min(final Board board, final int depth, int alpha, int beta,
                    final Move prev, long nodeHash){
        final boolean isEndGame = isEndGame(board);
        if(depth == 0 || isEndGame){
            /*if(!isEndGame){
                if(prev.isAttack()) {
                    final TableEntry nodeEntry = transpositions.get(nodeHash);
                    Move tableMove = MoveFactory.NULL_MOVE;
                    if(nodeEntry != null && depth == nodeEntry.depth && nodeEntry.type.isBeta()) tableMove = nodeEntry.move;
                    final List<Move> retaliationMoves = quiescenceSearch.orderedAttackMoves(board, tableMove);
                    if (!retaliationMoves.isEmpty()) return quiescenceSearch.min(
                            board, depth - 1, alpha, beta, prev, retaliationMoves, nodeHash
                    );
                }
            }*/
            return evaluator.evaluate(board, depth);
        }
        final TableEntry nodeEntry = transpositions.get(nodeHash);
        Move tableMove = MoveFactory.NULL_MOVE;
        if(nodeEntry != null && depth == nodeEntry.depth && nodeEntry.type.isBeta()) tableMove = nodeEntry.move;
        int lowestValue = Integer.MAX_VALUE;
        Move bestMove = MoveFactory.NULL_MOVE;
        for(final Move move: orderedMoves(board, tableMove, depth, nodeEntry != null? nodeEntry.depth: 0)){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move, true);
            if(moveTransition.getMoveStatus().isDone()){
                final long currentHash = updateHash(nodeHash, move);
                final int currentValue; final TableEntry foundEntry = transpositions.get(currentHash);
                TableEntry newEntry = null;
                if(foundEntry != null && foundEntry.depth == depth && foundEntry.type.isExact()) {
                    currentValue = foundEntry.score;
                    foundEntry.keep = true;
                } else {
                    currentValue = max(
                        moveTransition.getTransitionBoard(),
                        depth - 1, alpha, beta, move, currentHash
                    );
                }
                if(currentValue <= lowestValue) {
                    lowestValue = currentValue; beta = Math.min(beta, lowestValue);
                    bestMove = move;
                }
                if(beta <= alpha) {
                    if(nodeEntry == null || nodeEntry.depth < depth || !nodeEntry.keep) {
                        transpositions.put(nodeHash, newEntry = new TableEntry(lowestValue, depth, bestMove));
                        newEntry.type = NodeType.BETA;
                    }
                    return lowestValue;
                }
                //if(newEntry != null) System.out.println(newEntry.type);
            }
        }
        TableEntry newEntry;
        if(nodeEntry == null || nodeEntry.depth < depth || !nodeEntry.keep) {
            transpositions.put(nodeHash, newEntry = new TableEntry(lowestValue, depth, bestMove));
            if (lowestValue < alpha) newEntry.type = NodeType.ALPHA;
        }
        return lowestValue;
    }

    private int max(final Board board, final int depth, int alpha, int beta,
                    final Move prev, long nodeHash){
        final boolean isEndGame = isEndGame(board);
        if(depth == 0 || isEndGame){
            /*if(!isEndGame){
                if(prev.isAttack()) {
                    final TableEntry nodeEntry = transpositions.get(nodeHash);
                    Move tableMove = MoveFactory.NULL_MOVE;
                    if(nodeEntry != null && depth == nodeEntry.depth && nodeEntry.type.isBeta()) tableMove = nodeEntry.move;
                    final List<Move> retaliationMoves = quiescenceSearch.orderedAttackMoves(board, tableMove);
                    if (!retaliationMoves.isEmpty()) return quiescenceSearch.max(
                            board, depth - 1, alpha, beta, prev, retaliationMoves, nodeHash
                    );
                }
            }*/
            return evaluator.evaluate(board, depth);
        }
        final TableEntry nodeEntry = transpositions.get(nodeHash);
        Move tableMove = MoveFactory.NULL_MOVE;
        if(nodeEntry != null && depth == nodeEntry.depth && nodeEntry.type.isBeta()) tableMove = nodeEntry.move;
        int highestValue = Integer.MIN_VALUE;
        Move bestMove = MoveFactory.NULL_MOVE;
        for(final Move move: orderedMoves(board, tableMove, depth, nodeEntry != null? nodeEntry.depth: 0)){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move, true);
            if(moveTransition.getMoveStatus().isDone()){
                final long currentHash = updateHash(nodeHash, move);
                final int currentValue; final TableEntry foundEntry = transpositions.get(currentHash);
                TableEntry newEntry = null;
                if(foundEntry != null && foundEntry.depth == depth && foundEntry.type.isExact()) {
                    currentValue = foundEntry.score;
                    foundEntry.keep = true;
                } else {
                    currentValue = min(
                            moveTransition.getTransitionBoard(),
                            depth - 1, alpha, beta, move, currentHash
                    );
                }
                if(currentValue >= highestValue){
                    highestValue = currentValue;
                    alpha = Math.max(alpha, highestValue);
                    bestMove = move;
                }
                if(beta <= alpha) {
                    if(nodeEntry == null || nodeEntry.depth < depth || !nodeEntry.keep) {
                        transpositions.put(nodeHash, newEntry = new TableEntry(highestValue, depth, bestMove));
                        newEntry.type = NodeType.BETA;
                    }
                    return highestValue;
                }
                //if(newEntry != null) System.out.println(newEntry.type);
            }
        }
        TableEntry newEntry;
        if(nodeEntry == null || nodeEntry.depth < depth || !nodeEntry.keep) {
            transpositions.put(nodeHash, newEntry = new TableEntry(highestValue, depth, bestMove));
            if (highestValue < alpha) newEntry.type = NodeType.ALPHA;
        }
        return highestValue;
    }

    /**
     * QuiescenceSearch
     *
     * @author Ellie Moore
     * @version 07.09.2020
     */
    private final class QuiescenceSearch {

        private final int depth;

        private QuiescenceSearch(int depth){
            this.depth = depth;
        }

        private List<Move> orderedAttackMoves(final Board board, final Move tableMove){
            final List<Move> attackMoves = new ArrayList<>();
            final Move foundMove = tableMove == null? MoveFactory.NULL_MOVE: MoveFactory.produce(
                    board, tableMove.getCurrentCoordinate(), tableMove.getDestinationCoordinate()
            );
            for(Move move: board.currentPlayer().getLegalMoves()){
                if(move.isAttack() && !move.equals(foundMove)) attackMoves.add(move);
            }
            attackMoves.sort(ATTACK_COMPARATOR);
            if(foundMove != MoveFactory.NULL_MOVE && foundMove.isAttack()) attackMoves.add(0, foundMove);
            return Collections.unmodifiableList(attackMoves);
        }

        private int min(final Board board, final int depth,
                                 int alpha, int beta, final Move prev, final List<Move> legalAttackMoves,
                                 long nodeHash){
            if(depth == -this.depth || Minimax.isEndGame(board) || legalAttackMoves.isEmpty()) {
                return evaluator.evaluate(board, depth);
            }
            int lowestValue = Integer.MAX_VALUE;
            for(final Move move: legalAttackMoves){
                final MoveTransition moveTransition = board.currentPlayer().makeMove(move, true);
                if (moveTransition.getMoveStatus().isDone()) {
                    final long currentHash = Minimax.updateHash(nodeHash, move);
                    final Board newBoard = moveTransition.getTransitionBoard();
                    final int currentValue;
                    final TableEntry foundEntry = transpositions.get(currentHash);
                    TableEntry newEntry = null;
                    if(foundEntry != null && foundEntry.depth == depth && foundEntry.type.isExact()) {
                            currentValue = foundEntry.score;
                        foundEntry.keep = true;
                    } else {
                        final TableEntry nodeEntry = transpositions.get(nodeHash);
                        Move tableMove = MoveFactory.NULL_MOVE;
                        if(nodeEntry != null && depth == nodeEntry.depth && nodeEntry.type.isBeta()) tableMove = nodeEntry.move;
                        currentValue = max(
                                newBoard,
                                depth - 1, alpha, beta, move,
                                orderedAttackMoves(newBoard, tableMove), currentHash
                        );
                        if(foundEntry == null || foundEntry.depth < depth || !foundEntry.keep) {
                            transpositions.put(currentHash, newEntry = new TableEntry(currentValue, depth, move));
                            if (currentValue < alpha) newEntry.type = NodeType.ALPHA;
                        }
                    }
                    if (currentValue <= lowestValue) {
                        lowestValue = currentValue;
                        beta = Math.min(beta, lowestValue);
                    }
                    if(beta <= alpha) {
                        if(newEntry != null) newEntry.type = NodeType.BETA; return lowestValue;
                    }
                    //if(newEntry != null) System.out.println(newEntry.type);
                }

            }
            return lowestValue;

        }

        private int max(final Board board, final int depth,
                                 int alpha, int beta,
                                 final Move prev,
                                 final List<Move> legalAttackMoves,
                                 long nodeHash){
            if(depth == -this.depth || Minimax.isEndGame(board) || legalAttackMoves.isEmpty()) {
                return evaluator.evaluate(board, depth);
            }
            int highestValue = Integer.MIN_VALUE;
            for(final Move move: legalAttackMoves){
                final MoveTransition moveTransition = board.currentPlayer().makeMove(move, true);
                if (moveTransition.getMoveStatus().isDone()) {
                    final long currentHash = Minimax.updateHash(nodeHash, move);
                    final Board newBoard = moveTransition.getTransitionBoard();
                    final int currentValue;
                    final TableEntry foundEntry = transpositions.get(currentHash);
                    TableEntry newEntry = null;
                    if(foundEntry != null && foundEntry.depth == depth && foundEntry.type.isExact()) {
                            currentValue = foundEntry.score;
                        foundEntry.keep = true;
                    } else {
                        final TableEntry nodeEntry = transpositions.get(nodeHash);
                        Move tableMove = MoveFactory.NULL_MOVE;
                        if(nodeEntry != null && depth == nodeEntry.depth && nodeEntry.type.isBeta()) tableMove = nodeEntry.move;
                        currentValue = min(
                                newBoard,
                                depth - 1, alpha, beta, move,
                                orderedAttackMoves(newBoard, tableMove), currentHash
                        );
                        if(foundEntry == null || foundEntry.depth < depth || !foundEntry.keep) {
                            transpositions.put(currentHash, newEntry = new TableEntry(currentValue, depth, move));
                            if (currentValue < alpha) newEntry.type = NodeType.ALPHA;
                        }
                    }
                    if (currentValue >= highestValue) {
                        highestValue = currentValue;
                        alpha = Math.max(alpha, highestValue);
                    }
                    if(beta <= alpha) {
                        if(newEntry != null) newEntry.type = NodeType.BETA; return highestValue;
                    }
                    //if(newEntry != null) System.out.println(newEntry.type);
                }
            }
            return highestValue;

        }

    }

    /**
     * Promotion Search
     *
     * @author Ellie Moore
     * @version 07.09.2020
     */
    public final class PromotionSearch {

        private final int depth;

        public PromotionSearch(final int depth){
            this.depth = depth;
        }

        private Collection<Move> attackOnPawnMoves(final Board board){
            final List<Move> attackOnPawnMoves = new ArrayList<>();
            for(Move move: board.currentPlayer().getLegalMoves()){
                if(move.isAttack() && move.getAttackedPiece().getPieceType().isPawn()) attackOnPawnMoves.add(move);
            }
            return attackOnPawnMoves;
        }

        private Collection<Move> orderedPawnMoves(final Board board){
            final List<Move> pawnAttackMoves = new ArrayList<>();
            final List<Move> pawnMoves = new ArrayList<>();
            for(Move move: board.currentPlayer().getLegalMoves()){
                if(move.getMovedPiece().getPieceType().isPawn()){
                    if(move.isAttack()) pawnAttackMoves.add(move);
                    else pawnMoves.add(move);
                }
            }
            pawnAttackMoves.sort(ATTACK_COMPARATOR);
            return Utility.concat(pawnAttackMoves, pawnMoves);
        }

        private int min(final Board board, final int depth,
                        int alpha, int beta, final Move prev, final Collection<Move> moves,
                        long prevHash, final Player initiator, final boolean isDefensive){
            if(depth == -this.depth || Minimax.isEndGame(board) || initiator.hasPromoted()) {
                return evaluator.evaluate(board, depth);
            }
            int lowestValue = Integer.MAX_VALUE;
            for(final Move move: moves){
                final MoveTransition moveTransition = board.currentPlayer().makeMove(move, true);
                if (moveTransition.getMoveStatus().isDone()) {
                    final long currentHash = Minimax.updateHash(prevHash, move);
                    final Board newBoard = moveTransition.getTransitionBoard();
                    final int currentValue;
                    final TableEntry foundEntry = transpositions.get(currentHash);
                    if(foundEntry != null && foundEntry.depth == depth && foundEntry.type.isExact()) {
                        currentValue = foundEntry.score;
                        foundEntry.keep = true;
                    } else {
                        currentValue = max(
                                newBoard,
                                depth - 1, alpha, beta, move,
                                isDefensive? orderedPawnMoves(newBoard): attackOnPawnMoves(newBoard), currentHash, initiator, !isDefensive
                        );
                        if(foundEntry == null || foundEntry.depth > depth) transpositions.put(
                                currentHash, new TableEntry(currentValue, depth, move)
                        );
                    }
                    if (currentValue <= lowestValue) {
                        lowestValue = currentValue;
                        beta = Math.min(beta, lowestValue);
                    }
                    if(beta <= alpha) {
                        transpositions.get(currentHash).type = NodeType.BETA;
                        return lowestValue;
                    }

                }

            }
            return lowestValue;

        }

        private int max(final Board board, final int depth,
                        int alpha, int beta,
                        final Move prev,
                        final Collection<Move> moves,
                        long prevHash, final Player initiator, final boolean isDefensive){
            if(depth == -this.depth || Minimax.isEndGame(board) || initiator.hasPromoted()) {
                return evaluator.evaluate(board, depth);
            }
            int highestValue = Integer.MIN_VALUE;
            for(final Move move: moves){
                final MoveTransition moveTransition = board.currentPlayer().makeMove(move, true);
                if (moveTransition.getMoveStatus().isDone()) {
                    final long currentHash = Minimax.updateHash(prevHash, move);
                    final Board newBoard = moveTransition.getTransitionBoard();
                    final int currentValue;
                    final TableEntry foundEntry = transpositions.get(currentHash);
                    if(foundEntry != null && foundEntry.depth == depth && foundEntry.type.isExact()) {
                        currentValue = foundEntry.score;
                        foundEntry.keep = true;
                    } else {
                        currentValue = min(
                                newBoard,
                                depth - 1, alpha, beta, move,
                                isDefensive? orderedPawnMoves(newBoard): attackOnPawnMoves(newBoard), currentHash, initiator , !isDefensive
                        );
                        if(foundEntry == null || foundEntry.depth > depth) transpositions.put(
                                currentHash, new TableEntry(currentValue, depth, move)
                        );
                    }
                    if (currentValue >= highestValue) {
                        highestValue = currentValue;
                        alpha = Math.max(alpha, highestValue);
                    }
                    if(beta <= alpha) {
                        transpositions.get(currentHash).type = NodeType.BETA;
                        return highestValue;
                    }
                }
            }
            return highestValue;

        }

    }

}
