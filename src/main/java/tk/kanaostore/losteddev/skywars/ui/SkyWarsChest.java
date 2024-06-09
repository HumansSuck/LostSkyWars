package tk.kanaostore.losteddev.skywars.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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
import tk.kanaostore.losteddev.skywars.world.WorldServer;

public class SkyWarsChest {

  private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
  private static final List<Integer> SLOTS = new ArrayList<>();

  static {
    for (int slot = 0; slot < 27; slot++) {
      SLOTS.add(slot);
    }
  }

  private WorldServer<?> server;
  private String serialized;
  private String chestType;
  
  private Hologram hologram = null;

  public SkyWarsChest(WorldServer<?> server, String serialized) {
    this.server = server;
    this.serialized = serialized;
    this.chestType = serialized.split("; ")[6];
  }
  
  public void update() {
    if (this.server.getType().equals(SkyWarsType.DUELS)) {
      return;
    }
    
    if (this.hologram != null) {
      Block block = this.getLocation().getBlock();
      if (!(block.getState() instanceof Chest)) {
        this.destroy();
        return;
      }
      
      NMS.playChestAction(this.getLocation(), true);
      this.hologram.updateLine(1, Language.game$hologram$chest.replace("{time}", new SimpleDateFormat("mm:ss").format((server.getTimer() - server.getEventTime(true)) * 1000)));
    }
  }
  
  public void createHologram() {
    if (this.server.getType().equals(SkyWarsType.DUELS)) {
      return;
    }
    
    if (this.hologram == null) {
      this.hologram = Holograms.createHologram(this.getLocation().add(0.5, -0.5, 0.5));
      this.hologram.withLine(Language.game$hologram$chest.replace("{time}", new SimpleDateFormat("mm:ss").format((server.getTimer() - server.getEventTime(true)) * 1000)));
    }
  }
  
  public void destroy() {
    if (this.server.getType().equals(SkyWarsType.DUELS)) {
      return;
    }
    
    if (this.hologram != null) {
      NMS.playChestAction(this.getLocation(), false);
      Holograms.removeHologram(this.hologram);
      this.hologram = null;
    }
  }

  public void setType(ChestType chestType) {
    this.chestType = chestType.getName();
  }

  public void fill() {
    ChestType type = ChestType.getByName(chestType);
    if (type == null) {
      type = ChestType.getFirst();
    }

    if (type != null) {
      type.fill(getLocation(), false);
    }
  }

  public void refill() {
    ChestType type = ChestType.getByName(chestType);
    if (type == null) {
      type = ChestType.getFirst();
    }

    if (type != null) {
      type.fill(getLocation(), true);
    }
  }

  public String getChestType() {
    return chestType;
  }

  public Location getLocation() {
    return BukkitUtils.deserializeLocation(serialized);
  }

  @Override
  public String toString() {
    return BukkitUtils.serializeLocation(getLocation()) + "; " + chestType;
  }

  public static class ChestType {

    private String name;
    private List<ChestItem> refill;
    private List<ChestItem> content;

    public ChestType(String name, List<ChestItem> refill, List<ChestItem> content) {
      this.name = name;
      this.refill = refill;
      this.content = content;
    }

    public void fill(Location location, boolean refill) {
      Block block = location.getBlock();
      if (block != null && block.getState() instanceof Chest) {
        Chest chest = (Chest) block.getState();

        Inventory inventory = chest.getInventory();
        inventory.clear();
        int index = 0;
        Collections.shuffle(SLOTS);
        for (ChestItem item : refill ? this.refill : this.content) {
          if (index >= 27) {
            break;
          }

          ItemStack apply = item.get();
          if (apply != null) {
            inventory.setItem(SLOTS.get(index++), apply);
          }
        }
      }
    }

    public String getName() {
      return name;
    }

    public static final LostLogger LOGGER = Main.LOGGER.getModule("ChestType");
    private static Map<String, ChestType> types = new HashMap<>();

    public static void setupTypes() {
      ConfigUtils cu = ConfigUtils.getConfig("chesttypes");
      for (String key : cu.getKeys(false)) {
        String name = cu.getString(key + ".name");
        List<String> items = cu.getStringList(key + ".content");
        List<String> refill = cu.getStringList(key + ".refill");
        List<ChestItem> citems = new ArrayList<>(items.size());
        List<ChestItem> ritems = new ArrayList<>(refill.size());

        for (String serialized : items) {
          try {
            citems.add(new ChestItem(BukkitUtils.deserializeItemStack(serialized.split(", ")[1]), Integer.parseInt(serialized.split(", ")[0])));
          } catch (Exception e) {
            LOGGER.log(LostLevel.WARNING, "Invalid ContentItem (name=\"" + name + "\", string=\"" + serialized + "\")");
          }
        }

        for (String serialized : refill) {
          try {
            ritems.add(new ChestItem(BukkitUtils.deserializeItemStack(serialized.split(", ")[1]), Integer.parseInt(serialized.split(", ")[0])));
          } catch (Exception e) {
            LOGGER.log(LostLevel.WARNING, "Invalid RefillItem (name=\"" + name + "\", string=\"" + serialized + "\")");
          }
        }

        items.clear();
        items = null;

        types.put(name.toLowerCase(), new ChestType(name.toLowerCase(), ritems, citems));
      }

      LOGGER.log(LostLevel.INFO, "Loaded " + types.size() + " chesttypes!");
    }

    public static ChestType getFirst() {
      return types.values().stream().findFirst().orElse(null);
    }

    public static ChestType getByName(String name) {
      return types.get(name.toLowerCase());
    }

    public static Collection<ChestType> listTypes() {
      return ImmutableList.copyOf(types.values());
    }
  }

  public static class ChestItem {

    private ItemStack item;
    private int percentage;

    public ChestItem(ItemStack item, int percentage) {
      this.item = item;
      this.percentage = percentage;
    }

    public ItemStack get() {
      if (RANDOM.nextInt(100) + 1 <= percentage) {
        return item;
      }

      return null;
    }
  }
}
