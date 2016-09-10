//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package eladkay.scanner.biome;

import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbu.gui.GuiClaimChunks;
import com.latmod.lib.PixelBuffer;
import com.latmod.lib.math.MathHelperLM;
import com.latmod.lib.util.LMColorUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ThreadReloadArea extends Thread {
    public static final PixelBuffer pixels = new PixelBuffer(256, 256);
    private static final Map<IBlockState, Integer> colorCache = new HashMap();
    private static MutableBlockPos currentBlockPos = new MutableBlockPos(0, 0, 0);
    public final World worldObj;
    public final GuiBiomeScanner gui;

    public ThreadReloadArea(World w, GuiBiomeScanner m) {
        super("MapReloader");
        this.setDaemon(true);
        this.worldObj = w;
        this.gui = m;
    }

    private static int getBlockColor(IBlockState state) {
        Integer col = (Integer)colorCache.get(state);
        if(col == null) {
            col = -16777216 | getBlockColor0(state);
            colorCache.put(state, col);
        }

        return col;
    }

    private static int getBlockColor0(IBlockState state) {
        Block b = state.getBlock();
        if(b == Blocks.SANDSTONE) {
            return MapColor.SAND.colorValue;
        } else if(b == Blocks.FIRE) {
            return MapColor.RED.colorValue;
        } else if(b == Blocks.YELLOW_FLOWER) {
            return MapColor.YELLOW.colorValue;
        } else if(b == Blocks.LAVA) {
            return MapColor.ADOBE.colorValue;
        } else if(b == Blocks.END_STONE) {
            return MapColor.SAND.colorValue;
        } else if(b == Blocks.OBSIDIAN) {
            return 1376327;
        } else if(b == Blocks.GRAVEL) {
            return 9279387;
        } else if(b == Blocks.GRASS) {
            return 7650428;
        } else {
            if(b == Blocks.RED_FLOWER) {
                switch(((EnumFlowerType)state.getValue(Blocks.RED_FLOWER.getTypeProperty())).ordinal()) {
                    case 1:
                        return MapColor.YELLOW.colorValue;
                    case 2:
                        return MapColor.RED.colorValue;
                    case 3:
                        return MapColor.LIGHT_BLUE.colorValue;
                    case 4:
                        return MapColor.MAGENTA.colorValue;
                    case 5:
                        return MapColor.SILVER.colorValue;
                    case 6:
                        return MapColor.RED.colorValue;
                    case 7:
                        return MapColor.ADOBE.colorValue;
                    case 8:
                        return MapColor.SNOW.colorValue;
                    case 9:
                        return MapColor.PINK.colorValue;
                    case 10:
                        return MapColor.SILVER.colorValue;
                }
            } else if(b == Blocks.PLANKS) {
                switch(state.getValue(BlockPlanks.VARIANT).ordinal()) {
                    case 1:
                        return 13015113;
                    case 2:
                        return 8150574;
                    case 3:
                        return 15917203;
                    case 4:
                        return 13006419;
                    case 5:
                        return 14712638;
                    case 6:
                        return 5319956;
                }
            }

            return state.getMapColor().colorValue;
        }
    }

    public void run() {
        Arrays.fill(pixels.pixels, 0);
        int startY = Minecraft.getMinecraft().thePlayer.getPosition().getY();

        try {
            for(int cz = 0; cz < 15; ++cz) {
                for(int cx = 0; cx < 15; ++cx) {
                    Chunk chunk = this.worldObj.getChunkProvider().getLoadedChunk(this.gui.startX + cx, this.gui.startZ + cz);
                    if(chunk != null) {
                        int x = (this.gui.startX + cx) * 16;
                        int z = (this.gui.startZ + cz) * 16;

                        for(int wz = 0; wz < 16; ++wz) {
                            for(int wx = 0; wx < 16; ++wx) {
                                for(int by = Math.max(255, chunk.getTopFilledSegment() + 15); by > 0; --by) {
                                    IBlockState e = chunk.getBlockState(wx, by, wz);
                                    currentBlockPos.setPos(x + wx, by, z + wz);
                                    if(e.getBlock() != Blocks.TALLGRASS && !this.worldObj.isAirBlock(currentBlockPos)) {
                                        int color = getBlockColor(e);
                                        color = LMColorUtils.addBrightness(color, MathHelperLM.clampInt(by - startY, -30, 30) * 5);
                                        pixels.setRGB(cx * 16 + wx, cz * 16 + wz, color);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    GuiClaimChunks.pixelBuffer = FTBLibClient.toByteBuffer(pixels.pixels, false);
                }
            }
        } catch (Exception var12) {
            var12.printStackTrace();
        }

        GuiClaimChunks.pixelBuffer = FTBLibClient.toByteBuffer(pixels.pixels, false);
    }
}
