package eladkay.scanner;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Created by Saad on 9/7/2016.
 */
public class Config {

	public static boolean alignChunks;
	public static int energyPerBlock;
	public static int maxEnergyBuffer;
    public static int dimid;

    //public static int arbitraryOreSpawnCount;
	public static void initConfig(File configFile) {
		Configuration config = new Configuration(configFile);
		config.load();
		alignChunks = config.get("Scanner", "align_scanner_to_grid", false, "Align scanner iterations to the chunk grid").getBoolean();
		energyPerBlock = config.get("Scanner", "energy_per_block", 100, "The amount of energy required in the block to spawn 1 block").getInt();
		maxEnergyBuffer = config.get("Scanner", "max_energy_buffer", 300000, "The energy buffer of the scanner").getInt();
        dimid = config.get("scanner", "dimid", 99, "The ID for the fake overworld dimension").getInt();
		//arbitraryOreSpawnCount = config.get("Scanner", "arbitrary_ore_spawn_count", 150, "How rarely non-vanilla ores spawn (Higher is rarer, diamond is 150, coal is 25)").getInt();
		config.save();
	}

}