package eladkay.scanner.terrain;

import com.google.common.collect.Lists;
import eladkay.scanner.Config;
import eladkay.scanner.misc.NetworkHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class GuiScannerQueue extends GuiContainer {
    private static final ResourceLocation BACKGROUND = new ResourceLocation("scanner:textures/gui/standard_background.png");
    private final TileEntityScannerQueue scanner;
    private GuiTextField coordinates;
    private GuiButton push;
    private static final GuiButton[] bufferBuild;
    private static final GuiButton[] bufferRemove;
    private final List<GuiButton> buttonsBuild = Lists.newArrayList(bufferBuild);
    private final List<GuiButton> buttonsRemove = Lists.newArrayList(bufferRemove);

    static {
        bufferBuild = new GuiButton[TileEntityScannerQueue.CAPACITY];
        int j = 105;
        for (int i = 0; i < bufferBuild.length; i++) bufferBuild[i] = new GuiButton(j++, 0, 0, 50, 10, I18n.format("gui.button.build"));

        bufferRemove = new GuiButton[TileEntityScannerQueue.CAPACITY];
        int k = 205;
        for (int i = 0; i < bufferRemove.length; i++) bufferRemove[i] = new GuiButton(k++, 0, 0, 50, 10, I18n.format("gui.button.remove"));
    }

    public GuiScannerQueue(TileEntityScannerQueue scanner) {
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
        coordinates = new GuiTextField(101, mc.fontRenderer, (this.width / 2) - 50, this.height / 2 + 15, 100, 20);
        coordinates.setText("");
        this.coordinates.setMaxStringLength(2000);
        push = new GuiButton(102, (this.width / 2) - 50, this.height / 2 - 10, 100, 20, I18n.format("gui.button.add"));
        buttonList.add(push);
        buttonList.addAll(buttonsBuild);
        buttonList.addAll(buttonsRemove);
        super.initGui();
    }

    private boolean isPosValid(int x, int z) {
        BlockPos pos = scanner.getPos();
        return Config.maxQueueRange == 0 || Math.abs((pos.getX() - x)) + Math.abs(pos.getZ() - z) <= Config.maxQueueRange;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == push) {
            try {
                String[] split = coordinates.getText().replace("(", "").replace(")", "").replace(" ", "").split(",");
                BlockPos pos = new BlockPos(Integer.parseInt(split[0]), 0, Integer.parseInt(split[1]));
                if (!isPosValid(pos.getX(), pos.getZ())) {
                    //todo: add out of range message
                    return;
                }
                if (!scanner.queue.stream().map(BlockPos::toLong).collect(Collectors.toList()).contains(pos.toLong())) {
                    scanner.push(pos);
                    //todo: make sure serverside syncs
                }
            } catch (Exception e) {
                //ignored
            }
        } else if (button.id - 105 >= 0 && button.id - 105 <= TileEntityScannerQueue.CAPACITY) {
            if (scanner.scanner == null) return;
            scanner.scanner.posStart = scanner.get(button.id - 105);
            scanner.scanner.current = new BlockPos.MutableBlockPos(0, -1, 0);
            scanner.scanner.on = false;
            scanner.scanner.markDirty();
            NetworkHelper.instance.sendToServer(new MessageUpdateScanner(scanner.scanner));
        } else if (button.id - 205 >= 0 && button.id - 205 <= TileEntityScannerQueue.CAPACITY) {
            scanner.remove(scanner.get(button.id - 205));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.coordinates.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!this.coordinates.textboxKeyTyped(typedChar, keyCode)) {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void updateScreen() {
        this.coordinates.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        try {
            super.drawDefaultBackground();
            super.drawScreen(mouseX, mouseY, partialTicks);
        } catch (Exception idky) {
            //:P
        }
        this.coordinates.drawTextBox();
    }

    public void drawCenteredString(String text, int x, int y, int color) {
        fontRenderer.drawString(text, x - fontRenderer.getStringWidth(text) / 2, y, color);
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawCenteredString(I18n.format("tile.scannerQueue.name") + " (" + I18n.format("capacity") + ": " + TileEntityScannerQueue.CAPACITY + ")", 90, 6, 4210752); //

        for (GuiButton btn : buttonsBuild) btn.visible = false;
        for (GuiButton btn : buttonsRemove) btn.visible = false;
        int i = 0;
        for (int j = 0; j < scanner.size(); j++) {
            BlockPos pos = scanner.get(j);
            if (pos == null) continue;
            GuiButton btnBuild = buttonsBuild.get(j);
            btnBuild.visible = true;
            btnBuild.x = (this.width / 2) - 83;
            btnBuild.y = this.height / 2 - 65 + i;

            GuiButton btnRemove = buttonsRemove.get(j);
            btnRemove.visible = true;
            btnRemove.x = (this.width / 2) + 35;
            btnRemove.y = this.height / 2 - 65 + i;

            drawCenteredString("(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")", 90, 20 + i, 4210752);
            i += 10;
        }
        if (scanner.scanner != null)
            drawCenteredString(I18n.format("gui.tsAttached"), 85, 150, 4210752);
        else drawCenteredString(I18n.format("gui.tsNotAttached"), 85, 150, 4210752);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BACKGROUND);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }
}
