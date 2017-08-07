package eladkay.scanner.biome;

import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.lib.MouseButton;
import com.feed_the_beast.ftbl.lib.gui.Button;
import com.feed_the_beast.ftbl.lib.gui.GuiBase;
import com.feed_the_beast.ftbl.lib.gui.GuiHelper;
import com.feed_the_beast.ftbl.lib.gui.GuiLang;
import com.feed_the_beast.ftbl.lib.gui.Panel;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiChunkSelectorBase;
import com.feed_the_beast.ftbl.lib.gui.misc.ThreadReloadChunkSelector;
import com.teamwizardry.librarianlib.common.network.PacketHandler;
import eladkay.scanner.Config;
import eladkay.scanner.misc.MessageUpdateEnergyServer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.Collection;
import java.util.List;

public class GuiBiomeScanner extends GuiChunkSelectorBase
{
    public static GuiBiomeScanner instance;
    private final Button buttonRefresh, buttonClose;
    private final TileEntityBiomeScanner scanner;

    public GuiBiomeScanner(TileEntityBiomeScanner scanner)
    {
        this.scanner = scanner;

        buttonClose = new Button(0, 0, 16, 16, GuiLang.BUTTON_CLOSE.textComponent().getFormattedText())
        {
            @Override
            public void onClicked(GuiBase gui, IMouseButton button)
            {
                GuiHelper.playClickSound();
                closeGui();
            }
        };

        buttonRefresh = new Button(0, 16, 16, 16, GuiLang.BUTTON_REFRESH.textComponent().getFormattedText())
        {
            @Override
            public void onClicked(GuiBase gui, IMouseButton button)
            {
                ThreadReloadChunkSelector.reloadArea(mc.world, startX, startZ);
            }
        };
    }

    @Override
    public void onInit()
    {
        buttonRefresh.onClicked(this, MouseButton.LEFT);
    }

    @Override
    public void onChunksSelected(Collection<ChunkPos> chunks)
    {
        ChunkPos pos = chunks.iterator().next();

        int distance = scanner.getDist(pos);
        PacketHandler.NETWORK.sendToServer(new MessageUpdateEnergyServer(scanner.getPos().getX(), scanner.getPos().getY(), scanner.getPos().getZ()));
        if(scanner.getMapping(pos.chunkXPos, pos.chunkZPos) != null || scanner.getEnergyStored(null) < Config.minEnergyPerChunkBiomeScanner * Config.increase * distance)
        {
            return;
        }
        if(distance > (1 << (scanner.type + 1)))
        {
            return;
        }

        scanner.getContainer().extractEnergy(Config.minEnergyPerChunkBiomeScanner * Config.increase * distance, false);
        scanner.mapping.put(new ChunkPos(pos.chunkXPos, pos.chunkZPos), mc.world.getBiome(new BlockPos(pos.chunkXPos << 4, 64, pos.chunkZPos << 4)).getBiomeName());
        scanner.markDirty();
        PacketHandler.NETWORK.sendToServer(new MessageUpdateMap(scanner, pos.chunkXPos, pos.chunkZPos));
    }

    @Override
    public void addCornerButtons(Panel panel)
    {
        panel.add(buttonClose);
        panel.add(buttonRefresh);
    }

    @Override
    public void addButtonText(MapButton button, List<String> list)
    {
        int distance = scanner.getDist(button.chunkPos);
        if(scanner.getMapping(button.chunkPos.chunkXPos, button.chunkPos.chunkZPos) != null)
        {
            list.add(scanner.getMapping(button.chunkPos.chunkXPos, button.chunkPos.chunkZPos));
            list.add("(" + button.chunkPos.chunkXPos + ", " + button.chunkPos.chunkZPos + ")");
        }
        else
        {
            list.add("???");
            list.add("Click to scan!");
            list.add("Power cost: " + Config.minEnergyPerChunkBiomeScanner * Config.increase * distance);
            list.add("Distance (chunks): " + distance);
            if(scanner.type == 0 && distance > 2)
            {
                list.add("Basic Biome Scanner cannot scan chunks more than 2 chunks away!");
            }
            else if(scanner.type == 1 && distance > 4)
            {
                list.add("Advanced Biome Scanner cannot scan chunks more than 4 chunks away!");
            }
            else if(scanner.type == 2 && distance > 8)
            {
                list.add("Elite Biome Scanner cannot scan chunks more than 8 chunks away!");
            }
        }

        if(GuiScreen.isCtrlKeyDown())
        {
            list.add(button.chunkPos.toString());
        }
    }
}