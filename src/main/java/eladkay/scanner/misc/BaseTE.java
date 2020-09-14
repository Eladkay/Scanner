package eladkay.scanner.misc;

import cofh.redstoneflux.api.IEnergyReceiver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;

public class BaseTE extends TileEntity implements IEnergyReceiver {
    public BaseEnergyContainer container;
    private int max;

    public BaseTE(int max) {
        this.max = max;
        if (max != 0)
            this.container = new BaseEnergyContainer(max, max);
        else this.container = null;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.max = nbt.getInteger("max");
        if (max != 0) {
            this.container = new BaseEnergyContainer(max, max);
            this.container.deserializeNBT(nbt.getCompoundTag("TeslaContainer"));
        } else this.container = null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("max", max);
        if (this.container != null)
            nbt.setTag("TeslaContainer", this.container.serializeNBT());
        return nbt;
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        if (this.container == null) return 0;
        int energyReceived = Math.min(max - container.getEnergyStored(), maxReceive);
        if (!simulate) {
            energyReceived = container.receiveEnergy(energyReceived, false);
            markDirty();
        }
        return energyReceived;
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        if (this.container == null) return 0;
        return container.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return max;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        return new SPacketUpdateTileEntity(getPos(), 0, nbt);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return container == null ? super.hasCapability(capability, facing) : capability == CapabilityEnergy.ENERGY || capability == BaseEnergyContainer.CAPABILITY_CONSUMER || super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || capability == BaseEnergyContainer.CAPABILITY_CONSUMER ? container != null ? (T) container : super.getCapability(capability, facing) : super.getCapability(capability, facing);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);
        readFromNBT(packet.getNbtCompound());
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }
}
