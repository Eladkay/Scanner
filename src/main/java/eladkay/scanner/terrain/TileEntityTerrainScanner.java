package eladkay.scanner.terrain;

import eladkay.scanner.Config;
import eladkay.scanner.biome.TileEntityBiomeScanner;
import eladkay.scanner.compat.Oregistry;
import eladkay.scanner.misc.BaseTE;
import eladkay.scanner.misc.WtfException;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

public class TileEntityTerrainScanner extends BaseTE implements ITickable {

    public static final String PRESET = "{\"coordinateScale\":684.412,\"heightScale\":684.412,\"lowerLimitScale\":512.0,\"upperLimitScale\":512.0,\"depthNoiseScaleX\":200.0,\"depthNoiseScaleZ\":200.0,\"depthNoiseScaleExponent\":0.5,\"mainNoiseScaleX\":80.0,\"mainNoiseScaleY\":160.0,\"mainNoiseScaleZ\":80.0,\"baseSize\":8.5,\"stretchY\":12.0,\"biomeDepthWeight\":1.0,\"biomeDepthOffset\":0.0,\"biomeScaleWeight\":1.0,\"biomeScaleOffset\":0.0,\"seaLevel\":63,\"useCaves\":true,\"useDungeons\":true,\"dungeonChance\":8,\"useStrongholds\":true,\"useVillages\":true,\"useMineShafts\":true,\"useTemples\":true,\"useMonuments\":true,\"useRavines\":true,\"useWaterLakes\":true,\"waterLakeChance\":4,\"useLavaLakes\":true,\"lavaLakeChance\":80,\"useLavaOceans\":false,\"fixedBiome\":-1,\"biomeSize\":4,\"riverSize\":4,\"dirtSize\":33,\"dirtCount\":10,\"dirtMinHeight\":0,\"dirtMaxHeight\":256,\"gravelSize\":33,\"gravelCount\":8,\"gravelMinHeight\":0,\"gravelMaxHeight\":256,\"graniteSize\":33,\"graniteCount\":10,\"graniteMinHeight\":0,\"graniteMaxHeight\":80,\"dioriteSize\":33,\"dioriteCount\":10,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":80,\"andesiteSize\":33,\"andesiteCount\":10,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":80,\"coalSize\":17,\"coalCount\":20,\"coalMinHeight\":0,\"coalMaxHeight\":128,\"ironSize\":9,\"ironCount\":20,\"ironMinHeight\":0,\"ironMaxHeight\":64,\"goldSize\":9,\"goldCount\":2,\"goldMinHeight\":0,\"goldMaxHeight\":32,\"redstoneSize\":8,\"redstoneCount\":8,\"redstoneMinHeight\":0,\"redstoneMaxHeight\":16,\"diamondSize\":8,\"diamondCount\":1,\"diamondMinHeight\":0,\"diamondMaxHeight\":16,\"lapisSize\":7,\"lapisCount\":1,\"lapisCenterHeight\":16,\"lapisSpread\":16}";
    private static final int MAX = Config.maxEnergyBufferTerrain;
    TileEntityScannerQueue queue;
    boolean on;
    MutableBlockPos current = new MutableBlockPos(0, -1, 0);
    //BlockPos pos = null;
    public EnumRotation rotation = EnumRotation.POSX_POSZ;
    public int speedup = 1;
    public BlockPos posStart = null;
    public int maxY = 255;

