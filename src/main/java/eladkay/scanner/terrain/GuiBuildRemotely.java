package eladkay.scanner.terrain;

import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.lib.Color4I;
import com.feed_the_beast.ftbl.lib.MouseButton;
import com.feed_the_beast.ftbl.lib.gui.Button;
import com.feed_the_beast.ftbl.lib.gui.GuiBase;
import com.feed_the_beast.ftbl.lib.gui.GuiHelper;
import com.feed_the_beast.ftbl.lib.gui.GuiLang;
import com.feed_the_beast.ftbl.lib.gui.Panel;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiChunkSelectorBase;
import com.feed_the_beast.ftbl.lib.gui.misc.ThreadReloadChunkSelector;
import eladkay.scanner.Config;
import eladkay.scanner.misc.MessageUpdateEnergyServer;
import eladkay.scanner.misc.NetworkHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.Collection;
import java.util.List;

public class GuiBuildRemotely extends GuiChunkSelectorBase
{
    private static final Color4I COL_GREEN = new Color4I(false, 0x6600FF00);
    public static GuiBuildRemotely instance;
    private final Button buttonRefresh, buttonClose;
    private final TileEntityTerrainScanner scanner;

    public GuiBuildRemotely(TileEntityTerrainScanner scanner)
    {
        this.scanner = scanner;

        buttonClose = new Button(0, 0, 16, 16, GuiLang.BUTTON_CLOSE.translate())
        {
            @Override
            public void onClicked(GuiBase gui, IMouseButton button)
            {
                GuiHelper.playClickSound();
                closeGui();
            }
        };

        buttonRefresh = new Button(0, 16, 16, 16, GuiLang.BUTTON_REFRESH.translate())
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

        //do stuff
        NetworkHelper.instance.sendToServer(new MessageUpdateEnergyServer(scanner.getPos().getX(), scanner.getPos().getY(), scanner.getPos().getZ()));
        if(scanner.getEnergyStored(null) < Config.remoteBuildCost)
        {
            return;
        }

        scanner.container().extractEnergy(Config.remoteBuildCost, false);
        scanner.on = false;
        scanner.posStart = new BlockPos(pos.chunkXPos << 4, 0, pos.chunkZPos << 4);
        scanner.current = new BlockPos.MutableBlockPos(0, -1, 0);
        scanner.markDirty();
        NetworkHelper.instance.sendToServer(new MessageUpdateScanner(scanner));
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
        list.add("Click to scan!");
        list.add("Power cost: " + Config.remoteBuildCost);
        list.add(button.chunkPos.toString());
        NetworkHelper.instance.sendToServer(new MessageUpdateEnergyServer(scanner.getPos().getX(), scanner.getPos().getY(), scanner.getPos().getZ()));
        if(scanner.posStart != null && scanner.posStart.getX() >> 4 == button.chunkPos.chunkXPos && scanner.posStart.getZ() >> 4 == button.chunkPos.chunkZPos)
        {
            list.add("Already building!");
        }
        else if(scanner.getEnergyStored(null) < Config.remoteBuildCost)
        {
            list.add("Insufficient power!");
        }
    }

    /*
    @Override
    public void renderWidget(GuiBase gui)
    {
        int ax = getAX();
        int ay = getAY();

        if((isSelected || gui.isMouseOver(this)) && !(scanner.posStart != null && scanner.posStart.getX() == chunkPos.chunkXPos * 16 && scanner.posStart.getZ() == chunkPos.chunkZPos * 16))
        {
            GuiHelper.drawBlankRect(ax, ay, 16, 16, Color4I.WHITE_A33);
        }
        else if(scanner.posStart != null && scanner.posStart.getX() == chunkPos.chunkXPos * 16 && scanner.posStart.getZ() == chunkPos.chunkZPos * 16)
        {
            GuiHelper.drawBlankRect(ax, ay, 16, 16, COL_GREEN);
        }

        if(!isSelected && currentSelectionMode != -1 && isMouseOver(this))
        {
            isSelected = true;
        }
    }
    */
}