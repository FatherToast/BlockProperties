package toast.blockProperties.entry.nbt;

import net.minecraft.nbt.NBTTagList;
import toast.blockProperties.FileHelper;
import toast.blockProperties.IPropertyReader;
import toast.blockProperties.NBTStats;
import toast.blockProperties.entry.EntryAbstract;
import toast.blockProperties.entry.NBTStatsInfo;

import com.google.gson.JsonObject;

public class EntryNBTList extends EntryAbstract {
    // The name of this tag.
    private final String name;
    // The entry objects included in this property.
    private final NBTStats nbtStatsObj;

    public EntryNBTList(String path, JsonObject root, int index, JsonObject node, IPropertyReader loader) {
        super(node, path);
        this.name = FileHelper.readText(node, path, "name", "");
        this.nbtStatsObj = new NBTStats(path, root, index, node, loader);
    }

    // Returns an array of required field names.
    @Override
    public String[] getRequiredFields() {
        return new String[] { };
    }

    // Returns an array of optional field names.
    @Override
    public String[] getOptionalFields() {
        return new String[] { "name", "tags" };
    }

    // Adds any NBT tags to the list.
    @Override
    public void addTags(NBTStatsInfo nbtStats) {
        NBTTagList tag = new NBTTagList();
        this.nbtStatsObj.generate(nbtStats.theWorld, nbtStats.theItem, tag, nbtStats);
        nbtStats.addTag(this.name, tag);
    }
}