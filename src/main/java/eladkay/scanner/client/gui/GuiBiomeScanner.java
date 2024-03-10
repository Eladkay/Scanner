package eladkay.scanner.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.FaceIcon;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.math.MathUtils;
import dev.ftb.mods.ftblibrary.math.XZ;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import eladkay.scanner.ScannerConfig;
import eladkay.scanner.compat.FTBChunksCompat;
import eladkay.scanner.tiles.TileEntityBiomeScanner;
import eladkay.scanner.init.ModBlocks;
import eladkay.scanner.networking.MessageUpdateEnergyServer;
import eladkay.scanner.networking.MessageUpdateMap;
import eladkay.scanner.networking.NetworkHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.lwjgl.opengl.GL11;

import java.util.*;


public class GuiBiomeScanner extends BaseScreen {

    private final TileEntityBiomeScanner scanner;
    public List<ChunkButton> chunkButtons;
    public Set<XZ> selectedChunks;

    public GuiBiomeScanner(TileEntityBiomeScanner scanner) {
        super();
        this.scanner = scanner;
    }

    public boolean onInit() {
        return this.setFullscreen();
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
                this.chunkButtons.add(button);
                button.setPos(sx + x * 16, sy + z * 16);
            }
        }

        this.addAll(this.chunkButtons);
    }

    @Override
    public void mouseReleased(MouseButton button) {
        super.mouseReleased(button);
        if (!this.selectedChunks.isEmpty()) {
            this.selectedChunks.clear();
            this.playClickSound();
        }
    }

    @Override
    public void drawBackground(MatrixStack matrixStack, Theme theme, int x, int y, int w, int h) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        PlayerEntity player = Minecraft.getInstance().player;
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
            super(panel, StringTextComponent.EMPTY, Icon.EMPTY);
            this.setSize(16, 16);
            this.chunkPos = xz;
        }

        public int calcDist(XZ a) {
            int arg1;
            int arg2;
            int fixposx = (int) (scanner.getBlockPos().getX() / 16.0);
            int fixposz = (int) (scanner.getBlockPos().getZ() / 16.0);
            int arg3;
            arg1 = a.x - fixposx;
            arg2 = a.z - fixposz;
            arg1 *= arg1;
            arg2 *= arg2;
            arg3 = (int) MathHelper.sqrt(arg1 + arg2);
            return arg3;
        }

        @Override
        public void onClicked(MouseButton mouseButton) {
            int distance = calcDist(chunkPos);
            NetworkHelper.INSTANCE.sendToServer(new MessageUpdateEnergyServer(scanner.getBlockPos().getX(), scanner.getBlockPos().getY(), scanner.getBlockPos().getZ()));
            if (scanner.getMapping(chunkPos.x, chunkPos.z) != null || scanner.getEnergyStored() < ScannerConfig.CONFIG.minEnergyPerChunkBiomeScanner.get() * ScannerConfig.CONFIG.increase.get() * distance)
                return;
            if (scanner.getBlockState().getBlock() == ModBlocks.BIOME_SCANNER_BASIC.get() && distance > 2)
                return;
            else if (scanner.getBlockState().getBlock() == ModBlocks.BIOME_SCANNER_ADVANCED.get() && distance > 4)
                return;
            else if (scanner.getBlockState().getBlock() == ModBlocks.BIOME_SCANNER_ELITE.get() && distance > 8)
                return;

            World scannerWorld = scanner.getLevel();

            Biome biome = scannerWorld.getBiome(new BlockPos(chunkPos.x * 16, 64, chunkPos.z * 16));
            ResourceLocation biomeLocation = scannerWorld.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(biome);
            TranslationTextComponent component = new TranslationTextComponent(Util.makeDescriptionId("biome", biomeLocation));
            scanner.mapping.put(new ChunkPos(chunkPos.x, chunkPos.z), component);
            scanner.setChanged();
            NetworkHelper.INSTANCE.sendToServer(new MessageUpdateMap(scanner, chunkPos.x, chunkPos.z));
            GuiBiomeScanner.this.selectedChunks.add(this.chunkPos);
        }

        @Override
        public void drawBackground(MatrixStack matrixStack, Theme theme, int x, int y, int w, int h) {
            if (this.isMouseOver() || GuiBiomeScanner.this.selectedChunks.contains(this.chunkPos)) {
                Color4I.WHITE.withAlpha(100).draw(matrixStack, x, y, w, h);
                if (isMouseButtonDown(MouseButton.LEFT) || isMouseButtonDown(MouseButton.RIGHT)) {
                    GuiBiomeScanner.this.selectedChunks.add(this.chunkPos);
                }
            }
        }

        @Override
        public void addMouseOverText(TooltipList list) {
            int distance = calcDist(chunkPos);

            if (scanner.getMapping(chunkPos.x, chunkPos.z) != null) {
                list.add(scanner.getMapping(chunkPos.x, chunkPos.z));
                list.add(new StringTextComponent("(" + chunkPos.x + ", " + chunkPos.z + ")"));
            } else {
                list.add(new StringTextComponent("???"));
                list.add(new TranslationTextComponent("gui.scanner.clickToScan"));
                list.add(new TranslationTextComponent("gui.scanner.powerCost").append(" " + ScannerConfig.CONFIG.minEnergyPerChunkBiomeScanner.get() * ScannerConfig.CONFIG.increase.get() * distance));
                list.add(new TranslationTextComponent("gui.scanner.distanceInChunks").append(": " + distance));
                if (scanner.getBlockState().getBlock() == ModBlocks.BIOME_SCANNER_BASIC.get() && distance > 2) {
                    list.add(new TranslationTextComponent("block.biome_scanner_basic").append(" ").append(new TranslationTextComponent("gui.scanner.cannotScanChunks", 2)));
                } else if (scanner.getBlockState().getBlock() == ModBlocks.BIOME_SCANNER_ADVANCED.get() && distance > 4) {
                    list.add(new TranslationTextComponent("block.biome_scanner_advancedanced").append(" ").append(new TranslationTextComponent("gui.scanner.cannotScanChunks", 4)));
                } else if (scanner.getBlockState().getBlock() == ModBlocks.BIOME_SCANNER_ELITE.get() && distance > 8) {
                    list.add(new TranslationTextComponent("block.biome_scanner_elite").append(" ").append(new TranslationTextComponent("gui.scanner.cannotScanChunks", 8)));
                }
            }

            if (Screen.hasControlDown()) {
                list.add(new StringTextComponent(chunkPos.toString()));
            }
        }
    }

}
