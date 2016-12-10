package eladkay.scanner.terrain;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

/**
 * Things this should do:
 * Rotate chunk
 * Map
 * Speedup
 * Stop/start (check!)
 */
public class GuiTerrainScanner extends GuiContainer {
    private static final ResourceLocation BACKGROUND = new ResourceLocation("scanner:textures/gui/standardBackground.png");
    private final TileEntityTerrainScanner scanner;
    private GuiButton rotate;
    private GuiButton toggleMode;

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
        buttonList.add(toggleMode);
        buttonList.add(rotate);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == toggleMode) if (scanner.on) scanner.deactivate();
        else scanner.activate();
        else if (button == rotate) {
            scanner.rotation = scanner.rotation.getNext();
            if (scanner.on) scanner.deactivate();
            scanner.current.setPos(scanner.pos.getX() + (scanner.rotation.x > 0 ? 1 : -1), 0, scanner.pos.getZ());
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
        drawCenteredString(fontRendererObj, scanner.current.toString(), 50, 20, 4210752);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString("Terrain Scanner", 50, 6, 4210752);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BACKGROUND);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

    }
}
