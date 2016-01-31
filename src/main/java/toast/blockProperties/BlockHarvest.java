package toast.blockProperties;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import toast.blockProperties.entry.BlockDropsInfo;
import toast.blockProperties.entry.BlockXPInfo;
import toast.blockProperties.entry.BreakSpeedInfo;
import toast.blockProperties.entry.HarvestingInfo;
import toast.blockProperties.entry.ItemStatsInfo;
import toast.blockProperties.entry.NBTStatsInfo;
import toast.blockProperties.entry.PropertyChoose;
import toast.blockProperties.entry.PropertyExternal;
import toast.blockProperties.entry.PropertyGroup;
import toast.blockProperties.entry.PropertyGroupConditional;
import toast.blockProperties.entry.misc.EntryHarvest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class BlockHarvest implements IProperty, IPropertyReader {

    // Returns the effective harvest level of a tool on a specific block.
    public static int getHarvestLevel(Block block, int metadata, EntityPlayer harvester) {
        if (block.getMaterial().isToolNotRequired())
            return Integer.MAX_VALUE;
        if (harvester == null)
            return -1;
        ItemStack tool = harvester.getHeldItem();
        String reqTool = block.getHarvestTool(metadata);
        if (reqTool == null)
            return Integer.MAX_VALUE;
        if (tool == null)
            return -1;
        return tool.getItem().getHarvestLevel(tool, reqTool);
    }

    // The entry objects included in this property.
    private final IProperty[] entries;

    public BlockHarvest(String path, JsonObject root, JsonArray nodes) {
        int length = nodes.size();
        this.entries = new IProperty[length];
        for (int i = 0; i < length; i++) {
            this.entries[i] = this.readLine(path, root, i, nodes.get(i));
        }
    }

    // Returns an array of required field names.
    @Override
    public String[] getRequiredFields() {
        return new String[] { };
    }

    // Returns an array of optional field names.
    @Override
    public String[] getOptionalFields() {
        return new String[] { };
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
    public void modifyHarvest(HarvestingInfo harvest) {
        for (IProperty entry : this.entries) {
            if (entry != null) {
                entry.modifyHarvest(harvest);
            }
        }
    }

    // Modifies the break speed.
    @Override
    public void modifyBreakSpeed(BreakSpeedInfo breakSpeed) {
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

    // Loads a line as a mob property.
    @Override
    public IProperty readLine(String path, JsonObject root, int index, JsonElement node) {
        path += "\\entry_" + (index + 1);
        if (!node.isJsonObject())
            throw new BlockPropertyException("Invalid node (object expected)!", path);
        JsonObject objNode = node.getAsJsonObject();
        String function = null;
        try {
            function = objNode.get("function").getAsString();
        }
        catch (NullPointerException ex) {
            // Do nothing
        }
        catch (IllegalArgumentException ex) {
            // Do nothing
        }
        if (function == null)
            throw new BlockPropertyException("Missing function name!", path);
        path += "(" + function + ")";

        if (function.equals("all"))
            return new PropertyGroup(path, root, index, objNode, this);
        if (function.equals("choose"))
            return new PropertyChoose(path, root, index, objNode, this);
        if (function.equals("external"))
            return new PropertyExternal(path, root, index, objNode, this);

        if (function.equals("set"))
            return new EntryHarvest(path, root, index, objNode, this);

        boolean inverted = false;
        if (function.startsWith(Character.toString(FileHelper.CHAR_INVERT))) {
            inverted = true;
            function = function.substring(1);
        }
        if (function.startsWith("if_"))
            return new PropertyGroupConditional(path, root, index, objNode, this, function.substring(3), inverted);

        throw new BlockPropertyException("Invalid function name!", path);
    }
}
