package eladkay.scanner.misc;

import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class GuiHelper
{
    @Nullable
    public static TileEntity getTile(EntityPlayer player, @Nullable NBTTagCompound data)
    {
        return data == null ? null : player.worldObj.getTileEntity(new BlockPos(data.getInteger("X"), data.getInteger("Y"), data.getInteger("Z")));
    }

    public static NBTTagCompound getPosData(BlockPos pos)
    {
        NBTTagCompound data = new NBTTagCompound();
        data.setInteger("X", pos.getX());
        data.setInteger("Y", pos.getY());
        data.setInteger("Z", pos.getZ());
        return data;
    }

    @Nullable
    public static NBTTagCompound getPosData(@Nullable TileEntity tile)
    {
        return (tile == null) ? null : getPosData(tile.getPos());
    }

    public static void drawTexturedRect(int x, int y, int w, int h, double u0, double v0, double u1, double v1)
    {
        if(u0 == 0D && v0 == 0D && u1 == 0D && v1 == 0D)
        {
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer buffer = tessellator.getBuffer();
            buffer.begin(7, DefaultVertexFormats.POSITION);
            buffer.pos(x, y + h, 0D).endVertex();
            buffer.pos(x + w, y + h, 0D).endVertex();
            buffer.pos(x + w, y, 0D).endVertex();
            buffer.pos(x, y, 0D).endVertex();
            tessellator.draw();
        }
        else
        {
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer buffer = tessellator.getBuffer();
            buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(x, y + h, 0D).tex(u0, v1).endVertex();
            buffer.pos(x + w, y + h, 0D).tex(u1, v1).endVertex();
            buffer.pos(x + w, y, 0D).tex(u1, v0).endVertex();
            buffer.pos(x, y, 0D).tex(u0, v0).endVertex();
            tessellator.draw();
        }
    }

    public static void drawPlayerHead(String username, int x, int y, int w, int h)
    {
        FTBLibClient.setTexture(FTBLibClient.getSkinTexture(username));
        drawTexturedRect(x, y, w, h, 0.125D, 0.125D, 0.25D, 0.25D);
        drawTexturedRect(x, y, w, h, 0.625D, 0.125D, 0.75D, 0.25D);
    }

    public static void drawBlankRect(int x, int y, int w, int h)
    {
        GlStateManager.disableTexture2D();
        drawTexturedRect(x, y, w, h, 0D, 0D, 0D, 0D);
        GlStateManager.enableTexture2D();
    }

    public static void drawCenteredString(FontRenderer font, String txt, double x, double y, int color)
    {
        font.drawString(txt, (int) (x - font.getStringWidth(txt) / 2D), (int) (y - font.FONT_HEIGHT / 2D), color);
    }

    public static void playClickSound()
    {
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1F));
    }

    public static void renderGuiItem(RenderItem itemRender, ItemStack stack, double x, double y)
    {
        itemRender.zLevel = 200F;
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 32F);
        FontRenderer font = stack.getItem().getFontRenderer(stack);

        GlStateManager.enableLighting();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
        GlStateManager.color(1F, 1F, 1F, 1F);
        itemRender.renderItemAndEffectIntoGUI(stack, 0, 0);
        itemRender.renderItemOverlayIntoGUI(font, stack, 0, 0, null);
        GlStateManager.popMatrix();
        itemRender.zLevel = 0F;
    }
}
