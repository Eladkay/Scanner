package eladkay.scanner.misc;

import cofh.api.energy.IEnergyReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

public class BaseTE extends TileEntity implements IEnergyReceiver {
    protected BaseEnergyContainer container;
    private int max;
    public BaseTE(int max) {
        this.max = max;
        this.container = new BaseEnergyContainer(max, max, max);
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
        this.container = new BaseEnergyContainer(nbt.getCompoundTag("TeslaContainer"));

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
        int energyReceived = Math.min(max - container.getEnergyStored(), maxReceive);
        if (!simulate) {
            container.receiveEnergy(energyReceived, simulate);
            markDirty();
            NetworkHelper.tellEveryone(new MessageUpdateEnergy(pos.getX(), pos.getY(), pos.getZ(), container.getEnergyStored()));
        }
        return energyReceived;
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return (int) container.getEnergyStored();
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

}
