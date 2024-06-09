package tk.kanaostore.losteddev.skywars.hook.citizens;

import static tk.kanaostore.losteddev.skywars.hook.CitizensHook.LOGGER;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import com.google.common.collect.ImmutableList;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.holograms.Hologram;
import tk.kanaostore.losteddev.skywars.holograms.Holograms;
import tk.kanaostore.losteddev.skywars.hook.CitizensHook;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;

@SuppressWarnings("deprecation")
public class StatsNPC {

  private String id;
  private Location location;

  private NPC npc;
  private Hologram hologram;

  public StatsNPC(String id, Location location) {
    this.id = id;
    this.location = location;
    if (!this.location.getChunk().isLoaded()) {
      this.location.getChunk().load(true);
    }

    this.spawn();
  }

  public void spawn() {
    if (this.npc != null) {
      this.npc.destroy();
      this.npc = null;
    }

    if (this.hologram != null) {
      Holograms.removeHologram(this.hologram);
      this.hologram = null;
    }

    this.hologram = Holograms.createHologram(this.location.clone().add(0, 0.5, 0));
    List<String> lines = new ArrayList<>(Language.lobby$npcs$statsnpc$holograms);
    Collections.reverse(lines);
    for (String line : lines) {
      this.hologram.withLine(line);
    }
    this.npc = CitizensHook.getRegistry().createNPC(EntityType.PLAYER, "§8[NPC] ");
    this.npc.data().setPersistent("profile", true);
    this.npc.data().setPersistent("cached-skin-uuid-name", "[npc] ");
    this.npc.data().setPersistent("player-skin-use-latest", false);
    this.npc.data().setPersistent("player-skin-textures", "");
    this.npc.data().setPersistent("player-skin-signature", "");
    
    this.npc.addTrait(new Trait("stats-item") {
      @Override
      public void onSpawn() {
        ((Player) this.npc.getEntity()).setItemInHand(BukkitUtils.deserializeItemStack("PAPER : 1"));
      }
    });
    
    this.npc.spawn(location);
  }

  public void update() {
    List<String> list = new ArrayList<>(Language.lobby$npcs$statsnpc$holograms);
    Collections.reverse(list);
    for (int slot = 0; slot < list.size(); slot++) {
      this.hologram.updateLine(slot + 1, list.get(slot));
    }
  }

  public void destroy() {
    this.id = null;
    this.location = null;

    this.npc.destroy();
    this.npc = null;
    Holograms.removeHologram(hologram);
    this.hologram = null;
  }

  public String getId() {
    return id;
  }

  public Location getLocation() {
    return location;
  }

  public NPC getNPC() {
    return npc;
  }

  public Hologram getHologram() {
    return hologram;
  }

  private static final ConfigUtils CONFIG = ConfigUtils.getConfig("locations");

  private static List<StatsNPC> npcs = new ArrayList<>();

  public static void setupStatsNPCs() {
    if (!CONFIG.contains("stats-npcs")) {
      CONFIG.set("stats-npcs", new ArrayList<>());
    }

    for (String serialized : CONFIG.getStringList("stats-npcs")) {
      if (serialized.split("; ").length > 6) {
        String id = serialized.split("; ")[6];
        npcs.add(new StatsNPC(id, BukkitUtils.deserializeLocation(serialized)));
      }
    }

    new BukkitRunnable() {
      @Override
      public void run() {
        npcs.forEach(StatsNPC::update);
      }
    }.runTaskTimer(Main.getInstance(), 0, 20);
    LOGGER.log(LostLevel.INFO, "Loaded " + npcs.size() + " StatsNPCs!");
  }

  public static void add(String id, Location location) {
    npcs.add(new StatsNPC(id, location));
    List<String> list = CONFIG.getStringList("stats-npcs");
    list.add(BukkitUtils.serializeLocation(location) + "; " + id);
    CONFIG.set("stats-npcs", list);
  }

  public static void remove(StatsNPC npc) {
    npcs.remove(npc);
    List<String> list = CONFIG.getStringList("stats-npcs");
    list.remove(BukkitUtils.serializeLocation(npc.getLocation()) + "; " + npc.getId());
    CONFIG.set("stats-npcs", list);

    npc.destroy();
  }

  public static StatsNPC getById(String id) {
    return npcs.stream().filter(npc -> npc.getId().equals(id)).findFirst().orElse(null);
  }

  public static List<StatsNPC> listNPCs() {
    return ImmutableList.copyOf(npcs);
  }
}
