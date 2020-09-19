package Engine.Pieces;

import Engine.Board.Board;
import Engine.Board.Utility;
import Engine.Board.Tile;
import Engine.Board.Move;
import Engine.Board.Move.EliteMove;
import Engine.Board.Move.EliteAttackMove;

import java.util.*;

import java.util.Collection;

/**
 * Bishop
 *
 * @author Ellie Moore
 * @version 06.08.2020
 */
public final class Bishop extends Piece {

    /**
     * Vectors for use in determining move direction on a one-dimensional
     * representation of a chess board.
     */
    private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES = {-9, -7, 7, 9};

    /**
     * A factory method to instantiate a default {@code Bishop} with {@code isFirstMove}
     * equal to true.
     *
     * @param piecePosition the position of the {@code Bishop}
     * @param pieceAlliance the {@code Alliance} of the {@code Bishop}
     * @return a default {@code Bishop}
     */
    public static Bishop defaultInstance(final int piecePosition, final Alliance pieceAlliance) {
        return new Bishop(piecePosition, pieceAlliance, true);
    }

    /**
     * A factory method to instantiate a moved {@code Bishop} with {@code isFirstMove}
     * equal to false.
     *
     * @param piecePosition the position of the {@code Bishop}
     * @param pieceAlliance the {@code Alliance} of the {@code Bishop}
     * @return a moved {@code Bishop}
     */
    public static Bishop movedInstance(final int piecePosition, final Alliance pieceAlliance) {
        return new Bishop(piecePosition, pieceAlliance, false);
    }

    /*
     * A private constructor for a Bishop.
     */
    private Bishop(final int piecePosition,
                   final Alliance pieceAlliance,
                   final boolean isFirstMove) {
        super(PieceType.BISHOP, piecePosition, pieceAlliance, isFirstMove);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        boolean isAtBoundary = false;
        final List<Move> legalMoves = new ArrayList<>();
        for (final int candidateOffset : CANDIDATE_MOVE_VECTOR_COORDINATES) {
            int candidateCoordinate = this.piecePosition;
            if (!isFirstColumnExclusion(this.piecePosition, candidateOffset) &&
                    !isEighthColumnExclusion(this.piecePosition, candidateOffset)) {
                isAtBoundary = false;
                while (Utility.isValidTileCoordinate(candidateCoordinate) && !isAtBoundary) {
                    candidateCoordinate += candidateOffset;
                    if (Utility.isValidTileCoordinate(candidateCoordinate)) {
                        final Tile candidateTile = board.getTile(candidateCoordinate);
                        if (!candidateTile.isTileOccupied()) {
                            legalMoves.add(new EliteMove(board, this, candidateCoordinate));
                            if (isRightLeftBoundary(candidateCoordinate, candidateOffset)) {
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
    public Bishop movePiece(final Move move) {
        return movedInstance(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }

    /*
     * Boolean methods to enhance the readability of legal move logic.
     * Note: magic numbers are left in to simplify the expressions.
     * These numbers are vector coordinates that the reader can easily
     * plot in two dimensions (8x8 grid).
     */
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return Utility.FIRST_COLUMN[currentPosition] && (candidateOffset == -9 || candidateOffset == 7);
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        return Utility.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -7 || candidateOffset == 9);
    }

    private static boolean isRightLeftBoundary(final int candidateCoordinate, final int candidateOffset) {
        return (Utility.FIRST_COLUMN[candidateCoordinate] || Utility.EIGHTH_COLUMN[candidateCoordinate])
                && (candidateOffset == -9 || candidateOffset == -7 || candidateOffset == 7 || candidateOffset == 9);
    }

}
