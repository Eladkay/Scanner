package eladkay.scanner.misc;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;

public class PlaceObject implements INBTSerializable<NBTTagCompound> {

	public static final int maxTick = 100;

	private World world;
	public IBlockState state;
	public BlockPos pos;
	@Nullable
	public NBTTagCompound tileNBT;
	public int tick = 0;
	public boolean expired = false;
	public long worldTime;

	public PlaceObject(World world, IBlockState state, BlockPos pos, @Nullable TileEntity tile, long worldTime) {
		this.world = world;
		this.state = state;
		this.pos = pos;
		this.worldTime = worldTime;
		if (tile != null)
			this.tileNBT = tile.serializeNBT();
	}

	public PlaceObject() {
	}

	public void tick() {
		if (expired) return;
		if (tick < maxTick) tick++;
		else {
			expired = true;
			if (!world.isRemote) {
				world.setBlockState(pos, state, 2);
				//SoundType sound = state.getBlock().getSoundType(state, world, currentPos, null);
				//getWorld().playSound(getPos().getX(), getPos().getY(), getPos().getZ(), sound.getPlaceSound(), SoundCategory.BLOCKS, sound.getVolume(), sound.getPitch(), false);

				if (tileNBT != null) {
					TileEntity freshTE = world.getTileEntity(pos);
					if (freshTE != null) freshTE.writeToNBT(tileNBT);
				}
			}
		}
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();

		compound.setLong("pos", pos.toLong());

		NBTTagCompound stateNBT = new NBTTagCompound();
		NBTUtil.writeBlockState(stateNBT, state);
		compound.setTag("state", stateNBT);

		if (tileNBT != null) compound.setTag("tile", tileNBT);

		compound.setInteger("tick", tick);

		compound.setInteger("world", world.provider.getDimension());
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		pos = BlockPos.fromLong(nbt.getLong("pos"));
		state = NBTUtil.readBlockState(nbt.getCompoundTag("state"));
		tick = nbt.getInteger("tick");
		if (nbt.hasKey("tile")) tileNBT = nbt.getCompoundTag("tile");
		world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(nbt.getInteger("world"));
	}
}
