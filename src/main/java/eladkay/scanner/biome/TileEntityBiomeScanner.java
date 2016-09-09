package eladkay.scanner.biome;

import com.google.gson.Gson;
import eladkay.scanner.Config;
import eladkay.scanner.misc.BaseTE;
import eladkay.scanner.misc.Vec2i;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class TileEntityBiomeScanner extends BaseTE implements ITickable {

    public HashMap<Vec2i, String> mapping = new HashMap<>();

    Vec2i currentChunk = null;
    int cooldown = -1;
    int range = 2;
    int x = 0;
    int y = 0;
    public TileEntityBiomeScanner() {
        super(Config.maxEnergyBufferBiome);
    }

    public void onBlockActivated(EntityPlayer player) {
        if(worldObj.isRemote)
            new GuiBiomeScanner(0).openGui();
        int range1 = (range - 1 ) / 2;
        if(currentChunk == null)
            currentChunk = new Vec2i(worldObj.getChunkFromBlockCoords(pos).xPosition, worldObj.getChunkFromBlockCoords(pos).zPosition);
        x = range1;
        y = range1;
        range = player.isSneaking() ? Math.max(range - 2, -1) : Math.min(range + 2, 5);
        System.out.println(toJson());
        System.out.println(currentChunk.serialize());
        System.out.println(range);
    }

    @Override
    public void update() {
        cooldown--;
        if (range < 0 || cooldown > 0 || currentChunk == null || container.getStoredPower() < Config.minEnergyPerChunkBiomeScanner || worldObj.isRemote) return;
        container.takePower(Config.minEnergyPerChunkBiomeScanner * range * Config.increase, false);
        markDirty();



        int range1 = range == 1 ? -1 : range == 3 ? -2 : range == 5 ? -3 : 0;
        int range2 = -range1;
        Vec2i vec = new Vec2i(worldObj.getChunkFromBlockCoords(pos).xPosition, worldObj.getChunkFromBlockCoords(pos).zPosition);
        /*else currentChunk = ThreadLocalRandom.current().nextBoolean() ?
                new Vec2i(currentChunk.getX() + 1, currentChunk.getY()):
                new Vec2i(currentChunk.getX(), currentChunk.getY() + 1);*/


        mapping.put(vec.add(x, y), worldObj.getBiomeGenForCoords(new BlockPos(vec.add(x, y).getX(), pos.getY(), vec.add(x, y).getY())).getBiomeName());
        x++;
        if(x == range2) {
            x = range1;
            y = range1;
            y++;
        }
        if(y == range2) {
            currentChunk = null;
            System.out.println("Done:" + toJson());
        }

        cooldown = Config.cooldown;
    }

    public String toJson() {
        Gson gson = new Gson();
        HashMap<String, String> ret = new HashMap<>();
        for(Map.Entry<Vec2i, String> entry : mapping.entrySet())
            ret.put(entry.getKey().serialize(), entry.getValue());
        return gson.toJson(ret);
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
        for(Map.Entry<Vec2i, String> entry : mapping.entrySet())
            ret.put(entry.getKey().serialize(), entry.getValue());
        String json = gson.toJson(ret);
        compound.setString("json", json);
        if(currentChunk != null)
            compound.setString("currentchunk", currentChunk.serialize());
        compound.setInteger("range", range);
        compound.setInteger("posY", y);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        Gson gson = new Gson();
        HashMap<String, String> ret = gson.fromJson(compound.getString("json"), HashMap.class);
        for(Map.Entry<String, String> entry : ret.entrySet())
            mapping.put(Vec2i.deserialize(entry.getKey()), entry.getValue());
        currentChunk = Vec2i.deserialize(compound.getString("currentchunk"));
        range = compound.getInteger("range");
        y = compound.getInteger("y");
        super.readFromNBT(compound);
    }
}
