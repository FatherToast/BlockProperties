package toast.blockProperties.entry.misc;

import toast.blockProperties.FileHelper;
import toast.blockProperties.IPropertyReader;
import toast.blockProperties.entry.BlockXPInfo;
import toast.blockProperties.entry.EntryAbstract;

import com.google.gson.JsonObject;

public class EntryXP extends EntryAbstract {
    // The operator to perform.
    private final byte operator;
    // The value of this entry.
    private final double[] values;

    public EntryXP(int operator, String path, JsonObject root, int index, JsonObject node, IPropertyReader loader) {
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

    // Modifies the experience drop.
    @Override
    public void modifyXP(BlockXPInfo blockXP) {
        switch (this.operator) {
            case 0: // set
                blockXP.newXpBase = FileHelper.getCount(this.values, blockXP.random);
                break;
            case 1: // add
                blockXP.xpAdd += FileHelper.getCount(this.values, blockXP.random);
                break;
            case 2: // mult
                blockXP.xpMult += FileHelper.getValue(this.values, blockXP.random);
                break;
        }
    }
}