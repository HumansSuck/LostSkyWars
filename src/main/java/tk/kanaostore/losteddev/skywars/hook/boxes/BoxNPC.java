package tk.kanaostore.losteddev.skywars.hook.boxes;

import static tk.kanaostore.losteddev.skywars.hook.BoxesHook.LOGGER;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import com.google.common.collect.ImmutableList;
import io.github.losteddev.boxes.api.box.Box;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.holograms.Hologram;
import tk.kanaostore.losteddev.skywars.holograms.Holograms;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;

public class BoxNPC {
  
  private String id;
  private Location location;

  private Hologram hologram;
  
  public BoxNPC(String id, Location location) {
    this.id = id;
    this.location = location;
    if (!this.location.getChunk().isLoaded()) {
      this.location.getChunk().load(true);
    }
    
    if (this.location.getBlock().getType() == Material.AIR) {
      this.location.getBlock().setType(Material.ENDER_CHEST);
    }
    
    this.hologram = Holograms.createHologram(this.location.clone());
    List<String> lines = new ArrayList<>(Language.lobby$npcs$box$holograms);
    Collections.reverse(lines);
    for (String line : lines) {
      this.hologram.withLine(line);
    }
  }
  
  public void enable() {
    this.hologram.spawn();
    this.using = null;
  }
  
  public void disable() {
    this.hologram.despawn();
  }
  
  public void destroy() {
    this.id = null;
    this.location = null;

    Holograms.removeHologram(hologram);
    this.hologram = null;
  }
  
  private String using;
  
  public void open(Account account, Box box) {
    Player player = account.getPlayer();
    if (using != null) {
      player.sendMessage(Language.lobby$npcs$box$already_in_use);
      return;
    }

    this.disable();
    this.using = player.getName();
    BoxOpener.spin(account, box, location, new OpeningCallback() {
      
      @Override
      public void finish() {
        BoxNPC.this.enable();
      }
    });
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
  
  private static final ConfigUtils CONFIG = ConfigUtils.getConfig("locations");

  private static List<BoxNPC> npcs = new ArrayList<>();

  public static void setupBoxNPCs() {
    if (!CONFIG.contains("box-npcs")) {
      CONFIG.set("box-npcs", new ArrayList<>());
    }

    for (String serialized : CONFIG.getStringList("box-npcs")) {
      if (serialized.split("; ").length > 6) {
        String id = serialized.split("; ")[6];

        npcs.add(new BoxNPC(id, BukkitUtils.deserializeLocation(serialized)));
      }
    }

    LOGGER.log(LostLevel.INFO, "Loaded " + npcs.size() + " BoxNPCS!");
  }

  public static void add(String id, Location location) {
    npcs.add(new BoxNPC(id, location));
    List<String> list = CONFIG.getStringList("box-npcs");
    list.add(BukkitUtils.serializeLocation(location) + "; " + id);
    CONFIG.set("box-npcs", list);
  }

  public static void remove(BoxNPC npc) {
    npcs.remove(npc);
    List<String> list = CONFIG.getStringList("box-npcs");
    list.remove(BukkitUtils.serializeLocation(npc.getLocation()) + "; " + npc.getId());
    CONFIG.set("box-npcs", list);

    npc.destroy();
  }

  public static BoxNPC getById(String id) {
    return npcs.stream().filter(npc -> npc.getId().equals(id)).findFirst().orElse(null);
  }
  
  public static BoxNPC getByLocation(Location location) {
    return npcs.stream().filter(npc -> npc.getLocation().getBlock().equals(location.getBlock())).findFirst().orElse(null);
  }

  public static List<BoxNPC> listNPCs() {
    return ImmutableList.copyOf(npcs);
  }
}
