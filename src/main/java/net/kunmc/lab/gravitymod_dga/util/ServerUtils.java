package net.kunmc.lab.gravitymod_dga.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.List;
import java.util.UUID;

public class ServerUtils {
    public static MinecraftServer getServer() {
        return FMLCommonHandler.instance().getMinecraftServerInstance();
    }

    public static List<EntityPlayerMP> getPlayers() {
        return getServer().getPlayerList().getPlayers();
    }

    public static boolean isOnlinePlayer(UUID uuid) {
        List<EntityPlayerMP> playes = getPlayers();
        return playes.stream().anyMatch(n -> n.getGameProfile().getId().equals(uuid));
    }
}
