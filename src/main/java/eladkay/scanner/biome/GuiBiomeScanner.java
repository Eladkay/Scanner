package eladkay.scanner.biome;

import com.feed_the_beast.ftbl.api.MouseButton;
import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.client.gui.GuiIcons;
import com.feed_the_beast.ftbl.api.client.gui.GuiLM;
import com.feed_the_beast.ftbl.api.client.gui.GuiLang;
import com.feed_the_beast.ftbl.api.client.gui.widgets.ButtonLM;
import com.feed_the_beast.ftbl.api.client.gui.widgets.PanelLM;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbl.util.TextureCoords;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.net.MessageAreaRequest;
import com.latmod.lib.math.MathHelperLM;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.util.List;

import static org.lwjgl.opengl.Display.getHeight;
import static org.lwjgl.opengl.Display.getWidth;

@SideOnly(Side.CLIENT)
public class GuiBiomeScanner extends GuiLM implements GuiYesNoCallback // implements IClientActionGui
{
    static final int TILES_TEX = 16;
    static final int TILES_GUI = 15;
    private static final double UV = (double) TILES_GUI / (double) TILES_TEX;
    private static final ResourceLocation TEX_ENTITY = new ResourceLocation(FTBUFinals.MOD_ID, "textures/gui/entity.png");
    private static final ResourceLocation TEX_CHUNK_CLAIMING = new ResourceLocation(FTBUFinals.MOD_ID, "textures/gui/chunk_claiming.png");
    private static final TextureCoords TEX_FILLED = TextureCoords.fromUV(TEX_CHUNK_CLAIMING, 0D, 0D, 0.5D, 1D);
    private static final TextureCoords TEX_BORDER = TextureCoords.fromUV(TEX_CHUNK_CLAIMING, 0.5D, 0D, 1D, 1D);
    static ByteBuffer pixelBuffer = null;
    private static int textureID = -1;

    private class MapButton extends ButtonLM
    {
        private final ChunkDimPos chunkPos;
        private final CachedClientData.ChunkData chunkData;

        private MapButton(int x, int y, int i)
        {
            super(x, y, 16, 16);
            posX += (i % TILES_GUI) * getWidth();
            posY += (i / TILES_GUI) * getHeight();
            chunkPos = new ChunkDimPos(startX + (i % TILES_GUI), startZ + (i / TILES_GUI), currentDim);
            chunkData = CachedClientData.CHUNKS.get(chunkPos);
        }


        @Override
        public void addMouseOverText(IGui gui, List<String> l)
        {
            if(chunkData != null)
            {
                if(chunkData.team != null)
                {
                    l.add(chunkData.team.formattedName);

                    l.add(TextFormatting.GREEN + FTBULang.label_cchunks_count.translate());

                    /*if(team.getStatus(ForgeWorldSP.inst.clientPlayer).isAlly())
                    {
                        l.add(chunk.owner.getProfile().getName());
                        if(chunk.loaded)
                        {
                            l.add(TextFormatting.RED + ClaimedChunk.LANG_LOADED.translate());
                        }
                    }*/
                }
            }
            else
            {
                l.add(TextFormatting.DARK_GREEN + FTBULang.CHUNKTYPE_WILDERNESS.translate());
            }
        }

        @Override
        public void renderWidget(IGui gui)
        {
            int ax = (int) getAX();
            int ay = (int) getAY();

            if(chunkData != null)
            {
                FTBLibClient.setTexture(TEX_CHUNK_CLAIMING);

                if(chunkData.team != null)
                {
                    FTBLibClient.setGLColor(chunkData.team.color.getColor(), 180);
                }
                else
                {
                    GlStateManager.color(0F, 0F, 0F, 180F / 255F);
                }

                GuiHelper.drawTexturedRect(ax, ay, 16, 16, TEX_FILLED.getMinU(), TEX_FILLED.getMinV(), TEX_FILLED.getMaxU(), TEX_FILLED.getMaxV());

                //GlStateManager.color((chunk.loaded && team != null && team.getStatus(ForgeWorldSP.inst.clientPlayer).isAlly()) ? 1F : 0F, chunk.isChunkOwner(ForgeWorldSP.inst.clientPlayer) ? 0.27F : 0F, 0F, 0.78F);
                GuiHelper.drawTexturedRect(ax, ay, 16, 16, TEX_BORDER.getMinU(), TEX_BORDER.getMinV(), TEX_BORDER.getMaxU(), TEX_BORDER.getMaxV());
            }

            if(gui.isMouseOver(this))
            {
                GlStateManager.color(1F, 1F, 1F, 0.27F);
                GuiHelper.drawBlankRect(ax, ay, 16, 16);
                GlStateManager.color(1F, 1F, 1F, 1F);
            }
        }

