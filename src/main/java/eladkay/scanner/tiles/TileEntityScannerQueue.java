package eladkay.scanner.tiles;

import eladkay.scanner.client.container.ScannerQueueContainer;
import eladkay.scanner.init.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;

public class TileEntityScannerQueue extends BaseTE implements ITickableTileEntity, Iterable<BlockPos>, INamedContainerProvider {
    public Queue<BlockPos> queue = new ArrayDeque<>();

    public transient TileEntityTerrainScanner scanner;

    public TileEntityScannerQueue() {
        super(ModTileEntities.SCANNER_QUEUE_TILE.get(), 0);
    }

    @Nullable
    public static TileEntityScannerQueue getNearbyQueue(World world, TileEntityTerrainScanner scanner) {
        for (Direction facing : Direction.values()) {
            TileEntity te = world.getBlockEntity(scanner.getBlockPos().relative(facing));
            if (te instanceof TileEntityScannerQueue) return (TileEntityScannerQueue) te;
        }
        return null;
    }

    @Nullable
    private static <T extends TileEntity> T getNearbyQueue0(World world, TileEntity scanner, Class<T> block) {
        for (Direction facing : Direction.values()) {
            TileEntity te = world.getBlockEntity(scanner.getBlockPos().relative(facing));
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

    public static final int CAPACITY = 5;

    public void push(BlockPos pos) {
        if (queue.size() >= CAPACITY) return;
        queue.add(pos);
    }

    @Nullable
    public BlockPos peek() {
        return queue.peek();
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
        ListNBT list = new ListNBT();
        for (BlockPos pos : queue) list.add(LongNBT.valueOf(pos.asLong()));
        nbt.put("queue", list);
        return nbt;
    }

    @Override
    public void load(BlockState blockState, CompoundNBT nbt) {
        super.load(blockState, nbt);
        ListNBT list = nbt.getList("queue", 4);
        queue.clear();
        for (int i = 0; i < list.size(); i++) queue.add(BlockPos.of(((LongNBT) list.get(i)).getAsLong()));
    }

    @Override
    public void tick() {
        scanner = getNearbyQueue0(getLevel(), this, TileEntityTerrainScanner.class);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("");
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
        return new ScannerQueueContainer(id, playerInventory, this);
    }
}
