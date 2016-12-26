package eladkay.scanner.terrain;

import eladkay.scanner.Config;
import eladkay.scanner.compat.Oregistry;
import eladkay.scanner.misc.BaseTE;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.concurrent.ThreadLocalRandom;

public class TileEntityTerrainScanner extends BaseTE implements ITickable {

    public static final String PRESET = "{\"coordinateScale\":684.412,\"heightScale\":684.412,\"lowerLimitScale\":512.0,\"upperLimitScale\":512.0,\"depthNoiseScaleX\":200.0,\"depthNoiseScaleZ\":200.0,\"depthNoiseScaleExponent\":0.5,\"mainNoiseScaleX\":80.0,\"mainNoiseScaleY\":160.0,\"mainNoiseScaleZ\":80.0,\"baseSize\":8.5,\"stretchY\":12.0,\"biomeDepthWeight\":1.0,\"biomeDepthOffset\":0.0,\"biomeScaleWeight\":1.0,\"biomeScaleOffset\":0.0,\"seaLevel\":63,\"useCaves\":true,\"useDungeons\":true,\"dungeonChance\":8,\"useStrongholds\":true,\"useVillages\":true,\"useMineShafts\":true,\"useTemples\":true,\"useMonuments\":true,\"useRavines\":true,\"useWaterLakes\":true,\"waterLakeChance\":4,\"useLavaLakes\":true,\"lavaLakeChance\":80,\"useLavaOceans\":false,\"fixedBiome\":-1,\"biomeSize\":4,\"riverSize\":4,\"dirtSize\":33,\"dirtCount\":10,\"dirtMinHeight\":0,\"dirtMaxHeight\":256,\"gravelSize\":33,\"gravelCount\":8,\"gravelMinHeight\":0,\"gravelMaxHeight\":256,\"graniteSize\":33,\"graniteCount\":10,\"graniteMinHeight\":0,\"graniteMaxHeight\":80,\"dioriteSize\":33,\"dioriteCount\":10,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":80,\"andesiteSize\":33,\"andesiteCount\":10,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":80,\"coalSize\":17,\"coalCount\":20,\"coalMinHeight\":0,\"coalMaxHeight\":128,\"ironSize\":9,\"ironCount\":20,\"ironMinHeight\":0,\"ironMaxHeight\":64,\"goldSize\":9,\"goldCount\":2,\"goldMinHeight\":0,\"goldMaxHeight\":32,\"redstoneSize\":8,\"redstoneCount\":8,\"redstoneMinHeight\":0,\"redstoneMaxHeight\":16,\"diamondSize\":8,\"diamondCount\":1,\"diamondMinHeight\":0,\"diamondMaxHeight\":16,\"lapisSize\":7,\"lapisCount\":1,\"lapisCenterHeight\":16,\"lapisSpread\":16}";
    private static final int MAX = Config.maxEnergyBufferTerrain;
    boolean on;
    MutableBlockPos current = new MutableBlockPos(0, -1, 0);
    //BlockPos pos = null;
    public EnumRotation rotation = EnumRotation.POSX_POSZ;
    public int speedup = 1;
    public BlockPos posStart = null;

