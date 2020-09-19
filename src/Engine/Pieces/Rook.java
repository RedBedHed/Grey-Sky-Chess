package Engine.Pieces;

import Engine.Board.Board;
import Engine.Board.Move;
import Engine.Board.Move.EliteMove;
import Engine.Board.Move.EliteAttackMove;
import Engine.Board.Utility;
import Engine.Board.Tile;

import java.util.*;

/**
 * Rook
 *
 * @author Ellie Moore
 * @version 06.09.2020
 */
public final class Rook extends Piece{

    /**
     * Coordinates to determine the destination of the {Queen}'s legal moves on a one-
     * dimensional representation of a chess board.
     */
    private static final int[] CANDIDATE_MOVE_VECTOR_COORDINATES = {-8, -1, 1, 8};

    /**
     * A factory method to instantiate a default {@code Rook} with isFirstMove set to true.
     *
     * @param piecePosition the {@code Rook}'s position
     * @param pieceAlliance the {@code Rook}'s {@code Alliance}
     * @return a default {@code Rook}
     */
    public static Rook defaultInstance(final int piecePosition, final Alliance pieceAlliance) {
        return new Rook(piecePosition, pieceAlliance, true);
    }

    /**
     * A factory method to instantiate a moved {@code Rook} with isFirstMove set to false.
     *
     * @param piecePosition the {@code Rook}'s position
     * @param pieceAlliance the {@code Rook}'s {@code Alliance}
     * @return a moved {@code Rook}
     */
    public static Rook movedInstance(final int piecePosition, final Alliance pieceAlliance) {
        return new Rook(piecePosition, pieceAlliance, false);
    }

    /*
     * A private constructor for a Rook.
     */
    private Rook(final int piecePosition,
                final Alliance pieceAlliance,
                final boolean isFirstMove){
        super(PieceType.ROOK, piecePosition, pieceAlliance, isFirstMove);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        boolean isAtBoundary = false;
        final List<Move> legalMoves = new ArrayList<>();
        for(final int candidateOffset: CANDIDATE_MOVE_VECTOR_COORDINATES){
            int candidateCoordinate = this.piecePosition;
            if(!isFirstColumnExclusion(this.piecePosition, candidateOffset) &&
               !isEighthColumnExclusion(this.piecePosition, candidateOffset)) {
                //System.out.println("Hi!");
                isAtBoundary = false;
                while (Utility.isValidTileCoordinate(candidateCoordinate) && !isAtBoundary) {
                    candidateCoordinate += candidateOffset;
                    if (Utility.isValidTileCoordinate(candidateCoordinate)) {
                        final Tile candidateTile = board.getTile(candidateCoordinate);
                        if (!candidateTile.isTileOccupied()) {
                            legalMoves.add(new EliteMove(board, this, candidateCoordinate));
                            if(isRightLeftBoundary(candidateCoordinate, candidateOffset)){
                                isAtBoundary = true;
                                continue;
                            }
                        } else {
                            final Piece pieceAtDestination = candidateTile.getPiece();
                            final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
                            if (this.pieceAlliance != pieceAlliance) {
                                legalMoves.add(new EliteAttackMove(
                                        board, this, candidateCoordinate, pieceAtDestination
                                ));
                            }
                            isAtBoundary = true;
                        }
                    }
                }
            }
        }
        return Collections.unmodifiableCollection(legalMoves);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Rook movePiece(final Move move) {
        return movedInstance(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }

    /*
     * Boolean methods to enhance the readability of legal move logic.
     * Note: magic numbers are left in to simplify the expressions.
     * These numbers are vector coordinates that the reader can easily
     * plot in two dimensions (8x8 grid).
     */
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return Utility.FIRST_COLUMN[currentPosition] && candidateOffset == -1;
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset){
        return Utility.EIGHTH_COLUMN[currentPosition] && candidateOffset == 1;
    }

    private boolean isRightLeftBoundary(int candidateCoordinate, int candidateOffset) {
        return (Utility.FIRST_COLUMN[candidateCoordinate] || Utility.EIGHTH_COLUMN[candidateCoordinate])
                && (candidateOffset == -1 || candidateOffset == 1);
    }

}
