package toast.blockProperties.entry;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemStatsInfo
{
    // The block info this is a part of.
    public final Object parent;
    // The item currently being initialized.
    public final ItemStack theItem;
    // The world that the item is dropping into.
    public final World theWorld;
    // The world's random number generator.
    public final Random random;

    public ItemStatsInfo(ItemStack item, World world, Object blockInfo) {
        this.parent = blockInfo;
        this.theItem = item;
        this.theWorld = world;
        this.random = world.rand;
    }
}