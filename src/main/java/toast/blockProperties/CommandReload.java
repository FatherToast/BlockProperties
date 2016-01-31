package toast.blockProperties;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import toast.blockProperties.entry.PropertyExternal;
import toast.blockProperties.entry.drops.EntryDropsSchematic;

public class CommandReload extends CommandBase {
    // Returns true if the given command sender is allowed to use this command.
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return MinecraftServer.getServer().isSinglePlayer() || super.canCommandSenderUseCommand(sender);
    }

    // The command name.
    @Override
    public String getCommandName() {
        return "bpreload";
    }

    // Returns the help string.
    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/bpreload - reloads all block properties.";
    }

    // Executes the command.
    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        sender.addChatMessage(new ChatComponentText("Reloading block properties!"));

        _BlockPropertiesMod.console("Reloading block properties...");
        BlockProperties.unload();
        PropertyExternal.unload();
        EntryDropsSchematic.unload();
        _BlockPropertiesMod.console("Loaded " + FileHelper.load() + " block properties!");
        if (!EventHandler.DISABLED && Properties.getBoolean(Properties.GENERAL, "auto_generate_files")) {
            _BlockPropertiesMod.console("Generating default block properties...");
            _BlockPropertiesMod.console("Generated " + FileHelper.generateDefaults() + " block properties!");
        }
        BlockProperties.init();
    }
}