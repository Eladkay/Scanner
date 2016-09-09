package eladkay.scanner.terrain;

import eladkay.scanner.Config;
import eladkay.scanner.compat.Oregistry;
import eladkay.scanner.misc.BaseTE;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.concurrent.ThreadLocalRandom;

public class TileEntityTerrainScanner extends BaseTE implements ITickable {

    public static final String PRESET = "{\"coordinateScale\":684.412,\"heightScale\":684.412,\"lowerLimitScale\":512.0,\"upperLimitScale\":512.0,\"depthNoiseScaleX\":200.0,\"depthNoiseScaleZ\":200.0,\"depthNoiseScaleExponent\":0.5,\"mainNoiseScaleX\":80.0,\"mainNoiseScaleY\":160.0,\"mainNoiseScaleZ\":80.0,\"baseSize\":8.5,\"stretchY\":12.0,\"biomeDepthWeight\":1.0,\"biomeDepthOffset\":0.0,\"biomeScaleWeight\":1.0,\"biomeScaleOffset\":0.0,\"seaLevel\":63,\"useCaves\":true,\"useDungeons\":true,\"dungeonChance\":8,\"useStrongholds\":true,\"useVillages\":true,\"useMineShafts\":true,\"useTemples\":true,\"useMonuments\":true,\"useRavines\":true,\"useWaterLakes\":true,\"waterLakeChance\":4,\"useLavaLakes\":true,\"lavaLakeChance\":80,\"useLavaOceans\":false,\"fixedBiome\":-1,\"biomeSize\":4,\"riverSize\":4,\"dirtSize\":33,\"dirtCount\":10,\"dirtMinHeight\":0,\"dirtMaxHeight\":256,\"gravelSize\":33,\"gravelCount\":8,\"gravelMinHeight\":0,\"gravelMaxHeight\":256,\"graniteSize\":33,\"graniteCount\":10,\"graniteMinHeight\":0,\"graniteMaxHeight\":80,\"dioriteSize\":33,\"dioriteCount\":10,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":80,\"andesiteSize\":33,\"andesiteCount\":10,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":80,\"coalSize\":17,\"coalCount\":20,\"coalMinHeight\":0,\"coalMaxHeight\":128,\"ironSize\":9,\"ironCount\":20,\"ironMinHeight\":0,\"ironMaxHeight\":64,\"goldSize\":9,\"goldCount\":2,\"goldMinHeight\":0,\"goldMaxHeight\":32,\"redstoneSize\":8,\"redstoneCount\":8,\"redstoneMinHeight\":0,\"redstoneMaxHeight\":16,\"diamondSize\":8,\"diamondCount\":1,\"diamondMinHeight\":0,\"diamondMaxHeight\":16,\"lapisSize\":7,\"lapisCount\":1,\"lapisCenterHeight\":16,\"lapisSpread\":16}";
    public static final int MAX = Config.maxEnergyBufferTerrain;
    int x = -1;
    int y = -1;
    int z = -1;

    public TileEntityTerrainScanner() {
        super(MAX);

    }

    public void onBlockActivated() {
        if (x < 0 && y < 0 && z < 0) {
            x = 0;
            y = 0;
            z = 0;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        BlockPos pos = BlockPos.fromLong(nbt.getLong("positions"));
        x = pos.getX();
        y = pos.getY();
        z = pos.getZ();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setLong("positions", new BlockPos(x, y, z).toLong());
        return nbt;

    }

    @Override
    public void update() {

        if(worldObj.isRemote) return;
        ChunkProviderServer cps = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(Config.dimid).getChunkProvider();
        Chunk chunk = cps.provideChunk(pos.getX(), pos.getZ());
        //Chunk chunk = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(Config.dimid).getChunkFromBlockCoords(pos);

        if (x < 0 || y < 0 || z < 0 || container.getStoredPower() < Config.energyPerBlockTerrainScanner) return;
        x++;
        container.takePower(Config.energyPerBlockTerrainScanner, false);
        markDirty();
        IBlockState block = chunk.getBlockState(x, y, z);
        /*if (Config.alignChunks)
            worldObj.setBlockState(new BlockPos(chunk.xPosition * x, y, chunk.zPosition * z), block, 2);
        else*/ worldObj.setBlockState(new BlockPos(x + pos.getX(), y, z + pos.getZ()), block, 2);
        if (x >= 16) {
            z++;
            x = 0;
        }
        if (z >= 16) {
            y++;
            z = 0;
        }
        if (y >= 256) {
            x = -1;
            y = -1;
            z = -1;
        }

        if(worldObj.getBlockState(new BlockPos(x + pos.getX(), y, z + pos.getZ())).getBlock() != Blocks.STONE) return;
        if(y > 8) {
            int i = ThreadLocalRandom.current().nextInt(25);
            if(i == 0)
                worldObj.setBlockState(new BlockPos(x + pos.getX(), y, z + pos.getZ()), Blocks.COAL_ORE.getDefaultState(), 2);
            else if(i == 1)
                worldObj.setBlockState(new BlockPos(x + pos.getX(), y, z + pos.getZ()), Blocks.IRON_ORE.getDefaultState(), 2);
        }
        if(y > 8 && y < 16) {
            int i = ThreadLocalRandom.current().nextInt(150);
            if(i == 0)
                worldObj.setBlockState(new BlockPos(x + pos.getX(), y, z + pos.getZ()), Blocks.DIAMOND_ORE.getDefaultState(), 2);
            else if(i == 1)
                worldObj.setBlockState(new BlockPos(x + pos.getX(), y, z + pos.getZ()), Blocks.EMERALD_ORE.getDefaultState(), 2);
            else if(i == 2)
                worldObj.setBlockState(new BlockPos(x + pos.getX(), y, z + pos.getZ()), Blocks.REDSTONE_ORE.getDefaultState(), 2);
            else if(i == 3)
                worldObj.setBlockState(new BlockPos(x + pos.getX(), y, z + pos.getZ()), Blocks.LAPIS_ORE.getDefaultState(), 2);
        }
        if(y > 8 && y < 32) {
            int i = ThreadLocalRandom.current().nextInt(45);
            if(i == 0)
                worldObj.setBlockState(new BlockPos(x + pos.getX(), y, z + pos.getZ()), Blocks.GOLD_ORE.getDefaultState(), 2);
        }
        Oregistry.getEntryList().stream().filter(entry -> y < entry.maxY && y > entry.minY).forEach(entry -> {
            int i = ThreadLocalRandom.current().nextInt(entry.rarity);
            if (i == 0) worldObj.setBlockState(new BlockPos(x + pos.getX(), y, z + pos.getZ()), entry.ore, 2);
        });



    }


}

