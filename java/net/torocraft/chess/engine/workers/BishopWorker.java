package net.torocraft.chess.engine.workers;

import net.torocraft.chess.engine.ChessPieceState;
import net.torocraft.chess.engine.MoveResult;

import java.util.List;

public class BishopWorker extends ChessPieceWorker {
    public BishopWorker(List<ChessPieceState> state, ChessPieceState chessPieceToMove) {
        super(state, chessPieceToMove);
    }

    @Override
    public MoveResult getLegalMoves() {
        //TODO get legal moves for this piece
        return null;
    }
}
