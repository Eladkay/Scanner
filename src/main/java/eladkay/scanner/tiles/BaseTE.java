package eladkay.scanner.tiles;

import eladkay.scanner.misc.BaseEnergyContainer;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BaseTE extends TileEntity implements IEnergyStorage {
    public BaseEnergyContainer container;
    private int max;

    public BaseTE(TileEntityType<? extends BaseTE> type, int max) {
        super(type);
        this.max = max;
        if (max != 0)
            this.container = new BaseEnergyContainer(max, max);
        else this.container = null;
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = new CompoundNBT();
        save(tag);
        return tag;
    }

    @Override
    public void load(BlockState blockState, CompoundNBT nbt) {
        super.load(blockState, nbt);
        this.max = nbt.getInt("max");
        if (max != 0) {
            this.container = new BaseEnergyContainer(max, max);
            this.container.deserializeNBT(nbt.getCompound("Container"));
        } else this.container = null;
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        nbt.putInt("max", max);
        if (this.container != null)
            nbt.put("Container", this.container.serializeNBT());
        return nbt;
    }

    public boolean canExtract() {
        return false;
    }

    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (this.container == null) return 0;
        int energyReceived = Math.min(max - container.getEnergyStored(), maxReceive);
        if (!simulate) {
            energyReceived = container.receiveEnergy(energyReceived, false);
            setChanged();
        }
        return energyReceived;
    }

    @Override
    public int getEnergyStored() {
        if (this.container == null) return 0;
        return container.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return max;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        save(nbt);
        return new SUpdateTileEntityPacket(getBlockPos(), 0, nbt);
    }

    /*@Override
    public boolean hasCapability(Capability<?> capability, Direction direction) {
        return container == null ? super.hasCapability(capability, facing) : capability == CapabilityEnergy.ENERGY || capability == BaseEnergyContainer.CAPABILITY_CONSUMER || super.hasCapability(capability, facing);
    }*/

    @Override
    public @Nonnull <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> capability, final @Nullable Direction side) {
        if(capability == CapabilityEnergy.ENERGY) {
            if(container != null) {
                return LazyOptional.of(() -> container).cast();
            }
        }
        return super.getCapability(capability, side);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        super.onDataPacket(net, packet);
        load(this.getBlockState(), packet.getTag());
    }

    /*@Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }*/
}
