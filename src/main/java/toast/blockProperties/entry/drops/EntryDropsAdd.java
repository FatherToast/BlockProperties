package toast.blockProperties.entry.drops;

import net.minecraft.item.Item;
import toast.blockProperties.FileHelper;
import toast.blockProperties.IPropertyReader;
import toast.blockProperties.ItemStats;
import toast.blockProperties.entry.BlockDropsInfo;
import toast.blockProperties.entry.EntryAbstract;

import com.google.gson.JsonObject;

public class EntryDropsAdd extends EntryAbstract {
    // The item id.
    private final Item item;
    // The min and max item damage.
    private final double[] damages;
    // The min and max item counts.
    private final double[] counts;
    // The item's stats.
    private final ItemStats itemStats;

    public EntryDropsAdd(String path, JsonObject root, int index, JsonObject node, IPropertyReader loader) {
        super(node, path);
        this.item = FileHelper.readItem(node, path, "id");
        this.damages = FileHelper.readCounts(node, path, "damage", 0.0, 0.0);
        this.counts = FileHelper.readCounts(node, path, "count", 1.0, 1.0);
        this.itemStats = new ItemStats(path, root, index, node, loader);
    }

    // Returns an array of required field names.
    @Override
    public String[] getRequiredFields() {
        return new String[] { "id" };
    }

    // Returns an array of optional field names.
    @Override
    public String[] getOptionalFields() {
        return new String[] { "damage", "count", "item_stats" };
    }

    // Modifies the list of drops.
    @Override
    public void modifyDrops(BlockDropsInfo blockDrops) {
        int damage = FileHelper.getCount(this.damages, blockDrops.random);
        int count = FileHelper.getCount(this.counts, blockDrops.random);
        blockDrops.addDrop(this.item, damage, count, this.itemStats);
    }
}