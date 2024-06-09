package tk.kanaostore.losteddev.skywars.world;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import com.google.common.collect.ImmutableList;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsServer;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsState;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsTeam;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsBlock;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsChest;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsChest.ChestType;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsMode;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsType;
import tk.kanaostore.losteddev.skywars.ui.server.ScanCallback;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;
import tk.kanaostore.losteddev.skywars.utils.FileUtils;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;
import tk.kanaostore.losteddev.skywars.world.type.DoublesRankedServer;
import tk.kanaostore.losteddev.skywars.world.type.DoublesServer;
import tk.kanaostore.losteddev.skywars.world.type.DuelsServer;
import tk.kanaostore.losteddev.skywars.world.type.SoloRankedServer;
import tk.kanaostore.losteddev.skywars.world.type.SoloServer;

@SuppressWarnings("deprecation")
public abstract class WorldServer<T> extends SkyWarsServer {

  protected int timer;
  protected WorldConfig config;

  protected WorldTimer task;

  protected List<SkyWarsTeam> teams = new ArrayList<>();
  protected List<SkyWarsChest> chests = new ArrayList<>();
  protected Map<String, SkyWarsBlock> blocks = new HashMap<>();

  public WorldServer(String yaml) {
    this(yaml, null);
  }

  public WorldServer(String yaml, ScanCallback callback) {
    super();
    this.timer = Language.game$countdown$start + 1;
    this.config = new WorldConfig(yaml);
    this.name = config.getMapName();
    this.task = new WorldTimer(this);
    for (String spawn : this.config.listSpawns()) {
      this.teams.add(new SkyWarsTeam(this, this.teams.size(), spawn));
    }
    this.config.listChests().forEach(chest -> chests.add(new SkyWarsChest(this, chest)));

    ConfigUtils cu = ConfigUtils.getConfig(yaml, "plugins/LostSkyWars/blocks");
    if (cu.contains("data")) {
      for (String blockdata : cu.getStringList("data")) {
        blocks.put(blockdata.split(" : ")[0],
            new SkyWarsBlock(Material.matchMaterial(blockdata.split(" : ")[1].split(", ")[0]), Byte.valueOf(blockdata.split(" : ")[1].split(", ")[1])));
      }

      this.state = SkyWarsState.WAITING;
    } else {
      this.state = SkyWarsState.ENDED;
      WorldRegeneration.scan(this, cu, callback);
    }
  }

  @Override
  public void destroy() {
    this.timer = 0;
    this.config.destroy();
    this.config = null;
    this.task.cancel();
    this.teams.clear();
    this.teams = null;
    this.chests.clear();
    this.chests = null;
    this.blocks.clear();
    this.blocks = null;
  }

  public abstract void start();

  public abstract void broadcast(String message);

  public abstract void broadcastAction(String message);

  public abstract void broadcastTitle(String title, String subtitle);

  public abstract void broadcast(String message, boolean spectators);

  public abstract void updateScoreboards();

  public abstract void stop(T winner);

  public abstract void reset();

  public abstract void kill(Account account, Account killer);

  public abstract List<Player> getPlayers(boolean spectators);

  public List<SkyWarsTeam> getAliveTeams() {
    return teams.stream().filter(team -> team.isAlive()).collect(Collectors.toList());
  }

  public List<SkyWarsTeam> getTeams() {
    return teams;
  }

  public SkyWarsTeam getTeam(Player player) {
    for (SkyWarsTeam team : teams) {
      if (team.hasMember(player)) {
        return team;
      }
    }

    return null;
  }

  public SkyWarsTeam getAvaibleTeam(Player player) {
    return getAvaibleTeam(player, 1);
  }

  public SkyWarsTeam getAvaibleTeam(Player player, int size) {
    for (SkyWarsTeam team : teams) {
      if (team.canJoin(size)) {
        team.addMember(player);
        return team;
      }
    }

    return null;
  }

  public abstract boolean isAlive(Player player);

  public abstract boolean isSpectator(Player player);

  public abstract int getAlive();

  public abstract int getKills(Player player);

  public void setTimer(int timer) {
    this.timer = timer;
  }

  public void resetBlock(Block block) {
    SkyWarsBlock sb = blocks.get(BukkitUtils.serializeLocation(block.getLocation()));

    if (sb != null) {
      block.setType(sb.getMaterial());
      BlockState state = block.getState();
      state.getData().setData(sb.getData());
      state.update(true);
    } else {
      block.setType(Material.AIR);
    }
  }

  public void changeChest(SkyWarsChest chest, ChestType type) {
    this.config.listChests().remove(chest.toString());
    chest.setType(type);
    this.config.listChests().add(chest.toString());
    this.config.getConfig().set("chests", this.config.listChests());
  }

  public String getEvent() {
    if (this.getState() == SkyWarsState.STARTING) {
      return Language.game$event$start + new SimpleDateFormat("mm:ss").format(getTimer() * 1000);
    }

    int eventTime = getEventTime(true);
    return (eventTime == 0 ? Language.game$event$end : Language.game$event$refill) + new SimpleDateFormat("mm:ss").format((getTimer() - eventTime) * 1000);
  }

  public String getEventTime() {
    int eventTime = getEventTime(true);
    return new SimpleDateFormat("mm:ss").format((getTimer() - eventTime) * 1000);
  }

