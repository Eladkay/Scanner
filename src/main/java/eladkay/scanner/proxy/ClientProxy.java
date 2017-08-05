package eladkay.scanner.proxy;

import eladkay.scanner.ScannerMod;
import eladkay.scanner.biome.GuiBiomeScanner;
import eladkay.scanner.biome.TileEntityBiomeScanner;
import eladkay.scanner.terrain.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nullable;

public class ClientProxy extends CommonProxy {
    private static final String IP = "http://eladkay.pw/scanner/ScannerCallback.php";
    private static boolean sentCallback = false;

    @Override
    public void init() {
        super.init();
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ScannerMod.dimensionalCore), 0, new ModelResourceLocation("scanner:dimensionalCore_overworld", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ScannerMod.dimensionalCore), 1, new ModelResourceLocation("scanner:dimensionalCore_nether", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ScannerMod.dimensionalCore), 2, new ModelResourceLocation("scanner:dimensionalCore_end", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ScannerMod.dimensionalCore), 3, new ModelResourceLocation("scanner:dimensionalCore_none", "inventory"));
        ClientRegistry.bindTileEntitySpecialRenderer(BlockDimensionalRift.TileDimensionalRift.class, new TileRiftRenderer());
       /* if(Config.showOutline)
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTerrainScanner.class, new TileEntitySpecialRendererTerrainScanner());*/
        MinecraftForge.EVENT_BUS.register(this);

    }

    public static class TileRiftRenderer extends TileEntitySpecialRenderer<BlockDimensionalRift.TileDimensionalRift> {
        private IBakedModel modelRift = null, modelCore;

        @Override
        public void renderTileEntityAt(BlockDimensionalRift.TileDimensionalRift te, double x, double y, double z, float partialTicks, int destroyStage) {
            super.renderTileEntityAt(te, x, y, z, partialTicks, destroyStage);

            IModel model = null;
            if (modelCore == null) {
                try {
                    model = ModelLoaderRegistry.getModel(new ResourceLocation(ScannerMod.MODID, "block/dimensionalCore_overworld")); //todo every dim
                } catch (Exception e) {
                    e.printStackTrace();
                }
                modelCore = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
                        location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
            }
            if (modelRift == null) {
                try {
                    model = ModelLoaderRegistry.getModel(new ResourceLocation(ScannerMod.MODID, "block/dimensionalRift"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                modelRift = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
                        location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
            }

            GlStateManager.pushMatrix();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.color(1, 1, 1);

            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

            GlStateManager.translate(x, y, z);
            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(modelCore, 1.0F, 1, 1, 1);

            GlStateManager.popMatrix();

        }
    }


    @Override
    public void openGuiBiomeScanner(TileEntityBiomeScanner tileEntity) {
        new GuiBiomeScanner(tileEntity).openGui();
    }

    @Override
    public void openGuiTerrainScanner(TileEntityTerrainScanner tileEntity) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiTerrainScanner(tileEntity));
    }

    @Override
    @Nullable
    public World getWorld() {
        return Minecraft.getMinecraft().world;
    }

    @Override
    public void openGuiScannerQueue(TileEntityScannerQueue tileEntity) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiScannerQueue(tileEntity));
    }
}
