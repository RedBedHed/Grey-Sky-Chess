package Engine.Player;

import Engine.Board.Board;
import Engine.Board.Move;
import Engine.Board.Move.KingSideCastleMove;
import Engine.Board.Move.QueenSideCastleMove;
import Engine.Pieces.Alliance;
import Engine.Pieces.Piece;
import Engine.Pieces.Rook;

import java.util.*;

/**
 * Black Player
 *
 * @author Ellie Moore
 * @author Amir Afghani
 * @version 06.11.2020
 */
public final class BlackPlayer extends Player {

    /**
     * A public constructor for a {@code Black PLayer}.
     *
     * @param board the current {@code Board}
     * @param blackStdLegalMoves all legal {@code Moves} pertaining to the black {@code Alliance}
     * @param whiteStdLegalMoves all legal {@code Moves} pertaining to the white {@code Alliance}
     * @param isCastled whether or not the {@code Player} has committed to a castling move
     */
    public BlackPlayer(final Board board,
                       final Collection<Move> blackStdLegalMoves,
                       final Collection<Move> whiteStdLegalMoves,
                       final boolean isCastled,
                       final boolean hasPromoted,
                       PlayerType type) {
        super(board, blackStdLegalMoves, whiteStdLegalMoves, isCastled, hasPromoted, type);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Collection<Piece> getActivePieces() {
        return board.getBlackPieces();
    }

    /**
     * @inheritDoc
     */
    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Player getOpponent() {
        return board.whitePlayer();
    }

    /**
     * @inheritDoc
     */
    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,
                                                    final Collection<Move> opponentLegals) {
        if(isInCheck() || isCastled()) return Collections.emptyList();
        final List<Move> kingCastles = new ArrayList<>();
        if(playerKing.getPiecePosition() == 4 && !isInCheck() && playerKing.isFirstMove()){
            //Black player's king side Castle.
            if(board.getTile(5).getPiece() == null
               && board.getTile(6).getPiece() == null){
                final Piece rook = board.getTile(7).getPiece();
                if(rook != null){
                    if(Player.calculateAttacksOnTile(5, opponentLegals).isEmpty()
                       && Player.calculateAttacksOnTile(6, opponentLegals).isEmpty()
                       && rook.getPieceType().isRook()
                        && rook.isFirstMove()){
                        kingCastles.add(new KingSideCastleMove(
                                board, playerKing, 6, (Rook) rook, 5
                        ));
                    }
                }
            }
            //Black player's queen side Castle
            if(board.getTile(3).getPiece() == null
               && board.getTile(2).getPiece() == null
               && board.getTile(1).getPiece() == null){
                final Piece rook = board.getTile(0).getPiece();
                if(rook != null){
                    if(Player.calculateAttacksOnTile(3, opponentLegals).isEmpty()
                       && Player.calculateAttacksOnTile(2, opponentLegals).isEmpty()
                       && rook.getPieceType().isRook()
                        && rook.isFirstMove()){
                        kingCastles.add(new QueenSideCastleMove(
                                board, playerKing, 2, (Rook) rook, 3
                        ));
                    }
                }
            }
        }
        return Collections.unmodifiableList(kingCastles);
    }

    public boolean isKingSideCastleCapable(){
        Piece piece = board.getTile(7).getPiece();
        if(piece != null && playerKing.isFirstMove() && piece.getPieceType().isRook())
            return piece.isFirstMove();
        return false;
    }

    public boolean isQueenSideCastleCapable(){
        Piece piece = board.getTile(0).getPiece();
        if(piece != null && playerKing.isFirstMove() && piece.getPieceType().isRook())
            return piece.isFirstMove();
        return false;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return Alliance.BLACK.toString();
    }

}
