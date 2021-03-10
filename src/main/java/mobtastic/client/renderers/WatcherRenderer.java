package mobtastic.client.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import mobtastic.client.renderers.layers.WatcherLayer;
import mobtastic.client.renderers.models.WatcherModel;
import mobtastic.common.entities.WatcherEntity;
import mobtastic.core.Mobtastic;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WatcherRenderer extends MobRenderer<WatcherEntity, WatcherModel<WatcherEntity>> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(Mobtastic.MOD_ID, "textures/entity/watcher.png");
	
	public WatcherRenderer(EntityRendererManager manager) {
		super(manager, new WatcherModel<>(16), 0.25F);
		this.addLayer(new WatcherLayer<>(this));
	}

	@Override
	protected void preRenderCallback(WatcherEntity entity, MatrixStack matrixstack, float f) {
		matrixstack.scale(1.4F, 1.4F, 1.4F);
	}
	
	@Override
	public ResourceLocation getEntityTexture(WatcherEntity entity) {
		return TEXTURE;
	}
}