package eladkay.scanner.biome;

import com.feed_the_beast.ftbl.FTBLibFinals;
import com.feed_the_beast.ftbl.lib.client.TextureCoords;
import net.minecraft.util.ResourceLocation;

/**
 * Created by LatvianModder on 29.09.2016.
 */
public class BiomeScanner {
    public static final int TILES_TEX = 16;
    public static final int TILES_GUI = 15;
    public static final int TILES_GUI_HALF = TILES_GUI / 2;
    public static final double UV = (double) TILES_GUI / (double) TILES_TEX;

    public static final ResourceLocation TEX_ENTITY = new ResourceLocation(FTBLibFinals.MOD_ID, "textures/gui/entity.png");
    public static final ResourceLocation TEX_CHUNK_CLAIMING = new ResourceLocation(FTBLibFinals.MOD_ID, "textures/gui/chunk_textures.png");
    public static final TextureCoords TEX_FILLED = TextureCoords.fromUV(TEX_CHUNK_CLAIMING, 0D, 0D, 0.5D, 1D);
    public static final TextureCoords TEX_BORDER = TextureCoords.fromUV(TEX_CHUNK_CLAIMING, 0.5D, 0D, 1D, 1D);
}
