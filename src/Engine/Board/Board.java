package Engine.Board;

import Engine.Pieces.*;
import Engine.Player.Player;
import Engine.Player.WhitePlayer;
import Engine.Player.BlackPlayer;
import Engine.Player.Player.PlayerType;
import Engine.Pieces.Alliance;

import java.util.*;

/**
 * Board
 *
 * <p>
 * This is a class representative of a chess board. A {@code Board}
 * is an immutable and disposable {@code Object}. A new {@code Board}
 * is instantiated for each {@code Move} that takes place during the
 * chess game. The immutable design of the {@code Board} class is
 * vital to this chess engine and offers a plethora of benefits.
 * Among these are maintainability and reliability.
 *
 * <p>
 * Firstly, immutable design helps to simplify much of the logic
 * involved in moving each {@code Piece} (for both human and A.I.
 * players), guaranteeing concise and clean code throughout the
 * {@code Board}'s clientele and offering better readability and
 * maintainability than mutable design.
 *
 * <p>
 * Secondly, immutable design is widely considered to be safe and
 * reliable. The immutable nature of this {@code Board} serves to
 * encourage immutable practices elsewhere in the engine, helping
 * protect the engine from bugs inherent in mutable code.
 *
 * @author Ellie Moore
 * @author Amir Afghani
 * @version 06.09.2020
 */
public final class Board {

	/**
	 * A {@code List} of individual {@code Tile}s which constitute the game board.
	 */
	private final List<Tile> gameBoard;

	/**
	 * A {@code Collection} of active {@code Piece}s for each {@code Alliance}.
	 */
	private final Collection<Piece> whitePieces;
	private final Collection<Piece> blackPieces;

	/**
	 * A {@code Player} for each {@code Alliance}.
	 */
	private final WhitePlayer whitePlayer;
	private final BlackPlayer blackPlayer;

	/**
	 * A variable to keep track of the {@code Board}'s current {@code Player}.
	 */
	private final Player currentPlayer;

	/**
	 * A variable to keep track of the {@code Board}'s en passant {@code Pawn}.
	 */
	private final Pawn enPassantPawn;

