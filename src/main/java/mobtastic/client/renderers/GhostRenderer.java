package mobtastic.client.renderers;

import mobtastic.client.renderers.models.GhostModel;
import mobtastic.common.entities.GhostEntity;
import mobtastic.core.Mobtastic;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GhostRenderer extends BipedRenderer<GhostEntity, GhostModel<GhostEntity>> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(Mobtastic.MOD_ID, "textures/entity/ghost.png");
	
	public GhostRenderer(EntityRendererManager manager) {
		super(manager, new GhostModel<>(), 0.0F);
	}
	
	@Override
	public ResourceLocation getEntityTexture(GhostEntity entity) {
		return TEXTURE;
	}
	
	//this handles transparency - return false for always transparent
	@Override
	protected boolean isVisible(GhostEntity entity) {
		return false;
	}
}