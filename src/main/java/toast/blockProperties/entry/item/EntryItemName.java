package toast.blockProperties.entry.item;

import java.util.Random;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import toast.blockProperties.FileHelper;
import toast.blockProperties.IPropertyReader;
import toast.blockProperties._BlockPropertiesMod;
import toast.blockProperties.entry.EntryAbstract;
import toast.blockProperties.entry.ItemStatsInfo;

import com.google.gson.JsonObject;

public class EntryItemName extends EntryAbstract {
    // The name to set.
    private final String name;

    public EntryItemName(String path, JsonObject root, int index, JsonObject node, IPropertyReader loader) {
        super(node, path);
        this.name = FileHelper.readText(node, path, "value", "");
    }

    // Returns an array of required field names.
    @Override
    public String[] getRequiredFields() {
        return new String[] { "value" };
    }

    // Returns an array of optional field names.
    @Override
    public String[] getOptionalFields() {
        return new String[] { };
    }

    // Modifies the item.
    @Override
    public void modifyItem(ItemStatsInfo itemStats) {
        String[] nameParts = this.name.split(Character.toString(FileHelper.CHAR_RAND), -1);
        if (nameParts.length == 1) {
            itemStats.theItem.setStackDisplayName(this.name);
        }
        else {
            String itemName = nameParts[0];
            for (int i = 1; i < nameParts.length; i++) {
                if (nameParts[i].length() > 0) {
                    char nextLetter = nameParts[i].charAt(0);
                    if (nextLetter == 'c') {
                        itemName += EntryItemName.getItemName(itemStats.theItem, itemStats.random);
                    }
                    else if (nextLetter == 'C') {
                        itemName += EntryItemName.getItemName(itemStats.theItem, itemStats.random); // TO DO
                    }
                    else if (nextLetter == 'i') {
                        itemName += EntryItemName.getItemInfo(itemStats.theItem, itemStats.random);
                    }
                    else if (nextLetter == 'p') {
                        itemName += EntryItemName.ITEM_PREFIXES[itemStats.random.nextInt(EntryItemName.ITEM_PREFIXES.length)];
                    }
                    else if (nextLetter == 'P') {
                        itemName += EntryItemName.ITEM_PREFIXES[itemStats.random.nextInt(EntryItemName.ITEM_PREFIXES.length)]; // TO DO
                    }
                    else if (nextLetter == 's') {
                        itemName += EntryItemName.ITEM_POSTFIXES[itemStats.random.nextInt(EntryItemName.ITEM_POSTFIXES.length)];
                    }
                    else if (nextLetter == 'S') {
                        itemName += EntryItemName.ITEM_POSTFIXES[itemStats.random.nextInt(EntryItemName.ITEM_POSTFIXES.length)]; // TO DO
                    }
                    else if (nextLetter == 'm') {
                        itemName += EntryItemName.buildName(itemStats.random);
                    }
                    else if (nextLetter == 'n') {
                        itemName += EntryItemName.NAMES[itemStats.random.nextInt(EntryItemName.NAMES.length)];
                    }
                    else {
                        itemName += EntryItemName.getItemName(itemStats.theItem, itemStats.random);
                    }
                    itemName += nameParts[i].substring(1);
                }
                else {
                    itemName += EntryItemName.getItemName(itemStats.theItem, itemStats.random);
                }
            }
            itemStats.theItem.setStackDisplayName(itemName);
        }
    }

    // Returns a mash name.
    public static String buildName(Random random) {
        String name = EntryItemName.NAME_PARTS[random.nextInt(EntryItemName.NAME_PARTS.length)] + EntryItemName.NAME_PARTS[random.nextInt(EntryItemName.NAME_PARTS.length)].toLowerCase();
        if (random.nextInt(2) == 0) {
            name += EntryItemName.NAME_PARTS[random.nextInt(EntryItemName.NAME_PARTS.length)].toLowerCase();
        }
        return name;
    }

