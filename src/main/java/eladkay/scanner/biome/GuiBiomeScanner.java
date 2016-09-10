//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package eladkay.scanner.biome;

import com.feed_the_beast.ftbl.api.ForgePlayerSPSelf;
import com.feed_the_beast.ftbl.api.ForgeTeam;
import com.feed_the_beast.ftbl.api.ForgeWorldSP;
import com.feed_the_beast.ftbl.api.MouseButton;
import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.client.gui.GuiIcons;
import com.feed_the_beast.ftbl.api.client.gui.GuiLM;
import com.feed_the_beast.ftbl.api.client.gui.GuiLang;
import com.feed_the_beast.ftbl.api.client.gui.widgets.ButtonLM;
import com.feed_the_beast.ftbl.api.client.gui.widgets.PanelLM;
import com.feed_the_beast.ftbl.net.MessageRequestSelfUpdate;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbl.util.TextureCoords;
import com.feed_the_beast.ftbu.net.MessageAreaRequest;
import com.feed_the_beast.ftbu.world.chunks.ClaimedChunk;
import com.feed_the_beast.ftbu.world.data.FTBUWorldDataSP;
import com.google.gson.Gson;
import com.latmod.lib.math.MathHelperLM;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiBiomeScanner extends GuiLM implements GuiYesNoCallback {
    public static final int tiles_tex = 16;
    public static final int tiles_gui = 15;
    public static final double UV = 0.9375D;
    public static final ResourceLocation TEX_ENTITY = new ResourceLocation("ftbu", "textures/gui/entity.png");
    public static final ResourceLocation TEX_CHUNK_CLAIMING = new ResourceLocation("ftbu", "textures/gui/chunk_claiming.png");
    public static final TextureCoords TEX_FILLED;
    public static final TextureCoords TEX_BORDER;
    public static int textureID;
    public static ByteBuffer pixelBuffer;
    public final ForgePlayerSPSelf playerLM;
    public final int startX;
    public final int startZ;
    public final int currentDim;
    public final ButtonLM buttonRefresh;
    public final ButtonLM buttonClose;
    public final GuiBiomeScanner.MapButton[] mapButtons;
    public final PanelLM panelButtons;
    public ThreadReloadArea thread = null;
    public String currentDimName;
    private BlockPos pos;

    public GuiBiomeScanner(long token, BlockPos pos) {
        this.pos = pos;
        this.width = this.height = 240.0D;
        this.playerLM = ForgeWorldSP.inst.clientPlayer;
        this.startX = MathHelperLM.chunk(this.mc.thePlayer.posX) - 7;
        this.startZ = MathHelperLM.chunk(this.mc.thePlayer.posZ) - 7;
        this.currentDim = FTBLibClient.getDim();
        this.currentDimName = this.mc.theWorld.provider.getDimensionType().getName();
        this.buttonClose = new ButtonLM(0.0D, 16.0D, 16, 16, GuiLang.button_close.translate()) {
            public void onClicked(@Nonnull GuiLM gui, @Nonnull MouseButton button) {
                GuiLM.playClickSound();
                GuiBiomeScanner.this.closeGui();
            }
        };
        this.buttonRefresh = new ButtonLM(0.0D, 32, 16, 16, GuiLang.button_refresh.translate()) {
            public void onClicked(@Nonnull GuiLM gui, @Nonnull MouseButton button) {
                GuiBiomeScanner.this.thread = new ThreadReloadArea(GuiBiomeScanner.this.mc.theWorld, GuiBiomeScanner.this);
                GuiBiomeScanner.this.thread.run();
                (new MessageAreaRequest(GuiBiomeScanner.this.startX, GuiBiomeScanner.this.startZ, 15, 15)).sendToServer();
                (new MessageRequestSelfUpdate()).sendToServer();
                GuiLM.playClickSound();
            }
        };
        this.panelButtons = new PanelLM(0, 0, 16, 0) {
            public void addWidgets() {
                this.add(GuiBiomeScanner.this.buttonClose);
                this.add(GuiBiomeScanner.this.buttonRefresh);
                this.height = (double)(this.widgets.size() * 16);
            }

            public double getAX() {
                return GuiBiomeScanner.this.screen.getScaledWidth_double() - 16.0D;
            }

            public double getAY() {
                return 0.0D;
            }
        };
        this.mapButtons = new GuiBiomeScanner.MapButton[225];

        for(int i = 0; i < this.mapButtons.length; ++i) {
            this.mapButtons[i] = new GuiBiomeScanner.MapButton(0, 0, i);
        }

    }

    public void onInit() {
        this.buttonRefresh.onClicked(this, MouseButton.LEFT);
    }

    public void addWidgets() {
        GuiBiomeScanner.MapButton[] var1 = this.mapButtons;
        int var2 = var1.length;

        for (MapButton b : var1)
            this.add(b);

        this.add(this.panelButtons);
    }

    public void drawBackground() {
        super.drawBackground();
        if(textureID == -1) {
            textureID = TextureUtil.glGenTextures();
            (new MessageAreaRequest(this.startX, this.startZ, 15, 15)).sendToServer();
        }

        if(pixelBuffer != null) {
            GlStateManager.bindTexture(textureID);
            GL11.glTexParameteri(3553, 10241, 9728);
            GL11.glTexParameteri(3553, 10240, 9728);
            GL11.glTexParameteri(3553, 10242, '脯');
            GL11.glTexParameteri(3553, 10243, '脯');
            GL11.glTexImage2D(3553, 0, '聘', 256, 256, 0, 6408, 5121, pixelBuffer);
           /* pixelBuffer = null;
            this.thread = null;*/
        }

        GlStateManager.color(0.0F, 0.0F, 0.0F, 1.0F);
        drawBlankRect(this.posX - 2.0D, this.posY - 2.0D, this.width + 4.0D, this.height + 4.0D);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        if(this.thread == null) {
            GlStateManager.bindTexture(textureID);
            drawTexturedRect(this.posX, this.posY, 240.0D, 240.0D, 0.0D, 0.0D, 0.9375D, 0.9375D);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        FTBLibClient.setTexture(TEX_CHUNK_CLAIMING);
        GuiBiomeScanner.MapButton[] cx = this.mapButtons;
        int cy = cx.length;

        for(int x = 0; x < cy; ++x) {
            GuiBiomeScanner.MapButton mapButton = cx[x];
            mapButton.renderWidget(this);
        }

        int var7 = MathHelperLM.chunk(this.mc.thePlayer.posX);
        cy = MathHelperLM.chunk(this.mc.thePlayer.posZ);
        if(var7 >= this.startX && cy >= this.startZ && var7 < this.startX + 15 && cy < this.startZ + 15) {
            double var8 = (double)(var7 - this.startX) * 16.0D + MathHelperLM.wrap(this.mc.thePlayer.posX, 16.0D);
            double y = (double)(cy - this.startZ) * 16.0D + MathHelperLM.wrap(this.mc.thePlayer.posZ, 16.0D);
            GlStateManager.pushMatrix();
            GlStateManager.translate(this.posX + var8, this.posY + y, 0.0D);
            GlStateManager.pushMatrix();
            GlStateManager.rotate(this.mc.thePlayer.rotationYaw + 180.0F, 0.0F, 0.0F, 1.0F);
            FTBLibClient.setTexture(TEX_ENTITY);
            GlStateManager.color(1.0F, 1.0F, 1.0F, this.mc.thePlayer.isSneaking()?0.4F:0.7F);
            drawTexturedRect(-8.0D, -8.0D, 16.0D, 16.0D, 0.0D, 0.0D, 1.0D, 1.0D);
            GlStateManager.popMatrix();
            drawPlayerHead(this.mc.thePlayer.getName(), -2.0D, -2.0D, 4.0D, 4.0D);
            GlStateManager.popMatrix();
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.buttonRefresh.render(GuiIcons.refresh);
        this.buttonClose.render(GuiIcons.accept);
    }

    @Override
    public void confirmClicked(boolean set, int id) {
        this.openGui();
        this.refreshWidgets();
    }

    static {
        TEX_FILLED = new TextureCoords(TEX_CHUNK_CLAIMING, 0.0D, 0.0D, 0.5D, 1.0D);
        TEX_BORDER = new TextureCoords(TEX_CHUNK_CLAIMING, 0.5D, 0.0D, 1.0D, 1.0D);
        textureID = -1;
        pixelBuffer = null;
    }

    public class MapButton extends ButtonLM {
        public final ChunkDimPos chunkPos;

        public MapButton(int x, int y, int i) {
            super((double)x, (double)y, 16.0D, 16.0D);
            this.posX += (double)(i % 15) * this.width;
            this.posY += (double)(i / 15) * this.height;
            this.chunkPos = new ChunkDimPos(GuiBiomeScanner.this.currentDim, GuiBiomeScanner.this.startX + i % 15, GuiBiomeScanner.this.startZ + i / 15);
        }

        public void onClicked(@Nonnull GuiLM gui, @Nonnull MouseButton button) {
            if(!gui.isMouseOver(GuiBiomeScanner.this.panelButtons)) {
                if(button.isLeft()) {
                    //todo scan the chunk
                }
                GuiLM.playClickSound();
            }
        }

        public void addMouseOverText(GuiLM gui, List<String> l) {
            TileEntityBiomeScanner te = (TileEntityBiomeScanner) mc.theWorld.getTileEntity(pos);
            if (te == null) return;
            Gson gson = new Gson();
            HashMap<String, String> ret = gson.fromJson(te.toJson(), HashMap.class);


            if (ret.get(chunkPos.chunkXPos + "/" + chunkPos.chunkZPos) != null)
                l.add(ret.get(chunkPos.chunkXPos + "/" + chunkPos.chunkZPos));
            l.add("{" + chunkPos.chunkXPos + ", " + chunkPos.chunkZPos + "}");
            l.add(mc.theWorld.getBiomeForCoordsBody(new BlockPos(chunkPos.chunkXPos * 16 + 8, 0, chunkPos.chunkZPos * 16 + 8)).getBiomeName());

        }

        public void renderWidget(GuiLM gui) {
            ClaimedChunk chunk = FTBUWorldDataSP.getChunk(this.chunkPos);
            double ax = this.getAX();
            double ay = this.getAY();
            if(chunk != null) {
                FTBLibClient.setTexture(GuiBiomeScanner.TEX_CHUNK_CLAIMING);
                ForgeTeam team = chunk.owner.getTeam();
                if(team != null) {
                    FTBLibClient.setGLColor(team.getColor().color, 180);
                } else {
                    GlStateManager.color(0.0F, 0.0F, 0.0F, 0.7058824F);
                }

                GuiLM.drawTexturedRect(ax, ay, 16.0D, 16.0D, GuiBiomeScanner.TEX_FILLED.minU, GuiBiomeScanner.TEX_FILLED.minV, GuiBiomeScanner.TEX_FILLED.maxU, GuiBiomeScanner.TEX_FILLED.maxV);
                GlStateManager.color(chunk.loaded && team != null && team.getStatus(ForgeWorldSP.inst.clientPlayer).isAlly()?1.0F:0.0F, chunk.isChunkOwner(ForgeWorldSP.inst.clientPlayer)?0.27F:0.0F, 0.0F, 0.78F);
                GuiLM.drawTexturedRect(ax, ay, 16.0D, 16.0D, GuiBiomeScanner.TEX_BORDER.minU, GuiBiomeScanner.TEX_BORDER.minV, GuiBiomeScanner.TEX_BORDER.maxU, GuiBiomeScanner.TEX_BORDER.maxV);
            }

            if(gui.isMouseOver(this)) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 0.27F);
                GuiLM.drawBlankRect(ax, ay, 16.0D, 16.0D);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            }

        }
    }
}
