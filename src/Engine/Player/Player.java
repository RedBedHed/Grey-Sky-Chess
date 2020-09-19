package Engine.Player;

import Engine.Board.Board;
import Engine.Board.Utility;
import Engine.Board.Move;
import Engine.Pieces.Alliance;
import Engine.Pieces.King;
import Engine.Pieces.Piece;

import java.util.*;

/**
 * Player
 *
 * <p>
 * A {@code Player} is an abstraction with two concrete extensions:
 * {@code WhitePlayer} and {@code BlackPlayer}. Two {@code Player}s are
 * present on the board at all times. A {@code Player} can be thought of as
 * a team, or an army, representing all of the legal {@code Move}s and
 * {@code Pieces} of an {@code Alliance} as a single, cohesive unit.
 *
 * @author Ellie Moore
 * @author Amir Afghani
 * @version 06.11.2020
 */
public abstract class Player {

    /**
     * The current {@code Board}.
     */
    protected final Board board;

    /**
     * The {@code Player}'s {@code King}.
     */
    protected final King playerKing;

    /**
     * The {@code Player}'s legal {@code Move}s.
     */
    protected final Collection<Move> legalMoves;

    /**
     * A variable to indicate whether or not the {@code Player} is in check.
     */
    private final boolean isInCheck;

    /**
     * A variable to indicate whether or not the {@code Player} has promoted a pawn.
     */
    private final boolean hasPromoted;

    /**
     * A variable to indicate whether or not the {@code Player} is castled.
     */
    private final boolean isCastled;

    /**
     * The {@code Player}'s castles.
     */
    private final Collection<Move> castles;

    /**
     * The type of the {@code Player} (User or Computer).
     */
    private final PlayerType playerType;

    /**
     * A protected constructor for a {@code Player}.
     *
     * @param board the current {@code Board}
     * @param legalMoves the {@code Player}'s legal moves
     * @param opponentsLegalMoves the opponent {@code Player}'s legal moves
     */
    protected Player(final Board board,
                     final Collection<Move> legalMoves,
                     final Collection<Move> opponentsLegalMoves,
                     final boolean isCastled,
                     final boolean hasPromoted,
                     final PlayerType playerType){
        this.board = board;
        this.playerKing = establishKing();
        this.isInCheck = !Player.calculateAttacksOnTile(
                this.playerKing.getPiecePosition(), opponentsLegalMoves
        ).isEmpty();
        this.isCastled = isCastled;
        this.hasPromoted = hasPromoted;
        this.castles = this.calculateKingCastles(legalMoves, opponentsLegalMoves);
        this.legalMoves = this.castles.isEmpty()?
                legalMoves: Utility.concat(castles, legalMoves);
        this.playerType = playerType;
    }

    /**
     * A method to expose the {@code PlayerType}.
     *
     * @return the {@code PLayerType}
     */
    public PlayerType getPlayerType(){
        return playerType;
    }

    /**
     * A method to expose the {@code Player}'s {@code King}.
     *
     * @return the {@code Player}'s {@code King}
     */
    public King getPlayerKing(){
        return this.playerKing;
    }

    /**
     * A method to expose the {@code Player}'s full legal moves (including castles).
     *
     * @return the {@code Player}'s full legal moves
     */
    public Collection<Move> getLegalMoves(){
        return this.legalMoves;
    }

    /**
     * A method to expose the {@code Player}'s castle {@code Moves}.
     *
     * @return the {@code Player}'s castle {@code Moves}
     */
    public Collection<Move> getCastles() {
        return this.castles;
    }

    /**
     * A method to calculate all {@code AttackMove}s on a given tile position contained
     * within a given {@code Collection} of {@code Move}s.
     *
     * @param tilePosition the position under attack
     * @param moves the moves to search
     * @return a {@code List} of {@code AttackMove}s on the tile position
     */
    protected static Collection<Move> calculateAttacksOnTile(final int tilePosition,
                                                             final Collection<Move> moves) {
        final List<Move> attackMoves = new ArrayList<>();
        for(final Move move: moves){
            if(tilePosition == move.getDestinationCoordinate()){
                attackMoves.add(move);
            }
        }
        return Collections.unmodifiableList(attackMoves);
    }

