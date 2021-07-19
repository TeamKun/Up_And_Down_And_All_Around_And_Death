package net.kunmc.lab.gravitymod_dga;

import net.kunmc.lab.gravitymod_dga.data.GravityGameInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameType;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ServerHandler {
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent e) {
        if (e.side == Side.SERVER)
            GravityGameInstance.getInstance().tick();
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent e) {
        GravityGameInstance gameInstance = GravityGameInstance.getInstance();
        if (!e.getEntityLiving().world.isRemote && e.getEntityLiving() instanceof EntityPlayerMP && gameInstance.isRunning()) {

            if (gameInstance.getPlayers().contains(((EntityPlayerMP) e.getEntityLiving()).getGameProfile().getId()))
                ((EntityPlayerMP) e.getEntityLiving()).setGameType(GameType.SPECTATOR);

            gameInstance.removePlayer(((EntityPlayerMP) e.getEntityLiving()).getGameProfile().getId());
        }
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent e) {
        GravityGameInstance gameInstance = GravityGameInstance.getInstance();
        if (!e.getEntityLiving().world.isRemote && e.getEntityLiving() instanceof EntityPlayerMP && gameInstance.isRunning() && gameInstance.isWait()) {
            if (gameInstance.getPlayers().contains(((EntityPlayerMP) e.getEntityLiving()).getGameProfile().getId()))
                e.setCanceled(true);
        }
    }
}
