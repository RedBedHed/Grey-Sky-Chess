package Engine.Pieces;

import Engine.Board.Board;
import Engine.Board.Move;
import Engine.Board.Move.EliteMove;
import Engine.Board.Move.EliteAttackMove;
import Engine.Board.Utility;
import Engine.Board.Tile;

import java.util.*;

/**
 * Queen
 *
 * @author Ellie Moore
 * @version 06.09.2020
 */
public final class Queen extends Piece {

    /**
     * Coordinates to determine the destination of the {Queen}'s legal moves on a one-
     * dimensional representation of a chess board.
     */
    private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES = {-9, -8, -7, -1, 1, 7, 8, 9};

    /**
     * A factory method to instantiate a default {@code Queen} with isFirstMove set to true.
     *
     * @param piecePosition the {@code Queen}'s position
     * @param pieceAlliance the {@code Queen}'s {@code Alliance}
     * @return a default {@code Queen}
     */
    public static Queen defaultInstance(final int piecePosition, final Alliance pieceAlliance) {
        return new Queen(piecePosition, pieceAlliance, true);
    }

    /**
     * A factory method to instantiate a moved {@code Queen} with isFirstMove set to false.
     *
     * @param piecePosition the {@code Queen}'s position
     * @param pieceAlliance the {@code Queen}'s {@code Alliance}
     * @return a moved {@code Queen}
     */
    public static Queen movedInstance(final int piecePosition, final Alliance pieceAlliance) {
        return new Queen(piecePosition, pieceAlliance, false);
    }

    /*
     * A private constructor for a Queen.
     */
    private Queen(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(PieceType.QUEEN, piecePosition, pieceAlliance, isFirstMove);
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
    public Queen movePiece(final Move move) {
        return movedInstance(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }

    /*
     * Boolean methods to enhance the readability of legal move logic.
     * Note: magic numbers are left in to simplify the expressions.
     * These numbers are vector coordinates that the reader can easily
     * plot in two dimensions (8x8 grid).
     */
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return Utility.FIRST_COLUMN[currentPosition] &&
                (candidateOffset == -9 || candidateOffset == -1 || candidateOffset == 7);
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset){
        return Utility.EIGHTH_COLUMN[currentPosition] &&
                (candidateOffset == -7 || candidateOffset == 1 || candidateOffset == 9);
    }

    private static boolean isRightLeftBoundary(final int candidateCoordinate, final int candidateOffset){
        return (Utility.FIRST_COLUMN[candidateCoordinate]
                || Utility.EIGHTH_COLUMN[candidateCoordinate]) &&
                (candidateOffset == -9 || candidateOffset == -7 || candidateOffset == 7
                || candidateOffset == 9 || candidateOffset == -1 || candidateOffset == 1);
    }

}
