package Test;

import Engine.Pieces.*;
import Engine.Board.Board;
import Engine.Board.Utility;
import Engine.Board.Move;
import Engine.Pieces.Alliance;
import Engine.Player.MoveTransition;
import Engine.Player.Player;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;

public class TestPieces {

    /*public void testMiddleQueenOnEmptyBoard() {
        final Board.Builder builder = new Board.Builder();
        // Black Layout
        builder.setPiece(King.defaultInstance(4, Alliance.BLACK));
        // White Layout
        builder.setPiece(Queen.defaultInstance(36, Alliance.WHITE));
        builder.setPiece(King.defaultInstance(60, Alliance.WHITE));
        // Set the current player
        builder.setMoveMaker(Alliance.WHITE);
        final Board board = builder.build();
        final Collection<Move> whiteLegals = board.whitePlayer().getLegalMoves();
        final Collection<Move> blackLegals = board.blackPlayer().getLegalMoves();
        assertEquals(whiteLegals.size(), 31);
        assertEquals(blackLegals.size(), 5);
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("e8"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("e7"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("e6"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("e5"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("e3"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("e2"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("a4"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("b4"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("c4"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("d4"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("f4"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("g4"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("h4"))));
    }

    public void testLegalMoveAllAvailable() {

        final Board.Builder boardBuilder = new Board.Builder();
        // Black Layout
        boardBuilder.setPiece(King.defaultInstance(4, Alliance.BLACK));
        boardBuilder.setPiece(Knight.defaultInstance(28, Alliance.BLACK));
        // White Layout
        boardBuilder.setPiece(Knight.defaultInstance( 36, Alliance.WHITE));
        boardBuilder.setPiece(King.defaultInstance(60, Alliance.WHITE));
        // Set the current player
        boardBuilder.setMoveMaker(Alliance.WHITE);
        final Board board = boardBuilder.build();
        final Collection<Move> whiteLegals = board.whitePlayer().getLegalMoves();
        assertEquals(whiteLegals.size(), 13);
        final Move wm1 = Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("d6"));
        final Move wm2 = Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("f6"));
        final Move wm3 = Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("c5"));
        final Move wm4 = Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("g5"));
        final Move wm5 = Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("c3"));
        final Move wm6 = Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("g3"));
        final Move wm7 = Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("d2"));
        final Move wm8 = Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("f2"));

        assertTrue(whiteLegals.contains(wm1));
        assertTrue(whiteLegals.contains(wm2));
        assertTrue(whiteLegals.contains(wm3));
        assertTrue(whiteLegals.contains(wm4));
        assertTrue(whiteLegals.contains(wm5));
        assertTrue(whiteLegals.contains(wm6));
        assertTrue(whiteLegals.contains(wm7));
        assertTrue(whiteLegals.contains(wm8));

        final Board.Builder boardBuilder2 = new Board.Builder();
        // Black Layout
        boardBuilder2.setPiece(King.defaultInstance(4, Alliance.BLACK));
        boardBuilder2.setPiece(Knight.defaultInstance( 28, Alliance.BLACK));
        // White Layout
        boardBuilder2.setPiece(Knight.defaultInstance(36, Alliance.WHITE));
        boardBuilder2.setPiece(King.defaultInstance( 60, Alliance.WHITE));
        // Set the current player
        boardBuilder2.setMoveMaker(Alliance.BLACK);
        final Board board2 = boardBuilder2.build();
        final Collection<Move> blackLegals = board.blackPlayer().getLegalMoves();

        final Move bm1 = Move.MoveFactory
                .deliver(board2, Utility.getCoordinateAtPosition("e5"), Utility.getCoordinateAtPosition("d7"));
        final Move bm2 = Move.MoveFactory
                .deliver(board2, Utility.getCoordinateAtPosition("e5"), Utility.getCoordinateAtPosition("f7"));
        final Move bm3 = Move.MoveFactory
                .deliver(board2, Utility.getCoordinateAtPosition("e5"), Utility.getCoordinateAtPosition("c6"));
        final Move bm4 = Move.MoveFactory
                .deliver(board2, Utility.getCoordinateAtPosition("e5"), Utility.getCoordinateAtPosition("g6"));
        final Move bm5 = Move.MoveFactory
                .deliver(board2, Utility.getCoordinateAtPosition("e5"), Utility.getCoordinateAtPosition("c4"));
        final Move bm6 = Move.MoveFactory
                .deliver(board2, Utility.getCoordinateAtPosition("e5"), Utility.getCoordinateAtPosition("g4"));
        final Move bm7 = Move.MoveFactory
                .deliver(board2, Utility.getCoordinateAtPosition("e5"), Utility.getCoordinateAtPosition("d3"));
        final Move bm8 = Move.MoveFactory
                .deliver(board2, Utility.getCoordinateAtPosition("e5"), Utility.getCoordinateAtPosition("f3"));

        assertEquals(blackLegals.size(), 13);

        assertTrue(blackLegals.contains(bm1));
        assertTrue(blackLegals.contains(bm2));
        assertTrue(blackLegals.contains(bm3));
        assertTrue(blackLegals.contains(bm4));
        assertTrue(blackLegals.contains(bm5));
        assertTrue(blackLegals.contains(bm6));
        assertTrue(blackLegals.contains(bm7));
        assertTrue(blackLegals.contains(bm8));
    }

    public void testKnightInCorners() {
        final Board.Builder boardBuilder = new Board.Builder();
        boardBuilder.setPiece(King.defaultInstance(4, Alliance.BLACK));
        boardBuilder.setPiece(Knight.defaultInstance( 0, Alliance.BLACK));
        boardBuilder.setPiece(Knight.defaultInstance( 56, Alliance.WHITE));
        boardBuilder.setPiece(King.defaultInstance(60, Alliance.WHITE));
        boardBuilder.setMoveMaker(Alliance.WHITE);
        final Board board = boardBuilder.build();
        final Collection<Move> whiteLegals = board.whitePlayer().getLegalMoves();
        final Collection<Move> blackLegals = board.blackPlayer().getLegalMoves();
        assertEquals(whiteLegals.size(), 7);
        assertEquals(blackLegals.size(), 7);
        final Move wm1 = Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("a1"), Utility.getCoordinateAtPosition("b3"));
        final Move wm2 = Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("a1"), Utility.getCoordinateAtPosition("c2"));
        assertTrue(whiteLegals.contains(wm1));
        assertTrue(whiteLegals.contains(wm2));
        final Move bm1 = Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("a8"), Utility.getCoordinateAtPosition("b6"));
        final Move bm2 = Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("a8"), Utility.getCoordinateAtPosition("c7"));
        assertTrue(blackLegals.contains(bm1));
        assertTrue(blackLegals.contains(bm2));

    }

    public void testMiddleBishopOnEmptyBoard() {
        final Board.Builder builder = new Board.Builder();
        // Black Layout
        builder.setPiece(King.defaultInstance(4, Alliance.BLACK));
        // White Layout
        builder.setPiece(Bishop.defaultInstance(35, Alliance.WHITE));
        builder.setPiece(King.defaultInstance(60, Alliance.WHITE));
        // Set the current player
        builder.setMoveMaker(Alliance.WHITE);
        //build the board
        final Board board = builder.build();
        final Collection<Move> whiteLegals = board.whitePlayer().getLegalMoves();
        final Collection<Move> blackLegals = board.blackPlayer().getLegalMoves();
        assertEquals(whiteLegals.size(), 18);
        assertEquals(blackLegals.size(), 5);
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("d4"), Utility.getCoordinateAtPosition("a7"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("d4"), Utility.getCoordinateAtPosition("b6"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("d4"), Utility.getCoordinateAtPosition("c5"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("d4"), Utility.getCoordinateAtPosition("e3"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("d4"), Utility.getCoordinateAtPosition("f2"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("d4"), Utility.getCoordinateAtPosition("g1"))));
    }

    public void testTopLeftBishopOnEmptyBoard() {
        Board.Builder builder = new Board.Builder();
        // Black Layout
        builder.setPiece(King.defaultInstance( 4, Alliance.BLACK));
        // White Layout
        builder.setPiece(Bishop.defaultInstance(0, Alliance.WHITE));
        builder.setPiece(King.defaultInstance( 60, Alliance.WHITE));
        // Set the current player
        builder.setMoveMaker(Alliance.WHITE);
        //build the board
        final Board board = builder.build();
        final Collection<Move> whiteLegals = board.whitePlayer().getLegalMoves();
        final Collection<Move> blackLegals = board.blackPlayer().getLegalMoves();
        assertEquals(board.getTile(0).getPiece(), board.getTile(0).getPiece());
        assertNotNull(board.getTile(0).getPiece());
        assertEquals(whiteLegals.size(), 12);
        assertEquals(blackLegals.size(), 5);
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("a8"), Utility.getCoordinateAtPosition("b7"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("a8"), Utility.getCoordinateAtPosition("c6"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("a8"), Utility.getCoordinateAtPosition("d5"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("a8"), Utility.getCoordinateAtPosition("e4"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("a8"), Utility.getCoordinateAtPosition("f3"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("a8"), Utility.getCoordinateAtPosition("g2"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("a8"), Utility.getCoordinateAtPosition("h1"))));
    }

    public void testTopRightBishopOnEmptyBoard() {
        Board.Builder builder = new Board.Builder();
        // Black Layout
        builder.setPiece(King.defaultInstance(4, Alliance.BLACK));
        // White Layout
        builder.setPiece(Bishop.defaultInstance(7, Alliance.WHITE));
        builder.setPiece(King.defaultInstance( 60, Alliance.WHITE));
        // Set the current player
        builder.setMoveMaker(Alliance.WHITE);
        //build the board
        final Board board = builder.build();
        final Collection<Move> whiteLegals = board.whitePlayer().getLegalMoves();
        final Collection<Move> blackLegals = board.blackPlayer().getLegalMoves();
        assertEquals(whiteLegals.size(), 12);
        assertEquals(blackLegals.size(), 5);
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("h8"), Utility.getCoordinateAtPosition("g7"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("h8"), Utility.getCoordinateAtPosition("f6"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("h8"), Utility.getCoordinateAtPosition("e5"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("h8"), Utility.getCoordinateAtPosition("d4"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("h8"), Utility.getCoordinateAtPosition("c3"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("h8"), Utility.getCoordinateAtPosition("b2"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("h8"), Utility.getCoordinateAtPosition("a1"))));
    }

    public void testBottomLeftBishopOnEmptyBoard() {
        Board.Builder builder = new Board.Builder();
        // Black Layout
        builder.setPiece(King.defaultInstance(4, Alliance.BLACK));
        // White Layout
        builder.setPiece(Bishop.defaultInstance(56, Alliance.WHITE));
        builder.setPiece(King.defaultInstance( 60, Alliance.WHITE));
        // Set the current player
        builder.setMoveMaker(Alliance.WHITE);
        //build the board
        final Board board = builder.build();
        final Collection<Move> whiteLegals = board.whitePlayer().getLegalMoves();
        final Collection<Move> blackLegals = board.blackPlayer().getLegalMoves();
        assertEquals(whiteLegals.size(), 12);
        assertEquals(blackLegals.size(), 5);
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("a1"), Utility.getCoordinateAtPosition("b2"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("a1"), Utility.getCoordinateAtPosition("c3"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("a1"), Utility.getCoordinateAtPosition("d4"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("a1"), Utility.getCoordinateAtPosition("e5"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("a1"), Utility.getCoordinateAtPosition("f6"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("a1"), Utility.getCoordinateAtPosition("g7"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("a1"), Utility.getCoordinateAtPosition("h8"))));
    }

    public void testBottomRightBishopOnEmptyBoard() {
        Board.Builder builder = new Board.Builder();
        // Black Layout
        builder.setPiece(King.defaultInstance( 4, Alliance.BLACK));
        // White Layout
        builder.setPiece(Bishop.defaultInstance( 63, Alliance.WHITE));
        builder.setPiece(King.defaultInstance( 60, Alliance.WHITE));
        // Set the current player
        builder.setMoveMaker(Alliance.WHITE);
        //build the board
        final Board board = builder.build();
        final Collection<Move> whiteLegals = board.whitePlayer().getLegalMoves();
        final Collection<Move> blackLegals = board.blackPlayer().getLegalMoves();
        assertEquals(whiteLegals.size(), 12);
        assertEquals(blackLegals.size(), 5);
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("h1"), Utility.getCoordinateAtPosition("g2"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("h1"), Utility.getCoordinateAtPosition("f3"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("h1"), Utility.getCoordinateAtPosition("e4"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("h1"), Utility.getCoordinateAtPosition("d5"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("h1"), Utility.getCoordinateAtPosition("c6"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("h1"), Utility.getCoordinateAtPosition("b7"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("h1"), Utility.getCoordinateAtPosition("a8"))));
    }

    public void testMiddleRookOnEmptyBoard() {
        final Board.Builder builder = new Board.Builder();
        // Black Layout
        builder.setPiece(King.defaultInstance( 4, Alliance.BLACK));
        // White Layout
        builder.setPiece(King.defaultInstance(36, Alliance.WHITE));
        builder.setPiece(King.defaultInstance(60, Alliance.WHITE));
        // Set the current player
        builder.setMoveMaker(Alliance.WHITE);
        final Board board = builder.build();
        final Collection<Move> whiteLegals = board.whitePlayer().getLegalMoves();
        final Collection<Move> blackLegals = board.blackPlayer().getLegalMoves();
        assertEquals(whiteLegals.size(), 18);
        assertEquals(blackLegals.size(), 5);
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("e8"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("e7"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("e6"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("e5"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("e3"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("e2"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("a4"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("b4"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("c4"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("d4"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("f4"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("g4"))));
        assertTrue(whiteLegals.contains(Move.MoveFactory
                .deliver(board, Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("h4"))));
    }

    public void testPawnPromotion() {
        final Board.Builder builder = new Board.Builder();
        // Black Layout
        builder.setPiece(Rook.defaultInstance(3, Alliance.BLACK));
        builder.setPiece(King.defaultInstance(22, Alliance.BLACK));
        // White Layout
        builder.setPiece(Pawn.defaultInstance(15, Alliance.WHITE));
        builder.setPiece(King.defaultInstance(52, Alliance.WHITE));
        // Set the current player
        builder.setMoveMaker(Alliance.WHITE);
        final Board board = builder.build();
        final Move m1 = Move.MoveFactory.deliver(board, Utility.getCoordinateAtPosition(
                "h7"), Utility.getCoordinateAtPosition("h8"));
        final MoveTransition t1 = board.currentPlayer().makeMove(m1);
        assertTrue(t1.getMoveStatus().isDone());
        final Move m2 = Move.MoveFactory.deliver(t1.getTransitionBoard(), Utility.getCoordinateAtPosition("d8"), Utility.getCoordinateAtPosition("h8"));
        final MoveTransition t2 = t1.getTransitionBoard().currentPlayer().makeMove(m2);
        assertTrue(t2.getMoveStatus().isDone());
        final Move m3 = Move.MoveFactory.deliver(t2.getTransitionBoard(), Utility.getCoordinateAtPosition("e2"), Utility.getCoordinateAtPosition("d2"));
        final MoveTransition t3 = board.currentPlayer().makeMove(m3);
        assertTrue(t3.getMoveStatus().isDone());
    }

    public void testSimpleWhiteEnPassant() {
        final Board.Builder builder = new Board.Builder();
        // Black Layout
        builder.setPiece(King.defaultInstance( 4, Alliance.BLACK));
        builder.setPiece(Pawn.defaultInstance(11, Alliance.BLACK));
        // White Layout
        builder.setPiece(Pawn.defaultInstance(52, Alliance.WHITE));
        builder.setPiece(King.defaultInstance(60, Alliance.WHITE));
        // Set the current player
        builder.setMoveMaker(Alliance.WHITE);
        final Board board = builder.build();
        final Move m1 = Move.MoveFactory.deliver(board, Utility.getCoordinateAtPosition(
                "e2"), Utility.getCoordinateAtPosition("e4"));
        final MoveTransition t1 = board.currentPlayer().makeMove(m1);
        assertTrue(t1.getMoveStatus().isDone());
        final Move m2 = Move.MoveFactory.deliver(t1.getTransitionBoard(), Utility.getCoordinateAtPosition("e8"), Utility.getCoordinateAtPosition("d8"));
        final MoveTransition t2 = t1.getTransitionBoard().currentPlayer().makeMove(m2);
        assertTrue(t2.getMoveStatus().isDone());
        final Move m3 = Move.MoveFactory.deliver(t2.getTransitionBoard(), Utility.getCoordinateAtPosition("e4"), Utility.getCoordinateAtPosition("e5"));
        final MoveTransition t3 = t2.getTransitionBoard().currentPlayer().makeMove(m3);
        assertTrue(t3.getMoveStatus().isDone());
        final Move m4 = Move.MoveFactory.deliver(t3.getTransitionBoard(), Utility.getCoordinateAtPosition("d7"), Utility.getCoordinateAtPosition("d5"));
        final MoveTransition t4 = t3.getTransitionBoard().currentPlayer().makeMove(m4);
        assertTrue(t4.getMoveStatus().isDone());
        final Move m5 = Move.MoveFactory.deliver(t4.getTransitionBoard(), Utility.getCoordinateAtPosition("e5"), Utility.getCoordinateAtPosition("d6"));
        final MoveTransition t5 = t4.getTransitionBoard().currentPlayer().makeMove(m5);
        assertTrue(t5.getMoveStatus().isDone());
    }

    public void testSimpleBlackEnPassant() {
        final Board.Builder builder = new Board.Builder();
        // Black Layout
        builder.setPiece(King.defaultInstance( 4, Alliance.BLACK));
        builder.setPiece(Pawn.defaultInstance(11, Alliance.BLACK));
        // White Layout
        builder.setPiece(Pawn.defaultInstance( 52, Alliance.WHITE));
        builder.setPiece(King.defaultInstance( 60, Alliance.WHITE));
        // Set the current player
        builder.setMoveMaker(Alliance.WHITE);
        final Board board = builder.build();
        final Move m1 = Move.MoveFactory.deliver(board, Utility.getCoordinateAtPosition(
                "e1"), Utility.getCoordinateAtPosition("d1"));
        final MoveTransition t1 = board.currentPlayer().makeMove(m1);
        assertTrue(t1.getMoveStatus().isDone());
        final Move m2 = Move.MoveFactory.deliver(t1.getTransitionBoard(), Utility.getCoordinateAtPosition("d7"), Utility.getCoordinateAtPosition("d5"));
        final MoveTransition t2 = t1.getTransitionBoard().currentPlayer().makeMove(m2);
        assertTrue(t2.getMoveStatus().isDone());
        final Move m3 = Move.MoveFactory.deliver(t2.getTransitionBoard(), Utility.getCoordinateAtPosition("d1"), Utility.getCoordinateAtPosition("c1"));
        final MoveTransition t3 = t2.getTransitionBoard().currentPlayer().makeMove(m3);
        assertTrue(t3.getMoveStatus().isDone());
        final Move m4 = Move.MoveFactory.deliver(t3.getTransitionBoard(), Utility.getCoordinateAtPosition("d5"), Utility.getCoordinateAtPosition("d4"));
        final MoveTransition t4 = t3.getTransitionBoard().currentPlayer().makeMove(m4);
        assertTrue(t4.getMoveStatus().isDone());
        final Move m5 = Move.MoveFactory.deliver(t4.getTransitionBoard(), Utility.getCoordinateAtPosition("e2"), Utility.getCoordinateAtPosition("e4"));
        final MoveTransition t5 = t4.getTransitionBoard().currentPlayer().makeMove(m5);
        assertTrue(t5.getMoveStatus().isDone());
        final Move m6 = Move.MoveFactory.deliver(t5.getTransitionBoard(), Utility.getCoordinateAtPosition("d4"), Utility.getCoordinateAtPosition("e3"));
        final MoveTransition t6 = t5.getTransitionBoard().currentPlayer().makeMove(m6);
        assertTrue(t6.getMoveStatus().isDone());
    }

    public void testEnPassant2() {
        final Board board = Board.generateStandardBoard(Player.PlayerType.COMPUTER, Player.PlayerType.COMPUTER);
        final Move m1 = Move.MoveFactory.deliver(board, Utility.getCoordinateAtPosition(
                "e2"), Utility.getCoordinateAtPosition("e3"));
        final MoveTransition t1 = board.currentPlayer().makeMove(m1);
        assertTrue(t1.getMoveStatus().isDone());
        final Move m2 = Move.MoveFactory.deliver(t1.getTransitionBoard(), Utility.getCoordinateAtPosition("h7"), Utility.getCoordinateAtPosition("h5"));
        final MoveTransition t2 = t1.getTransitionBoard().currentPlayer().makeMove(m2);
        assertTrue(t2.getMoveStatus().isDone());
        final Move m3 = Move.MoveFactory.deliver(t2.getTransitionBoard(), Utility.getCoordinateAtPosition("e3"), Utility.getCoordinateAtPosition("e4"));
        final MoveTransition t3 = t2.getTransitionBoard().currentPlayer().makeMove(m3);
        assertTrue(t3.getMoveStatus().isDone());
        final Move m4 = Move.MoveFactory.deliver(t3.getTransitionBoard(), Utility.getCoordinateAtPosition("h5"), Utility.getCoordinateAtPosition("h4"));
        final MoveTransition t4 = t3.getTransitionBoard().currentPlayer().makeMove(m4);
        assertTrue(t4.getMoveStatus().isDone());
        final Move m5 = Move.MoveFactory.deliver(t4.getTransitionBoard(), Utility.getCoordinateAtPosition("g2"), Utility.getCoordinateAtPosition("g4"));
        final MoveTransition t5 = t4.getTransitionBoard().currentPlayer().makeMove(m5);
        assertTrue(t5.getMoveStatus().isDone());
    }

    public void testKingEquality() {
        final Board board = Board.generateStandardBoard(Player.PlayerType.COMPUTER, Player.PlayerType.COMPUTER);
        final Board board2 = Board.generateStandardBoard(Player.PlayerType.COMPUTER, Player.PlayerType.COMPUTER);
        assertEquals(board.getTile(60).getPiece(), board2.getTile(60).getPiece());
        assertFalse(board.getTile(60).getPiece().equals(null));
    }

    /*@Test
    public void testHashCode() {
        final Board board = Board.generateStandardBoard();
        final Set<Piece> pieceSet = Sets.newHashSet(board.getAllPieces());
        final Set<Piece> whitePieceSet = Sets.newHashSet(board.getWhitePieces());
        final Set<Piece> blackPieceSet = Sets.newHashSet(board.getBlackPieces());
        assertTrue(pieceSet.size() == 32);
        assertTrue(whitePieceSet.size() == 16);
        assertTrue(blackPieceSet.size() == 16);
    }*/

}