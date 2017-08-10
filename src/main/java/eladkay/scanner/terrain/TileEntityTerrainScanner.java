package eladkay.scanner.terrain;

import com.teamwizardry.librarianlib.common.network.PacketHandler;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import eladkay.scanner.Config;
import eladkay.scanner.compat.Oregistry;
import eladkay.scanner.misc.PlaceObject;
import eladkay.scanner.misc.RandUtil;
import eladkay.scanner.misc.TileEnergyConsumer;
import eladkay.scanner.misc.WtfException;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static eladkay.scanner.terrain.EnumDimensions.*;

@TileRegister("terrainScanner")
public class TileEntityTerrainScanner extends TileEnergyConsumer implements ITickable {

	public static final String PRESET = "{\"coordinateScale\":684.412,\"heightScale\":684.412,\"lowerLimitScale\":512.0,\"upperLimitScale\":512.0,\"depthNoiseScaleX\":200.0,\"depthNoiseScaleZ\":200.0,\"depthNoiseScaleExponent\":0.5,\"mainNoiseScaleX\":80.0,\"mainNoiseScaleY\":160.0,\"mainNoiseScaleZ\":80.0,\"baseSize\":8.5,\"stretchY\":12.0,\"biomeDepthWeight\":1.0,\"biomeDepthOffset\":0.0,\"biomeScaleWeight\":1.0,\"biomeScaleOffset\":0.0,\"seaLevel\":63,\"useCaves\":true,\"useDungeons\":true,\"dungeonChance\":8,\"useStrongholds\":true,\"useVillages\":true,\"useMineShafts\":true,\"useTemples\":true,\"useMonuments\":true,\"useRavines\":true,\"useWaterLakes\":true,\"waterLakeChance\":4,\"useLavaLakes\":true,\"lavaLakeChance\":80,\"useLavaOceans\":false,\"fixedBiome\":-1,\"biomeSize\":4,\"riverSize\":4,\"dirtSize\":33,\"dirtCount\":10,\"dirtMinHeight\":0,\"dirtMaxHeight\":256,\"gravelSize\":33,\"gravelCount\":8,\"gravelMinHeight\":0,\"gravelMaxHeight\":256,\"graniteSize\":33,\"graniteCount\":10,\"graniteMinHeight\":0,\"graniteMaxHeight\":80,\"dioriteSize\":33,\"dioriteCount\":10,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":80,\"andesiteSize\":33,\"andesiteCount\":10,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":80,\"coalSize\":17,\"coalCount\":20,\"coalMinHeight\":0,\"coalMaxHeight\":128,\"ironSize\":9,\"ironCount\":20,\"ironMinHeight\":0,\"ironMaxHeight\":64,\"goldSize\":9,\"goldCount\":2,\"goldMinHeight\":0,\"goldMaxHeight\":32,\"redstoneSize\":8,\"redstoneCount\":8,\"redstoneMinHeight\":0,\"redstoneMaxHeight\":16,\"diamondSize\":8,\"diamondCount\":1,\"diamondMinHeight\":0,\"diamondMaxHeight\":16,\"lapisSize\":7,\"lapisCount\":1,\"lapisCenterHeight\":16,\"lapisSpread\":16}";
	private static final int MAX = Config.maxEnergyBufferTerrain;
	public transient TileEntityScannerQueue queueTE;
	@Save
	public boolean on;
	@Nonnull
	public MutableBlockPos currentPos = new MutableBlockPos(0, -1, 0);
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

	public ArrayList<PlaceObject> animationQueue = new ArrayList<>();

	@Save
	public double currentAngle = -1;
	@Save
	public double angleTick = 0;
	@Save
	public boolean angleShouldTick = false;
	@Save
	public double animTime = 30;

	public TileEntityTerrainScanner() {
		super(MAX);
	}

	@Override
	public void writeCustomNBT(@NotNull NBTTagCompound cmp, boolean sync) {
		super.writeCustomNBT(cmp, sync);
		cmp.setLong("current", currentPos.toLong());

		NBTTagList list = new NBTTagList();

		ArrayList<PlaceObject> temp = new ArrayList<>(animationQueue);
		for (int i = 0; i < animationQueue.size(); i++) {
			if (i >= animationQueue.size()) continue;
			list.appendTag(temp.get(i).serializeNBT());
		}
		cmp.setTag("anim_queue", list);
	}