	/**
	 * A private constructor for a board, meant to be called from the {@code Builder}.
	 *
	 * @param builder the {@code Builder} currently in use
	 * @see Builder#build()
	 */
	private Board(final Builder builder){
		populateActivePieces(
			this.gameBoard = generateStandardBoard(builder),
			this.whitePieces = new ArrayList<>(),
			this.blackPieces = new ArrayList<>()
		);
        this.enPassantPawn = builder.enPassantPawn;
		final Collection<Move> whiteStdLegalMoves = calculateLegalMoves(this.whitePieces);
        final Collection<Move> blackStdLegalMoves = calculateLegalMoves(this.blackPieces);
		this.whitePlayer = new WhitePlayer(
				this, whiteStdLegalMoves, blackStdLegalMoves,
				builder.isWhiteCastled, builder.hasWhitePromoted, builder.whitePlayerType
		);
		this.blackPlayer = new BlackPlayer(
				this, blackStdLegalMoves, whiteStdLegalMoves,
				builder.isBlackCastled, builder.hasBlackPromoted, builder.blackPlayerType
		);
		this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whitePlayer, this.blackPlayer);
	}

	/**
	 * A method generate an immutable {@code List} of {@code Tile}s from a {@code Builder}'s
	 * board configuration.
	 *
	 * @param builder the current builder
	 * @return an immutable {@code List} of {@code Tile}s representing the current board
	 */
	private static List<Tile> generateStandardBoard(final Builder builder){
		final List<Tile> tiles = new ArrayList<>();
		for(int i = 0; i < Utility.NUMBER_OF_TILES; i++){
			tiles.add(Tile.createTile(i, builder.boardConfig.get(i)));
		}
		return Collections.unmodifiableList(tiles);
	}

	/**
	 * A method to calculate the legal {@code Move}s for a {@code Collection} of
	 * {@code Pieces}.
	 *
	 * @param pieces the {@code Pieces} for which to calculate {@code Move}s
	 * @return all legal {@code Move}s for the given {@code Pieces}
	 */
	private Collection<Move> calculateLegalMoves(final Collection<Piece> pieces) {
		final List<Move> legalMoves = new ArrayList<>();
		for(final Piece piece: pieces){
			legalMoves.addAll(piece.calculateLegalMoves(this));
		}
		return Collections.unmodifiableList(legalMoves);
	}

	/**
	 * A method to calculate the active {@code Pieces} in the {@code Board}'s
	 * {@code Tile Collection}.
	 *
	 * @param gameBoard a {@code List} of {@code Tile}s representing the current board
	 * @param whitePieces a {@code Collection} of all {@code Piece}s with a white {@code Alliance}
	 * @param blackPieces a {@code Collection} of all {@code Piece}s with a black {@code Alliance}
	 */
	private static void populateActivePieces(final List<Tile> gameBoard,
											 final Collection<Piece> whitePieces,
											 final Collection<Piece> blackPieces) {
		for(final Tile tile: gameBoard){
			if(tile.isTileOccupied()){
				final Piece piece = tile.getPiece();
				if(piece.getPieceAlliance().isWhite()) whitePieces.add(piece);
				else blackPieces.add(piece);
			}
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public String toString(){
		final StringBuilder out = new StringBuilder();
		int count = 0;
		for(int i = 0; i < Utility.NUMBER_OF_TILES; i++){
			Tile tile = this.gameBoard.get(i);
			if(tile.isTileOccupied()){
				if(count == 0) out.append(String.format("%s", tile));
				else out.append(String.format("%d%s", count, tile));
				count = 0;
			}
			else count++;
			if((i+1) % Utility.NUMBER_OF_TILES_IN_ROW_OR_COLUMN == 0){
				if(count != 0) out.append(count);
				out.append(i < 63? "/": "");
				count = 0;
			}
		}
		return out.toString();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean equals(Object other){
		if(this == other) return true;
		if(other == null) return false;
		if(!(other instanceof Board)) return false;
		Board cast = (Board) other;
		for(int i = 0; i < gameBoard.size(); i++)
			if(!gameBoard.get(i).equals(cast.gameBoard.get(i))) return false;
		return true;
	}

	/**
	 * A method to calculate the Zobrist hash code for the current board.
	 * Note: this method should only be invoked once per Minimax move calculation.
	 * All proceeding Zobrist hashes may be calculated simply by XOR-ing out the
	 * moved piece at its starting position and then XOR-ing it back in at its
	 * destination.
	 *
	 * @return the Zobrist hash code for the current board.
	 */
	public long zobristHash(){
		long count = 0;
		for(Tile t: gameBoard) count ^= t.zobristHash();
		return count;
	}

	/**
	 * A method to expose the white {@code Player}.
	 *
	 * @return the {@code board}'s {@code WhitePlayer}
	 */
	public WhitePlayer whitePlayer(){
		return this.whitePlayer;
	}

	/**
	 * A method to expose the black {@code Player}.
	 *
	 * @return the {@code board}'s {@code BlackPlayer}
	 */
	public BlackPlayer blackPlayer(){
		return this.blackPlayer;
	}

	/**
	 * A method to expose the current {@code Player}.
	 *
	 * @return the {@code board}'s current {@code Player}
	 */
	public Player currentPlayer(){
		return this.currentPlayer;
	}

	/**
	 * A method to expose the en passant {@code Pawn}.
	 *
	 * @return the {@code board}'s en passant {@code Pawn}
	 */
	public Pawn getEnPassantPawn(){
		return this.enPassantPawn;
	}

	/**
	 * A method to expose the black {@code Piece}s.
	 *
	 * @return the {@code board}'s black {@code Piece}s.
	 */
	public Collection<Piece> getBlackPieces(){
		return this.blackPieces;
	}

	/**
	 * A method to expose the white {@code Piece}s.
	 *
	 * @return the {@code board}'s white {@code Piece}s.
	 */
	public Collection<Piece> getWhitePieces(){
		return this.whitePieces;
	}

	/**
	 * A method to expose all active {@code Piece}s. This method is meant to be
	 * invoked only once per {@code Board}.
	 *
	 * @return all of the {@code board}'s active {@code Piece}s.
	 */
	public Collection<Piece> getAllActivePieces(){
		return Utility.concat(this.blackPieces, this.whitePieces);
	}

	/**
	 * A method to get all legal {@code Move}s on the {@code Board}. This method
	 * is meant to be invoked only once per {@code Board}.
	 *
	 * @return all legal {@code Moves}.
	 */
	public Collection<Move> getAllLegalMoves() {
		return Utility.concat(this.whitePlayer.getLegalMoves(), this.blackPlayer.getLegalMoves());
	}

	/**
	 * A method to get a {@code Tile} by coordinate.
	 *
	 * @param tileCoordinate the coordinate of the desired {@code Tile}
	 * @return the {@code Tile} at the given coordinate
	 */
	public Tile getTile(final int tileCoordinate) {
		return this.gameBoard.get(tileCoordinate);
	}

	/**
	 * A factory method to generate a standard {@code Board} with all default, unmoved
	 * {@code Pieces} in their starting positions.
	 *
	 * @return a standard {@code Board} with all {@code Pieces} in their initial positions
	 */
	public static Board generateStandardBoard(final PlayerType whitePlayerType,
											  final PlayerType blackPlayerType){
		return new Builder()
		//Set upper half of the Board with black pieces.
		.setPiece(Rook.defaultInstance(0, Alliance.BLACK))
		.setPiece(Knight.defaultInstance(1, Alliance.BLACK))
		.setPiece(Bishop.defaultInstance(2, Alliance.BLACK))
		.setPiece(Queen.defaultInstance(3, Alliance.BLACK))
		.setPiece(King.defaultInstance(4, Alliance.BLACK))
		.setPiece(Bishop.defaultInstance(5, Alliance.BLACK))
		.setPiece(Knight.defaultInstance(6, Alliance.BLACK))
		.setPiece(Rook.defaultInstance(7, Alliance.BLACK))
		.setPiece(Pawn.defaultInstance(8, Alliance.BLACK))
		.setPiece(Pawn.defaultInstance(9, Alliance.BLACK))
		.setPiece(Pawn.defaultInstance(10, Alliance.BLACK))
		.setPiece(Pawn.defaultInstance(11, Alliance.BLACK))
		.setPiece(Pawn.defaultInstance(12, Alliance.BLACK))
		.setPiece(Pawn.defaultInstance(13, Alliance.BLACK))
		.setPiece(Pawn.defaultInstance(14, Alliance.BLACK))
		.setPiece(Pawn.defaultInstance(15, Alliance.BLACK))
		//Set lower half of the Board with white pieces.
		.setPiece(Pawn.defaultInstance(48, Alliance.WHITE))
		.setPiece(Pawn.defaultInstance(49, Alliance.WHITE))
		.setPiece(Pawn.defaultInstance(50, Alliance.WHITE))
		.setPiece(Pawn.defaultInstance(51, Alliance.WHITE))
		.setPiece(Pawn.defaultInstance(52, Alliance.WHITE))
		.setPiece(Pawn.defaultInstance(53, Alliance.WHITE))
		.setPiece(Pawn.defaultInstance(54, Alliance.WHITE))
		.setPiece(Pawn.defaultInstance(55, Alliance.WHITE))
		.setPiece(Rook.defaultInstance(56, Alliance.WHITE))
		.setPiece(Knight.defaultInstance(57, Alliance.WHITE))
		.setPiece(Bishop.defaultInstance(58, Alliance.WHITE))
		.setPiece(Queen.defaultInstance(59, Alliance.WHITE))
		.setPiece(King.defaultInstance(60, Alliance.WHITE))
		.setPiece(Bishop.defaultInstance(61, Alliance.WHITE))
		.setPiece(Knight.defaultInstance(62, Alliance.WHITE))
		.setPiece(Rook.defaultInstance(63, Alliance.WHITE))
		// Set move maker and instantiate the Board.
		.setMoveMaker(Alliance.WHITE)
		// Set the PlayerType
		.setPlayerType(whitePlayerType, blackPlayerType)
		.build();
	}

	/**
	 * Builder
	 *
	 * <p>
	 * A {@code Builder} is a design pattern that allows for greater specificity
	 * during the instantiation of a {@code Board}. Taking the place of a long
	 * and confusing constructor, this {@code Builder} uses specific methods to
	 * initialize each field of the {@code Board} under construction. This
	 * {@code Builder} also enables the client to set {@code Piece}s iteratively.
	 */
	public static final class Builder {

		/**
		 * The {@code Piece} configuration for the {@code Board} under construction.
		 */
		private final Map<Integer, Piece> boardConfig;

		/**
		 * The move maker (turn taker) for the {@code Board} under construction.
		 */
		private Alliance nextMoveMaker;

		/**
		 * The en passant pawn for the {@code Board} under construction.
		 */
		private Pawn enPassantPawn;

		/**
		 * Boolean fields to indicate whether or not an {@code Alliance} has
		 * castled. For use in {@code Player} initialization.
		 */
		private boolean isWhiteCastled;
		private boolean isBlackCastled;

		/**
		 * Boolean fields to indicate whether or not the {@code Player} has
		 * promoted a {@code Pawn}.
		 */
		private boolean hasWhitePromoted;
		private boolean hasBlackPromoted;

		/**
		 * Fields to indicate the type of each {@code Player}.
		 */
		private PlayerType whitePlayerType;
		private PlayerType blackPlayerType;

		/**
		 * A public constructor for a {@code Builder}.
		 */
		public Builder(){
			this.boardConfig = new HashMap<>();
			this.isBlackCastled = false;
			this.isWhiteCastled = false;
			this.hasBlackPromoted = false;
			this.hasWhitePromoted = false;
		}

		/**
		 * A method to insert a piece into the configuration.
		 *
		 * @param piece the piece to be inserted
		 * @return the instance
		 */
		public Builder setPiece(final Piece piece){
			this.boardConfig.put(piece.getPiecePosition(), piece);
			return this;
		}

		/**
		 * A method to set the move maker.
		 *
		 * @param nextMoveMaker the upcoming move maker
		 * @return the instance
		 */
		public Builder setMoveMaker(final Alliance nextMoveMaker){
			this.nextMoveMaker = nextMoveMaker;
			return this;
		}

		/**
		 * A method to set the en passant pawn when applicable.
		 *
		 * @param enPassantPawn the enpassant pawn
		 * @return the instance
		 */
		public Builder setEnPassantPawn(Pawn enPassantPawn) {
			this.enPassantPawn = enPassantPawn;
			return this;
		}

		/**
		 * A method to set {@code isWhiteCastled} and {@code isBlackCastled}
		 * respectively.
		 *
		 * @param currentPlayer the current {@code Player}  of the previous board
		 * @param isCastled whether or not the current {@code Player} is castled
		 * @return the instance
		 */
		public Builder setIsCastled(final Player currentPlayer,
									boolean isCastled){
			final boolean isCurrentPlayerWhite = currentPlayer.getAlliance().isWhite();
			this.isWhiteCastled = isCurrentPlayerWhite?
					isCastled : currentPlayer.getOpponent().isCastled();
			this.isBlackCastled = isCurrentPlayerWhite?
					currentPlayer.getOpponent().isCastled() : isCastled;
			return this;
		}

		/**
		 * A method to set {@code isWhiteCastled} and {@code isBlackCastled}
		 * respectively.
		 *
		 * @param isBlackCastled whether or not the white {@code Player} is castled
		 * @param isWhiteCastled whether or not the white {@code Player} is castled
		 * @return the instance
		 */
		public Builder setIsCastled(boolean isBlackCastled,
									boolean isWhiteCastled){
			this.isWhiteCastled = isBlackCastled;
			this.isBlackCastled = isWhiteCastled;
			return this;
		}

		/**
		 * A method to set {@code isWhitePawnPromoted} and {@code isBlackPawnPromoted}
		 * respectively.
		 *
		 * @param currentPlayer the current {@code Player}  of the previous board
		 * @param hasPromoted whether or not the current {@code Player} is castled
		 * @return the instance
		 */
		public Builder setHasPromoted(final Player currentPlayer,
									  final boolean hasPromoted){
			final boolean isCurrentPlayerWhite = currentPlayer.getAlliance().isWhite();
			this.hasWhitePromoted = isCurrentPlayerWhite?
					hasPromoted: currentPlayer.getOpponent().hasPromoted();
			this.hasBlackPromoted = isCurrentPlayerWhite?
					currentPlayer.getOpponent().hasPromoted() : hasPromoted;
			return this;
		}

		/**
		 * A method to set {@code isWhitePawnPromoted} and {@code isBlackPawnPromoted}
		 * respectively.
		 *
		 * @param hasWhitePromoted whether or not the {@code WhitePlayer} has promoted.
		 * @param hasBlackPromoted whether or not the {@code BlackPlayer} has promoted.
		 * @return the instance
		 */
		public Builder setHasPromoted(final boolean hasWhitePromoted,
									  final boolean hasBlackPromoted){
			this.hasWhitePromoted = hasWhitePromoted;
			this.hasBlackPromoted = hasBlackPromoted;
			return this;
		}

		/**
		 * A method to set the {@code PlayerType} for each player. The {@code PlayerType}
		 * will either be User or Computer.
		 *
		 * @param whitePlayerType the white {@code PlayerType}
		 * @param blackPlayerType the black {@code PlayerType}
		 * @return the instance
		 */
		public Builder setPlayerType(final PlayerType whitePlayerType,
									 final PlayerType blackPlayerType) {
			this.whitePlayerType = whitePlayerType;
			this.blackPlayerType = blackPlayerType;
			return this;
		}

		/**
		 * A method to invoke the Board constructor, instantiating a new board
		 * using the {@code Builder}'s configuration.
		 *
		 * @return a new {@code Board}
		 */
		public Board build(){
			return new Board(this);
		}

	}

}