  public int getEventTime(boolean flag) {
    for (Integer integer : Language.game$countdown$refills) {
      if (getTimer() >= (flag ? integer + 1 : integer)) {
        return integer;
      }
    }

    return 0;
  }

  public int getNextEventTime() {
    for (Integer integer : Language.game$countdown$refills) {
      if (getTimer() > integer) {
        return integer;
      }
    }

    return 0;
  }

  public int getTimer() {
    return timer;
  }

  public WorldTimer getTask() {
    return task;
  }

  public World getWorld() {
    return config.getWorld();
  }

  public WorldConfig getConfig() {
    return config;
  }

  public Map<String, SkyWarsBlock> getBlocks() {
    return blocks;
  }

  public SkyWarsChest getChest(Block block) {
    for (SkyWarsChest chest : chests) {
      if (chest.getLocation().equals(block.getLocation())) {
        return chest;
      }
    }

    return null;
  }

  @Override
  public SkyWarsType getType() {
    return SkyWarsType.fromName(this.config.getServerType());
  }

  @Override
  public int getMaxPlayers() {
    return config.listSpawns().size();
  }

  public static final LostLogger LOGGER = Main.LOGGER.getModule("WorldServer");
  private static Map<String, WorldServer<?>> servers = new HashMap<>();

  public static void setupServers() {
    File ymlFolder = new File("plugins/LostSkyWars/servers");
    File mapFolder = new File("plugins/LostSkyWars/maps");

    if (!ymlFolder.exists() || !mapFolder.exists()) {
      if (!ymlFolder.exists()) {
        ymlFolder.mkdirs();
      }
      if (!mapFolder.exists()) {
        mapFolder.mkdirs();
      }
    }

    for (File file : ymlFolder.listFiles()) {
      loadArena(file, null);
    }

    LOGGER.log(LostLevel.INFO, "Loaded " + servers.size() + " servers!");
  }

  public static void loadArena(File yamlFile, ScanCallback callback) {
    String arenaName = yamlFile.getName().split("\\.")[0];

    try {
      File backup = new File("plugins/LostSkyWars/maps", arenaName);
      if (!backup.exists() || !backup.isDirectory()) {
        throw new IllegalArgumentException("Cannot find world dir for arena " + yamlFile.getName() + "!");
      }

      String mode = ConfigUtils.getConfig(arenaName, "plugins/LostSkyWars/servers").getString("mode");
      String type = ConfigUtils.getConfig(arenaName, "plugins/LostSkyWars/servers").getString("type");
      WorldServer<?> server = null;
      if (type.equalsIgnoreCase("ranked")) {
        server = mode.equalsIgnoreCase("solo") ? new SoloRankedServer(arenaName, callback) : new DoublesRankedServer(arenaName, callback);
      } else if (type.equalsIgnoreCase("duels")) {
        server = new DuelsServer(arenaName, callback);
      } else {
        server = mode.equalsIgnoreCase("solo") ? new SoloServer(arenaName, callback) : new DoublesServer(arenaName, callback);
      }

      server.getWorld().getEntities().forEach(Entity::remove);
      servers.put(arenaName, server);
    } catch (IllegalArgumentException ex) {
      LOGGER.log(LostLevel.WARNING, "loadArena(\"" + yamlFile + "\")", ex);
    }
  }

  public static void removeArena(WorldServer<?> server) {
    servers.remove(server.getServerName());
    FileUtils.deleteFile(server.getConfig().getConfig().getFile());
    FileUtils.deleteFile(new File("plugins/LostSkyWars/maps", server.getServerName()));
    FileUtils.deleteFile(new File("plugins/LostSkyWars/blocks", server.getServerName() + ".yml"));
    server.destroy();
  }

  public static WorldServer<?> findRandom(SkyWarsMode mode, SkyWarsType type) {
    List<WorldServer<?>> servers = listServers().stream()
        .filter(server -> server.getMode().equals(mode) && server.getType().equals(type) && server.getState().canJoin() && server.getAlive() < server.getMaxPlayers())
        .collect(Collectors.toList());
    Collections.sort(servers, (s1, s2) -> Integer.compare(s2.getAlive(), s1.getAlive()));
    WorldServer<?> server = servers.stream().findFirst().orElse(null);
    if (server != null && server.getAlive() == 0) {
      server = servers.get(ThreadLocalRandom.current().nextInt(servers.size()));
    }

    return server;
  }

  public static Map<String, List<WorldServer<?>>> getAsMap(SkyWarsMode mode, SkyWarsType type) {
    Map<String, List<WorldServer<?>>> result = new HashMap<>();
    listServers().stream().filter(server -> server.getMode().equals(mode) && server.getType().equals(type)).forEach(arena -> {
      List<WorldServer<?>> list = result.get(arena.getName());
      if (list == null) {
        list = new ArrayList<>();
        result.put(arena.getName(), list);
      }

      if (arena.getState().canJoin() && arena.getAlive() < arena.getMaxPlayers()) {
        list.add(arena);
      }
    });

    return result;
  }

  public static WorldServer<?> getByWorldName(String name) {
    return servers.get(name);
  }

  public static Collection<WorldServer<?>> listServers() {
    return ImmutableList.copyOf(servers.values());
  }
}
