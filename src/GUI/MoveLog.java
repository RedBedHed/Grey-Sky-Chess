package GUI;
import Engine.Board.Move;
import Engine.Board.Move.MoveFactory;

import java.util.ArrayList;
import java.util.List;

public class MoveLog {

    private final List<Move> moves;

    public MoveLog(){
        this.moves = new ArrayList<>();
    }

    public List<Move> getMoves() {
        return this.moves;
    }

    public void addMove(final Move move){
        this.moves.add(MoveFactory.showcase(move));
    }

    public int size(){
        return this.moves.size();
    }

    public void clear(){
        this.moves.clear();
    }

    public Move removeMove(final int index){
        return this.moves.remove(index);
    }

    public boolean removeMove(final Move move){
        return this.moves.remove(move);
    }

}


