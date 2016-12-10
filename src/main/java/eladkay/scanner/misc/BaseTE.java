package eladkay.scanner.misc;

import cofh.api.energy.IEnergyReceiver;
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
        this.container = new BaseEnergyContainer(max, max, max, this);
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
        this.container = new BaseEnergyContainer(nbt.getCompoundTag("TeslaContainer"), this);

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
            container.receiveEnergy(energyReceived, false);
            markDirty();
            NetworkHelper.tellEveryone(new MessageUpdateEnergy(pos.getX(), pos.getY(), pos.getZ(), container.getEnergyStored(), worldObj.provider.getDimension()));
        }
        return energyReceived;
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
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
        SPacketUpdateTileEntity packer = new SPacketUpdateTileEntity(getPos(), 0, nbt);
        return packer;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY ? (T) container : super.getCapability(capability, facing);
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
