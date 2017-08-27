package net.torocraft.chess.engine.checkers.workers;


import java.util.List;
import net.torocraft.chess.engine.checkers.CheckersMoveResult;
import net.torocraft.chess.engine.checkers.CheckersPieceState;

public class CheckerWorker extends CheckersPieceWorker {

  public CheckerWorker(List<CheckersPieceState> state, CheckersPieceState pieceToMove) {
    super(state, pieceToMove);
  }

  @Override
  public CheckersMoveResult getLegalMoves() {
    //TODO get legal moves for this piece
    return null;
  }
}
