//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package eladkay.scanner.biome;

import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.gui.IGui;
import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.lib.client.TextureCoords;
import com.feed_the_beast.ftbl.lib.gui.ButtonLM;
import com.feed_the_beast.ftbl.lib.gui.GuiLM;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbl.lib.math.MathHelperLM;
import com.google.common.collect.Lists;
import eladkay.scanner.Config;
import eladkay.scanner.misc.GuiHelper;
import eladkay.scanner.misc.NetworkHelper;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.util.List;


@SideOnly(Side.CLIENT)
public class GuiBiomeScanner extends GuiLM implements GuiYesNoCallback {
    public static final double UV = 0.9375D;
    public static final ResourceLocation TEX_ENTITY = new ResourceLocation("ftbu", "textures/gui/entity.png");
    public static final ResourceLocation TEX_CHUNK_CLAIMING = new ResourceLocation("ftbu", "textures/gui/chunk_claiming.png");
    public static final TextureCoords TEX_FILLED;
    public static final TextureCoords TEX_BORDER;
    static final int TILES_TEX = 16;
    static final int TILES_GUI = 15;
    public static int textureID;
    public static ByteBuffer pixelBuffer;
    public final int startX;
    public final int startZ;
    public final int currentDim;
    public final GuiBiomeScanner.MapButton[] mapButtons;
    public ThreadReloadArea thread = null;
    public String currentDimName;

    static {
        TEX_FILLED = TextureCoords.fromUV(TEX_CHUNK_CLAIMING, 0.0D, 0.0D, 0.5D, 1.0D);
        TEX_BORDER = TextureCoords.fromUV(TEX_CHUNK_CLAIMING, 0.5D, 0.0D, 1.0D, 1.0D);
        textureID = -1;
        pixelBuffer = null;
    }

    private BlockPos pos;
    private int type;

    @Override
    public void onInit() {
        super.onInit();
        new ThreadReloadArea(mc.theWorld, this).start();
    }


    public GuiBiomeScanner(long token, BlockPos pos, int type) {
        super(240, 240);
        this.pos = pos;
        this.type = type;
        this.startX = MathHelperLM.chunk(this.mc.thePlayer.posX) - 7;
        this.startZ = MathHelperLM.chunk(this.mc.thePlayer.posZ) - 7;
        this.currentDim = FTBLibClient.getDim();
        this.currentDimName = this.mc.theWorld.provider.getDimensionType().getName();
        this.mapButtons = new GuiBiomeScanner.MapButton[225];
        for (int i = 0; i < this.mapButtons.length; ++i) {
            this.mapButtons[i] = new GuiBiomeScanner.MapButton(0, 0, i);
            register(this.mapButtons[i]);
        }


    }

    public static double getDistanceMC(double x1, double x2, double y1, double y2) {
        double d0 = x1 - x2;
        double d1 = y1 - y2;
        return (double) MathHelper.sqrt_double(d0 * d0 + d1 * d1);
    }

    @Override
    public void addWidgets() {
        GuiBiomeScanner.MapButton[] var1 = this.mapButtons;
        for (MapButton b : var1)
            this.add(b);

    }

    @Override
    public void drawBackground() {
        super.drawBackground();

        if (textureID == -1)
            textureID = TextureUtil.glGenTextures();


        if (pixelBuffer != null) {
            //boolean hasBlur = false;
            //int filter = hasBlur ? GL11.GL_LINEAR : GL11.GL_NEAREST;
            GlStateManager.bindTexture(textureID);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, TILES_TEX * 16, TILES_TEX * 16, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixelBuffer);
            pixelBuffer = null;
            thread = null;
        }

        GlStateManager.color(0F, 0F, 0F, 1F);
        GuiHelper.drawBlankRect(posX - 2, posY - 2, 240 + 4, 240 + 4);
        //drawBlankRect((xSize - 128) / 2, (ySize - 128) / 2, zLevel, 128, 128);
        GlStateManager.color(1F, 1F, 1F, 1F);

        if (thread == null) {
            GlStateManager.bindTexture(textureID);
            GuiHelper.drawTexturedRect((int) posX, (int) posY, TILES_GUI * 16, TILES_GUI * 16, 0D, 0D, UV, UV);
            GlStateManager.color(0, 0, 0, 0);
            GuiHelper.drawBlankRect((int) posX - 1, (int) posY - 1, TILES_GUI * 16 + 2, TILES_GUI * 16 + 2);
        }

        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.enableTexture2D();
        FTBLibClient.setTexture(TEX_CHUNK_CLAIMING);

        for (MapButton mapButton : mapButtons) {
            mapButton.renderWidget(this);
        }

        // Grid start //

        GlStateManager.disableTexture2D();
        GlStateManager.glLineWidth(1F);
        GlStateManager.color(1F, 1F, 1F, 1F);
        int gridR = 128, gridG = 128, gridB = 128, gridA = 150;

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        int gridX = (int) mapButtons[0].getAX();
        int gridY = (int) mapButtons[0].getAY();

        for (int x = 0; x <= TILES_GUI; x++) {
            buffer.pos(gridX + x * 16, gridY, 0D).color(gridR, gridG, gridB, gridA).endVertex();
            buffer.pos(gridX + x * 16, gridY + TILES_GUI * 16, 0D).color(gridR, gridG, gridB, gridA).endVertex();
        }

        for (int y = 0; y <= TILES_GUI; y++) {
            buffer.pos(gridX, gridY + y * 16, 0D).color(gridR, gridG, gridB, gridA).endVertex();
            buffer.pos(gridX + TILES_GUI * 16, gridY + y * 16, 0D).color(gridR, gridG, gridB, gridA).endVertex();
        }

