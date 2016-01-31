package toast.blockProperties.entry;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import toast.blockProperties.BlockHarvest;

public class BlockXPInfo {
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
    // The player harvesting the block. May be null.
    public final EntityPlayer harvester;
    // The block's base XP.
    public final int xpBase;

    // The harvester's mining level on the block.
    public final int harvestLevel;
    // The harvester's fortune level.
    public final int fortune;
    // True if the harvester has silk touch.
    public final boolean silkTouch;

    // The block's new base XP. -1 if not used.
    public int newXpBase = -1;
    // The block's additional XP.
    public int xpAdd = 0;
    // The block's XP multiplier.
    public double xpMult = 1.0;

    public BlockXPInfo(World world, Block block, int blockMetadata, int x, int y, int z, EntityPlayer harvester, int xp) {
        this.theWorld = world;
        this.random = world.rand;
        this.theBlock = block;
        this.metadata = blockMetadata;
        this.x = x;
        this.y = y;
        this.z = z;
        this.harvester = harvester;
        this.xpBase = xp;

        this.harvestLevel = BlockHarvest.getHarvestLevel(block, blockMetadata, harvester);
        this.silkTouch = EnchantmentHelper.getSilkTouchModifier(harvester);
        if (this.silkTouch) {
            this.fortune = 0;
        }
        else {
            this.fortune = EnchantmentHelper.getFortuneModifier(harvester);
        }
    }

    // Calculates the new XP to drop.
    public int getXP() {
        int newXP = this.newXpBase >= 0 ? this.newXpBase : this.xpBase;
        newXP = (int) Math.round((newXP + this.xpAdd) * this.xpMult);
        return newXP;
    }
}