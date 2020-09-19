package Engine.Board;

import Engine.Pieces.*;
import Engine.Player.Player.PlayerType;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Utility
 *
 * <p>
 * A class full of static {@code Board}-related support functions.
 *
 * @author Ellie Moore
 * @version 06.09.2020
 */
public class Utility {

    /**
     * The full number of tiles on a standard {@code Board}.
     */
    public static final int NUMBER_OF_TILES = 64;

    /**
     * The number of tiles in a row or column.
     */
    public static final int NUMBER_OF_TILES_IN_ROW_OR_COLUMN = 8;

    /**
     * Boolean arrays for use in determining whether or not a location is within
     * a named column.
     */
    public static final boolean[] FIRST_COLUMN = initColumn(0);
    public static final boolean[] SECOND_COLUMN = initColumn(1);
    public static final boolean[] SEVENTH_COLUMN = initColumn(6);
    public static final boolean[] EIGHTH_COLUMN = initColumn(7);

    /**
     * Boolean arrays for use in determining whether or not a location is within
     * a named row.
     */
    public static final boolean[] FIRST_ROW = initRow(7);
    public static final boolean[] SECOND_ROW = initRow(6);
    public static final boolean[] THIRD_ROW = initRow(5);
    public static final boolean[] FOURTH_ROW = initRow(4);
    public static final boolean[] FIFTH_ROW = initRow(3);
    public static final boolean[] SIXTH_ROW = initRow(2);
    public static final boolean[] SEVENTH_ROW = initRow(1);
    public static final boolean[] EIGHTH_ROW = initRow(0);

    /**
     * A {@code List} of {@code String}s to represent each location on the board with
     * algebraic notation.
     */
    public static final List<String> ALGEBRAIC_NOTATION = initAlgebraicNotation();

    /**
     * A map of algebraic position {@code String}s to {@code Integer} locations.
     */
    public static final Map<String, Integer> POSITION_TO_COORDINATE = initPositionToCoordinateMap();

    /**
     * Prevents instantiation.
     */
    private Utility() {
    }

    /*
     * A method to initialize each column boolean array. The argument passed to this
     * method must be a zero-indexed integer.
     */
    private static boolean[] initColumn(int columnNumber) {
        final boolean[] column = new boolean[NUMBER_OF_TILES];
        do {
            column[columnNumber] = true;
            columnNumber += NUMBER_OF_TILES_IN_ROW_OR_COLUMN;
        } while (columnNumber < NUMBER_OF_TILES);
        return column;
    }

    /*
     * A method to initialize each row boolean array. The argument passed to this
     * method must be a zero-indexed integer.
     */
    private static boolean[] initRow(int rowStart) {
        final boolean[] row = new boolean[NUMBER_OF_TILES];
        rowStart *= NUMBER_OF_TILES_IN_ROW_OR_COLUMN;
        final int rowEnd = rowStart + NUMBER_OF_TILES_IN_ROW_OR_COLUMN;
        do row[rowStart++] = true;
        while (rowStart < rowEnd);
        return row;
    }

    /*
     * A method to initialize a List for use in converting integer coordinates
     * to algebraic notation.
     */
    private static List<String> initAlgebraicNotation() {
        return Collections.unmodifiableList(Arrays.asList(
                "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"
        ));
    }

    /*
     * A method to initialize a Map for use in converting an algebraic
     * String to an integer coordinate.
     */
    private static Map<String, Integer> initPositionToCoordinateMap() {
        final Map<String, Integer> positionToCoordinate = new HashMap<>();
        for (int i = 0; i < NUMBER_OF_TILES; i++) {
            positionToCoordinate.put(ALGEBRAIC_NOTATION.get(i), i);
        }
        return Collections.unmodifiableMap(positionToCoordinate);
    }

