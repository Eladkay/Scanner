package eladkay.scanner.biome;

import com.feed_the_beast.ftblib.lib.math.MathUtils;
import com.google.gson.Gson;
import eladkay.scanner.Config;
import eladkay.scanner.ScannerMod;
import eladkay.scanner.init.ModBlocks;
import eladkay.scanner.misc.BaseEnergyContainer;
import eladkay.scanner.misc.BaseTE;
import eladkay.scanner.terrain.TileEntityTerrainScanner;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class TileEntityBiomeScanner extends BaseTE implements ITickable {
    public Queue<BlockPos> biomeScanner = new ArrayDeque<>();

    public HashMap<ChunkPos, String> mapping = new HashMap<>();
    public int type;

    public TileEntityBiomeScanner() {
        super(Config.maxEnergyBufferBiome);
    }

    private static String serialize(ChunkPos pos) {
        return pos.x + "/" + pos.z;
    }

    public static ChunkPos deserialize(String s) {
        String[] split = s.split("/");
        return new ChunkPos(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    BaseEnergyContainer container() {
        return container;
    }

    @Nullable
    public static TileEntityBiomeScanner getNearbyBiomeScanner(World world, TileEntityTerrainScanner scanner) {
        for (EnumFacing facing : EnumFacing.values()) {
            TileEntity te = world.getTileEntity(scanner.getPos().offset(facing));
            if (te instanceof TileEntityBiomeScanner) return (TileEntityBiomeScanner) te;
        }
        return null;
    }

    @Nullable
    public BlockPos pop() {
        return biomeScanner.poll();
    }

    public void onBlockActivated(EntityPlayer player) {
        ScannerMod.proxy.openGuiBiomeScanner(this);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return tag;
    }


    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        Gson gson = new Gson();
        HashMap<String, String> ret = new HashMap<>();
        for (Map.Entry<ChunkPos, String> entry : mapping.entrySet())
            ret.put(serialize(entry.getKey()), entry.getValue());
        String json = gson.toJson(ret);
        compound.setString("json", json);
        compound.setInteger("type", type);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        Gson gson = new Gson();
        HashMap<String, String> ret = gson.fromJson(compound.getString("json"), HashMap.class);
        for (Map.Entry<String, String> entry : ret.entrySet())
            mapping.put(deserialize(entry.getKey()), entry.getValue());
        type = compound.getInteger("type");
        super.readFromNBT(compound);
    }

    @Nullable
    public String getMapping(int chunkX, int chunkY) {
        for (Map.Entry<ChunkPos, String> entry : mapping.entrySet())
            if (entry.getKey().x == chunkX && entry.getKey().z == chunkY) return entry.getValue();
        return null;
    }

    @Override
    public void update() {
        Block block = getWorld().getBlockState(pos).getBlock();
        this.type = block == ModBlocks.biomeScannerBasic ? 0 : block == ModBlocks.biomeScannerAdv ? 1 : block == ModBlocks.biomeScannerElite ? 2 : 3;
    }

    public int getDist(ChunkPos chunkPos) {
        double d0 = this.pos.getX() - MathUtils.chunk(chunkPos.x);
        double d1 = this.pos.getZ() - MathUtils.chunk(chunkPos.z);
        return (int) (MathHelper.sqrt(d0 * d0 + d1 * d1) / 16D);
    }
}
