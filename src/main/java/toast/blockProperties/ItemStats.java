package toast.blockProperties;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import toast.blockProperties.entry.ItemStatsInfo;
import toast.blockProperties.entry.PropertyChoose;
import toast.blockProperties.entry.PropertyExternal;
import toast.blockProperties.entry.PropertyGroup;
import toast.blockProperties.entry.PropertyGroupConditional;
import toast.blockProperties.entry.item.EntryItemColor;
import toast.blockProperties.entry.item.EntryItemEnchant;
import toast.blockProperties.entry.item.EntryItemLore;
import toast.blockProperties.entry.item.EntryItemModifier;
import toast.blockProperties.entry.item.EntryItemNBT;
import toast.blockProperties.entry.item.EntryItemName;
import toast.blockProperties.entry.item.EntryItemPotion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ItemStats implements IPropertyReader {
    // The property reader this is a part of.
    public final IPropertyReader parent;
    // The entry objects included in this property.
    public final IProperty[] entries;

    public ItemStats(String path, JsonObject root, int index, JsonObject node, IPropertyReader loader) {
        this.parent = loader;
        JsonArray nodes = node.getAsJsonArray("item_stats");
        if (nodes == null) {
            this.entries = new IProperty[0];
        }
        else {
            int length = nodes.size();
            this.entries = new IProperty[length];
            for (int i = 0; i < length; i++) {
                this.entries[i] = this.readLine(path, root, i, nodes.get(i));
            }
        }
    }

    // Generates an appropriate item stack with a stack size of 1.
    public ItemStack generate(World world, Item item, int damage, Object mobInfo) {
        ItemStack itemStack = new ItemStack(item, 1, damage);
        ItemStatsInfo info = new ItemStatsInfo(itemStack, world, mobInfo);
        for (IProperty entry : this.entries)
            if (entry != null) {
                entry.modifyItem(info);
            }
        return itemStack;
    }
    public void generate(ItemStatsInfo info) {
        for (IProperty entry : this.entries) {
            if (entry != null) {
                entry.modifyItem(info);
            }
        }
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

        if (function.equals("name"))
            return new EntryItemName(path, root, index, objNode, this);
        if (function.equals("modifier"))
            return new EntryItemModifier(path, root, index, objNode, this);
        if (function.equals("potion"))
            return new EntryItemPotion(path, root, index, objNode, this);
        if (function.equals("nbt"))
            return new EntryItemNBT(path, root, index, objNode, this);
        if (function.equals("enchant"))
            return new EntryItemEnchant(path, root, index, objNode, this);
        if (function.equals("lore"))
            return new EntryItemLore(path, root, index, objNode, this);
        if (function.equals("color"))
            return new EntryItemColor(path, root, index, objNode, this);

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
