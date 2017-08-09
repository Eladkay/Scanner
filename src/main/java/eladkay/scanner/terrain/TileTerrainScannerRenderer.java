package eladkay.scanner.terrain;

import com.teamwizardry.librarianlib.common.util.math.interpolate.position.InterpBezier3D;
import com.teamwizardry.librarianlib.common.util.math.interpolate.position.InterpLine;
import eladkay.scanner.Config;
import eladkay.scanner.misc.PlaceObject;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.awt.*;

import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.GL_FUNC_REVERSE_SUBTRACT;

public class TileTerrainScannerRenderer extends TileEntitySpecialRenderer<TileEntityTerrainScanner> {

	private static int mode = 0;

	@Override
	public void renderTileEntityAt(TileEntityTerrainScanner te, double x, double y, double z, float partialTicks, int destroyStage) {
		super.renderTileEntityAt(te, x, y, z, partialTicks, destroyStage);

		BlockPos me = te.getPos();
		Vec3d start = new Vec3d(te.getPosStart().subtract(me));
		Vec3d end = new Vec3d(te.getEnd().subtract(me));
		start = new Vec3d(start.xCoord - 0.01, -me.getY(), start.zCoord - 0.01);
		end = new Vec3d(end.xCoord + 1 + 0.01, 255, end.zCoord + 1 + 0.01);

		double mode = 3;
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);

			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.enableLighting();
			GlStateManager.enableRescaleNormal();
			GlStateManager.color(1, 1, 1);
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

