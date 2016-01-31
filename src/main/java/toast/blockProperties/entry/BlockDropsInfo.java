package toast.blockProperties.entry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import toast.blockProperties.BlockHarvest;
import toast.blockProperties.ItemStats;

public class BlockDropsInfo {
    // The world this is in.
    public final World theWorld;
    // The world's random number generator.
    public final Random random;
    // The block being broken.
    public final Block theBlock;
    // The block's metadata (0-15).
    public final int metadata;
    // The block's coordinates.
    public final int x, y, z;
    // The harvester's fortune level.
    public final int fortune;
    // The list of drops.
    public final ArrayList<ItemStack> dropsList;
    // The list of drops to be added.
    public final ArrayList<ItemStack> addDropsList = new ArrayList<ItemStack>();
    // True if the harvester has silk touch.
    public final boolean silkTouch;
    // Drop chance for each item in dropsList.
    public final float dropChance;
    // The player harvesting the block. May be null.
    public final EntityPlayer harvester;

    // The harvest level of the harvester's tool on the block. -1 if inapplicable.
    public final int harvestLevel;

    // Filter for the original drops. 1=keep all, 0=destroy all
    public byte defaultBehavior = 1;
    // New drop chance to use.
    public float newDropChance;

    public BlockDropsInfo(World world, Block block, int blockMetadata, int x, int y, int z, int fortuneLevel, ArrayList<ItemStack> drops, boolean isSilkTouching, float dropChance, EntityPlayer harvester) {
        this.theWorld = world;
        this.random = world.rand;
        this.theBlock = block;
        this.metadata = blockMetadata;
        this.x = x;
        this.y = y;
        this.z = z;
        this.fortune = fortuneLevel;
        this.dropsList = drops;
        this.silkTouch = isSilkTouching;
        this.dropChance = this.newDropChance = dropChance;
        this.harvester = harvester;

        this.harvestLevel = BlockHarvest.getHarvestLevel(block, blockMetadata, harvester);
    }

    // Adds the item to the list of drops. If the stack size is negative, removes the item.
    public void addDrop(Item item, int damage, int count, ItemStats itemStats) {
        ItemStack dropStack = itemStats == null ? new ItemStack(item, count, damage) : itemStats.generate(this.theWorld, item, damage, this);
        if (dropStack.getItem() != null) {
            if (count > 0) {
                this.addDropsList.add(dropStack.copy());
            }
            else if (count < 0) {
                this.removeDrop(item, damage, count);
            }
        }
    }

    // Removes the item stack. Called when an item with a negative stack size is added.
    private void removeDrop(Item item, int damage, int count) {
        boolean infinite = count == -Integer.MAX_VALUE;
        count = this.removeDrop(item, damage, count, infinite, this.addDropsList);
        if (infinite || count < 0) {
            this.removeDrop(item, damage, count, infinite, this.dropsList);
        }
    }

    private int removeDrop(Item item, int damage, int count, boolean infinite, ArrayList<ItemStack> drops) {
        ItemStack dropStack;
        for (Iterator<ItemStack> iterator = drops.iterator(); iterator.hasNext();) {
            dropStack = iterator.next();
            if (item == dropStack.getItem() && (damage < 0 || damage == dropStack.getItemDamage())) {
                if (infinite) {
                    iterator.remove();
                }
                else {
                    count += dropStack.stackSize;
                    if (count < 0) {
                        iterator.remove();
                    }
                    else if (count == 0) {
                        iterator.remove();
                        return 0;
                    }
                    else {
                        dropStack.stackSize = count;
                        return 0;
                    }
                }
            }
        }
        return count;
    }

    // Applies the default behavior and adds the queued drops.
    public void applyDefaultAndAddDrops() {
        switch (this.defaultBehavior) {
            case 1:
                break;
            case 0:
                this.dropsList.clear();
                break;
        }
        this.dropsList.addAll(this.addDropsList);
    }
}