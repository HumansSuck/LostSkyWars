package tk.kanaostore.losteddev.skywars.world;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsCube;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;
import tk.kanaostore.losteddev.skywars.utils.FileUtils;

@SuppressWarnings("deprecation")
public class WorldConfig {

  private String yaml;
  private String name;
  private String mode;
  private String type;
  private World world;
  private int minPlayers;
  private SkyWarsCube cube;
  private SkyWarsCube waitingCube;
  private String waitingLobby;

  private List<String> spawns;
  private List<String> chests;
  private List<String> balloons;

  private ConfigUtils config;

  public WorldConfig(String yaml) {
    this.yaml = yaml;
    this.spawns = new ArrayList<>();
    this.chests = new ArrayList<>();
    this.balloons = new ArrayList<>();
    this.config = ConfigUtils.getConfig(yaml, "plugins/LostSkyWars/servers");

    this.name = config.getString("name");
    this.mode = config.getString("mode");
    this.type = config.getString("type");
    this.minPlayers = config.getInt("min-players");
    this.cube = new SkyWarsCube(config.getString("cube"));
    if (this.config.contains("waiting-cube")) {
      this.waitingCube = new SkyWarsCube(this.config.getString("waiting-cube"));
      this.waitingLobby = this.config.getString("waiting-lobby");
    }
    this.spawns.addAll(config.getStringList("spawns"));
    this.chests.addAll(config.getStringList("chests"));
    if (!this.config.contains("balloons")) {
      this.config.set("balloons", new ArrayList<>());
    }
    this.balloons.addAll(config.getStringList("balloons"));

    File file = new File("plugins/LostSkyWars/maps/" + yaml);
    if ((this.world = Bukkit.getWorld(file.getName())) != null) {
      Bukkit.unloadWorld(world, false);
    }

    FileUtils.deleteFile(new File(file.getName()));
    FileUtils.copyFiles(file, new File(file.getName()));

    WorldCreator wc = WorldCreator.name(file.getName());
    wc.generateStructures(false);
    this.world = wc.createWorld();
    this.world.setTime(0l);
    this.world.setStorm(false);
    this.world.setThundering(false);
    this.world.setAutoSave(false);
    this.world.setAnimalSpawnLimit(0);
    this.world.setWaterAnimalSpawnLimit(0);
    this.world.setKeepSpawnInMemory(false);
    this.world.setGameRuleValue("doMobSpawning", "false");
    this.world.setGameRuleValue("doDaylightCycle", "false");
    this.world.setGameRuleValue("mobGriefing", "false");
  }

  public void setWaitingLobby(SkyWarsCube waitingCube, Location waitingLobby) {
    this.config.set("waiting-cube", waitingCube.toString());
    this.config.set("waiting-lobby", BukkitUtils.serializeLocation(waitingLobby));
    this.waitingCube = new SkyWarsCube(this.config.getString("waiting-cube"));
    this.waitingLobby = this.config.getString("waiting-lobby");
  }
  
  public void addBalloon(String balloon) {
    this.balloons.add(balloon);
    this.config.set("balloons", this.balloons);
  }
  
  public boolean isBalloon(String blockLocation) {
    return this.balloons.stream().filter(balloon -> balloon.equals(blockLocation)).count() > 0;
  }

  public void removeWaitingLobby() {
    if (this.waitingCube != null) {
      new BukkitRunnable() {
        @Override
        public void run() {
          Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            Iterator<Block> iterator = WorldConfig.this.waitingCube.iterator();
            while (iterator.hasNext()) {
              iterator.next().setType(Material.AIR);
            }
          });
        }
      }.runTaskAsynchronously(Main.getInstance());
    }
  }

  public void destroy() {
    if ((this.world = Bukkit.getWorld(yaml)) != null) {
      Bukkit.unloadWorld(world, false);
    }

    FileUtils.deleteFile(new File(yaml));
    this.yaml = null;
    this.name = null;
    this.mode = null;
    this.type = null;
    this.minPlayers = 0;
    this.cube = null;
    this.spawns.clear();
    this.spawns = null;
    this.chests.clear();
    this.chests = null;
    this.world = null;
    ConfigUtils.removeConfig(config);
    this.config = null;
  }

  public void reload() {
    File file = new File("plugins/LostSkyWars/maps/" + yaml);
    if ((this.world = Bukkit.getWorld(file.getName())) != null) {
      Bukkit.unloadWorld(world, false);
    }

    FileUtils.deleteFile(new File(file.getName()));
    FileUtils.copyFiles(file, new File(file.getName()));

    WorldCreator wc = WorldCreator.name(file.getName());
    wc.generateStructures(false);
    this.world = wc.createWorld();
    this.world.setTime(0l);
    this.world.setStorm(false);
    this.world.setThundering(false);
    this.world.setAutoSave(false);
    this.world.setAnimalSpawnLimit(0);
    this.world.setWaterAnimalSpawnLimit(0);
    this.world.setKeepSpawnInMemory(false);
    this.world.setGameRuleValue("doMobSpawning", "false");
    this.world.setGameRuleValue("doDaylightCycle", "false");
    this.world.setGameRuleValue("mobGriefing", "false");
  }

  public ConfigUtils getConfig() {
    return config;
  }

  public String getMapName() {
    return name;
  }

  public String getServerMode() {
    return mode;
  }

  public String getServerType() {
    return type;
  }

  public World getWorld() {
    return world;
  }

  public int getMinPlayers() {
    return minPlayers;
  }

  public SkyWarsCube getWorldCube() {
    return cube;
  }

  public SkyWarsCube getWaitingCube() {
    return waitingCube;
  }

  public Location getWaitingLocation() {
    return BukkitUtils.deserializeLocation(this.waitingLobby);
  }

  public boolean hasWaitingLobby() {
    return this.waitingCube != null;
  }

  public List<String> listSpawns() {
    return spawns;
  }

  public List<String> listChests() {
    return chests;
  }
  
  public List<String> listBalloons() {
    return balloons;
  }
  
  public String getBalloon(int index) {
    return index >= this.balloons.size() ? null : this.balloons.get(index);
  }
}
