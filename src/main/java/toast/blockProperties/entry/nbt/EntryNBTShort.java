package toast.blockProperties.entry.nbt;

import net.minecraft.nbt.NBTTagShort;
import toast.blockProperties.FileHelper;
import toast.blockProperties.IPropertyReader;
import toast.blockProperties.entry.NBTStatsInfo;

import com.google.gson.JsonObject;

public class EntryNBTShort extends EntryNBTNumber {
    public EntryNBTShort(String path, JsonObject root, int index, JsonObject node, IPropertyReader loader) {
        super(path, root, index, node, loader);
    }

    // Adds any NBT tags to the list.
    @Override
    public void addTags(NBTStatsInfo nbtStats) {
        int value = FileHelper.getCount(this.values, nbtStats.random);
        nbtStats.addTag(this.name, new NBTTagShort((short) value));
    }
}