package eladkay.scanner;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Created by Saad on 9/7/2016.
 */
public class Config {

	//public static boolean alignChunks;
	public static int energyPerBlockTerrainScanner;
	public static int maxEnergyBufferTerrain;
    public static int dimid;

    public static int minEnergyPerChunkBiomeScanner;
    public static int maxEnergyBufferBiome;
    public static int increase;
    public static int cooldown;

    //public static int arbitraryOreSpawnCount;
	public static void initConfig(File configFile) {
		Configuration config = new Configuration(configFile);
		config.load();
		//alignChunks = config.get("Scanner", "align_scanner_to_grid", false, "Align Terrain Scanner iterations to the chunk grid").getBoolean();
		energyPerBlockTerrainScanner = config.get("Scanner", "energyPerBlockTerrainScanner", 100, "The amount of energy required for the Terrain Scanner to spawn 1 block").getInt();
		maxEnergyBufferTerrain = config.get("Scanner", "maxEnergyBufferTerrain", 300000, "The energy buffer of the Terrain Scanner").getInt();
        dimid = config.get("Scanner", "dimid", 99, "The ID for the fake overworld dimension").getInt();
        //arbitraryOreSpawnCount = config.get("Scanner", "arbitrary_ore_spawn_count", 150, "How rarely non-vanilla ores spawn (Higher is rarer, diamond is 150, coal is 25)").getInt();
        minEnergyPerChunkBiomeScanner = config.get("Scanner", "energy_per_block", 10000, "The base amount of energy required for the Biome Scanner to scan one chunk.").getInt();
        maxEnergyBufferBiome = config.get("Scanner", "maxEnergyBufferTerrain", 300000, "The energy buffer of the Terrain Scanner").getInt();
        increase = config.get("Scanner", "increase", 2, "The amount of energy required to run the Biome Scanner will increase by (this * (range - 1)) for every range tier increase").getInt();
        cooldown = config.get("Scanner", "cooldown", 40, "The cooldown between chunk scans (in ticks) for the Biome Scanner").getInt();
		config.save();
	}

}