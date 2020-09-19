package Engine.Pieces;

import Engine.Board.Utility;
import Engine.Player.Player;
import Engine.Player.WhitePlayer;
import Engine.Player.BlackPlayer;

/**
 * Alliance
 *
 * <p>
 * An enumeration to represent the {@code Alliance} of each {@code Piece}.
 * An {@code Alliance} is either white or black.
 *
 * @author Ellie Moore
 * @version 06.08.2020
 */
public enum Alliance {

    WHITE {

        /** @inheritDoc */
        @Override
        public boolean isWhite() {
            return true;
        }

        /** @inheritDoc */
        @Override
        public boolean isBlack() {
            return false;
        }

        /** @inheritDoc */
        @Override
        public boolean isPawnPromotionTile(int position) {
            return Utility.EIGHTH_ROW[position];
        }

        /** @inheritDoc */
        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer,
                                   final BlackPlayer blackPlayer) {
            return whitePlayer;
        }

        /** @inheritDoc */
        @Override
        public String toString() {
            return "White Player";
        }

        @Override
        public Alliance opposite(){
            return Alliance.BLACK;
        }

    },
    BLACK {

        /** @inheritDoc */
        @Override
        public boolean isBlack() {
            return true;
        }

        /** @inheritDoc */
        @Override
        public boolean isWhite() {
            return false;
        }

        /** @inheritDoc */
        @Override
        public boolean isPawnPromotionTile(int position) {
            return Utility.FIRST_ROW[position];
        }

        /** @inheritDoc */
        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer,
                                   final BlackPlayer blackPlayer) {
            return blackPlayer;
        }

        /** @inheritDoc */
        @Override
        public String toString() {
            return "Black Player";
        }

        @Override
        public Alliance opposite(){
            return Alliance.WHITE;
        }

    };

    /**
     * A method to parse the alliance of a character which represents a
     * chess {@code Piece}.
     *
     * @param c the character to analyze
     * @return the {@code Alliance} of the {@code Piece} character
     */
    public static Alliance parseAlliance(final char c){
        switch(c){
            case 'P': case 'R': case 'B': case 'N': case 'Q': case 'K':
                return Alliance.WHITE;
            case 'p': case 'r': case 'b': case 'n': case 'q': case 'k':
                return Alliance.BLACK;
            default: throw new IllegalArgumentException("Argument character is invalid.");
        }
    }

    /**
     * A polymorphic and readability-enhancing approach for describing
     * each alliance.
     *
     * @return whether or not the {@code Alliance} is white
     */
    public abstract boolean isWhite();

    /**
     * A polymorphic and readability-enhancing approach for describing
     * each alliance.
     *
     * @return whether or not the {@code Alliance} is black
     */
    public abstract boolean isBlack();

    /**
     * A polymorphic method for determining whether or not a given
     * position is a pawn promotion tile for the {@code Alliance}.
     *
     * @param position the position to check
     * @return whether or not the given position is a promotion tile
     */
    public abstract boolean isPawnPromotionTile(int position);

    /**
     * A method for determining which {@code Player} belongs to the {@code Alliance}.
     *
     * @param whitePlayer the current {@code WhitePlayer}
     * @param blackPlayer the current {@code BlackPlayer}
     * @return the player associated with the the {@code Alliance}
     */
    public abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);

    /**
     * A method to get the opposite {@code Alliance}.
     *
     * @return the opposite {@code Alliance}.
     */
    public abstract Alliance opposite();

}
