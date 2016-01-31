package toast.blockProperties;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class BlockCommandSender implements ICommandSender {

    // The world this is in.
    public final World theWorld;
	// The block sending commands.
	public final Block commandSender;
    // The block's coordinates.
    public final int x, y, z;

	public BlockCommandSender(World world, Block block, int x, int y, int z) {
        this.theWorld = world;
		this.commandSender = block;
        this.x = x;
        this.y = y;
        this.z = z;
	}

	@Override
	public String getCommandSenderName() {
		return this.commandSender.getUnlocalizedName();
	}
	// Gets the name of the command sender as a chat component.
	@Override
	public IChatComponent func_145748_c_() {
		return new ChatComponentText(this.commandSender.getLocalizedName());
	}

	@Override
	public void addChatMessage(IChatComponent message) {
		// Do nothing
	}

	@Override
	public boolean canCommandSenderUseCommand(int permissionLevel, String commandName) {
		return true;
	}

	@Override
	public ChunkCoordinates getPlayerCoordinates() {
		return new ChunkCoordinates(this.x, this.y, this.z);
	}
	@Override
	public World getEntityWorld() {
		return this.theWorld;
	}

}
