package eladkay.scanner.biome;

import com.feed_the_beast.ftblib.lib.client.CachedVertexData;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import com.feed_the_beast.ftblib.lib.gui.*;
import com.feed_the_beast.ftblib.lib.gui.misc.*;
import com.feed_the_beast.ftblib.lib.math.MathUtils;
import eladkay.scanner.Config;
import eladkay.scanner.init.ModBlocks;
import eladkay.scanner.misc.MessageUpdateEnergyServer;
import eladkay.scanner.misc.NetworkHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiScreen;
import static eladkay.scanner.ScannerMod.logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class GuiBiomeScanner extends GuiChunkSelectorBase
{
    protected enum Corner
    {
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        TOP_LEFT
    }

    public static final int TILE_SIZE = 12;
    private static final CachedVertexData GRID = new CachedVertexData(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

    static
    {
        GRID.color.set(128, 128, 128, 50);

        for (int x = 0; x <= ChunkSelectorMap.TILES_GUI - 1; x++)
        {
            GRID.pos(x * TILE_SIZE, 0D);
            GRID.pos(x * TILE_SIZE, (ChunkSelectorMap.TILES_GUI * TILE_SIZE), 0D);
        }

        for (int y = 0; y <= ChunkSelectorMap.TILES_GUI - 1; y++)
        {
            GRID.pos(0D, y * TILE_SIZE, 0D);
            GRID.pos((ChunkSelectorMap.TILES_GUI * TILE_SIZE), y * TILE_SIZE, 0D);
        }
    }

    public class MapButton extends Button
    {
        public final GuiBiomeScanner gui;
        public final ChunkPos chunkPos;
        public final int index;
        private boolean isSelected = false;

        //todo make a better function for this, it looks messy.
        public int calcDist(ChunkPos a) {
            int arg1;
            int arg2;
            int fixposx = (int)(scanner.getPos().getX()/16.0);
            int fixposz = (int)(scanner.getPos().getZ()/16.0);
            int arg3;
            arg1 = a.x - fixposx;
            arg2 = a.z - fixposz;
            arg1 *= arg1;
            arg2 *= arg2;
            arg3 = (int)MathHelper.sqrt(arg1+arg2);
            return arg3;

        }

        private MapButton(GuiBiomeScanner g, int i)
        {
            super(g);
            gui = g;
            index = i;
            setPosAndSize(((index % ChunkSelectorMap.TILES_GUI) * TILE_SIZE), ((index / ChunkSelectorMap.TILES_GUI) * TILE_SIZE), TILE_SIZE, TILE_SIZE);
            chunkPos = new ChunkPos(gui.startX + (i % ChunkSelectorMap.TILES_GUI), gui.startZ + (i / ChunkSelectorMap.TILES_GUI));
        }

        @Override
        public void onClicked(MouseButton button)
        {
            int distance = calcDist(chunkPos);
            NetworkHelper.instance.sendToServer(new MessageUpdateEnergyServer(scanner.getPos().getX(), scanner.getPos().getY(), scanner.getPos().getZ()));
            if (scanner.getMapping(chunkPos.x, chunkPos.z) != null || scanner.getEnergyStored(null) < Config.minEnergyPerChunkBiomeScanner * Config.increase * distance)
                return;
            if (scanner.getBlockType() == ModBlocks.biomeScannerBasic && distance > 2)
                return;
            else if (scanner.getBlockType() == ModBlocks.biomeScannerAdv && distance > 4)
                return;
            else if (scanner.getBlockType() == ModBlocks.biomeScannerElite && distance > 8)
                return;

            World scannerWorld = scanner.getWorld();

            scanner.container.extractEnergy(Config.minEnergyPerChunkBiomeScanner * Config.increase * distance, false);
            scanner.mapping.put(new ChunkPos(chunkPos.x, chunkPos.z), scannerWorld.getBiome(new BlockPos(chunkPos.x * 16, 64, chunkPos.z * 16)).getBiomeName());
            scanner.markDirty();
            NetworkHelper.instance.sendToServer(new MessageUpdateMap(scanner, chunkPos.x, chunkPos.z));

            GuiHelper.playClickSound();
            currentSelectionMode = 1;

        }

        @Override
        public void addMouseOverText(List<String> l)
        {
            //int distance = scanner.getDist(chunkPos);

            int distance = calcDist(chunkPos);

            if (scanner.getMapping(chunkPos.x, chunkPos.z) != null) {
                l.add(scanner.getMapping(chunkPos.x, chunkPos.z));
                l.add("(" + chunkPos.x + ", " + chunkPos.z + ")");
            } else {
                l.add("???");
                l.add("Click to scan!");
                l.add("Power cost: " + Config.minEnergyPerChunkBiomeScanner * Config.increase * distance);
                l.add("Distance (chunks): " + distance);
                if (scanner.getBlockType() == ModBlocks.biomeScannerBasic && distance > 2) {
                    l.add("Basic Biome Scanner cannot scan chunks more than 2 chunks away!");
                } else if (scanner.getBlockType() == ModBlocks.biomeScannerAdv && distance > 4) {
                    l.add("Advanced Biome Scanner cannot scan chunks more than 4 chunks away!");
                } else if (scanner.getBlockType() == ModBlocks.biomeScannerElite && distance > 8) {
                    l.add("Elite Biome Scanner cannot scan chunks more than 8 chunks away!");
                }
            }

            if (GuiScreen.isCtrlKeyDown()) {
                l.add(chunkPos.toString());
            }

        }

        @Override
        public void draw(Theme theme, int x, int y, int w, int h)
        {
            if (!isSelected && gui.currentSelectionMode != -1 && gui.isMouseOver(this))
            {
                isSelected = true;
            }

            if (isSelected || gui.isMouseOver(this))
            {
                Color4I.WHITE.withAlpha(33).draw(x, y, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    public int startX, startZ;
    private final MapButton[] mapButtons;
    private final Panel panelButtons;
    private final TileEntityBiomeScanner scanner;
    public int currentSelectionMode = -1;

    public GuiBiomeScanner(TileEntityBiomeScanner scanner)
    {

        this.scanner = scanner;

        startX = MathUtils.chunk(Minecraft.getMinecraft().player.posX) - ChunkSelectorMap.TILES_GUI2;
        startZ = MathUtils.chunk(Minecraft.getMinecraft().player.posZ) - ChunkSelectorMap.TILES_GUI2;

        panelButtons = new Panel(this)
        {
            @Override
            public void addWidgets()
            {
                addCornerButtons(panelButtons);
            }

            @Override
            public void alignWidgets()
            {
                int h = align(WidgetLayout.VERTICAL);
                int w = 0;

                for (Widget widget : widgets)
                {
                    w = Math.max(w, widget.width);
                }

                panelButtons.setPosAndSize(getGui().width + 2, -2, w, h);
            }
        };

        mapButtons = new MapButton[ChunkSelectorMap.TILES_GUI * ChunkSelectorMap.TILES_GUI];

        for (int i = 0; i < mapButtons.length; i++)
        {
            mapButtons[i] = new MapButton(this, i);
        }
    }

    @Override
    public boolean onInit()
    {
        ChunkSelectorMap.getMap().resetMap(startX, startZ);
        return true;
    }

    @Override
    public void addWidgets()
    {
        for (MapButton b : mapButtons)
        {
            add(b);
        }

        add(panelButtons);
    }

    @Override
    public void alignWidgets()
    {
        setSize((ChunkSelectorMap.TILES_GUI * TILE_SIZE), (ChunkSelectorMap.TILES_GUI * TILE_SIZE));
        panelButtons.alignWidgets();
    }

    @Override
    public void drawBackground(Theme theme, int x, int y, int w, int h)
    {
        int currentStartX = MathUtils.chunk(Minecraft.getMinecraft().player.posX) - ChunkSelectorMap.TILES_GUI2;
        int currentStartZ = MathUtils.chunk(Minecraft.getMinecraft().player.posZ) - ChunkSelectorMap.TILES_GUI2;

        if (currentStartX != startX || currentStartZ != startZ)
        {
            startX = currentStartX;
            startZ = currentStartZ;

            for (int i = 0; i < mapButtons.length; i++)
            {
                mapButtons[i] = new MapButton(this, i);
            }

            ChunkSelectorMap.getMap().resetMap(startX, startZ);
        }

        GlStateManager.color(1F, 1F, 1F, 1F);
        Color4I.BLACK.draw(x - 2, y - 2, w + 4, h + 4);

        ChunkSelectorMap.getMap().drawMap(this, x, y, startX, startZ);

        GlStateManager.color(1F, 1F, 1F, 1F);

        for (MapButton mapButton : mapButtons)
        {
            mapButton.draw(theme, mapButton.getX(), mapButton.getY(), mapButton.width, mapButton.height);
        }

        GlStateManager.disableTexture2D();
        GlStateManager.glLineWidth(1F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.setTranslation(mapButtons[0].getX(), mapButtons[0].getY(), 0D);
        //GlStateManager.color(1F, 1F, 1F, GuiScreen.isCtrlKeyDown() ? 0.2F : 0.7F);
        GlStateManager.color(1F, 1F, 1F, 1F);

        if (!isKeyDown(Keyboard.KEY_TAB))
        {
            drawArea(tessellator, buffer);
        }

        GRID.draw(tessellator, buffer);
        buffer.setTranslation(0D, 0D, 0D);
        GlStateManager.enableTexture2D();
        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    @Override
    public void mouseReleased(MouseButton button)
    {
        super.mouseReleased(button);

        if (currentSelectionMode != -1)
        {
            Collection<ChunkPos> c = new ArrayList<>();

            for (MapButton b : mapButtons)
            {
                if (b.isSelected)
                {
                    c.add(b.chunkPos);
                    b.isSelected = false;
                }
            }

            onChunksSelected(c);
            currentSelectionMode = -1;
        }
    }

    @Override
    public void drawForeground(Theme theme, int x, int y, int w, int h)
    {
        int lineSpacing = theme.getFontHeight() + 1;
        List<String> tempTextList = new ArrayList<>();
        addCornerText(tempTextList, Corner.BOTTOM_RIGHT);

        for (int i = 0; i < tempTextList.size(); i++)
        {
            String s = tempTextList.get(i);
            theme.drawString(s, getScreen().getScaledWidth() - theme.getStringWidth(s) - 2, getScreen().getScaledHeight() - (tempTextList.size() - i) * lineSpacing, Theme.SHADOW);
        }

        tempTextList.clear();

        addCornerText(tempTextList, Corner.BOTTOM_LEFT);

        for (int i = 0; i < tempTextList.size(); i++)
        {
            theme.drawString(tempTextList.get(i), 2, getScreen().getScaledHeight() - (tempTextList.size() - i) * lineSpacing, Theme.SHADOW);
        }

        tempTextList.clear();

        addCornerText(tempTextList, Corner.TOP_LEFT);

        for (int i = 0; i < tempTextList.size(); i++)
        {
            theme.drawString(tempTextList.get(i), 2, 2 + i * lineSpacing, Theme.SHADOW);
        }

        super.drawForeground(theme, x, y, w, h);
    }

    public int getSelectionMode(MouseButton button)
    {
        return -1;
    }

    public void onChunksSelected(Collection<ChunkPos> chunks)
    {
    }

    public void drawArea(Tessellator tessellator, BufferBuilder buffer)
    {
    }

    public void addCornerButtons(Panel panel)
    {
    }

    public void addCornerText(List<String> list, Corner corner)
    {
    }

    public void addButtonText(MapButton button, List<String> list)
    {
    }
}