package eladkay.scanner.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.FaceIcon;
import dev.ftb.mods.ftblibrary.math.MathUtils;
import dev.ftb.mods.ftblibrary.math.XZ;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import eladkay.scanner.ScannerConfig;
import eladkay.scanner.compat.FTBChunksCompat;
import eladkay.scanner.networking.MessageUpdateEnergyServer;
import eladkay.scanner.networking.NetworkHelper;
import eladkay.scanner.networking.MessageUpdateScanner;
import eladkay.scanner.tiles.TileEntityTerrainScanner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

import java.util.*;


public class GuiBuildRemotely extends BaseScreen {

    private static int minimapTextureId;

    private final TileEntityTerrainScanner scanner;
    public List<ChunkButton> chunkButtons;
    public Set<XZ> selectedChunks;


    public GuiBuildRemotely(TileEntityTerrainScanner scanner) {
        super();
        this.scanner = scanner;
    }

    public boolean onInit() {
        return this.setFullscreen();
    }

    public void onClosed() {
        /*FTBChunksClient.alwaysRenderChunksOnMap = false;
        if (MapManager.inst != null) {
            MapManager.inst.updateAllRegions(false);
        }*/

        super.onClosed();
    }

    @Override
    public void addWidgets() {
        int sx = this.getX() + (this.width - 240) / 2;
        int sy = this.getY() + (this.height - 240) / 2;
        PlayerEntity player = Minecraft.getInstance().player;
        int startX = player.xChunk - 7;
        int startZ = player.zChunk - 7;
        this.chunkButtons = new ArrayList();
        this.selectedChunks = new LinkedHashSet();

        for(int z = 0; z < 15; ++z) {
            for(int x = 0; x < 15; ++x) {
                ChunkButton button = new ChunkButton(this, XZ.of(startX + x, startZ + z));
                //button.chunk = this.dimension.getRegion(XZ.regionFromChunk(startX + x, startZ + z)).getDataBlocking().getChunk(button.chunkPos);
                this.chunkButtons.add(button);
                button.setPos(sx + x * 16, sy + z * 16);
            }
        }

        this.addAll(this.chunkButtons);
        //(new RequestMapDataPacket(player.xChunk - 7, player.zChunk - 7, player.xChunk + 7, player.zChunk + 7)).sendToServer();
    }

    @Override
    public void mouseReleased(MouseButton button) {
        super.mouseReleased(button);
        if (!this.selectedChunks.isEmpty()) {
            //(new RequestChunkChangePacket(isShiftKeyDown() ? (button.isLeft() ? 2 : 3) : (button.isLeft() ? 0 : 1), this.selectedChunks)).sendToServer();
            this.selectedChunks.clear();
            this.playClickSound();
        }
    }

    @Override
    public void drawBackground(MatrixStack matrixStack, Theme theme, int x, int y, int w, int h) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        PlayerEntity player = Minecraft.getInstance().player;
        int startX = player.xChunk - 7;
        int startZ = player.zChunk - 7;
        int sx = x + (w - 240) / 2;
        int sy = y + (h - 240) / 2;
        int r = 70;
        int g = 70;
        int b = 70;
        int a = 100;
        RenderSystem.lineWidth(Math.max(2.5F, (float)Minecraft.getInstance().getWindow().getWidth() / 1920.0F * 2.5F));

        RenderSystem.enableTexture();
        RenderSystem.bindTexture(FTBChunksCompat.getMinimapTextureId());
        RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GuiHelper.drawTexturedRect(matrixStack, sx, sy, 240, 240, Color4I.WHITE, 0F, 0F, 1F, 1F);

