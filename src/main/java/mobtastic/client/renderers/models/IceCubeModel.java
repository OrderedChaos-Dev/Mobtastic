package mobtastic.client.renderers.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.SlimeModel;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IceCubeModel<T extends Entity> extends SlimeModel<T> {

	public IceCubeModel(int slimeBodyTexOffY) {
		super(slimeBodyTexOffY);
	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		this.getParts().forEach((part) -> {
			part.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, 0.6F);
		});
	}
}
