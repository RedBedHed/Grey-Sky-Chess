package Engine.Board;

import Engine.Pieces.Alliance;
import Engine.Pieces.Pawn;
import Engine.Pieces.Piece;
import Engine.Board.Board.Builder;
import Engine.Pieces.Rook;
import Engine.Player.Player;
import Engine.Pieces.Piece.PieceType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Move
 *
 * <p>
 * This is an abstract umbrella class with many concrete
 * sub-classes representing each type of move that is possible
 * in the game of chess.
 *
 * <p>
 * In chess, all moves belong to two main categories: passive
 * and aggressive. A passive move is one with an empty tile as
 * its destination. No pieces are captured during a passive move.
 * An aggressive move, in contrast, is one with an occupied tile
 * as its destination. A piece is always captured during an
 * aggressive move.
 *
 * <p>
 * For the purposes of this chess engine, all direct extensions
 * of {@code Move} (except for {@code AttackMove}) are considered
 * to be passive. Additionally, castling {@code Move}s on both
 * sides of the board are considered to be passive. Only
 * {@code AttackMove} and its direct extensions are considered to
 * be aggressive.
 *
 * @author Ellie Moore
 * @author Amir Afghani
 * @version 06.09.2020
 */
public abstract class Move {

	/**
	 * The current {@code Board}.
	 */
	protected final Board board;

	/**
	 * The {@code Piece} to be moved.
	 */
	protected final Piece movedPiece;

	/**
	 * The destination coordinate.
	 */
	protected final int destinationCoordinate;

	/**
	 * A boolean to represent whether or not this {@code Move} is the first
	 * {@code Move} to be made by the moved {@code Piece}.
	 */
	protected final boolean isFirstMove;

	/**
	 * The type of {@code Move} for use in recording move history in a *.pgn file.
	 */
	private final MoveType type;

