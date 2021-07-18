package net.kunmc.lab.gravitymod_dga;

import net.kunmc.lab.gravitymod_dga.data.GravityGameInstance;
import net.minecraft.command.CommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = GravityModAddon.MODID, name = GravityModAddon.NAME, version = GravityModAddon.VERSION)
public class GravityModAddon {
    public static final String MODID = "gravitymod_deathgameaddon";
    public static final String NAME = "Up And Down And All Around And Death";
    public static final String VERSION = "1.0";
    private static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        MinecraftForge.EVENT_BUS.register(ServerHandler.class);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        ((CommandHandler) event.getServer().getCommandManager()).registerCommand(new CommandGameStart());
    }

    @Mod.EventHandler
    public void serverStop(FMLServerStoppingEvent event) {
        GravityGameInstance.getInstance().stop();
    }
}
