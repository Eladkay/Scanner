package eladkay.scanner.misc;


import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fml.common.Optional;

@Optional.InterfaceList({
        @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaHolder", modid = "tesla"),
        @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaConsumer", modid = "tesla"),
        @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaProducer", modid = "tesla")
})
public class BaseEnergyContainer extends EnergyStorage implements ITeslaHolder, ITeslaConsumer, ITeslaProducer, INBTSerializable<NBTTagCompound> {

    @CapabilityInject(ITeslaConsumer.class)
    public static Capability<?> CAPABILITY_CONSUMER = null;
    @CapabilityInject(ITeslaProducer.class)
    public static Capability<?> CAPABILITY_PRODUCER = null;
    @CapabilityInject(ITeslaHolder.class)
    public static Capability<?> CAPABILITY_HOLDER = null;

    public void setEnergyStored(long energyStored) {
        this.energy = (int) energyStored;
    }

    public BaseEnergyContainer(int capacity) {
        super(capacity);
    }

    public BaseEnergyContainer(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public BaseEnergyContainer(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    @Override
    @Optional.Method(modid = "tesla")
    public long givePower(long tesla, boolean simulate) {
        return receiveEnergy((int) tesla & 0xEFFFFFFF, simulate);
    }

    @Override
    @Optional.Method(modid = "tesla")
    public long getStoredPower() {
        return getEnergyStored();
    }

    @Override
    @Optional.Method(modid = "tesla")
    public long getCapacity() {
        return getMaxEnergyStored();
    }

    @Override
    @Optional.Method(modid = "tesla")
    public long takePower(long tesla, boolean simulate) {
        return extractEnergy((int) tesla & 0xEFFFFFFF, simulate);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("energy", energy);
        compound.setInteger("capacity", capacity);
        compound.setInteger("maxReceive", maxReceive);
        compound.setInteger("maxExtract", maxExtract);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        energy = nbt.getInteger("energy");
        capacity = nbt.getInteger("capacity");
        maxReceive = nbt.getInteger("maxReceive");
        maxExtract = nbt.getInteger("maxExtract");
    }
}