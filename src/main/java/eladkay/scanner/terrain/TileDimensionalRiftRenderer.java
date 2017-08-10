package eladkay.scanner.terrain;

import eladkay.scanner.ScannerMod;
import eladkay.scanner.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class TileDimensionalRiftRenderer extends TileEntitySpecialRenderer<BlockDimensionalRift.TileDimensionalRift> {

	private IBakedModel modelFrom, modelTo;

	public TileDimensionalRiftRenderer() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void reload(ClientProxy.ResourceReloadEvent event) {
		modelFrom = null;
		modelTo = null;
	}

	private void getBakedModels() {
		IModel model = null;
		if (modelFrom == null) {
			try {
				model = ModelLoaderRegistry.getModel(new ResourceLocation(ScannerMod.MODID, "block/dimensionalRift"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			modelFrom = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
					location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
		}
		if (modelTo == null) {
			try {
				model = ModelLoaderRegistry.getModel(new ResourceLocation(ScannerMod.MODID, "block/dimensionalCore_" + (getWorld().provider.getDimension() == 0 ? "overworld" : getWorld().provider.getDimension() == 1 ? "end" : "nether")));
			} catch (Exception e) {
				e.printStackTrace();
			}
			modelTo = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
					location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
		}
	}

	@Override
	public void renderTileEntityAt(BlockDimensionalRift.TileDimensionalRift te, double x, double y, double z, float partialTicks, int destroyStage) {
		super.renderTileEntityAt(te, x, y, z, partialTicks, destroyStage);

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		getBakedModels();

		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		if (Minecraft.isAmbientOcclusionEnabled()) GlStateManager.shadeModel(GL11.GL_SMOOTH);
		else GlStateManager.shadeModel(GL11.GL_FLAT);

		GlStateManager.translate(x, y, z);
		GlStateManager.disableRescaleNormal();
		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();

		float s = (float) te.ticks / (float) BlockDimensionalRift.TileDimensionalRift.TICKS_TO_COMPLETION;

		GlStateManager.color(1, 1, 1, 1);

		Tessellator tes = Tessellator.getInstance();
		VertexBuffer buffer = tes.getBuffer();
		BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
		Minecraft mc = Minecraft.getMinecraft();
		mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		{
			int r = 255, g = 255, b = 255, a = (int) (s * 255);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
			for (EnumFacing facing : EnumFacing.VALUES) {
				modelFrom.getQuads(null, facing, 0).forEach(q -> {
					buffer.addVertexData(q.getVertexData());
					for (int i = 1; i <= 4; i++) {
						buffer.putColorRGBA(buffer.getColorIndex(i), r, g, b, a);
					}
				});
			}
		}
		{
			int r = 255, g = 255, b = 255, a = (int) (s * 255);
			// buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
			for (EnumFacing facing : EnumFacing.VALUES) {
				modelFrom.getQuads(null, facing, 0).forEach(q -> {
					buffer.addVertexData(q.getVertexData());
					for (int i = 1; i <= 4; i++) {
						buffer.putColorRGBA(buffer.getColorIndex(i), r, g, b, a);
					}
				});
			}
		}
		tes.draw();
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}
}

