package eladkay.scanner.compat;

import eladkay.scanner.blocks.BlockBiomeScanner;
import eladkay.scanner.tiles.BaseTE;
import eladkay.scanner.networking.MessageUpdateEnergyServer;
import eladkay.scanner.networking.NetworkHelper;
import eladkay.scanner.blocks.BlockTerrainScanner;
import eladkay.scanner.tiles.TileEntityTerrainScanner;
import mcp.mobius.waila.api.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.List;

@WailaPlugin
public class Hwyla implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        registrar.registerComponentProvider(new ScannerComponentProvider(), TooltipPosition.BODY, BlockTerrainScanner.class);
        registrar.registerBlockDataProvider(new ScannerNBTProvider(), BlockTerrainScanner.class);

        registrar.registerComponentProvider(new ScannerComponentProvider(), TooltipPosition.BODY, BlockBiomeScanner.class);
        registrar.registerBlockDataProvider(new ScannerNBTProvider(), BlockBiomeScanner.class);
    }

    public static class ScannerComponentProvider implements IComponentProvider {
        @Override
        public ItemStack getStack(IDataAccessor accessor, IPluginConfig config) {
            return accessor.getStack();
        }

        @Override
        public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
            TileEntity tileEntity = accessor.getTileEntity();
            if (!(tileEntity instanceof BaseTE)) return;
            if (((BaseTE) tileEntity).container == null) return;
            NetworkHelper.INSTANCE.sendToServer(new MessageUpdateEnergyServer(accessor.getPosition().getX(), accessor.getPosition().getY(), accessor.getPosition().getZ()));
            int energy = ((BaseTE) tileEntity).getEnergyStored();
            int max = ((BaseTE) tileEntity).getMaxEnergyStored();
            tooltip.add(new TranslationTextComponent("waila.scanner.energy").append(": " + energy + "/" + max));
            if (tileEntity instanceof TileEntityTerrainScanner) {
                ITextComponent owner = ((TileEntityTerrainScanner) tileEntity).placerName;
                if(owner != null) {
                    if (!"".equals(owner.getString())) {
                        tooltip.add(new TranslationTextComponent("waila.scanner.owner").append(": ").append(owner));
                    }
                }
            }
        }
    }

    public static class ScannerNBTProvider implements IServerDataProvider<TileEntity> {
        @Override
        public void appendServerData(CompoundNBT compoundNBT, ServerPlayerEntity serverPlayerEntity, World world, TileEntity tileEntity) {
            BaseTE scanner = (BaseTE) tileEntity;
            compoundNBT.putInt("energy", scanner.getEnergyStored());
            compoundNBT.putInt("max", scanner.getMaxEnergyStored());
            if (scanner instanceof TileEntityTerrainScanner)
                compoundNBT.putString("name", ((TileEntityTerrainScanner) scanner).placerName.getString());
        }
    }
}