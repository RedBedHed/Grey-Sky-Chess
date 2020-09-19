package Engine.Board;

import java.util.*;

import Engine.Pieces.Piece;

/**
 * Tile
 *
 * <p>
 * A {@code Tile} represents a single square on a chess board.
 * A {@code Tile} is either empty or occupied.
 *
 * @author Ellie Moore
 * @author Amir Afghani
 * @version 06.10.2020
 */
public abstract class Tile {

	/**
	 * The coordinate of the {@code Tile} on the current {@code Board}.
	 */
	protected final int tileCoordinate;

	/**
	 * A cache of all possible empty tiles.
	 */
	private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE;
	static {
		EMPTY_TILES_CACHE = createAllPossibleEmptyTiles();
	}

	/*
	 * A method to initialize the empty tiles cache.
	 */
	private static Map<Integer, EmptyTile> createAllPossibleEmptyTiles(){
		final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();
		for(int i = 0; i < Utility.NUMBER_OF_TILES; i++) {
			emptyTileMap.put(i, new EmptyTile(i));
		}
		return Collections.unmodifiableMap(emptyTileMap);
	}

	/**
	 * This is a Factory method to return a {@code Tile} given a coordinate and a {@code Piece}.
	 * If the {@code Piece} argument is null, the {@code EmptyTile} at the given coordinate will
	 * be taken directly from a static cache. Otherwise, this method will instantiate and return
	 * a new {@code OccupiedTile}.
	 *
	 * @param coordinate the coordinate of the {@code Tile} on the current {@code Board}
	 * @param piece the {@code Piece} at the given coordinate. (null if there is no {@code Piece})
	 * @return a new {@code Tile}
	 */
	public static Tile createTile(final int coordinate, final Piece piece) {
		return (piece != null)? new OccupiedTile(coordinate, piece): EMPTY_TILES_CACHE.get(coordinate);
	}

	/**
	 * A private constructor for a {@code Tile}
	 *
	 * @param tileCoordinate the coordinate of the {@code Tile} on the current {@code Board}
	 */
	private Tile(final int tileCoordinate) {
		this.tileCoordinate = tileCoordinate;
	}

	/**
	 * A boolean method to describe the tile using polymorphism, controlling flow at
	 * the client level.
	 *
	 * @return whether or not the {@code Tile} is occupied
	 */
	public abstract boolean isTileOccupied();

	/**
	 * A method to get the {@code Piece} on the tile or null if nonesuch.
	 *
	 * @return the {@code Piece} on the tile or null if nonesuch.
	 */
	public abstract Piece getPiece();

	/**
	 * A method to expose the {@code Tile} coordinate.
	 *
	 * @return the {@code Tile} coordinate
	 */
	public int getTileCoordinate(){
		return this.tileCoordinate;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public int hashCode(){
		return isTileOccupied()? getPiece().hashCode(): 0;
	}

	/**
	 * A method to return the Zobrist hash of the {@code Tile} or 0 if the {@code Tile}
	 * is unoccupied.
	 *
	 * @return the Zobrist hash of the tile.
	 */
	public abstract long zobristHash();

	/**
	 * Empty Tile
	 *
	 * <p>
	 * This is a {@code Tile} with a null {@code Piece}. An {@code EmptyTile} may be
	 * visited by any piece without conflict.
	 */
	private static final class EmptyTile extends Tile {

		/**
		 * A private constructor for an {@code EmptyTile}
		 *
		 * @param tileCoordinate the coordinate of the {@code Tile} on the current {@code Board}
		 */
		private EmptyTile(final int tileCoordinate){
			super(tileCoordinate);
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public String toString(){
			return "-";
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public boolean isTileOccupied() {
			return false;
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public Piece getPiece() {
			return null;
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public boolean equals(Object other){
			if(this == other) return true;
			if(other == null) return false;
			if(!(other instanceof EmptyTile)) return false;
			EmptyTile cast = (EmptyTile) other;
			return cast.getTileCoordinate() == this.getTileCoordinate();

		}

		/**
		 * @inheritDoc
		 */
		@Override
		public long zobristHash(){
			return 0;
		}

	}

	/**
	 * Occupied Tile
	 *
	 * <p>
	 * This is a {@code Tile} inhabited by a non-null {@code Piece}. An {@code OccupiedTile}
	 * may only be visited upon attack.
	 */
	private static final class OccupiedTile extends Tile {

		/**
		 * The {@code Piece} that resides on the {@code Tile}.
		 */
		private final Piece pieceOnTile;

		/**
		 * A private constructor for an {@code OccupiedTile}
		 *
		 * @param tileCoordinate the coordinate of the {@code Tile} on the current {@code Board}
		 */
		private OccupiedTile(final int tileCoordinate, final Piece pieceOnTile){
			super(tileCoordinate);
			this.pieceOnTile = pieceOnTile;
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public String toString(){
			return getPiece().getPieceAlliance().isBlack() ? getPiece().toString().toLowerCase():
					getPiece().toString();
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public boolean isTileOccupied() {
			return true;
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public Piece getPiece() {
			return pieceOnTile;
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public boolean equals(Object other){
			if(this == other) return true;
			if(other == null) return false;
			if(!(other instanceof OccupiedTile)) return false;
			OccupiedTile cast = (OccupiedTile) other;
			return cast.pieceOnTile.equals(this.pieceOnTile);
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public long zobristHash(){
			return getPiece().zobristHash();
		}

	}

}
