package eladkay.scanner.biome;

import com.feed_the_beast.ftbl.lib.client.PixelBuffer;
import com.feed_the_beast.ftbl.lib.util.LMColorUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ThreadReloadArea extends Thread {
    public static final PixelBuffer PIXELS = new PixelBuffer(BiomeScanner.TILES_TEX * 16, BiomeScanner.TILES_TEX * 16);
    private static final Map<IBlockState, Integer> COLOR_CACHE = new HashMap<>();
    private static final BlockPos.MutableBlockPos CURRENT_BLOCK_POS = new BlockPos.MutableBlockPos(0, 0, 0);
    public final World worldObj;
    public final GuiBiomeScanner gui;
    public boolean cancelled = false;

    public ThreadReloadArea(World w, GuiBiomeScanner m) {
        super("BiomeScanner_MapReloader");
        setDaemon(true);
        worldObj = w;
        gui = m;
    }

    private static int getBlockColor(IBlockState state) {
        Integer col = COLOR_CACHE.get(state);

        if (col == null) {
            col = 0xFF000000 | getBlockColor0(state);
            COLOR_CACHE.put(state, col);
        }

        return col;
    }

    private static int getBlockColor0(IBlockState state) {
        Block b = state.getBlock();

        if (b == Blocks.SANDSTONE) {
            return MapColor.SAND.colorValue;
        } else if (b == Blocks.FIRE) {
            return MapColor.RED.colorValue;
        } else if (b == Blocks.YELLOW_FLOWER) {
            return MapColor.YELLOW.colorValue;
        } else if (b == Blocks.LAVA) {
            return MapColor.ADOBE.colorValue;
        } else if (b == Blocks.END_STONE) {
            return MapColor.SAND.colorValue;
        } else if (b == Blocks.OBSIDIAN) {
            return 0x150047;
        } else if (b == Blocks.GRAVEL) {
            return 0x8D979B;
        } else if (b == Blocks.GRASS) {
            return 0x74BC7C;
        } else if (b == Blocks.TORCH) {
            return 0xFFA530;
        }
        //else if(b.getMaterial(state) == Material.water)
        //	return LMColorUtils.multiply(MapColor.waterColor.colorValue, b.colorMultiplier(worldObj, pos), 200);
        else if (b == Blocks.RED_FLOWER) {
            switch (state.getValue(Blocks.RED_FLOWER.getTypeProperty())) {
                case DANDELION:
                    return MapColor.YELLOW.colorValue;
                case POPPY:
                    return MapColor.RED.colorValue;
                case BLUE_ORCHID:
                    return MapColor.LIGHT_BLUE.colorValue;
                case ALLIUM:
                    return MapColor.MAGENTA.colorValue;
                case HOUSTONIA:
                    return MapColor.SILVER.colorValue;
                case RED_TULIP:
                    return MapColor.RED.colorValue;
                case ORANGE_TULIP:
                    return MapColor.ADOBE.colorValue;
                case WHITE_TULIP:
                    return MapColor.SNOW.colorValue;
                case PINK_TULIP:
                    return MapColor.PINK.colorValue;
                case OXEYE_DAISY:
                    return MapColor.SILVER.colorValue;
            }
        } else if (b == Blocks.PLANKS) {
            switch (state.getValue(BlockPlanks.VARIANT)) {
                case OAK:
                    return 0xC69849;
                case SPRUCE:
                    return 0x7C5E2E;
                case BIRCH:
                    return 0xF2E093;
                case JUNGLE:
                    return 0xC67653;
                case ACACIA:
                    return 0xE07F3E;
                case DARK_OAK:
                    return 0x512D14;
            }
        }

        //if(b == Blocks.leaves || b == Blocks.vine || b == Blocks.waterlily)
        //	return LMColorUtils.addBrightness(b.colorMultiplier(worldObj, pos), -40);
        //else if(b == Blocks.grass && state.getValue(BlockGrass.SNOWY))
        //	return LMColorUtils.addBrightness(b.colorMultiplier(worldObj, pos), -15);

        return state.getMapColor().colorValue;
    }

    @Override
    public void run() {
        Arrays.fill(PIXELS.getPixels(), 0);
        GuiBiomeScanner.pixelBuffer = LMColorUtils.toByteBuffer(PIXELS.getPixels(), false);

        Chunk chunk;
        int cx, cz, x, z, wx, wz, by, color, topY;
        boolean depth = BiomeScanner.ENABLE_DEPTH.getBoolean();

        int startY = Minecraft.getMinecraft().thePlayer.getPosition().getY();

        try {
            for (cz = 0; cz < BiomeScanner.TILES_GUI; cz++) {
                for (cx = 0; cx < BiomeScanner.TILES_GUI; cx++) {
                    chunk = worldObj.getChunkProvider().getLoadedChunk(gui.startX + cx, gui.startZ + cz);

                    if (chunk != null) {
                        x = (gui.startX + cx) * 16;
                        z = (gui.startZ + cz) * 16;
                        topY = Math.max(255, chunk.getTopFilledSegment() + 15);

                        for (wz = 0; wz < 16; wz++) {
                            for (wx = 0; wx < 16; wx++) {
                                for (by = topY; by > 0; --by) {
                                    if (cancelled) {
                                        return;
                                    }

                                    IBlockState state = chunk.getBlockState(wx, by, wz);

                                    CURRENT_BLOCK_POS.setPos(x + wx, by, z + wz);

                                    if (state.getBlock() != Blocks.TALLGRASS && !worldObj.isAirBlock(CURRENT_BLOCK_POS)) {
                                        color = getBlockColor(state);

                                        if (depth) {
                                            color = LMColorUtils.addBrightness(color, MathHelper.clamp_int(by - startY, -30, 30) * 5);
                                        }

                                        PIXELS.setRGB(cx * 16 + wx, cz * 16 + wz, color);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    GuiBiomeScanner.pixelBuffer = LMColorUtils.toByteBuffer(PIXELS.getPixels(), false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        GuiBiomeScanner.pixelBuffer = LMColorUtils.toByteBuffer(PIXELS.getPixels(), false);
    }
}