    /**
     * A utility method to concatenate two {@code Collection}s.
     *
     * @param firstCollection  the first {@code Collection}
     * @param secondCollection the second {@code Collection}
     * @param <T>              the type
     * @return a single {@code Collection}
     */
    public static <T> Collection<T> concat(final Collection<T> firstCollection,
                                           final Collection<T> secondCollection) {
        final List<T> completeCollection = new ArrayList<>();
        completeCollection.addAll(firstCollection);
        completeCollection.addAll(secondCollection);
        return Collections.unmodifiableList(completeCollection);
    }

    /**
     * A method to validate a tile coordinate.
     *
     * @param coordinate the coordinate to be validated
     * @return whether or not the given coordinate is within the bounds of a regulation chess board
     */
    public static boolean isValidTileCoordinate(final int coordinate) {
        return coordinate >= 0 && coordinate < NUMBER_OF_TILES;
    }

    /**
     * A method to convert a given algebraic {@code String} to an integer coordinate.
     *
     * @param position an algebraic {@code String} representation of a coordinate
     * @return an integer coordinate
     */
    public static int getCoordinateAtPosition(final String position) {
        return POSITION_TO_COORDINATE.get(position);
    }

    /**
     * A method to convert a given integer coordinate to algebraic notation.
     *
     * @param coordinate the coordinate to be converted
     * @return the algebraic representation of the coordinate
     */
    public static String getPositionAtCoordinate(final int coordinate) {
        return ALGEBRAIC_NOTATION.get(coordinate);
    }

    /**
     * A method to convert a {@code Board} to a FEN {@code String}.
     *
     * @param board the {@code Board} to be converted
     * @return a FEN {@code String} describing the {@code Board}
     */
    public static String boardToFENString(final Board board) {
        return board.toString() + " " +
                getCurrentPlayerText(board) + " " +
                getCastleAvailabilityText(board) + " " +
                getEnPassantText(board) + " " +
                getIsCastledText(board) + " " +
                getHasPromotedText(board) + " " +
                board.whitePlayer().getPlayerType().toString().charAt(0);
    }

    private static String getCurrentPlayerText(final Board board) {
        return board.currentPlayer().toString().substring(0, 1);
    }

    private static String getCastleAvailabilityText(final Board board) {
        StringBuilder sb = new StringBuilder();
        if (board.whitePlayer().isKingSideCastleCapable()) sb.append("K");
        if (board.whitePlayer().isQueenSideCastleCapable()) sb.append("Q");
        if (board.blackPlayer().isKingSideCastleCapable()) sb.append("k");
        if (board.blackPlayer().isQueenSideCastleCapable()) sb.append("q");
        final String resultString = sb.toString();
        return resultString.isEmpty() ? "-" : resultString;
    }

    private static String getIsCastledText(final Board board){
        return (board.whitePlayer().isCastled()? "t": "f") +
                (board.blackPlayer().isCastled()? "t": "f");
    }

    private static String getHasPromotedText(final Board board){
        return (board.whitePlayer().hasPromoted()? "t": "f") +
                (board.blackPlayer().hasPromoted()? "t": "f");
    }

    private static String getEnPassantText(final Board board) {
        final Pawn enPassantPawn = board.getEnPassantPawn();
        return enPassantPawn == null ? "-" : getPositionAtCoordinate(
                enPassantPawn.getPiecePosition()
        );
    }

    /**
     * A method to convert a {@code Move} to a {@code String}.
     *
     * @param move the {@code Move} to be converted
     * @return a {@code String} describing the {@code Move}
     */
    public static String moveToPGNString(final Move move){
        final boolean isMovedPieceWhite = move.getMovedPiece().getPieceAlliance().isWhite();
        return move.getMoveType() +
                (isMovedPieceWhite? move.getMovedPiece().toString():
                        move.getMovedPiece().toString().toLowerCase()) +
                (move.getCurrentCoordinate() < 10? "0" + move.getCurrentCoordinate():
                        move.getCurrentCoordinate()) +
                (move.getDestinationCoordinate() < 10? "0" + move.getDestinationCoordinate():
                        move.getDestinationCoordinate()) +
                (move.isAttack()? (isMovedPieceWhite? move.getAttackedPiece().toString().toLowerCase():
                        move.getAttackedPiece().toString()): "-") + " ";
    }

