package toast.blockProperties.entry.drops;

import toast.blockProperties.BlockPropertyException;
import toast.blockProperties.FileHelper;
import toast.blockProperties.IPropertyReader;
import toast.blockProperties.entry.BlockDropsInfo;
import toast.blockProperties.entry.EntryAbstract;

import com.google.gson.JsonObject;

public class EntryDropsDefault extends EntryAbstract {
    // The value to set for the default.
    private final byte value;

    public EntryDropsDefault(String path, JsonObject root, int index, JsonObject node, IPropertyReader loader) {
        super(node, path);
        String text = FileHelper.readText(node, path, "value", "");
        if (text.equals("true")) {
            this.value = 1;
        }
        else if (text.equals("false")) {
            this.value = 0;
        }
        else {
            this.value = 1;
            throw new BlockPropertyException("Invalid default value! (must be true or false)", path);
        }
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

    // Modifies the list of drops.
    @Override
    public void modifyDrops(BlockDropsInfo blockDrops) {
        blockDrops.defaultBehavior = this.value;
    }
}