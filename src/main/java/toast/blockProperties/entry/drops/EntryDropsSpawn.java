package toast.blockProperties.entry.drops;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;
import toast.blockProperties.BlockPropertyException;
import toast.blockProperties.FileHelper;
import toast.blockProperties.IPropertyReader;
import toast.blockProperties.NBTStats;
import toast.blockProperties.entry.BlockDropsInfo;
import toast.blockProperties.entry.EntryAbstract;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class EntryDropsSpawn extends EntryAbstract {

    private static final String MP_TAG_BASE = "mp|drops";
    private static final String MP_TAG_STATS = "pfs";

    // The item id.
    private final String entityId;
    // The min and max item counts.
    private final double[] counts;
    // The nbt stats for this property.
    private final NBTStats nbtStats;
    // The entity's stats to pass to Mob Properties.
    private final String[] mpStats;

    public EntryDropsSpawn(String path, JsonObject root, int index, JsonObject node, IPropertyReader loader) {
        super(node, path);
        this.entityId = FileHelper.readText(node, path, "id", "");
        this.counts = FileHelper.readCounts(node, path, "count", 1.0, 1.0);
        this.nbtStats = new NBTStats(path, root, index, node, loader);

        JsonArray nodes = node.getAsJsonArray("mp_external");
        if (nodes != null) {
	        path += "\\mp_external";
	        int length = nodes.size();
	        this.mpStats = new String[length];
	        for (int i = 0; i < length; i++) {
	        	try {
	        		this.mpStats[i] = FileHelper.getFunctionString(nodes.get(i).getAsJsonObject(), path + "\\entry_" + (i + 1));
	        	}
	        	catch (Exception ex) {
	        		throw new BlockPropertyException("Failed to get string for Mob Properties function!", path + "\\entry_" + (i + 1), ex);
	        	}
	        }
        }
        else {
        	this.mpStats = null;
        }
    }

    // Returns an array of required field names.
    @Override
    public String[] getRequiredFields() {
        return new String[] { "id" };
    }

    // Returns an array of optional field names.
    @Override
    public String[] getOptionalFields() {
        return new String[] { "count", "tags", "mp_external" };
    }

    // Modifies the list of drops.
    @Override
    public void modifyDrops(BlockDropsInfo blockDrops) {
        Entity entity;
        if (!blockDrops.theWorld.isRemote) {
            for (int count = FileHelper.getCount(this.counts); count-- > 0;) {
                entity = EntityList.createEntityByName(this.entityId, blockDrops.theWorld);
                if (entity == null)
                    return;

                this.initEntity(blockDrops.theWorld, entity, blockDrops);

                blockDrops.theWorld.spawnEntityInWorld(entity);
            }
        }
    }

    // Initializes an entity to be "dropped".
    private void initEntity(World world, Entity entity, BlockDropsInfo blockDrops) {
        entity.rotationYaw = blockDrops.random.nextFloat() * 180.0F;
        if (this.nbtStats.hasEntries()) {
            NBTTagCompound tag = new NBTTagCompound();
            entity.writeToNBT(tag);
            this.nbtStats.generate(world, tag, blockDrops);
            entity.readFromNBT(tag);
            entity.setPosition(blockDrops.x + entity.posX, blockDrops.y + entity.posY, blockDrops.z + entity.posZ);
        }
        else {
            entity.setPosition(blockDrops.x, blockDrops.y, blockDrops.z);
        }

        if (entity instanceof EntityLivingBase && this.mpStats != null) {
            NBTTagCompound tag = entity.getEntityData().getCompoundTag(EntryDropsSpawn.MP_TAG_BASE);
            if (!entity.getEntityData().hasKey(EntryDropsSpawn.MP_TAG_BASE)) {
            	entity.getEntityData().setTag(EntryDropsSpawn.MP_TAG_BASE, tag);
            }

            NBTTagList statsList = tag.getTagList(EntryDropsSpawn.MP_TAG_STATS, new NBTTagString().getId());
            if (!tag.hasKey(EntryDropsSpawn.MP_TAG_STATS)) {
                tag.setTag(EntryDropsSpawn.MP_TAG_STATS, statsList);
            }
            for (String statsFunction : this.mpStats) {
                statsList.appendTag(new NBTTagString(statsFunction));
            }
        }
    }
}