    public BlockPos getPosStart() {
        return posStart != null ? posStart : getPos();
    }


    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        current.setPos(BlockPos.fromLong(nbt.getLong("positions")));
        on = nbt.getBoolean("on");
        rotation = EnumRotation.values()[nbt.getInteger("rot")];
        speedup = nbt.getInteger("speedup");
        if (nbt.getLong("posStart") != 0)
            posStart = BlockPos.fromLong(nbt.getLong("posStart"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setLong("positions", current.toLong());
        nbt.setBoolean("on", on);
        nbt.setInteger("rot", rotation.ordinal());
        nbt.setInteger("speedup", speedup);
        if (posStart != null)
            nbt.setLong("posStart", posStart.toLong());
        return nbt;

    }

    public TileEntityTerrainScanner() {
        super(MAX);

    }

    public void onBlockActivated() {
        if (current.getY() < 0) {
            current.setPos(getPos().getX() + 1, 0, getPos().getZ());
            changeState(true);
        }
    }

    public void activate() {
        changeState(true);
        current.setPos(getPosStart().getX() + 1, 0, getPosStart().getZ());
        MessageUpdateScanner.send(this);
    }


    public void deactivate() {
        changeState(false);
    }

    BlockPos getEnd() {
        return getPosStart().east().add(15, 255, 15);
    }

    void changeState(boolean state) {
        on = state;
        /*worldObj.setBlockState(pos, worldObj.getBlockState(pos).withProperty(BlockTerrainScanner.ONOFF, state));
        worldObj.setTileEntity(pos, this);*/
        markDirty();
        try {
            worldObj.markAndNotifyBlock(getPos(), worldObj.getChunkFromBlockCoords(getPos()), worldObj.getBlockState(getPos()), worldObj.getBlockState(getPos()).withProperty(BlockTerrainScanner.ONOFF, state), 4);
            worldObj.notifyBlockUpdate(getPos(), worldObj.getBlockState(getPos()), worldObj.getBlockState(getPos()).withProperty(BlockTerrainScanner.ONOFF, state), 3);
        } catch (IllegalArgumentException ignored) {
        }
        worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
        MessageUpdateScanner.send(this);
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        int ret = super.receiveEnergy(from, maxReceive, simulate);
        //MessageUpdateScanner.send(this);
        return ret;
    }


    @Override
    public void update() {
        for (int j = 0; j < speedup; j++) {
            if (this.worldObj.isRemote || !on)
                return; //Dont do stuff client side else we get ghosts
            if (container.getEnergyStored() < Config.energyPerBlockTerrainScanner) {
                changeState(false);
                return;
            }
            WorldServer remoteWorld;
            if (worldObj.provider.getDimension() == -1)
                remoteWorld = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(Config.dimid + 1);
            else if (worldObj.provider.getDimension() == 1)
                remoteWorld = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(Config.dimid + 2);
            else
                remoteWorld = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(Config.dimid);
            changeState(true);

            IBlockState remote = remoteWorld.getBlockState(current);
            IBlockState local = worldObj.getBlockState(current);
            TileEntity remoteTE = remoteWorld.getTileEntity(current);
            BlockPos imm = current.toImmutable();
            if (local.getBlock().isReplaceable(worldObj, imm) || local.getBlock().isAir(local, worldObj, imm)) {
                worldObj.setBlockState(imm, remote, 2);
                if (remoteTE != null) {
                    NBTTagCompound tag = new NBTTagCompound();
                    remoteTE.writeToNBT(tag);
                    worldObj.getTileEntity(imm).writeToNBT(tag);
                }
                if (!remote.getBlock().isAir(remote, worldObj, imm))
                    container.extractEnergy(Config.energyPerBlockTerrainScanner, false);
            }

            if (Config.genVanillaOres && worldObj.getBlockState(current).getBlock() == Blocks.STONE) {
                if (current.getY() > 8) {
                    int i = ThreadLocalRandom.current().nextInt(25);
                    if (i == 0)
                        worldObj.setBlockState(current, Blocks.COAL_ORE.getDefaultState(), 2);
                    else if (i == 1)
                        worldObj.setBlockState(current, Blocks.IRON_ORE.getDefaultState(), 2);
                }
                if (current.getY() > 8 && current.getY() < 16) {
                    int i = ThreadLocalRandom.current().nextInt(150);
                    if (i == 0)
                        worldObj.setBlockState(current, Blocks.DIAMOND_ORE.getDefaultState(), 2);
                    else if (i == 1)
                        worldObj.setBlockState(current, Blocks.EMERALD_ORE.getDefaultState(), 2);
                    else if (i == 2)
                        worldObj.setBlockState(current, Blocks.REDSTONE_ORE.getDefaultState(), 2);
                    else if (i == 3)
                        worldObj.setBlockState(current, Blocks.LAPIS_ORE.getDefaultState(), 2);
                }
                if (current.getY() > 8 && current.getY() < 32) {
                    int i = ThreadLocalRandom.current().nextInt(45);
                    if (i == 0)
                        worldObj.setBlockState(current, Blocks.GOLD_ORE.getDefaultState(), 2);
                }
            }
            Oregistry.getEntryList().stream().filter(entry -> current.getY() < entry.maxY && current.getY() > entry.minY).forEach(entry -> {
                int i = ThreadLocalRandom.current().nextInt(entry.rarity);
                if (i == 0) worldObj.setBlockState(current, entry.ore, 2);
            });

            //Movement needs to happen BELOW oregen else things get weird and desynced
            if (rotation.x > 0) current = new MutableBlockPos(current.east());
            else new MutableBlockPos(current.west()); //X++
            BlockPos end = this.getEnd(); //We do this lazy load do it can cache the right value

            if (current.getX() > end.getX()) {
                if (rotation == EnumRotation.NEGX_POSZ || rotation == EnumRotation.POSX_POSZ)
                    current = new MutableBlockPos(current.south());
                else current = new MutableBlockPos(current.north());
                current.setPos(getPosStart().getX(), current.getY(), current.getZ());
            }
            if (current.getZ() > end.getZ() && rotation.z > 0 || current.getZ() < end.getZ() && rotation.z < 0) {
                current.setPos(getPosStart().getX(), current.getY() + 1, getPosStart().getZ());
            }
            if (current.getY() > 255)
                changeState(false);

            markDirty();
        }
        MessageUpdateScanner.send(this);
    }


}