    /**
     * A method to parse a {@code Board} from a FEN String procedurally.
     *
     * @param fen the {@code String} to analyze
     * @return a {@code Board} set to the {@code String}'s specifications
     */
    public static Board parseFEN(final String fen) {
        //CONSTANTS /////////////////////
        final char WHITE_PAWN = 'P';
        final char WHITE_ROOK = 'R';
        final char WHITE_BISHOP = 'B';
        final char WHITE_KNIGHT = 'N';
        final char WHITE_QUEEN = 'Q';
        final char WHITE_KING = 'K';
        final char BLACK_PAWN = 'p';
        final char BLACK_ROOK = 'r';
        final char BLACK_BISHOP = 'b';
        final char BLACK_KNIGHT = 'n';
        final char BLACK_QUEEN = 'q';
        final char BLACK_KING = 'k';
        final char WHITE = 'W';
        final char BLACK = 'B';
        final char TRUE = 't';
        final char USER = 'U';
        final int NUMBER_OF_SECTIONS = 7;
        //////////////////////////////////
        //Split FEN String around whitespace into easy-to-manage sections.
        final String[] state = Pattern.compile("\\s").split(fen);
        if(state.length != NUMBER_OF_SECTIONS)
            throw new IllegalArgumentException("FEN String is improperly formatted.");
        //Establish sections.
        final String BOARD = state[0];
        final String MOVE_MAKER = state[1];
        final String CASTLE_CAPABILITY = state[2];
        final String EN_PASSANT = state[3];
        final String IS_CASTLED = state[4];
        final String HAS_PROMOTED = state[5];
        final String WHITE_PLAYER_TYPE = state[6];
        //Check Length.
        if(CASTLE_CAPABILITY.length() > 4)
            throw new IllegalArgumentException("'CASTLE_CAPABILITY' FEN section is improperly formatted.");
        if(IS_CASTLED.length() != 2)
            throw new IllegalArgumentException("'IS_CASTLED' FEN section is improperly formatted.");
        if(HAS_PROMOTED.length() != 2)
            throw new IllegalArgumentException("'HAS_PROMOTED' FEN section is improperly formatted.");
        if(WHITE_PLAYER_TYPE.length() != 1)
            throw new IllegalArgumentException("'WHITE_PLAYER_TYPE' FEN section is improperly formatted.");
        if(MOVE_MAKER.length() != 1)
            throw new IllegalArgumentException("'MOVE_MAKER' FEN section is improperly formatted.");
        if(EN_PASSANT.length() > 2)
            throw new IllegalArgumentException("'EN_PASSANT' FEN section is improperly formatted.");
        //Find en passant pawn (if one exists).
        final int EPP_LOCATION = EN_PASSANT.charAt(0) != '-' ?
                getCoordinateAtPosition(EN_PASSANT) : -1;
        //Determine castle capability.
        boolean whiteKSCC = false;
        boolean whiteQSCC = false;
        boolean blackKSCC = false;
        boolean blackQSCC = false;
        for (int i = 0; i < CASTLE_CAPABILITY.length(); i++) {
            switch (CASTLE_CAPABILITY.charAt(i)) {
                case BLACK_KING:
                    blackKSCC = true;
                    break;
                case BLACK_QUEEN:
                    blackQSCC = true;
                    break;
                case WHITE_KING:
                    whiteKSCC = true;
                    break;
                case WHITE_QUEEN:
                    whiteQSCC = true;
                    break;
            }
        }
        //Determine Player characteristics... En passant pawn... Move maker...
        Pawn EPP = null;
        int position = 0;
        final Board.Builder builder = new Board.Builder();
        builder.setIsCastled(
                IS_CASTLED.charAt(0) == TRUE, IS_CASTLED.charAt(1) == TRUE
        );
        builder.setHasPromoted(
                HAS_PROMOTED.charAt(0) == TRUE, HAS_PROMOTED.charAt(1) == TRUE
        );
        builder.setMoveMaker(MOVE_MAKER.charAt(0) == WHITE ? Alliance.WHITE : Alliance.BLACK);
        builder.setPlayerType(
                WHITE_PLAYER_TYPE.charAt(0) == USER? PlayerType.USER: PlayerType.COMPUTER, PlayerType.COMPUTER
        );
        //Determine board layout.
        for (int j = 0; j < BOARD.length(); j++) {
            final char c = BOARD.charAt(j);
            switch (c) {
                case '/':
                    break;
                case '1':
                    position += 1;
                    break;
                case '2':
                    position += 2;
                    break;
                case '3':
                    position += 3;
                    break;
                case '4':
                    position += 4;
                    break;
                case '5':
                    position += 5;
                    break;
                case '6':
                    position += 6;
                    break;
                case '7':
                    position += 7;
                    break;
                case '8':
                    position += 8;
                    break;
                default:
                    switch (c) {
                        case BLACK_PAWN:
                            if (position != EPP_LOCATION)
                                builder.setPiece(Pawn.defaultInstance(position, Alliance.BLACK));
                            else builder.setPiece(EPP = Pawn.movedInstance(position, Alliance.BLACK));
                            break;
                        case BLACK_ROOK:
                                builder.setPiece(position == 7 && blackKSCC || position == 0 && blackQSCC?
                                        Rook.defaultInstance(position, Alliance.BLACK) :
                                        Rook.movedInstance(position, Alliance.BLACK)
                                );
                            break;
                        case BLACK_KNIGHT:
                            builder.setPiece(Knight.defaultInstance(position, Alliance.BLACK));
                            break;
                        case BLACK_BISHOP:
                            builder.setPiece(Bishop.defaultInstance(position, Alliance.BLACK));
                            break;
                        case BLACK_KING:
                                if(blackKSCC || blackQSCC)
                                    builder.setPiece(King.defaultInstance(position, Alliance.BLACK));
                                else builder.setPiece(King.movedInstance(position, Alliance.BLACK));
                            break;
                        case BLACK_QUEEN:
                            builder.setPiece(Queen.defaultInstance(position, Alliance.BLACK));
                            break;
                        case WHITE_PAWN:
                            if (position != EPP_LOCATION)
                                builder.setPiece(Pawn.defaultInstance(position, Alliance.WHITE));
                            else builder.setPiece(EPP = Pawn.movedInstance(position, Alliance.WHITE));
                            break;
                        case WHITE_ROOK:
                                builder.setPiece(position == 63 && whiteKSCC || position == 56 && whiteQSCC?
                                        Rook.defaultInstance(position, Alliance.WHITE) :
                                        Rook.movedInstance(position, Alliance.WHITE)
                                );
                            break;
                        case WHITE_KNIGHT:
                            builder.setPiece(Knight.defaultInstance(position, Alliance.WHITE));
                            break;
                        case WHITE_BISHOP:
                            builder.setPiece(Bishop.defaultInstance(position, Alliance.WHITE));
                            break;
                        case WHITE_KING:
                                if(whiteKSCC || whiteQSCC)
                                    builder.setPiece(King.defaultInstance(position, Alliance.WHITE));
                                else builder.setPiece(King.movedInstance(position, Alliance.WHITE));
                            break;
                        case WHITE_QUEEN:
                            builder.setPiece(Queen.defaultInstance(position, Alliance.WHITE));
                            break;
                        default:
                            throw new IllegalArgumentException("'BOARD' FEN section is improperly formatted.");
                    }
                    position++;
            }
        }
        //Set the en passant pawn, if it exists.
        if (EPP != null) builder.setEnPassantPawn(EPP);
        //Build the board.
        return builder.build();
    }

}
