package tk.kanaostore.losteddev.skywars.cosmetics.skywars;

import java.util.Arrays;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.cosmetics.Cosmetic;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticRarity;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticServer;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticType;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;

@SuppressWarnings("deprecation")
public class SkyWarsCage extends Cosmetic {

  private String name;
  private String permission;
  private ItemStack icon;
  private JSONArray locations;

  public SkyWarsCage(int id, CosmeticRarity rarity, String name, String permission, ItemStack icon, JSONArray locations) {
    super(id, CosmeticServer.SKYWARS, CosmeticType.SKYWARS_CAGE, rarity);
    this.name = name;
    this.permission = permission;
    this.icon = icon;
    this.locations = locations;
  }

  public void apply(Location location) {
    for (Object object : this.locations) {
      if (object instanceof String) {
        String offset = (String) object;
        double offsetX = Double.parseDouble(offset.split("; ")[0]);
        double offsetY = Double.parseDouble(offset.split("; ")[1]);
        double offsetZ = Double.parseDouble(offset.split("; ")[2]);
        Material blockMaterial = Material.matchMaterial(offset.split("; ")[3]);
        byte data = Byte.parseByte(offset.split("; ")[4]);

        Block block = location.clone().add(offsetX, offsetY, offsetZ).getBlock();
        block.setType(blockMaterial);
        BlockState state = block.getState();
        state.getData().setData(data);
        state.update(true);
      }
    }
  }

  public boolean canBeSold() {
    return false;
  }

  @Override
  public boolean canBeFoundInBox(Player player) {
    return !isPermissible() || this.hasByPermission(player);
  }

  public boolean isPermissible() {
    return !this.permission.isEmpty() && !this.permission.equals("none");
  }

  public boolean hasByPermission(Player player) {
    return !isPermissible() || player.hasPermission(this.permission);
  }

  @Override
  public String getName() {
    return Language.options$cosmetic$cage + name;
  }

  public String getRawName() {
    return name;
  }

  @Override
  public int getCoins() {
    return 0;
  }

  public String getPermission() {
    return permission;
  }

  @Override
  public ItemStack getIcon() {
    return this.getIcon("§a");
  }

  public ItemStack getIcon(String colorDisplay, String... lores) {
    ItemStack cloned = icon.clone();
    ItemMeta meta = cloned.getItemMeta();
    meta.setDisplayName(colorDisplay + meta.getDisplayName());
    meta.setLore(Arrays.asList(lores));
    cloned.setItemMeta(meta);
    return cloned;
  }

  public static final LostLogger LOGGER = Main.LOGGER.getModule("Cages");
  private static final ConfigUtils CONFIG = ConfigUtils.getConfig("cages");

  public static void setupCages() {
    for (String key : CONFIG.getSection("cages").getKeys(false)) {
      ConfigurationSection section = CONFIG.getSection("cages." + key);

      int id = section.getInt("id");
      String name = section.getString("name");
      CosmeticRarity rarity = CosmeticRarity.fromName(section.getString("rarity"));
      String permission = section.getString("permission");
      String icon = section.getString("icon");
      JSONArray locations = null;
      try {
        locations = (JSONArray) new JSONParser().parse(section.getString("data"));
      } catch (ParseException ex) {
        LOGGER.log(LostLevel.WARNING, "Invalid CageData \"" + key + "\": ", ex);
        continue;
      }

      CosmeticServer.SKYWARS.addCosmetic(new SkyWarsCage(id, rarity, name, permission, BukkitUtils.deserializeItemStack(icon), locations));
    }
  }

