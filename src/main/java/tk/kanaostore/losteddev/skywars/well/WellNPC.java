package tk.kanaostore.losteddev.skywars.well;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import com.google.common.collect.ImmutableList;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.holograms.Hologram;
import tk.kanaostore.losteddev.skywars.holograms.Holograms;
import tk.kanaostore.losteddev.skywars.nms.NMS;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;

public class WellNPC {

  private String id;
  private Location location;
  
  private Hologram hologram;

  public WellNPC(String id, Location location) {
    this.id = id;
    this.location = location;
    if (!this.location.getChunk().isLoaded()) {
      this.location.getChunk().load(true);
    }
    
    this.location.getBlock().setType(NMS.ENDER_PORTAL_FRAME);

    this.spawn();
  }

  public void spawn() {
    if (this.hologram != null) {
      Holograms.removeHologram(this.hologram);
      this.hologram = null;
    }

    this.hologram = Holograms.createHologram(this.location);
    List<String> lines = new ArrayList<>(Language.lobby$npcs$well$holograms);
    Collections.reverse(lines);
    for (String line : lines) {
      this.hologram.withLine(line);
    }
  }

  public void destroy() {
    this.id = null;
    this.location.getBlock().setType(Material.AIR);
    this.location = null;

    Holograms.removeHologram(hologram);
    this.hologram = null;
  }

  public String getId() {
    return id;
  }

  public Location getLocation() {
    return location;
  }

  public Hologram getHologram() {
    return hologram;
  }

  public static final LostLogger LOGGER = Main.LOGGER.getModule("SoulWell");
  private static final ConfigUtils CONFIG = ConfigUtils.getConfig("locations");

  private static List<WellNPC> npcs = new ArrayList<>();

  public static void setupWellNPCs() {
    if (!CONFIG.contains("well-npcs")) {
      CONFIG.set("well-npcs", new ArrayList<>());
    }

    for (String serialized : CONFIG.getStringList("well-npcs")) {
      if (serialized.split("; ").length > 6) {
        String id = serialized.split("; ")[6];

        npcs.add(new WellNPC(id, BukkitUtils.deserializeLocation(serialized)));
      }
    }
    WellUpgrade.setupUpgrades();

    LOGGER.log(LostLevel.INFO, "Loaded " + npcs.size() + " WellNPCS!");
  }

  public static void add(String id, Location location) {
    npcs.add(new WellNPC(id, location));
    List<String> list = CONFIG.getStringList("well-npcs");
    list.add(BukkitUtils.serializeLocation(location) + "; " + id);
    CONFIG.set("well-npcs", list);
  }

  public static void remove(WellNPC npc) {
    npcs.remove(npc);
    List<String> list = CONFIG.getStringList("well-npcs");
    list.remove(BukkitUtils.serializeLocation(npc.getLocation()) + "; " + npc.getId());
    CONFIG.set("well-npcs", list);

    npc.destroy();
  }

  public static WellNPC getById(String id) {
    return npcs.stream().filter(npc -> npc.getId().equals(id)).findFirst().orElse(null);
  }
  
  public static WellNPC getByLocation(Location location) {
    return npcs.stream().filter(npc -> npc.getLocation().getBlock().equals(location.getBlock())).findFirst().orElse(null);
  }

  public static List<WellNPC> listNPCs() {
    return ImmutableList.copyOf(npcs);
  }
}
