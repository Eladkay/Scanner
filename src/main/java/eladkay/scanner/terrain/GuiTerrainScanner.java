package eladkay.scanner.terrain;

import eladkay.scanner.misc.NetworkHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

/**
 * Things this should do:
 * Rotate chunk
 * Map
 * Speedup
 * Stop/start
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
        toggleMode = new GuiButton(0, 85, 15, scanner.on ? "On" : "Off") {
            @Override
            public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                displayString = scanner.on ? "Off" : "On";
                scanner.changeState(!scanner.on);
                if (scanner.on) {
                    if (scanner.pos == null) scanner.pos = scanner.getPos();
                    if (scanner.current.getY() < 0)
                        scanner.current.setPos(scanner.pos.getX() + 1, 0, scanner.pos.getZ());
                } else scanner.current.setPos(scanner.pos.getX() + 1, -1, scanner.pos.getY());
                NetworkHelper.instance.sendToServer(new MessageUpdateState(!scanner.on, scanner.getPos().getX(), scanner.getPos().getY(), scanner.getPos().getZ()));
                return super.mousePressed(mc, mouseX, mouseY);
            }
        };
        buttonList.add(toggleMode);
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
