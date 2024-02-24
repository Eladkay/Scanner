package eladkay.scanner.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import eladkay.scanner.ScannerConfig;
import eladkay.scanner.client.container.ScannerQueueContainer;
import eladkay.scanner.networking.NetworkHelper;
import eladkay.scanner.networking.MessageUpdateScanner;
import eladkay.scanner.tiles.TileEntityScannerQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GuiScannerQueue extends ContainerScreen<ScannerQueueContainer> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation("scanner:textures/gui/standard_background.png");
    private final TileEntityScannerQueue scanner;
    private TextFieldWidget coordinates;
    private final List<Button> buttonsBuild = new ArrayList<>();
    private final List<Button> buttonsRemove = new ArrayList<>();

    public GuiScannerQueue(ScannerQueueContainer scannerContainer, PlayerInventory playerInventory, ITextComponent title) {
        super(scannerContainer, playerInventory, new StringTextComponent(""));
        this.scanner = scannerContainer.scanner;
    }

    @Override
    public void init() {
        super.init();
        coordinates = new TextFieldWidget(Minecraft.getInstance().font, (this.width / 2) - 50, this.height / 2 + 15, 100, 20, new StringTextComponent(""));
        this.coordinates.setMaxLength(2000);
        this.addButton(coordinates);
        this.addButton(new Button((this.width / 2) - 50, this.height / 2 - 10, 100, 20, new TranslationTextComponent("gui.scanner.button.add"), (button) -> {
            try {
                String[] split = coordinates.getValue().replace("(", "").replace(")", "").replace(" ", "").split(",");
                BlockPos pos = new BlockPos(Integer.parseInt(split[0]), 0, Integer.parseInt(split[1]));
                if (!isPosValid(pos.getX(), pos.getZ())) {
                    //todo: add out of range message
                    return;
                }
                if (!scanner.queue.stream().map(BlockPos::asLong).collect(Collectors.toList()).contains(pos.asLong()))
                    scanner.push(pos);

                NetworkHelper.INSTANCE.sendToServer(new MessageUpdateScanner(scanner));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        for (int i = 0; i < TileEntityScannerQueue.CAPACITY; i++) {
            int finalI = i;
            Button buildButton = new Button((this.width / 2) - 83, this.height / 2 - 65 + (finalI * 10), 50, 10, new TranslationTextComponent("gui.scanner.button.build"), (button) -> {
                if (scanner.scanner == null) return;
                scanner.scanner.posStart = scanner.get(finalI);
                scanner.scanner.current = new BlockPos.Mutable(0, -1, 0);
                scanner.scanner.powered = false;
                scanner.scanner.setChanged();
                NetworkHelper.INSTANCE.sendToServer(new MessageUpdateScanner(scanner.scanner));
            });
            buttonsBuild.add(buildButton);
            this.addButton(buildButton);
            Button removeButton = new Button((this.width / 2) + 35, this.height / 2 - 65 + (finalI * 10), 50, 10, new TranslationTextComponent("gui.scanner.button.remove"), (button) -> {
                scanner.remove(scanner.get(finalI));
            });
            buttonsRemove.add(removeButton);
            this.addButton(removeButton);
        }
    }

    private boolean isPosValid(int x, int z) {
        BlockPos pos = scanner.getBlockPos();
        return ScannerConfig.CONFIG.maxQueueRange.get() == 0 || Math.abs((pos.getX() - x)) + Math.abs(pos.getZ() - z) <= ScannerConfig.CONFIG.maxQueueRange.get();
    }

    private void drawCenteredComponent(MatrixStack matrixStack, FontRenderer fontRenderer, ITextComponent textComponent, int x, int y, int color) {
        IReorderingProcessor processor = textComponent.getVisualOrderText();
        fontRenderer.draw(matrixStack, processor, (float)(x - fontRenderer.width(processor) / 2), (float)y, color);
    }

    @Override
    public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks) {
        this.renderBackground(pMatrixStack);
        super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
        int xTranslate = (this.width - this.imageWidth) / 2;
        int yTranslate = (this.height - this.imageHeight) / 2;
        pMatrixStack.translate(xTranslate, yTranslate, 0);

        drawCenteredComponent(pMatrixStack, this.minecraft.font, new TranslationTextComponent("block.scanner.scanner_queue").append(" (").append(new TranslationTextComponent("gui.scanner.capacity")).append(": " + TileEntityScannerQueue.CAPACITY + ")"), 90, 6, 4210752); //

        if(scanner == null) return;

        for (Button btn : buttonsBuild) btn.visible = false;
        for (Button btn : buttonsRemove) btn.visible = false;
        int i = 0;
        for (int j = 0; j < scanner.size(); j++) {
            BlockPos pos = scanner.get(j);
            if (pos == null) continue;
            Button btnBuild = buttonsBuild.get(j);
            btnBuild.visible = true;

            Button btnRemove = buttonsRemove.get(j);
            btnRemove.visible = true;

            drawCenteredComponent(pMatrixStack, this.minecraft.font, new StringTextComponent("(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")"), 90, 20 + i, 4210752);
            i += 10;
        }

        if (scanner.scanner != null) {
            drawCenteredComponent(pMatrixStack, this.minecraft.font, new TranslationTextComponent("gui.scanner.tsAttached"), 85, 150, 4210752);
        } else {
            drawCenteredComponent(pMatrixStack, this.minecraft.font, new TranslationTextComponent("gui.scanner.tsNotAttached"), 85, 150, 4210752);
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
