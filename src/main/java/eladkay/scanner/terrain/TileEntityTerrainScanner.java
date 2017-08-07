package eladkay.scanner.terrain;

import com.teamwizardry.librarianlib.common.network.PacketHandler;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import eladkay.scanner.Config;
import eladkay.scanner.compat.Oregistry;
import eladkay.scanner.misc.RandUtil;
import eladkay.scanner.misc.TileEnergyConsumer;
import eladkay.scanner.misc.WtfException;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

import static eladkay.scanner.terrain.EnumDimensions.*;

@TileRegister("terrainScanner")
public class TileEntityTerrainScanner extends TileEnergyConsumer implements ITickable {

	public static final String PRESET = "{\"coordinateScale\":684.412,\"heightScale\":684.412,\"lowerLimitScale\":512.0,\"upperLimitScale\":512.0,\"depthNoiseScaleX\":200.0,\"depthNoiseScaleZ\":200.0,\"depthNoiseScaleExponent\":0.5,\"mainNoiseScaleX\":80.0,\"mainNoiseScaleY\":160.0,\"mainNoiseScaleZ\":80.0,\"baseSize\":8.5,\"stretchY\":12.0,\"biomeDepthWeight\":1.0,\"biomeDepthOffset\":0.0,\"biomeScaleWeight\":1.0,\"biomeScaleOffset\":0.0,\"seaLevel\":63,\"useCaves\":true,\"useDungeons\":true,\"dungeonChance\":8,\"useStrongholds\":true,\"useVillages\":true,\"useMineShafts\":true,\"useTemples\":true,\"useMonuments\":true,\"useRavines\":true,\"useWaterLakes\":true,\"waterLakeChance\":4,\"useLavaLakes\":true,\"lavaLakeChance\":80,\"useLavaOceans\":false,\"fixedBiome\":-1,\"biomeSize\":4,\"riverSize\":4,\"dirtSize\":33,\"dirtCount\":10,\"dirtMinHeight\":0,\"dirtMaxHeight\":256,\"gravelSize\":33,\"gravelCount\":8,\"gravelMinHeight\":0,\"gravelMaxHeight\":256,\"graniteSize\":33,\"graniteCount\":10,\"graniteMinHeight\":0,\"graniteMaxHeight\":80,\"dioriteSize\":33,\"dioriteCount\":10,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":80,\"andesiteSize\":33,\"andesiteCount\":10,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":80,\"coalSize\":17,\"coalCount\":20,\"coalMinHeight\":0,\"coalMaxHeight\":128,\"ironSize\":9,\"ironCount\":20,\"ironMinHeight\":0,\"ironMaxHeight\":64,\"goldSize\":9,\"goldCount\":2,\"goldMinHeight\":0,\"goldMaxHeight\":32,\"redstoneSize\":8,\"redstoneCount\":8,\"redstoneMinHeight\":0,\"redstoneMaxHeight\":16,\"diamondSize\":8,\"diamondCount\":1,\"diamondMinHeight\":0,\"diamondMaxHeight\":16,\"lapisSize\":7,\"lapisCount\":1,\"lapisCenterHeight\":16,\"lapisSpread\":16}";
	private static final int MAX = Config.maxEnergyBufferTerrain;
	public transient TileEntityScannerQueue queueTE;
	@Save
	public boolean on;
	@Save(saveName = "positions")
	public final MutableBlockPos currentPos = new MutableBlockPos(0, -1, 0);
	@Save(saveName = "speedup")
	public int speedup = 1;
	@Save
	public BlockPos posStart = null;
	@Save(saveName = "my")
	public int maxY = 127;

	@Save
	public int layerBlocksPlace = 0;
	@Save
	public int currentY = 0;

	@Nonnull
	public BlockPos getPosStart() {
		return posStart != null ? posStart : getPos();
	}

	public TileEntityTerrainScanner() {
		super(MAX);
	}

	public void onBlockActivated() {
		if (currentPos.getY() < 0) {
			currentPos.setPos(getPos().getX() + 1, 0, getPos().getZ());
			changeState(true);
		}
	}

	public void activate() {
		currentPos.setPos(getPosStart().getX() + 1, 0, getPosStart().getZ());
		changeState(true);
	}

	public void deactivate() {
		changeState(false);
	}

	@Nonnull
	public BlockPos getEnd() {
		return getPosStart().add(15, maxY, 15);
	}

