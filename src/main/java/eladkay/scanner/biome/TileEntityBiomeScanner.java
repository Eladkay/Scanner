package eladkay.scanner.biome;

import com.feed_the_beast.ftbl.lib.math.MathUtils;
import com.google.gson.Gson;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import eladkay.scanner.Config;
import eladkay.scanner.ScannerMod;
import eladkay.scanner.misc.TileEnergyConsumer;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@TileRegister("biomeScanner")
public class TileEntityBiomeScanner extends TileEnergyConsumer implements ITickable {

    @Save(saveName = "json")
    public HashMap<ChunkPos, String> mapping = new HashMap<>();
    @Save
    public int type;

    public TileEntityBiomeScanner() {
        super(Config.maxEnergyBufferBiome);
    }

    private static String serialize(ChunkPos pos) {
        return pos.chunkXPos + "/" + pos.chunkZPos;
    }

    public static ChunkPos deserialize(String s) {
        String[] split = s.split("/");
        return new ChunkPos(Integer.valueOf(split[0]), Integer.valueOf(split[1]));
    }


    public void onBlockActivated(EntityPlayer player) {
        ScannerMod.proxy.openGuiBiomeScanner(this);
    }

    public String toJson() {
        Gson gson = new Gson();
        HashMap<String, String> ret = new HashMap<>();
        for (Map.Entry<ChunkPos, String> entry : mapping.entrySet())
            ret.put(serialize(entry.getKey()), entry.getValue());
        return gson.toJson(ret);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return tag;
    }



    @Nullable
    public String getMapping(int chunkX, int chunkY) {
        for (Map.Entry<ChunkPos, String> entry : mapping.entrySet())
            if (entry.getKey().chunkXPos == chunkX && entry.getKey().chunkZPos == chunkY) return entry.getValue();
        return null;
    }

    @Override
    public void update() {
        Block block = getWorld().getBlockState(pos).getBlock();
        this.type = block == ScannerMod.biomeScannerBasic ? 0 : block == ScannerMod.biomeScannerAdv ? 1 : block == ScannerMod.biomeScannerElite ? 2 : 3;
    }

    public int getDist(ChunkPos chunkPos) {
        double d0 = pos.getX() - MathUtils.unchunk(chunkPos.chunkXPos);
        double d1 = pos.getZ() - MathUtils.unchunk(chunkPos.chunkZPos);
        return (int) (MathHelper.sqrt(d0 * d0 + d1 * d1) / 16D);
    }
}
