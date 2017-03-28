package net.torocraft.chess.engine.checkers.workers;


import net.torocraft.chess.engine.checkers.CheckersMoveResult;
import net.torocraft.chess.engine.checkers.CheckersPieceState;

import java.util.List;

public class KingWorker extends CheckersPieceWorker {
    public KingWorker(List<CheckersPieceState> state, CheckersPieceState pieceToMove) {
        super(state, pieceToMove);
    }

    @Override
    public CheckersMoveResult getLegalMoves() {
        //TODO get legal moves for this piece
        return null;
    }
}