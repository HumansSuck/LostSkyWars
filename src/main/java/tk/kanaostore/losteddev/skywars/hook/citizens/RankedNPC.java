package tk.kanaostore.losteddev.skywars.hook.citizens;

import static tk.kanaostore.losteddev.skywars.hook.CitizensHook.LOGGER;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;
import com.google.common.collect.ImmutableList;
import net.citizensnpcs.api.npc.NPC;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.bungee.Core;
import tk.kanaostore.losteddev.skywars.bungee.CoreLobbies;
import tk.kanaostore.losteddev.skywars.bungee.CoreMode;
import tk.kanaostore.losteddev.skywars.holograms.Hologram;
import tk.kanaostore.losteddev.skywars.holograms.Holograms;
import tk.kanaostore.losteddev.skywars.hook.CitizensHook;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsType;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;
import tk.kanaostore.losteddev.skywars.world.WorldServer;

public class RankedNPC {

  private String id;
  private Location location;

  private NPC npc;
  private Hologram hologram;

  public RankedNPC(String id, Location location) {
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
    List<String> lines = new ArrayList<>(Language.lobby$npcs$ranked$holograms);
    Collections.reverse(lines);
    for (String line : lines) {
      this.hologram.withLine(line.replace("{players}", "0"));
    }
    this.npc = CitizensHook.getRegistry().createNPC(EntityType.PLAYER, "§8[NPC] ");
    this.npc.data().setPersistent("ranked-npc", "ranked");
    this.npc.data().setPersistent("cached-skin-uuid-name", "[npc] ");
    this.npc.data().setPersistent("player-skin-use-latest", false);
    this.npc.data().setPersistent("player-skin-textures", Language.lobby$npcs$ranked$skin_value);
    this.npc.data().setPersistent("player-skin-signature", Language.lobby$npcs$ranked$skin_signature);
    this.npc.spawn(location);
  }

  public void update() {
    int playing = CoreLobbies.SOLO_RANKED + CoreLobbies.DOUBLES_RANKED;
    if (Core.MODE == CoreMode.MULTI_ARENA) {
      for (WorldServer<?> server : WorldServer.listServers()) {
        if (server.getType().equals(SkyWarsType.RANKED)) {
          playing += server.getOnline();
        }
      }
    }

    List<String> lines = new ArrayList<>(Language.lobby$npcs$ranked$holograms);
    Collections.reverse(lines);
    for (int slot = 0; slot < lines.size(); slot++) {
      this.hologram.updateLine(slot + 1, lines.get(slot).replace("{players}", StringUtils.formatNumber(playing)));
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

  private static List<RankedNPC> npcs = new ArrayList<>();

  public static void setupRankedNPCs() {
    if (!CONFIG.contains("ranked-npcs")) {
      CONFIG.set("ranked-npcs", new ArrayList<>());
    }

    for (String serialized : CONFIG.getStringList("ranked-npcs")) {
      if (serialized.split("; ").length > 6) {
        String id = serialized.split("; ")[6];
        npcs.add(new RankedNPC(id, BukkitUtils.deserializeLocation(serialized)));
      }
    }

    new BukkitRunnable() {

      @Override
      public void run() {
        npcs.forEach(RankedNPC::update);
      }
    }.runTaskTimer(Main.getInstance(), 20, 20);

    LOGGER.log(LostLevel.INFO, "Loaded " + npcs.size() + " RankedNPCs!");
  }

  public static void add(String id, Location location) {
    npcs.add(new RankedNPC(id, location));
    List<String> list = CONFIG.getStringList("ranked-npcs");
    list.add(BukkitUtils.serializeLocation(location) + "; " + id);
    CONFIG.set("ranked-npcs", list);
  }

  public static void remove(RankedNPC npc) {
    npcs.remove(npc);
    List<String> list = CONFIG.getStringList("ranked-npcs");
    list.remove(BukkitUtils.serializeLocation(npc.getLocation()) + "; " + npc.getId());
    CONFIG.set("ranked-npcs", list);

    npc.destroy();
  }

  public static RankedNPC getById(String id) {
    return npcs.stream().filter(npc -> npc.getId().equals(id)).findFirst().orElse(null);
  }

  public static List<RankedNPC> listNPCs() {
    return ImmutableList.copyOf(npcs);
  }
}
