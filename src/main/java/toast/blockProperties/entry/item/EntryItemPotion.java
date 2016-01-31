package toast.blockProperties.entry.item;

import toast.blockProperties.EffectHelper;
import toast.blockProperties.FileHelper;
import toast.blockProperties.IPropertyReader;
import toast.blockProperties.entry.EntryAbstract;
import toast.blockProperties.entry.ItemStatsInfo;

import com.google.gson.JsonObject;

public class EntryItemPotion extends EntryAbstract {
    // The attribute name to modify.
    private final int potionId;
    // The min and max potion amplifier.
    private final double[] amplifiers;
    // The min and max potion duration.
    private final double[] durations;
    // If true, the particles will be less visible.
    private final boolean ambient;

    public EntryItemPotion(String path, JsonObject root, int index, JsonObject node, IPropertyReader loader) {
        super(node, path);
        this.potionId = FileHelper.readPotion(node, path, "id").id;
        this.amplifiers = FileHelper.readCounts(node, path, "amplifier", 0.0, 0.0);
        this.durations = FileHelper.readCounts(node, path, "duration", Integer.MAX_VALUE, Integer.MAX_VALUE);
        this.ambient = FileHelper.readBoolean(node, path, "ambient", false);
    }

    // Returns an array of required field names.
    @Override
    public String[] getRequiredFields() {
        return new String[] { "id" };
    }

    // Returns an array of optional field names.
    @Override
    public String[] getOptionalFields() {
        return new String[] { "amplifier", "duration", "ambient" };
    }

    // Modifies the item.
    @Override
    public void modifyItem(ItemStatsInfo itemStats) {
        int amplifier = FileHelper.getCount(this.amplifiers, itemStats.random);
        int duration = FileHelper.getCount(this.durations, itemStats.random);
        EffectHelper.addPotionEffect(itemStats.theItem, this.potionId, duration, amplifier, this.ambient);
    }
}