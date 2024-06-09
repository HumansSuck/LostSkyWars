package tk.kanaostore.losteddev.skywars.hook.citizens;

import static tk.kanaostore.losteddev.skywars.hook.CitizensHook.LOGGER;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import com.google.common.collect.ImmutableList;
import net.citizensnpcs.api.npc.NPC;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.delivery.Delivery;
import tk.kanaostore.losteddev.skywars.holograms.Hologram;
import tk.kanaostore.losteddev.skywars.holograms.Holograms;
import tk.kanaostore.losteddev.skywars.hook.CitizensHook;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;

public class DeliveryNPC {

  private String id;
  private Location location;

  private NPC npc;
  private Hologram hologram;

  public DeliveryNPC(String id, Location location) {
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
    List<String> lines = new ArrayList<>(Language.lobby$npcs$deliveryman$holograms);
    Collections.reverse(lines);
    for (String line : lines) {
      this.hologram.withLine(line);
    }
    this.npc = CitizensHook.getRegistry().createNPC(EntityType.PLAYER, "§8[NPC] ");
    this.npc.data().setPersistent("deliveryman", true);
    this.npc.data().setPersistent("cached-skin-uuid-name", "[npc] ");
    this.npc.data().setPersistent("player-skin-use-latest", false);
    this.npc.data().setPersistent("player-skin-textures", Language.lobby$npcs$deliveryman$skin_value);
    this.npc.data().setPersistent("player-skin-signature", Language.lobby$npcs$deliveryman$skin_signature);
    this.npc.spawn(location);
  }
  
  public void update() {
    
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

  private static List<DeliveryNPC> npcs = new ArrayList<>();

  public static void setupDeliveryNPCs() {
    if (!CONFIG.contains("delivery-npcs")) {
      CONFIG.set("delivery-npcs", new ArrayList<>());
    }

    for (String serialized : CONFIG.getStringList("delivery-npcs")) {
      if (serialized.split("; ").length > 6) {
        String id = serialized.split("; ")[6];
        npcs.add(new DeliveryNPC(id, BukkitUtils.deserializeLocation(serialized)));
      }
    }
    Delivery.setupDeliveries();
    
    LOGGER.log(LostLevel.INFO, "Loaded " + npcs.size() + " DeliveryNPCs!");
  }

  public static void add(String id, Location location) {
    npcs.add(new DeliveryNPC(id, location));
    List<String> list = CONFIG.getStringList("delivery-npcs");
    list.add(BukkitUtils.serializeLocation(location) + "; " + id);
    CONFIG.set("delivery-npcs", list);
  }

  public static void remove(DeliveryNPC npc) {
    npcs.remove(npc);
    List<String> list = CONFIG.getStringList("delivery-npcs");
    list.remove(BukkitUtils.serializeLocation(npc.getLocation()) + "; " + npc.getId());
    CONFIG.set("delivery-npcs", list);

    npc.destroy();
  }

  public static DeliveryNPC getById(String id) {
    return npcs.stream().filter(npc -> npc.getId().equals(id)).findFirst().orElse(null);
  }

  public static List<DeliveryNPC> listNPCs() {
    return ImmutableList.copyOf(npcs);
  }
}
