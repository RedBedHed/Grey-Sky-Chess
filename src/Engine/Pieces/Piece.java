package Engine.Pieces;
import java.util.*;

import Engine.Board.Board;
import Engine.Board.Move;

/**
 * Piece
 *
 * <p>
 * An abstract parent class for the several types of {@code Piece} in the game
 * of chess.
 *
 * @author Ellie Moore
 * @version 06.09.2020
 */
public abstract class Piece {

	/**
	 * The type of the {@code Piece}.
	 */
	protected final PieceType pieceType;

	/**
	 * The position of the {@code Piece} on the current game {@code Board}.
	 */
	protected final int piecePosition;

	/**
	 * The alliance of the {@code Piece}.
	 */
	protected final Alliance pieceAlliance;

	/**
	 * Indicates whether or not the piece has been moved.
	 */
	protected final boolean isFirstMove;

	/**
	 * Stores the {@code Piece}'s hash code for efficient retrieval.
	 */
	private final int cachedHashCode;

	/*
	 * A static cache for the Zobrist hash codes of each type of piece on
	 * each tile of the chess board.
	 */
	private static final long[][] ZOBRIST_HASH_CACHE;

	/**
	 * A {@code ShowcasePiece} with a NULL {@code PieceType}.
	 */
	public static final Piece NULL_PIECE;

	/** Static initialization block */
	static {
		ZOBRIST_HASH_CACHE = initZobristHashCache();
		NULL_PIECE = showcase(PieceType.NULL, -1, null);
	}

	/**
	 * A public constructor for a {@code Piece}.
	 *
	 * @param pieceType the type of {@code Piece}
	 * @param piecePosition the position of the {@code Piece}
	 * @param pieceAlliance the alliance of the {@code Piece}
	 * @param isFirstMove whether or not the {@code Piece} has been moved
	 */
	public Piece(final PieceType pieceType,
				 final int piecePosition,
				 final Alliance pieceAlliance,
				 final boolean isFirstMove){
		this.pieceType = pieceType;
		this.piecePosition = piecePosition;
		this.pieceAlliance = pieceAlliance;
		this.isFirstMove = isFirstMove;
		cachedHashCode = computeHashCode();
	}

	/*
	 * A method to compute the default hash code of the Piece.
	 */
	private int computeHashCode() {
		int result = (pieceAlliance != null? pieceType.hashCode(): 0);
		result = 31 * result + piecePosition;
		result = 31 * result + (pieceAlliance != null? pieceAlliance.hashCode(): 0);
		result = 31 * result + (isFirstMove? 1: 0);
		return result;
	}

	/*
	 * A method to initialize a Zobrist hash code cache using an under-approximation
	 * of Long.MAX_VALUE as an upper limit.
	 */
	private static long[][] initZobristHashCache() {
		final long[][] hashes = new long[12][64];
		final Random rgen = new Random();
		for(long[] h: hashes) for(int i = 0; i < h.length; i++)
			h[i] = ((((long)rgen.nextInt(Integer.MAX_VALUE) * (long)rgen.nextInt(Integer.MAX_VALUE)) << 1) +
					(long) rgen.nextInt(Integer.MAX_VALUE));
		return hashes;
	}

	/**
	 * A method to produce the Zobrist hash code for the {@code Piece}.
	 *
	 * @return the Zobrist hash code for the {@code Piece}
	 */
	public long zobristHash() {
		return ZOBRIST_HASH_CACHE[this.pieceType.ordinal() + (this.pieceAlliance.isWhite()? 0: 6)][this.piecePosition];
	}

	/**
	 * A method to produce the Zobrist hash code for the {@code Piece} given a piece position.
	 * Useful for calculating post-move hash codes.
	 *
	 * @return the Zobrist hash code for the {@code Piece}
	 */
	public long zobristHash(final int piecePosition) {
		return ZOBRIST_HASH_CACHE[this.pieceType.ordinal() + (this.pieceAlliance.isWhite()? 0: 6)][piecePosition];
	}

