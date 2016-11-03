package eladkay.scanner.biome;

import com.feed_the_beast.ftbl.api.config.ConfigValue;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;

/**
 * Created by LatvianModder on 29.09.2016.
 */
public class BiomeScanner {
    public static final int TILES_TEX = 16;
    public static final int TILES_GUI = 15;
    public static final double UV = (double) TILES_GUI / (double) TILES_TEX;

    @ConfigValue(id = "enable_depth", file = "biome_scanner", client = true)
    public static final PropertyBool ENABLE_DEPTH = new PropertyBool(false);
}
