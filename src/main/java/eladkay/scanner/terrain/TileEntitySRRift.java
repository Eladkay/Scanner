package eladkay.scanner.terrain;

import com.sun.javafx.geom.Vec4d;
import com.teamwizardry.librarianlib.common.util.math.Vec2d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

@SideOnly(Side.CLIENT)
public class TileEntitySRRift extends TileEntitySpecialRenderer<BlockDimensionalRift.TileDimensionalRift> {
    private IBakedModel modelRift = null, modelCore;

    @Override
    public void renderTileEntityAt(BlockDimensionalRift.TileDimensionalRift te, double x, double y, double z, float partialTicks, int destroyStage) {
        super.renderTileEntityAt(te, x, y, z, partialTicks, destroyStage);
        float percentOfRiftRemaining = te.ticks / BlockDimensionalRift.TileDimensionalRift.TICKS_TO_COMPLETION;
        VertexBuffer buf = Tessellator.getInstance().getBuffer();
        GlStateManager.disableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableRescaleNormal();
        setLightmapDisabled(false);
        //minU, minV, w, h
        float w = 0.25f;
        float h = 0.25f;
        //NORTH, SOUTH, UP, DOWN, EAST, WEST
        Vec2d[] uvs = {new Vec2d(0.0, 0.25), // north
                new Vec2d(0.25, 0.25), // south
                new Vec2d(0.25, 0.0), // up
                new Vec2d(0.0, 0.0), // down
                new Vec2d(0.75, 0.0), // east
                new Vec2d(0.5, 0.0), // west
        };
        Vec4d[] array = Arrays.stream(uvs).map(a -> new Vec4d(a.getX(), a.getY(), w, h)).toArray(Vec4d[]::new);
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("scanner:textures/blocks/texsheet.png"));
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        addCubeToBuffer(buf, x, y, z, x + 1, y + 1, z + 1, array, 1, 1, 1, percentOfRiftRemaining, true, true, true, true, true, true);
        Tessellator.getInstance().draw();
        uvs = new Vec2d[]{new Vec2d(48 / 64, 33 / 64), // north
                new Vec2d(48 / 64, 33 / 64), // south
                new Vec2d(48 / 64, 17 / 64), // up
                new Vec2d(48 / 64, 33 / 64), // down
                new Vec2d(48 / 64, 33 / 64), // east
                new Vec2d(48 / 64, 33 / 64), // west
        };
        array = Arrays.stream(uvs).map(a -> new Vec4d(a.getX(), a.getY(), w, h)).toArray(Vec4d[]::new);
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("scanner:textures/blocks/texsheet.png"));
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        addCubeToBuffer(buf, x, y, z, x + 1, y + 1, z + 1, array, 1, 1, 1, 1 - percentOfRiftRemaining, true, true, true, true, true, true);
        Tessellator.getInstance().draw();