	/**
	 * A method to check for equality between two {@code Piece}s
	 *
	 * @param other the other {@code Piece}
	 * @return whether or not the {@code Piece} is equal to the argument
	 */
	@Override
	public boolean equals(final Object other){
		if(this == other) return true;
		if(!(other instanceof Piece)) return false;
		final Piece otherPiece = (Piece) other;
		return piecePosition == otherPiece.getPiecePosition() &&
				pieceType == otherPiece.getPieceType() &&
				pieceAlliance == otherPiece.getPieceAlliance() &&
				isFirstMove == otherPiece.isFirstMove();
	}

	/**
	 * A method to expose the {@code Piece}'s default hash code.
	 *
	 * @return the {@code Piece}'s default hash code
	 */
	@Override
	public int hashCode(){
		return this.cachedHashCode;
	}

	/**
	 * A method to expose the {@code Piece}'s position.
	 *
	 * @return the {@code Piece}'s position
	 */
	public int getPiecePosition(){
		return this.piecePosition;
	}

	/**
	 * A method to expose the {@code Piece}'s {@code Alliance}.
	 *
	 * @return the {@code Piece}'s position
	 */
	public Alliance getPieceAlliance() {
		return this.pieceAlliance;
	}

	/**
	 * A method to indicate whether or not the {@code Piece} has been moved.
	 *
	 * @return whether or not the {@code Piece} has been moved
	 */
	public boolean isFirstMove(){
		return this.isFirstMove;
	}

