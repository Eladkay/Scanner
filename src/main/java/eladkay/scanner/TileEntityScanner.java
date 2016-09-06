package eladkay.scanner;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.List;

public class TileEntityScanner extends TileEntity implements IEnergyReceiver, IEnergyProvider, ITickable {

	protected EnergyStorage storage = new EnergyStorage(300000);

	@Override
	public void update() {
		Chunk chunk = worldObj.getChunkFromBlockCoords(getPos());
	    /*ChunkProviderOverworld chunkProviderOverworld = new ChunkProviderOverworld(worldObj, worldObj.getSeed(), true, "");
        WorldProviderSurface providerSurface = new WorldProviderSurface();
        //providerSurface.registerWorld(worldObj);
        //providerSurface.createChunkGenerator().populate(chunk.xPosition, chunk.zPosition);
        worldObj.provider.createChunkGenerator().populate(chunk.xPosition, chunk.zPosition);
        chunk = new ChunkGeneratorOverworld(worldObj).provideChunk(chunk.xPosition, chunk.zPosition);
        //chunk.getBlockState()
        for(BlockPos pos : BlockPos.getAllInBox(new BlockPos(chunk.xPosition * 16, 0, chunk.zPosition + 16), new BlockPos(chunk.xPosition * 16 + 15, 256, chunk.zPosition * 16 + 15))) {
            worldObj.setBlockState(pos, chunk.getBlockState(pos));
            //System.out.println(chunk.getBlockState(pos).getBlock() + ""+ pos);
        }
        for(int i = 0; i < 16; i++)
            for(int j = 0; j < 16; j++)
                for(int k = 0; k < 256; k++)
                    worldObj.setBlockState(new BlockPos(16 * chunk.xPosition + i, k, 16 * chunk.zPosition + j), chunk.getBlockState(i, k, j));
        //new WorldGenMinable().generate()*/

		ChunkProviderServer cps = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(0).getChunkProvider();
		cps.provideChunk(chunk.xPosition, chunk.zPosition);
	}

	public static void generateChunks(MinecraftServer server, int x, int z, int width, int height, int dimensionID) {
		ChunkProviderServer cps = server.worldServerForDimension(dimensionID).getChunkProvider();

		List<Chunk> chunks = new ArrayList<Chunk>(width * height);
		for (int i = (x - width / 2); i < (x + width / 2); i++) {
			for (int j = (z - height / 2); j < (z + height / 2); j++) {
				generateChunk(server, i, j, dimensionID);
			}
		}
		for (Chunk c : chunks) {
			cps.unloadAllChunks();
		}
	}

	public static void generateChunk(MinecraftServer server, int x, int z, int dimensionID) {
		//Reference.logger.info(String.format("Loaded Chunk at %s, %s (%s) ", x, z, DimensionManager.getProviderType(dimensionID) != null ? DimensionManager.getProviderType(dimensionID).getName() : dimensionID));

	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);
		storage.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);
		storage.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		return storage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		return storage.extractEnergy(maxExtract, simulate);
	}

	/* IEnergyHandler */
	@Override
	public int getEnergyStored(EnumFacing from) {

		return storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {

		return storage.getMaxEnergyStored();
	}

}
