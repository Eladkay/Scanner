package eladkay.scanner.compat;

import eladkay.scanner.biome.BlockBiomeScanner;
import eladkay.scanner.misc.BaseTE;
import eladkay.scanner.misc.MessageUpdateEnergyServer;
import eladkay.scanner.misc.NetworkHelper;
import eladkay.scanner.terrain.BlockTerrainScanner;
import eladkay.scanner.terrain.TileEntityTerrainScanner;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.client.resources.I18n;
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
            if (!(tileEntity instanceof BaseTE)) return currenttip;
            if (((BaseTE) tileEntity).container == null) return currenttip;
            NetworkHelper.instance.sendToServer(new MessageUpdateEnergyServer(accessor.getPosition().getX(), accessor.getPosition().getY(), accessor.getPosition().getZ()));
            int energy = ((BaseTE) tileEntity).getEnergyStored(accessor.getSide());
            int max = ((BaseTE) tileEntity).getMaxEnergyStored(accessor.getSide());
            currenttip.add(I18n.format("waila.scanner.energy") + ": " + energy + "/" + max);
            if (tileEntity instanceof TileEntityTerrainScanner) {
                String owner = ((TileEntityTerrainScanner) tileEntity).placerName;
                if (!"".equals(owner))
                    currenttip.add(I18n.format("waila.scanner.owner") + ": " + owner);
            }
            return currenttip;
        }

        @Override
        public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            return currenttip;
        }

        @Override
        public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
            BaseTE scanner = (BaseTE) te;
            tag.setInteger("energy", scanner.getEnergyStored(null));
            tag.setInteger("max", scanner.getMaxEnergyStored(null));
            if (scanner instanceof TileEntityTerrainScanner)
                tag.setString("name", ((TileEntityTerrainScanner) scanner).placerName);
            return tag;
        }

    }
}