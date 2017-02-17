package net.torocraft.chess.engine;

public class ChessPieceState {
	public enum Type {
		PAWN, BISHOP, KING, KNIGHT, QUEEN, ROOK
	}

	public enum Side {
		WHITE, BLACK
	}

	public enum File {
		A, B, C, D, E, F, G, H
	}

	public enum Rank {
		ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT
	}

	public Type type;
	public Position position;
	public Side side;
	public boolean isInitialMove;

	public static class Position {
		public File letter;
		public Rank number;

		public Position(File letter, Rank number) {
			this.letter = letter;
			this.number = number;
		}

		@Override
		public String toString() {
			return letter.toString().toLowerCase() + number.ordinal();
		}
	}

	public static class Move {
		public Position currentPosition;
		public Position requestedMoveToPosition;

		public Move(Position currentPosition, Position requestedMoveToPosition) {
			this.currentPosition = currentPosition;
			this.requestedMoveToPosition = requestedMoveToPosition;
		}
	}
}