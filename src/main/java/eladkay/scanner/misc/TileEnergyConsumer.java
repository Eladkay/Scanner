package eladkay.scanner.misc;

import cofh.api.energy.IEnergyReceiver;
import com.teamwizardry.librarianlib.common.base.block.TileMod;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

public class TileEnergyConsumer extends TileMod implements IEnergyReceiver {

	private BaseEnergyContainer container;
	private int max;

	public TileEnergyConsumer(int max) {
		this.max = max;
		if (max != 0)
			this.container = new BaseEnergyContainer(max, max);
		else this.container = null;
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

	public BaseEnergyContainer getContainer() {
		if (shouldHaveContainer())
			if (container == null)
				return container = new BaseEnergyContainer(max);
			else return container;
		else return null;
	}

	protected boolean shouldHaveContainer() {
		return max > 0;
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

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return container == null ? super.hasCapability(capability, facing) : capability == CapabilityEnergy.ENERGY || capability == BaseEnergyContainer.CAPABILITY_CONSUMER || super.hasCapability(capability, facing);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return capability == CapabilityEnergy.ENERGY || capability == BaseEnergyContainer.CAPABILITY_CONSUMER ? container != null ? (T) container : super.getCapability(capability, facing) : super.getCapability(capability, facing);
	}
}