        @Override
        public void onClicked(@Nonnull GuiLM gui, @Nonnull MouseButton button) {
            if(gui.isMouseOver(panelButtons))
            {
                return;
            }

            if(button.isLeft())
            {
                if(GuiScreen.isShiftKeyDown())
                {
                    FTBLibClient.execClientCommand("/ftb chunks load " + chunkPos + ' ' + chunkPos.posZ, false);
                }
                else
                {
                    FTBLibClient.execClientCommand("/ftb chunks claim " + chunkPos.posX + ' ' + chunkPos.posZ, false);
                }
            }
            else if(button.isRight())
            {
                if(GuiScreen.isShiftKeyDown())
                {
                    FTBLibClient.execClientCommand("/ftb chunks unload " + chunkPos.posX + ' ' + chunkPos.posZ, false);
                }
                else
                {
                    FTBLibClient.execClientCommand("/ftb chunks unclaim " + chunkPos.posX + ' ' + chunkPos.posZ, false);
                }
            }

            GuiHelper.playClickSound();
        }
    }

    final int startX, startZ;
    private final int currentDim;
    private final ButtonLM buttonRefresh, buttonClose, buttonUnclaimAll;
    private final MapButton mapButtons[];
    private final PanelLM panelButtons;
    public ThreadReloadArea thread = null;
    private String currentDimName;

    public GuiClaimChunks()
    {
        super(TILES_GUI * 16, TILES_GUI * 16);

        startX = MathHelperLM.chunk(mc.thePlayer.posX) - (int) (TILES_GUI * 0.5D);
        startZ = MathHelperLM.chunk(mc.thePlayer.posZ) - (int) (TILES_GUI * 0.5D);
        currentDim = FTBLibClient.getDim();

        currentDimName = mc.theWorld.provider.getDimensionType().getName();

        buttonClose = new ButtonLM(0, 0, 16, 16, GuiLang.BUTTON_CLOSE.translate())
        {
            @Override
            public void onClicked(IGui gui, IMouseButton button)
            {
                GuiHelper.playClickSound();
                closeGui();
            }
        };

        buttonRefresh = new ButtonLM(0, 16, 16, 16, GuiLang.BUTTON_REFRESH.translate())
        {
            @Override
            public void onClicked(IGui gui, IMouseButton button)
            {
                thread = new ThreadReloadArea(mc.theWorld, GuiClaimChunks.this);
                thread.start();
                new MessageAreaRequest(startX, startZ, TILES_GUI, TILES_GUI).sendToServer();
                GuiHelper.playClickSound();
            }
        };

        buttonUnclaimAll = new ButtonLM(0, 32, 16, 16)
        {
            @Override
            public void onClicked(IGui gui, IMouseButton button)
            {
                GuiHelper.playClickSound();
                String s = GuiScreen.isShiftKeyDown() ? FTBULang.BUTTON_CLAIMS_UNCLAIM_ALL_Q.translate() : FTBULang.BUTTON_CLAIMS_UNCLAIM_ALL_DIM_Q.translate(currentDimName);
                Minecraft.getMinecraft().displayGuiScreen(new GuiYesNo(GuiClaimChunks.this, s, "", GuiScreen.isShiftKeyDown() ? 1 : 0));
            }

            @Override
            public void addMouseOverText(IGui gui, List<String> l)
            {
                l.add(GuiScreen.isShiftKeyDown() ? FTBULang.BUTTON_CLAIMS_UNCLAIM_ALL.translate() : FTBULang.BUTTON_CLAIMS_UNCLAIM_ALL_DIM.translate(currentDimName));
            }
        };

        panelButtons = new PanelLM(0, 0, 16, 0)
        {
            @Override
            public void addWidgets()
            {
                add(buttonClose);
                add(buttonRefresh);
                add(buttonUnclaimAll);

                setHeight(widgets.size() * 16);
            }

            @Override
            public int getAX()
            {
                return getScreenWidth() - 16;
            }

            @Override
            public int getAY()
            {
                return 0;
            }
        };

        mapButtons = new MapButton[TILES_GUI * TILES_GUI];
        for(int i = 0; i < mapButtons.length; i++)
        {
            mapButtons[i] = new MapButton(0, 0, i);
        }
    }

