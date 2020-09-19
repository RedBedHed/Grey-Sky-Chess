package Engine.Player;

import Engine.Board.Move;

/**
 * Move Status
 *
 * <p>
 * A {@code Move Status} is a polymorphic approach to preventing illegal moves
 * or moves that result in check for the current player. A move transition with
 * any move status other than {@code DONE} will recieve the pre-move {@code Board}
 * as its {@code transitionBoard} at initialization. In this manner, the unsuccessful
 * move will be ignored and the current {@code Player} will be free to try again
 * until their move is successful.
 *
 * @see MoveTransition
 * @see Player#makeMove(Move, boolean)
 */
public enum MoveStatus {

    /** Move successfully executed. */
    DONE {

        /** @inheritDoc */
        @Override
        public boolean isDone() {
            return true;
        }

    },

    /** Move unsuccessful. */
    ILLEGAL_MOVE {

        /** @inheritDoc */
        @Override
        public boolean isDone() {
            return false;
        }

    },

    /** Move unsuccessful. */
    LEAVES_PLAYER_IN_CHECK {

        /** @inheritDoc */
        @Override
        public boolean isDone() {
            return false;
        }

    };

    /**
     * A description of a {@code MoveStatus} to allow for polymorphic control
     * of flow at the client level.
     *
     * @return whether or not the {@code MoveStatus} is {@code DONE}
     */
    public abstract boolean isDone();

}
