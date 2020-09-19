package Opponent;

import Engine.Board.Board;
import Engine.Player.Player;

public interface BoardEvaluator {

    int CHECK_BONUS = 50;
    int CHECK_MATE_BONUS = 10000;
    int DEPTH_BONUS = 100;
    int CASTLE_BONUS = 60;
    int PROMOTION_BONUS = 10000;
    int STALE_MATE_PENALTY = 60;

    default int evaluate(final Board board, final int depth) {
        return scorePlayer(board.whitePlayer(), depth) - scorePlayer(board.blackPlayer(), depth);
    }

    private static int scorePlayer(final Player player, final int depth){

        return pieceValue(player) +
                mobility(player) +
                check(player) +
                endGame(player, depth) +
                castled(player) +
                promotion(player);
    }

    private static int castled(final Player player) {
        return player.isCastled() ? CASTLE_BONUS: 0;
    }

    private static int endGame(final Player player, final int depth) {
        return player.getOpponent().isInCheckMate(player.hasEscapeMoves()) ? CHECK_MATE_BONUS * depthBonus(depth):
                player.getOpponent().isInStaleMate(player.hasEscapeMoves()) ? STALE_MATE_PENALTY: 0;
    }

    static int depthBonus(final int depth) {
        return depth == 0? 1: DEPTH_BONUS * depth;
    }

    private static int check(final Player player) {
        return player.getOpponent().isInCheck()? CHECK_BONUS: 0;
    }

    private static int mobility(final Player player) {
        return player.getLegalMoves().size();
    }

    private static int pieceValue(final Player player){
        return player.getPieceValueScore();
    }

    private static int promotion(final Player player){
        return player.hasPromoted()? PROMOTION_BONUS: 0;
    }

}
