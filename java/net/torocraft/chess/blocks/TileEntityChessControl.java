package net.torocraft.chess.blocks;

import java.util.List;
import java.util.UUID;

import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.chess.CheckerBoardOverlay;
import net.torocraft.chess.engine.ChessPieceState.File;
import net.torocraft.chess.engine.ChessPieceState.Position;
import net.torocraft.chess.engine.ChessPieceState.Rank;
import net.torocraft.chess.engine.ChessPieceState.Side;
import net.torocraft.chess.engine.IChessRuleEngine;
import net.torocraft.chess.engine.MoveResult;
import net.torocraft.chess.engine.impl.TestingHighlightPiecesRuleEngine;
import net.torocraft.chess.enities.EntityChessPiece;
import net.torocraft.chess.gen.CheckerBoardUtil;
import net.torocraft.chess.items.HighlightedChessPiecePredicate;

public class TileEntityChessControl extends TileEntity {

	private static final String NBT_SELECTED_RANK = "chessposrank";
	private static final String NBT_SELECTED_FILE = "chessposfile";
	private static final String NBT_GAME_ID = "chessgameid";
	private static final String NBT_TURN = "chessturn";

	private UUID gameId;
	private Position selectedPiece;
	private Side turn = Side.WHITE;
	private IChessRuleEngine ruleEngine;

	public static void init() {
		GameRegistry.registerTileEntity(TileEntityChessControl.class, "chess_control_tile_entity");
	}

	public IChessRuleEngine getRuleEngine() {
		if (ruleEngine == null) {
			ruleEngine = new TestingHighlightPiecesRuleEngine();
		}
		return ruleEngine;
	}

	public void movePiece(BlockPos a8, Position to) {

		if (selectedPiece == null) {
			System.out.println("No piece has been selected");
			return;
		}

		Position from = selectedPiece;
		
		EntityChessPiece attacker = CheckerBoardUtil.getPiece(world, from, a8, gameId);

		if (attacker == null) {
			return;
		}

		if (isNotYourTurn(attacker)) {
			System.out.println("It's not " + attacker.getSide().toString().toLowerCase() + "'s turn!");
			return;
		}

		System.out.println("Request Move:  " + from + " -> " + to);

		if (CheckerBoardUtil.isInvalidMove(gameId, a8, from, to)) {
			System.out.println("INVALID MOVE");
			return;
		}

		EntityChessPiece victum = CheckerBoardUtil.getPiece(world, to, a8, gameId);

		if (isSameSide(attacker, victum)) {
			return;
		}

		deselectEntity();
		switchTurns();

		attacker.setAttackTarget(victum);
		attacker.setChessPosition(to);
	}

	private boolean isNotYourTurn(EntityChessPiece attacker) {
		return !attacker.getSide().equals(turn);
	}

	private void switchTurns() {
		if(Side.WHITE.equals(turn)) {
			turn = Side.BLACK;
		}else{
			turn = Side.WHITE;
		}
		System.out.println(turn.toString().toLowerCase() + "'s trun");
	}

	private boolean isSameSide(EntityChessPiece target, EntityChessPiece victum) {
		return victum != null && victum.getSide().equals(target.getSide());
	}

	// TODO sync highlights on startup
	public void selectEntity(EntityChessPiece target) {
		if (target == null) {
			throw new NullPointerException("target is null");
		}
		
		if(target.getChessPosition().equals(selectedPiece)){
			deselectEntity();
			return;
		}
		
		if (isNotYourTurn(target)) {
			System.out.println("It's not " + target.getSide().toString().toLowerCase() + "'s turn!");
			return;
		}
		
		selectedPiece = target.getChessPosition();
		setHightlight(target);
		updateValidMoves(target);
		markDirty();
	}

	public void deselectEntity() {
		selectedPiece = null;
		removeAllHighlights();
		markDirty();
	}

	private void setHightlight(EntityChessPiece target) {
		removeAllHighlights();
		target.addPotionEffect(new PotionEffect(MobEffects.GLOWING, 1000, 0, false, false));
	}

	private void removeAllHighlights() {
		List<EntityChessPiece> pieces = world.getEntitiesWithinAABB(EntityChessPiece.class, new AxisAlignedBB(pos).expand(80, 20, 80),
				new HighlightedChessPiecePredicate(gameId));
		if (pieces == null) {
			return;
		}
		for (EntityChessPiece piece : pieces) {
			piece.removeActivePotionEffect(MobEffects.GLOWING);
		}
	}

	/*
	 * TODO this needs to happen on the client, via a packet?
	 */
	private void updateValidMoves(EntityChessPiece piece) {
		MoveResult moves = getRuleEngine().getMoves(CheckerBoardUtil.loadPiecesFromWorld(piece), CheckerBoardUtil.convertToState(piece));
		CheckerBoardOverlay.INSTANCE.setValidMoves(moves.legalPositions);
	}

	@Override
	public void readFromNBT(NBTTagCompound c) {
		super.readFromNBT(c);
		if (c.hasKey(NBT_SELECTED_FILE) && c.hasKey(NBT_SELECTED_RANK)) {
			selectedPiece = new Position(File.values()[c.getInteger(NBT_SELECTED_FILE)], Rank.values()[c.getInteger(NBT_SELECTED_RANK)]);
		} else {
			selectedPiece = null;
		}
		gameId = c.getUniqueId(NBT_GAME_ID);
		turn = CheckerBoardUtil.castSide(c.getBoolean(NBT_TURN));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound cIn) {
		NBTTagCompound c = super.writeToNBT(cIn);
		if (selectedPiece == null) {
			c.removeTag(NBT_SELECTED_FILE);
			c.removeTag(NBT_SELECTED_RANK);
		} else {
			c.setInteger(NBT_SELECTED_FILE, selectedPiece.file.ordinal());
			c.setInteger(NBT_SELECTED_RANK, selectedPiece.rank.ordinal());
		}
		c.setUniqueId(NBT_GAME_ID, gameId);
		c.setBoolean(NBT_TURN, CheckerBoardUtil.castSide(turn));
		markDirty();
		return c;
	}

	public UUID getGameId() {
		return gameId;
	}

	public Position getSelectedPiece() {
		return selectedPiece;
	}

	public Side getTurn() {
		return turn;
	}

	public void setGameId(UUID gameId) {
		this.gameId = gameId;
	}

	public void setSelectedPiece(Position selectedPiece) {
		this.selectedPiece = selectedPiece;
	}

	public void setTurn(Side turn) {
		this.turn = turn;
	}

	public void setRuleEngine(IChessRuleEngine ruleEngine) {
		this.ruleEngine = ruleEngine;
	}

}
