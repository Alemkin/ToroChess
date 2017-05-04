package net.torocraft.chess.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.torocraft.chess.engine.GamePieceState.File;
import net.torocraft.chess.engine.GamePieceState.Position;
import net.torocraft.chess.engine.GamePieceState.Rank;
import net.torocraft.chess.engine.GamePieceState.Side;
import net.torocraft.chess.entities.EntityChessPiece;
import net.torocraft.chess.entities.bishop.EntityBishop;
import net.torocraft.chess.entities.king.EntityKing;
import net.torocraft.chess.entities.knight.EntityKnight;
import net.torocraft.chess.entities.pawn.EntityPawn;
import net.torocraft.chess.entities.queen.EntityQueen;
import net.torocraft.chess.entities.rook.EntityRook;
import net.torocraft.chess.items.ItemChessControlWand;
import net.torocraft.chess.items.util.BookCreator;

public class ChessGameGenerator {

	private final CheckerBoardGenerator board;
	private final World world;
	private final BlockPos a8;
	private final BlockPos controlPos;
	private final UUID gameId;
	private IBlockState whiteBlock = Blocks.QUARTZ_BLOCK.getDefaultState();
	private IBlockState blackBlock = Blocks.OBSIDIAN.getDefaultState();

	public ChessGameGenerator(World world, BlockPos a8, BlockPos controlPos, UUID gameId, IBlockState whiteBlock, IBlockState blackBlock) {
		if (world == null) {
			throw new NullPointerException("null world");
		}
		if (a8 == null) {
			throw new NullPointerException("null a8");
		}
		this.board = new CheckerBoardGenerator(world, a8);
		this.world = world;
		this.a8 = a8;
		this.controlPos = controlPos;
		this.gameId = gameId;

		if (whiteBlock != null) {
			this.whiteBlock = whiteBlock;
		}

		if (blackBlock != null) {
			this.blackBlock = blackBlock;
		}
	}

	public void generate() {
		if (world.isRemote) {
			return;
		}
		board.setWhiteBlock(whiteBlock);
		board.setBlackBlock(blackBlock);
		board.generate();
		addWand();
		placePieces(world, a8, gameId);
		placeBanners(world, a8);
		placeInstructions(world, a8);
		// saveGameData();
	}

	private void placeInstructions(World world2, BlockPos a82) {
		board.getBlackChest().setInventorySlotContents(6, BookCreator.createBook("chess_instructions"));
		board.getWhiteChest().setInventorySlotContents(6, BookCreator.createBook("chess_instructions"));
	}

	public static void placePieces(World world, BlockPos a8, UUID gameId) {
		List<EntityChessPiece> pieces = genPieces(world, a8, gameId);
		for (EntityChessPiece piece : pieces) {
			world.spawnEntity(piece);
		}
	}

	public static List<EntityChessPiece> genPieces(World world, BlockPos a8, UUID gameId) {
		List<EntityChessPiece> pieces = new ArrayList<>();
		pieces.add(genEntity(world, a8, gameId, new EntityPawn(world), Side.WHITE, File.A, Rank.TWO));
		pieces.add(genEntity(world, a8, gameId, new EntityPawn(world), Side.WHITE, File.B, Rank.TWO));
		pieces.add(genEntity(world, a8, gameId, new EntityPawn(world), Side.WHITE, File.C, Rank.TWO));
		pieces.add(genEntity(world, a8, gameId, new EntityPawn(world), Side.WHITE, File.D, Rank.TWO));
		pieces.add(genEntity(world, a8, gameId, new EntityPawn(world), Side.WHITE, File.E, Rank.TWO));
		pieces.add(genEntity(world, a8, gameId, new EntityPawn(world), Side.WHITE, File.F, Rank.TWO));
		pieces.add(genEntity(world, a8, gameId, new EntityPawn(world), Side.WHITE, File.G, Rank.TWO));
		pieces.add(genEntity(world, a8, gameId, new EntityPawn(world), Side.WHITE, File.H, Rank.TWO));

		pieces.add(genEntity(world, a8, gameId, new EntityRook(world), Side.WHITE, File.A, Rank.ONE));
		pieces.add(genEntity(world, a8, gameId, new EntityKnight(world), Side.WHITE, File.B, Rank.ONE));
		pieces.add(genEntity(world, a8, gameId, new EntityBishop(world), Side.WHITE, File.C, Rank.ONE));
		pieces.add(genEntity(world, a8, gameId, new EntityKing(world), Side.WHITE, File.E, Rank.ONE));
		pieces.add(genEntity(world, a8, gameId, new EntityQueen(world), Side.WHITE, File.D, Rank.ONE));
		pieces.add(genEntity(world, a8, gameId, new EntityBishop(world), Side.WHITE, File.F, Rank.ONE));
		pieces.add(genEntity(world, a8, gameId, new EntityKnight(world), Side.WHITE, File.G, Rank.ONE));
		pieces.add(genEntity(world, a8, gameId, new EntityRook(world), Side.WHITE, File.H, Rank.ONE));

		pieces.add(genEntity(world, a8, gameId, new EntityPawn(world), Side.BLACK, File.A, Rank.SEVEN));
		pieces.add(genEntity(world, a8, gameId, new EntityPawn(world), Side.BLACK, File.B, Rank.SEVEN));
		pieces.add(genEntity(world, a8, gameId, new EntityPawn(world), Side.BLACK, File.C, Rank.SEVEN));
		pieces.add(genEntity(world, a8, gameId, new EntityPawn(world), Side.BLACK, File.D, Rank.SEVEN));
		pieces.add(genEntity(world, a8, gameId, new EntityPawn(world), Side.BLACK, File.E, Rank.SEVEN));
		pieces.add(genEntity(world, a8, gameId, new EntityPawn(world), Side.BLACK, File.F, Rank.SEVEN));
		pieces.add(genEntity(world, a8, gameId, new EntityPawn(world), Side.BLACK, File.G, Rank.SEVEN));
		pieces.add(genEntity(world, a8, gameId, new EntityPawn(world), Side.BLACK, File.H, Rank.SEVEN));

		pieces.add(genEntity(world, a8, gameId, new EntityRook(world), Side.BLACK, File.A, Rank.EIGHT));
		pieces.add(genEntity(world, a8, gameId, new EntityKnight(world), Side.BLACK, File.B, Rank.EIGHT));
		pieces.add(genEntity(world, a8, gameId, new EntityBishop(world), Side.BLACK, File.C, Rank.EIGHT));
		pieces.add(genEntity(world, a8, gameId, new EntityKing(world), Side.BLACK, File.E, Rank.EIGHT));
		pieces.add(genEntity(world, a8, gameId, new EntityQueen(world), Side.BLACK, File.D, Rank.EIGHT));
		pieces.add(genEntity(world, a8, gameId, new EntityBishop(world), Side.BLACK, File.F, Rank.EIGHT));
		pieces.add(genEntity(world, a8, gameId, new EntityKnight(world), Side.BLACK, File.G, Rank.EIGHT));
		pieces.add(genEntity(world, a8, gameId, new EntityRook(world), Side.BLACK, File.H, Rank.EIGHT));

		return pieces;
	}