    // Generates a full random name.
    public static String getEntityName(Random random) {
        String name = "";
        if (random.nextInt(4) != 0) {
            name = EntryItemName.getPrename(random);
            if (random.nextInt(4) != 0) {
                name = EntryItemName.getSurname(random);
            }
        }
        else if (random.nextInt(4) != 0) {
            name = EntryItemName.getSurnameNoDescriptors(random);
        }
        else if (random.nextInt(200) != 0) {
            name = EntryItemName.getPrename(random);
        }
        else {
            name = "\00a7lThe Almighty Lord of the Chickens";
        }
        if (random.nextInt(5) == 0) {
            name = EntryItemName.SALUTATIONS[random.nextInt(EntryItemName.SALUTATIONS.length)] + " " + name;
        }
        return name;
    }

    // Generates a random first name.
    public static String getPrename(Random random) {
        return random.nextInt(2) == 0 ? EntryItemName.NAMES[random.nextInt(EntryItemName.NAMES.length)] : EntryItemName.buildName(random);
    }

    // Generates a random last name.
    public static String getSurname(Random random) {
        return random.nextInt(2) == 0 ? EntryItemName.getSurnameNoDescriptors(random) : "the " + EntryItemName.DESCRIPTORS[random.nextInt(EntryItemName.DESCRIPTORS.length)];
    }

    // Generates a random last name.
    public static String getSurnameNoDescriptors(Random random) {
        String name = "";
        if (random.nextInt(10) == 0) {
            if (random.nextInt(2) == 0) {
                name += "Mac";
            }
            else {
                name += "Mc";
            }
        }
        return name + EntryItemName.buildName(random);
    }

    // Generates a full random item name.
    public static String getItemName(ItemStack itemStack, Random random) {
        String name = "";
        boolean prefixed = false;
        if (random.nextInt(2) == 0) {
            prefixed = true;
            name += EntryItemName.ITEM_PREFIXES[random.nextInt(EntryItemName.ITEM_PREFIXES.length)] + " ";
        }

        name += EntryItemName.getItemInfo(itemStack, random);

        if (!prefixed) {
            name += " of " + EntryItemName.ITEM_POSTFIXES[random.nextInt(EntryItemName.ITEM_POSTFIXES.length)];
        }
        return name;
    }

