package eladkay.scanner.biome;

import com.google.gson.Gson;
import eladkay.scanner.Config;
import eladkay.scanner.misc.BaseTE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;

import java.util.HashMap;
import java.util.Map;

public class TileEntityBiomeScanner extends BaseTE {

    public HashMap<ChunkPos, String> mapping = new HashMap<>();
    public TileEntityBiomeScanner() {
        super(Config.maxEnergyBufferBiome);
    }

    public void onBlockActivated(EntityPlayer player) {
        if(worldObj.isRemote)
            new GuiBiomeScanner(0, pos).openGui();
    }

    public String toJson() {
        Gson gson = new Gson();
        HashMap<String, String> ret = new HashMap<>();
        for(Map.Entry<ChunkPos, String> entry : mapping.entrySet())
            ret.put(serialize(entry.getKey()), entry.getValue());
        return gson.toJson(ret);
    }

    private static String serialize(ChunkPos pos) {
        return pos.chunkXPos + "/" + pos.chunkZPos;
    }

    public static ChunkPos deserialize(String s) {
        String[] split = s.split("/");
        return new ChunkPos(Integer.valueOf(split[0]), Integer.valueOf(split[1]));
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
        for(Map.Entry<ChunkPos, String> entry : mapping.entrySet())
            ret.put(serialize(entry.getKey()), entry.getValue());
        String json = gson.toJson(ret);
        compound.setString("json", json);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        Gson gson = new Gson();
        HashMap<String, String> ret = gson.fromJson(compound.getString("json"), HashMap.class);
        for(Map.Entry<String, String> entry : ret.entrySet())
            mapping.put(deserialize(entry.getKey()), entry.getValue());
        super.readFromNBT(compound);
    }
}
