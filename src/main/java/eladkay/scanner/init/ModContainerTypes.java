package eladkay.scanner.init;

import eladkay.scanner.ScannerMod;
import eladkay.scanner.client.container.ScannerQueueContainer;
import eladkay.scanner.client.container.TerrainScannerContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainerTypes {

    public static DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, ScannerMod.MODID);
    public static RegistryObject<ContainerType<ScannerQueueContainer>> SCANNER_QUEUE_MENU = CONTAINER_TYPES.register("scanner_queue", () -> IForgeContainerType.create(ScannerQueueContainer::new));
    public static RegistryObject<ContainerType<TerrainScannerContainer>> TERRAIN_SCANNER_MENU = CONTAINER_TYPES.register("terrain_scanner", () -> IForgeContainerType.create(TerrainScannerContainer::new));
}
