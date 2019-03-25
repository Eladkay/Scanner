package eladkay.scanner.terrain;

import eladkay.scanner.ScannerMod;
import eladkay.scanner.init.ModBlocks;
import eladkay.scanner.misc.BaseTE;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

public class TileEntityScannerQueue extends BaseTE implements ITickable, Iterable<BlockPos> {
    public Queue<BlockPos> queue = new ArrayDeque<>();

    transient TileEntityTerrainScanner scanner;
    public boolean flag;

    @Nullable
    public static TileEntityScannerQueue getNearbyQueue(World world, TileEntityTerrainScanner scanner) {
        for (EnumFacing facing : EnumFacing.values()) {
            TileEntity te = world.getTileEntity(scanner.getPos().offset(facing));
            if (te instanceof TileEntityScannerQueue) return (TileEntityScannerQueue) te;
            //else System.out.println(te != null ? te.getClass() : "No te here!");
        }
        return null;
    }

    @Nullable
    private static <T extends TileEntity> T getNearbyQueue0(World world, TileEntity scanner, Class<T> block) {
        for (EnumFacing facing : EnumFacing.values()) {
            TileEntity te = world.getTileEntity(scanner.getPos().offset(facing));
            if (te != null && block.isAssignableFrom(te.getClass())) return (T) te;
        }
        return null;
    }

    @Nullable
    public BlockPos pop() {
        return queue.poll();
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
    }

    static final int CAPACITY = 5;

    public void push(BlockPos pos) {
        if (queue.size() >= CAPACITY) return;
        queue.add(pos);
    }

    @Nullable
    public BlockPos peek() {
        return queue.peek();
    }


    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return tag;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagList list = new NBTTagList();
        for (BlockPos pos : queue) list.appendTag(new NBTTagLong(pos.toLong()));
        nbt.setTag("queue", list);
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        NBTTagList list = nbt.getTagList("queue", 4);
        for (int i = 0; i < list.tagCount(); i++) queue.add(BlockPos.fromLong(((NBTTagLong) list.get(i)).getLong()));
    }

    public TileEntityScannerQueue() {
        super(0);
    }

    @Override
    public void update() {
        scanner = getNearbyQueue0(getWorld(), this, TileEntityTerrainScanner.class);
        flag = false;
        if (scanner != null) {
            for (EnumFacing facing : EnumFacing.values())
                flag |= getWorld().getBlockState(scanner.getPos().offset(facing)).getBlock() == ModBlocks.biomeScannerUltimate;
        }
    }
}