	@Override
	public void readCustomNBT(@NotNull NBTTagCompound cmp) {
		super.readCustomNBT(cmp);

		if (cmp.hasKey("current"))
			currentPos = new MutableBlockPos(BlockPos.fromLong(cmp.getLong("current")));

		if (cmp.hasKey("anim_queue")) {
			animationQueue.clear();
			NBTTagList list = cmp.getTagList("anim_queue", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < list.tagCount(); i++) {
				PlaceObject object = new PlaceObject();
				object.deserializeNBT(list.getCompoundTagAt(i));
				animationQueue.add(object);
			}
		}
	}

	@Nonnull
	public BlockPos getPosStart() {
		return posStart != null ? posStart : getPos();
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
		{
			double angleSep = 2.0 * Math.PI / (animationQueue.size());

			if (currentAngle == -1) {
				currentAngle = angleSep;
				angleTick = animTime;
			}

			if (currentAngle < angleSep) {
				if (!angleShouldTick) angleShouldTick = true;
			}
			if (angleTick < animTime) {
				angleTick++;
				currentAngle = currentAngle + (angleTick / animTime) * (angleSep - currentAngle);
			} else {
				angleShouldTick = false;
				angleTick = 0;
			}
			markDirty();
		}
		// --- TICK ANIMATION QUEUE --- //
		{
			ArrayList<PlaceObject> temp = new ArrayList<>(animationQueue);
			for (PlaceObject object : temp) {
				if (object.expired) {
					animationQueue.remove(object);
				} else object.tick();
			}
			markDirty();
		}
		// --- TICK ANIMATION QUEUE --- //

		if (!getWorld().isRemote) {
			queueTE = TileEntityScannerQueue.getNearbyTile(getWorld(), this, TileEntityScannerQueue.class);
		}

		// --- GET REMOTE WORLD --- //
		WorldServer fakeWorld = null;
		if (!getWorld().isRemote) {
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
				} catch (NullPointerException ignored) {
				}
			}
		}
		// --- GET REMOTE WORLD --- //

		// Set on if powered by redstone
		if (!getWorld().isRemote && getWorld().isBlockPowered(getPos())) on = true;

		int multiplier = 0;
		for (int tick = 0; tick < speedup; tick++) {

			if (!on) return;

			if (getContainer().getEnergyStored() < Config.energyPerBlockTerrainScanner) return;

			//remoteWorld.getBlockState(currentPos);
			if (!getWorld().isRemote && fakeWorld != null) {
				IBlockState fakeState = fakeWorld.getBlockState(currentPos);
				IBlockState currentState = getWorld().getBlockState(currentPos);
				TileEntity fakeTE = fakeWorld.getTileEntity(currentPos);

				BlockPos currentPosImm = currentPos.toImmutable();

				// --- PLACE NEW BLOCK HERE --- //
				{
					if (currentState.getBlock().isReplaceable(getWorld(), currentPosImm) && currentState.getBlock().isAir(currentState, getWorld(), currentPosImm)) {

						PlaceObject object = new PlaceObject(getWorld(), fakeState, currentPosImm, fakeTE, getWorld().getTotalWorldTime());
						animationQueue.add(object);
						PacketHandler.NETWORK.sendToAll(new PacketSyncAnimationQueue(getPos(), currentPosImm, fakeState, fakeTE, getWorld().getTotalWorldTime()));

						multiplier++;
						markDirty();
					}
				}
				// --- PLACE NEW BLOCK HERE --- //

				// --- SET ORES HERE --- //
				{
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
					markDirty();
				}
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
					currentY = currentPos.getY();
					layerBlocksPlace = 0;
				}
				if (currentPos.getY() > maxY) {
					layerBlocksPlace = 0;
					if (queueTE != null && queueTE.queue.peek() != null) {
						BlockPos pos = queueTE.pop();
						if (pos == null) throw new WtfException("How can this be???");
						this.currentPos.setPos(new BlockPos(pos.getX() << 4, 0, pos.getZ() << 4));
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

