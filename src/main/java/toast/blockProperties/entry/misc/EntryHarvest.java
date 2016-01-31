package toast.blockProperties.entry.misc;

import toast.blockProperties.BlockPropertyException;
import toast.blockProperties.FileHelper;
import toast.blockProperties.IPropertyReader;
import toast.blockProperties.entry.EntryAbstract;
import toast.blockProperties.entry.HarvestingInfo;

import com.google.gson.JsonObject;

public class EntryHarvest extends EntryAbstract {
    // The value to set for the default.
    private final byte value;

    public EntryHarvest(String path, JsonObject root, int index, JsonObject node, IPropertyReader loader) {
        super(node, path);
        String text = FileHelper.readText(node, path, "value", "");
        if (text.equals("true")) {
            this.value = 1;
        }
        else if (text.equals("false")) {
            this.value = 0;
        }
        else if (text.equals("default")) {
            this.value = -1;
        }
        else
            throw new BlockPropertyException("Invalid harvest check value! (must be true, false, or default)", path);
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

    // Modifies the harvesting requirements.
    @Override
    public void modifyHarvest(HarvestingInfo blockHarvest) {
        switch (this.value) {
            case 1: // true
            	blockHarvest.newSuccess = true;
                break;
            case 0: // false
            	blockHarvest.newSuccess = false;
                break;
            case -1: // default
            	blockHarvest.newSuccess = blockHarvest.success;
                break;
        }
    }
}