	private void addWand() {
		for (int i = 0; i < 4; i++) {
			board.getWhiteChest().setInventorySlotContents(i, createWand(Side.WHITE));
			board.getBlackChest().setInventorySlotContents(i, createWand(Side.BLACK));
		}
	}

	private ItemStack createWand(Side side) {
		ItemStack wand = new ItemStack(ItemChessControlWand.INSTANCE, 1);
		NBTTagCompound c = new NBTTagCompound();
		c.setLong(ItemChessControlWand.NBT_A8_POS, a8.toLong());
		c.setBoolean(ItemChessControlWand.NBT_SIDE, castSide(side));
		c.setUniqueId(ItemChessControlWand.NBT_GAME_ID, gameId);
		c.setLong(ItemChessControlWand.NBT_CONTROL_POS, controlPos.toLong());
		wand.setTagCompound(c);
		return wand;
	}

	public static EntityChessPiece genEntity(World world, BlockPos a8, UUID gameId, EntityChessPiece e, Side side, File file, Rank rank) {
		// TODO this method should take in ChessPieceState.Type instead of
		// EntityChessPiece
		int x = a8.getX() + world.rand.nextInt(8);
		int z = a8.getZ() + world.rand.nextInt(8);
		e.setPosition(x, a8.getY() + 1, z);
		setGameDataToEntity(world, a8, gameId, e, side, file, rank);
		return e;
	}

	public static void setGameDataToEntity(World world, BlockPos a8, UUID gameId, EntityChessPiece e, Side side, File file, Rank rank) {
		e.setChessPosition(new Position(file, rank));
		e.setSide(side);
		e.setGameId(gameId);
		e.setA8(a8);
		e.setInitialMove(true);
	}

	private Boolean castSide(Side side) {
		if (Side.BLACK.equals(side)) {
			return true;
		} else {
			return false;
		}
	}

	private void placeBanners(World world, BlockPos a8) {
		Side side = Side.BLACK;
		placeBanner(world, new BlockPos(a8.getX() + 8, a8.getY() + 1, a8.getZ() + 8), side);
		placeBanner(world, new BlockPos(a8.getX() - 1, a8.getY() + 1, a8.getZ() + 8), side);
		side = Side.WHITE;
		placeBanner(world, new BlockPos(a8.getX() + 8, a8.getY() + 1, a8.getZ() - 1), side);
		placeBanner(world, new BlockPos(a8.getX() - 1, a8.getY() + 1, a8.getZ() - 1), side);
	}

	private void placeBanner(World worldIn, BlockPos pos, Side side) {
		int rotation = Side.BLACK.equals(side) ? 0 : 8;
		IBlockState banner = Blocks.STANDING_BANNER.getDefaultState().withProperty(BlockStandingSign.ROTATION, Integer.valueOf(rotation));
		worldIn.setBlockState(pos, banner, 3);
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if (tileentity instanceof TileEntityBanner) {
			((TileEntityBanner) tileentity).setItemValues(createBannerItem(side), true);
		}
	}

	private ItemStack createBannerItem(Side side) {
		ItemStack itemstack = new ItemStack(Items.BANNER);

		int color = Side.BLACK.equals(side) ? 0 : 15;

		NBTTagCompound blockEntityTag = new NBTTagCompound();
		blockEntityTag.setInteger("Base", color);

		NBTTagCompound c = new NBTTagCompound();
		c.setTag("BlockEntityTag", blockEntityTag);
		itemstack.setTagCompound(c);
		return itemstack;
	}

}
