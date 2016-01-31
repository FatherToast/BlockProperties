package toast.blockProperties.entry;

import java.util.HashMap;

import toast.blockProperties.BlockDrops;
import toast.blockProperties.BlockHarvest;
import toast.blockProperties.BlockPropertyException;
import toast.blockProperties.BlockXP;
import toast.blockProperties.BreakSpeed;
import toast.blockProperties.FileHelper;
import toast.blockProperties.IPropertyReader;
import toast.blockProperties.ItemStats;
import toast.blockProperties.NBTStats;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class PropertyExternal extends EntryAbstract {
    // Mapping of all loaded external functions to their file name.
    private static final HashMap<String, BlockHarvest> HARVEST_MAP = new HashMap<String, BlockHarvest>();
    private static final HashMap<String, BreakSpeed> BREAK_SPEED_MAP = new HashMap<String, BreakSpeed>();
    private static final HashMap<String, BlockXP> XP_MAP = new HashMap<String, BlockXP>();
    private static final HashMap<String, BlockDrops> DROPS_MAP = new HashMap<String, BlockDrops>();
    private static final HashMap<String, ItemStats> ITEMS_MAP = new HashMap<String, ItemStats>();
    private static final HashMap<String, NBTStats> NBT_MAP = new HashMap<String, NBTStats>();

    // Unloads all properties.
    public static void unload() {
        PropertyExternal.HARVEST_MAP.clear();
        PropertyExternal.BREAK_SPEED_MAP.clear();
        PropertyExternal.XP_MAP.clear();
        PropertyExternal.DROPS_MAP.clear();
        PropertyExternal.ITEMS_MAP.clear();
        PropertyExternal.NBT_MAP.clear();
    }

    // Turns a string of info into data. Crashes the game if something goes wrong.
    public static void load(String type, String path, String fileName, JsonObject node) {
        String name = fileName.substring(0, fileName.length() - 5);
        if (type.equals("harvest")) {
            PropertyExternal.loadHarvest(path, name, node);
        }
        else if (type.equals("speed")) {
            PropertyExternal.loadSpeed(path, name, node);
        }
        else if (type.equals("xp")) {
            PropertyExternal.loadXP(path, name, node);
        }
        else if (type.equals("drops")) {
            PropertyExternal.loadDrop(path, name, node);
        }
        else if (type.equals("items")) {
            PropertyExternal.loadItem(path, name, node);
        }
        else if (type.equals("nbt")) {
            PropertyExternal.loadNbt(path, name, node);
        }
    }
    private static void loadHarvest(String path, String name, JsonObject node) {
        if (PropertyExternal.HARVEST_MAP.containsKey(name))
            throw new BlockPropertyException("Duplicate external harvest property! (name: " + name + ")", path);

        JsonObject dummyRoot = new JsonObject();
        JsonArray dummyArray = new JsonArray();
        dummyArray.add(node);
        dummyRoot.add("harvest", dummyArray);
        PropertyExternal.HARVEST_MAP.put(name, new BlockHarvest(path, dummyRoot, dummyArray));
    }
    private static void loadSpeed(String path, String name, JsonObject node) {
        if (PropertyExternal.BREAK_SPEED_MAP.containsKey(name))
            throw new BlockPropertyException("Duplicate external break speed property! (name: " + name + ")", path);

        JsonObject dummyRoot = new JsonObject();
        JsonArray dummyArray = new JsonArray();
        dummyArray.add(node);
        dummyRoot.add("break_speed", dummyArray);
        PropertyExternal.BREAK_SPEED_MAP.put(name, new BreakSpeed(path, dummyRoot, dummyArray));
    }
    private static void loadXP(String path, String name, JsonObject node) {
        if (PropertyExternal.XP_MAP.containsKey(name))
            throw new BlockPropertyException("Duplicate external xp property! (name: " + name + ")", path);

        JsonObject dummyRoot = new JsonObject();
        JsonArray dummyArray = new JsonArray();
        dummyArray.add(node);
        dummyRoot.add("xp", dummyArray);
        PropertyExternal.XP_MAP.put(name, new BlockXP(path, dummyRoot, dummyArray));
    }
    private static void loadDrop(String path, String name, JsonObject node) {
        if (PropertyExternal.DROPS_MAP.containsKey(name))
            throw new BlockPropertyException("Duplicate external drops property! (name: " + name + ")", path);

        JsonObject dummyRoot = new JsonObject();
        JsonArray dummyArray = new JsonArray();
        dummyArray.add(node);
        dummyRoot.add("drops", dummyArray);
        PropertyExternal.DROPS_MAP.put(name, new BlockDrops(path, dummyRoot, dummyArray));
    }
    private static void loadItem(String path, String name, JsonObject node) {
        if (PropertyExternal.ITEMS_MAP.containsKey(name))
            throw new BlockPropertyException("Duplicate external item stats property! (name: " + name + ")", path);

        JsonObject dummyRoot = new JsonObject();
        JsonArray dummyArray = new JsonArray();
        dummyArray.add(node);
        dummyRoot.add("item_stats", dummyArray);
        PropertyExternal.ITEMS_MAP.put(name, new ItemStats(path, dummyRoot, 0, dummyRoot, null));
    }
    private static void loadNbt(String path, String name, JsonObject node) {
        if (PropertyExternal.NBT_MAP.containsKey(name))
            throw new BlockPropertyException("Duplicate external nbt stats property! (name: " + name + ")", path);

        JsonObject dummyRoot = new JsonObject();
        JsonArray dummyArray = new JsonArray();
        dummyArray.add(node);
        dummyRoot.add("tags", dummyArray);
        PropertyExternal.NBT_MAP.put(name, new NBTStats(path, dummyRoot, 0, dummyRoot, null));
    }

    // The min and max number of times to perform the task.
    private final double[] counts;
    // The name of the external function to use.
    private final String externalFunction;

    public PropertyExternal(String path, JsonObject root, int index, JsonObject node, IPropertyReader loader) {
        super(node, path);
        this.counts = FileHelper.readCounts(node, path, "count", 1.0, 1.0);

        this.externalFunction = FileHelper.readText(node, path, "file", "");
        if (this.externalFunction == "")
            throw new BlockPropertyException("Missing or invalid external file name!", path);
    }

    // Returns an array of required field names.
    @Override
    public String[] getRequiredFields() {
        return new String[] { "file" };
    }

    // Returns an array of optional field names.
    @Override
    public String[] getOptionalFields() {
        return new String[] { "count" };
    }

    // Modifies the item.
    @Override
    public void modifyItem(ItemStatsInfo itemStats) {
        ItemStats stats = PropertyExternal.ITEMS_MAP.get(this.externalFunction);
        if (stats != null) {
            for (int count = FileHelper.getCount(this.counts); count-- > 0;) {
                stats.generate(itemStats);
            }
        }
        else {
            super.modifyItem(itemStats);
        }
    }

    // Adds any NBT tags to the list.
    @Override
    public void addTags(NBTStatsInfo nbtStats) {
        NBTStats stats = PropertyExternal.NBT_MAP.get(this.externalFunction);
        if (stats != null) {
            for (int count = FileHelper.getCount(this.counts); count-- > 0;) {
                stats.generate(nbtStats);
            }
        }
        else {
            super.addTags(nbtStats);
        }
    }

    // Modifies the harvesting requirements.
    @Override
    public void modifyHarvest(HarvestingInfo harvest) {
        BlockHarvest blockHarvest = PropertyExternal.HARVEST_MAP.get(this.externalFunction);
        if (blockHarvest != null) {
            for (int count = FileHelper.getCount(this.counts); count-- > 0;) {
                blockHarvest.modifyHarvest(harvest);
            }
        }
        else {
            super.modifyHarvest(harvest);
        }
    }

    // Modifies the break speed.
    @Override
    public void modifyBreakSpeed(BreakSpeedInfo breakSpeed) {
        BreakSpeed speed = PropertyExternal.BREAK_SPEED_MAP.get(this.externalFunction);
        if (speed != null) {
            for (int count = FileHelper.getCount(this.counts); count-- > 0;) {
                speed.modifyBreakSpeed(breakSpeed);
            }
        }
        else {
            super.modifyBreakSpeed(breakSpeed);
        }
    }

    // Modifies the experience drop.
    @Override
    public void modifyXP(BlockXPInfo blockXP) {
        BlockXP xp = PropertyExternal.XP_MAP.get(this.externalFunction);
        if (xp != null) {
            for (int count = FileHelper.getCount(this.counts); count-- > 0;) {
                xp.modifyXP(blockXP);
            }
        }
        else {
            super.modifyXP(blockXP);
        }
    }

    // Modifies the list of drops.
    @Override
    public void modifyDrops(BlockDropsInfo blockDrops) {
        BlockDrops drops = PropertyExternal.DROPS_MAP.get(this.externalFunction);
        if (drops != null) {
            for (int count = FileHelper.getCount(this.counts); count-- > 0;) {
                drops.modifyDrops(blockDrops);
            }
        }
        else {
            super.modifyDrops(blockDrops);
        }
    }
}
