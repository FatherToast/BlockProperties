package toast.blockProperties.entry;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import toast.blockProperties.BlockHarvest;

public class BreakSpeedInfo {
    // The player harvesting the block.
    public final EntityPlayer harvester;
    // The player's random number generator.
    public final Random random;
    // The block being broken.
    public final Block theBlock;
    // The block's metadata (0-15).
    public final int metadata;
    // The block's coordinates.
    public final int x, y, z;
    // The original break speed.
    public final float originalSpeed;
    // The break speed as modified by any event listeners in other mods.
    public final float moddedSpeed;

    // The harvester's mining level on the block.
    public final int harvestLevel;
    // The harvester's fortune level.
    public final int fortune;
    // True if the harvester has silk touch.
    public final boolean silkTouch;

    // New base break speed. NaN if unused. 0.0 makes the block unbreakable, 1.0 makes it break instantly.
    public float newSpeedBase = Float.NaN;
    // Additional break speed.
    public float speedAdd = 0.0F;
    // Break speed multiplier.
    public float speedMult = 1.0F;

    public BreakSpeedInfo(EntityPlayer harvester, Block block, int metadata, float originalSpeed, float newSpeed, int x, int y, int z) {
        this.harvester = harvester;
        this.random = harvester.getRNG();
        this.theBlock = block;
        this.metadata = metadata;
        this.x = x;
        this.y = y;
        this.z = z;
        this.originalSpeed = originalSpeed;
        this.moddedSpeed = newSpeed;

        this.harvestLevel = BlockHarvest.getHarvestLevel(block, metadata, harvester);
        this.silkTouch = EnchantmentHelper.getSilkTouchModifier(harvester);
        if (this.silkTouch) {
            this.fortune = 0;
        }
        else {
            this.fortune = EnchantmentHelper.getFortuneModifier(harvester);
        }
    }

    // Calculates the new break speed.
    public float getBreakSpeed() {
        float newBreakSpeed = Float.isNaN(this.newSpeedBase) ? this.moddedSpeed : this.newSpeedBase;
        newBreakSpeed = (newBreakSpeed + this.speedAdd) * this.speedMult;
        return newBreakSpeed;
    }
}