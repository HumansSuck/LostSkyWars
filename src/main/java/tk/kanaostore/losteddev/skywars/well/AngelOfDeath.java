package tk.kanaostore.losteddev.skywars.well;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.util.EulerAngle;
import com.google.common.collect.ImmutableList;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.holograms.entity.IArmorStand;
import tk.kanaostore.losteddev.skywars.nms.NMS;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;

@SuppressWarnings("deprecation")
public class AngelOfDeath {
  
  private String id;
  private Location location;
  
  private List<IArmorStand> armorstands;
  
  public AngelOfDeath(String id, Location location) {
    this.id = id;
    this.location = location;
    if (!this.location.getChunk().isLoaded()) {
      this.location.getChunk().load(true);
    }
    
    this.spawn();
  }
  
  public void spawn() {
    ItemStack helmet = BukkitUtils.deserializeItemStack("SKULL_ITEM:1 : 1");
    ItemStack itemOnHand = BukkitUtils.deserializeItemStack("DIAMOND_SWORD : 1");

    ItemStack chestplate = BukkitUtils.deserializeItemStack("LEATHER_CHESTPLATE : 1 : color=BLACK");
    ItemStack leggings = BukkitUtils.deserializeItemStack("LEATHER_LEGGINGS : 1 : color=BLACK");
    ItemStack boots = BukkitUtils.deserializeItemStack("LEATHER_BOOTS : 1 : color=BLACK");

    this.armorstands = new ArrayList<>(19);
    this.armorstands.add(this.spawn(0, location.clone(), false, itemOnHand, helmet, chestplate, leggings, boots));
    this.armorstands.addAll(this.banners(location));
  }
  
  public void destroy() {
    this.id = null;
    this.location = null;
    this.armorstands.forEach(armorstand -> {
      if (armorstand != null) {
        armorstand.killEntity();
      }
    });
    this.armorstands.clear();
    this.armorstands = null;
  }
  
  public String getId() {
    return id;
  }
  
  public Location getLocation() {
    return location;
  }
  
  private List<IArmorStand> banners(Location location) {
    ItemStack banner = BukkitUtils.deserializeItemStack("BANNER : 1");
    BannerMeta meta = (BannerMeta) banner.getItemMeta();
    meta.setBaseColor(DyeColor.BLACK);
    banner.setItemMeta(meta);
    
    List<IArmorStand> list = new ArrayList<>();
    for (int id = 1; id < 19; id++) {
      list.add(this.spawn(id, location.clone(), true, null, banner, null, null, null));
    }
    
    return list;
  }

  private IArmorStand spawn(int id, Location location, boolean invisible, ItemStack itemOnHand, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
    float baseYaw = Math.round(location.getYaw()) * 256.0F / 360.0F;
    int nmsYaw = (int) baseYaw * 360 / 256;

    location = this.changeLocation(id, nmsYaw, location);

    IArmorStand armorstand = NMS.createArmorStand(location, "", null);
    armorstand.getEntity().setGravity(false);
    armorstand.getEntity().setSmall(false);
    armorstand.getEntity().setVisible(!invisible);
    armorstand.getEntity().setArms(true);

    HeadPosition.apply(armorstand.getEntity(), id);

    if (id == 0) {
      armorstand.getEntity().setItemInHand(itemOnHand);
      armorstand.getEntity().setHelmet(helmet);
      armorstand.getEntity().setChestplate(chestplate);
      armorstand.getEntity().setLeggings(leggings);
      armorstand.getEntity().setBoots(boots);
    } else {
      armorstand.getEntity().setHelmet(helmet);
    }
    
    return armorstand;
  }

