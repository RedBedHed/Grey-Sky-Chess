package Opponent;

import Engine.Board.Board;
import Engine.Board.Move;
import GUI.MoveLog;

public interface MoveStrategy {

    Move execute(Board board, MoveLog log, boolean allMovesAreExhausted);

}
