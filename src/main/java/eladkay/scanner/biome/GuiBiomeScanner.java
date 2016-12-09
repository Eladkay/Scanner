package eladkay.scanner.biome;

import com.feed_the_beast.ftbl.api.gui.IGui;
import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.lib.MouseButton;
import com.feed_the_beast.ftbl.lib.gui.ButtonLM;
import com.feed_the_beast.ftbl.lib.gui.GuiHelper;
import com.feed_the_beast.ftbl.lib.gui.GuiLM;
import com.feed_the_beast.ftbl.lib.gui.GuiLang;
import com.feed_the_beast.ftbl.lib.gui.PanelLM;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiConfigs;
import com.feed_the_beast.ftbl.lib.gui.misc.ThreadReloadChunkSelector;
import com.feed_the_beast.ftbl.lib.math.MathHelperLM;
import eladkay.scanner.Config;
import eladkay.scanner.misc.NetworkHelper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GuiBiomeScanner extends GuiLM {
    public static GuiBiomeScanner instance;
    public final int startX, startZ;
    private final ButtonLM buttonRefresh, buttonClose;
    private final MapButton mapButtons[];
    private final PanelLM panelButtons;
    private final TileEntityBiomeScanner biomeScanner;
    private byte currentSelectionMode = -1;
    public GuiBiomeScanner(TileEntityBiomeScanner scanner) {
        super(GuiConfigs.CHUNK_SELECTOR_TILES_GUI * 16, GuiConfigs.CHUNK_SELECTOR_TILES_GUI * 16);

        biomeScanner = scanner;

        startX = MathHelperLM.chunk(mc.thePlayer.posX) - 7;
        startZ = MathHelperLM.chunk(mc.thePlayer.posZ) - 7;

        buttonClose = new ButtonLM(0, 0, 16, 16, GuiLang.BUTTON_CLOSE.translate()) {
            @Override
            public void onClicked(IGui gui, IMouseButton button) {
                GuiHelper.playClickSound();
                closeGui();
            }
        };

        buttonRefresh = new ButtonLM(0, 16, 16, 16, GuiLang.BUTTON_REFRESH.translate()) {
            @Override
            public void onClicked(IGui gui, IMouseButton button) {
                ThreadReloadChunkSelector.reloadArea(mc.theWorld, startX, startZ);
            }
        };

        panelButtons = new PanelLM(0, 0, 16, 0) {
            @Override
            public void addWidgets() {
                add(buttonClose);
                add(buttonRefresh);

                setHeight(getWidgets().size() * 16);
            }

            @Override
            public int getAX() {
                return getScreenWidth() - 16;
            }

            @Override
            public int getAY() {
                return 0;
            }
        };

        mapButtons = new MapButton[GuiConfigs.CHUNK_SELECTOR_TILES_GUI * GuiConfigs.CHUNK_SELECTOR_TILES_GUI];

        for (int i = 0; i < mapButtons.length; i++) {
            mapButtons[i] = new MapButton(0, 0, i);
        }
    }

    @Override
    public void onInit() {
        buttonRefresh.onClicked(this, MouseButton.LEFT);
    }

    @Override
    public void addWidgets() {
        for (MapButton b : mapButtons) {
            add(b);
        }

        add(panelButtons);
    }

    @Override
    public void drawBackground() {
        super.drawBackground();

        GlStateManager.color(0F, 0F, 0F, 1F);
        GuiHelper.drawBlankRect(posX - 2, posY - 2, getWidth() + 4, getHeight() + 4);
        //drawBlankRect((xSize - 128) / 2, (ySize - 128) / 2, zLevel, 128, 128);
        GlStateManager.color(1F, 1F, 1F, 1F);

        ThreadReloadChunkSelector.updateTexture();
        GlStateManager.bindTexture(ThreadReloadChunkSelector.getTextureID());
        GuiHelper.drawTexturedRect(posX, posY, GuiConfigs.CHUNK_SELECTOR_TILES_GUI * 16, GuiConfigs.CHUNK_SELECTOR_TILES_GUI * 16, 0D, 0D, GuiConfigs.CHUNK_SELECTOR_UV, GuiConfigs.CHUNK_SELECTOR_UV);

        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.enableTexture2D();

        for (MapButton mapButton : mapButtons) {
            mapButton.renderWidget(this);
        }

        GlStateManager.disableTexture2D();
        GlStateManager.glLineWidth(1F);
        GlStateManager.color(1F, 1F, 1F, 1F);
        int gridR = 128, gridG = 128, gridB = 128, gridA = 50;

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        int gridX = mapButtons[0].getAX();
        int gridY = mapButtons[0].getAY();

        for (int x = 0; x <= GuiConfigs.CHUNK_SELECTOR_TILES_GUI; x++) {
            buffer.pos(gridX + x * 16, gridY, 0D).color(gridR, gridG, gridB, gridA).endVertex();
            buffer.pos(gridX + x * 16, gridY + GuiConfigs.CHUNK_SELECTOR_TILES_GUI * 16, 0D).color(gridR, gridG, gridB, gridA).endVertex();
        }

        for (int y = 0; y <= GuiConfigs.CHUNK_SELECTOR_TILES_GUI; y++) {
            buffer.pos(gridX, gridY + y * 16, 0D).color(gridR, gridG, gridB, gridA).endVertex();
            buffer.pos(gridX + GuiConfigs.CHUNK_SELECTOR_TILES_GUI * 16, gridY + y * 16, 0D).color(gridR, gridG, gridB, gridA).endVertex();
        }

        tessellator.draw();
        GlStateManager.enableTexture2D();

        int cx = MathHelperLM.chunk(mc.thePlayer.posX);
        int cy = MathHelperLM.chunk(mc.thePlayer.posZ);

        if (cx >= startX && cy >= startZ && cx < startX + GuiConfigs.CHUNK_SELECTOR_TILES_GUI && cy < startZ + GuiConfigs.CHUNK_SELECTOR_TILES_GUI) {
            double x = ((cx - startX) * 16D + MathHelperLM.wrap(mc.thePlayer.posX, 16D));
            double y = ((cy - startZ) * 16D + MathHelperLM.wrap(mc.thePlayer.posZ, 16D));

            GlStateManager.pushMatrix();
            GlStateManager.translate(posX + x, posY + y, 0D);
            /*GlStateManager.pushMatrix();
            //GlStateManager.rotate((int)((ep.rotationYaw + 180F) / (180F / 8F)) * (180F / 8F), 0F, 0F, 1F);
            GlStateManager.rotate(mc.thePlayer.rotationYaw + 180F, 0F, 0F, 1F);
            FTBLibClient.setTexture(GuiConfigs.TEX_ENTITY);
            GlStateManager.color(1F, 1F, 1F, mc.thePlayer.isSneaking() ? 0.4F : 0.7F);
            GuiHelper.drawTexturedRect(-8, -8, 16, 16, 0D, 0D, 1D, 1D);
            GlStateManager.popMatrix();*/
            GuiHelper.drawPlayerHead(mc.thePlayer.getName(), -2, -2, 4, 4);
            GlStateManager.popMatrix();
        }

        GlStateManager.color(1F, 1F, 1F, 1F);

    }

    @Override
    public void mouseReleased(IGui gui) {
        super.mouseReleased(gui);

        if (currentSelectionMode != -1) {
            Collection<ChunkPos> c = new ArrayList<>();


            for (MapButton b : mapButtons) {
                if (b.isSelected) {
                    c.add(b.chunkPos);
                    b.isSelected = false;
                }
            }

            //new MessageClaimedChunksModify(startX, startZ, currentSelectionMode, c).sendToServer();
            currentSelectionMode = -1;
        }
    }

    @Override
    public void drawForeground() {
        super.drawForeground();
    }

    private class MapButton extends ButtonLM {
        private final ChunkPos chunkPos;
        private final int index;
        private boolean isSelected = false;

        private MapButton(int x, int y, int i) {
            super(x, y, 16, 16);
            posX += (i % GuiConfigs.CHUNK_SELECTOR_TILES_GUI) * getWidth();
            posY += (i / GuiConfigs.CHUNK_SELECTOR_TILES_GUI) * getHeight();
            chunkPos = new ChunkPos(startX + (i % GuiConfigs.CHUNK_SELECTOR_TILES_GUI), startZ + (i / GuiConfigs.CHUNK_SELECTOR_TILES_GUI));
            index = i;
        }

        @Override
        public void onClicked(IGui gui, IMouseButton button) {
            int distance = biomeScanner.getDist(chunkPos);
            if (biomeScanner.getMapping(chunkPos.chunkXPos, chunkPos.chunkZPos) != null || biomeScanner.getEnergyStored(null) < Config.minEnergyPerChunkBiomeScanner * Config.increase * distance)
                return;
            if (biomeScanner.type == 0 && distance > 2)
                return;
            else if (biomeScanner.type == 1 && distance > 4)
                return;
            else if (biomeScanner.type == 2 && distance > 8)
                return;

            biomeScanner.container().extractEnergy(Config.minEnergyPerChunkBiomeScanner * Config.increase * distance, false);
            biomeScanner.mapping.put(new ChunkPos(chunkPos.chunkXPos, chunkPos.chunkZPos), mc.theWorld.getBiomeGenForCoords(new BlockPos(chunkPos.chunkXPos * 16, 64, chunkPos.chunkZPos * 16)).getBiomeName());
            biomeScanner.markDirty();
            NetworkHelper.instance.sendToServer(new MessageUpdateMap(biomeScanner, chunkPos.chunkXPos, chunkPos.chunkZPos));

            GuiHelper.playClickSound();
            currentSelectionMode = 1;
        }

        @Override
        public void addMouseOverText(IGui gui, List<String> l) {
            int distance = biomeScanner.getDist(chunkPos);
            if (biomeScanner.getMapping(chunkPos.chunkXPos, chunkPos.chunkZPos) != null) {
                l.add(biomeScanner.getMapping(chunkPos.chunkXPos, chunkPos.chunkZPos));
                l.add("(" + chunkPos.chunkXPos + ", " + chunkPos.chunkZPos + ")");
            } else {
                l.add("???");
                l.add("Click to scan!");
                l.add("Power cost: " + Config.minEnergyPerChunkBiomeScanner * Config.increase * distance);
                l.add("Distance (chunks): " + distance);
                if (biomeScanner.type == 0 && distance > 2) {
                    l.add("Basic Biome Scanner cannot scan chunks more than 2 chunks away!");
                } else if (biomeScanner.type == 1 && distance > 4) {
                    l.add("Advanced Biome Scanner cannot scan chunks more than 4 chunks away!");
                } else if (biomeScanner.type == 2 && distance > 8) {
                    l.add("Elite Biome Scanner cannot scan chunks more than 8 chunks away!");
                }
            }

            if (GuiScreen.isCtrlKeyDown()) {
                l.add(chunkPos.toString());
            }
        }

        @Override
        public void renderWidget(IGui gui) {
            int ax = getAX();
            int ay = getAY();

            if (isSelected || gui.isMouseOver(this)) {
                GlStateManager.color(1F, 1F, 1F, 0.27F);
                GuiHelper.drawBlankRect(ax, ay, 16, 16);
                GlStateManager.color(1F, 1F, 1F, 1F);
            }

            if (!isSelected && currentSelectionMode != -1 && isMouseOver(this)) {
                isSelected = true;
            }
        }
    }
}