    // Generates a random item name.
    public static String getItemInfo(ItemStack itemStack, Random random) {
        String name = "";

        String material = null;
        if (itemStack.getItem() instanceof ItemSword) {
            material = ((ItemSword) itemStack.getItem()).getToolMaterialName();
        }
        else if (itemStack.getItem() instanceof ItemTool) {
            material = ((ItemTool) itemStack.getItem()).getToolMaterialName();
        }
        if (material != null) {
            String[][] materials = { { "Wooden", "Wood", "Hardwood", "Balsa Wood", "Mahogany", "Plywood" }, { "Stone", "Rock", "Marble", "Cobblestone", }, { "Iron", "Steel", "Ferrous", "Rusty", "Wrought Iron" }, { "Diamond", "Zircon", "Gemstone", "Jewel", "Crystal" }, { "Golden", "Gold", "Gilt", "Auric", "Ornate" } };
            int index = -1;
            if (material.equals(Item.ToolMaterial.WOOD.toString())) {
                index = 0;
            }
            else if (material.equals(Item.ToolMaterial.STONE.toString())) {
                index = 1;
            }
            else if (material.equals(Item.ToolMaterial.IRON.toString())) {
                index = 2;
            }
            else if (material.equals(Item.ToolMaterial.EMERALD.toString())) {
                index = 3;
            }
            else if (material.equals(Item.ToolMaterial.GOLD.toString())) {
                index = 4;
            }
            if (index < 0) {
                name += _BlockPropertiesMod.cap(material.toLowerCase()) + " ";
            }
            else {
                name += materials[index][random.nextInt(materials[index].length)] + " ";
            }

            String[] type = { "Tool" };
            if (itemStack.getItem() instanceof ItemSword) {
                type = new String[] { "Sword", "Cutter", "Slicer", "Dicer", "Knife", "Blade", "Machete", "Brand", "Claymore", "Cutlass", "Foil", "Dagger", "Glaive", "Rapier", "Saber", "Scimitar", "Shortsword", "Longsword", "Broadsword", "Calibur" };
            }
            else if (itemStack.getItem() instanceof ItemAxe) {
                type = new String[] { "Axe", "Chopper", "Hatchet", "Tomahawk", "Cleaver", "Hacker", "Tree-Cutter", "Truncator" };
            }
            else if (itemStack.getItem() instanceof ItemPickaxe) {
                type = new String[] { "Pickaxe", "Pick", "Mattock", "Rock-Smasher", "Miner" };
            }
            else if (itemStack.getItem() instanceof ItemSpade) {
                type = new String[] { "Shovel", "Spade", "Digger", "Excavator", "Trowel", "Scoop" };
            }
            name += type[random.nextInt(type.length)];
        }
        else if (itemStack.getItem() instanceof ItemBow) {
            String[] type = { "Bow", "Shortbow", "Longbow", "Flatbow", "Recurve Bow", "Reflex Bow", "Self Bow", "Composite Bow", "Arrow-Flinger" };
            name += type[random.nextInt(type.length)];
        }
        else if (itemStack.getItem() instanceof ItemArmor) {
            String[][] materials = { { "Leather", "Rawhide", "Lamellar", "Cow Skin" }, { "Chainmail", "Chain", "Chain Link", "Scale" }, { "Iron", "Steel", "Ferrous", "Rusty", "Wrought Iron" }, { "Diamond", "Zircon", "Gemstone", "Jewel", "Crystal" }, { "Golden", "Gold", "Gilt", "Auric", "Ornate" } };
            material = ((ItemArmor) itemStack.getItem()).getArmorMaterial().toString();
            int index = -1;
            if (material.equals(ItemArmor.ArmorMaterial.CLOTH.toString())) {
                index = 0;
            }
            else if (material.equals(ItemArmor.ArmorMaterial.CHAIN.toString())) {
                index = 1;
            }
            else if (material.equals(ItemArmor.ArmorMaterial.IRON.toString())) {
                index = 2;
            }
            else if (material.equals(ItemArmor.ArmorMaterial.DIAMOND.toString())) {
                index = 3;
            }
            else if (material.equals(ItemArmor.ArmorMaterial.GOLD.toString())) {
                index = 4;
            }
            if (index < 0) {
                name += _BlockPropertiesMod.cap(material.toLowerCase()) + " ";
            }
            else {
                name += materials[index][random.nextInt(materials[index].length)] + " ";
            }

            String[] type = { "Armor" };
            switch ( ((ItemArmor) itemStack.getItem()).armorType) {
                case 0:
                    type = new String[] { "Helmet", "Cap", "Crown", "Great Helm", "Bassinet", "Sallet", "Close Helm", "Barbute" };
                    break;
                case 1:
                    type = new String[] { "Chestplate", "Tunic", "Brigandine", "Hauberk", "Cuirass" };
                    break;
                case 2:
                    type = new String[] { "Leggings", "Pants", "Tassets", "Cuisses", "Schynbalds" };
                    break;
                case 3:
                    type = new String[] { "Boots", "Shoes", "Greaves", "Sabatons", "Sollerets" };
                    break;
            }
            name += type[random.nextInt(type.length)];
        }
        else {
            name += itemStack.getItem().getItemStackDisplayName(itemStack);
        }

        return name;
    }

