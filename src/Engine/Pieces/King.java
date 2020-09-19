package Engine.Pieces;

import Engine.Board.Board;
import Engine.Board.Move;
import Engine.Board.Move.EliteMove;
import Engine.Board.Move.EliteAttackMove;
import Engine.Board.Utility;
import Engine.Board.Tile;

import java.util.*;

import java.util.Collection;

/**
 * King
 *
 * @author Ellie Moore
 * @version 06.08.2020
 */
public final class King extends Piece {

    /**
     * Coordinates to determine move direction on a one-dimensional representation
     * of a chess board.
     */
    final static int[] CANDIDATE_MOVE_COORDINATES = {-9, -8, -7, -1, 1, 7, 8, 9};

    /**
     * A factory method to instantiate a default {@code King} with {@code isFirstMove} set
     * to true.
     *
     * @param piecePosition the position of the {@code King}
     * @param pieceAlliance the {@code Alliance} of the {@code King}
     * @return a default {@code King}
     */
    public static King defaultInstance(final int piecePosition, final Alliance pieceAlliance) {
        return new King(piecePosition, pieceAlliance, true);
    }

    /**
     * A factory method to instantiate a moved {@code King} with {@code isFirstMove} set
     * to false.
     *
     * @param piecePosition the position of the {@code King}
     * @param pieceAlliance the {@code Alliance} of the {@code King}
     * @return a moved {@code King}
     */
    public static King movedInstance(final int piecePosition, final Alliance pieceAlliance) {
        return new King(piecePosition, pieceAlliance, false);
    }

    /*
     * A private constructor for a King.
     */
    private King(final int piecePosition,
                 final Alliance pieceAlliance,
                 final boolean isFirstMove) {
        super(PieceType.KING, piecePosition, pieceAlliance, isFirstMove);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for (final int candidateOffset : CANDIDATE_MOVE_COORDINATES) {
            final int candidateCoordinate = this.piecePosition + candidateOffset;
            if (Utility.isValidTileCoordinate(candidateCoordinate)) {
                if (!isFirstColumnExclusion(this.piecePosition, candidateOffset)
                        && !isEighthColumnExclusion(this.piecePosition, candidateOffset)) {
                    final Tile candidateTile = board.getTile(candidateCoordinate);
                    if (!candidateTile.isTileOccupied()) {
                        legalMoves.add(new EliteMove(board, this, candidateCoordinate));
                    } else {
                        final Piece pieceAtDestination = candidateTile.getPiece();
                        final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
                        if (this.pieceAlliance != pieceAlliance) {
                            legalMoves.add(new EliteAttackMove(
                                    board, this, candidateCoordinate, pieceAtDestination
                            ));
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
    public King movePiece(final Move move) {
        return movedInstance(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }

    /*
     * Boolean methods to enhance the readability of legal move logic.
     * Note: magic numbers are left in to simplify the expressions.
     * These numbers are vector coordinates that the reader can easily
     * plot in two dimensions (8x8 grid).
     */
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return Utility.FIRST_COLUMN[currentPosition] && (candidateOffset == -9 || candidateOffset == -1 ||
                candidateOffset == 7);
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        return Utility.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -7 || candidateOffset == 1 ||
                candidateOffset == 9);
    }

}
