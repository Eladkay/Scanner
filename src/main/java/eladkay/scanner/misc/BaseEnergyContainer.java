package eladkay.scanner.misc;


import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * A basic Tesla container that serves as a consumer, producer and holder. Custom
 * implementations do not need to use all three. The INBTSerializable interface is also
 * optional.
 */
public class BaseEnergyContainer implements IEnergyReceiver, IEnergyProvider, IEnergyStorage, INBTSerializable<NBTTagCompound>, net.minecraftforge.energy.IEnergyStorage {

    private final TileEntity te;
    /**
     * The amount of stored Tesla power.
     */
    private long stored;

    /**
     * The maximum amount of Tesla power that can be stored.
     */
    private long capacity;

    /**
     * The maximum amount of Tesla power that can be accepted.
     */
    private long inputRate;

    /**
     * The maximum amount of Tesla power that can be extracted
     */
    private long outputRate;

    /**
     * Default constructor. Sets capacity to 5000 and transfer rate to 50. This constructor
     * will not set the amount of stored power. These values are arbitrary and should not be
     * taken as a base line for balancing.
     */
    public BaseEnergyContainer(TileEntity tileEntity) {

        this(5000, 50, 50, tileEntity);
    }

    /**
     * Constructor for setting the basic values. Will not construct with any stored power.
     *  @param capacity The maximum amount of Tesla power that the container should hold.
     * @param input    The maximum rate of power that can be accepted at a time.
     * @param output   The maximum rate of power that can be extracted at a time.
     * @param te
     */
    public BaseEnergyContainer(long capacity, long input, long output, TileEntity tileEntity) {
        this(0, capacity, input, output, tileEntity);
    }

    /**
     * Constructor for setting all of the base values, including the stored power.
     *
     * @param power    The amount of stored power to initialize the container with.
     * @param capacity The maximum amount of Tesla power that the container should hold.
     * @param input    The maximum rate of power that can be accepted at a time.
     * @param output   The maximum rate of power that can be extracted at a time.
     */
    public BaseEnergyContainer(long power, long capacity, long input, long output, TileEntity tileEntity) {

        this.stored = power;
        this.capacity = capacity;
        this.inputRate = input;
        this.outputRate = output;
        this.te = tileEntity;
    }

    /**
     * Constructor for creating an instance directly from a compound tag. This expects that the
     * compound tag has some of the required data. @See {@link #deserializeNBT(NBTTagCompound)}
     * for precise info on what is expected. This constructor will only set the stored power if
     * it has been written on the compound tag.
     *
     * @param dataTag The NBTCompoundTag to read the important data from.
     * @param te
     */
    public BaseEnergyContainer(NBTTagCompound dataTag, TileEntity te) {
        this.te = te;

        this.deserializeNBT(dataTag);
    }


    public void setStored(long stored) {
        this.stored = stored;
    }


    @Override
    public NBTTagCompound serializeNBT() {

        final NBTTagCompound dataTag = new NBTTagCompound();
        dataTag.setLong("TeslaPower", this.stored);
        dataTag.setLong("TeslaCapacity", this.capacity);
        dataTag.setLong("TeslaInput", this.inputRate);
        dataTag.setLong("TeslaOutput", this.outputRate);
        return dataTag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {

        this.stored = nbt.getLong("TeslaPower");

        if (nbt.hasKey("TeslaCapacity"))
            this.capacity = nbt.getLong("TeslaCapacity");

        if (nbt.hasKey("TeslaInput"))
            this.inputRate = nbt.getLong("TeslaInput");

        if (nbt.hasKey("TeslaOutput"))
            this.outputRate = nbt.getLong("TeslaOutput");

        if (this.stored > this.capacity)
            this.stored = this.capacity;
    }

    /**
     * Sets the capacity of the the container. If the existing stored power is more than the
     * new capacity, the stored power will be decreased to match the new capacity.
     *
     * @param capacity The new capacity for the container.
     * @return The instance of the container being updated.
     */
    public BaseEnergyContainer setCapacity(long capacity) {

        this.capacity = capacity;

        if (this.stored > capacity)
            this.stored = capacity;

        return this;
    }

    /**
     * Gets the maximum amount of Tesla power that can be accepted by the container.
     *
     * @return The amount of Tesla power that can be accepted at any time.
     */
    public long getInputRate() {

        return this.inputRate;
    }

    /**
     * Sets the maximum amount of Tesla power that can be accepted by the container.
     *
     * @param rate The amount of Tesla power to accept at a time.
     * @return The instance of the container being updated.
     */
    public BaseEnergyContainer setInputRate(long rate) {

        this.inputRate = rate;
        return this;
    }

    /**
     * Gets the maximum amount of Tesla power that can be pulled from the container.
     *
     * @return The amount of Tesla power that can be extracted at any time.
     */
    public long getOutputRate() {

        return this.outputRate;
    }

    /**
     * Sets the maximum amount of Tesla power that can be pulled from the container.
     *
     * @param rate The amount of Tesla power that can be extracted.
     * @return The instance of the container being updated.
     */
    public BaseEnergyContainer setOutputRate(long rate) {

        this.outputRate = rate;
        return this;
    }

    /**
     * Sets both the input and output rates of the container at the same time. Both rates will
     * be the same.
     *
     * @param rate The input/output rate for the Tesla container.
     * @return The instance of the container being updated.
     */
    public BaseEnergyContainer setTransferRate(long rate) {

        this.setInputRate(rate);
        this.setOutputRate(rate);
        return this;
    }

    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        final long removedPower = Math.min(this.stored, Math.min(this.outputRate, maxExtract));

        if (!simulate)
            this.stored -= removedPower;
        te.markDirty();
        return (int) removedPower;
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {

        final long acceptedTesla = Math.min(this.capacity - this.stored, Math.min(this.inputRate, maxReceive));

        if (!simulate)
            this.stored += acceptedTesla;
        te.markDirty();
        return (int) acceptedTesla;
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return (int) this.stored;
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return (int) capacity;
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return receiveEnergy(null, maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return extractEnergy(null, maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return (int) stored;
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) capacity;
    }

    @Override
    public boolean canExtract() {
        return getEnergyStored() > 0;
    }

    @Override
    public boolean canReceive() {
        return getEnergyStored() < getMaxEnergyStored();
    }
}