    // List of all possible item prefixes.
    public static final String[] ITEM_PREFIXES = { "Mighty", "Supreme", "Superior", "Ultimate", "Shoddy", "Flimsy", "Curious", "Secret", "Pathetic", "Crying", "Eagle's", "Errant", "Unholy", "Questionable", "Mean", "Hungry", "Thirsty", "Feeble", "Wise", "Sage's", "Magical", "Mythical", "Legendary", "Not Very Nice", "Jerk's", "Doctor's", "Misunderstood", "Angry", "Knight's", "Bishop's", "Godly", "Special", "Toasty", "Shiny", "Shimmering", "Light", "Dark", "Odd-Smelling", "Funky", "Slightly Overdone", "Half-Baked", "Cracked", "Sticky", "\u00a7kAlien", "Baby", "Manly", "Rough", "Scary", "Undoubtable", "Honest", "Non-Suspicious", "Boring", "Odd", "Lazy", "Super", "Nifty", "Ogre-Slaying" };
    // List of all possible item postfixes.
    public static final String[] ITEM_POSTFIXES = { "Mightiness", "Supremity", "Superiority", "Flimsiness", "Curiousity", "Secrets", "Patheticness", "Crying", "The Eagles", "Unholiness", "Questionable Integrity", "Meanness", "Hunger", "Thirst", "Wisdom", "The Sages", "Magic", "Myths", "Legends", "The Jerks", "The Doctor", "Misunderstanding", "Anger", "The Gods", "Toast", "Shininess", "Shimmering", "The Light", "Darkness", "Strange Odors", "Funk", "Slight Abnormality", "Baking", "Breaking", "Stickiness", "Babies", "Manliness", "Roughness", "Scary Stuff", "Doubt", "Honesty", "Nothing", "Boringness", "Oddness", "Laziness", "Super Something", "Nifty Things", "Ogre-Slaying" };

