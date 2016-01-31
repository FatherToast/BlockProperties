package toast.blockProperties;

import java.io.File;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import toast.blockProperties.entry.BlockDropsInfo;
import toast.blockProperties.entry.BlockXPInfo;
import toast.blockProperties.entry.BreakSpeedInfo;
import toast.blockProperties.entry.HarvestingInfo;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {
    // Properties stored for easy access.
    public static final boolean DISABLED = Properties.getBoolean(Properties.GENERAL, "disable");

    public EventHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Called by EntityPlayer.canHarvestBlock().
     * EntityPlayer entityPlayer = the player breaking the block.
     * Block block = the block being broken.
     * boolean success = true if the block can be harvested.
     *
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        BlockProperties blockProps = BlockProperties.getProperties(event.block);
        if (blockProps != null) {
            HarvestingInfo harvest = new HarvestingInfo(event.entityPlayer, event.block, event.success);
            blockProps.modifyHarvest(harvest);
            event.success = harvest.newSuccess;
        }
    }

    /**
     * Called by EntityPlayer.canHarvestBlock().
     * EntityPlayer entityPlayer = the player breaking the block.
     * Block block = the block being broken.
     * int metadata = the metadata of the block being broken.
     * float originalSpeed = the normal break speed.
     * float newSpeed = the new break speed to use.
     * int x, y, z = the coordinates of the block. y is -1 if location is unknown.
     *
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        BlockProperties blockProps = BlockProperties.getProperties(event.block);
        if (blockProps != null) {
            BreakSpeedInfo breakSpeed = new BreakSpeedInfo(event.entityPlayer, event.block, event.metadata, event.originalSpeed, event.newSpeed, event.x, event.y, event.z);
            blockProps.modifyBreakSpeed(breakSpeed);
            event.newSpeed = breakSpeed.getBreakSpeed();
        }
    }

    /**
     * Called by ItemInWorldManager.tryHarvestBlock().
     * World world = the world the event is in.
     * Block block = the block being broken.
     * int blockMetadata = the metadata of the block being broken.
     * int x, y, z = the coordinates of the block.
     * EntityPlayer getPlayer() = the player breaking the block.
     * int get/setExpToDrop() = the amount of experience to drop, if successful.
     *
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        BlockProperties blockProps = BlockProperties.getProperties(event.block);
        if (blockProps != null) {
            BlockXPInfo blockXP = new BlockXPInfo(event.world, event.block, event.blockMetadata, event.x, event.y, event.z, event.getPlayer(), event.getExpToDrop());
            blockProps.modifyXP(blockXP);
            event.setExpToDrop(blockXP.getXP());
        }
    }

    /**
     * Called by Block.dropBlockAsItemWithChance().
     * World world = the world the event is in.
     * Block block = the block being broken.
     * int blockMetadata = the metadata of the block being broken.
     * int x, y, z = the coordinates of the block.
     * int fortuneLevel = the harvester's fortune level.
     * ArrayList<ItemStack> drops = the items being dropped.
     * boolean isSilkTouching = true if silk touch is being used.
     * float dropChance = the chance for each item in the list to be dropped, not always used.
     * EntityPlayer harvester = the player harvesting the block, may be null.
     *
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBlockHarvestDrops(BlockEvent.HarvestDropsEvent event) {
        BlockProperties blockProps = BlockProperties.getProperties(event.block);
        if (blockProps != null) {
            BlockDropsInfo drops = new BlockDropsInfo(event.world, event.block, event.blockMetadata, event.x, event.y, event.z, event.fortuneLevel, event.drops, event.isSilkTouching, event.dropChance, event.harvester);
            blockProps.modifyDrops(drops);
            drops.applyDefaultAndAddDrops();
            event.dropChance = drops.newDropChance;
        }
    }

    /**
     * Called by EntityPlayer.interactWith().
     * EntityPlayer entityPlayer = the player interacting.
     * Entity target = the entity being right clicked.
     *
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityInteract(EntityInteractEvent event) {
    	if (event.target != null && event.entityPlayer != null && !event.entityPlayer.worldObj.isRemote) {
    		ItemStack held = event.entityPlayer.getHeldItem();
    		if (held != null && held.getItem() == Items.stick && held.stackTagCompound != null && held.stackTagCompound.getBoolean("BP|InfoWand")) {
    			// Force position tag to be [0,0,0]
    			float offset = event.target.ySize;
    			double x = event.target.posX;
    			double y = event.target.posY;
    			double z = event.target.posZ;
    			event.target.ySize = 0.0F;
    			event.target.posX = event.target.posY = event.target.posZ = 0.0;

    			NBTTagCompound tag = new NBTTagCompound();
    			event.target.writeToNBT(tag);

    			// Remove dangerous tags and restore position
    			tag.removeTag("Dimension");
    			tag.removeTag("UUIDMost");
    			tag.removeTag("UUIDLeast");
    			event.target.ySize = offset;
    			event.target.posX = x;
    			event.target.posY = y;
    			event.target.posZ = z;

    			File generated = FileHelper.generateNbtStats(event.target.getCommandSenderName(), tag);
    			if (generated != null) {
    				event.entityPlayer.addChatMessage(new ChatComponentText("[Info Wand] Generated external nbt stats file \"" + generated.getName().substring(0, generated.getName().length() - FileHelper.FILE_EXT.length()) + "\" at:"));
    				event.entityPlayer.addChatMessage(new ChatComponentText("    " + generated.getAbsolutePath()));
    			}
    			else {
					event.entityPlayer.addChatMessage(new ChatComponentText("[Info Wand] Failed to generate external nbt stats file!"));
				}
    		}
    	}
    }

    /**
     * Called by a number of methods.
     * EntityPlayer entityPlayer = the player interacting.
     * PlayerInteractEvent.Action action = the action being taken.
     * int x = the x-coord of the block being interacted with, if any.
     * int y = the y-coord of the block being interacted with, if any.
     * int z = the z-coord of the block being interacted with, if any.
     * int face = the side of the block being interacted with, if any.
     * World world = the world being interacted with.
     * Result useBlock = result for using the targeted block.
     * Result useItem = result for using the held item.
     *
     * @param event The event being triggered.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
    	if (event.world != null && event.y >= 0 && event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && event.entityPlayer != null && !event.entityPlayer.worldObj.isRemote) {
    		ItemStack held = event.entityPlayer.getHeldItem();
    		if (held != null && held.getItem() == Items.stick && held.stackTagCompound != null && held.stackTagCompound.getBoolean("BP|InfoWand")) {
    			TileEntity tileEntity = event.world.getTileEntity(event.x, event.y, event.z);
    			if (tileEntity == null)
    				return;

    			NBTTagCompound tag = new NBTTagCompound();
    			tileEntity.writeToNBT(tag);
    			String name = tag.getString("id");

    			// Remove dangerous tags
    			tag.removeTag("id");
    			tag.removeTag("x");
    			tag.removeTag("y");
    			tag.removeTag("z");

    			if (tag.hasNoTags()) {
					event.entityPlayer.addChatMessage(new ChatComponentText("[Info Wand] Tile entity has no usable tags!"));
					return;
    			}

    			File generated = FileHelper.generateNbtStats(name, tag);
    			if (generated != null) {
    				event.entityPlayer.addChatMessage(new ChatComponentText("[Info Wand] Generated external nbt stats file \"" + generated.getName().substring(0, generated.getName().length() - FileHelper.FILE_EXT.length()) + "\" at:"));
    				event.entityPlayer.addChatMessage(new ChatComponentText("    " + generated.getAbsolutePath()));
    			}
    			else {
					event.entityPlayer.addChatMessage(new ChatComponentText("[Info Wand] Failed to generate external nbt stats file!"));
				}
    		}
    	}
    }
}