        if (!InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 258)) {
            RenderSystem.disableTexture();
            buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);

            int gx;
            for(gx = 1; gx < 15; ++gx) {
                buffer.vertex((double)sx, (double)(sy + gx * 16), 0.0).color(r, g, b, a).endVertex();
                buffer.vertex((double)(sx + 240), (double)(sy + gx * 16), 0.0).color(r, g, b, a).endVertex();
            }

            for(gx = 1; gx < 15; ++gx) {
                buffer.vertex((double)(sx + gx * 16), (double)sy, 0.0).color(r, g, b, a).endVertex();
                buffer.vertex((double)(sx + gx * 16), (double)(sy + 240), 0.0).color(r, g, b, a).endVertex();
            }

            tessellator.end();
            buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
            // This adds red lines which are unnecessary
            /*Iterator var26 = this.chunkButtons.iterator();

            while(var26.hasNext()) {
                ChunkButton button = (ChunkButton)var26.next();
                int cx = button.getX();
                int cy = button.getY();
                buffer.vertex((double)cx, (double)cy, 0.0).color(255, 0, 0, 100).endVertex();
                buffer.vertex((double)(cx + 16), (double)(cy + 16), 0.0).color(255, 0, 0, 100).endVertex();
                buffer.vertex((double)((float)cx + 8.0F), (double)cy, 0.0).color(255, 0, 0, 100).endVertex();
                buffer.vertex((double)(cx + 16), (double)((float)cy + 8.0F), 0.0).color(255, 0, 0, 100).endVertex();
                buffer.vertex((double)cx, (double)((float)cy + 8.0F), 0.0).color(255, 0, 0, 100).endVertex();
                buffer.vertex((double)((float)cx + 8.0F), (double)(cy + 16), 0.0).color(255, 0, 0, 100).endVertex();
            }*/

            tessellator.end();
        }

        RenderSystem.enableTexture();
        RenderSystem.lineWidth(1.0F);
        double hx = (double)(sx + 112) + MathUtils.mod(player.getX(), 16.0);
        double hy = (double)(sy + 112) + MathUtils.mod(player.getZ(), 16.0);
        FaceIcon.getFace(player.getGameProfile()).draw(matrixStack, (int)(hx - 4.0), (int)(hy - 4.0), 8, 8);
    }

    public class ChunkButton extends Button {
        public final XZ chunkPos;

        public ChunkButton(Panel panel, XZ xz) {
            super(panel);
            this.setSize(16, 16);
            this.chunkPos = xz;
        }

        @Override
        public void onClicked(MouseButton button) {
            NetworkHelper.INSTANCE.sendToServer(new MessageUpdateEnergyServer(scanner.getBlockPos().getX(), scanner.getBlockPos().getY(), scanner.getBlockPos().getZ()));
            if (scanner.getEnergyStored() < ScannerConfig.CONFIG.remoteBuildCost.get())
                return;

            scanner.container.extractEnergy(ScannerConfig.CONFIG.remoteBuildCost.get(), false);
            scanner.powered = false;
            // todo make a better solution to getting vegetation to work.
            scanner.posStart = new BlockPos((chunkPos.x * 16) + 8, 0, (chunkPos.z * 16) + 8);
            scanner.current = new BlockPos.Mutable(0, -1, 0);
            scanner.setChanged();
            NetworkHelper.INSTANCE.sendToServer(new MessageUpdateScanner(scanner));
            GuiBuildRemotely.this.selectedChunks.add(this.chunkPos);
            //GuiHelper.playClickSound();
            //currentSelectionMode = 1;

        }

        @Override
        public void drawBackground(MatrixStack matrixStack, Theme theme, int x, int y, int w, int h) {
            if (this.isMouseOver() || GuiBuildRemotely.this.selectedChunks.contains(this.chunkPos)) {
                Color4I.WHITE.withAlpha(100).draw(matrixStack, x, y, w, h);
                if (isMouseButtonDown(MouseButton.LEFT) || isMouseButtonDown(MouseButton.RIGHT)) {
                    GuiBuildRemotely.this.selectedChunks.add(this.chunkPos);
                }
            }
        }

        @Override
        public void addMouseOverText(TooltipList list) {
            list.add(new TranslationTextComponent("gui.scanner.clickToScan"));
            list.add(new TranslationTextComponent("gui.scanner.powerCost").append(" " + ScannerConfig.CONFIG.remoteBuildCost.get()));
            list.add(new StringTextComponent(chunkPos.toString()));
            NetworkHelper.INSTANCE.sendToServer(new MessageUpdateEnergyServer(scanner.getBlockPos().getX(), scanner.getBlockPos().getY(), scanner.getBlockPos().getZ()));
            if (scanner.posStart != null && scanner.posStart.getX() == (chunkPos.x * 16) + 8 && scanner.posStart.getZ() == (chunkPos.z * 16) + 8)
                list.add(new TranslationTextComponent("gui.scanner.alreadyBuilding"));
            else if (scanner.getEnergyStored() < ScannerConfig.CONFIG.remoteBuildCost.get())
                list.add(new TranslationTextComponent("gui.scanner.insufficientPower"));
        }
    }

}