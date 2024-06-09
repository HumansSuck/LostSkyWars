package tk.kanaostore.losteddev.skywars.nms;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.holograms.Hologram;
import tk.kanaostore.losteddev.skywars.holograms.HologramLine;
import tk.kanaostore.losteddev.skywars.holograms.entity.IArmorStand;
import tk.kanaostore.losteddev.skywars.nms.v1_10_R1.NMS1_10R1;
import tk.kanaostore.losteddev.skywars.nms.v1_11_R1.NMS1_11R1;
import tk.kanaostore.losteddev.skywars.nms.v1_12_R1.NMS1_12R1;
import tk.kanaostore.losteddev.skywars.nms.v1_8_R3.NMS1_8R3;
import tk.kanaostore.losteddev.skywars.nms.v1_9_R1.NMS1_9R1;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;
import tk.kanaostore.losteddev.skywars.utils.MinecraftVersion;

@SuppressWarnings("deprecation")
public class NMS {

  private static NMSBridge BRIDGE;
  public static final LostLogger LOGGER = Main.LOGGER.getModule("NMS");

  public static IArmorStand createArmorStand(Location location, String name, HologramLine line) {
    return BRIDGE.createArmorStand(location, name, line);
  }
  
  public static BalloonEntity createBalloonLeash(Location location) {
    return BRIDGE.createBalloonLeash(location);
  }
  
  public static BalloonEntity createBalloonBat(Location location, BalloonEntity leash) {
    return BRIDGE.createBalloonBat(location, leash);
  }
  
  public static BalloonEntity createBalloonGiant(Location location, List<String> frames) {
    return BRIDGE.createBalloonGiant(location, frames);
  }

  public static Hologram getHologram(Entity entity) {
    return BRIDGE.getHologram(entity);
  }

  public static boolean isHologramEntity(Entity entity) {
    return BRIDGE.isHologramEntity(entity);
  }

  public static void playChestAction(Location location, boolean open) {
    BRIDGE.playChestAction(location, open);
  }

  public static void sendActionBar(Player player, String message) {
    BRIDGE.sendActionBar(player, message);
  }

  public static void sendTitle(Player player, String title, String subtitle) {
    BRIDGE.sendTitle(player, title, subtitle);
  }

  public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
    BRIDGE.sendTitle(player, title, subtitle, fadeIn, stay, fadeOut);
  }

  public static void sendTabHeaderFooter(Player player, String header, String footer) {
    BRIDGE.sendTabHeaderFooter(player, header, footer);
  }

  public static boolean setupNMS() {
    if (MinecraftVersion.getCurrentVersion().getCompareId() == 183) {
      BRIDGE = new NMS1_8R3();
      return true;
    } else if (MinecraftVersion.getCurrentVersion().getCompareId() == 191) {
      BRIDGE = new NMS1_9R1();
      return true;
    } else if (MinecraftVersion.getCurrentVersion().getCompareId() == 1101) {
      BRIDGE = new NMS1_10R1();
      return true;
    } else if (MinecraftVersion.getCurrentVersion().getCompareId() == 1111) {
      BRIDGE = new NMS1_11R1();
      return true;
    } else if (MinecraftVersion.getCurrentVersion().getCompareId() == 1121) {
      BRIDGE = new NMS1_12R1();
      return true;
    }

    return false;
  }

  public static Material STATIONARY_WATER, SKULL_ITEM, ENDER_PORTAL_FRAME;

  static {
    STATIONARY_WATER = Material.matchMaterial("STATIONARY_WATER");
    if (STATIONARY_WATER == null) {
      STATIONARY_WATER = Material.LEGACY_STATIONARY_WATER;
    }

    SKULL_ITEM = Material.matchMaterial("SKULL_ITEM");
    if (SKULL_ITEM == null) {
      SKULL_ITEM = Material.LEGACY_SKULL_ITEM;
    }

    ENDER_PORTAL_FRAME = Material.matchMaterial("ENDER_PORTAL_FRAME");
    if (ENDER_PORTAL_FRAME == null) {
      ENDER_PORTAL_FRAME = Material.LEGACY_ENDER_PORTAL_FRAME;
    }
  }
}
