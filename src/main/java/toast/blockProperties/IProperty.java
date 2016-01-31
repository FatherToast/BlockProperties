package toast.blockProperties;

import toast.blockProperties.entry.BlockDropsInfo;
import toast.blockProperties.entry.BlockXPInfo;
import toast.blockProperties.entry.BreakSpeedInfo;
import toast.blockProperties.entry.HarvestingInfo;
import toast.blockProperties.entry.ItemStatsInfo;
import toast.blockProperties.entry.NBTStatsInfo;

public interface IProperty {

    // Returns an array of required field names.
    public String[] getRequiredFields();

    // Returns an array of optional field names.
    public String[] getOptionalFields();

    // Modifies the item.
    public void modifyItem(ItemStatsInfo itemStats);

    // Adds any NBT tags to the list.
    public void addTags(NBTStatsInfo nbtStats);

    // Modifies the harvesting requirements.
    public void modifyHarvest(HarvestingInfo blockHarvest);

    // Modifies the break speed.
    public void modifyBreakSpeed(BreakSpeedInfo blockBreakSpeed);

    // Modifies the experience drop.
    public void modifyXP(BlockXPInfo blockXP);

    // Modifies the list of drops.
    public void modifyDrops(BlockDropsInfo blockDrops);
}
