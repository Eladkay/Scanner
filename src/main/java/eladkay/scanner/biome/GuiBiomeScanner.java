package eladkay.scanner.biome;

import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.lib.Color4I;
import com.feed_the_beast.ftbl.lib.MouseButton;
import com.feed_the_beast.ftbl.lib.client.FTBLibClient;
import com.feed_the_beast.ftbl.lib.gui.Button;
import com.feed_the_beast.ftbl.lib.gui.GuiBase;
import com.feed_the_beast.ftbl.lib.gui.GuiHelper;
import com.feed_the_beast.ftbl.lib.gui.GuiLang;
import com.feed_the_beast.ftbl.lib.gui.Panel;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiConfigs;
import com.feed_the_beast.ftbl.lib.gui.misc.ThreadReloadChunkSelector;
import com.feed_the_beast.ftbl.lib.math.MathUtils;
import eladkay.scanner.Config;
import eladkay.scanner.misc.MessageUpdateEnergyServer;
import eladkay.scanner.misc.NetworkHelper;
import net.minecraft.client.Minecraft;
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

import static com.feed_the_beast.ftbl.lib.gui.GuiHelper.drawTexturedRect;

public class GuiBiomeScanner extends GuiBase
{
    public static GuiBiomeScanner instance;
    public final int startX, startZ;
    private final Button buttonRefresh, buttonClose;
    private final MapButton mapButtons[];
    private final Panel panelButtons;
    private final TileEntityBiomeScanner scanner;
    private byte currentSelectionMode = -1;