  private Location changeLocation(int id, int nmsYaw, Location location) {
    if (id == 1) {
      Location local = location.clone().add(0.0, 0.0, 0.35001);
      location = rotate(location, Math.sqrt(this.square(location.getZ() - local.getZ())), Math.floorMod(180 + nmsYaw - 40, 360));
      location.add(0.0, -0.20001, 0.0);
    } else if (id == 2) {
      Location local = location.clone().add(0.0, 0.0, 0.35001);
      location = rotate(location, Math.sqrt(this.square(location.getZ() - local.getZ())), Math.floorMod(nmsYaw + 40, 360));
      location.add(0.0, -0.20001, 0.0);
    }

    else if (id == 3) {
      Location local = location.clone().add(0.0, 0.0, 0.65001);
      location = rotate(location, Math.sqrt(this.square(location.getZ() - local.getZ())), Math.floorMod(180 + nmsYaw + 36, 360));
      location.add(0.0, 1.25001, 0.0);
    } else if (id == 4) {
      Location local = location.clone().add(0.0, 0.0, 0.65001);
      location = rotate(location, Math.sqrt(this.square(location.getZ() - local.getZ())), Math.floorMod(nmsYaw - 36, 360));
      location.add(0.0, 1.25001, 0.0);
    }

    else if (id == 5) {
      Location local = location.clone().add(0.0, 0.0, 0.60001);
      location = rotate(location, Math.sqrt(this.square(location.getZ() - local.getZ())), Math.floorMod(180 + nmsYaw + 31, 360));
      location.add(0.0, 0.95001, 0.0);
    } else if (id == 6) {
      Location local = location.clone().add(0.0, 0.0, 0.60001);
      location = rotate(location, Math.sqrt(this.square(location.getZ() - local.getZ())), Math.floorMod(nmsYaw - 31, 360));
      location.add(0.0, 0.95001, 0.0);
    }

    else if (id == 7) {
      Location local = location.clone().add(0.0, 0.0, 2.05001);
      location = rotate(location, Math.sqrt(this.square(location.getZ() - local.getZ())), Math.floorMod(180 + nmsYaw + 22, 360));
      location.add(0.0, 1.17001, 0.0);
    } else if (id == 8) {
      Location local = location.clone().add(0.0, 0.0, 2.05001);
      location = rotate(location, Math.sqrt(this.square(location.getZ() - local.getZ())), Math.floorMod(nmsYaw - 22, 360));
      location.add(0.0, 1.17001, 0.0);
    }

    else if (id == 9) {
      Location local = location.clone().add(0.0, 0.0, 3.12001);
      location = rotate(location, Math.sqrt(this.square(location.getZ() - local.getZ())), Math.floorMod(180 + nmsYaw + 16, 360));
      location.add(0.0, 0.05001, 0.0);
    } else if (id == 10) {
      Location local = location.clone().add(0.0, 0.0, 3.12001);
      location = rotate(location, Math.sqrt(this.square(location.getZ() - local.getZ())), Math.floorMod(nmsYaw - 16, 360));
      location.add(0.0, 0.05001, 0.0);
    }

    else if (id == 11) {
      Location local = location.clone().add(0.0, 0.0, 4.20001);
      location = rotate(location, Math.sqrt(this.square(location.getZ() - local.getZ())), Math.floorMod(180 + nmsYaw + 16, 360));
      location.add(0.0, -0.73001, 0.0);
    } else if (id == 12) {
      Location local = location.clone().add(0.0, 0.0, 4.20001);
      location = rotate(location, Math.sqrt(this.square(location.getZ() - local.getZ())), Math.floorMod(nmsYaw - 16, 360));
      location.add(0.0, -0.73001, 0.0);
    }

    else if (id == 13) {
      Location local = location.clone().add(0.0, 0.0, 2.00001);
      location = rotate(location, Math.sqrt(this.square(location.getZ() - local.getZ())), Math.floorMod(180 + nmsYaw + 25, 360));
      location.add(0.0, 1.25001, 0.0);
    } else if (id == 14) {
      Location local = location.clone().add(0.0, 0.0, 2.00001);
      location = rotate(location, Math.sqrt(this.square(location.getZ() - local.getZ())), Math.floorMod(nmsYaw - 25, 360));
      location.add(0.0, 1.25001, 0.0);
    }

    else if (id == 15) {
      Location local = location.clone().add(0.0, 0.0, 2.55001);
      location = rotate(location, Math.sqrt(this.square(location.getZ() - local.getZ())), Math.floorMod(180 + nmsYaw + 22, 360));
      location.add(0.0, 1.05001, 0.0);
    } else if (id == 16) {
      Location local = location.clone().add(0.0, 0.0, 2.55001);
      location = rotate(location, Math.sqrt(this.square(location.getZ() - local.getZ())), Math.floorMod(nmsYaw - 22, 360));
      location.add(0.0, 1.05001, 0.0);
    }

    else if (id == 17) {
      Location local = location.clone().add(0.0, 0.0, 3.40001);
      location = rotate(location, Math.sqrt(this.square(location.getZ() - local.getZ())), Math.floorMod(180 + nmsYaw + 20, 360));
      location.add(0.0, 0.75001, 0.0);
    } else if (id == 18) {
      Location local = location.clone().add(0.0, 0.0, 3.40001);
      location = rotate(location, Math.sqrt(this.square(location.getZ() - local.getZ())), Math.floorMod(nmsYaw - 20, 360));
      location.add(0.0, 0.75001, 0.0);
    }

    return location;
  }

