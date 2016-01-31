package toast.blockProperties.entry.item;

import net.minecraft.nbt.NBTTagCompound;
import toast.blockProperties.IPropertyReader;
import toast.blockProperties.NBTStats;
import toast.blockProperties.entry.EntryAbstract;
import toast.blockProperties.entry.ItemStatsInfo;

import com.google.gson.JsonObject;

public class EntryItemNBT extends EntryAbstract {
    // The nbt stats for this property.
    private final NBTStats nbtStats;

    public EntryItemNBT(String path, JsonObject root, int index, JsonObject node, IPropertyReader loader) {
        super(node, path);
        this.nbtStats = new NBTStats(path, root, index, node, loader);
    }

    // Returns an array of required field names.
    @Override
    public String[] getRequiredFields() {
        return new String[] { "tags" };
    }

    // Returns an array of optional field names.
    @Override
    public String[] getOptionalFields() {
        return new String[] { };
    }

    // Modifies the item.
    @Override
    public void modifyItem(ItemStatsInfo itemStats) {
        if (!itemStats.theItem.hasTagCompound()) {
            itemStats.theItem.setTagCompound(new NBTTagCompound());
        }
        this.nbtStats.generate(itemStats.theWorld, itemStats.theItem, itemStats.theItem.getTagCompound(), itemStats);
    }
}
