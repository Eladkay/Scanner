package eladkay.scanner.tiles;

import com.google.gson.Gson;
import eladkay.scanner.ScannerConfig;
import eladkay.scanner.init.ModBlocks;
import eladkay.scanner.init.ModTileEntities;
import eladkay.scanner.misc.BaseEnergyContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class TileEntityBiomeScanner extends BaseTE implements ITickableTileEntity {
    public Queue<BlockPos> biomeScanner = new ArrayDeque<>();

    public HashMap<ChunkPos, ITextComponent> mapping = new HashMap<>();
    public int type;

    public TileEntityBiomeScanner() {
        super(ModTileEntities.BIOME_SCANNER_TILE.get(), ScannerConfig.CONFIG.maxEnergyBufferBiome.get());
    }

    private static String serialize(ChunkPos pos) {
        return pos.x + "/" + pos.z;
    }

    public static ChunkPos deserialize(String s) {
        String[] split = s.split("/");
        return new ChunkPos(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    public BaseEnergyContainer container() {
        return container;
    }

    @Nullable
    public static TileEntityBiomeScanner getNearbyBiomeScanner(World world, TileEntityTerrainScanner scanner) {
        for (Direction facing : Direction.values()) {
            TileEntity te = world.getBlockEntity(scanner.getBlockPos().relative(facing));
            if (te instanceof TileEntityBiomeScanner) return (TileEntityBiomeScanner) te;
        }
        return null;
    }

    @Nullable
    public BlockPos pop() {
        return biomeScanner.poll();
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
        Gson gson = new Gson();
        HashMap<String, String> ret = new HashMap<>();
        for (Map.Entry<ChunkPos, ITextComponent> entry : mapping.entrySet())
            ret.put(serialize(entry.getKey()), ITextComponent.Serializer.toJson(entry.getValue()));
        String json = gson.toJson(ret);
        nbt.putString("json", json);
        nbt.putInt("type", type);
        return nbt;
    }

    @Override
    public void load(BlockState blockState, CompoundNBT nbt) {
        Gson gson = new Gson();
        HashMap<String, String> ret = gson.fromJson(nbt.getString("json"), HashMap.class);
        mapping.clear();
        for (Map.Entry<String, String> entry : ret.entrySet())
            mapping.put(deserialize(entry.getKey()), ITextComponent.Serializer.fromJson(entry.getValue()));
        type = nbt.getInt("type");
        super.load(blockState, nbt);
    }

    @Nullable
    public ITextComponent getMapping(int chunkX, int chunkY) {
        for (Map.Entry<ChunkPos, ITextComponent> entry : mapping.entrySet())
            if (entry.getKey().x == chunkX && entry.getKey().z == chunkY) return entry.getValue();
        return null;
    }

    @Override
    public void tick() {
        Block block = getBlockState().getBlock();
        this.type = block == ModBlocks.BIOME_SCANNER_BASIC.get() ? 0 : block == ModBlocks.BIOME_SCANNER_ADVANCED.get() ? 1 : block == ModBlocks.BIOME_SCANNER_ELITE.get() ? 2 : 3;
    }

    public int getDist(ChunkPos chunkPos) {
        int i0 = chunkPos.x - (int)(this.getBlockPos().getX() / 16.0);
        int i1 = chunkPos.z - (int)(this.getBlockPos().getZ() / 16.0);
        return (int) MathHelper.sqrt(i0 * i0 + i1 * i1);
    }
}
