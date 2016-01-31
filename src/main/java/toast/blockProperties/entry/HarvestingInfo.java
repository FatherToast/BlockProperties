package toast.blockProperties.entry;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import toast.blockProperties.BlockHarvest;

public class HarvestingInfo {
    // The block being broken.
    public final Block theBlock;
    // The player harvesting the block.
    public final EntityPlayer harvester;
    // The harvester's random number generator.
    public final Random random;
    // The initial success of the event.
    public final boolean success;

    // The harvester's mining level on the block.
    public final int harvestLevel;
    // The harvester's fortune level.
    public final int fortune;
    // True if the harvester has silk touch.
    public final boolean silkTouch;

    // When set to true, the block is harvestable.
    public boolean newSuccess;

    public HarvestingInfo(EntityPlayer harvester, Block block, boolean success) {
        this.harvester = harvester;
        this.random = harvester.getRNG();
        this.theBlock = block;
        this.success = this.newSuccess = success;

        this.harvestLevel = BlockHarvest.getHarvestLevel(block, 0, harvester);
        this.silkTouch = EnchantmentHelper.getSilkTouchModifier(harvester);
        if (this.silkTouch) {
            this.fortune = 0;
        }
        else {
            this.fortune = EnchantmentHelper.getFortuneModifier(harvester);
        }
    }
}