package tk.kanaostore.losteddev.skywars.bungee;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsMode;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsType;

public class CoreLobbies {
  
  public static int SOLO_NORMAL;
  public static int SOLO_INSANE;
  public static int SOLO_RANKED;
  public static int SOLO_DUELS;
  public static int DOUBLES_NORMAL;
  public static int DOUBLES_INSANE;
  public static int DOUBLES_RANKED;
  public static int DOUBLES_DUELS;
  
  public static Map<String, Integer> SOLO_NORMAL_MAP = new HashMap<>();
  public static Map<String, Integer> SOLO_INSANE_MAP = new HashMap<>();
  public static Map<String, Integer> SOLO_RANKED_MAP = new HashMap<>();
  public static Map<String, Integer> DOUBLES_NORMAL_MAP = new HashMap<>();
  public static Map<String, Integer> DOUBLES_INSANE_MAP = new HashMap<>();
  public static Map<String, Integer> DOUBLES_RANKED_MAP = new HashMap<>();

  public static void writeLobby(Player player) {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();

    out.writeUTF("Lobby");

    Account account = Database.getInstance().getAccount(player.getUniqueId());
    if (account != null) {
      account.save();
    }
    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
      player.sendPluginMessage(Main.getInstance(), "LostSWAPI", out.toByteArray());
    }, 15);
  }

  public static void writeCount(String serverType) {
    Player faker = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
    if (faker != null) {
      ByteArrayDataOutput out = ByteStreams.newDataOutput();

      out.writeUTF("Count");
      out.writeUTF(serverType);
      
      faker.sendPluginMessage(Main.getInstance(), "LostSWAPI", out.toByteArray());
    }
  }

  public static void writeMinigame(Player player, String serverType, String map) {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();

    out.writeUTF("Play");
    out.writeUTF(serverType);
    out.writeUTF(map);

    Account account = Database.getInstance().getAccount(player.getUniqueId());
    if (account != null) {
      account.save();
    }
    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
      player.sendPluginMessage(Main.getInstance(), "LostSWAPI", out.toByteArray());
    }, 15);
  }

  public static void writeMapSelector(String serverType) {
    Player faker = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
    if (faker != null) {
      ByteArrayDataOutput out = ByteStreams.newDataOutput();

      out.writeUTF("MapSelector");
      out.writeUTF(serverType);
      
      faker.sendPluginMessage(Main.getInstance(), "LostSWAPI", out.toByteArray());
    }
  }

  public static void setupLobbies() {
    Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(Main.getInstance(), "LostSWAPI");

    new BukkitRunnable() {
      @Override
      public void run() {
        if (Bukkit.getOnlinePlayers().size() > 0) {
          for (SkyWarsMode mode : SkyWarsMode.values()) {
            for (SkyWarsType type : SkyWarsType.values()) {
              writeCount(mode.name() + "_" + type.name());
              if (type == SkyWarsType.DUELS) {
                continue;
              }
              writeMapSelector(mode.name() + "_" + type.name());
            }
          }
        }
      }
    }.runTaskTimer(Main.getInstance(), 0, 40);
  }
}
