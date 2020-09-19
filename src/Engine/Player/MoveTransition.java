package Engine.Player;

import Engine.Board.Board;
import Engine.Board.Move;

/**
 * Move Transition
 *
 * <p>
 * A {@code MoveTransition}'s purpose is to keep track of the post-move {@code Board}
 * while associating the {@code Move} with a {@code MoveStatus}. This allows for precise
 * control of the move-making process.
 */
public class MoveTransition {

    /**
     * A post-move {@code Board}.
     */
    private final Board transitionBoard;

    /**
     * The pre-transition {@code Move}.
     */
    private final Move move;

    /**
     * The post-move {@code MoveStatus}.
     */
    private final MoveStatus moveStatus;

    /** A public constructor for a {@code MoveTransition}. */
    public MoveTransition(final Board transitionBoard,
                          final Move move,
                          final MoveStatus moveStatus){
        this.transitionBoard = transitionBoard;
        this.move = move;
        this.moveStatus = moveStatus;
    }

    /**
     * A method to expose the post-move {@code MoveStatus}.
     *
     * @return the post-move {@code MoveStatus}
     */
    public MoveStatus getMoveStatus(){
        return this.moveStatus;
    }

    /**
     * A method to expose the post-move {@code Board}.
     *
     * @return post-move {@code Board}
     */
    public Board getTransitionBoard(){
        return this.transitionBoard;
    }

}
