package toast.blockProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.init.Blocks;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/** Things that can be changed:<br>
    setLightOpacity(int),<br>
    setLightLevel(float),<br>
    setHardness(float),<br>
    setResistance(float) - also set by setHardness(float),<br>
    setStepSound(Block.SoundType),<br>
    float slipperiness
*/
public class BlockStats {

    // Populates the given Json object with the block's default stats.
    public static JsonObject populateDefaults(JsonObject defaultNode, Block block) {
        JsonObject node = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : defaultNode.entrySet()) {
            if (entry.getKey() != null && !entry.getKey().equals("_name") && !entry.getKey().equals("_comment")) {
                node.add(entry.getKey(), entry.getValue());
            }
        }

        if (!node.has("opacity")) {
            node.addProperty("opacity", Integer.valueOf(block.getLightOpacity()));
        }
        if (!node.has("brightness")) {
            node.addProperty("brightness", Integer.valueOf(block.getLightValue()));
        }
        if (!node.has("hardness")) {
            try {
                node.addProperty("hardness", Float.valueOf(block.getBlockHardness(null, 0, -1, 0)));
            }
            catch (Exception ex) {
                _BlockPropertiesMod.console("[WARNING] Error while fetching default block hardness! for " + Block.blockRegistry.getNameForObject(block));
                ex.printStackTrace();
            }
        }
        if (!node.has("resistance")) {
            try {
                node.addProperty("resistance", Float.valueOf(block.getExplosionResistance(null) * 5.0F));
            }
            catch (Exception ex) {
                _BlockPropertiesMod.console("[WARNING] Error while fetching default block blast resistance! for " + Block.blockRegistry.getNameForObject(block));
                ex.printStackTrace();
            }
        }
        if (!node.has("sound")) {
            node.addProperty("sound", BlockStats.getSoundName(block.stepSound));
        }
        if (!node.has("slipperiness")) {
            node.addProperty("slipperiness", Float.valueOf(block.slipperiness));
        }

