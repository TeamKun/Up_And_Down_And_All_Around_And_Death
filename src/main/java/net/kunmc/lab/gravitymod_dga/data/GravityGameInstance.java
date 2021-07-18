package net.kunmc.lab.gravitymod_dga.data;

import net.kunmc.lab.gravitymod_dga.GADamageSources;
import net.kunmc.lab.gravitymod_dga.util.ServerUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import uk.co.mysterymayhem.gravitymod.api.API;
import uk.co.mysterymayhem.gravitymod.api.EnumGravityDirection;

import java.util.*;

public class GravityGameInstance {
    private static final GravityGameInstance INSTANCE = new GravityGameInstance();
    private final Map<UUID, EnumGravityDirection> directiondata = new HashMap<>();
    private final List<UUID> ikisugiPlayers = new ArrayList<>();
    private final List<UUID> players = new ArrayList<>();
    private final Random random = new Random();
    private boolean dismemberedGravity;
    private int maxEntityCramming;
    private boolean running;
    private int coolDown;
    private int dimension;
    private int speed;
    private int size;
    private double x;
    private double y;
    private double z;

    public static GravityGameInstance getInstance() {
        return INSTANCE;
    }

    public void start(double x, double y, double z, int speed, int dimension, int size) {
        this.maxEntityCramming = ServerUtils.getServer().getWorld(dimension).getGameRules().getInt("maxEntityCramming");
        ServerUtils.getServer().getWorld(dimension).getGameRules().setOrCreateGameRule("maxEntityCramming", "0");
        for (EntityPlayerMP player : ServerUtils.getPlayers()) {
            if (player.isDead)
                continue;
            players.add(player.getGameProfile().getId());
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 114514 * 1919, 0, true, false));
            if (player.dimension != dimension)
                player.changeDimension(dimension);
            player.connection.setPlayerLocation(x, y, z, 0, 0);
            player.setGameType(GameType.ADVENTURE);
        }
        this.x = x;
        this.y = y;
        this.z = z;
        this.coolDown = 0;
        this.speed = speed;
        this.dimension = dimension;
        this.size = size;
        this.running = true;
    }

    public void stop() {
        running = false;
        players.clear();
        directiondata.clear();
        coolDown = 0;
        ServerUtils.getServer().getWorld(dimension).getGameRules().setOrCreateGameRule("maxEntityCramming", String.valueOf(maxEntityCramming));
    }

    public boolean isRunning() {
        return running;
    }

    public void tick() {
        if (!running)
            return;

        List<UUID> offLine = new ArrayList<>();
        players.forEach(n -> {
            if (!ServerUtils.isOnlinePlayer(n))
                offLine.add(n);
        });
        players.removeAll(offLine);
        offLine.forEach(directiondata::remove);

        if (players.isEmpty()) {
            stop();
            return;
        }

        coolDown++;

        if (coolDown >= 20 * speed) {
            coolDown = 0;

            if (dismemberedGravity) {
                for (UUID player : players) {
                    EnumGravityDirection mae = null;
                    if (directiondata.containsKey(player))
                        mae = directiondata.get(player);
                    directiondata.put(player, nextDirection(mae));
                }
            } else {
                EnumGravityDirection mae = nextDirection(directiondata.values().stream().findFirst().orElse(null));
                for (UUID player : players) {
                    directiondata.put(player, mae);
                }
            }

        }
        for (UUID player : players) {
            EntityPlayerMP pl = ServerUtils.getServer().getPlayerList().getPlayerByUUID(player);
            if (pl.isDead)
                continue;
            Vec3d vec3d = pl.getPositionVector();
            int gsize = size + 3;
            if (!(vec3d.x <= x + gsize && vec3d.x >= x - gsize && vec3d.y <= y + gsize && vec3d.y >= y - gsize && vec3d.z <= z + gsize && vec3d.z >= z - gsize)) {
                pl.attackEntityFrom(GADamageSources.OUT_OF_AREA, Float.MAX_VALUE);
                ikisugiPlayers.add(player);
            }
        }
        players.removeAll(ikisugiPlayers);
        ikisugiPlayers.forEach(directiondata::remove);
        ikisugiPlayers.clear();

        directiondata.forEach((n, m) -> {
            if (players.contains(n)) {
                EntityPlayerMP ple = ServerUtils.getServer().getPlayerList().getPlayerByUUID(n);
                API.setPlayerGravity(m, ple, 715827881);
            }
        });

    }

    private EnumGravityDirection nextDirection(EnumGravityDirection direction) {
        List<EnumGravityDirection> directions = new ArrayList<>();

        if (direction == EnumGravityDirection.DOWN || direction == EnumGravityDirection.UP) {
            directions.add(EnumGravityDirection.EAST);
            directions.add(EnumGravityDirection.NORTH);
            directions.add(EnumGravityDirection.SOUTH);
            directions.add(EnumGravityDirection.WEST);
        } else {
            directions.add(EnumGravityDirection.DOWN);
            directions.add(EnumGravityDirection.UP);
        }

        if (direction == EnumGravityDirection.EAST || direction == EnumGravityDirection.WEST) {
            directions.add(EnumGravityDirection.NORTH);
            directions.add(EnumGravityDirection.SOUTH);
        } else if (direction == EnumGravityDirection.NORTH || direction == EnumGravityDirection.SOUTH) {
            directions.add(EnumGravityDirection.EAST);
            directions.add(EnumGravityDirection.WEST);
        }

        return directions.get(random.nextInt(directions.size()));
    }


    public void removePlayer(UUID uuid) {
        ikisugiPlayers.add(uuid);
    }

    public List<UUID> getPlayers() {
        return players;
    }
}