package eladkay.scanner.terrain;

import com.google.common.collect.Lists;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import eladkay.scanner.ScannerMod;
import eladkay.scanner.misc.TileEnergyConsumer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.LinkedList;

@TileRegister("q")
public class TileEntityScannerQueue extends TileEnergyConsumer implements ITickable, Iterable<BlockPos> {

	public static final int CAPACITY = 5;

	public LinkedList<BlockPos> queue = Lists.newLinkedList();
	public transient TileEntityTerrainScanner scanner;

	@Save
	@Deprecated
	public boolean flag;

	public TileEntityScannerQueue() {
		super(0);
	}

	@Nullable
	@Deprecated
	public static TileEntityScannerQueue getNearbyQueue(World world, TileEntityTerrainScanner scanner) {
		for (EnumFacing facing : EnumFacing.values()) {
			TileEntity te = world.getTileEntity(scanner.getPos().offset(facing));
			if (te != null && te instanceof TileEntityScannerQueue) return (TileEntityScannerQueue) te;
		}
		return null;
	}

	@Nullable
	@Deprecated
	public static TileEntityScannerQueue getNearbyQueue(World world, BlockPos scanner) {
		for (EnumFacing facing : EnumFacing.values()) {
			TileEntity te = world.getTileEntity(scanner.offset(facing));
			if (te != null && te instanceof TileEntityScannerQueue) return (TileEntityScannerQueue) te;
		}
		return null;
	}

	@Override
	public void writeCustomNBT(@NotNull NBTTagCompound cmp, boolean sync) {
		NBTTagList list = new NBTTagList();
		for (BlockPos pos : queue) list.appendTag(new NBTTagLong(pos.toLong()));
		cmp.setTag("queueTE", list);
		super.writeCustomNBT(cmp, sync);
	}

	@Override
	public void readCustomNBT(@NotNull NBTTagCompound cmp) {
		NBTTagList list = cmp.getTagList("queueTE", 4);
		for (int i = 0; i < list.tagCount(); i++) queue.add(BlockPos.fromLong(((NBTTagLong) list.get(i)).getLong()));
		super.readCustomNBT(cmp);
	}

	@Nullable
	public static <T extends TileEntity> T getNearbyTile(World world, TileEntity scanner, Class<T> block) {
		for (EnumFacing facing : EnumFacing.values()) {
			TileEntity te = world.getTileEntity(scanner.getPos().offset(facing));
			if (te != null && block.isAssignableFrom(te.getClass())) return (T) te;
		}
		return null;
	}

	@Nullable
	public BlockPos pop() {
		BlockPos ret = queue.poll();
		markDirty();
		return ret;
	}

	@Override
	public Iterator<BlockPos> iterator() {
		return queue.iterator();
	}

	public int size() {
		return queue.size();
	}

	@Nullable
	public BlockPos get(int index) {
		int i = 0;
		for (BlockPos pos : this) if (index == i++) return pos;
		return null;
	}

	public void remove(BlockPos pos) {
		queue.remove(pos);
		markDirty();
	}

	public void push(BlockPos pos) {
		if (queue.size() >= CAPACITY) return;
		queue.add(pos);
		markDirty();
	}

	@Nullable
	public BlockPos peek() {
		return queue.peek();
	}

	@Override
	public void update() {
		scanner = getNearbyTile(getWorld(), this, TileEntityTerrainScanner.class);
		if (scanner == null) return;

		flag = false;
		for (EnumFacing facing : EnumFacing.values()) {
			flag |= getWorld().getBlockState(scanner.getPos().offset(facing)).getBlock() == ScannerMod.biomeScannerUltimate;
		}
	}
}
