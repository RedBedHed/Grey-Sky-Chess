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
 * White Player
 *
 * @author Ellie Moore
 * @author Amir Afghani
 * @version 06.11.2020
 */
public final class WhitePlayer extends Player {

    /**
     * A public constructor for a {@code White PLayer}.
     *
     * @param board the current {@code Board}
     * @param whiteStdLegalMoves all legal {@code Moves} pertaining to the white {@code Alliance}
     * @param blackStdLegalMoves all legal {@code Moves} pertaining to the black {@code Alliance}
     * @param isCastled whether or not the {@code Player} has committed to a castling move
     */
    public WhitePlayer(final Board board,
                       final Collection<Move> whiteStdLegalMoves,
                       final Collection<Move> blackStdLegalMoves,
                       final boolean isCastled,
                       final boolean hasPromoted,
                       final PlayerType type) {
        super(board, whiteStdLegalMoves, blackStdLegalMoves, isCastled, hasPromoted, type);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Collection<Piece> getActivePieces() {
        return board.getWhitePieces();
    }

    /**
     * @inheritDoc
     */
    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Player getOpponent() {
        return board.blackPlayer();
    }

    /**
     * @inheritDoc
     */
    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,
                                                    final Collection<Move> opponentLegals) {
        if(isInCheck() || isCastled()) return Collections.emptyList();
        final List<Move> kingCastles = new ArrayList<>();
        if(playerKing.getPiecePosition() == 60 && !isInCheck() && playerKing.isFirstMove()){
            //White player's king side Castle.
            if(board.getTile(61).getPiece() == null
               && board.getTile(62).getPiece() == null){
                final Piece rook = board.getTile(63).getPiece();
                if(rook != null){
                    if(Player.calculateAttacksOnTile(61, opponentLegals).isEmpty()
                    && Player.calculateAttacksOnTile(62, opponentLegals).isEmpty()
                    && rook.getPieceType().isRook()
                    && rook.isFirstMove()){
                        kingCastles.add(new KingSideCastleMove(
                                board, playerKing, 62, (Rook) rook, 61
                        ));
                    }
                }
            }
            //White player's queen side Castle
            if(board.getTile(59).getPiece() == null
               && board.getTile(58).getPiece() == null
               && board.getTile(57).getPiece() == null){
                final Piece rook = board.getTile(56).getPiece();
                if(rook != null){
                    if(Player.calculateAttacksOnTile(59, opponentLegals).isEmpty()
                        && Player.calculateAttacksOnTile(58, opponentLegals).isEmpty()
                        && rook.getPieceType().isRook()
                        && rook.isFirstMove()){
                        kingCastles.add(new QueenSideCastleMove(
                                board, playerKing, 58, (Rook) rook, 59
                        ));
                    }
                }
            }
        }
        return Collections.unmodifiableList(kingCastles);
    }

    public boolean isKingSideCastleCapable(){
        Piece piece = board.getTile(63).getPiece();
        if(piece != null && playerKing.isFirstMove() && piece.getPieceType().isRook())
            return piece.isFirstMove();
        return false;
    }

    public boolean isQueenSideCastleCapable(){
        Piece piece = board.getTile(56).getPiece();
        if(piece != null && playerKing.isFirstMove() && piece.getPieceType().isRook())
            return piece.isFirstMove();
        return false;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return Alliance.WHITE.toString();
    }

}
