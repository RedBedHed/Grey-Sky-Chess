package Engine.Pieces;

import Engine.Board.Board;
import Engine.Board.Utility;
import Engine.Board.Move;
import Engine.Board.Move.EliteMove;
import Engine.Board.Move.EliteAttackMove;
import Engine.Board.Tile;

import java.util.*;

public final class Knight extends Piece {

    /**
     * Coordinates to determine the destination of the {Knight}'s legal moves on a one-
     * dimensional representation of a chess board.
     */
    private final static int[] CANDIDATE_MOVE_COORDINATES = {-17, -15, -10, -6, 6, 10, 15, 17};

    /**
     * A factory method to instantiate a default {@code Knight} with isFirstMove set to true.
     *
     * @param piecePosition the {@code Knight}'s position
     * @param pieceAlliance the {@code Knight}'s {@code Alliance}
     * @return a default {@code Knight}
     */
    public static Knight defaultInstance(final int piecePosition, final Alliance pieceAlliance) {
        return new Knight(piecePosition, pieceAlliance, true);
    }

    /**
     * A factory method to instantiate a moved {@code Knight} with isFirstMove set to false.
     *
     * @param piecePosition the {@code Knight}'s position
     * @param pieceAlliance the {@code Knight}'s {@code Alliance}
     * @return a moved {@code Knight}
     */
    public static Knight movedInstance(final int piecePosition, final Alliance pieceAlliance) {
        return new Knight(piecePosition, pieceAlliance, false);
    }

    /*
     * A private constructor for a Knight.
     */
    private Knight(final int piecePosition,
                   final Alliance pieceAlliance,
                   final boolean isFirstMove) {
        super(PieceType.KNIGHT, piecePosition, pieceAlliance, isFirstMove);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for (final int candidateOffset : CANDIDATE_MOVE_COORDINATES) {
            final int candidateCoordinate = this.piecePosition + candidateOffset;
            if (Utility.isValidTileCoordinate(candidateCoordinate)) {
                if (!isFirstColumnExclusion(this.piecePosition, candidateOffset) &&
                        !isSecondColumnExclusion(this.piecePosition, candidateOffset) &&
                        !isSeventhColumnExclusion(this.piecePosition, candidateOffset) &&
                        !isEighthColumnExclusion(this.piecePosition, candidateOffset)) {
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
    public Knight movePiece(final Move move) {
        return movedInstance(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }

	/*
	 * Boolean methods to enhance the readability of legal move logic.
	 * Note: magic numbers are left in to simplify the expressions.
	 * These numbers are vector coordinates that the reader can easily
	 * plot in two dimensions (8x8 grid).
	 */
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return Utility.FIRST_COLUMN[currentPosition] && (candidateOffset == -17 || candidateOffset == -10 ||
                candidateOffset == 6 || candidateOffset == 15);
    }

    private static boolean isSecondColumnExclusion(final int currentPosition, final int candidateOffset) {
        return Utility.SECOND_COLUMN[currentPosition] && (candidateOffset == -10 || candidateOffset == 6);
    }

    private static boolean isSeventhColumnExclusion(final int currentPosition, final int candidateOffset) {
        return Utility.SEVENTH_COLUMN[currentPosition] && (candidateOffset == -6 || candidateOffset == 10);
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        return Utility.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -15 || candidateOffset == -6 ||
                candidateOffset == 10 || candidateOffset == 17);
    }

}