        tessellator.draw();
        GlStateManager.enableTexture2D();

        // Grid End //

        int cx = MathHelperLM.chunk(mc.thePlayer.posX);
        int cy = MathHelperLM.chunk(mc.thePlayer.posZ);


        if (cx >= startX && cy >= startZ && cx < startX + TILES_GUI && cy < startZ + TILES_GUI) {
            double x = ((cx - startX) * 16D + MathHelperLM.wrap(mc.thePlayer.posX, 16D));
            double y = ((cy - startZ) * 16D + MathHelperLM.wrap(mc.thePlayer.posZ, 16D));

            GlStateManager.pushMatrix();
            GlStateManager.translate(posX + x, posY + y, 0D);
            GlStateManager.pushMatrix();
            //GlStateManager.rotate((int)((ep.rotationYaw + 180F) / (180F / 8F)) * (180F / 8F), 0F, 0F, 1F);
            GlStateManager.rotate(mc.thePlayer.rotationYaw + 180F, 0F, 0F, 1F);
            FTBLibClient.setTexture(TEX_ENTITY);
            GlStateManager.color(1F, 1F, 1F, mc.thePlayer.isSneaking() ? 0.4F : 0.7F);
            GuiHelper.drawTexturedRect(-8, -8, 16, 16, 0D, 0D, 1D, 1D);
            GlStateManager.popMatrix();
            GuiHelper.drawPlayerHead(mc.thePlayer.getName(), -2, -2, 4, 4);
            GlStateManager.popMatrix();
        }

        GlStateManager.color(1F, 1F, 1F, 1F);

    }

    @Override
    public void confirmClicked(boolean set, int id) {
        this.openGui();
        this.refreshWidgets();
    }

    List<MapButton> registered = Lists.newArrayList();
    public void register(MapButton b) {
        if(registered.contains(b)) return;
        this.add(b);
        registered.add(b);
    }

    public class MapButton extends ButtonLM {
        public final ChunkDimPos chunkPos;
        private final int index;
        private boolean isSelected;

        public MapButton(int x, int y, int i) {
            super(x, y, 16, 16);
            this.posX += (double) (i % 15) * 240;
            this.posY += (double) (i / 15) * 240;
            this.chunkPos = new ChunkDimPos(GuiBiomeScanner.this.currentDim, GuiBiomeScanner.this.startX + i % 15, GuiBiomeScanner.this.startZ + i / 15);
            index = 1;
            isSelected = false;
        }


        @Override
        public void onClicked(@Nonnull IGui gui, @Nonnull IMouseButton button) {
            register(this);
            if (button.isLeft()) {
                TileEntityBiomeScanner te = (TileEntityBiomeScanner) mc.theWorld.getTileEntity(pos);
                if (te == null) return;
                int distance = (int) (getDistanceMC(pos.getX(), chunkPos.posX * 16, pos.getZ(),
                        chunkPos.posZ * 16) / 16);
                if (te.getMapping(chunkPos.posX, chunkPos.posZ) != null ||
                        te.getEnergyStored(null) < Config.minEnergyPerChunkBiomeScanner *
                                Config.increase * distance) return;
                if (type == 0 && distance > 2) return;
                else if (type == 1 && distance > 4) return;
                else if (type == 2 && distance > 8) return;
                te.container().extractEnergy(Config.minEnergyPerChunkBiomeScanner * Config.increase * distance, false);
                te.mapping.put(new ChunkPos(chunkPos.posX, chunkPos.posZ), mc.theWorld.getBiomeGenForCoords(new BlockPos(chunkPos.posX * 16, 64, chunkPos.posZ * 16)).getBiomeName());
                te.markDirty();
                NetworkHelper.instance.sendToServer(new MessageUpdateMap(pos.getX(), pos.getY(), pos.getZ(),
                        chunkPos.posX, chunkPos.posZ, Config.minEnergyPerChunkBiomeScanner * Config.increase * distance));
            }
            GuiHelper.playClickSound();

        }

        @Override
        public void addMouseOverText(IGui gui, List<String> l) {
            TileEntityBiomeScanner te = (TileEntityBiomeScanner) mc.theWorld.getTileEntity(pos);
            register(this);
            if (te == null) return;
            long distance = (long) (getDistanceMC(pos.getX(), chunkPos.posX * 16, pos.getZ(),
                    chunkPos.posZ * 16) / 16);
            if (te.getMapping(chunkPos.posX, chunkPos.posZ) != null)
                l.add(te.getMapping(chunkPos.posX, chunkPos.posZ));
            else {
                l.add("???");
                l.add("Click to scan!");
                l.add("Power cost: " + Config.minEnergyPerChunkBiomeScanner * Config.increase * distance);
                l.add("Distance (chunks): " + distance);
                if (type == 0 && distance > 2) l.add("Basic Biome Scanner cannot scan chunks more than 2 chunks away!");
                else if (type == 1 && distance > 4)
                    l.add("Advanced Biome Scanner cannot scan chunks more than 4 chunks away!");
                else if (type == 2 && distance > 8)
                    l.add("Elite Biome Scanner cannot scan chunks more than 8 chunks away!");
            }

            l.add("{" + chunkPos.posX + ", " + chunkPos.posZ + "}");
        }
        @Override
        public void renderWidget(IGui gui) {
            int ax = this.getAX();
            int ay = this.getAY();

            if(this.isSelected || gui.isMouseOver(this)) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 0.27F);
                com.feed_the_beast.ftbl.lib.gui.GuiHelper.drawBlankRect(ax, ay, 16, 16);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            }

            if(!this.isSelected && GuiBiomeScanner.this.isMouseOver(this)) {
                this.isSelected = true;
            }

        }


    }
}
