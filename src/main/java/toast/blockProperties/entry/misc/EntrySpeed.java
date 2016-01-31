package toast.blockProperties.entry.misc;

import toast.blockProperties.FileHelper;
import toast.blockProperties.IPropertyReader;
import toast.blockProperties.entry.BreakSpeedInfo;
import toast.blockProperties.entry.EntryAbstract;

import com.google.gson.JsonObject;

public class EntrySpeed extends EntryAbstract {
    // The operator to perform.
    private final byte operator;
    // The value of this entry.
    private final double[] values;

    public EntrySpeed(int operator, String path, JsonObject root, int index, JsonObject node, IPropertyReader loader) {
        super(node, path);
        this.operator = (byte) operator;
        this.values = FileHelper.readCounts(node, path, "value", 0.0, 0.0);
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

    // Modifies the break speed.
    @Override
    public void modifyBreakSpeed(BreakSpeedInfo blockBreakSpeed) {
        switch (this.operator) {
            case 0: // set
            	if (this.values[1] < 0.0) { // undo mods
                    blockBreakSpeed.newSpeedBase = blockBreakSpeed.originalSpeed;
        		}
            	else {
					blockBreakSpeed.newSpeedBase = FileHelper.getCount(this.values, blockBreakSpeed.random);
				}
                break;
            case 1: // add
                blockBreakSpeed.speedAdd += FileHelper.getCount(this.values, blockBreakSpeed.random);
                break;
            case 2: // mult
                blockBreakSpeed.speedMult += FileHelper.getValue(this.values, blockBreakSpeed.random);
                break;
        }
    }
}