    @Nonnull
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
        maxY = nbt.getInteger("my");
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
        nbt.setInteger("my", maxY);
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
    }


    public void deactivate() {
        changeState(false);
    }

    @Nonnull
    BlockPos getEnd() {
        return getPosStart()./*east().*/add(15, maxY, 15);
    }

    void changeState(boolean state) {
        on = state;
        /*getWorld().setBlockState(pos, getWorld().getBlockState(pos).withProperty(BlockTerrainScanner.ONOFF, state));
        getWorld().setTileEntity(pos, this);*/
        markDirty();

        /*try {
            getWorld().markAndNotifyBlock(getPos(), getWorld().getChunkFromBlockCoords(getPos()), getWorld().getBlockState(getPos()), getWorld().getBlockState(getPos()).withProperty(BlockTerrainScanner.ONOFF, state), 4);
            getWorld().notifyBlockUpdate(getPos(), getWorld().getBlockState(getPos()), getWorld().getBlockState(getPos()).withProperty(BlockTerrainScanner.ONOFF, state), 3);
        } catch (IllegalArgumentException ignored) {
        }
        getWorld().markBlockRangeForRenderUpdate(getPos(), getPos());*/
    }

    @Override
    public void update() {
        if (getWorld().isRemote) return; //Dont do stuff client side else we get ghosts
        queue = TileEntityScannerQueue.getNearbyQueue(getWorld(), this);
        TileEntityBiomeScanner biomeScanner = TileEntityBiomeScanner
            .getNearbyBiomeScanner(getWorld(), this);

        if (getWorld().isBlockPowered(getPos())) on = true;
        //System.out.println(queue);
        int multiplier = 0;
        for (int j = 0; j < speedup; j++) {
            if (!on)
                return;
            if (container.getEnergyStored() < Config.energyPerBlockTerrainScanner) {
                //changeState(false);
                return;
            }
            WorldServer remoteWorld;
            try {
                if (getWorld().provider.getDimension() == -1)
                    remoteWorld = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(Config.dimid + 1);
                else if (getWorld().provider.getDimension() == 1)
                    remoteWorld = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(Config.dimid + 2);
                else
                    remoteWorld = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(Config.dimid);
            } catch (NullPointerException lazy) {
                return;
            }
            changeState(true);

            IBlockState remote = remoteWorld.getBlockState(current);
            IBlockState local = getWorld().getBlockState(current);
            TileEntity remoteTE = remoteWorld.getTileEntity(current);
            BlockPos imm = current.toImmutable();
            if ((local.getBlock().isReplaceable(getWorld(), imm) || local.getBlock().isAir(local, getWorld(), imm)) && !(local.getBlock() instanceof BlockFluidBase) && !(local.getBlock() instanceof BlockLiquid)) {
                getWorld().setBlockState(imm, remote, 2);
                if (remoteTE != null) {
                    NBTTagCompound tag = new NBTTagCompound();
                    remoteTE.writeToNBT(tag);
                    getWorld().getTileEntity(imm).writeToNBT(tag);
                }
                if (!remote.getBlock().isAir(remote, getWorld(), imm))
                    multiplier++;

            }

            if (Config.genExtraVanillaOres && getWorld().getBlockState(current).getBlock() == Blocks.STONE) {
                if (current.getY() > 8) {
                    int i = ThreadLocalRandom.current().nextInt(25);
                    if (i == 0)
                        getWorld().setBlockState(current, Blocks.COAL_ORE.getDefaultState(), 2);
                    else if (i == 1)
                        getWorld().setBlockState(current, Blocks.IRON_ORE.getDefaultState(), 2);
                }
                if (current.getY() > 8 && current.getY() < 16) {
                    int i = ThreadLocalRandom.current().nextInt(150);
                    if (i == 0)
                        getWorld().setBlockState(current, Blocks.DIAMOND_ORE.getDefaultState(), 2);
                    else if (i == 1)
                        getWorld().setBlockState(current, Blocks.EMERALD_ORE.getDefaultState(), 2);
                    else if (i == 2)
                        getWorld().setBlockState(current, Blocks.REDSTONE_ORE.getDefaultState(), 2);
                    else if (i == 3)
                        getWorld().setBlockState(current, Blocks.LAPIS_ORE.getDefaultState(), 2);
                }
                if (current.getY() > 8 && current.getY() < 32) {
                    int i = ThreadLocalRandom.current().nextInt(45);
                    if (i == 0)
                        getWorld().setBlockState(current, Blocks.GOLD_ORE.getDefaultState(), 2);
                }
            }
            Oregistry.getEntryList().stream().filter(entry -> current.getY() < entry.maxY && current.getY() > entry.minY).forEach(entry -> {
                int i = ThreadLocalRandom.current().nextInt(entry.rarity);
                if ((i == 0) && (getWorld().getBlockState(current) == entry.material)) {
                    getWorld().setBlockState(current, entry.ore, 2);
                }
            });

            //Movement needs to happen BELOW oregen else things get weird and desynced
            if (rotation.x > 0) current = new MutableBlockPos(current.east());
            else new MutableBlockPos(current.west()); //X++
            BlockPos end = this.getEnd(); //We do this lazy load so it can cache the right value

            if (current.getX() > end.getX()) {
                if (rotation == EnumRotation.NEGX_POSZ || rotation == EnumRotation.POSX_POSZ)
                    current = new MutableBlockPos(current.south());
                else current = new MutableBlockPos(current.north());
                current.setPos(getPosStart().getX(), current.getY(), current.getZ());
            }
            if (current.getZ() > end.getZ() && rotation.z > 0 || current.getZ() < end.getZ() && rotation.z < 0) {
                current.setPos(getPosStart().getX(), current.getY() + 1, getPosStart().getZ());
            }
            if (current.getY() > maxY) {
                if (queue != null && queue.queue.peek() != null) {
                    BlockPos pos = queue.pop();
                    if (pos == null) throw new WtfException("How can this be???");
                    this.current.setPos(pos);
                    this.posStart = pos;

                } else changeState(false);

            }
            if (current.getY() > maxY) {
                if (biomeScanner != null && biomeScanner.biomeScanner.peek() != null) {
                    BlockPos pos = biomeScanner.pop();
                    if (pos == null) throw new WtfException("How can this be???");
                    this.current.setPos(pos);
                    this.posStart = pos;

                } else changeState(false);

            }

            markDirty();
        }
        container.extractEnergy(Config.energyPerBlockTerrainScanner * multiplier, false);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return tag;
    }


}
