package eladkay.scanner.compat;

import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class Oregistry {
    public static List<Entry> entries = Lists.newArrayList();

    @Nullable
    public static Entry registerEntry(Entry entry) {
        FMLLog.info("Registering Entry \"" + entry + "\".");

        if (!entries.contains(entry)) {
            entries.add(entry);
        } else {
            FMLLog.warning("Ore \"" + entry + "\" registered twice. Report this to the author of " + Loader.instance().activeModContainer().getModId() + ".");
            return null;
        }

        return entry;
    }


    @Nonnull
    public static Collection<Entry> getEntryList() {
        return entries;
    }

    public static class Entry {
        public IBlockState ore;
        public IBlockState material;
        public int rarity;
        public int maxY;
        public int minY;

        public Entry(IBlockState ore, IBlockState material, int rarity, int maxY, int minY) {
            this.ore = ore;
            this.material = material;
            this.rarity = rarity;
            this.maxY = maxY;
            this.minY = minY;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "ore=" + ore +
                    ", material=" + material +
                    ", rarity=" + rarity +
                    ", maxY=" + maxY +
                    ", minY=" + minY +
                    '}';
        }
    }

}