    public GuiBiomeScanner(TileEntityBiomeScanner scanner) {
        super(GuiConfigs.CHUNK_SELECTOR_TILES_GUI * 16, GuiConfigs.CHUNK_SELECTOR_TILES_GUI * 16);

        this.scanner = scanner;

        startX = MathUtils.chunk(mc.player.posX) - 7;
        startZ = MathUtils.chunk(mc.player.posZ) - 7;

        buttonClose = new Button(0, 0, 16, 16, GuiLang.BUTTON_CLOSE.translate()) {
            @Override
            public void onClicked(GuiBase gui, IMouseButton button) {
                GuiHelper.playClickSound();
                closeGui();
            }
        };

        buttonRefresh = new Button(0, 16, 16, 16, GuiLang.BUTTON_REFRESH.translate()) {
            @Override
            public void onClicked(GuiBase gui, IMouseButton button) {
                ThreadReloadChunkSelector.reloadArea(mc.world, startX, startZ);
            }
        };

        panelButtons = new Panel(0, 0, 16, 0) {
            @Override
            public void addWidgets() {
                add(buttonClose);
                add(buttonRefresh);

                setHeight(getWidgets().size() * 16);
            }

            @Override
            public int getAX() {
                return width - 16;
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

        GuiHelper.drawBlankRect(posX - 2, posY - 2, width + 4, height + 4, Color4I.BLACK);
        //drawBlankRect((xSize - 128) / 2, (ySize - 128) / 2, zLevel, 128, 128);
        GlStateManager.color(1F, 1F, 1F, 1F);

        ThreadReloadChunkSelector.updateTexture();
        GlStateManager.bindTexture(ThreadReloadChunkSelector.getTextureID());
        drawTexturedRect(posX, posY, GuiConfigs.CHUNK_SELECTOR_TILES_GUI * 16, GuiConfigs.CHUNK_SELECTOR_TILES_GUI * 16, Color4I.WHITE, 0D, 0D, GuiConfigs.CHUNK_SELECTOR_UV, GuiConfigs.CHUNK_SELECTOR_UV);

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

        int cx = MathUtils.chunk(mc.player.posX);
        int cy = MathUtils.chunk(mc.player.posZ);

        if (cx >= startX && cy >= startZ && cx < startX + GuiConfigs.CHUNK_SELECTOR_TILES_GUI && cy < startZ + GuiConfigs.CHUNK_SELECTOR_TILES_GUI) {
            double x = ((cx - startX) * 16D + MathUtils.wrap(mc.player.posX, 16D));
            double y = ((cy - startZ) * 16D + MathUtils.wrap(mc.player.posZ, 16D));

            GlStateManager.pushMatrix();
            GlStateManager.translate(posX + x, posY + y, 0D);
            /*GlStateManager.pushMatrix();
            //GlStateManager.rotate((int)((ep.rotationYaw + 180F) / (180F / 8F)) * (180F / 8F), 0F, 0F, 1F);
            GlStateManager.rotate(mc.player.rotationYaw + 180F, 0F, 0F, 1F);
            FTBLibClient.setTexture(GuiConfigs.TEX_ENTITY);
            GlStateManager.color(1F, 1F, 1F, mc.player.isSneaking() ? 0.4F : 0.7F);
            GuiHelper.drawTexturedRect(-8, -8, 16, 16, 0D, 0D, 1D, 1D);
            GlStateManager.popMatrix();*/
            drawPlayerHead(mc.player.getName(), -2, -2, 4, 4);
            GlStateManager.popMatrix();
        }

        GlStateManager.color(1F, 1F, 1F, 1F);

    }

    public static void drawPlayerHead(String username, int x, int y, int w, int h) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(FTBLibClient.getSkinTexture(username));
        drawTexturedRect(x, y, w, h, Color4I.WHITE, 0.125D, 0.125D, 0.25D, 0.25D);
        drawTexturedRect(x, y, w, h, Color4I.WHITE, 0.625D, 0.125D, 0.75D, 0.25D);
    }

    @Override
    public void mouseReleased(GuiBase gui) {
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

    private class MapButton extends Button
    {
        private final ChunkPos chunkPos;
        private final int index;
        private boolean isSelected = false;

        private MapButton(int x, int y, int i) {
            super(x, y, 16, 16);
            posX += (i % GuiConfigs.CHUNK_SELECTOR_TILES_GUI) * width;
            posY += (i / GuiConfigs.CHUNK_SELECTOR_TILES_GUI) * height;
            chunkPos = new ChunkPos(startX + (i % GuiConfigs.CHUNK_SELECTOR_TILES_GUI), startZ + (i / GuiConfigs.CHUNK_SELECTOR_TILES_GUI));
            index = i;
        }

        @Override
        public void onClicked(GuiBase gui, IMouseButton button) {
            int distance = scanner.getDist(chunkPos);
            NetworkHelper.instance.sendToServer(new MessageUpdateEnergyServer(scanner.getPos().getX(), scanner.getPos().getY(), scanner.getPos().getZ()));
            if (scanner.getMapping(chunkPos.chunkXPos, chunkPos.chunkZPos) != null || scanner.getEnergyStored(null) < Config.minEnergyPerChunkBiomeScanner * Config.increase * distance)
                return;
            if (scanner.type == 0 && distance > 2)
                return;
            else if (scanner.type == 1 && distance > 4)
                return;
            else if (scanner.type == 2 && distance > 8)
                return;

            scanner.container().extractEnergy(Config.minEnergyPerChunkBiomeScanner * Config.increase * distance, false);
            scanner.mapping.put(new ChunkPos(chunkPos.chunkXPos, chunkPos.chunkZPos), mc.world.getBiome(new BlockPos(chunkPos.chunkXPos * 16, 64, chunkPos.chunkZPos * 16)).getBiomeName());
            scanner.markDirty();
            NetworkHelper.instance.sendToServer(new MessageUpdateMap(scanner, chunkPos.chunkXPos, chunkPos.chunkZPos));

            GuiHelper.playClickSound();
            currentSelectionMode = 1;
        }

        @Override
        public void addMouseOverText(GuiBase gui, List<String> l) {
            int distance = scanner.getDist(chunkPos);
            if (scanner.getMapping(chunkPos.chunkXPos, chunkPos.chunkZPos) != null) {
                l.add(scanner.getMapping(chunkPos.chunkXPos, chunkPos.chunkZPos));
                l.add("(" + chunkPos.chunkXPos + ", " + chunkPos.chunkZPos + ")");
            } else {
                l.add("???");
                l.add("Click to scan!");
                l.add("Power cost: " + Config.minEnergyPerChunkBiomeScanner * Config.increase * distance);
                l.add("Distance (chunks): " + distance);
                if (scanner.type == 0 && distance > 2) {
                    l.add("Basic Biome Scanner cannot scan chunks more than 2 chunks away!");
                } else if (scanner.type == 1 && distance > 4) {
                    l.add("Advanced Biome Scanner cannot scan chunks more than 4 chunks away!");
                } else if (scanner.type == 2 && distance > 8) {
                    l.add("Elite Biome Scanner cannot scan chunks more than 8 chunks away!");
                }
            }

            if (GuiScreen.isCtrlKeyDown()) {
                l.add(chunkPos.toString());
            }
        }

        @Override
        public void renderWidget(GuiBase gui) {
            int ax = getAX();
            int ay = getAY();

            if (isSelected || gui.isMouseOver(this)) {
                GuiHelper.drawBlankRect(ax, ay, 16, 16, Color4I.WHITE_A33);
            }

            if (!isSelected && currentSelectionMode != -1 && isMouseOver(this)) {
                isSelected = true;
            }
        }
    }
}