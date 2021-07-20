package net.kunmc.lab.gravitymod_dga;

import net.kunmc.lab.gravitymod_dga.data.GravityGameInstance;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandGameStart extends CommandBase {
    @Override
    public String getName() {
        return "gravity";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.gravity.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        switch (args.length) {
            case 0:
                throw new WrongUsageException("commands.gravity.usage");
            case 1:
                if ("stop".equals(args[0]))
                    setGameState(false);
                else
                    throw new WrongUsageException("commands.gravity.usage");
                break;
        }
        if ("start".equals(args[0]) && args.length >= 2) {
            int i = 1;
            Vec3d vec3d = sender.getPositionVector();
            double x = parseDouble(vec3d.x, args[i++], true);
            double y = args.length > i ? parseDouble(vec3d.y, args[i++], 0, 0, false) : vec3d.y;
            double z = args.length > i ? parseDouble(vec3d.z, args[i], true) : vec3d.z;
            if ("start".equals(args[0])) {
                int cont = args.length > 4 ? parseInt(args[4], 3, 100) : 25;
                Block block = args.length > 5 ? CommandBase.getBlockByText(sender, args[5]) : Blocks.STONE;
                IBlockState state = args.length > 6 ? convertArgToBlockState(block, args[6]) : block.getDefaultState();
                int speed = args.length > 7 ? parseInt(args[7], 1) : 10;
                boolean dismemberedGravity = args.length > 8 && parseBoolean(args[8]);
                for (int j = -cont; j < cont; j++) {
                    for (int k = -cont; k < cont; k++) {
                        sender.getEntityWorld().setBlockState(new BlockPos(x + j, y + k, z - cont), state);
                        sender.getEntityWorld().setBlockState(new BlockPos(x + j, y + k, z + cont), state);
                        sender.getEntityWorld().setBlockState(new BlockPos(x + k, y - cont, z + j), state);
                        sender.getEntityWorld().setBlockState(new BlockPos(x + k, y + cont, z + j), state);
                        sender.getEntityWorld().setBlockState(new BlockPos(x - cont, y + k, z + j), state);
                        sender.getEntityWorld().setBlockState(new BlockPos(x + cont, y + k, z + j), state);
                    }
                }

                setGameState(true, x, y, z, speed, sender.getCommandSenderEntity().dimension, cont, dismemberedGravity);
            }
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "start", "stop");
        } else if ("start".equals(args[0]) && args.length <= 4) {
            return getTabCompletionCoordinate(args, 1, targetPos);
        } else if ("start".equals(args[0]) && args.length == 6) {
            return getListOfStringsMatchingLastWord(args, Block.REGISTRY.getKeys());
        }else if ("start".equals(args[0]) && args.length ==9) {
            return getListOfStringsMatchingLastWord(args, "true","false");
        }
        return Collections.emptyList();
    }

    private void setGameState(boolean start) throws CommandException {
        setGameState(start, 0, 0, 0, 0, 0, 0, false);
    }

    private void setGameState(boolean start, double x, double y, double z, int speed, int dimension, int size, boolean dismemberedGravity) throws CommandException {
        GravityGameInstance gameInstance = GravityGameInstance.getInstance();
        if (start) {
            if (gameInstance.isRunning())
                throw new CommandException("commands.gravity.alreadystarted");
            gameInstance.start(x, y, z, speed, dimension, size, dismemberedGravity);
        } else {
            if (!gameInstance.isRunning())
                throw new CommandException("commands.gravity.nostarted");
            gameInstance.stop();
        }
    }

}
