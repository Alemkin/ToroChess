package net.torocraft.chess.gen;

import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.torocraft.chess.enities.EntityChessPiece;
import net.torocraft.chess.enities.IChessPiece.Side;
import net.torocraft.chess.enities.bishop.EntityBishop;
import net.torocraft.chess.enities.king.EntityKing;
import net.torocraft.chess.enities.knight.EntityKnight;
import net.torocraft.chess.enities.pawn.EntityPawn;
import net.torocraft.chess.enities.queen.EntityQueen;
import net.torocraft.chess.enities.rook.EntityRook;
import net.torocraft.chess.items.ItemChessControlWand;

public class ChessGameGenerator {

	private CheckerBoardGenerator board;
	private final World world;
	private final BlockPos a1Pos;
	private UUID gameId = UUID.randomUUID();

	public ChessGameGenerator(World world, BlockPos a1Pos) {
		if(world == null){
			throw new NullPointerException("null world");
		}
		if(a1Pos == null){
			throw new NullPointerException("null a1Pos");
		}
		this.board = new CheckerBoardGenerator(world, a1Pos);
		this.world = world;
		this.a1Pos = a1Pos;
	}

	public CheckerBoardGenerator getBoard() {
		if(board == null){
			board = new CheckerBoardGenerator(world, a1Pos);
		}
		return board;
	}

	public void generate() {
		if(world.isRemote){
			return;
		}
		getBoard().generate();
		addWand();
		placePieces();
	}

	public void placePieces() {
		placeEntity(new EntityPawn(world), Side.WHITE, "a2");
		placeEntity(new EntityPawn(world), Side.WHITE, "b2");
		placeEntity(new EntityPawn(world), Side.WHITE, "c2");
		placeEntity(new EntityPawn(world), Side.WHITE, "d2");
		placeEntity(new EntityPawn(world), Side.WHITE, "e2");
		placeEntity(new EntityPawn(world), Side.WHITE, "f2");
		placeEntity(new EntityPawn(world), Side.WHITE, "g2");
		placeEntity(new EntityPawn(world), Side.WHITE, "h2");

		placeEntity(new EntityRook(world), Side.WHITE, "a1");
		placeEntity(new EntityKnight(world), Side.WHITE, "b1");
		placeEntity(new EntityBishop(world), Side.WHITE, "c1");
		placeEntity(new EntityKing(world), Side.WHITE, "d1");
		placeEntity(new EntityQueen(world), Side.WHITE, "e1");
		placeEntity(new EntityBishop(world), Side.WHITE, "f1");
		placeEntity(new EntityKnight(world), Side.WHITE, "g1");
		placeEntity(new EntityRook(world), Side.WHITE, "h1");

		placeEntity(new EntityPawn(world), Side.BLACK, "a7");
		placeEntity(new EntityPawn(world), Side.BLACK, "b7");
		placeEntity(new EntityPawn(world), Side.BLACK, "c7");
		placeEntity(new EntityPawn(world), Side.BLACK, "d7");
		placeEntity(new EntityPawn(world), Side.BLACK, "e7");
		placeEntity(new EntityPawn(world), Side.BLACK, "f7");
		placeEntity(new EntityPawn(world), Side.BLACK, "g7");
		placeEntity(new EntityPawn(world), Side.BLACK, "h7");

		placeEntity(new EntityRook(world), Side.BLACK, "a8");
		placeEntity(new EntityKnight(world), Side.BLACK, "b8");
		placeEntity(new EntityBishop(world), Side.BLACK, "c8");
		placeEntity(new EntityKing(world), Side.BLACK, "d8");
		placeEntity(new EntityQueen(world), Side.BLACK, "e8");
		placeEntity(new EntityBishop(world), Side.BLACK, "f8");
		placeEntity(new EntityKnight(world), Side.BLACK, "g8");
		placeEntity(new EntityRook(world), Side.BLACK, "h8");
	}

	private void addWand() {
		//ItemChessControlWand wand = new ItemChessControlWand();
		//wand.setChessControlBlockPosition(a1Pos);
		ItemStack stack = new ItemStack(ItemChessControlWand.INSTANCE, 1);
		getBoard().getWhiteChest().setInventorySlotContents(0, stack);
	}

	private void placeEntity(EntityChessPiece e, Side side, String position) {
		int x = a1Pos.getX() + world.rand.nextInt(8);
		int z = a1Pos.getZ() + world.rand.nextInt(8);
		e.setChessPosition(position);
		e.setPosition(x, a1Pos.getY() + 1, z);
		e.setSide(side);
		e.setGameId(gameId);
		e.setA1Pos(a1Pos);
		world.spawnEntity(e);
	}

}
