package net.torocraft.chess.engine.chess.impl;

import net.torocraft.chess.engine.chess.ChessPieceState;
import net.torocraft.chess.engine.chess.IChessRuleEngine;
import net.torocraft.chess.engine.chess.ChessMoveResult;
import net.torocraft.chess.engine.chess.workers.*;

import java.util.ArrayList;
import java.util.List;

import static net.torocraft.chess.engine.chess.ChessPieceState.Side;
import static net.torocraft.chess.engine.chess.ChessPieceState.Type;
import static net.torocraft.chess.engine.GamePieceState.Position;
import static net.torocraft.chess.engine.chess.ChessMoveResult.Condition;

//TODO add support for castling
//TODO add support for en passant
//TODO add support for pawn promotion
public class ChessRuleEngine implements IChessRuleEngine {
	private ChessMoveResult moveResult;
	private List<ChessPieceState> internalState;
	private ChessPieceState internalChessPieceToMove;
	private ChessPieceState currentKingState;
	private boolean isKingInCheck = false;

	@Override
	public Game getGameType() {
		return Game.CHESS;
	}

	@Override
	public ChessMoveResult getMoves(List<ChessPieceState> state, ChessPieceState chessPieceToMove) {
		internalState = state;
		internalChessPieceToMove = chessPieceToMove;

		currentKingState = getCurrentKingState();
		isKingInCheck = isKingInCheck();
		if (isKingInCheckMate() || isKingInStalemate()) {
			return moveResult;
		}

		IChessPieceWorker chessPieceWorker = getChessPieceWorker(internalState, internalChessPieceToMove);
		if (chessPieceWorker == null) {
			return new ChessMoveResult();
		}
		moveResult = chessPieceWorker.getLegalMoves();

		updateBoardCondition();
		updateMoveResult();

		System.out.println("\nCURRENT BOARD STATE:" +
				"\nBlack: " + moveResult.blackCondition.toString() +
				"\n White: " + moveResult.whiteCondition.toString());
		return moveResult;
	}

	private ChessPieceState getCurrentKingState() {
		for (ChessPieceState currentChessPieceState : internalState) {
			if (currentChessPieceState.side.equals(internalChessPieceToMove.side)
					&& currentChessPieceState.type.equals(Type.KING)) {
				return currentChessPieceState;
			}
		}
		return null;
	}

	private ChessPieceWorker getChessPieceWorker(List<ChessPieceState> chessPieceStateTo, ChessPieceState chessPieceToCheck) {
		if (chessPieceToCheck == null) {
			return null;
		}
		switch (chessPieceToCheck.type) {
			case BISHOP:
				return new BishopWorker(chessPieceStateTo, chessPieceToCheck);
			case KING:
				return new KingWorker(chessPieceStateTo, chessPieceToCheck);
			case KNIGHT:
				return new KnightWorker(chessPieceStateTo, chessPieceToCheck);
			case PAWN:
				return new PawnWorker(chessPieceStateTo, chessPieceToCheck);
			case QUEEN:
				return new QueenWorker(chessPieceStateTo, chessPieceToCheck);
			case ROOK:
				return new RookWorker(chessPieceStateTo, chessPieceToCheck);
			default:
				return null;
		}
	}

	private void updateBoardCondition() {
		if (isKingInCheck) {
			if (internalChessPieceToMove.side.equals(ChessPieceState.Side.BLACK)) {
				moveResult.blackCondition = ChessMoveResult.Condition.CHECK;
				moveResult.whiteCondition = ChessMoveResult.Condition.CLEAR;
			} else {
				moveResult.blackCondition = ChessMoveResult.Condition.CLEAR;
				moveResult.whiteCondition = ChessMoveResult.Condition.CHECK;
			}
		} else {
			moveResult.blackCondition = ChessMoveResult.Condition.CLEAR;
			moveResult.whiteCondition = ChessMoveResult.Condition.CLEAR;
		}
	}

	private void updateMoveResult() {
	    List<Position> positionListOverride = new ArrayList<>();
	    for (Position position : moveResult.legalPositions) {
            if (!willPutKingInCheck(position)) {
                positionListOverride.add(new Position(position));
            }
        }
        moveResult.legalPositions = positionListOverride;
    }

