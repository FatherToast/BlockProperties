package toast.blockProperties;

import java.util.Random;
import java.util.UUID;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public abstract class EffectHelper {
    // Applies the enchantment to the itemStack at the given level. Called by all other enchantItem methods to do the actual enchanting.
    public static void enchantItem(ItemStack itemStack, int id, int level) {
        if (Enchantment.enchantmentsList[id] != null) {
            itemStack.addEnchantment(Enchantment.enchantmentsList[id], level);
        }
    }

    // Randomly enchants the itemStack based on the level (identical to using an enchantment table).
    public static void enchantItem(ItemStack itemStack, int level) {
        EffectHelper.enchantItem(_BlockPropertiesMod.random, itemStack, level);
    }

    public static void enchantItem(Random random, ItemStack itemStack, int level) {
        EnchantmentHelper.addRandomEnchantment(random, itemStack, level);
    }

    // Adds a line of text to the item stack's infobox.
    public static void addItemText(ItemStack itemStack, String text) {
        if (itemStack.stackTagCompound == null) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        if (!itemStack.stackTagCompound.hasKey("display")) {
            itemStack.stackTagCompound.setTag("display", new NBTTagCompound());
        }
        NBTTagCompound displayTag = itemStack.stackTagCompound.getCompoundTag("display");
        if (!displayTag.hasKey("Lore")) {
            displayTag.setTag("Lore", new NBTTagList());
        }
        NBTTagString stringTag = new NBTTagString(text);
        displayTag.getTagList("Lore", stringTag.getId()).appendTag(stringTag);
    }

    // Sets the item's color. No effect on most items.
    public static void dye(ItemStack itemStack, int color) {
        if (itemStack.stackTagCompound == null) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        if (!itemStack.stackTagCompound.hasKey("display")) {
            itemStack.stackTagCompound.setTag("display", new NBTTagCompound());
        }
        itemStack.stackTagCompound.getCompoundTag("display").setInteger("color", color);
    }

    // Adds a custom potion effect to the item stack.
    public static void addPotionEffect(ItemStack itemStack, int id, int duration, int amplifier, boolean ambient) {
        if (itemStack.stackTagCompound == null) {
            itemStack.stackTagCompound = new NBTTagCompound();
        }
        if (!itemStack.stackTagCompound.hasKey("CustomPotionEffects")) {
            itemStack.stackTagCompound.setTag("CustomPotionEffects", new NBTTagList());
        }
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByte("Id", (byte) id);
        tag.setInteger("Duration", duration);
        tag.setByte("Amplifier", (byte) amplifier);
        tag.setBoolean("Ambient", ambient);
        itemStack.stackTagCompound.getTagList("CustomPotionEffects", tag.getId()).appendTag(tag);
    }

    // Adds a custom attribute modifier to the item stack.
    public static void addModifier(ItemStack itemStack, String attribute, double value, int operation) {
        if (itemStack.stackTagCompound == null) {
            itemStack.stackTagCompound = new NBTTagCompound();
        }
        if (!itemStack.stackTagCompound.hasKey("AttributeModifiers")) {
            itemStack.stackTagCompound.setTag("AttributeModifiers", new NBTTagList());
        }
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("AttributeName", attribute);
        tag.setString("Name", "BlockProperties|" + Integer.toString(_BlockPropertiesMod.random.nextInt(), Character.MAX_RADIX));
        tag.setDouble("Amount", value);
        tag.setInteger("Operation", operation);
        UUID id = UUID.randomUUID();
        tag.setLong("UUIDMost", id.getMostSignificantBits());
        tag.setLong("UUIDLeast", id.getLeastSignificantBits());
        itemStack.stackTagCompound.getTagList("AttributeModifiers", tag.getId()).appendTag(tag);
    }
}