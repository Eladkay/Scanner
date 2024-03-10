package eladkay.scanner.compat;

import com.google.common.collect.Lists;
import eladkay.scanner.ScannerMod;
import net.minecraft.block.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class Oregistry {
    public static List<Entry> entries = Lists.newArrayList();

    @Nullable
    public static Entry registerEntry(Entry entry) {
        ScannerMod.LOGGER.info("Registering Entry \"" + entry + "\".");

        if (!entries.contains(entry)) {
            entries.add(entry);
        } else {
            ScannerMod.LOGGER.warn("Ore \"" + entry + "\" registered twice. Report this to the author of " + ScannerMod.MODID + ".");
            return null;
        }

        return entry;
    }


    @Nonnull
    public static Collection<Entry> getEntryList() {
        return entries;
    }

    public static class Entry {
        public BlockState ore;
        public BlockState material;
        public int rarity;
        public int maxY;
        public int minY;

        public Entry(BlockState ore, BlockState material, int rarity, int maxY, int minY) {
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
