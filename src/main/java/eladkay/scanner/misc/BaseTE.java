package eladkay.scanner.misc;

import cofh.api.energy.IEnergyReceiver;
import net.darkhax.tesla.api.BaseTeslaContainer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class BaseTE extends TileEntity implements IEnergyReceiver {
    protected BaseTeslaContainer container;
    private int max;
    public BaseTE(int max) {
        this.max = max;
        this.container = new BaseTeslaContainer(max, max, max);
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
        this.container = new BaseTeslaContainer(nbt.getCompoundTag("TeslaContainer"));

    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setTag("TeslaContainer", this.container.serializeNBT());
        return nbt;
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        int energyReceived = Math.min(max - (int) container.getStoredPower(), maxReceive);
        if (!simulate) {
            container.givePower(energyReceived, simulate);
            markDirty();
        }
        return energyReceived;
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return (int) container.getStoredPower();
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
        SPacketUpdateTileEntity packer = new SPacketUpdateTileEntity(getPos(), 0, nbt);
        return packer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability == TeslaCapabilities.CAPABILITY_CONSUMER || capability == TeslaCapabilities.CAPABILITY_HOLDER ? (T) this.container : super.getCapability(capability, facing);

    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == TeslaCapabilities.CAPABILITY_CONSUMER || capability == TeslaCapabilities.CAPABILITY_HOLDER || super.hasCapability(capability, facing);
    }
}
