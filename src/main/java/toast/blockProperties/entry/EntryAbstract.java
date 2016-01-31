package toast.blockProperties.entry;

import toast.blockProperties.FileHelper;
import toast.blockProperties.IProperty;

import com.google.gson.JsonObject;

public abstract class EntryAbstract implements IProperty {
    // The Json string that makes up this property.
    private final String jsonString;

    public EntryAbstract(JsonObject node, String path) {
        FileHelper.verify(node, path, this);
        this.jsonString = FileHelper.getFunctionString(node, path);
    }

    // Returns this property's Json string.
    public String getJsonString() {
        return this.jsonString;
    }

    // Modifies the item.
    @Override
    public void modifyItem(ItemStatsInfo itemStats) {
        throw new UnsupportedOperationException("Non-item properties can not modify items!");
    }

    // Adds any NBT tags to the list.
    @Override
    public void addTags(NBTStatsInfo nbtStats) {
        throw new UnsupportedOperationException("Non-nbt properties can not modify nbt!");
    }

    // Modifies the harvesting requirements.
    @Override
    public void modifyHarvest(HarvestingInfo blockHarvest) {
        throw new UnsupportedOperationException("Non-harvest properties can not modify harvest requirements!");
    }

    // Modifies the break speed.
    @Override
    public void modifyBreakSpeed(BreakSpeedInfo blockBreakSpeed) {
        throw new UnsupportedOperationException("Non-break-speed properties can not modify break speed!");
    }

    // Modifies the experience drop.
    @Override
    public void modifyXP(BlockXPInfo blockXP) {
        throw new UnsupportedOperationException("Non-xp properties can not modify xp!");
    }

    // Modifies the list of drops.
    @Override
    public void modifyDrops(BlockDropsInfo blockDrops) {
        throw new UnsupportedOperationException("Non-drops properties can not modify drops!");
    }
}
