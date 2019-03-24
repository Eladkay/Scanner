package eladkay.scanner;

import eladkay.scanner.biome.TileEntityBiomeScanner;
import eladkay.scanner.init.ModBlocks;
import eladkay.scanner.terrain.TileEntityScannerQueue;
import eladkay.scanner.terrain.TileEntityTerrainScanner;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static eladkay.scanner.ScannerMod.logger;


@Mod.EventBusSubscriber
public class RegistryEventHandler {


    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(ModBlocks.BLOCKS);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {

        for (Block block : ModBlocks.BLOCKS) {

            event.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName()));

        }

    }

    @SubscribeEvent
    public static void registerTileEntities(ModelRegistryEvent event) {

        GameRegistry.registerTileEntity(TileEntityTerrainScanner.class, "terrainScanner");
        GameRegistry.registerTileEntity(TileEntityScannerQueue.class, "q");
        GameRegistry.registerTileEntity(TileEntityBiomeScanner.class, "biomeScanner");

    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {

        for (Block block: ModBlocks.BLOCKS) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
        }

    }

}