  public static void createNew(Object[] arr) {
    // 0 = name
    // 1 = key
    // 2 = array
    // 3 = permission
    // 4 = rarity
    int id = 1;
    String name = (String) arr[0];
    String key = (String) arr[1];
    JSONArray array = (JSONArray) arr[2];
    String permission = (String) arr[3];
    CosmeticRarity rarity = (CosmeticRarity) arr[4];

    CONFIG.createSection("cages." + key);
    ConfigurationSection sec = CONFIG.getSection("cages." + key);
    Cosmetic c = CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_CAGE).stream().filter(cosmetic -> cosmetic.getId() == 1).findAny().orElse(null);
    while (c != null) {
      id++;
      int copyId = id;
      c = CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_CAGE).stream().filter(cosmetic -> cosmetic.getId() == copyId).findAny().orElse(null);
    }
    sec.set("id", id);
    sec.set("name", name);
    sec.set("rarity", rarity.name());
    sec.set("permission", permission);
    sec.set("icon", "BARRIER : 1 : display=" + name + " Cage (change that in cages.yml)");
    sec.set("data", array.toString());
    CONFIG.save();
    CosmeticServer.SKYWARS.addCosmetic(
        new SkyWarsCage(id, rarity, name, permission, BukkitUtils.deserializeItemStack("BARRIER : 1 : display=" + name + " Cage : lore=&7Change that on cages.yml"), array));
  }

  @SuppressWarnings("unchecked")
  public static JSONArray createCage(Location location) {
    JSONArray cageData = new JSONArray();
    location = location.getBlock().getLocation().clone().add(0.5, -1, 0.5);
    for (double y = 0; y <= 4; y++) {
      for (double x = -1; x <= 1; x++) {
        for (double z = -1; z <= 1; z++) {
          if (y > 0 && y < 4) {
            if (x == 0 && z == 0) {
              continue;
            }
          }

          Block block = location.clone().add(x, y, z).getBlock();
          if (block.getType() != Material.AIR) {
            cageData.add(x + "; " + y + "; " + z + "; " + block.getType().name() + "; " + block.getData());
          }
        }
      }
    }

    return cageData;
  }

  public static void def(Location location, boolean big) {
    location = location.clone();
    if (big) {
      location.add(0, -1, 0);
      Location[] downs = {location, location.clone().add(1, 0, 0), location.clone().add(-1, 0, 0), location.clone().add(0, 0, 1), location.clone().add(0, 0, -1),
          location.clone().add(1, 0, 1), location.clone().add(-1, 0, 1), location.clone().add(1, 0, -1), location.clone().add(-1, 0, -1)};
      for (Location down : downs) {
        down.getBlock().setType(Material.GLASS);
      }
      for (int i = 1; i < 4; i++) {
        location.add(0, 1, 0);
        Location[] uppers = {location.clone().add(2, 0, 0), location.clone().add(-2, 0, 0), location.clone().add(0, 0, 2), location.clone().add(0, 0, -2),
            location.clone().add(2, 0, 1), location.clone().add(2, 0, -1), location.clone().add(-2, 0, 1), location.clone().add(-2, 0, -1), location.clone().add(1, 0, 2),
            location.clone().add(-1, 0, -2), location.clone().add(1, 0, -2), location.clone().add(-1, 0, 2)};
        for (Location upper : uppers) {
          upper.getBlock().setType(Material.GLASS);
        }
      }
      location.add(0, 1, 0);
      downs = new Location[] {location, location.clone().add(1, 0, 0), location.clone().add(-1, 0, 0), location.clone().add(0, 0, 1), location.clone().add(0, 0, -1),
          location.clone().add(1, 0, 1), location.clone().add(-1, 0, 1), location.clone().add(1, 0, -1), location.clone().add(-1, 0, -1)};
      for (Location down : downs) {
        down.getBlock().setType(Material.GLASS);
      }

      return;
    }

    for (double y = 0; y <= 4; y++) {
      for (double x = -1; x <= 1; x++) {
        for (double z = -1; z <= 1; z++) {
          if (y > 0 && y < 4) {
            if (x > -1 && z < 1) {
              continue;
            }
          }

          location.clone().add(x, y, z).getBlock().setType(Material.GLASS);
        }
      }
    }
  }

  public static void remove(Location location, boolean big) {
    if (big) {
      location.add(0, -1, 0);
      Location[] downs = {location, location.clone().add(1, 0, 0), location.clone().add(-1, 0, 0), location.clone().add(0, 0, 1), location.clone().add(0, 0, -1),
          location.clone().add(1, 0, 1), location.clone().add(-1, 0, 1), location.clone().add(1, 0, -1), location.clone().add(-1, 0, -1)};
      for (Location down : downs) {
        down.getBlock().setType(Material.AIR);
      }
      for (int i = 1; i < 4; i++) {
        location.add(0, 1, 0);
        Location[] uppers = {location.clone().add(2, 0, 0), location.clone().add(-2, 0, 0), location.clone().add(0, 0, 2), location.clone().add(0, 0, -2),
            location.clone().add(2, 0, 1), location.clone().add(2, 0, -1), location.clone().add(-2, 0, 1), location.clone().add(-2, 0, -1), location.clone().add(1, 0, 2),
            location.clone().add(-1, 0, -2), location.clone().add(1, 0, -2), location.clone().add(-1, 0, 2)};
        for (Location upper : uppers) {
          upper.getBlock().setType(Material.AIR);
        }
      }
      location.add(0, 1, 0);
      downs = new Location[] {location, location.clone().add(1, 0, 0), location.clone().add(-1, 0, 0), location.clone().add(0, 0, 1), location.clone().add(0, 0, -1),
          location.clone().add(1, 0, 1), location.clone().add(-1, 0, 1), location.clone().add(1, 0, -1), location.clone().add(-1, 0, -1)};
      for (Location down : downs) {
        down.getBlock().setType(Material.AIR);
      }

      return;
    }

    for (double y = 0; y <= 4; y++) {
      for (double x = -1; x <= 1; x++) {
        for (double z = -1; z <= 1; z++) {
          if (y > 0 && y < 4) {
            if (x == 0 && z == 0) {
              continue;
            }
          }

          location.clone().add(x, y, z).getBlock().setType(Material.AIR);
        }
      }
    }
  }
}