  private Location rotate(Location location, double add, int stop) {
    double angle = stop * 0.017453292519943295;
    double x = location.getX() + add * Math.cos(angle);
    double z = location.getZ() + add * Math.sin(angle);
    Location rotated = new Location(location.getWorld(), x, location.getY(), z);
    rotated.setYaw(location.getYaw());
    rotated.setPitch(location.getPitch());
    return rotated;
  }
  
  private double square(double toSquare) {
    return toSquare * toSquare;
  }
  
  public static final LostLogger LOGGER = Main.LOGGER.getModule("Angels");
  private static final ConfigUtils CONFIG = ConfigUtils.getConfig("locations");

  private static List<AngelOfDeath> npcs = new ArrayList<>();

  public static void setupAngels() {
    if (!CONFIG.contains("angels-of-death")) {
      CONFIG.set("angels-of-death", new ArrayList<>());
    }

    for (String serialized : CONFIG.getStringList("angels-of-death")) {
      if (serialized.split("; ").length > 6) {
        String id = serialized.split("; ")[6];

        npcs.add(new AngelOfDeath(id, BukkitUtils.deserializeLocation(serialized)));
      }
    }

    LOGGER.log(LostLevel.INFO, "Loaded " + npcs.size() + " Angels!");
  }

  public static void add(String id, Location location) {
    npcs.add(new AngelOfDeath(id, location));
    List<String> list = CONFIG.getStringList("angels-of-death");
    list.add(BukkitUtils.serializeLocation(location) + "; " + id);
    CONFIG.set("angels-of-death", list);
  }

  public static void remove(AngelOfDeath npc) {
    npcs.remove(npc);
    List<String> list = CONFIG.getStringList("angels-of-death");
    list.remove(BukkitUtils.serializeLocation(npc.getLocation()) + "; " + npc.getId());
    CONFIG.set("angels-of-death", list);

    npc.destroy();
  }

  public static AngelOfDeath getById(String id) {
    return npcs.stream().filter(npc -> npc.getId().equals(id)).findFirst().orElse(null);
  }
  
  public static AngelOfDeath getByLocation(Location location) {
    return npcs.stream().filter(npc -> npc.getLocation().getBlock().equals(location.getBlock())).findFirst().orElse(null);
  }

  public static List<AngelOfDeath> listNPCs() {
    return ImmutableList.copyOf(npcs);
  }

  private static enum HeadPosition {
      _1(-25.0F, 0.0F, -25.0F),
      _2(-25.0F, 0.0F, 25.0F),
      _3(-15.0F, -30.0F, -95.0F),
      _4(-15.0F, 30.0F, 95.0F),
      _5(-5.0F, -30.0F, -110.0F),
      _6(-5.0F, 30.0F, 110.0F),
      _7(-5.0F, -35.0F, -125.0F),
      _8(-5.0F, 35.0F, 125.0F),
      _9(-5.0F, -25.0F, -115.0F),
      _10(-5.0F, 25.0F, 115.0F),
      _11(5.0F, -5.0F, -105.0F),
      _12(5.0F, 5.0F, 105.0F),
      _13(30.0F, -7.0F, -200.0F),
      _14(30.0F, 7.0F, 200.0F),
      _15(30.0F, -15.0F, -190.0F),
      _16(30.0F, 15.0F, 190.0F),
      _17(25.0F, -22.0F, -190.0F),
      _18(25.0F, 22.0F, 190.0F);

    private float x;
    private float y;
    private float z;

    HeadPosition(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    public void apply(ArmorStand armorstand) {
      armorstand.setHeadPose(new EulerAngle(x, y, z));
    }

    public static void apply(ArmorStand armorstand, int id) {
      if (id == 0) {
        armorstand.setLeftArmPose(new EulerAngle(-90.0F, 0.0F, 0.0F));
        armorstand.setLeftLegPose(new EulerAngle(20.0F, 0.0F, -10.0F));
        armorstand.setRightLegPose(new EulerAngle(20.0F, 0.0F, 10.0F));
        return;
      }

      for (HeadPosition position : values()) {
        if (position.name().equals("_" + id)) {
          position.apply(armorstand);
          return;
        }
      }
    }
  }
}