	/*
	 * A private constructor for a Move.
	 */
	private Move(final Board board,
				 final Piece movedPiece,
				 final int destinationCoordinate,
				 final MoveType type) {
		this.board = board;
		this.movedPiece = movedPiece;
		this.destinationCoordinate = destinationCoordinate;
		this.isFirstMove = movedPiece != null && movedPiece.isFirstMove();
		this.type = type;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public int hashCode(){
		int result;
		result = 31 * this.destinationCoordinate;
		result += 31 * (this.movedPiece == null? this.movedPiece.hashCode(): 0);
		result += 31 * this.movedPiece.getPiecePosition();
		return result;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean equals(final Object other){
		if(this == other) return true;
		if(!(other instanceof Move)) return false;
		final Move otherMove = (Move) other;
		return	getCurrentCoordinate() == otherMove.getCurrentCoordinate() &&
				getDestinationCoordinate() == otherMove.getDestinationCoordinate() &&
				getMovedPiece().equals(otherMove.getMovedPiece());
	}

	/**
	 * A method to expose the current {@code Board}.
	 *
	 * @return the current {@code Board}
	 */
	public Board getBoard(){
		return this.board;
	}

	/**
	 * A method to return the current coordinate of the {@code Piece} to be moved.
	 *
	 * @return the current coordinate of the {@code Piece} to be moved
	 */
	public int getCurrentCoordinate(){
		return movedPiece.getPiecePosition();
	}

	/**
	 * A method to expose the destination coordinate of the {@code Move}.
	 *
	 * @return the destination coordinate of the {@code Move}
	 */
	public int getDestinationCoordinate() {
		return this.destinationCoordinate;
	}

	/**
	 * A method to expose the {@code Piece} to be moved.
	 *
	 * @return the {@code Piece} to be moved
	 */
	public Piece getMovedPiece(){
		return this.movedPiece;
	}

	/**
	 * A method to indicate whether or not the {@code Move} is an {@code AttackMove}.
	 *
	 * @return whether or not the move is an {@code AttackMove}
	 */
	public boolean isAttack(){
		return false;
	}

	/**
	 * A method to indicate whether or not the {@code Move} is a {@code CastlingMove}.
	 *
	 * @return whether or not the {@code Move} is a {@code CastlingMove}
	 */
	public boolean isCastlingMove(){
		return false;
	}

	/**
	 * A method to return the attacked piece. Returns a null showcase piece for
	 * non-attack {@code Move}s.
	 *
	 * @return the {@code Piece} under attack or a null piece if nonesuch
	 */
	public Piece getAttackedPiece(){
		return Piece.NULL_PIECE;
	}

	/**
	 * A method to expose the {@code MoveType}.
	 *
	 * @return the {@code MoveType} associated with the {@code Move}
	 */
	public MoveType getMoveType(){
		return type;
	}

	/**
	 * A method to indicate whether or not the {@code Move} is a {@code ShowcaseMove}.
	 * This method is useful in preventing a {@code Move} from being showcased (limited)
	 * more than once.
	 *
	 * @return whether or not the {@code Move} is a {@code ShowcaseMove}.
	 */
	public boolean isShowcased(){
		return false;
	}

	/**
	 * A method to "move" the {@code Piece} from the current location to the destination.
	 * This method builds a new {@code Board}. All {@code Pieces} will be recycled except
	 * for the moved piece, which will be replaced with a twin instance. This twin instance
	 * will have one difference: its {@code isFirstMove} field will be set to false. Each
	 * {@code Board} is disposable by design (left for jvm garbage collection). This enables
	 * the {@code Board} class to achieve immutability in exchange for a reduction in memory
	 * efficiency.
	 *
	 * @param isAI whether or not this {@code Move} is executed by an AI opponent
	 * @return a new {@code Board} with the moved {@code Piece} at the destination position
	 */
	public Board execute(final boolean isAI) {
		final Board.Builder builder = new Builder();
		for(final Piece piece: this.board.currentPlayer().getActivePieces()){
			if(!this.movedPiece.equals(piece)){
				builder.setPiece(piece);
			}
		}
		for(final Piece piece: this.board.currentPlayer().getOpponent().getActivePieces()){
			builder.setPiece(piece);
		}
		//"Moves" the piece.
		builder.setPiece(this.movedPiece.movePiece(this))
		.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance())
		.setIsCastled(this.board.currentPlayer(), this.board.currentPlayer().isCastled())
		.setHasPromoted(this.board.currentPlayer(), this.board.currentPlayer().hasPromoted())
		.setPlayerType(
				this.board.whitePlayer().getPlayerType(),
				this.board.blackPlayer().getPlayerType()
		);
		return builder.build();
	}

	/**
	 * Elite Move
	 *
	 * <p>
	 * A {@code EliteMove} represents a passive move to be made by any {@code Piece}
	 * other than a {@code Pawn}.
	 */
	public static final class EliteMove extends Move {

		/** A public constructor for a {@code MajorMove}. */
		public EliteMove(final Board board,
						 final Piece movedPiece,
						 final int destinationCoordinate){
			super(board, movedPiece, destinationCoordinate, MoveType.ELITE_MOVE);
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public boolean equals(final Object other){
			return this == other || other instanceof EliteMove && super.equals(other);
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public String toString(){
			return movedPiece.getPieceType().toString() +
					Utility.getPositionAtCoordinate(this.destinationCoordinate);
		}

	}

	/**
	 * Attack Move
	 *
	 * <p>
	 * A parent class for the various types of attack {@code Move}s.
	 */
	public static class AttackMove extends Move {

		/**
		 * The piece under attack.
		 */
		protected final Piece attackedPiece;

		/** A public constructor for an {@code AttackMove}. */
		private AttackMove(final Board board,
						  final Piece movedPiece,
						  final int destinationCoordinate,
						  final Piece attackedPiece,
						  final MoveType moveType){
			super(board, movedPiece, destinationCoordinate, moveType);
			this.attackedPiece = attackedPiece;
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public int hashCode(){
			return this.attackedPiece.hashCode() + super.hashCode();
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public boolean equals(final Object other){
			if(this == other) return true;
			if(!(other instanceof AttackMove)) return false;
			final AttackMove otherAttackMove = (AttackMove) other;
			return super.equals(otherAttackMove) &&
					getAttackedPiece() == otherAttackMove.getAttackedPiece();
		}

		/**
		 * {@inheritDoc}
		 *
		 * <p>
		 * This implementation of {@link Move#execute(boolean)} will replace the attacked {@code Piece}
		 * with the moved {@code Piece}.
		 *
		 * @return a new {@code Board} with the moved {@code Piece} at the destination position
		 */
		@Override
		public Board execute(final boolean isAI){
			final Builder builder = new Builder();
			for(final Piece piece: this.board.currentPlayer().getActivePieces()){
				if(!this.movedPiece.equals(piece)){
					builder.setPiece(piece);
				}
			}
			for(final Piece piece: this.board.currentPlayer().getOpponent().getActivePieces()){
				if(!piece.equals(this.getAttackedPiece())){
					builder.setPiece(piece);
				}
			}
			builder.setPiece(this.movedPiece.movePiece(this))
			.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance())
			.setIsCastled(this.board.currentPlayer(), this.board.currentPlayer().isCastled())
			.setHasPromoted(this.board.currentPlayer(), this.board.currentPlayer().hasPromoted())
			.setPlayerType(
							this.board.whitePlayer().getPlayerType(),
							this.board.blackPlayer().getPlayerType()
			);
			return builder.build();
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public boolean isAttack(){
			return true;
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public Piece getAttackedPiece(){
			return this.attackedPiece;
		}

	}

	/**
	 * Elite Attack Move
	 *
	 * <p>
	 * A {@code EliteAttackMove} is an attack move to be made by a {@code Piece} with a
	 * major value (i.e. not a {@code Pawn}).
	 */
	public static final class EliteAttackMove extends AttackMove {

		/** A public constructor for a {@code MajorAttackMove}. */
		public EliteAttackMove(final Board board,
							   final Piece movedPiece,
							   final int destinationCoordinate,
							   final Piece attackedPiece){
			super(board, movedPiece, destinationCoordinate, attackedPiece, MoveType.ELITE_ATTACK_MOVE);
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public boolean equals(final Object other){
			return this == other || other instanceof EliteAttackMove && super.equals(other);
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public String toString(){
			return movedPiece.getPieceType() +
					Utility.getPositionAtCoordinate(this.destinationCoordinate);
		}

	}

	/**
	 * Pawn Move
	 *
	 * <p>
	 * A {@code PawnMove} represents a passive move to be made by a {@code Pawn}.
	 */
	public static final class PawnMove extends Move {

		/** A public constructor for a {@code PawnMove}. */
		public PawnMove(final Board board,
						 final Piece movedPiece,
						 final int destinationCoordinate){
			super(board, movedPiece, destinationCoordinate, MoveType.PAWN_MOVE);
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public boolean equals(final Object other){
			return this == other || other instanceof PawnMove && super.equals(other);
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public String toString(){
			return Utility.getPositionAtCoordinate(this.destinationCoordinate);
		}

	}

	/**
	 * Pawn Attack Move
	 *
	 * <p>
	 * A {@code PawnAttackMove} represents an attack move to be made by a {@code Pawn}.
	 */
	public static final class PawnAttackMove extends AttackMove {

		/** A public constructor for a {@code PawnAttackMove}. */
		public PawnAttackMove(final Board board,
						      final Piece movedPiece,
							  final int destinationCoordinate,
							  final Piece attackedPiece){
			super(board, movedPiece, destinationCoordinate, attackedPiece, MoveType.PAWN_ATTACK_MOVE);
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public boolean equals(final Object other){
			return this == other || other instanceof PawnAttackMove && super.equals(other);
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public String toString(){
			return Utility.getPositionAtCoordinate(this.movedPiece.getPiecePosition()).substring(0, 1)
					+ "x" + Utility.getPositionAtCoordinate(this.destinationCoordinate);
		}

	}

	/**
	 * Pawn En Passant Attack Move
	 *
	 * <p>
	 * A {@code PawnEnPassantAttackMove} represents an en passant attack move to be
	 * made by a {@code Pawn}.
	 */
	public static final class PawnEnPassantAttackMove extends AttackMove {

		/** A public constructor for a {@code PawnEnPassantAttackMove}. */
		public PawnEnPassantAttackMove(final Board board,
							  final Piece movedPiece,
							  final int destinationCoordinate,
							  final Piece attackedPiece){
			super(board, movedPiece, destinationCoordinate, attackedPiece, MoveType.PAWN_EN_PASSANT_ATTACK_MOVE);
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public boolean equals(final Object other){
			return this == other || other instanceof PawnEnPassantAttackMove && super.equals(other);
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public String toString(){
			return Utility.getPositionAtCoordinate(this.movedPiece.getPiecePosition()).substring(0, 1)
					+ "x" + Utility.getPositionAtCoordinate(this.destinationCoordinate);
		}

	}

	/**
	 * Pawn Promotion
	 *
	 * <p>
	 * A {@code PawnPromotion} represents a passive OR attack move to be made by a
	 * {@code Pawn} upon reaching the far side of the {@code Board}. The move passed
	 * to the constructor is decorated with logic to replace the moved piece with a
	 * Queen.
	 */
	public static final class PawnPromotion extends Move {

		/**
		 * The move to be decorated as a {@code PawnPromotion}.
		 */
		private final Move decoratedMove;

		/**
		 * The pawn to be promoted.
		 */
		private final Piece promotedPawn;

		/** A public constructor for a {@code PawnPromotion}. */
		public PawnPromotion(final Move decoratedMove) {
			super(
					decoratedMove.getBoard(), decoratedMove.getMovedPiece(),
					decoratedMove.getDestinationCoordinate(), MoveType.PAWN_PROMOTION
			);
			this.decoratedMove = decoratedMove;
			this.promotedPawn = decoratedMove.getMovedPiece();
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public int hashCode(){
			return decoratedMove.hashCode() + (31 * promotedPawn.hashCode());
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public boolean equals(final Object other){
			return this == other || other instanceof PawnPromotion && super.equals(other);
		}

		/**
		 * {@inheritDoc}
		 *
		 * <p>
		 * This implementation of {@link Move#execute(boolean)} will replace the moved {@code Pawn}
		 * with a {@code Queen} rather than the usual twin instance.
		 *
		 * @return a new {@code Board} with a {@code Queen} at the destination position
		 */
		@Override
		public Board execute(final boolean isAI){
			final Board.Builder builder = new Builder();
			for(final Piece piece: this.board.currentPlayer().getActivePieces()){
				if(!this.promotedPawn.equals(piece)) builder.setPiece(piece);
			}
			for(final Piece piece: this.board.currentPlayer().getOpponent().getActivePieces()){
				if(!piece.equals(this.getAttackedPiece())) builder.setPiece(piece);
			}
			builder.setPiece(((Pawn)this.promotedPawn).getPromotionPiece(isAI).movePiece(this))
			.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance())
			.setIsCastled(this.board.currentPlayer(), this.board.currentPlayer().isCastled())
			.setHasPromoted(this.board.currentPlayer(), true)
			.setPlayerType(
					this.board.whitePlayer().getPlayerType(),
					this.board.blackPlayer().getPlayerType()
			);
			return builder.build();
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public boolean isAttack(){
			return this.decoratedMove.isAttack();
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public Piece getAttackedPiece(){
			return this.decoratedMove.getAttackedPiece();
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public String toString(){
			return Utility.getPositionAtCoordinate(this.decoratedMove.getDestinationCoordinate()) + "Q";
		}

	}

	/**
	 * Pawn Jump
	 *
	 * <p>
	 * A {@code PawnJump} represents a passive move to be made by a {@code Pawn}.
	 * This {@code Move}'s destination is two {@code Tile}s directly ahead of the
	 * current {@code Pawn}'s current location. This {@code Move} may be accomplished
	 * by "jumping" over an occupied {@code Tile} and onto the next {@code EmptyTile}.
	 */
	public static final class PawnJump extends Move {

		/** A public constructor for a {@code PawnJump}. */
		public PawnJump(final Board board,
						final Piece movedPiece,
						final int destinationCoordinate){
			super(board, movedPiece, destinationCoordinate, MoveType.PAWN_JUMP);
		}

		/**
		 * {@inheritDoc}
		 *
		 * <p>
		 * This implementation of {@link Move#execute(boolean)} will set the moved {@code Piece} as the
		 * en passant {@code Pawn} for the new board per the rules of the game.
		 *
		 * @return a new {@code Board} with the moved {@code Pawn} at the destination position
		 */
		@Override
		public Board execute(final boolean isAI){
			final Builder builder = new Builder();
			for(final Piece piece: this.board.currentPlayer().getActivePieces()){
				if(!(this.movedPiece.equals(piece))){
					builder.setPiece(piece);
				}
			}
			for(final Piece piece: this.board.currentPlayer().getOpponent().getActivePieces()){
				builder.setPiece(piece);
			}
			final Pawn movedPawn = (Pawn)this.movedPiece.movePiece(this);
			builder.setPiece(movedPawn)
			.setEnPassantPawn(movedPawn)
			.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance())
			.setIsCastled(this.board.currentPlayer(), this.board.currentPlayer().isCastled())
			.setHasPromoted(this.board.currentPlayer(), this.board.currentPlayer().hasPromoted())
			.setPlayerType(
					this.board.whitePlayer().getPlayerType(),
					this.board.blackPlayer().getPlayerType()
			);
			return builder.build();
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public String toString(){
			return movedPiece.getPieceType().toString() +
					Utility.getPositionAtCoordinate(this.destinationCoordinate);
		}

	}

	/**
	 * Castle Move
	 *
	 * <p>
	 * An abstract class to handle the execution logic and all other functionality shared
	 * by both Queen-side and King-side {@code CastleMove}s.
	 */
	public static abstract class CastleMove extends Move {

		/**
		 * The {@code Rook} to be moved during the {@code CastleMove}.
		 */
		protected final Piece castleRook;

		/**
		 * The current location of the unmoved castle-{@code Rook}.
		 */
		protected final int castleRookStartPosition;

		/**
		 * The destination of the castle-{@code Rook}.
		 */
		protected final int castleRookDestination;

		/**
		 * A public constructor for a castle move, only to be invoked during
		 * the instantiation of a concrete extension of this class.
		 */
		private CastleMove(final Board board,
						  final Piece movedPiece,
						  final int destinationCoordinate,
						  final Piece castleRook,
						  final int castleRookDestination,
						  final MoveType moveType){
			super(board, movedPiece, destinationCoordinate, moveType);
			this.castleRook = castleRook;
			this.castleRookStartPosition = castleRook.getPiecePosition();
			this.castleRookDestination = castleRookDestination;
		}

		/**
		 * A method to expose the {@code Rook} to be moved during the {@code CastleMove}.
		 *
		 * @return the rook to be moved during the {@code CastleMove}
		 */
		public Piece getCastleRook(){
			return this.castleRook;
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public boolean isCastlingMove(){
			return true;
		}

		/**
		 * {@inheritDoc}
		 *
		 * <p>
		 * This implementation of {@link Move#execute(boolean)} will replace both the castle {@code Rook}
		 * and {@code King} with twin {@code Piece}s at the {@code Move}'s destination positions.
		 * The current {@code Player}'s {@code isCastled} variable will be set to true via 
		 * {@link Board.Builder#setIsCastled(Player, boolean)}
		 *
		 * @return a new {@code Board} with the moved {@code Piece}s at their destination positions
		 */
		@Override
		public Board execute(final boolean isAI){
			final Board.Builder builder = new Builder();
			for(final Piece piece: this.board.currentPlayer().getActivePieces()){
				if(!this.movedPiece.equals(piece) && !this.castleRook.equals(piece)){
					builder.setPiece(piece);
				}
			}
			for(final Piece piece: this.board.currentPlayer().getOpponent().getActivePieces()){
				builder.setPiece(piece);
			}
			builder.setPiece(this.movedPiece.movePiece(this));
			builder.setPiece(Rook.movedInstance(
					this.castleRookDestination, this.castleRook.getPieceAlliance()
			));
			builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance())
			.setIsCastled(this.board.currentPlayer(), true)
			.setHasPromoted(this.board.currentPlayer(), this.board.currentPlayer().hasPromoted())
			.setPlayerType(
					this.board.whitePlayer().getPlayerType(),
					this.board.blackPlayer().getPlayerType()
			);
			return builder.build();
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public int hashCode(){
			int result = super.hashCode();
			result = 31 + result + this.castleRook.hashCode();
			result = 31 + result + this.castleRookDestination;
			return result;
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public boolean equals(final Object other){
			if(this == other) return true;
			if(other instanceof CastleMove) return false;
			final CastleMove otherCastleMove = (CastleMove) other;
			return super.equals(otherCastleMove) && this.castleRook.equals(otherCastleMove.getCastleRook());
		}

	}

	/**
	 * King Side Castle Move
	 *
	 * <p>
	 * A {@code KingSideCastleMove} is a castling {@code Move} to be made on the
	 * {@code King}s initial side of the first or eighth rank.
	 */
	public static final class KingSideCastleMove extends CastleMove {

		/** A public constructor for a {@code KingSideCastleMove} */
		public KingSideCastleMove(final Board board,
						          final Piece movedPiece,
						          final int destinationCoordinate,
								  final Piece castleRook,
								  final int castleRookDestination){
			super(
					board, movedPiece, destinationCoordinate, castleRook,
					castleRookDestination, MoveType.KING_SIDE_CASTLE_MOVE
			);
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public boolean equals(final Object other){
			if(this == other) return true;
			if(!(other instanceof KingSideCastleMove)) return false;
			KingSideCastleMove cast = (KingSideCastleMove) other;
			return super.equals(cast) && cast.castleRook == this.castleRook;
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public String toString(){
			return "O-O";
		}

	}

	/**
	 * Queen Side Castle Move
	 *
	 * <p>
	 * A {@code QueenSideCastleMove} is a castling {@code Move} to be made on the
	 * {@code Queen}s initial side of the first or eighth rank.
	 */
	public static final class QueenSideCastleMove extends CastleMove {

		/** A public constructor for a {@code QueenSideCastleMove} */
		public QueenSideCastleMove(final Board board,
								   final Piece movedPiece,
								   final int destinationCoordinate,
								   final Piece castleRook,
								   final int castleRookDestination){
			super(
					board, movedPiece, destinationCoordinate, castleRook,
					castleRookDestination, MoveType.QUEEN_SIDE_CASTLE_MOVE
			);
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public boolean equals(final Object other){
			if(this == other) return true;
			if(!(other instanceof QueenSideCastleMove)) return false;
			QueenSideCastleMove cast = (QueenSideCastleMove) other;
			return super.equals(cast) && cast.castleRook == this.castleRook;
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public String toString(){
			return "O-O-O";
		}

	}

	/**
	 * Move Factory
	 *
	 * <p>
	 * An internal factory for the {@code Move Class}.
	 */
	public static final class MoveFactory {

		/** An instance of {@code NullMove}. */
		public static final Move NULL_MOVE = new NullMove();

		private static final class NullMove extends Move {

			private NullMove(){
				super(null, null, -1, MoveType.NULL_MOVE);
			}

			@Override
			public Board execute(final boolean isAI){
				throw new UnsupportedOperationException();
			}

			@Override
			public int getCurrentCoordinate(){
				return -1;
			}

			@Override
			public int getDestinationCoordinate(){
				return -1;
			}

			@Override
			public String toString(){
				return "NULL MOVE";
			}

		}

		/** Prevents instantiation. */
		private MoveFactory() {
		}

		/**
		 * A factory method to return the first legal {@code Move} on the current {@code Board}
		 * that matches the given current coordinate and destination coordinate.
		 *
		 * @param board the current {@code Board}.
		 * @param currentCoordinate the current coordinate to look for
		 * @param destinationCoordinate the destination coordinate to look for
		 * @return a legal {@code Move}, or a {@code NullMove} if nonesuch
		 */
		public static Move produce(final Board board,
								   final int currentCoordinate,
								   final int destinationCoordinate){
			for(final Move move: board.getAllLegalMoves()){
				if(move.getCurrentCoordinate() == currentCoordinate
				  && move.getDestinationCoordinate() == destinationCoordinate){
					return move;
				}
			}
			return NULL_MOVE;
		}

		/**
		 * A factory method to showcase a {@code Move}. Showcasing a {@code Move} is a
		 * limiting strategy to protect the {@code Move} from accidental execution.
		 *
		 * @param move the move to be showcased
		 * @return a {@code ShowcaseMove}
		 */
		public static Move showcase(final Move move){
			return move.isShowcased()? move: new ShowcaseMove(move);
		}

		private static final class ShowcaseMove extends Move {

			private final Move limitedMove;
			private final Piece attackedPiece;

			private ShowcaseMove(final Move move){
				super(
						null, Piece.showcase(move.movedPiece),
						move.getDestinationCoordinate(), move.type
				);
				this.limitedMove = move;
				this.attackedPiece = move.isAttack()? Piece.showcase(move.getAttackedPiece()): null;
			}

			@Override
			public Board execute(final boolean isAI){
				throw new UnsupportedOperationException();
			}

			@Override
			public String toString(){
				return limitedMove.toString();
			}

			@Override
			public boolean equals(final Object other){
				return limitedMove.equals(other);
			}

			@Override
			public int hashCode(){
				return limitedMove.hashCode();
			}

			@Override
			public boolean isAttack(){
				return limitedMove.isAttack();
			}

			@Override
			public boolean isCastlingMove(){
				return limitedMove.isCastlingMove();
			}

			@Override
			public Piece getAttackedPiece(){
				return attackedPiece;
			}

			@Override
			public boolean isShowcased(){
				return true;
			}

		}

		/**
		 * A method to parse a {@code ShowcaseMove} from a String using a constant-time,
		 * Object-Oriented approach. This method is meant to be used in the process of loading a
		 * saved game from a *.pgn file.
		 *
		 * @param s the {@code String} to analyze.
		 * @return a {@code Move} set to the {@code String}'s specifications
		 */
		public static Move parseAndShowcase(String s){
			//Constants /////////////////////////////////////////////////////
			final int CURRENT_LOCATION = Integer.parseInt(s.substring(2, 4));
			final int DESTINATION = Integer.parseInt(s.substring(4, 6));
			final char MOVE_TYPE = s.charAt(0);
			final Alliance ALLIANCE = Alliance.parseAlliance(s.charAt(1));
			final char MOVED_PIECE = s.toUpperCase().charAt(1);
			final char ATTACKED_PIECE = s.toUpperCase().charAt(6);
			/////////////////////////////////////////////////////////////////
			return showcase(MoveType.VALUES.get(MOVE_TYPE).assembleDetached(
					Piece.showcase(PieceType.VALUES.get(MOVED_PIECE), CURRENT_LOCATION, ALLIANCE),
					ALLIANCE,
					DESTINATION,
					Piece.showcase(PieceType.VALUES.get(ATTACKED_PIECE), CURRENT_LOCATION, ALLIANCE.opposite()
			)));
		}

	}

	/**
	 * Move Type
	 *
	 * <p> An enumeration which bundles a {@code String} identification tag with the associated
	 * constructor call. The static VALUES {@code HashMap} included within this enumeration takes
	 * O(n) time to initialize (n being the number of move types). Once initialized, it can be
	 * used to parse a move from a handful of characters in constant time.
	 */
	private enum MoveType {

		ELITE_MOVE("A"){
			/** @inheritDoc */
			@Override
			public Move assembleDetached(final Piece piece,
										 final Alliance alliance,
										 final int destination,
										 final Piece attackedPiece){
				return new EliteMove(null, piece, destination);
			}
		},
		ELITE_ATTACK_MOVE("B"){
			/** @inheritDoc */
			@Override
			public Move assembleDetached(final Piece piece,
										 final Alliance alliance,
										 final int destination,
										 final Piece attackedPiece){
				return new EliteAttackMove(null, piece, destination, attackedPiece);
			}
		},
		PAWN_MOVE("C"){
			/** @inheritDoc */
			@Override
			public Move assembleDetached(final Piece piece,
										 final Alliance alliance,
										 final int destination,
										 final Piece attackedPiece){
				return new PawnMove(null, piece, destination);
			}
		},
		PAWN_ATTACK_MOVE("D"){
			/** @inheritDoc */
			@Override
			public Move assembleDetached(final Piece piece,
										 final Alliance alliance,
										 final int destination,
										 final Piece attackedPiece){
				return new PawnAttackMove(null, piece, destination, attackedPiece);
			}
		},
		PAWN_EN_PASSANT_ATTACK_MOVE("E"){
			/** @inheritDoc */
			@Override
			public Move assembleDetached(final Piece piece,
										 final Alliance alliance,
										 final int destination,
										 final Piece attackedPiece){
				return new PawnEnPassantAttackMove(null, piece, destination, attackedPiece);
			}
		},
		PAWN_PROMOTION("F") {
			/** @inheritDoc */
			@Override
			public Move assembleDetached(final Piece piece,
										 final Alliance alliance,
										 final int destination,
										 final Piece attackedPiece){
				return new PawnPromotion(new PawnMove(null, piece, destination));
			}
		},
		PAWN_JUMP("G") {
			/** @inheritDoc */
			@Override
			public Move assembleDetached(final Piece piece,
										 final Alliance alliance,
										 final int destination,
										 final Piece attackedPiece){
				return new PawnJump(null, piece, destination);
			}
		},
		KING_SIDE_CASTLE_MOVE("H"){
			/** @inheritDoc */
			@Override
			public Move assembleDetached(final Piece piece,
										 final Alliance alliance,
										 final int destination,
										 final Piece attackedPiece){
				return new KingSideCastleMove(
						null, piece, destination,
						Piece.showcase(PieceType.ROOK, alliance.isWhite()? 7: 63, alliance),
						alliance.isWhite()? 61:5);
			}
		},
		QUEEN_SIDE_CASTLE_MOVE("I"){
			/** @inheritDoc */
			@Override
			public Move assembleDetached(final Piece piece,
										 final Alliance alliance,
										 final int destination,
										 final Piece attackedPiece){
				return new QueenSideCastleMove(
						null, piece, destination,
						Piece.showcase(PieceType.ROOK, alliance.isWhite()? 0: 56, alliance),
						alliance.isWhite()? 59:3);
			}
		},
		NULL_MOVE("-"){
			/** @inheritDoc */
			@Override
			public Move assembleDetached(final Piece piece,
										 final Alliance alliance,
										 final int destination,
										 final Piece attackedPiece){
				return MoveFactory.NULL_MOVE;
			}
		};

		/**
		 * The identification tag for a {@code MoveType}.
		 */
		private final String label;

		/**
		 * A constructor for a {@code MoveType}.
		 *
		 * @param label an id tag
		 */
		MoveType(final String label){
			this.label = label;
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public String toString(){
			return label;
		}

		/**
		 * A {@code Map} of all {@code MoveType}s to their respective id tags.
		 */
		public static final Map<Character, MoveType> VALUES = initVALUES();

		// A method to initialize the VALUES Map.
		public static final Map<Character, MoveType> initVALUES(){
			Map<Character, MoveType> m = new HashMap<>();
			for(MoveType t: values()) m.put(t.label.charAt(0), t);
			return Collections.unmodifiableMap(m);
		}

		/**
		 * A method to call the constructor associated with the enumerated type.
		 *
		 * @param piece the moved {@code Piece}
		 * @param alliance the {@code Piece Alliance}
		 * @param destination the destination
		 * @param attackedPiece the attacked {@code Piece} (or null if nonesuch)
		 * @return a detached move (A move on a null board)
		 */
		public abstract Move assembleDetached(Piece piece,
											  Alliance alliance,
											  int destination,
											  final Piece attackedPiece);

	}

}
