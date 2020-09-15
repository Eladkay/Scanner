package eladkay.scanner.terrain;

import eladkay.scanner.Config;
import eladkay.scanner.init.ModBlocks;
import eladkay.scanner.misc.MessageUpdateEnergyServer;
import eladkay.scanner.misc.NetworkHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public class GuiTerrainScanner extends GuiContainer {
    private static final ResourceLocation BACKGROUND = new ResourceLocation("scanner:textures/gui/standard_background.png");
    private final TileEntityTerrainScanner scanner;
    private GuiButton rotate; // todo
    private GuiButton toggleMode;
    private GuiSlider sliderSpeedup;
    private GuiButton showMap;

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
        toggleMode = new GuiButton(0, (this.width / 2) - 75, this.height / 2 + 40, 150, 20, "");
        rotate = new GuiButton(1, (this.width / 2) - 75, this.height / 2, 150, 20, "");
        //noinspection Convert2Lambda
        sliderSpeedup = new GuiSlider(new GuiPageButtonList.GuiResponder() {
            @Override
            public void setEntryValue(int id, boolean value) {

            }

            @Override
            public void setEntryValue(int id, float value) {
                scanner.speedup = value < 1 ? 1 : (int) value;
                scanner.markDirty();
                MessageUpdateScanner.send(scanner);
            }

            @Override
            public void setEntryValue(int id, String value) {

            }
        }, 2, (this.width / 2) - 75, this.height / 2 + 10, I18n.format("speedInBlocksPerTick"), 1f, Float.parseFloat(Config.maxSpeedup + ""), scanner.speedup, new GuiSlider.FormatHelper() {
            @Override
            public String getText(int id, String name, float value) {
                return name + ": " + (int) value;
            }
        });
        showMap = new GuiButton(3, (this.width / 2) - 75, this.height / 2 - 15, 150, 20, I18n.format("gui.showMap")); //Build elsewhere (Requires adjacent ultimate biome scanner)
        buttonList.add(toggleMode);
        buttonList.add(showMap);
        //buttonList.add(rotate); todo
        if (Config.maxSpeedup > 0)
            buttonList.add(sliderSpeedup);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == toggleMode) {
            if (scanner.on) scanner.deactivate();
            else scanner.activate();
            NetworkHelper.instance.sendToServer(new MessageUpdateEnergyServer(scanner.getPos().getX(), scanner.getPos().getY(), scanner.getPos().getZ()));
            MessageUpdateScanner.send(scanner);
        } else if (button == rotate) {
            scanner.rotation = scanner.rotation.getNext();
            if (scanner.on) scanner.deactivate();
            scanner.current.setPos(scanner.getPos().getX(), 0, scanner.getPos().getZ());
            MessageUpdateScanner.send(scanner);
        } else if (button == showMap) new GuiBuildRemotely(scanner).openGui();
    }

    private static final ResourceLocation ENERGY_BAR = new ResourceLocation("scanner:textures/gui/bar.png");

    // todo, why am i not using this?
    public void drawMultiEnergyBar(int x, int y, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(ENERGY_BAR);
        int energyStored = scanner.getEnergyStored(null);
        int maxEnergyStored = scanner.getMaxEnergyStored(null);

        drawTexturedModalRect(x, y, -15, -1, 14, 50);

        int draw = (int) ((double) energyStored / (double) maxEnergyStored * (48));
        drawTexturedModalRect(x + 1, y + 49 - draw, 0, 48 - draw, 12, draw);

        if (isInRect(x + 1, y + 1, 11, 48, mouseX, mouseY)) {
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.colorMask(true, true, true, false);
            GuiUtils.drawGradientRect(0, x + 1, y + 1, x + 13, y + 49, 0x80FFFFFF, 0x80FFFFFF);
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.enableDepth();

            List<String> list = new ArrayList<>();
            list.add(TextFormatting.GOLD + "" + energyStored + "/" + maxEnergyStored + " RF");
            if (isShiftKeyDown()) {
                int percentage = (energyStored / maxEnergyStored) * 100;
                TextFormatting color;
                if (percentage <= 10) {
                    color = TextFormatting.RED;
                } else if (percentage >= 75) {
                    color = TextFormatting.GREEN;
                } else {
                    color = TextFormatting.YELLOW;
                }
                list.add(color + "" + percentage + "%" + TextFormatting.GRAY + " " + I18n.format("gui.charged"));
            }
            GuiUtils.drawHoveringText(list, mouseX, mouseY, width, height, -1, mc.fontRenderer);
            GlStateManager.disableLighting();
        }
    }

    private static boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY) {
        return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        try {
            if (scanner == null || toggleMode == null || showMap == null) return;
            toggleMode.displayString = scanner.on ? I18n.format("gui.turnOn") : I18n.format("gui.turnOff");
            boolean flag = false;
            for (EnumFacing facing : EnumFacing.values())
                if (mc.world.getBlockState(scanner.getPos().offset(facing)).getBlock() == ModBlocks.biomeScannerUltimate) {
                    flag = true;
                }
            showMap.visible = flag;
            switch (scanner.rotation) { // I'll I18n this when I implement this
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
            super.drawDefaultBackground();
            super.drawScreen(mouseX, mouseY, partialTicks);
        } catch (Exception idky) {
            //
        }
    }

    public void drawCenteredString(String text, int x, int y, int color) {
        fontRenderer.drawString(text, x - fontRenderer.getStringWidth(text) / 2, y, color);
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        NetworkHelper.instance.sendToServer(new MessageUpdateEnergyServer(scanner.getPos().getX(), scanner.getPos().getY(), scanner.getPos().getZ()));
        if (mc.isSingleplayer()) drawCenteredString(I18n.format("tile.terrainScanner.name"), 90, 6, 4210752); //
        else {
            drawCenteredString(I18n.format("tile.terrainScanner.name"), 90, 6, 4210752);
            drawCenteredString(I18n.format("gui.enderIOWarningL1"), 90, 13, 4210752);
            drawCenteredString(I18n.format("gui.enderIOWarningL2"), 90, 20, 4210752);
        }
        if (!"(0, -1, 0)".equals("(" + scanner.current.getX() + ", " + scanner.current.getY() + ", " + scanner.current.getZ() + ")"))
            drawCenteredString(I18n.format("gui.current") + " (" + scanner.current.getX() + ", " + scanner.current.getY() + ", " + scanner.current.getZ() + ")", 90, 35, 4210752);
        else drawCenteredString(I18n.format("gui.currentBlockOff"), 90, 35, 4210752);
        drawCenteredString(I18n.format("gui.endBlock") + " (" + scanner.getEnd().getX() + ", " + scanner.maxY + ", " + scanner.getEnd().getZ() + ")", 90, 45, 4210752);
        if (scanner.posStart != null)
            drawCenteredString(I18n.format("gui.remoteStart") + " (" + scanner.posStart.getX() + ", " + scanner.posStart.getZ() + ")", 90, 55, 4210752);
        boolean flag = false;
        for (EnumFacing facing : EnumFacing.values())
            if (mc.world.getBlockState(scanner.getPos().offset(facing)).getBlock() == ModBlocks.biomeScannerUltimate)
                flag = true;
        if (!flag) {
            this.fontRenderer.drawString(I18n.format("gui.ultimateBSL1"), 15, 65, 4210752);
            this.fontRenderer.drawString(I18n.format("gui.ultimateBSL2"), 30, 75, 4210752);
        }

        if (scanner.queue != null)
            this.fontRenderer.drawString(I18n.format("gui.queueAttached"), 30, 150, 4210752);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BACKGROUND);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }
}