//        IModel model = null;
//        if (modelCore == null) {
//            try {
//                model = ModelLoaderRegistry.getModel(new ResourceLocation(ScannerMod.MODID, "block/dimensionalCore_overworld")); //todo every dim
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            modelCore = model.bake(model.getDefaultState(), DefaultVertexFormats.BLOCK,
//                    location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
//        }
//        if (modelRift == null) {
//            try {
//                model = ModelLoaderRegistry.getModel(new ResourceLocation(ScannerMod.MODID, "block/dimensionalRift"));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            modelRift = model.bake(model.getDefaultState(), DefaultVertexFormats.BLOCK,
//                    location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
//        }

//            GlStateManager.pushMatrix();
//            GlStateManager.enableAlpha();
//            GlStateManager.enableBlend();
//            GlStateManager.enableLighting();
//            GlStateManager.enableRescaleNormal();
//            GlStateManager.color(1, 1, 1);
//
//            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//
//            GlStateManager.translate(x, y, z);
//            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        //Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColorQuads(1, 1, 1, 1, modelCore.getQuads(ScannerMod.dimensionalCore.getDefaultState(), EnumFacing.DOWN,  te.getWorld().rand.nextLong()));
//        BlockPos pos = te.getPos();
        //buffer.setTranslation(x - pos.getX(), y - pos.getY(), z - pos.getZ());
        //Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(te.getWorld(), modelCore, ScannerMod.dimensionalCore.getDefaultState(), te.getPos(), buffer, true);


        //GlStateManager.popMatrix();
    }


    public static void addCubeToBuffer(VertexBuffer buff,
                                       double x1, double y1, double z1, double x2, double y2, double z2,
                                       Vec4d[] uv, float r, float g, float b, float a,
                                       boolean north, boolean south, boolean up, boolean down, boolean east, boolean west) {
        if (north) {
            buff.pos(x1, y1, z1).tex(uv[0].x, uv[0].y).color(r, g, b, a).normal(0, 0, -1.0f).endVertex();
            buff.pos(x2, y1, z1).tex(uv[0].x + uv[0].z, uv[0].y).color(r, g, b, a).normal(0, 0, -1.0f).endVertex();
            buff.pos(x2, y2, z1).tex(uv[0].x + uv[0].z, uv[0].y + uv[0].w).color(r, g, b, a).normal(0, 0, -1.0f).endVertex();
            buff.pos(x1, y2, z1).tex(uv[0].x, uv[0].y + uv[0].w).color(r, g, b, a).normal(0, 0, -1.0f).endVertex();
        }
        if (south) {
            buff.pos(x2, y1, z2).tex(uv[1].x, uv[1].y).color(r, g, b, a).normal(0, 0, 1.0f).endVertex();
            buff.pos(x1, y1, z2).tex(uv[1].x + uv[1].z, uv[1].y).color(r, g, b, a).normal(0, 0, 1.0f).endVertex();
            buff.pos(x1, y2, z2).tex(uv[1].x + uv[1].z, uv[1].y + uv[1].w).color(r, g, b, a).normal(0, 0, 1.0f).endVertex();
            buff.pos(x2, y2, z2).tex(uv[1].x, uv[1].y + uv[1].w).color(r, g, b, a).normal(0, 0, 1.0f).endVertex();
        }
        if (up) {
            buff.pos(x1, y2, z1).tex(uv[2].x, uv[2].y).color(r, g, b, a).normal(0, 1.0f, 0).endVertex();
            buff.pos(x2, y2, z1).tex(uv[2].x + uv[2].z, uv[2].y).color(r, g, b, a).normal(0, 1.0f, 0).endVertex();
            buff.pos(x2, y2, z2).tex(uv[2].x + uv[2].z, uv[2].y + uv[2].w).color(r, g, b, a).normal(0, 1.0f, 0).endVertex();
            buff.pos(x1, y2, z2).tex(uv[2].x, uv[2].y + uv[2].w).color(r, g, b, a).normal(0, 1.0f, 0).endVertex();
        }
        if (down) {
            buff.pos(x2, y1, z1).tex(uv[3].x, uv[3].y).color(r, g, b, a).normal(0, -1.0f, 0).endVertex();
            buff.pos(x1, y1, z1).tex(uv[3].x + uv[3].z, uv[3].y).color(r, g, b, a).normal(0, -1.0f, 0).endVertex();
            buff.pos(x1, y1, z2).tex(uv[3].x + uv[3].z, uv[3].y + uv[3].w).color(r, g, b, a).normal(0, -1.0f, 0).endVertex();
            buff.pos(x2, y1, z2).tex(uv[3].x, uv[3].y + uv[3].w).color(r, g, b, a).normal(0, -1.0f, 0).endVertex();
        }
        if (east) {
            buff.pos(x2, y1, z2).tex(uv[4].x, uv[4].y).color(r, g, b, a).normal(1.0f, 0, 0).endVertex();
            buff.pos(x2, y1, z1).tex(uv[4].x + uv[4].z, uv[4].y).color(r, g, b, a).normal(1.0f, 0, 0).endVertex();
            buff.pos(x2, y2, z1).tex(uv[4].x + uv[4].z, uv[4].y + uv[4].w).color(r, g, b, a).normal(1.0f, 0, 0).endVertex();
            buff.pos(x2, y2, z2).tex(uv[4].x, uv[4].y + uv[4].w).color(r, g, b, a).normal(1.0f, 0, 0).endVertex();
        }
        if (west) {
            buff.pos(x1, y1, z1).tex(uv[5].x, uv[5].y).color(r, g, b, a).normal(-1.0f, 0, 0).endVertex();
            buff.pos(x1, y1, z2).tex(uv[5].x + uv[5].z, uv[5].y).color(r, g, b, a).normal(-1.0f, 0, 0).endVertex();
            buff.pos(x1, y2, z2).tex(uv[5].x + uv[5].z, uv[5].y + uv[5].w).color(r, g, b, a).normal(-1.0f, 0, 0).endVertex();
            buff.pos(x1, y2, z1).tex(uv[5].x, uv[5].y + uv[5].w).color(r, g, b, a).normal(-1.0f, 0, 0).endVertex();
        }
    }


}
