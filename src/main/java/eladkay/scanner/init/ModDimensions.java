package eladkay.scanner.init;

import eladkay.scanner.ScannerMod;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraft.world.World;

public class ModDimensions {
    public static RegistryKey<World> FAKE_OVERWORLD = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(ScannerMod.MODID, "minecraft_overworld"));
    public static RegistryKey<Dimension> FAKE_OVERWORLD_DIMENSION = RegistryKey.create(Registry.LEVEL_STEM_REGISTRY, new ResourceLocation(ScannerMod.MODID, "minecraft_overworld"));
}
