package toast.blockProperties.entry;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import toast.blockProperties.BlockCommandSender;
import toast.blockProperties.BlockPropertyException;
import toast.blockProperties.FileHelper;
import toast.blockProperties.IPropertyReader;

import com.google.gson.JsonObject;

public class EntryCommand extends EntryAbstract {

    // The min and max number of times to perform the task.
    private final double[] counts;
    // The name of the external function to use.
    private final String command;
    // If true, the command's output is suppressed.
    private final boolean noOutput;

    public EntryCommand(String path, JsonObject root, int index, JsonObject node, IPropertyReader loader) {
        super(node, path);
        this.counts = FileHelper.readCounts(node, path, "count", 1.0, 1.0);

        this.command = FileHelper.readText(node, path, "value", "");
        if (this.command == "")
            throw new BlockPropertyException("Missing or invalid command!", path);

        this.noOutput = FileHelper.readBoolean(node, path, "suppress_output", true);
    }

    // Returns an array of required field names.
    @Override
    public String[] getRequiredFields() {
        return new String[] { "value" };
    }

    // Returns an array of optional field names.
    @Override
    public String[] getOptionalFields() {
        return new String[] { "count", "suppress_output" };
    }

    // Modifies the break speed.
    @Override
    public void modifyBreakSpeed(BreakSpeedInfo blockBreakSpeed) {
        MinecraftServer server = MinecraftServer.getServer();
        if (server != null) {
            ICommandManager commandManager = server.getCommandManager();

            ServerCommandManager admin = null; // Used for silencing commands
            if (this.noOutput && commandManager instanceof ServerCommandManager) {
				admin = (ServerCommandManager) commandManager;
				CommandBase.setAdminCommander(null);
			}

        	BlockCommandSender commandSender = new BlockCommandSender(blockBreakSpeed.harvester.worldObj, blockBreakSpeed.theBlock, blockBreakSpeed.x, blockBreakSpeed.y, blockBreakSpeed.z);
        	for (int count = FileHelper.getCount(this.counts); count-- > 0;) {
                commandManager.executeCommand(commandSender, this.command);
        	}

        	if (admin != null) { // Reapply command admin
				CommandBase.setAdminCommander(admin);
			}
        }
    }

    // Modifies the experience drop.
    @Override
    public void modifyXP(BlockXPInfo blockXP) {
        MinecraftServer server = MinecraftServer.getServer();
        if (server != null) {
            ICommandManager commandManager = server.getCommandManager();

            ServerCommandManager admin = null; // Used for silencing commands
            if (this.noOutput && commandManager instanceof ServerCommandManager) {
				admin = (ServerCommandManager) commandManager;
				CommandBase.setAdminCommander(null);
			}

        	BlockCommandSender commandSender = new BlockCommandSender(blockXP.theWorld, blockXP.theBlock, blockXP.x, blockXP.y, blockXP.z);
        	for (int count = FileHelper.getCount(this.counts); count-- > 0;) {
                commandManager.executeCommand(commandSender, this.command);
        	}

        	if (admin != null) { // Reapply command admin
				CommandBase.setAdminCommander(admin);
			}
        }
    }

    // Modifies the list of drops.
    @Override
    public void modifyDrops(BlockDropsInfo blockDrops) {
        MinecraftServer server = MinecraftServer.getServer();
        if (server != null) {
            ICommandManager commandManager = server.getCommandManager();

            ServerCommandManager admin = null; // Used for silencing commands
            if (this.noOutput && commandManager instanceof ServerCommandManager) {
				admin = (ServerCommandManager) commandManager;
				CommandBase.setAdminCommander(null);
			}

        	BlockCommandSender commandSender = new BlockCommandSender(blockDrops.theWorld, blockDrops.theBlock, blockDrops.x, blockDrops.y, blockDrops.z);
        	for (int count = FileHelper.getCount(this.counts); count-- > 0;) {
                commandManager.executeCommand(commandSender, this.command);
        	}

        	if (admin != null) { // Reapply command admin
				CommandBase.setAdminCommander(admin);
			}
        }
    }
}