			if (mode == 0) {
				// From sky
				for (PlaceObject object : te.animationQueue) {
					if (object.state.getMaterial() == Material.AIR) continue;

					BlockPos posOffset = object.pos.subtract(me);
					double t = (te.getWorld().getTotalWorldTime() - object.worldTime) / (double) PlaceObject.maxTick;
					double m = 256.0 * (1 - MathHelper.sqrt(1 - Math.pow(1 - (t), 2)));

					GlStateManager.translate(posOffset.getX(), posOffset.getY(), posOffset.getZ());
					GlStateManager.translate(0, m, 0);
					GlStateManager.scale(m, m, m);
					Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(object.state), 1.0F, 1, 1, 1);
					GlStateManager.translate(0, -m, 0);
					GlStateManager.translate(-posOffset.getX(), -posOffset.getY(), -posOffset.getZ());
					GlStateManager.scale(1, 1, 1);
				}
			} else if (mode == 1) {
				// From machine to pos
				for (PlaceObject object : te.animationQueue) {
					if (object.state.getMaterial() == Material.AIR) continue;

					BlockPos posOffset = object.pos.subtract(me);

					double t = (te.getWorld().getTotalWorldTime() - object.worldTime) / PlaceObject.maxTick;
					double m = (MathHelper.sqrt(1 - Math.pow(1 - (t), 2)));
					double x1 = m * posOffset.getX();
					double y1 = m * posOffset.getY();
					double z1 = m * posOffset.getZ();

					Vec3d vec = new Vec3d(x1, y1, z1);

					GlStateManager.translate(0.5, 0.5, 0.5);
					GlStateManager.scale(m, m, m);
					GlStateManager.translate(-0.5, -0.5, -0.5);

					GlStateManager.translate(vec.xCoord, vec.yCoord, vec.zCoord);
					Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(object.state), (float) 1, 1, 1, 1);
					GlStateManager.translate(-vec.xCoord, -vec.yCoord, -vec.zCoord);
					GlStateManager.scale(1, 1, 1);
				}
			} else if (mode == 2) {
				// Expand from pos but it expands from chunk center
				for (PlaceObject object : te.animationQueue) {
					if (object.state.getMaterial() == Material.AIR) continue;

					BlockPos posOffset = object.pos.subtract(me);

					double t = (te.getWorld().getTotalWorldTime() - object.worldTime) / PlaceObject.maxTick;
					double m = (MathHelper.sqrt(1 - Math.pow(1 - (t), 2)));


					GlStateManager.translate(posOffset.getX(), posOffset.getY(), posOffset.getZ());

					GlStateManager.translate(0.5, 0.5, 0.5);
					GlStateManager.scale(m, m, m);
					GlStateManager.translate(-0.5, -0.5, -0.5);

					Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(object.state), (float) 1, 1, 1, 1);

					GlStateManager.translate(-posOffset.getX(), -posOffset.getY(), -posOffset.getZ());
					GlStateManager.scale(1, 1, 1);
				}
			} else if (mode == 3) {
				// rise from void
				for (PlaceObject object : te.animationQueue) {
					if (object.state.getMaterial() == Material.AIR) continue;

					BlockPos posOffset = object.pos.subtract(me);
					double t = (te.getWorld().getTotalWorldTime() - object.worldTime) / PlaceObject.maxTick;
					double m = -256 * (1 - MathHelper.sqrt(1 - Math.pow(1 - (t), 2))) ;

					GlStateManager.translate(posOffset.getX(), posOffset.getY(), posOffset.getZ());
					GlStateManager.translate(0, m, 0);
					Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(object.state), 1.0F, 1, 1, 1);
					GlStateManager.translate(0, -m, 0);
					GlStateManager.translate(-posOffset.getX(), -posOffset.getY(), -posOffset.getZ());
					GlStateManager.scale(1, 1, 1);
				}
			} else if (mode == 4) {
				// rise from 1 block below
				for (PlaceObject object : te.animationQueue) {
					if (object.state.getMaterial() == Material.AIR) continue;

					BlockPos posOffset = object.pos.subtract(me);
					double t = (te.getWorld().getTotalWorldTime() - object.worldTime) / PlaceObject.maxTick;
					double m = -1 * (1 - MathHelper.sqrt(1 - Math.pow(1 - (t), 2))) ;

					GlStateManager.translate(posOffset.getX(), posOffset.getY(), posOffset.getZ());
					GlStateManager.translate(0, m, 0);
					Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(object.state), 1.0F, 1, 1, 1);
					GlStateManager.translate(0, -m, 0);
					GlStateManager.translate(-posOffset.getX(), -posOffset.getY(), -posOffset.getZ());
					GlStateManager.scale(1, 1, 1);
				}
			}
			GlStateManager.popMatrix();
		}

		{
			if (Config.showOutline) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(x, y, z);

				GlStateManager.depthMask(false);

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

				// LAYER
				{
					buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
					Color color = new Color(0f, 0f, 1f, 0.5f);
					double layerY = (te.currentY - te.getPos().getY() - 0.2) + (te.layerBlocksPlace / 256.0);

					buffer.pos(start.xCoord - 2, layerY, start.zCoord - 2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
					buffer.pos(start.xCoord - 2, layerY, end.zCoord + 2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
					buffer.pos(end.xCoord + 2, layerY, end.zCoord + 2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
					buffer.pos(end.xCoord + 2, layerY, start.zCoord - 2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

					tess.draw();
				}

				float alpha = te.on ? 0.15f : 0.5f;

				GL14.glBlendEquation(GL_FUNC_REVERSE_SUBTRACT);

				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

				Color color = new Color(1f, 0f, 0f, alpha);
				buffer.pos(start.xCoord, start.yCoord, start.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
				buffer.pos(end.xCoord, start.yCoord, start.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
				buffer.pos(end.xCoord, start.yCoord + 255, start.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
				buffer.pos(start.xCoord, start.yCoord + 255, start.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();

				color = new Color(0f, 0f, 1f, alpha);
				buffer.pos(start.xCoord, start.yCoord, start.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
				buffer.pos(start.xCoord, start.yCoord, end.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
				buffer.pos(start.xCoord, start.yCoord + 255, end.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
				buffer.pos(start.xCoord, start.yCoord + 255, start.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();

				color = new Color(0f, 1f, 0f, alpha);
				buffer.pos(end.xCoord, start.yCoord, end.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
				buffer.pos(end.xCoord, start.yCoord, start.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
				buffer.pos(end.xCoord, start.yCoord + 255, start.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
				buffer.pos(end.xCoord, start.yCoord + 255, end.zCoord).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();

				color = new Color(1f, 0f, 1f, alpha);
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
	}

	@Override
	public boolean isGlobalRenderer(TileEntityTerrainScanner te) {
		return true;
	}
}