    /**
     * A method to find and return the {@code Player}'s {@code King}.
     *
     * @return the {@code Player}'s {@code King}
     */
    private King establishKing() {
        for(final Piece piece: getActivePieces()){
            if(piece.getPieceType().isKing()){
                return (King) piece;
            }
        }
        throw new RuntimeException("Invalid Board-- Missing King.");
    }

    /**
     * A boolean method to determine if a move is contained in the {@code Player}'s
     * legal moves.
     *
     * @param move the move to check for
     * @return whether or not the given move is legal
     */
    public boolean isMoveLegal(final Move move){
        return this.legalMoves.contains(move);
    }

    /**
     * A boolean method to indicate whether or not the {@code Player} is in check.
     *
     * @return whether or not the {@code Player} is in check
     */
    public boolean isInCheck(){
        return this.isInCheck;
    }

    /**
     * A boolean method to indicate whether or not the {@code Player} is in checkmate.
     * If the {@code Player} is in check and has no escape moves then the {@code Player}
     * is in checkmate
     *
     * @return whether or not the {@code Player} is in checkmate
     * @see Player#hasEscapeMoves()
     */
    public boolean isInCheckMate(final boolean hasEscapeMoves){
        return this.isInCheck && !hasEscapeMoves;
    }

    /**
     * A boolean method to determine whether or not the {@code Player} has any escape
     * moves. This method makes each {@code Move} in the {@code Player}'s legal
     * {@code Move}s and checks the {@code MoveStatus} of the corresponding
     * {@code MoveTransition}. If any status is {@code DONE}, then the Player is
     * unable to escape.
     *
     * @return whether or not the {@code Player} can escape from check
     */
    public boolean hasEscapeMoves() {
        for(final Move move: this.legalMoves){
            if(move.isAttack() && move.getAttackedPiece().getPieceType().isKing()) continue;
            final MoveTransition transition = makeMove(move, true);
            if(transition.getMoveStatus().isDone()) return true;
        }
        return false;
    }

    /**
     * A method to calculate the piece value score of the {@code Player}.
     *
     * @return the piece value score of the {@code Player}
     */
    public int getPieceValueScore() {
        int pieceValueScore = 0;
        for(final Piece piece: getActivePieces()){
            pieceValueScore += piece.getPieceValue();
        }
        return pieceValueScore;
    }

    /**
     * A method to indicate whether or not the {@code Player} is in stalemate. If the
     * {@code Player} is in NOT in check and has no escape moves, then the
     * {@code Player} is in stalemate
     *
     * @return whether or not the {@code Player} is in stalemate
     */
    public boolean isInStaleMate(final boolean hasEscapeMoves){
        return !this.isInCheck && !hasEscapeMoves;
    }

    /**
     * A method to indicate whether or not the {@code Player} is castled.
     *
     * @return whether or not the {@code Player} is castled
     */
    public boolean isCastled(){
        return isCastled;
    }

    /**
     * A method to indicate whether or not the {@code Player} has promoted
     * a pawn.
     *
     * @return whether or not the {@code Player} has promoted
     */
    public boolean hasPromoted(){
        return hasPromoted;
    }

    /**
     * A method to determine if the {@code Player} has insufficient material.
     *
     * @return whether or not the {@code Player} has insufficient material
     */
    public boolean hasInsufficientMaterial(){
        if(getActivePieces().size() <= 3) {
            InsufficientPiecesTest test = initTest();
            return getActivePieces().size() == 1 ||
                    (getActivePieces().size() == 2 && test.hasBishop) ||
                    (getActivePieces().size() == 2 && test.hasKnight) ||
                    test.hasTwoKnights;
        }
        return false;
    }

    /**
     * A struct to assist in determining whether or not the {@code Player} has
     * insufficient material. This is useful in the rare case that a game is
     * drawn by insufficient material.
     */
    private static final class InsufficientPiecesTest {
        boolean hasBishop;
        boolean hasKnight;
        boolean hasTwoKnights;
    }

