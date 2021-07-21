package net.kunmc.lab.gravitymod_dga;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
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
    private float holeSpeed;
    private int startWait;
    private int dimension;
    private int coolDown;
    private int speed;
    private int size;
    private int cont;
    private double x;
    private double y;
    private double z;

    public static GravityGameInstance getInstance() {
        return INSTANCE;
    }

    public void start(double x, double y, double z, int speed, int dimension, int size, boolean dismemberedGravity) {
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
            player.setHealth(player.getMaxHealth());
        }
        this.x = x;
        this.y = y;
        this.z = z;
        this.coolDown = 0;
        this.startWait = 20 * 10;
        this.cont = 0;
        this.speed = speed;
        this.dimension = dimension;
        this.size = size;
        this.running = true;
        this.dismemberedGravity = dismemberedGravity;
        this.holeSpeed = 1;
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

        if (players.size() <= 1) {
            players.stream().findFirst().ifPresent(n -> {
                for (EntityPlayerMP player : ServerUtils.getServer().getPlayerList().getPlayers()) {
                    SPacketTitle spackettitle = new SPacketTitle(SPacketTitle.Type.SUBTITLE, new TextComponentString(String.format("§6勝者", ServerUtils.getServer().getPlayerList().getPlayerByUUID(n).getGameProfile().getName())));
                    SPacketTitle spackettitle1 = new SPacketTitle(SPacketTitle.Type.TITLE, new TextComponentString(String.format("%s", ServerUtils.getServer().getPlayerList().getPlayerByUUID(n).getGameProfile().getName())));
                    player.connection.sendPacket(spackettitle1);
                    player.connection.sendPacket(spackettitle);
                }
            });
            stop();
            return;
        }

        if (startWait > 0) {
            startWait--;

            for (UUID player : players) {
                EntityPlayerMP pl = ServerUtils.getServer().getPlayerList().getPlayerByUUID(player);
                pl.sendStatusMessage(new TextComponentTranslation("gravitygame.start.wait", (startWait / 20)), true);
            }
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

            int mensiz = (size * 2) * (size * 2);
            float par = Math.min(((float) cont / 512) * holeSpeed, 0.5f);

            for (EnumGravityDirection value : EnumGravityDirection.values()) {
                for (int i = 0; i < ((float) mensiz * par); i++) {
                    BlockPos f = null;

                    if (value == EnumGravityDirection.DOWN)
                        f = new BlockPos(x - size + random.nextInt(size * 2), y - size, z - size + random.nextInt(size * 2));
                    if (value == EnumGravityDirection.UP)
                        f = new BlockPos(x - size + random.nextInt(size * 2), y + size, z - size + random.nextInt(size * 2));
                    if (value == EnumGravityDirection.EAST)
                        f = new BlockPos(x - size, y - size + random.nextInt(size * 2), z - size + random.nextInt(size * 2));
                    if (value == EnumGravityDirection.WEST)
                        f = new BlockPos(x + size, y - size + random.nextInt(size * 2), z - size + random.nextInt(size * 2));
                    if (value == EnumGravityDirection.SOUTH)
                        f = new BlockPos(x - size + random.nextInt(size * 2), y - size + random.nextInt(size * 2), z - size);
                    if (value == EnumGravityDirection.NORTH)
                        f = new BlockPos(x - size + random.nextInt(size * 2), y - size + random.nextInt(size * 2), z + size);

                    if (f != null)
                        getWorld().setBlockState(f, Blocks.AIR.getDefaultState());
                }
            }
            cont++;
        }


        for (UUID player : players) {
            EntityPlayerMP pl = ServerUtils.getServer().getPlayerList().getPlayerByUUID(player);
            if (pl.isDead)
                continue;
            Vec3d vec3d = pl.getPositionVector();
            int gsize = size + 3;
            if (!(vec3d.x <= x + gsize && vec3d.x >= x - gsize && vec3d.y <= y + gsize && vec3d.y >= y - gsize && vec3d.z <= z + gsize && vec3d.z >= z - gsize && pl.dimension == dimension)) {
                pl.attackEntityFrom(GADamageSources.OUT_OF_AREA, Float.MAX_VALUE);
                ikisugiPlayers.add(player);
            }
            pl.sendStatusMessage(new TextComponentTranslation("gravitygame.next", speed - (coolDown / 20)), true);
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

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void removePlayer(UUID uuid) {
        ikisugiPlayers.add(uuid);
    }

    public List<UUID> getPlayers() {
        return players;
    }

    private World getWorld() {
        return ServerUtils.getServer().getWorld(dimension);
    }

    public boolean isWait() {
        return startWait > 0;
    }

    public void setHoleSpeed(float holeSpeed) {
        this.holeSpeed = holeSpeed;
    }

    public void setDismemberedGravity(boolean dismemberedGravity) {
        this.dismemberedGravity = dismemberedGravity;
    }
}
