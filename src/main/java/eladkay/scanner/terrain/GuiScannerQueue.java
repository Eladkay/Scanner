package eladkay.scanner.terrain;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
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
    private List<GuiButton> buttonsBuild = Lists.newArrayList(bufferBuild);
    private List<GuiButton> buttonsRemove = Lists.newArrayList(bufferRemove);
    private static GuiButton[] bufferBuild;
    private static GuiButton[] bufferRemove;

    static {
        bufferBuild = new GuiButton[TileEntityScannerQueue.CAPACITY];
        int j = 105;
        for (int i = 0; i < bufferBuild.length; i++) bufferBuild[i] = new GuiButton(j++, 0, 0, 50, 10, "Build");

        bufferRemove = new GuiButton[TileEntityScannerQueue.CAPACITY];
        int k = 205;
        for (int i = 0; i < bufferRemove.length; i++) bufferRemove[i] = new GuiButton(k++, 0, 0, 50, 10, "Remove");
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
        coordinates = new GuiTextField(101, mc.fontRendererObj, (this.width / 2) - 50, this.height / 2 + 15, 100, 20);
        coordinates.setText("");
        this.coordinates.setMaxStringLength(2000);
        push = new GuiButton(102, (this.width / 2) - 50, this.height / 2 - 10, 100, 20, "Add");
        buttonList.add(push);
        buttonList.addAll(buttonsBuild);
        buttonList.addAll(buttonsRemove);
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == push) {
            try {
                String[] split = coordinates.getText().replace("(", "").replace(")", "").replace(" ", "").split(",");
                if (!scanner.queue.stream().map(BlockPos::toLong).collect(Collectors.toList()).contains(new BlockPos(Integer.parseInt(split[0]), 0, Integer.parseInt(split[1])).toLong()))
                    scanner.push(new BlockPos(Integer.parseInt(split[0]), 0, Integer.parseInt(split[1])));
            } catch (Exception e) {
                //ignored
            }
        } else if (button.id - 105 >= 0 && button.id - 105 <= TileEntityScannerQueue.CAPACITY) {
            if (scanner.scanner == null) return;
            scanner.scanner.posStart = scanner.get(button.id - 105);
            scanner.scanner.current = new BlockPos.MutableBlockPos(0, -1, 0);
            scanner.scanner.on = false;
        } else if (button.id - 205 >= 0 && button.id - 205 <= TileEntityScannerQueue.CAPACITY) {
            scanner.remove(scanner.get(button.id - 205));
        }
    }

    private static final ResourceLocation ENERGY_BAR = new ResourceLocation("scanner:textures/gui/bar.png");

    private static boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY) {
        return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
    }

    public int getKx() {
        return 0;
        //return mc.displayWidth / 5 + 5;
    }

    public int getKy() {
        return 0;
        // return mc.displayHeight / 5 - 30;
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
            super.drawScreen(mouseX, mouseY, partialTicks);
        } catch (Exception idky) {
            //
        }
        this.coordinates.drawTextBox();
    }

    public void drawCenteredString(String text, int x, int y, int color) {
        fontRendererObj.drawString(text, x - fontRendererObj.getStringWidth(text) / 2, y, color);
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawCenteredString("Scanner Queue (Capacity: " + TileEntityScannerQueue.CAPACITY + ")", 90 + getKx(), 6 + getKy(), 4210752); //

        for (GuiButton btn : buttonsBuild) btn.visible = false;
        for (GuiButton btn : buttonsRemove) btn.visible = false;
        int i = 0;
        for (int j = 0; j < scanner.size(); j++) {
            BlockPos pos = scanner.get(j);
            if (pos == null) continue;
            GuiButton btnBuild = buttonsBuild.get(j);
            btnBuild.visible = true;
            btnBuild.xPosition = (this.width / 2) - 83;
            btnBuild.yPosition = this.height / 2 - 65 + i;

            GuiButton btnRemove = buttonsRemove.get(j);
            btnRemove.visible = true;
            btnRemove.xPosition = (this.width / 2) + 35;
            btnRemove.yPosition = this.height / 2 - 65 + i;

            drawCenteredString("(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")", 90 + getKx(), 20 + getKy() + i, 4210752);
            i += 10;
        }
        /*if (scanner.scanner != null && scanner.flag)
            drawCenteredString("Biome Scanner attached", 85 + getKx(), 140 + getKy(), 4210752);
        else {
            drawCenteredString("Attach Ultimate Biome Scanner", 85 + getKx(), 128 + getKy(), 4210752);
            drawCenteredString("to Terrain Scanner to view map", 85 + getKx(), 138 + getKy(), 4210752);
        }*/
        if (scanner.scanner != null)
            drawCenteredString("Terrain Scanner attached", 85 + getKx(), 150 + getKy(), 4210752);
        else drawCenteredString("Terrain Scanner not attached", 85 + getKx(), 150 + getKy(), 4210752);
       /* boolean flag0 = false;
        if (mc.player.getName().matches("(?:Player\\d{1,3})|(?:Eladk[ae]y)") && flag0)
            this.fontRendererObj.drawString("Debug: (" + scanner.getPos().east().add(15, 255, 15).getX() + ", " + scanner.getPos().east().getY() + ", " + scanner.getPos().east().getZ() + ")", 20, 60, 4210752);*/
        /*if (Config.maxSpeedup > 0)
            this.fontRendererObj.drawString("Speedup (blocks per tick): " + scanner.speedup, 20, 120, 4210752);*/
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BACKGROUND);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }
}
