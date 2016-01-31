package toast.blockProperties.entry.misc;

import net.minecraft.block.Block;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import toast.blockProperties.FileHelper;
import toast.blockProperties.IPropertyReader;
import toast.blockProperties.entry.BreakSpeedInfo;
import toast.blockProperties.entry.EntryAbstract;

import com.google.gson.JsonObject;

public class EntrySpeedCopy extends EntryAbstract {
    // The block id.
    private final Block block;
    // The block metadata.
    private final int blockData;

    public EntrySpeedCopy(String path, JsonObject root, int index, JsonObject node, IPropertyReader loader) {
        super(node, path);
        this.block = FileHelper.readBlock(node, path, "id");
        this.blockData = FileHelper.readInteger(node, path, "data", 0);
    }

    // Returns an array of required field names.
    @Override
    public String[] getRequiredFields() {
        return new String[] { "id" };
    }

    // Returns an array of optional field names.
    @Override
    public String[] getOptionalFields() {
        return new String[] { "data" };
    }

    // Modifies the break speed.
    @Override
    public void modifyBreakSpeed(BreakSpeedInfo blockBreakSpeed) {
        float hardness = this.block.getBlockHardness(blockBreakSpeed.harvester.worldObj, 0, -1, 0) / blockBreakSpeed.theBlock.getBlockHardness(blockBreakSpeed.harvester.worldObj, blockBreakSpeed.x, blockBreakSpeed.y, blockBreakSpeed.z);

        if (ForgeHooks.canHarvestBlock(this.block, blockBreakSpeed.harvester, blockBreakSpeed.metadata)) {
            blockBreakSpeed.newSpeedBase = blockBreakSpeed.harvester.getBreakSpeed(this.block, false, this.blockData, 0, -1, 0);
        }
        else {
            blockBreakSpeed.newSpeedBase = 0.3F;
        }
        blockBreakSpeed.newSpeedBase = ForgeEventFactory.getBreakSpeed(blockBreakSpeed.harvester, this.block, this.blockData, blockBreakSpeed.newSpeedBase / hardness, 0, -1, 0);
    }
}