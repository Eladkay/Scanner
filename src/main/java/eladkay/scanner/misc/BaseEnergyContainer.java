package eladkay.scanner.misc;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;

public class BaseEnergyContainer extends EnergyStorage implements INBTSerializable<CompoundNBT> {

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
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();
        compound.putInt("energy", energy);
        compound.putInt("capacity", capacity);
        compound.putInt("maxReceive", maxReceive);
        compound.putInt("maxExtract", maxExtract);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        energy = nbt.getInt("energy");
        capacity = nbt.getInt("capacity");
        maxReceive = nbt.getInt("maxReceive");
        maxExtract = nbt.getInt("maxExtract");
    }
}