    /**
     * A method to initialize an {@code InsufficientPiecesTest}.
     *
     * @return an {@code InsufficientPiecesTest}
     */
    private InsufficientPiecesTest initTest(){
        InsufficientPiecesTest test = new InsufficientPiecesTest();
        for(Piece p: getActivePieces()){
            if(p.getPieceType() == Piece.PieceType.BISHOP)
                test.hasBishop = true;
            if(p.getPieceType() == Piece.PieceType.KNIGHT){
                if(test.hasKnight)
                    test.hasTwoKnights = true;
                test.hasKnight = true;
            }
        }
        return test;
    }

    /**
     * A method which allows the {@code Player} to execute a {@code Move} on the chess board if
     * and only if that {@code Move} is legal and does not place the {@code Player} in check. if
     * the move is illegal or results in check, the previous board will be passed to the {@code
     * MoveTransition} and the {@code Player} will be allowed to try again.
     *
     * @param move the move to be made
     * @return a {@code MoveTransition}
     */
    public MoveTransition makeMove(final Move move, final boolean isAI){
        if(!isMoveLegal(move))
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        final Board transitionBoard = move.execute(isAI);
        final Collection<Move> kingAttacks = Player.calculateAttacksOnTile(
                transitionBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
                transitionBoard.currentPlayer().getLegalMoves()
        );
        if(!kingAttacks.isEmpty())
            return new MoveTransition(this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }

    /**
     * Player Type
     *
     * A {@code PlayerType} is either USER or COMPUTER.
     */
    public enum PlayerType {

        USER {

            /**
             * @inheritDoc
             */
            @Override
            public String toString(){
                return "User";
            }

            /**
             * @inheritDoc
             */
            @Override
            public boolean isUser(){
                return true;
            }

            /**
             * @inheritDoc
             */
            @Override
            public boolean isComputer(){
                return false;
            }

        },
        COMPUTER{

            /**
             * @inheritDoc
             */
            @Override
            public String toString(){
                return "Computer";
            }

            /**
             * @inheritDoc
             */
            @Override
            public boolean isUser(){
                return false;
            }

            /**
             * @inheritDoc
             */
            @Override
            public boolean isComputer(){
                return true;
            }

        };

        /**
         * Indicates whether the {@code PlayerType} is USER via polymorphism.
         *
         * @return whether or not the {@code PlayerType} is USER.
         */
        public abstract boolean isUser();

        /**
         * Indicates whether the {@code PlayerType} is COMPUTER via polymorphism.
         *
         * @return whether or not the {@code PlayerType} is COMPUTER.
         */
        public abstract boolean isComputer();

    }

    /**
     * A method to get the {@code Player}'s active pieces. Calls the corresponding
     * method on the current {@code Board}.
     *
     * @return the {@code Player}'s active pieces
     */
    public abstract Collection<Piece> getActivePieces();

    /**
     * A method to get the the {@code Player}'s {@code Alliance} via polymorphism.
     *
     * @return the {@code Player}'s {@code Alliance}
     */
    public abstract Alliance getAlliance();

    /**
     * A method to get the {@code Player}'s opponent via polymorphism. Calls the
     * corresponding method on the current {@code Board}.
     *
     * @return the {@code Player}'s opponent
     */
    public abstract Player getOpponent();

    /**
     * A method to calculate the {@code Player}'s castles.
     *
     * @param playerLegals the {@code Player}'s legal moves
     * @param opponentLegals the opponent's legal moves
     * @return A {@code Collection} containing the {@code Player}'s existing castles
     */
    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals,
                                                             Collection<Move> opponentLegals);

    /**
     * A method to determine if the {@code Player} is able to castle king-side.
     *
     * @return Whether or not the {@code Player} can castle king-side.
     */
    public abstract boolean isKingSideCastleCapable();

    /**
     * A method to determine if the {@code Player} is able to castle queen-side.
     *
     * @return Whether or not the {@code Player} can castle queen-side.
     */
    public abstract boolean isQueenSideCastleCapable();

}
