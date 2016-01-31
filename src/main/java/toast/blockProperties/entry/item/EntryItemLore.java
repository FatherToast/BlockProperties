package toast.blockProperties.entry.item;

import toast.blockProperties.EffectHelper;
import toast.blockProperties.FileHelper;
import toast.blockProperties.IPropertyReader;
import toast.blockProperties.entry.EntryAbstract;
import toast.blockProperties.entry.ItemStatsInfo;

import com.google.gson.JsonObject;

public class EntryItemLore extends EntryAbstract {
    // The line of text to add.
    private final String contents;

    public EntryItemLore(String path, JsonObject root, int index, JsonObject node, IPropertyReader loader) {
        super(node, path);
        this.contents = FileHelper.readText(node, path, "value", ""); // TODO accept string arrays
    }

    // Returns an array of required field names.
    @Override
    public String[] getRequiredFields() {
        return new String[] { "value" };
    }

    // Returns an array of optional field names.
    @Override
    public String[] getOptionalFields() {
        return new String[] { };
    }

    // Modifies the item.
    @Override
    public void modifyItem(ItemStatsInfo itemStats) {
        EffectHelper.addItemText(itemStats.theItem, this.contents);
    }
}