	private void changeState(boolean state) {
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

		if (!getWorld().isRemote) {
			queueTE = TileEntityScannerQueue.getNearbyTile(getWorld(), this, TileEntityScannerQueue.class);
		}

		// --- GET REMOTE WORLD --- //
		WorldServer fakeWorld = null;
		boolean somethingFailed = false;
		{
			EnumDimensions type = getWorld().provider.getDimension() == -1 ? NETHER : getWorld().provider.getDimension() == 1 ? END : OVERWORLD;
			for (EnumFacing facing : EnumFacing.values()) {
				IBlockState te = world.getBlockState(getPos().offset(facing));
				if (te.getBlock() instanceof BlockDimensionalCore && te.getValue(BlockDimensionalCore.TYPE) != NONE)
					type = te.getValue(BlockDimensionalCore.TYPE);
			}
			try {
				if (type == NETHER)
					fakeWorld = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(Config.dimid + 1);
				else if (type == END)
					fakeWorld = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(Config.dimid + 2);
				else
					fakeWorld = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(Config.dimid);
			} catch (NullPointerException lazy) {
				somethingFailed = true;
			}
			if (fakeWorld == null) somethingFailed = true;
		}
		if (somethingFailed) return;
		// --- GET REMOTE WORLD --- //

		// Set on if powered by redstone
		if (!getWorld().isRemote && getWorld().isBlockPowered(getPos())) on = true;

		if (currentPos == null) return;

		int multiplier = 0;
		for (int tick = 0; tick < speedup; tick++) {

			if (!on) return;

			if (getContainer().getEnergyStored() < Config.energyPerBlockTerrainScanner) return;

			//remoteWorld.getBlockState(currentPos);
			IBlockState fakeState = fakeWorld.getBlockState(currentPos);
			IBlockState currentState = getWorld().getBlockState(currentPos);
			TileEntity fakeTE = fakeWorld.getTileEntity(currentPos);

			BlockPos currentPosImm = currentPos.toImmutable();

			SoundType sound = fakeState.getBlock().getSoundType(fakeState, world, currentPos, null);

			// --- PLACE NEW BLOCK HERE --- //
			{
				if (!world.isRemote && currentState.getBlock().isReplaceable(getWorld(), currentPosImm) && currentState.getBlock().isAir(currentState, getWorld(), currentPosImm)) {
					boolean success = getWorld().setBlockState(currentPosImm, fakeState);
					if (success) {
						getWorld().playSound(getPos().getX(), getPos().getY(), getPos().getZ(), sound.getPlaceSound(), SoundCategory.BLOCKS, sound.getVolume(), sound.getPitch(), false);

						if (fakeTE != null) {
							NBTTagCompound tag = new NBTTagCompound();
							fakeTE.writeToNBT(tag);

							TileEntity freshTE = getWorld().getTileEntity(currentPosImm);
							if (freshTE != null) freshTE.writeToNBT(tag);
						}
						if (!fakeState.getBlock().isAir(fakeState, getWorld(), currentPosImm))
							multiplier++;

						markDirty();
					}
				}
			}
			// --- PLACE NEW BLOCK HERE --- //

			// --- SET ORES HERE --- //
			{
				if (!world.isRemote) {
					if (Config.genVanillaOres && getWorld().getBlockState(currentPos).getBlock() == Blocks.STONE) {
						if (currentPos.getY() > 8) {
							int i = RandUtil.nextInt(25);
							if (i == 0)
								getWorld().setBlockState(currentPos, Blocks.COAL_ORE.getDefaultState());
							else if (i == 1)
								getWorld().setBlockState(currentPos, Blocks.IRON_ORE.getDefaultState());
						}
						if (currentPos.getY() > 8 && currentPos.getY() < 16) {
							int i = RandUtil.nextInt(250);
							if (i == 0)
								getWorld().setBlockState(currentPos, Blocks.DIAMOND_ORE.getDefaultState());
							else if (i == 1)
								getWorld().setBlockState(currentPos, Blocks.EMERALD_ORE.getDefaultState());
							else if (i == 2)
								getWorld().setBlockState(currentPos, Blocks.REDSTONE_ORE.getDefaultState());
							else if (i == 3)
								getWorld().setBlockState(currentPos, Blocks.LAPIS_ORE.getDefaultState());
						}
						if (currentPos.getY() > 8 && currentPos.getY() < 32) {
							int i = RandUtil.nextInt(45);
							if (i == 0) getWorld().setBlockState(currentPos, Blocks.GOLD_ORE.getDefaultState());
						}
					}
					Oregistry.getEntryList().stream().filter(entry -> currentPos.getY() < entry.maxY && currentPos.getY() > entry.minY).forEach(entry -> {
						int i = ThreadLocalRandom.current().nextInt(entry.rarity);
						if (i == 0) getWorld().setBlockState(currentPos, entry.ore, 2);
					});
				}
				markDirty();
			}
			// --- SET ORES HERE --- //

			// --- MOVE CURSOR HERE --- //
			{
				currentPos.move(EnumFacing.EAST);

				layerBlocksPlace++;
				BlockPos end = getEnd();

				if (currentPos.getX() > end.getX()) {
					currentPos.move(EnumFacing.SOUTH);
					currentPos.setPos(getPosStart().getX(), currentPos.getY(), currentPos.getZ());
				}
				if (currentPos.getZ() > end.getZ()) {
					currentPos.setPos(getPosStart().getX(), currentPos.getY() + 1, getPosStart().getZ());
					layerBlocksPlace = 0;
					currentY = currentPos.getY();
					//PacketHandler.NETWORK.sendToAllAround(new PacketLayerCompleteParty(getPosStart(), getEnd(), currentPos.getY()),
					//		new NetworkRegistry.TargetPoint(world.provider.getDimension(), getPos().getX(), getPos().getY(), getPos().getZ(), 128));
				}
				if (currentPos.getY() > maxY) {
					layerBlocksPlace = 0;
					currentY = 0;
					if (queueTE != null && queueTE.queue.peek() != null) {
						BlockPos pos = queueTE.pop();
						if (pos == null) throw new WtfException("How can this be???");
						this.currentPos.setPos(pos);
						this.posStart = pos;
					} else changeState(false);
				}
				markDirty();
			}
			// --- MOVE CURSOR HERE --- //
		}

		// Draw energy
		if (!world.isRemote)
			getContainer().extractEnergy(Config.energyPerBlockTerrainScanner * multiplier, false);
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return TileEntity.INFINITE_EXTENT_AABB;
	}

	@Override
	public double getMaxRenderDistanceSquared() {
		return 100000;
	}
}

