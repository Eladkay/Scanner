package eladkay.scanner.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import eladkay.scanner.ScannerConfig;
import eladkay.scanner.client.container.TerrainScannerContainer;
import eladkay.scanner.init.ModBlocks;
import eladkay.scanner.networking.MessageUpdateEnergyServer;
import eladkay.scanner.networking.NetworkHelper;
import eladkay.scanner.networking.MessageUpdateScanner;
import eladkay.scanner.tiles.TileEntityTerrainScanner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Direction;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.Slider;

import java.util.ArrayList;
import java.util.List;

public class GuiTerrainScanner extends ContainerScreen<TerrainScannerContainer> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation("scanner:textures/gui/standard_background.png");
    private final TileEntityTerrainScanner scanner;
    private Button toggleButton;
    private Button rotateButton;
    private Button showMapButton;

    public GuiTerrainScanner(TerrainScannerContainer scannerContainer, PlayerInventory playerInventory, ITextComponent title) {
        super(scannerContainer, playerInventory, new StringTextComponent(""));
        this.scanner = scannerContainer.scanner;
    }

    @Override
    public void init() {
        super.init();
        toggleButton = new Button((this.width / 2) - 75, this.height / 2 + 40, 150, 20, new StringTextComponent(""), (button) -> {
            if (scanner.powered) scanner.deactivate();
            else scanner.activate();
            NetworkHelper.INSTANCE.sendToServer(new MessageUpdateEnergyServer(scanner.getBlockPos().getX(), scanner.getBlockPos().getY(), scanner.getBlockPos().getZ()));
            MessageUpdateScanner.send(scanner);
        });
        this.addButton(toggleButton);

        //TODO: rotate button (idk what this is but yeah)
        rotateButton = new Button((this.width / 2) - 75, this.height / 2, 150, 20, new StringTextComponent(""), (button) -> {
            scanner.rotation = scanner.rotation.getNext();
            if (scanner.powered) scanner.deactivate();
            scanner.current.set(scanner.getBlockPos().getX(), 0, scanner.getBlockPos().getZ());
            MessageUpdateScanner.send(scanner);
        });

        showMapButton = new Button((this.width / 2) - 75, this.height / 2 - 15, 150, 20, new TranslationTextComponent("gui.scanner.showMap"), (button) -> {
            new GuiBuildRemotely(scanner).openGui();
        });
        this.addButton(showMapButton); //Build elsewhere (Requires adjacent ultimate biome scanner)

        if (ScannerConfig.CONFIG.maxSpeedup.get() > 0) {
            this.addButton(new Slider((this.width / 2) - 75, this.height / 2 + 10, new TranslationTextComponent("gui.scanner.speedInBlocksPerTick").append(":"), 1D, (double) ScannerConfig.CONFIG.maxSpeedup.get(), (double)scanner.speedup, (button) -> {

            }, (slider) -> {
                scanner.speedup = (int) Math.round(slider.sliderValue * 8);
                scanner.setChanged();
                MessageUpdateScanner.send(scanner);
            }));
        }
    }

    private static final ResourceLocation ENERGY_BAR = new ResourceLocation("scanner:textures/gui/bar.png");

    // todo, why am i not using this?
    public void drawMultiEnergyBar(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY) {
        Minecraft.getInstance().getTextureManager().bind(ENERGY_BAR);
        int energyStored = scanner.getEnergyStored();
        int maxEnergyStored = scanner.getMaxEnergyStored();

        GuiUtils.drawTexturedModalRect(matrixStack, x, y, -15, -1, 14, 50, 10);

        int draw = (int) ((double) energyStored / (double) maxEnergyStored * (48));
        GuiUtils.drawTexturedModalRect(matrixStack, x + 1, y + 49 - draw, 0, 48 - draw, 12, draw, 10);

        if (isInRect(x + 1, y + 1, 11, 48, mouseX, mouseY)) {
            RenderSystem.disableLighting();
            RenderSystem.disableDepthTest();
            RenderSystem.colorMask(true, true, true, false);
            GuiUtils.drawGradientRect(matrixStack.last().pose(), 0, x + 1, y + 1, x + 13, y + 49, 0x80FFFFFF, 0x80FFFFFF);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.enableDepthTest();

            List<ITextProperties> list = new ArrayList<>();
            list.add(new StringTextComponent(TextFormatting.GOLD + "" + energyStored + "/" + maxEnergyStored + " RF"));
            if (hasShiftDown()) {
                int percentage = (energyStored / maxEnergyStored) * 100;
                TextFormatting color;
                if (percentage <= 10) {
                    color = TextFormatting.RED;
                } else if (percentage >= 75) {
                    color = TextFormatting.GREEN;
                } else {
                    color = TextFormatting.YELLOW;
                }
                list.add(new StringTextComponent(color + "" + percentage + "%" + TextFormatting.GRAY + " ").append(new TranslationTextComponent("gui.scanner.charged")));
            }
            GuiUtils.drawHoveringText(matrixStack, list, mouseX, mouseY, width, height, -1, this.minecraft.font);
            RenderSystem.disableLighting();
        }
    }

    private static boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY) {
        return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
    }


    @Override
    public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks) {
        this.renderBackground(pMatrixStack);
        super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
        if (scanner == null) return;
        toggleButton.setMessage(scanner.powered ? new TranslationTextComponent("gui.scanner.turnOff") : new TranslationTextComponent("gui.scanner.turnOn"));
        boolean showMap = false;
        for (Direction facing : Direction.values())
            if (this.minecraft.level.getBlockState(scanner.getBlockPos().offset(facing.getNormal())).getBlock() == ModBlocks.BIOME_SCANNER_ULTIMATE.get()) {
                showMap = true;
            }
        showMapButton.visible = showMap;
        switch (scanner.rotation) { // I'll I18n this when I implement this
            case POSX_POSZ:
                rotateButton.setMessage(new StringTextComponent("Build on +x, +z"));;
                break;
            case POSX_NEGZ:
                rotateButton.setMessage(new StringTextComponent("Build on +x, -z"));
                break;
            case NEGX_POSZ:
                rotateButton.setMessage(new StringTextComponent("Build on -x, +z"));
                break;
            case NEGX_NEGZ:
                rotateButton.setMessage(new StringTextComponent("Build on -x, -z"));
                break;
        }
        drawForeground(pMatrixStack);
    }

    private void drawCenteredComponent(MatrixStack matrixStack, FontRenderer fontRenderer, ITextComponent textComponent, int x, int y, int color) {
        IReorderingProcessor processor = textComponent.getVisualOrderText();
        fontRenderer.draw(matrixStack, processor, (float)(x - fontRenderer.width(processor) / 2), (float)y, color);
    }

    protected void drawForeground(MatrixStack matrixStack) {
        NetworkHelper.INSTANCE.sendToServer(new MessageUpdateEnergyServer(scanner.getBlockPos().getX(), scanner.getBlockPos().getY(), scanner.getBlockPos().getZ()));
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        matrixStack.translate(i, j, 0);
        drawCenteredComponent(matrixStack, this.minecraft.font, new TranslationTextComponent("block.scanner.terrain_scanner"), 90, 6, 4210752);
        if(!this.minecraft.isLocalServer()) {
            drawCenteredComponent(matrixStack, this.minecraft.font, new TranslationTextComponent("block.scanner.terrain_scanner"), 90, 6, 4210752);
            drawCenteredComponent(matrixStack, this.minecraft.font, new TranslationTextComponent("gui.scanner.enderIOWarningL1"), 90, 13, 4210752);
            drawCenteredComponent(matrixStack, this.minecraft.font, new TranslationTextComponent("gui.scanner.enderIOWarningL2"), 90, 20, 4210752);
        }
        if(!"(0, -1, 0)".equals("(" + scanner.current.getX() + ", " + scanner.current.getY() + ", " + scanner.current.getZ() + ")")) {
            drawCenteredComponent(matrixStack, this.minecraft.font, new TranslationTextComponent("gui.scanner.current").append(" (" + scanner.current.getX() + ", " + scanner.current.getY() + ", " + scanner.current.getZ() + ")"), 90, 35, 4210752);
        } else {
            drawCenteredComponent(matrixStack, this.minecraft.font, new TranslationTextComponent("gui.scanner.currentBlockOff"), 90, 35, 4210752);
        }
        drawCenteredComponent(matrixStack, this.minecraft.font, new TranslationTextComponent("gui.scanner.endBlock").append(" (" + scanner.getEnd().getX() + ", " + scanner.maxY + ", " + scanner.getEnd().getZ() + ")"), 90, 45, 4210752);
        if (scanner.posStart != null) {
            drawCenteredComponent(matrixStack, this.minecraft.font, new TranslationTextComponent("gui.scanner.remoteStart").append(" (" + scanner.posStart.getX() + ", " + scanner.posStart.getZ() + ")"), 90, 55, 4210752);
        }
        boolean showMap = false;
        for (Direction facing : Direction.values())
            if (this.minecraft.level.getBlockState(scanner.getBlockPos().offset(facing.getNormal())).getBlock() == ModBlocks.BIOME_SCANNER_ULTIMATE.get()) {
                showMap = true;
            }
        if (!showMap) {
            drawCenteredComponent(matrixStack, this.minecraft.font, new TranslationTextComponent("gui.scanner.ultimateBSL1"), 90, 65, 4210752);
            drawCenteredComponent(matrixStack, this.minecraft.font, new TranslationTextComponent("gui.scanner.ultimateBSL2"), 90, 75, 4210752);
        }

        if (scanner.queue != null) {
            drawCenteredComponent(matrixStack, this.minecraft.font, new TranslationTextComponent("gui.scanner.queueAttached"), 90, 150, 4210752);
        }
    }

    @Override
    protected void renderBg(MatrixStack pMatrixStack, float pPartialTicks, int pX, int pY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(BACKGROUND);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(pMatrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(MatrixStack pMatrixStack, int pX, int pY) {
        // It should be empty
    }
}