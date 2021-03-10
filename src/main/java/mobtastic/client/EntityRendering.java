package mobtastic.client;

import mobtastic.client.renderers.IceCubeRenderer;
import mobtastic.init.MobEntities;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class EntityRendering {
	
	@OnlyIn(Dist.CLIENT)
	public static void registerRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(MobEntities.ICE_CUBE, IceCubeRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(MobEntities.SKELETAL_KNIGHT, SkeletonRenderer::new);
	}
}
