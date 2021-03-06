package toast.blockProperties.entry;

import toast.blockProperties.BlockPropertyException;
import toast.blockProperties.FileHelper;
import toast.blockProperties.IProperty;
import toast.blockProperties.IPropertyReader;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class PropertyGroup extends EntryAbstract {
    // The min and max number of times to perform all tasks.
    private final double[] counts;
    // The entry objects included in this property.
    private final IProperty[] entries;

    public PropertyGroup(String path, JsonObject root, int index, JsonObject node, IPropertyReader loader) {
        super(node, path);
        this.counts = FileHelper.readCounts(node, path, "count", 1.0, 1.0);

        JsonArray nodes = node.getAsJsonArray("functions");
        if (nodes == null)
            throw new BlockPropertyException("Missing or invalid functions!", path);

        path += "\\functions";
        int length = nodes.size();
        this.entries = new IProperty[length];
        for (int i = 0; i < length; i++) {
            this.entries[i] = loader.readLine(path, root, i, nodes.get(i));
        }
    }

    // Returns an array of required field names.
    @Override
    public String[] getRequiredFields() {
        return new String[] { "functions" };
    }

    // Returns an array of optional field names.
    @Override
    public String[] getOptionalFields() {
        return new String[] { "count" };
    }

    // Modifies the item.
    @Override
    public void modifyItem(ItemStatsInfo itemStats) {
        for (int count = FileHelper.getCount(this.counts); count-- > 0;) {
            for (IProperty entry : this.entries) {
                if (entry != null) {
                    entry.modifyItem(itemStats);
                }
            }
        }
    }

    // Adds any NBT tags to the list.
    @Override
    public void addTags(NBTStatsInfo nbtStats) {
        for (int count = FileHelper.getCount(this.counts); count-- > 0;) {
            for (IProperty entry : this.entries) {
                if (entry != null) {
                    entry.addTags(nbtStats);
                }
            }
        }
    }

    // Modifies the list of drops.
    @Override
    public void modifyDrops(BlockDropsInfo mobDrops) {
        for (int count = FileHelper.getCount(this.counts); count-- > 0;) {
            for (IProperty entry : this.entries) {
                if (entry != null) {
                    entry.modifyDrops(mobDrops);
                }
            }
        }
    }
}