	/**
	 * A method to expose the {@code PieceType}.
	 *
	 * @return the {@code PieceType}
	 */
	public PieceType getPieceType(){
		return this.pieceType;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public String toString(){
		return this.pieceType.toString();
	}

	/**
	 * A method to return a heuristic value for the {@code Piece}.
	 *
	 * @return the {@code Piece}'s value
	 */
	public int getPieceValue(){
		return this.pieceType.getPieceValue();
	}

	/**
	 * A method to indicate whether or not the {@code Piece} is a
	 * {@code ShowcasePiece}.
	 *
	 * @return whether or not the {@code Piece} is a {@code ShowcasePiece}
	 */
	public boolean isShowcased(){
		return false;
	}

	/**
	 * A factory method to instantiate a display-only {@code Piece}.
	 *
	 * @param pieceAlliance the {@code Alliance} of the {@code Piece}
	 * @return a {@code ShowcasePiece}
	 */
	public static Piece showcase(final PieceType pieceType,
								 final int piecePosition,
								 final Alliance pieceAlliance){
		return new ShowcasePiece(pieceType, piecePosition, pieceAlliance);
	}

	/**
	 * A factory method to instantiate a display-only {@code Piece} from
	 * an existing {@code Piece}.
	 *
	 * @param piece the {@code Piece} to be showcased
	 * @return a {@code ShowcasePiece}
	 */
	public static Piece showcase(final Piece piece){
		return piece.isShowcased()? piece: new ShowcasePiece(piece);
	}

	private static final class ShowcasePiece extends Piece {

		private ShowcasePiece(final PieceType pieceType,
							  final int piecePosition,
							  final Alliance alliance){
			super(pieceType, piecePosition, alliance, false);
		}

		private ShowcasePiece(final Piece piece){
			super(piece.pieceType, piece.piecePosition, piece.pieceAlliance, false);
		}

		@Override
		public Collection<Move> calculateLegalMoves(Board board) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Piece movePiece(Move move) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isShowcased(){
			return true;
		}

	}

	/**
	 * A method to calculate the legal moves of the {@code Piece} given the current
	 * {@code Board}.
	 *
	 * @param board the current {@code Board}
	 * @return a {@code Collection} of the {@code Piece}'s legal moves
	 */
	public abstract Collection<Move> calculateLegalMoves(final Board board);

	/**
	 * A method to instantiate a new {@code Piece} at the destination coordinate
	 * of the given {@code Move}.
	 *
	 * @param move the move to make
	 * @return a moved {@code Piece}
	 */
	public abstract Piece movePiece(Move move);

	/**
	 * Piece Type
	 *
	 * <p>
	 * An enumeration to describe each {@code Piece}.
	 */
	public enum PieceType {

		PAWN("P", 100){

			/** @inheritDoc */
			@Override
			public boolean isKing() {
				return false;
			}

			/** @inheritDoc */
			@Override
			public boolean isRook() {
				return false;
			}

			/** @inheritDoc */
			@Override
			public boolean isPawn() {
				return true;
			}

		},
		KNIGHT("N", 300){

			/** @inheritDoc */
			@Override
			public boolean isKing() {
				return false;
			}

			/** @inheritDoc */
			@Override
			public boolean isRook() {
				return false;
			}

			/** @inheritDoc */
			@Override
			public boolean isPawn() {
				return false;
			}

		},
		BISHOP("B", 300){

			/** @inheritDoc */
			@Override
			public boolean isKing() {
				return false;
			}

			/** @inheritDoc */
			@Override
			public boolean isRook() {
				return false;
			}

			/** @inheritDoc */
			@Override
			public boolean isPawn() {
				return false;
			}

		},
		ROOK("R", 500){

			/** @inheritDoc */
			@Override
			public boolean isKing() {
				return false;
			}

			/** @inheritDoc */
			@Override
			public boolean isRook() {
				return true;
			}

			/** @inheritDoc */
			@Override
			public boolean isPawn() {
				return false;
			}

		},
		QUEEN("Q", 900){

			/** @inheritDoc */
			@Override
			public boolean isKing() {
				return false;
			}

			/** @inheritDoc */
			@Override
			public boolean isRook() {
				return false;
			}

			/** @inheritDoc */
			@Override
			public boolean isPawn() {
				return false;
			}

		},
		KING("K", 10000){

			/** @inheritDoc */
			@Override
			public boolean isKing() {
				return true;
			}

			/** @inheritDoc */
			@Override
			public boolean isRook() {
				return false;
			}

			/** @inheritDoc */
			@Override
			public boolean isPawn() {
				return false;
			}

		},
		NULL("-",0){

			/** @inheritDoc */
			@Override
			public boolean isKing() {
				return false;
			}

			/** @inheritDoc */
			@Override
			public boolean isRook() {
				return false;
			}

			/** @inheritDoc */
			@Override
			public boolean isPawn() {
				return false;
			}

		};

		/**
		 * A {@code String} to represent the {@code PieceType}.
		 */
		private String pieceName;

		/**
		 * An integer to represent the value of the {@code PieceType}.
		 */
		private int pieceValue;

		/**
		 * A constructor for a {@code PieceType}.
		 *
		 * @param pieceName the name
		 * @param pieceValue the value
		 */
		PieceType(final String pieceName, final int pieceValue){
			this.pieceName = pieceName;
			this.pieceValue = pieceValue;
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public String toString(){
			return pieceName;
		}

		/**
		 * A {@code Map} of each {@code PieceType} to its representative character.
		 */
		public static final Map<Character, PieceType> VALUES = initVALUES();


		// A method to initialize the VALUES Map.
		public static final Map<Character, PieceType> initVALUES(){
			Map<Character, PieceType> m = new HashMap<>();
			for(PieceType p: values()) m.put(p.pieceName.charAt(0), p);
			return Collections.unmodifiableMap(m);
		}

		/**
		 * A method to expose the pieceValue variable.
		 *
		 * @return the value of the {@code PieceType}
		 */
		public int getPieceValue(){
			return pieceValue;
		}

		/**
		 * A descriptive phrase that allows for polymorphic
		 * control of flow at the client level.
		 *
		 * @return whether or not the {@code PieceType} is a {@code King}
		 */
		public abstract boolean isKing();

		/**
		 * A descriptive phrase that allows for polymorphic
		 * control of flow at the client level.
		 *
		 * @return whether or not the {@code PieceType} is a {@code Rook}
		 */
		public abstract boolean isRook();

		/**
		 * A descriptive phrase that allows for polymorphic
		 * control of flow at the client level.
		 *
		 * @return whether or not the {@code PieceType} is a {@code Pawn}
		 */
		public abstract boolean isPawn();

	}

}
