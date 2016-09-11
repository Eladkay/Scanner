package eladkay.scanner.terrain;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class TileEntitySpecialRendererTerrainScanner extends TileEntitySpecialRenderer<TileEntityTerrainScanner> {
    @Override
    public void renderTileEntityAt(TileEntityTerrainScanner te, double x, double y, double z, float partialTicks, int destroyStage) {
        super.renderTileEntityAt(te, x, y, z, partialTicks, destroyStage);
        int i = 255;
        int j = 223;
        int k = 127;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        GlStateManager.disableFog();
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        this.func_190053_a(true);

        renderLine(x + 1, y - 255, z, x + 17, (int)y + 255, z + 16, 255, 223, 127);
        this.func_190053_a(false);
        GlStateManager.glLineWidth(1.0F);
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableFog();

    }
    public static void renderLine(double x1, double y1, double z1, double x2, double y2, double z2, int idk1, int idk2, int idk3) {
        Tessellator tesselator = Tessellator.getInstance();
        GlStateManager.glLineWidth(2.0F);
        VertexBuffer vxb = tesselator.getBuffer();
        vxb.begin(3, DefaultVertexFormats.POSITION_COLOR);
        vxb.pos(x1, y1, z1).color((float) idk2, (float) idk2, (float) idk2, 0.0F).endVertex();
        vxb.pos(x1, y1, z1).color(idk2, idk2, idk2, idk1).endVertex();
        vxb.pos(x2, y1, z1).color(idk2, idk3, idk3, idk1).endVertex();
        vxb.pos(x2, y1, z2).color(idk2, idk2, idk2, idk1).endVertex();
        vxb.pos(x1, y1, z2).color(idk2, idk2, idk2, idk1).endVertex();
        vxb.pos(x1, y1, z1).color(idk3, idk3, idk2, idk1).endVertex();
        vxb.pos(x1, y2, z1).color(idk3, idk2, idk3, idk1).endVertex();
        vxb.pos(x2, y2, z1).color(idk2, idk2, idk2, idk1).endVertex();
        vxb.pos(x2, y2, z2).color(idk2, idk2, idk2, idk1).endVertex();
        vxb.pos(x1, y2, z2).color(idk2, idk2, idk2, idk1).endVertex();
        vxb.pos(x1, y2, z1).color(idk2, idk2, idk2, idk1).endVertex();
        vxb.pos(x1, y2, z2).color(idk2, idk2, idk2, idk1).endVertex();
        vxb.pos(x1, y1, z2).color(idk2, idk2, idk2, idk1).endVertex();
        vxb.pos(x2, y1, z2).color(idk2, idk2, idk2, idk1).endVertex();
        vxb.pos(x2, y2, z2).color(idk2, idk2, idk2, idk1).endVertex();
        vxb.pos(x2, y2, z1).color(idk2, idk2, idk2, idk1).endVertex();
        vxb.pos(x2, y1, z1).color(idk2, idk2, idk2, idk1).endVertex();
        vxb.pos(x2, y1, z1).color((float) idk2, (float) idk2, (float) idk2, 0.0F).endVertex();
        tesselator.draw();
        GlStateManager.glLineWidth(1.0F);
    }
}
