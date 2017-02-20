package net.torocraft.chess.items;

import java.util.UUID;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.chess.IExtendedReach;
import net.torocraft.chess.ToroChess;
import net.torocraft.chess.blocks.BlockChessControl;
import net.torocraft.chess.blocks.TileEntityChessControl;
import net.torocraft.chess.engine.ChessPieceState.Position;
import net.torocraft.chess.engine.ChessPieceState.Side;
import net.torocraft.chess.enities.EntityChessPiece;
import net.torocraft.chess.gen.CheckerBoardUtil;

public class ItemChessControlWand extends Item implements IExtendedReach {

	public static final float REACH_DISTANCE = 40;

	public static final String NBT_SIDE = "chessside";
	public static final String NBT_A8_POS = "chessa8";
	public static final String NBT_GAME_ID = "chessgameid";
	public static final String NAME = "chess_control_wand";
	public static final ModelResourceLocation MODEL_BLACK = new ModelResourceLocation(ToroChess.MODID + ":" + NAME + "_black", "inventory");
	public static final ModelResourceLocation MODEL_WHITE = new ModelResourceLocation(ToroChess.MODID + ":" + NAME + "_white", "inventory");

	public static ItemChessControlWand INSTANCE;

	public static void init() {
		INSTANCE = new ItemChessControlWand();
		GameRegistry.register(INSTANCE, new ResourceLocation(ToroChess.MODID, NAME));
	}

	public static void registerRenders() {
		ModelLoader.setCustomMeshDefinition(INSTANCE, new ItemMeshDefinition() {
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack) {
				if (Side.WHITE.equals(getSide(stack))) {
					return MODEL_WHITE;
				} else {
					return MODEL_BLACK;
				}
			}
		});
		ModelLoader.registerItemVariants(INSTANCE, new ModelResourceLocation[] { MODEL_WHITE, MODEL_BLACK });
	}

	public ItemChessControlWand() {
		setUnlocalizedName(NAME);
		setMaxDamage(1);
		setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY,
			float hitZ) {

		ItemStack wand = player.getHeldItem(hand);

		if (wand == null || world.isRemote || !wand.hasTagCompound() || !(wand.getItem() instanceof ItemChessControlWand)) {
			return EnumActionResult.PASS;
		}

		BlockPos a8 = getA8(wand);
		UUID gameId = getGameId(wand);
		TileEntityChessControl control = BlockChessControl.getChessControl(world, a8, gameId);

		if (control == null) {
			System.out.println("onItemUse: No control block found wand " + wand.getTagCompound());
			return EnumActionResult.PASS;
		}

		Position to = CheckerBoardUtil.getChessPosition(a8, pos);
		control.movePiece(a8, to);

		return EnumActionResult.SUCCESS;
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack s, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
		if (player.world.isRemote || !(target instanceof EntityChessPiece)) {
			return false;
		}

		ItemStack wand = player.getHeldItem(hand);

		if (wand == null || !(wand.getItem() instanceof ItemChessControlWand)) {
			return false;
		}

		EntityChessPiece piece = (EntityChessPiece) target;
		BlockPos a8 = getA8(wand);

		TileEntityChessControl control = BlockChessControl.getChessControl(player.world, a8, getGameId(wand));
		if (control == null) {
			System.out.println("itemInteractionForEntity: No control block found wand " + wand.getTagCompound());
			return false;
		}

		if (canAttack(wand, piece)) {
			control.movePiece(a8, piece.getChessPosition());
			return true;
		}

		if (canSelect(wand, piece)) {
			control.selectEntity(piece);
			return true;
		}

		return false;
	}

	private boolean canSelect(ItemStack wand, EntityChessPiece piece) {
		if (piece == null) {
			return false;
		}
		return getSide(wand).equals(piece.getSide()) && getGameId(wand).equals(piece.getGameId());
	}

	private boolean canAttack(ItemStack wand, EntityChessPiece target) {
		if (target == null) {
			return false;
		}
		return !getSide(wand).equals(target.getSide()) && getGameId(wand).equals(target.getGameId());
	}

	public static BlockPos getA8(ItemStack stack) {
		return BlockPos.fromLong(stack.getTagCompound().getLong(ItemChessControlWand.NBT_A8_POS));
	}

	public static UUID getGameId(ItemStack stack) {
		return stack.getTagCompound().getUniqueId(ItemChessControlWand.NBT_GAME_ID);
	}

	public static Side getSide(ItemStack stack) {
		return CheckerBoardUtil.castSide(stack.getTagCompound().getBoolean(NBT_SIDE));
	}

	@Override
	public float getReach() {
		return REACH_DISTANCE;
	}

}