    @Override
    public void onInit()
    {
        buttonRefresh.onClicked(this, MouseButton.LEFT);
    }

    @Override
    public void addWidgets()
    {
        for(MapButton b : mapButtons)
        {
            add(b);
        }

        add(panelButtons);
    }

    @Override
    public void drawBackground()
    {
        super.drawBackground();

        if(textureID == -1)
        {
            textureID = TextureUtil.glGenTextures();
            new MessageAreaRequest(startX, startZ, TILES_GUI, TILES_GUI).sendToServer();
        }

        if(pixelBuffer != null)
        {
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
        GuiHelper.drawBlankRect(posX - 2, posY - 2, getWidth() + 4, getHeight() + 4);
        //drawBlankRect((xSize - 128) / 2, (ySize - 128) / 2, zLevel, 128, 128);
        GlStateManager.color(1F, 1F, 1F, 1F);

        if(thread == null)
        {
            GlStateManager.bindTexture(textureID);
            GuiHelper.drawTexturedRect(posX, posY, TILES_GUI * 16, TILES_GUI * 16, 0D, 0D, UV, UV);
        }

        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.enableTexture2D();
        FTBLibClient.setTexture(TEX_CHUNK_CLAIMING);

        for(MapButton mapButton : mapButtons)
        {
            mapButton.renderWidget(this);
        }

        int cx = MathHelperLM.chunk(mc.thePlayer.posX);
        int cy = MathHelperLM.chunk(mc.thePlayer.posZ);

        if(cx >= startX && cy >= startZ && cx < startX + TILES_GUI && cy < startZ + TILES_GUI)
        {
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

        buttonRefresh.render(GuiIcons.REFRESH);
        buttonClose.render(GuiIcons.ACCEPT);
        buttonUnclaimAll.render(GuiIcons.REMOVE);
    }

    @Override
    public void drawForeground()
    {
        /*
        if(ForgeWorldSP.inst != null && ForgeWorldSP.inst.clientPlayer != null)
        {
            FTBUPlayerDataSP d = FTBUPlayerData.get(ForgeWorldSP.inst.clientPlayer).toSP();
            String s = FTBULang.label_cchunks_count.translateFormatted(d.claimedChunks, d.maxClaimedChunks);
            font.drawString(s, screen.getScaledWidth() - font.getStringWidth(s) - 4, screen.getScaledHeight() - 12, 0xFFFFFFFF);
            s = FTBULang.label_lchunks_count.translateFormatted(d.loadedChunks, d.maxLoadedChunks);
            font.drawString(s, screen.getScaledWidth() - font.getStringWidth(s) - 4, screen.getScaledHeight() - 24, 0xFFFFFFFF);
        }
        */

        super.drawForeground();
    }

    @Override
    public void confirmClicked(boolean set, int id)
    {
        if(set)
        {
            if(id == 1)
            {
                FTBLibClient.execClientCommand("/ftb chunks unclaim_all true", false);
            }
            else
            {
                FTBLibClient.execClientCommand("/ftb chunks unclaim_all false", false);
            }

            new MessageAreaRequest(startX, startZ, TILES_GUI, TILES_GUI).sendToServer();
        }

        openGui();
        refreshWidgets();
    }
}