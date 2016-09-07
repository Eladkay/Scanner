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

	public static void initConfig(File configFile) {
		Configuration config = new Configuration(configFile);
		config.load();
		alignChunks = config.get("Scanner", "align_scanner_to_grid", false, "Align scanner iterations to the chunk grid").getBoolean();
		energyPerBlock = config.get("Scanner", "energy_per_block", 100, "The amount of energy required in the block to spawn 1 block").getInt();
		maxEnergyBuffer = config.get("Scanner", "max_energy_buffer", 300000, "The amount of energy required in the block to spawn 1 block").getInt();
		config.save();
	}

}