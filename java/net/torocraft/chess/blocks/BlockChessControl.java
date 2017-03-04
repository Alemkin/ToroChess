package net.torocraft.chess.blocks;

import java.util.UUID;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.chess.ToroChess;
import net.torocraft.chess.ToroChessGuiHandler;
import net.torocraft.chess.control.TileEntityChessControl;
import net.torocraft.chess.gen.ChessGameGenerator;

public class BlockChessControl extends BlockContainer {

	public static final String NBT_TYPE = "chesstype";
	public static final String NAME = "chess_control";
	public static final BlockPos A8_OFFSET = new BlockPos(-4, 1, -4);

	public static BlockChessControl INSTANCE;
	public static Item ITEM_INSTANCE;

	public static void init() {
		INSTANCE = new BlockChessControl();
		ResourceLocation resourceName = new ResourceLocation(ToroChess.MODID, NAME);
		INSTANCE.setRegistryName(resourceName);
		GameRegistry.register(INSTANCE);

		ITEM_INSTANCE = new ItemBlock(INSTANCE);
		ITEM_INSTANCE.setRegistryName(resourceName);
		GameRegistry.register(ITEM_INSTANCE);

		GameRegistry.addRecipe(new ChessControlRecipe());
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	public static void registerRenders() {
		ModelResourceLocation model = new ModelResourceLocation(ToroChess.MODID + ":" + NAME, "inventory");
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(ITEM_INSTANCE, 0, model);
	}

	public BlockChessControl() {
		super(Material.GROUND);
		setUnlocalizedName(NAME);
		setResistance(0.1f);
		setHardness(0.5f);
		setLightLevel(0);
		setCreativeTab(CreativeTabs.MISC);
		isBlockContainer = true;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (placer != null) {
			placer.move(MoverType.SELF, 0, 2, 0);
		}
		if (!world.isRemote) {
			BlockPos a8 = pos.add(A8_OFFSET);

			NBTTagCompound c = stack.getTagCompound();

			IBlockState whiteBlock = null;
			IBlockState blackBlock = null;

			if (c != null) {
				NonNullList<ItemStack> list = NonNullList.<ItemStack> withSize(2, ItemStack.EMPTY);
				ItemStackHelper.loadAllItems(c, list);
				whiteBlock = getBlockState(world, pos, placer, list.get(0));
				blackBlock = getBlockState(world, pos, placer, list.get(1));
			}

			new ChessGameGenerator(world, a8, whiteBlock, blackBlock).generate();
			((TileEntityChessControl) world.getTileEntity(pos)).setA8(a8);
		}
	}

	private IBlockState getBlockState(World world, BlockPos pos, EntityLivingBase placer, ItemStack stack) {
		if (stack == null || !(stack.getItem() instanceof ItemBlock)) {
			return null;
		}

		return ((ItemBlock) stack.getItem()).getBlock().getStateForPlacement(world, pos, EnumFacing.DOWN, 0f, 0f, 0f, stack.getMetadata(), placer,
				EnumHand.MAIN_HAND);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityChessControl();
	}

	public static TileEntityChessControl getChessControl(World world, BlockPos a8, UUID gameId) {
		if (gameId == null) {
			return null;
		}
		TileEntityChessControl e = getChessControl(world, a8);
		if (e == null || !e.getGameId().equals(gameId)) {
			return null;
		}
		return e;
	}

	public static TileEntityChessControl getChessControl(World world, BlockPos a8) {
		if (world == null || a8 == null) {
			return null;
		}
		TileEntityChessControl e = (TileEntityChessControl) world.getTileEntity(a8.subtract(A8_OFFSET));
		if (e == null) {
			return null;
		}
		return e;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntityChessControl control = (TileEntityChessControl) world.getTileEntity(pos);
		control.clearBoard();
		super.breakBlock(world, pos, state);
	}

	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX,
			float hitY, float hitZ) {

		if (!world.isRemote) {
			return true;
		}

		player.openGui(ToroChess.INSTANCE, ToroChessGuiHandler.CHESS_CONTROL_GUI, world, pos.getX(), pos.getY(), pos.getZ());

		return true;
	}

}
