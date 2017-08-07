package eladkay.scanner.terrain;

import eladkay.scanner.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.awt.*;

import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.GL_FUNC_REVERSE_SUBTRACT;

public class TileTerrainScannerRenderer extends TileEntitySpecialRenderer<TileEntityTerrainScanner> {

	@Override
	public void renderTileEntityAt(TileEntityTerrainScanner te, double x, double y, double z, float partialTicks, int destroyStage) {
		super.renderTileEntityAt(te, x, y, z, partialTicks, destroyStage);
		if (Config.showOutline) {

			BlockPos me = te.getPos();
			Vec3d start = new Vec3d(te.getPosStart().subtract(me));
			Vec3d end = new Vec3d(te.getEnd().subtract(me));
			start = new Vec3d(start.xCoord - 0.01, -me.getY(), start.zCoord - 0.01);
			end = new Vec3d(end.xCoord + 1 + 0.01, 255, end.zCoord + 1 + 0.01);

			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);

			GlStateManager.depthMask(false);

			//GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.disableCull();
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
			GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE);
			GlStateManager.disableTexture2D();
			GlStateManager.color(1, 1, 1);
			Minecraft.getMinecraft().entityRenderer.disableLightmap();

			Tessellator tess = Tessellator.getInstance();
			VertexBuffer buffer = tess.getBuffer();

			{
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
				Color color = new Color(0f, 1f, 1f, 0.5f);
				double layerY = (te.currentY - te.getPos().getY() + 0.5) + (te.layerBlocksPlace / 256.0);

				buffer.pos(start.xCoord - 3, layerY, start.zCoord - 3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
				buffer.pos(start.xCoord - 3, layerY, end.zCoord + 3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
				buffer.pos(end.xCoord + 3, layerY, end.zCoord + 3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
				buffer.pos(end.xCoord + 3, layerY, start.zCoord - 3).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

				tess.draw();
			}

			GL14.glBlendEquation(GL_FUNC_REVERSE_SUBTRACT);

			float alpha = te.on ? 0.15f : 0.5f;

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

			Color color = new Color(1, 0, 0, alpha);
			buffer.pos(start.xCoord, start.yCoord, start.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
			buffer.pos(end.xCoord, start.yCoord, start.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
			buffer.pos(end.xCoord, start.yCoord + 255, start.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			buffer.pos(start.xCoord, start.yCoord + 255, start.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();

			color = new Color(0, 0, 1, alpha);
			buffer.pos(start.xCoord, start.yCoord, start.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
			buffer.pos(start.xCoord, start.yCoord, end.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
			buffer.pos(start.xCoord, start.yCoord + 255, end.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			buffer.pos(start.xCoord, start.yCoord + 255, start.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();

			color = new Color(0, 1, 0, alpha);
			buffer.pos(end.xCoord, start.yCoord, end.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
			buffer.pos(end.xCoord, start.yCoord, start.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
			buffer.pos(end.xCoord, start.yCoord + 255, start.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			buffer.pos(end.xCoord, start.yCoord + 255, end.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();

			color = new Color(1, 1, 0, alpha);
			buffer.pos(end.xCoord, start.yCoord, end.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
			buffer.pos(start.xCoord, start.yCoord, end.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
			buffer.pos(start.xCoord, start.yCoord + 255, end.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
			buffer.pos(end.xCoord, start.yCoord + 255, end.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();

			tess.draw();

			GL14.glBlendEquation(GL_FUNC_ADD);
			GlStateManager.enableTexture2D();
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean isGlobalRenderer(TileEntityTerrainScanner te) {
		return true;
	}
}
