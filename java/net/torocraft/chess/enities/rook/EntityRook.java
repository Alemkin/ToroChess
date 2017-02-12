package net.torocraft.chess.enities.rook;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.chess.ToroChess;
import net.torocraft.chess.enities.EntityChessPiece;
import net.torocraft.chess.enities.IChessPiece;

public class EntityRook extends EntityChessPiece implements IChessPiece {
	
	public static String NAME = "rook";

	public static void init(int entityId) {
		EntityRegistry.registerModEntity(new ResourceLocation(ToroChess.MODID, NAME), EntityRook.class, NAME, entityId, ToroChess.INSTANCE, 60, 2, true);
	}

	public static void registerRenders() {
		RenderingRegistry.registerEntityRenderingHandler(EntityRook.class, new IRenderFactory<EntityRook>() {
			@Override
			public Render<EntityRook> createRenderFor(RenderManager manager) {
				return new RenderRook(manager);
			}
		});
	}

	public EntityRook(World worldIn) {
		super(worldIn);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return null;
	}

}
