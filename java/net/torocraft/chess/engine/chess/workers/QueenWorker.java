package net.torocraft.chess.engine.chess.workers;

import net.torocraft.chess.engine.chess.ChessPieceState;
import net.torocraft.chess.engine.chess.ChessMoveResult;

import java.util.List;

public class QueenWorker extends ChessPieceWorker {
    public QueenWorker(List<ChessPieceState> state, ChessPieceState chessPieceToMove) {
        super(state, chessPieceToMove);
    }

    @Override
    public ChessMoveResult getLegalMoves() {
        //TODO get legal moves for this piece
        return null;
    }
}