        if (!node.has("tool_data")) {
            JsonArray array = new JsonArray();

            boolean allSame = true;
            String tool = block.getHarvestTool(0);
            int level = block.getHarvestLevel(0);
            for (int m = 1; m < 16; m++) {
                if (tool == null ? block.getHarvestTool(m) != null : !tool.equals(block.getHarvestTool(m))) {
                    allSame = false;
                    break;
                }
            }
            if (allSame) {
                if (tool != null || level != -1) {
                    JsonObject toolInfo = new JsonObject();
                    if (tool != null) {
                        toolInfo.addProperty("tool", tool);
                    }
                    if (level != -1) {
                        toolInfo.addProperty("level", Integer.valueOf(level));
                    }
                    array.add(toolInfo);
                }
            }
            else {
                JsonObject toolInfo;
                for (int m = 0; m < 16; m++) {
                    tool = block.getHarvestTool(m);
                    level = block.getHarvestLevel(m);
                    if (tool != null || level != -1) {
                        toolInfo = new JsonObject();
                        toolInfo.addProperty("_meta", Integer.valueOf(m));
                        if (tool != null) {
                            toolInfo.addProperty("tool", tool);
                        }
                        if (level != -1) {
                            toolInfo.addProperty("level", Integer.valueOf(level));
                        }
                        array.add(toolInfo);
                    }
                }
            }
            node.add("tool_data", array);
        }
        if (block == Blocks.fire) {
            if (!node.has("fire_data")) {
                BlockFire fire = (BlockFire) block;
                JsonArray array = new JsonArray();

                JsonObject fireInfo;
                Block targetBlock;
                int flammability, spread;
                for (String blockId : new HashSet<String>(Block.blockRegistry.getKeys())) {
                    targetBlock = Block.getBlockFromName(blockId);
                    if (targetBlock == null || targetBlock == Blocks.air) {
                        continue;
                    }
                    flammability = fire.getFlammability(targetBlock);
                    spread = fire.getEncouragement(targetBlock);
                    if (flammability > 0 || spread > 0) {
                        fireInfo = new JsonObject();
                        fireInfo.addProperty("_name", blockId);
                        fireInfo.addProperty("flammability", Integer.valueOf(flammability));
                        fireInfo.addProperty("spread", Integer.valueOf(spread));
                        array.add(fireInfo);
                    }
                }
                node.add("fire_data", array);
            }
        }
        return node;
    }

    // Amount of light blocked (0-255).
    private final int lightOpacity;
    // Light level produced by this block (0-15).
    private final int lightLevel;
    // How slow the block is to mine.
    private final float hardness;
    // The block's blast resistance.
    private final float resistance;
    // Determines the sounds the block makes.
    private final Block.SoundType stepSound;
    // How "slippery" the block is.
    private final float slipperiness;

    // Array of required harvest tools for each possible metadata value.
    private final String[] toolData;
    // Array of required harvest levels for each possible metadata value.
    private final int[] toolLevel;
    // List of flammability data. Only used by fire blocks.
    private final ArrayList<FireInfo> fireData;

    public BlockStats(String path, JsonObject root, JsonObject node) {
        FileHelper.verify(node, path, new String[0], new String[] {
                "opacity", "brightness", "hardness", "resistance", "sound", "slipperiness", "tool_data", "fire_data"
        });

        this.lightOpacity = FileHelper.readInteger(node, path, "opacity", -1);
        this.lightLevel = FileHelper.readInteger(node, path, "brightness", -1);
        this.hardness = (float) FileHelper.readDouble(node, path, "hardness");
        this.resistance = (float) FileHelper.readDouble(node, path, "resistance");
        this.stepSound = BlockStats.getSound(path, FileHelper.readText(node, path, "sound", ""));
        this.slipperiness = (float) FileHelper.readDouble(node, path, "slipperiness");

        String[] tmpTools = new String[16];
        int[] tmpLevels = new int[16];
        this.readToolData(path, node, tmpTools, tmpLevels);
        this.toolData = tmpTools;
        this.toolLevel = tmpLevels;
        this.fireData = BlockStats.readFireData(path, node);
    }

    // Initializes the block's stats.
    public void init(Block block) {
        if (this.lightOpacity >= 0) {
            block.setLightOpacity(this.lightOpacity);
        }
        if (this.lightLevel >= 0) {
            block.setLightLevel(this.lightLevel / 15.0F);
        }
        if (!Float.isNaN(this.hardness)) {
            block.setHardness(this.hardness);
        }
        if (!Float.isNaN(this.resistance)) {
            block.setResistance(this.resistance / 3.0F);
        }
        if (this.stepSound != null) {
            block.setStepSound(this.stepSound);
        }
        if (!Float.isNaN(this.slipperiness)) {
            block.slipperiness = this.slipperiness;
        }

        for (int m = this.toolData.length; m-- > 0;) {
            if (!"\u0000".equals(this.toolData[m])) {
                block.setHarvestLevel(this.toolData[m], this.toolLevel[m], m);
            }
        }
        if (this.fireData != null) {
            if (block == Blocks.fire) {
                BlockFire fire = (BlockFire) block;
                for (FireInfo info : this.fireData) {
                    fire.setFireInfo(info.block, info.spread, info.flammability);
                }
                fire.rebuildFireInfo();
            }
            else
                throw new IllegalArgumentException("Attempted to apply fire data to a non-fire block!");
        }
    }

    // Loads tool data from the given code into the buffer arrays.
    private void readToolData(String path, JsonObject node, String[] tools, int[] levels) {
        if (node.has("tool_data")) {
            path += "\\tool_data";
            try {
                String[] required = { };
                String[] optional = { "_meta", "tool", "level" };

                for (int m = tools.length; m-- > 0;) {
                    tools[m] = "\u0000";
                    levels[m] = -1;
                }

                JsonArray nodes = node.getAsJsonArray("tool_data");
                if (nodes == null)
                    return;
                JsonObject toolInfo;
                String subpath;
                int meta;
                String tool;
                int level;
                int length = nodes.size();
                for (int i = 0; i < length; i++) {
                    toolInfo = (JsonObject) nodes.get(i);
                    subpath = path + "\\entry_" + (i + 1);
                    FileHelper.verify(toolInfo, subpath, required, optional);
                    meta = FileHelper.readInteger(toolInfo, subpath, "_meta", -1);
                    tool = FileHelper.readText(toolInfo, subpath, "tool", "");
                    if ("".equals(tool)) {
                        tool = null;
                    }
                    level = FileHelper.readInteger(toolInfo, subpath, "level", -1);
                    if (meta < 0) {
                        for (int m = tools.length; m-- > 0;) {
                            tools[m] = tool;
                            levels[m] = level;
                        }
                        return;
                    }
                    tools[meta] = tool;
                    levels[meta] = level;
                }
            }
            catch (Exception ex) {
                throw new BlockPropertyException("Error loading tool data!", path, ex);
            }
        }
    }

    // Mapping of step sounds to their names.
    private static final HashMap<String, Block.SoundType> NAME_TO_SOUND_MAP = new HashMap<String, Block.SoundType>();
    private static final HashMap<Block.SoundType, String> SOUND_TO_NAME_MAP = new HashMap<Block.SoundType, String>();

    // Returns the step sound for a given name.
    private static Block.SoundType getSound(String path, String soundName) {
        path += "\\sound";
        if (soundName == null || soundName.length() == 0 || soundName.equalsIgnoreCase("<unknown>"))
            return null;
        Block.SoundType sound = BlockStats.NAME_TO_SOUND_MAP.get(soundName);
        if (sound == null)
            throw new BlockPropertyException("Invalid step sound name! (must be stone, wood, gravel, grass, piston, metal, glass, cloth, sand, snow, ladder, or anvil)", path);
        return sound;
    }

    // Returns the step sound's name.
    private static String getSoundName(Block.SoundType sound) {
        String soundName = BlockStats.SOUND_TO_NAME_MAP.get(sound);
        return soundName == null ? "<unknown>" : soundName;
    }

    // Loads fire data from the given code.
    private static ArrayList<FireInfo> readFireData(String path, JsonObject node) {
        if (node.has("fire_data")) {
            path += "\\fire_data";
            try {
                String[] required = { "_name" };
                String[] optional = { "flammability", "spread" };

                ArrayList<FireInfo> fireData = new ArrayList<FireInfo>();
                JsonArray nodes = node.getAsJsonArray("fire_data");
                JsonObject fireInfo;
                String subpath;
                Block block;
                int length = nodes.size();
                for (int i = 0; i < length; i++) {
                    fireInfo = (JsonObject) nodes.get(i);
                    subpath = path + "\\entry_" + (i + 1);
                    FileHelper.verify(fireInfo, subpath, required, optional);
                    block = FileHelper.readBlock(fireInfo, subpath, "_name");
                    fireData.add(new FireInfo(block, FileHelper.readInteger(fireInfo, subpath, "flammability", 0), FileHelper.readInteger(fireInfo, subpath, "spread", 0)));
                }
                return fireData;
            }
            catch (Exception ex) {
                throw new BlockPropertyException("Error loading fire data!", path, ex);
            }
        }
        return null;
    }

    static {
        String[] names = {
                "stone", "wood", "gravel",
                "grass", "piston", "metal",
                "glass", "cloth", "sand",
                "snow", "ladder", "anvil"
        };
        Block.SoundType[] sounds = {
                Block.soundTypeStone, Block.soundTypeWood, Block.soundTypeGravel,
                Block.soundTypeGrass, Block.soundTypePiston, Block.soundTypeMetal,
                Block.soundTypeGlass, Block.soundTypeCloth, Block.soundTypeSand,
                Block.soundTypeSnow, Block.soundTypeLadder, Block.soundTypeAnvil
        };
        for (int i = names.length; i-- > 0;) {
            BlockStats.NAME_TO_SOUND_MAP.put(names[i], sounds[i]);
            BlockStats.SOUND_TO_NAME_MAP.put(sounds[i], names[i]);
        }
    }

    // Used to store flammability data.
    private static class FireInfo {
        public final Block block;
        public final int flammability;
        public final int spread;

        public FireInfo(Block block, int flammability, int spread) {
            this.block = block;
            this.flammability = flammability;
            this.spread = spread;
        }
    }
}
