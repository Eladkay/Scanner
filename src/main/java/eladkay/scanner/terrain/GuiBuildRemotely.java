package eladkay.scanner.terrain;

import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.lib.Color4I;
import com.feed_the_beast.ftbl.lib.MouseButton;
import com.feed_the_beast.ftbl.lib.gui.Button;
import com.feed_the_beast.ftbl.lib.gui.GuiBase;
import com.feed_the_beast.ftbl.lib.gui.GuiHelper;
import com.feed_the_beast.ftbl.lib.gui.GuiLang;
import com.feed_the_beast.ftbl.lib.gui.Panel;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiConfigs;
import com.feed_the_beast.ftbl.lib.gui.misc.ThreadReloadChunkSelector;
import com.feed_the_beast.ftbl.lib.math.MathHelperLM;
import eladkay.scanner.Config;
import eladkay.scanner.biome.GuiBiomeScanner;
import eladkay.scanner.misc.MessageUpdateEnergyServer;
import eladkay.scanner.misc.NetworkHelper;
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

public class GuiBuildRemotely extends GuiBase
{
    private static final Color4I COL_GREEN = new Color4I(0x6600FF00);
    public static GuiBuildRemotely instance;
    public final int startX, startZ;
    private final Button buttonRefresh, buttonClose;
    private final MapButton mapButtons[];
    private final Panel panelButtons;
    private final TileEntityTerrainScanner scanner;
    private byte currentSelectionMode = -1;

    public GuiBuildRemotely(TileEntityTerrainScanner scanner) {
        super(GuiConfigs.CHUNK_SELECTOR_TILES_GUI * 16, GuiConfigs.CHUNK_SELECTOR_TILES_GUI * 16);

        this.scanner = scanner;

        startX = MathHelperLM.chunk(mc.player.posX) - 7;
        startZ = MathHelperLM.chunk(mc.player.posZ) - 7;

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
                return height - 16;
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

        GlStateManager.color(1F, 1F, 1F, 1F);
        GuiHelper.drawBlankRect(posX - 2, posY - 2, width + 4, height + 4, Color4I.BLACK);
        //drawBlankRect((xSize - 128) / 2, (ySize - 128) / 2, zLevel, 128, 128);

        ThreadReloadChunkSelector.updateTexture();
        GlStateManager.bindTexture(ThreadReloadChunkSelector.getTextureID());
        GuiHelper.drawTexturedRect(posX, posY, GuiConfigs.CHUNK_SELECTOR_TILES_GUI * 16, GuiConfigs.CHUNK_SELECTOR_TILES_GUI * 16, Color4I.WHITE, 0D, 0D, GuiConfigs.CHUNK_SELECTOR_UV, GuiConfigs.CHUNK_SELECTOR_UV);

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

        int cx = MathHelperLM.chunk(mc.player.posX);
        int cy = MathHelperLM.chunk(mc.player.posZ);

        if (cx >= startX && cy >= startZ && cx < startX + GuiConfigs.CHUNK_SELECTOR_TILES_GUI && cy < startZ + GuiConfigs.CHUNK_SELECTOR_TILES_GUI) {
            double x = ((cx - startX) * 16D + MathHelperLM.wrap(mc.player.posX, 16D));
            double y = ((cy - startZ) * 16D + MathHelperLM.wrap(mc.player.posZ, 16D));

            GlStateManager.pushMatrix();
            GlStateManager.translate(posX + x, posY + y, 0D);
            /*GlStateManager.pushMatrix();
            //GlStateManager.rotate((int)((ep.rotationYaw + 180F) / (180F / 8F)) * (180F / 8F), 0F, 0F, 1F);
            GlStateManager.rotate(mc.player.rotationYaw + 180F, 0F, 0F, 1F);
            FTBLibClient.setTexture(GuiConfigs.TEX_ENTITY);
            GlStateManager.color(1F, 1F, 1F, mc.player.isSneaking() ? 0.4F : 0.7F);
            GuiHelper.drawTexturedRect(-8, -8, 16, 16, 0D, 0D, 1D, 1D);
            GlStateManager.popMatrix();*/
            GuiBiomeScanner.drawPlayerHead(mc.player.getName(), -2, -2, 4, 4);
            GlStateManager.popMatrix();
        }

        GlStateManager.color(1F, 1F, 1F, 1F);

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
            //do stuff
            NetworkHelper.instance.sendToServer(new MessageUpdateEnergyServer(scanner.getPos().getX(), scanner.getPos().getY(), scanner.getPos().getZ()));
            if (scanner.getEnergyStored(null) < Config.remoteBuildCost)
                return;

            scanner.container().extractEnergy(Config.remoteBuildCost, false);
            scanner.on = false;
            scanner.posStart = new BlockPos(chunkPos.chunkXPos * 16, 0, chunkPos.chunkZPos * 16);
            scanner.current = new BlockPos.MutableBlockPos(0, -1, 0);
            scanner.markDirty();
            NetworkHelper.instance.sendToServer(new MessageUpdateScanner(scanner));

            GuiHelper.playClickSound();
            currentSelectionMode = 1;
        }

        @Override
        public void addMouseOverText(GuiBase gui, List<String> l) {
            l.add("Click to scan!");
            l.add("Power cost: " + Config.remoteBuildCost);
            l.add(chunkPos.toString());
            NetworkHelper.instance.sendToServer(new MessageUpdateEnergyServer(scanner.getPos().getX(), scanner.getPos().getY(), scanner.getPos().getZ()));
            if (scanner.posStart != null && scanner.posStart.getX() == chunkPos.chunkXPos * 16 && scanner.posStart.getZ() == chunkPos.chunkZPos * 16)
                l.add("Already building!");
            else if (scanner.getEnergyStored(null) < Config.remoteBuildCost) l.add("Insufficient power!");
        }

        @Override
        public void renderWidget(GuiBase gui) {
            int ax = getAX();
            int ay = getAY();

            if ((isSelected || gui.isMouseOver(this)) && !(scanner.posStart != null && scanner.posStart.getX() == chunkPos.chunkXPos * 16 && scanner.posStart.getZ() == chunkPos.chunkZPos * 16)) {
                GuiHelper.drawBlankRect(ax, ay, 16, 16, Color4I.WHITE_A33);
            } else if (scanner.posStart != null && scanner.posStart.getX() == chunkPos.chunkXPos * 16 && scanner.posStart.getZ() == chunkPos.chunkZPos * 16) {
                GuiHelper.drawBlankRect(ax, ay, 16, 16, COL_GREEN);
            }

            if (!isSelected && currentSelectionMode != -1 && isMouseOver(this)) {
                isSelected = true;
            }
        }
    }
}