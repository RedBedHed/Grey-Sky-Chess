package Engine.Pieces;

import Engine.Board.Board;
import Engine.Board.Move;
import Engine.Board.Move.PawnJump;
import Engine.Board.Move.PawnAttackMove;
import Engine.Board.Move.PawnEnPassantAttackMove;
import Engine.Board.Move.PawnMove;
import Engine.Board.Utility;
import Engine.Board.Move.PawnPromotion;
import GUI.PromotionChooser;
import GUI.Table;

import java.util.*;

/**
 * Pawn
 *
 * @author Ellie Moore
 * @version 06.09.2020
 */
public final class Pawn extends Piece {

    /**
     * The direction of the {@code Pawn}. This direction is either up or down
     * (-1 or 1 respectively).
     */
    private final int direction;

    /**
     * Coordinates to determine the destination of the {Pawn}'s legal moves on a one-
     * dimensional representation of a chess board.
     */
    private final static int[] CANDIDATE_MOVE_COORDINATES = {7, 8, 9, 16};

    /**
     * A factory method to instantiate a default {@code Pawn} with isFirstMove set to true.
     *
     * @param piecePosition the {@code Pawn}'s position
     * @param pieceAlliance the {@code Pawn}'s {@code Alliance}
     * @return a default {@code Pawn}
     */
    public static Pawn defaultInstance(final int piecePosition, final Alliance pieceAlliance) {
        return new Pawn(piecePosition, pieceAlliance, true);
    }

    /**
     * A factory method to instantiate a moved {@code Pawn} with isFirstMove set to false.
     *
     * @param piecePosition the {@code Pawn}'s position
     * @param pieceAlliance the {@code Pawn}'s {@code Alliance}
     * @return a moved {@code Pawn}
     */
    public static Pawn movedInstance(final int piecePosition, final Alliance pieceAlliance) {
        return new Pawn(piecePosition, pieceAlliance, false);
    }

    /*
     * A private constructor for a Pawn.
     */
    private Pawn(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(PieceType.PAWN, piecePosition, pieceAlliance, isFirstMove);
        this.direction = pieceAlliance == Alliance.WHITE? -1: 1;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for(final int candidateOffset: CANDIDATE_MOVE_COORDINATES) {
            int candidateCoordinate = this.piecePosition + (candidateOffset * this.direction);
            if(Utility.isValidTileCoordinate(candidateCoordinate)) {
                if(candidateOffset == 8 && !board.getTile(candidateCoordinate).isTileOccupied()){
                    if(this.pieceAlliance.isPawnPromotionTile(candidateCoordinate)) {
                        legalMoves.add(new PawnPromotion(new PawnMove(
                                board, this, candidateCoordinate
                        )));
                    } else legalMoves.add(new PawnMove(board, this, candidateCoordinate));
                } else if(candidateOffset == 16
                        && (Utility.SECOND_ROW[this.piecePosition]
                        || Utility.SEVENTH_ROW[this.piecePosition])){
                    final int beneathCandidateCoordinate = this.piecePosition + (this.direction * 8);
                    if(!board.getTile(beneathCandidateCoordinate).isTileOccupied() &&
                       !board.getTile(candidateCoordinate).isTileOccupied()){
                        legalMoves.add(new PawnJump(board, this, candidateCoordinate));
                    }
                } else if(!(Utility.FIRST_COLUMN[this.piecePosition] && this.direction == 1) &&
                        !(Utility.EIGHTH_COLUMN[this.piecePosition] && this.direction == -1) &&
                        candidateOffset == 7) {
                    if(board.getTile(candidateCoordinate).isTileOccupied()){
                        final Piece pieceOnCandidate = board.getTile(candidateCoordinate).getPiece();
                        if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){
                            if(this.pieceAlliance.isPawnPromotionTile(candidateCoordinate)) {
                                legalMoves.add(new PawnPromotion(new PawnAttackMove(
                                        board, this, candidateCoordinate, pieceOnCandidate
                                )));
                            } else legalMoves.add(new PawnAttackMove(
                                    board, this, candidateCoordinate, pieceOnCandidate
                            ));
                        }
                    } else if(board.getEnPassantPawn() != null) {
                        if(board.getEnPassantPawn().getPiecePosition() == (this.piecePosition - this.direction)){
                            final Piece pieceOnCandidate = board.getEnPassantPawn();
                            if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){
                                legalMoves.add(new PawnEnPassantAttackMove(
                                        board, this, candidateCoordinate, pieceOnCandidate
                                ));
                            }
                        }
                    }
                } else if(!(Utility.FIRST_COLUMN[this.piecePosition] && this.direction == -1) &&
                        !(Utility.EIGHTH_COLUMN[this.piecePosition] && this.direction == 1) &&
                        candidateOffset == 9) {
                    if(board.getTile(candidateCoordinate).isTileOccupied()){
                        final Piece pieceOnCandidate = board.getTile(candidateCoordinate).getPiece();
                        if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){
                            if(this.pieceAlliance.isPawnPromotionTile(candidateCoordinate)) {
                                legalMoves.add(new PawnPromotion(new PawnAttackMove(
                                        board, this, candidateCoordinate, pieceOnCandidate
                                )));
                            } else legalMoves.add(new PawnAttackMove(
                                    board, this, candidateCoordinate, pieceOnCandidate
                            ));
                        }
                    }else if(board.getEnPassantPawn() != null){
                        if(board.getEnPassantPawn().getPiecePosition() == (this.piecePosition + this.direction)){
                            final Piece pieceOnCandidate = board.getEnPassantPawn();
                            if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){
                                legalMoves.add(new PawnEnPassantAttackMove(
                                        board, this, candidateCoordinate, pieceOnCandidate
                                ));
                            }
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
    public Pawn movePiece(final Move move) {
        return movedInstance(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }

    /**
     * A method to instantiate a new piece in the case of pawn promotion. For an AI opponent,
     * the promotion piece will always be of type {@code Queen}.
     *
     * @param isAI whether or not the promotion {@code Piece} is needed by an AI opponent.
     * @return a moved {@code Queen}
     */
    public Piece getPromotionPiece(boolean isAI){
        return isAI? Queen.defaultInstance(this.piecePosition, this.pieceAlliance):
                new PromotionChooser(Table.INSTANCE.getFrame(), this).getPiece();
    }

}
