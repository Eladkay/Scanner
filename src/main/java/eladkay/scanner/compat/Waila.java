package eladkay.scanner.compat;

import com.teamwizardry.librarianlib.common.network.PacketHandler;
import eladkay.scanner.biome.BlockBiomeScanner;
import eladkay.scanner.misc.TileEnergyConsumer;
import eladkay.scanner.misc.MessageUpdateEnergyServer;
import eladkay.scanner.terrain.BlockDimensionalRift;
import eladkay.scanner.terrain.BlockTerrainScanner;
import mcp.mobius.waila.api.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class Waila {
    public static void onWailaCall(IWailaRegistrar registrar) {
        registrar.registerBodyProvider(new Scanner(), BlockTerrainScanner.class);
        registrar.registerNBTProvider(new Scanner(), BlockTerrainScanner.class);

        registrar.registerBodyProvider(new Scanner(), BlockBiomeScanner.class);
        registrar.registerNBTProvider(new Scanner(), BlockBiomeScanner.class);

        registrar.registerBodyProvider(new Rift(), BlockDimensionalRift.class);
        registrar.registerNBTProvider(new Rift(), BlockDimensionalRift.class);
    }

    public static class Scanner implements IWailaDataProvider {

        @Override
        public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
            return accessor.getStack();
        }

        @Override
        public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            return currenttip;
        }

        @Override
        public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            TileEntity tileEntity = accessor.getTileEntity();
            if (!(tileEntity instanceof TileEnergyConsumer)) return currenttip;
            if (((TileEnergyConsumer) tileEntity).getContainer() == null) return currenttip;
            PacketHandler.NETWORK.sendToServer(new MessageUpdateEnergyServer(accessor.getPosition().getX(), accessor.getPosition().getY(), accessor.getPosition().getZ()));
            int energy = ((TileEnergyConsumer) tileEntity).getEnergyStored(accessor.getSide());
            int max = ((TileEnergyConsumer) tileEntity).getMaxEnergyStored(accessor.getSide());
            /*int energy = accessor.getNBTData().getInteger("energy");
            int max = accessor.getNBTData().getInteger("max");*/
            currenttip.add("Energy: " + energy + "/" + max);
            return currenttip;
        }

        @Override
        public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            return currenttip;
        }

        @Override
        public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
            TileEnergyConsumer scanner = (TileEnergyConsumer) te;
            tag.setInteger("energy", scanner.getEnergyStored(null));
            tag.setInteger("max", scanner.getMaxEnergyStored(null));
            return tag;
        }

    }

    public static class Rift implements IWailaDataProvider {
        /**
         * Callback used to override the default Waila lookup system.</br>
         * Will be used if the implementing class is registered via {@link IWailaRegistrar}.{@link registerStackProvider}.</br>
         *
         * @param accessor Contains most of the relevant information about the currentPos environment.
         * @param config   Current configuration of Waila.
         * @return null if override is not required, an ItemStack otherwise.
         */
        @Override
        public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
            return accessor.getStack();
        }

        /**
         * Callback used to add lines to one of the three sections of the tooltip (Head, Body, Tail).</br>
         * Will be used if the implementing class is registered via {@link IWailaRegistrar}.{@link registerHeadProvider} client side.</br>
         * You are supposed to always return the modified input currenttip.</br>
         *
         * @param itemStack  Current block scanned, in ItemStack form.
         * @param currenttip Current list of tooltip lines (might have been processed by other providers and might be processed by other providers).
         * @param accessor   Contains most of the relevant information about the currentPos environment.
         * @param config     Current configuration of Waila.
         * @return Modified input currenttip
         */
        @Override
        public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            return currenttip;
        }

        /**
         * Callback used to add lines to one of the three sections of the tooltip (Head, Body, Tail).</br>
         * Will be used if the implementing class is registered via {@link IWailaRegistrar}.{@link registerBodyProvider} client side.</br>
         * You are supposed to always return the modified input currenttip.</br>
         *
         * @param itemStack  Current block scanned, in ItemStack form.
         * @param currenttip Current list of tooltip lines (might have been processed by other providers and might be processed by other providers).
         * @param accessor   Contains most of the relevant information about the currentPos environment.
         * @param config     Current configuration of Waila.
         * @return Modified input currenttip
         */
        @Override
        public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            currenttip.add("Ticks until conversion: " + (BlockDimensionalRift.TileDimensionalRift.TICKS_TO_COMPLETION - accessor.getNBTData().getInteger("ticks")));
            return currenttip;
        }

        /**
         * Callback used to add lines to one of the three sections of the tooltip (Head, Body, Tail).</br>
         * Will be used if the implementing class is registered via {@link IWailaRegistrar}.{@link registerTailProvider} client side.</br>
         * You are supposed to always return the modified input currenttip.</br>
         *
         * @param itemStack  Current block scanned, in ItemStack form.
         * @param currenttip Current list of tooltip lines (might have been processed by other providers and might be processed by other providers).
         * @param accessor   Contains most of the relevant information about the currentPos environment.
         * @param config     Current configuration of Waila.
         * @return Modified input currenttip
         */
        @Override
        public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            return currenttip;
        }

        /**
         * Callback used server side to return a custom synchronization NBTTagCompound.</br>
         * Will be used if the implementing class is registered via {@link IWailaRegistrar}.{@link registerNBTProvider} server and client side.</br>
         * You are supposed to always return the modified input NBTTagCompound tag.</br>
         *
         * @param player The player requesting data synchronization (The owner of the currentPos connection).
         * @param te     The TileEntity targeted for synchronization.
         * @param tag    Current synchronization tag (might have been processed by other providers and might be processed by other providers).
         * @param world  TileEntity's World.
         * @param pos    Position of the TileEntity.
         * @return Modified input NBTTagCompound tag.
         */
        @Override
        public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
            tag.setInteger("ticks", ((BlockDimensionalRift.TileDimensionalRift) te).ticks);
            return tag;
        }
    }
}


