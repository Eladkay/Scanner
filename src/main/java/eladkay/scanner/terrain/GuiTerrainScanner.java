package eladkay.scanner.terrain;

import eladkay.scanner.Config;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

/**
 * Things this should do:
 * Rotate chunk (i hate my life)
 * Map
 * Speedup (check!)
 * Stop/start (check!)
 */
public class GuiTerrainScanner extends GuiContainer {
    private static final ResourceLocation BACKGROUND = new ResourceLocation("scanner:textures/gui/standardBackground.png");
    private final TileEntityTerrainScanner scanner;
    private GuiButton rotate;
    private GuiButton toggleMode;
    private GuiSlider sliderSpeedup;
    public GuiTerrainScanner(TileEntityTerrainScanner scanner) {
        super(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer playerIn) {
                return true;
            }
        });
        this.scanner = scanner;
    }

    @Override
    public void initGui() {
        super.initGui();
        toggleMode = new GuiButton(0, (this.width / 2) - 75, this.height / 2 + 50, 150, 20, "");
        rotate = new GuiButton(1, (this.width / 2) - 75, this.height / 2 + 30, 150, 20, "");
        //noinspection Convert2Lambda
        sliderSpeedup = new GuiSlider(new GuiPageButtonList.GuiResponder() {
            @Override
            public void setEntryValue(int id, boolean value) {

            }

            @Override
            public void setEntryValue(int id, float value) {
                scanner.speedup = value < 1 ? 1 : (int) value;
            }

            @Override
            public void setEntryValue(int id, String value) {

            }
        }, 2, (this.width / 2) - 75, this.height / 2 + 10, "Speedup (Blocks/t)", 1f, Float.parseFloat(Config.maxSpeedup + ""), scanner.speedup, new GuiSlider.FormatHelper() {
            @Override
            public String getText(int id, String name, float value) {
                return name;
            }
        });

        buttonList.add(toggleMode);
        //buttonList.add(rotate); todo
        if (Config.maxSpeedup > 0)
            buttonList.add(sliderSpeedup);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == toggleMode) if (scanner.on) scanner.deactivate();
        else scanner.activate();
        else if (button == rotate) {
            scanner.rotation = scanner.rotation.getNext();
            if (scanner.on) scanner.deactivate();
            scanner.current.setPos(scanner.getPos().getX(), 0, scanner.getPos().getZ());
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        toggleMode.displayString = scanner.on ? "Turn off" : "Turn on";
        switch (scanner.rotation) {
            case POSX_POSZ:
                rotate.displayString = "Build on +x, +z";
                break;
            case POSX_NEGZ:
                rotate.displayString = "Build on +x, -z";
                break;
            case NEGX_POSZ:
                rotate.displayString = "Build on -x, +z";
                break;
            case NEGX_NEGZ:
                rotate.displayString = "Build on -x, -z";
                break;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString("Terrain Scanner", 50, 6, 4210752);
        this.fontRendererObj.drawString("Current: (" + scanner.current.getX() + ", " + scanner.current.getY() + ", " + scanner.current.getZ() + ")", 20, 20, 4210752);
        this.fontRendererObj.drawString("End: (" + scanner.getEnd().getX() + ", 256, " + scanner.getEnd().getZ() + ")", 20, 40, 4210752);
        if (mc.thePlayer.getName().matches("(?:Player\\d{1,3})|(?:Eladk[ae]y)"))
            this.fontRendererObj.drawString("Debug: (" + scanner.getPos().east().add(15, 255, 15).getX() + ", " + scanner.getPos().east().getY() + ", " + scanner.getPos().east().getZ() + ")", 20, 60, 4210752);
        if (Config.maxSpeedup > 0)
            this.fontRendererObj.drawString("Speedup (blocks per tick): " + scanner.speedup, 20, 80, 4210752);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BACKGROUND);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

    }
}
