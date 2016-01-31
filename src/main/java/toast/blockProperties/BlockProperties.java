package toast.blockProperties;

import java.util.HashMap;

import net.minecraft.block.Block;
import toast.blockProperties.entry.BlockDropsInfo;
import toast.blockProperties.entry.BlockXPInfo;
import toast.blockProperties.entry.BreakSpeedInfo;
import toast.blockProperties.entry.HarvestingInfo;
import toast.blockProperties.entry.ItemStatsInfo;
import toast.blockProperties.entry.NBTStatsInfo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class BlockProperties implements IProperty {
    // Mapping of all loaded properties to the block id and metadata.
    private static final HashMap<Block, BlockProperties> PROPERTIES_MAP = new HashMap<Block, BlockProperties>();

    // Returns the mob properties for the given entity/id.
    public static BlockProperties getProperties(String id) {
        return BlockProperties.getProperties(Block.getBlockFromName(id));
    }
    public static BlockProperties getProperties(Block block) {
        return BlockProperties.PROPERTIES_MAP.get(block);
    }

    // Initializes block stats.
    public static void init() {
        for (BlockProperties blockProps : BlockProperties.PROPERTIES_MAP.values()) {
            blockProps.init(blockProps.getBlock());
        }
    }

    // Unloads all properties.
    public static void unload() {
        BlockProperties.PROPERTIES_MAP.clear();
    }

    // Turns a string of info into data. Crashes the game if something goes wrong.
    public static void load(String path, JsonObject node) {
        Block block;
        try {
            block = FileHelper.readBlock(node, path, "_name");
        }
        catch (BlockPropertyException ex) {
            _BlockPropertiesMod.console("[WARNING] Missing or invalid block at " + path);
            return;
        }
        if (BlockProperties.PROPERTIES_MAP.containsKey(block))
            throw new BlockPropertyException("Duplicate block file! (id: " + Block.blockRegistry.getNameForObject(block) + ")", path);

        JsonObject statsObj = node.getAsJsonObject("stats");
        JsonArray breakSpeedNodes = node.getAsJsonArray("break_speed");
        JsonArray dropsNodes = node.getAsJsonArray("drops");
        JsonArray harvestNodes = node.getAsJsonArray("harvest");
        JsonArray xpNodes = node.getAsJsonArray("xp");

		BlockProperties.PROPERTIES_MAP.put(block, new BlockProperties(path, node, block, statsObj, breakSpeedNodes, dropsNodes, harvestNodes, xpNodes));
    }

    // The block id used for this category.
    private final Block theBlock;
    // Array of altered block stats.
    private final BlockStats stats;
    // Array of harvest check data.
    private final IProperty harvest;
    // Array of break speed modifications.
    private final IProperty breakSpeed;
    // Array of experience value modifications.
    private final IProperty xp;
    // Array of added drop contents.
    private final IProperty drops;

    private BlockProperties(String path, JsonObject root, Block block, JsonObject statsObj, JsonArray breakSpeedNodes, JsonArray dropsNodes, JsonArray harvestNodes, JsonArray xpNodes) {
        FileHelper.verify(root, path, this);
        this.theBlock = block;
        this.stats = statsObj == null ? null : new BlockStats(path + "\\stats", root, statsObj);
        this.breakSpeed = breakSpeedNodes == null ? null : new BreakSpeed(path + "\\break_speed", root, breakSpeedNodes);
        this.drops = dropsNodes == null ? null : new BlockDrops(path + "\\drops", root, dropsNodes);
        this.harvest = harvestNodes == null ? null : new BlockHarvest(path + "\\harvest", root, harvestNodes);
        this.xp = xpNodes == null ? null : new BlockXP(path + "\\xp", root, xpNodes);
    }

    // Returns the block these properties apply to.
    public Block getBlock() {
        return this.theBlock;
    }

    // Returns an array of required field names.
    @Override
    public String[] getRequiredFields() {
        return new String[] { "_name" };
    }

    // Returns an array of optional field names.
    @Override
    public String[] getOptionalFields() {
        return new String[] { "break_speed", "drops", "harvest", "stats", "xp" };
    }

    // Initializes the block's stats.
    public void init(Block block) {
        if (this.stats != null) {
            this.stats.init(block);
        }
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
        if (this.harvest != null) {
            this.harvest.modifyHarvest(blockHarvest);
        }
    }

    // Modifies the break speed.
    @Override
    public void modifyBreakSpeed(BreakSpeedInfo blockBreakSpeed) {
        if (this.breakSpeed != null) {
            this.breakSpeed.modifyBreakSpeed(blockBreakSpeed);
        }
    }

    // Modifies the experience drop.
    @Override
    public void modifyXP(BlockXPInfo blockXP) {
        if (this.xp != null) {
            this.xp.modifyXP(blockXP);
        }
    }

    // Modifies the list of drops.
    @Override
    public void modifyDrops(BlockDropsInfo blockDrops) {
        if (this.drops != null) {
            this.drops.modifyDrops(blockDrops);
        }
    }
}
