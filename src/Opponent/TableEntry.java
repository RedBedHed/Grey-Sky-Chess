package Opponent;

import Engine.Board.Move;

public final class TableEntry {

    protected int score;
    protected int depth;
    protected NodeType type;
    protected boolean keep;
    protected Move move;

    protected TableEntry(final int score,
                         final int depth,
                         final Move move){
        this.score = score;
        this.depth = depth;
        this.type = NodeType.EXACT;
        this.move = move;
        this.keep = false;
    }

    protected enum NodeType {
        ALPHA {
            @Override public boolean isExact(){ return false; }
            @Override public boolean isAlpha(){  return true; }
            @Override public boolean isBeta(){  return false; }
        },
        BETA {
            @Override public boolean isExact(){ return false; }
            @Override public boolean isAlpha(){ return false; }
            @Override public boolean isBeta(){   return true; }
        },
        EXACT {
            @Override public boolean isExact(){  return true; }
            @Override public boolean isAlpha(){ return false; }
            @Override public boolean isBeta(){  return false; }
        };
        public abstract boolean isExact();
        public abstract boolean isAlpha();
        public abstract boolean isBeta();
    }

    @Override
    public String toString(){
        return score + "=" + depth + "=" + type.toString().charAt(0) + "=" + (keep + "").charAt(0);
    }

}
