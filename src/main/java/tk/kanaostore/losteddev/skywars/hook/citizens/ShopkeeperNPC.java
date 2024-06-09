package tk.kanaostore.losteddev.skywars.hook.citizens;

import static tk.kanaostore.losteddev.skywars.hook.CitizensHook.LOGGER;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import com.google.common.collect.ImmutableList;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.holograms.Hologram;
import tk.kanaostore.losteddev.skywars.holograms.Holograms;
import tk.kanaostore.losteddev.skywars.hook.CitizensHook;
import tk.kanaostore.losteddev.skywars.particles.ParticleEffect;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;

@SuppressWarnings("deprecation")
public class ShopkeeperNPC {

  private String id;
  private Location location;

  private NPC npc;
  private Hologram hologram;

  public ShopkeeperNPC(String id, Location location) {
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

    this.hologram = Holograms.createHologram(this.location.clone().add(0, 1.2, 0));
    List<String> lines = new ArrayList<>(Language.lobby$npcs$shopkeeper$holograms);
    Collections.reverse(lines);
    for (String line : lines) {
      this.hologram.withLine(line);
    }
    EntityType wither = null;
    for (EntityType type : EntityType.values()) {
      if (type.name().equals("WITHER_SKELETON")) {
        wither = type;
      }
    }
    boolean witherFound = wither != null;
    this.npc = CitizensHook.getRegistry().createNPC(wither == null ? EntityType.SKELETON : wither, "");
    this.npc.data().setPersistent("shopkeeper", true);
    this.npc.data().setPersistent(NPC.NAMEPLATE_VISIBLE_METADATA, false);
    this.npc.addTrait(new Trait("skeleton-weapon") {
      @Override
      public void onSpawn() {
        ((LivingEntity) this.npc.getEntity()).getEquipment().setItemInHand(BukkitUtils.deserializeItemStack("DIAMOND_SWORD"));
        if (!witherFound) {
          ((Skeleton) this.npc.getEntity()).setSkeletonType(SkeletonType.WITHER);
        }
      }
    });
    this.npc.spawn(location);
  }

  public void update() {
    Location location = this.getLocation().clone().add(0, 1.2, 0);
    ParticleEffect.FLAME.display(1.0F, 0.0F, 1.0F, 1.0F, 90, location, 16);
    ParticleEffect.SMOKE_NORMAL.display(1.0F, 0.0F, 1.0F, 0.7F, 90, location, 16);
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

  private static List<ShopkeeperNPC> npcs = new ArrayList<>();

  public static void setupShopkeeperNPCs() {
    if (!CONFIG.contains("shopkeeper-npcs")) {
      CONFIG.set("shopkeeper-npcs", new ArrayList<>());
    }

    for (String serialized : CONFIG.getStringList("shopkeeper-npcs")) {
      if (serialized.split("; ").length > 6) {
        String id = serialized.split("; ")[6];
        npcs.add(new ShopkeeperNPC(id, BukkitUtils.deserializeLocation(serialized)));
      }
    }

//    new BukkitRunnable() {
//      @Override
//      public void run() {
//        npcs.forEach(ShopkeeperNPC::update);
//      }
//    }.runTaskTimer(Main.getInstance(), 0, 10);
    LOGGER.log(LostLevel.INFO, "Loaded " + npcs.size() + " ShopkeeperNPCs!");
  }

  public static void add(String id, Location location) {
    npcs.add(new ShopkeeperNPC(id, location));
    List<String> list = CONFIG.getStringList("shopkeeper-npcs");
    list.add(BukkitUtils.serializeLocation(location) + "; " + id);
    CONFIG.set("shopkeeper-npcs", list);
  }

  public static void remove(ShopkeeperNPC npc) {
    npcs.remove(npc);
    List<String> list = CONFIG.getStringList("shopkeeper-npcs");
    list.remove(BukkitUtils.serializeLocation(npc.getLocation()) + "; " + npc.getId());
    CONFIG.set("shopkeeper-npcs", list);

    npc.destroy();
  }

  public static ShopkeeperNPC getById(String id) {
    return npcs.stream().filter(npc -> npc.getId().equals(id)).findFirst().orElse(null);
  }

  public static List<ShopkeeperNPC> listNPCs() {
    return ImmutableList.copyOf(npcs);
  }
}
