package eladkay.scanner.tiles;

import eladkay.scanner.ScannerConfig;
import eladkay.scanner.ScannerMod;
import eladkay.scanner.blocks.BlockTerrainScanner;
import eladkay.scanner.compat.FTBChunksCompat;
import eladkay.scanner.misc.EnumRotation;
import eladkay.scanner.client.container.TerrainScannerContainer;
import eladkay.scanner.compat.Oregistry;
import eladkay.scanner.init.ModTileEntities;
import eladkay.scanner.misc.WtfException;
import eladkay.scanner.networking.MessageUpdateQueue;
import eladkay.scanner.networking.NetworkHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.IFluidBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class TileEntityTerrainScanner extends BaseTE implements ITickableTileEntity, INamedContainerProvider {

    public static final String PRESET = "{\"coordinateScale\":684.412,\"heightScale\":684.412,\"lowerLimitScale\":512.0,\"upperLimitScale\":512.0,\"depthNoiseScaleX\":200.0,\"depthNoiseScaleZ\":200.0,\"depthNoiseScaleExponent\":0.5,\"mainNoiseScaleX\":80.0,\"mainNoiseScaleY\":160.0,\"mainNoiseScaleZ\":80.0,\"baseSize\":8.5,\"stretchY\":12.0,\"biomeDepthWeight\":1.0,\"biomeDepthOffset\":0.0,\"biomeScaleWeight\":1.0,\"biomeScaleOffset\":0.0,\"seaLevel\":63,\"useCaves\":true,\"useDungeons\":true,\"dungeonChance\":8,\"useStrongholds\":true,\"useVillages\":true,\"useMineShafts\":true,\"useTemples\":true,\"useMonuments\":true,\"useRavines\":true,\"useWaterLakes\":true,\"waterLakeChance\":4,\"useLavaLakes\":true,\"lavaLakeChance\":80,\"useLavaOceans\":false,\"fixedBiome\":-1,\"biomeSize\":4,\"riverSize\":4,\"dirtSize\":33,\"dirtCount\":10,\"dirtMinHeight\":0,\"dirtMaxHeight\":256,\"gravelSize\":33,\"gravelCount\":8,\"gravelMinHeight\":0,\"gravelMaxHeight\":256,\"graniteSize\":33,\"graniteCount\":10,\"graniteMinHeight\":0,\"graniteMaxHeight\":80,\"dioriteSize\":33,\"dioriteCount\":10,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":80,\"andesiteSize\":33,\"andesiteCount\":10,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":80,\"coalSize\":17,\"coalCount\":20,\"coalMinHeight\":0,\"coalMaxHeight\":128,\"ironSize\":9,\"ironCount\":20,\"ironMinHeight\":0,\"ironMaxHeight\":64,\"goldSize\":9,\"goldCount\":2,\"goldMinHeight\":0,\"goldMaxHeight\":32,\"redstoneSize\":8,\"redstoneCount\":8,\"redstoneMinHeight\":0,\"redstoneMaxHeight\":16,\"diamondSize\":8,\"diamondCount\":1,\"diamondMinHeight\":0,\"diamondMaxHeight\":16,\"lapisSize\":7,\"lapisCount\":1,\"lapisCenterHeight\":16,\"lapisSpread\":16}";
    private static final int MAX = ScannerConfig.CONFIG.maxEnergyBufferTerrain.get();
    public TileEntityScannerQueue queue;
    public boolean powered;
    public BlockPos.Mutable current = new BlockPos.Mutable(0, -1, 0);
    public EnumRotation rotation = EnumRotation.POSX_POSZ;
    public int speedup = 1;
    public BlockPos posStart = null;
    public int maxY = ScannerConfig.CONFIG.maxY.get();
    public UUID placer;
    public ITextComponent placerName;

    public TileEntityTerrainScanner() {
        super(ModTileEntities.TERRAIN_SCANNER_TILE.get(), MAX);
    }

    @Nonnull
    public BlockPos getPosStart() {
        return posStart != null ? posStart : getBlockPos();
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = new CompoundNBT();
        save(tag);
        return tag;
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        nbt.putLong("positions", current.asLong());
        nbt.putBoolean("powered", powered);
        nbt.putInt("rot", rotation.ordinal());
        nbt.putInt("speedup", speedup);
        if (posStart != null)
            nbt.putLong("posStart", posStart.asLong());
        nbt.putInt("my", maxY);
        if (placer != null) {
            nbt.putString("placer", placer.toString());
            nbt.putString("placerName", placerName.getString());
        }
        return nbt;
    }

    @Override
    public void load(BlockState blockState, CompoundNBT nbt) {
        super.load(blockState, nbt);
        current.set(nbt.getLong("positions"));
        powered = nbt.getBoolean("powered");
        rotation = EnumRotation.values()[nbt.getInt("rot")];
        speedup = nbt.getInt("speedup");
        if (nbt.getLong("posStart") != 0)
            posStart = BlockPos.of(nbt.getLong("posStart"));
        maxY = nbt.getInt("my");
        try {
            placer = UUID.fromString(nbt.getString("placer"));
            placerName = new StringTextComponent(nbt.getString("placerName"));
        } catch (Exception e) { //Old scanners that lack the tag
            placer = null;
            placerName = new StringTextComponent("");
        }
    }

    public void activate() {
        changeState(true);
        current.set(getPosStart().getX(), 0, getPosStart().getZ());
    }


    public void deactivate() {
        changeState(false);
    }

    @Nonnull
    public BlockPos getEnd() {
        return getPosStart()./*east().*/offset(15, maxY, 15);
    }

    void changeState(boolean state) {
        powered = state;
        setChanged();
    }

    @Override
    public void tick() {
        if (getLevel().isClientSide) return; //Dont do stuff client side else we get ghosts
        queue = TileEntityScannerQueue.getNearbyQueue(getLevel(), this);
        TileEntityBiomeScanner biomeScanner = TileEntityBiomeScanner
                .getNearbyBiomeScanner(getLevel(), this);

        if (getLevel().getBlockState(getBlockPos()).getValue(BlockTerrainScanner.POWERED)) powered = true;
        int multiplier = 0;
        for (int j = 0; j < speedup; j++) {
            if (!powered)
                return;
            if (container.getEnergyStored() < ScannerConfig.CONFIG.energyPerBlockTerrainScanner.get()) {
                return;
            }

            RegistryKey<World> localKey = getLevel().dimension();
            RegistryKey<World> remoteKey = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(ScannerMod.MODID, localKey.location().toString().replace(":", "_")));

            ServerWorld remoteWorld = getLevel().getServer().getLevel(remoteKey);
            if(remoteWorld == null) return;

            changeState(true);

            BlockState remote = remoteWorld.getBlockState(current);
            BlockState local = getLevel().getBlockState(current);
            TileEntity remoteTE = remoteWorld.getBlockEntity(current);
            BlockPos imm = current.immutable();
            if (FTBChunksCompat.checkClaimed(imm, getLevel(), placer, getLevel().getBlockState(imm))) continue;
            boolean toGen = checkForBlock(local, imm);
            if (toGen) {
                getLevel().setBlock(imm, remote, 2);
                if (remoteTE != null) {
                    CompoundNBT tag = remoteTE.serializeNBT();
                    getLevel().getBlockEntity(imm).deserializeNBT(tag);
                }
                if (!remote.getBlock().isAir(remote, getLevel(), imm))
                    multiplier++;
            }

            if (ScannerConfig.CONFIG.genExtraVanillaOres.get() && getLevel().getBlockState(current).getBlock() == Blocks.STONE) {
                if (current.getY() > 8) {
                    int i = ThreadLocalRandom.current().nextInt(25);
                    if (i == 0)
                        getLevel().setBlock(current, Blocks.COAL_ORE.defaultBlockState(), 2);
                    else if (i == 1)
                        getLevel().setBlock(current, Blocks.IRON_ORE.defaultBlockState(), 2);
                }
                if (current.getY() > 8 && current.getY() < 16) {
                    int i = ThreadLocalRandom.current().nextInt(150);
                    if (i == 0)
                        getLevel().setBlock(current, Blocks.DIAMOND_ORE.defaultBlockState(), 2);
                    else if (i == 1)
                        getLevel().setBlock(current, Blocks.EMERALD_ORE.defaultBlockState(), 2);
                    else if (i == 2)
                        getLevel().setBlock(current, Blocks.REDSTONE_ORE.defaultBlockState(), 2);
                    else if (i == 3)
                        getLevel().setBlock(current, Blocks.LAPIS_ORE.defaultBlockState(), 2);
                }
                if (current.getY() > 8 && current.getY() < 32) {
                    int i = ThreadLocalRandom.current().nextInt(45);
                    if (i == 0)
                        getLevel().setBlock(current, Blocks.GOLD_ORE.defaultBlockState(), 2);
                }
            }
            Oregistry.getEntryList().stream().filter(entry -> current.getY() < entry.maxY && current.getY() > entry.minY).forEach(entry -> {
                int i = ThreadLocalRandom.current().nextInt(entry.rarity);
                if ((i == 0) && (getLevel().getBlockState(current) == entry.material)) {
                    getLevel().setBlock(current, entry.ore, 2);
                }
            });

            if (ScannerConfig.CONFIG.voidOriginalBlock.get() && toGen) { //Only clears when it actually builds
                remoteWorld.setBlock(current, Blocks.AIR.defaultBlockState(), 0); //don't do block update, resolving the falling sand issue
            }

            //Movement needs to happen BELOW oregen else things get weird and desynced
            if (rotation.x > 0) current = new BlockPos(current.east()).mutable();
            else new BlockPos(current.west()).mutable(); //X++
            BlockPos end = this.getEnd(); //We do this lazy load so it can cache the right value

            if (current.getX() > end.getX()) {
                if (rotation == EnumRotation.NEGX_POSZ || rotation == EnumRotation.POSX_POSZ)
                    current = new BlockPos(current.south()).mutable();
                else current = new BlockPos(current.north()).mutable();
                current.set(getPosStart().getX(), current.getY(), current.getZ());
            }
            if (current.getZ() > end.getZ() && rotation.z > 0 || current.getZ() < end.getZ() && rotation.z < 0) {
                current.set(getPosStart().getX(), current.getY() + 1, getPosStart().getZ());
            }
            System.out.println(current.getY());
            if (current.getY() > maxY) {
                if (queue != null && queue.peek() != null) {
                    BlockPos pos = queue.pop();
                    NetworkHelper.broadcastInLevel((ServerWorld) level, new MessageUpdateQueue(queue));
                    if (pos == null) throw new WtfException("How can this be???");
                    this.current.set(pos);
                    this.posStart = pos;

                } else changeState(false);

            }
            if (current.getY() > maxY) {
                if (biomeScanner != null && biomeScanner.biomeScanner.peek() != null) {
                    BlockPos pos = biomeScanner.pop();
                    if (pos == null) throw new WtfException("How can this be???");
                    this.current.set(pos);
                    this.posStart = pos;

                } else changeState(false);

            }

            setChanged();
        }
        container.extractEnergy(ScannerConfig.CONFIG.energyPerBlockTerrainScanner.get() * multiplier, false);
    }

    private boolean checkForBlock(BlockState local, BlockPos imm) { //True if the block will be generated
        Block block = local.getBlock();
        if (/*block.canBeReplaced(getLevel(), new BlockItemUseContext()) || */block.isAir(local, getLevel(), imm)) { //Replaceable / air
            if (!(block instanceof IFluidBlock) && !(block instanceof FlowingFluidBlock)) { //Not occupied by liquid
                return true;
            } else if (ScannerConfig.CONFIG.replaceNonSourceLiquid.get()) { //Occupied by liquid, check for config, if config is powered then replace non-source blocks
                //Non-source
                return (/*TODO: local.getValue(IFluidBlock.LEVEL) != 0 || */local.getValue(FlowingFluidBlock.LEVEL) != 0);
            }
        }
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("");
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
        return new TerrainScannerContainer(id, playerInventory, this);
    }
}
