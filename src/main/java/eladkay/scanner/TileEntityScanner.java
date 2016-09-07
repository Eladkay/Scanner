package eladkay.scanner;

import cofh.api.energy.IEnergyReceiver;
import net.darkhax.tesla.api.BaseTeslaContainer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;

public class TileEntityScanner extends TileEntity implements IEnergyReceiver, ITickable {

    public static final int MAX = 300000;
    int x = -1;
    int y = -1;
    int z = -1;

    public void onBlockActivated() {
        x = 0;
        y = 0;
        z = 0;
    }

	@Override
	public void update() {
        ChunkProviderServer cps = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(0).getChunkProvider();
        Chunk chunk = cps.provideChunk(pos.getX(), pos.getZ());

        if(x < 0 || y < 0 || z < 0) return;
        x++;
        container.takePower(100000, false);
        markDirty();
        IBlockState block = chunk.getBlockState(x, y, z);
        worldObj.setBlockState(new BlockPos(x + pos.getX(), y, z + pos.getZ()), block, 2);
        if(x >= 16) {
            y++;
            x = 0;
        }
        if(y >= 80) {
            z++;
            y = 0;
        }

        /*for(int x = 0; x < 16; x++)
            for(int z = 0; z < 16; z++)
                for(int y = 0; y < 256; y++) {*/

               // }

                      //  } // if(container.getStoredPower() >= 100000) {//i know this can be compacted

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
        BlockPos pos = BlockPos.fromLong(nbt.getLong("positions"));
        x = pos.getX();
        y = pos.getY();
        z = pos.getZ();

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
        nbt.setTag("TeslaContainer", this.container.serializeNBT());
        nbt.setLong("positions", new BlockPos(x, y, z).toLong());
		return nbt;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        int energyReceived = Math.min(MAX - (int)container.getStoredPower(), maxReceive);
        if (!simulate) {
            container.givePower(energyReceived, simulate);
            markDirty();
        }
        return energyReceived;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return (int)container.getStoredPower();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return MAX;
	}

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        SPacketUpdateTileEntity packer = new SPacketUpdateTileEntity(getPos(), 0, nbt);
        return packer;
    }
    //Tesla
    private BaseTeslaContainer container;

    public TileEntityScanner() {
        this.container = new BaseTeslaContainer(MAX, MAX, MAX);
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability (Capability<T> capability, EnumFacing facing) {
        return capability == TeslaCapabilities.CAPABILITY_CONSUMER || capability == TeslaCapabilities.CAPABILITY_HOLDER ? (T) this.container : super.getCapability(capability, facing);

    }

    @Override
    public boolean hasCapability (Capability<?> capability, EnumFacing facing) {
        return capability == TeslaCapabilities.CAPABILITY_CONSUMER || capability == TeslaCapabilities.CAPABILITY_HOLDER || super.hasCapability(capability, facing);

    }
}
