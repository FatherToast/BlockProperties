package toast.blockProperties.entry.drops;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import toast.blockProperties.BlockPropertyException;
import toast.blockProperties.FileHelper;
import toast.blockProperties.IPropertyReader;
import toast.blockProperties.NBTStats;
import toast.blockProperties.entry.BlockDropsInfo;
import toast.blockProperties.entry.EntryAbstract;

import com.google.gson.JsonObject;

public class EntryDropsSetBlock extends EntryAbstract {
    // The block id.
    private final Block block;
    // The block metadata.
    private final double[] blockData;
    // The code for the block update.
    private final byte update;
    // The min and max offsets.
    private final double[] offsetsX, offsetsY, offsetsZ;
    // The nbt stats for this property.
    private final NBTStats nbtStats;
    // The code for the override. 0=air, 1=all, 2=replaceable.
    private final byte override;

    public EntryDropsSetBlock(String path, JsonObject root, int index, JsonObject node, IPropertyReader loader) {
        super(node, path);
        this.block = FileHelper.readBlock(node, path, "id");
        this.blockData = FileHelper.readCounts(node, path, "data", 0.0, 0.0);
        this.update = (byte) FileHelper.readInteger(node, path, "update", 3);
        this.offsetsX = FileHelper.readCounts(node, path, "x", 0.0, 0.0);
        this.offsetsY = FileHelper.readCounts(node, path, "y", 0.0, 0.0);
        this.offsetsZ = FileHelper.readCounts(node, path, "z", 0.0, 0.0);
        this.nbtStats = new NBTStats(path, root, index, node, loader);

        String text = FileHelper.readText(node, path, "override", "replaceable");
        if (text.equals("true")) {
            this.override = 1;
        }
        else if (text.equals("false")) {
            this.override = 0;
        }
        else if (text.equals("replaceable")) {
            this.override = 2;
        }
        else {
            this.override = 2;
            throw new BlockPropertyException("Invalid override value! (must be true, false, or replaceable)", path);
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
        return new String[] { "data", "update", "x", "y", "z", "override", "tags" };
    }

    // Modifies the list of drops.
    @Override
    public void modifyDrops(BlockDropsInfo blockDrops) {
        if (!blockDrops.theWorld.isRemote) {
            int x = blockDrops.x + FileHelper.getCount(this.offsetsX);
            int y = blockDrops.y + FileHelper.getCount(this.offsetsY);
            int z = blockDrops.z + FileHelper.getCount(this.offsetsZ);
            int data = FileHelper.getCount(this.blockData);

            if (this.override != 1) {
                Block blockReplacing = blockDrops.theWorld.getBlock(x, y, z);
                if (blockReplacing != null && (this.override == 0 || !blockReplacing.getMaterial().isReplaceable()))
                    return;
            }

            blockDrops.theWorld.setBlock(x, y, z, this.block, data, this.update);

            if (this.nbtStats.hasEntries()) {
                TileEntity tileEntity = blockDrops.theWorld.getTileEntity(x, y, z);
                if (tileEntity != null) {
                    NBTTagCompound tag = new NBTTagCompound();
                    tileEntity.writeToNBT(tag);
                    this.nbtStats.generate(blockDrops.theWorld, tag, blockDrops);
                    tileEntity.readFromNBT(tag);
                }
            }
        }
    }
}
