package eladkay.scanner;

import cofh.api.energy.IEnergyReceiver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TileEntityScanner extends TileEntity implements IEnergyReceiver, ITickable {

	public int energy;
    public static final int MAX = 300000;

	@Override
	public void update() {
		//Chunk chunk = worldObj.getChunkFromBlockCoords(getPos());
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
        }*/
        /*for(int i = 0; i < 16; i++)
            for(int j = 0; j < 16; j++)
                for(int k = 0; k < 256; k++)
                    worldObj.setBlockState(new BlockPos(16 * chunk.xPosition + i, k, 16 * chunk.zPosition + j), chunk.getBlockState(i, k, j));
        //new WorldGenMinable().generate()

		ChunkProviderServer cps = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(0).getChunkProvider();
		cps.chunkGenerator.populate(chunk.xPosition, chunk.zPosition);
        ChunkPrimer primer = new ChunkPrimer();
        Biome.getBiome(1).generateBiomeTerrain(worldObj, worldObj.rand, primer, chunk.xPosition, chunk.zPosition, 25.6);
        for(int i = 0; i < 16; i++)
            for(int j = 0; j < 16; j++)
                for(int k = 0; k < 256; k++)
                    worldObj.setBlockState(new BlockPos(16 * chunk.xPosition + i, k, 16 * chunk.zPosition + j), primer.getBlockState(i, k, j));*/
        ChunkProviderServer cps = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(0).getChunkProvider();
        Chunk chunk = cps.provideChunk(pos.getX(), pos.getZ());
        for(int x = 0; x < 16; x++)
            for(int z = 0; z < 16; z++)
                for(int y = 0; y < 256; y++)
                    if(worldObj.getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.AIR)  //i know this can be compacted
                        if(energy >= 100000) {
                            energy -= 100000;
                            IBlockState block = chunk.getBlockState(x, y, z);
                            worldObj.setBlockState(new BlockPos(x + pos.getX(), y, z + pos.getZ()), block, 2);
                        } //else System.out.println(energy);

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
		energy = nbt.getInteger("energy");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);
		nbt.setInteger("energy", energy);
		return nbt;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        int energyReceived = Math.min(MAX - energy, maxReceive);
        if (!simulate) {
            energy += energyReceived;
        }
        return energyReceived;
	}

	/* IEnergyHandler */
	@Override
	public int getEnergyStored(EnumFacing from) {
		return energy;
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
}