	private boolean isKingInCheckMate() {
	    //TODO fix checkmate logic
		if (isKingInCheck && !areAnyLegalMovesForCurrentSide()) {
			if (internalChessPieceToMove.side.equals(Side.BLACK)) {
				moveResult = new ChessMoveResult();
				moveResult.blackCondition = ChessMoveResult.Condition.CHECKMATE;
				moveResult.whiteCondition = ChessMoveResult.Condition.CLEAR;
				moveResult.legalPositions = new ArrayList<>();
			} else {
				moveResult = new ChessMoveResult();
				moveResult.blackCondition = ChessMoveResult.Condition.CLEAR;
				moveResult.whiteCondition = ChessMoveResult.Condition.CHECKMATE;
				moveResult.legalPositions = new ArrayList<>();
			}
			return true;
		}
		return false;
	}

	private boolean isKingInStalemate() {
	    //TODO fix stalemate logic
		if (!isKingInCheck && !areAnyLegalMovesForCurrentSide()) {
			moveResult = new ChessMoveResult();
			moveResult.blackCondition = Condition.STALEMATE;
			moveResult.whiteCondition = Condition.STALEMATE;
			moveResult.legalPositions = new ArrayList<>();
			return true;
		}
		return false;
	}

	private boolean areAnyLegalMovesForCurrentSide() {
		for (ChessPieceState chessPieceState : internalState) {
			if (chessPieceState.side.equals(internalChessPieceToMove.side)) {
				ChessMoveResult moveResult = getChessPieceWorker(internalState, chessPieceState).getLegalMoves();
				if (moveResult.legalPositions.size() > 1) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isKingInCheck() {
		for (ChessPieceState chessPieceState : internalState) {
			if (!chessPieceState.side.equals(internalChessPieceToMove.side)) {
                ChessMoveResult moveResult = getChessPieceWorker(internalState, chessPieceState).getLegalMoves();
				for (Position position : moveResult.legalPositions) {
					if (position.rank.equals(currentKingState.position.rank)
							&& position.file.equals(currentKingState.position.file)) {
						return true;
					}
				}
			}
		}
		return false;
	}

    public boolean willPutKingInCheck(Position positionToMoveCurrentPieceTo) {
        List<ChessPieceState> stateClone = cloneState();
        ChessPieceState spoofedChessPieceState = new ChessPieceState(internalChessPieceToMove);
        spoofedChessPieceState.position = positionToMoveCurrentPieceTo;
        stateClone.add(spoofedChessPieceState);
        return isKingInCheckCloned(stateClone, spoofedChessPieceState);
    }

    private List<ChessPieceState> cloneState() {
        List<ChessPieceState> stateClone = new ArrayList<>();
        for (ChessPieceState pieceToClone : internalState) {
            if (pieceToClone.type.equals(internalChessPieceToMove.type)
                    && pieceToClone.position.rank.equals(internalChessPieceToMove.position.rank)
                    && pieceToClone.position.file.equals(internalChessPieceToMove.position.file)) {
                continue;
            }
            stateClone.add(new ChessPieceState(pieceToClone));
        }
        return stateClone;
    }

    private boolean isKingInCheckCloned(List<ChessPieceState> stateClone, ChessPieceState chessPieceMoving) {
        if (stateClone == null || stateClone.size() < 1) {
            return false;
        }
        ChessPieceState currentKingState = getCurrentKingStateCloned(stateClone);
        if (currentKingState == null) {
            return false;
        }
        for (ChessPieceState chessPieceState : stateClone) {
            if (!chessPieceState.side.equals(internalChessPieceToMove.side)
                    && !(chessPieceState.position.rank.equals(chessPieceMoving.position.rank)
                        && chessPieceState.position.file.equals(chessPieceMoving.position.file))
                ) {
                ChessMoveResult moveResult = getChessPieceWorker(stateClone, chessPieceState).getLegalMoves();
                for (Position position : moveResult.legalPositions) {
                    if (position.rank.equals(currentKingState.position.rank)
                            && position.file.equals(currentKingState.position.file)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private ChessPieceState getCurrentKingStateCloned(List<ChessPieceState> stateClone) {
        if (stateClone == null) {
            return null;
        }
        for (ChessPieceState currentChessPieceState : stateClone) {
            if (currentChessPieceState.side.equals(internalChessPieceToMove.side)
                    && currentChessPieceState.type.equals(Type.KING)) {
                return currentChessPieceState;
            }
        }
        return null;
    }

}
