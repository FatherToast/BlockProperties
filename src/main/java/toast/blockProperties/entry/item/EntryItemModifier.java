package toast.blockProperties.entry.item;

import toast.blockProperties.BlockPropertyException;
import toast.blockProperties.EffectHelper;
import toast.blockProperties.FileHelper;
import toast.blockProperties.IPropertyReader;
import toast.blockProperties.entry.EntryAbstract;
import toast.blockProperties.entry.ItemStatsInfo;

import com.google.gson.JsonObject;

public class EntryItemModifier extends EntryAbstract {
    // The attribute name to modify.
    private final String name;
    // The min and max amount to modify by.
    private final double[] values;
    // The operation to perform.
    private final int operation;

    public EntryItemModifier(String path, JsonObject root, int index, JsonObject node, IPropertyReader loader) {
        super(node, path);
        this.name = FileHelper.readText(node, path, "attribute", "");
        this.values = FileHelper.readCounts(node, path, "value", 0.0, 0.0);
        this.operation = FileHelper.readInteger(node, path, "operator", 0);
        if (this.operation < 0 || this.operation > 2)
            throw new BlockPropertyException("Invalid operator! (" + this.operation + ": 0=add, 1=additive_multiplier, 2=multiplicative_multiplier)", path);
    }

    // Returns an array of required field names.
    @Override
    public String[] getRequiredFields() {
        return new String[] { "attribute", "value" };
    }

    // Returns an array of optional field names.
    @Override
    public String[] getOptionalFields() {
        return new String[] { "operator" };
    }

    // Modifies the item.
    @Override
    public void modifyItem(ItemStatsInfo itemStats) {
        double value = FileHelper.getValue(this.values, itemStats.random);
        EffectHelper.addModifier(itemStats.theItem, this.name, value, this.operation);
    }
}