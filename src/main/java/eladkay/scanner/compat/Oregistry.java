package eladkay.scanner.compat;

import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;

import java.util.Collection;
import java.util.List;

public class Oregistry {
    public static List<Entry> entries = Lists.newArrayList();

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


    public static Collection<Entry> getEntryList() {
        return entries;
    }

    public static class Entry {
        public Entry(IBlockState ore, int rarity, int maxY, int minY) {
            this.ore = ore;
            this.rarity = rarity;
            this.maxY = maxY;
            this.minY = minY;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "ore=" + ore +
                    ", rarity=" + rarity +
                    ", maxY=" + maxY +
                    ", minY=" + minY +
                    '}';
        }
        public IBlockState ore;
        public int rarity;
        public int maxY;
        public int minY;
    }

}