    // List of all possible full names.
    public static final String[] NAMES = { "Albert", "Andrew", "Anderson", "Andy", "Allan", "Arthur", "Aaron", "Allison", "Arielle", "Amanda", "Anne", "Annie", "Amy", "Alana", "Brandon", "Brady", "Bernard", "Ben", "Benjamin", "Bob", "Bobette", "Brooke", "Brandy", "Beatrice", "Bea", "Bella", "Becky", "Carlton", "Carl", "Calvin", "Cameron", "Carson", "Chase", "Cassandra", "Cassie", "Cas", "Carol", "Carly", "Cherise", "Charlotte", "Cheryl", "Chasity", "Danny", "Drake", "Daniel", "Derrel", "David", "Dave", "Donovan", "Don", "Donald", "Drew", "Derrick", "Darla", "Donna", "Dora", "Danielle", "Edward", "Elliot", "Ed", "Edson", "Elton", "Eddison", "Earl", "Eric", "Ericson", "Eddie", "Ediovany", "Emma", "Elizabeth", "Eliza", "Esperanza", "Esper", "Esmeralda", "Emi", "Emily", "Elaine", "Fernando", "Ferdinand", "Fred", "Feddie", "Fredward", "Frank", "Franklin", "Felix", "Felicia", "Fran", "Greg", "Gregory", "George", "Gerald", "Gina", "Geraldine", "Gabby", "Hendrix", "Henry", "Hobbes", "Herbert", "Heath", "Henderson", "Helga", "Hera", "Helen", "Helena", "Hannah", "Ike", "Issac", "Israel", "Ismael", "Irlanda", "Isabelle", "Irene", "Irenia", "Jimmy", "Jim", "Justin", "Jacob", "Jake", "Jon", "Johnson", "Jonny", "Jonathan", "Josh", "Joshua", "Julian", "Jesus", "Jericho", "Jeb", "Jess", "Joan", "Jill", "Jillian", "Jessica", "Jennifer", "Jenny", "Jen", "Judy", "Kenneth", "Kenny", "Ken", "Keith", "Kevin", "Karen", "Kassandra", "Kassie", "Leonard", "Leo", "Leroy", "Lee", "Lenny", "Luke", "Lucas", "Liam", "Lorraine", "Latasha", "Lauren", "Laquisha", "Livia", "Lydia", "Lila", "Lilly", "Lillian", "Lilith", "Lana", "Mason", "Mike", "Mickey", "Mario", "Manny", "Mark", "Marcus", "Martin", "Marty", "Matthew", "Matt", "Max", "Maximillian", "Marth", "Mia", "Marriah", "Maddison", "Maddie", "Marissa", "Miranda", "Mary", "Martha", "Melonie", "Melody", "Mel", "Minnie", "Nathan", "Nathaniel", "Nate", "Ned", "Nick", "Norman", "Nicholas", "Natasha", "Nicki", "Nora", "Nelly", "Nina", "Orville", "Oliver", "Orlando", "Owen", "Olsen", "Odin", "Olaf", "Ortega", "Olivia", "Patrick", "Pat", "Paul", "Perry", "Pinnochio", "Patrice", "Patricia", "Pennie", "Petunia", "Patti", "Pernelle", "Quade", "Quincy", "Quentin", "Quinn", "Roberto", "Robbie", "Rob", "Robert", "Roy", "Roland", "Ronald", "Richard", "Rick", "Ricky", "Rose", "Rosa", "Rhonda", "Rebecca", "Roberta", "Sparky", "Shiloh", "Stephen", "Steve", "Saul", "Sheen", "Shane", "Sean", "Sampson", "Samuel", "Sammy", "Stefan", "Sasha", "Sam", "Susan", "Suzy", "Shelby", "Samantha", "Sheila", "Sharon", "Sally", "Stephanie", "Sandra", "Sandy", "Sage", "Tim", "Thomas", "Thompson", "Tyson", "Tyler", "Tom", "Tyrone", "Timmothy", "Tamara", "Tabby", "Tabitha", "Tessa", "Tiara", "Tyra", "Uriel", "Ursala", "Uma", "Victor", "Vincent", "Vince", "Vance", "Vinny", "Velma", "Victoria", "Veronica", "Wilson", "Wally", "Wallace", "Will", "Wilard", "William", "Wilhelm", "Xavier", "Xandra", "Young", "Yvonne", "Yolanda", "Zach", "Zachary" };
    // List of all name parts.
    public static final String[] NAME_PARTS = { "Grab", "Thar", "Ger", "Ald", "Mas", "On", "O", "Din", "Thor", "Jon", "Ath", "Burb", "En", "A", "E", "I", "U", "Hab", "Bloo", "Ena", "Dit", "Aph", "Ern", "Bor", "Dav", "Id", "Toast", "Son", "Dottir", "For", "Wen", "Lob", "Ed", "Die", "Van", "Y", "Zap", "Ear", "Ben", "Don", "Bran", "Gro", "Jen", "Bob", "Ette", "Ere", "Man", "Qua", "Bro", "Cree", "Per", "Skel", "Ton", "Zom", "Bie", "Wolf", "End", "Er", "Pig", "Sil", "Ver", "Fish", "Cow", "Chic", "Ken", "Sheep", "Squid", "Hell" };
    // List of salutations.
    public static final String[] SALUTATIONS = { "Mr.", "Mister", "Sir", "Mrs.", "Miss", "Madam", "Dr.", "Doctor", "Lord", "Father", "Grandfather", "Mother", "Grandmother" };
    // List of all mob descriptors.
    public static final String[] DESCRIPTORS = { "Mighty", "Supreme", "Superior", "Ultimate", "Lame", "Wimpy", "Curious", "Sneaky", "Pathetic", "Crying", "Eagle", "Errant", "Unholy", "Questionable", "Mean", "Hungry", "Thirsty", "Feeble", "Wise", "Sage", "Magical", "Mythical", "Legendary", "Not Very Nice", "Jerk", "Doctor", "Misunderstood", "Angry", "Knight", "Bishop", "Godly", "Special", "Toasty", "Shiny", "Shimmering", "Light", "Dark", "Odd-Smelling", "Funky", "Rock Smasher", "Son of Herobrine", "Cracked", "Sticky", "\u00a7kAlien", "Baby", "Manly", "Rough", "Scary", "Undoubtable", "Honest", "Non-Suspicious", "Boring", "Odd", "Lazy", "Super", "Nifty", "Ogre Slayer", "Pig Thief", "Dirt Digger", "Really Cool", "Doominator", "... Something" };
}