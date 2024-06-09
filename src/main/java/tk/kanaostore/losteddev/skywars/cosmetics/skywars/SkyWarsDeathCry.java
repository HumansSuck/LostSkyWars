package tk.kanaostore.losteddev.skywars.cosmetics.skywars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.cosmetics.Cosmetic;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticRarity;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticServer;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticType;
import tk.kanaostore.losteddev.skywars.nms.Sound;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;

public class SkyWarsDeathCry extends Cosmetic {

  private String name;
  private boolean buyable;
  private int coins;
  private Sound sound;
  private float volume;
  private float pitch;
  private ItemStack icon;

  public SkyWarsDeathCry(int id, String name, ItemStack icon, CosmeticRarity rarity, boolean buyable, int coins, String sound, float volume, float pitch) {
    super(id, CosmeticServer.SKYWARS, CosmeticType.SKYWARS_DEATHCRY, rarity);
    this.name = name;
    this.buyable = buyable;
    this.coins = coins;
    this.sound = Sound.valueOf(sound);
    this.volume = volume;
    this.pitch = pitch;
    this.icon = icon;
  }

  public boolean isValid() {
    return sound != null;
  }

  public boolean canBeSold() {
    return buyable;
  }

  public Sound getSound() {
    return sound;
  }

  public float getVolume() {
    return volume;
  }

  public float getPitch() {
    return pitch;
  }

  @Override
  public boolean canBeFoundInBox(Player player) {
    return true;
  }

  @Override
  public String getName() {
    return Language.options$cosmetic$deathcry + this.name;
  }

  public String getRawName() {
    return this.name;
  }

  @Override
  public ItemStack getIcon() {
    return this.getIcon("§a");
  }

  public ItemStack getIcon(String colorDisplay, String... lores) {
    ItemStack cloned = this.icon.clone();
    ItemMeta meta = cloned.getItemMeta();
    meta.addItemFlags(ItemFlag.values());
    meta.setDisplayName(colorDisplay + meta.getDisplayName());
    List<String> list = new ArrayList<>();
    if (meta.getLore() != null) {
      list.addAll(meta.getLore());
    }
    list.addAll(Arrays.asList(lores));
    meta.setLore(list);
    cloned.setItemMeta(meta);
    return cloned;
  }

  public int getCoins() {
    return coins;
  }

  public static final LostLogger LOGGER = Main.LOGGER.getModule("DeathCries");
  private static final ConfigUtils CONFIG = ConfigUtils.getConfig("deathcries");

  public static void setupDeathCries() {
    for (String key : CONFIG.getKeys(false)) {
      ConfigurationSection sec = CONFIG.getSection(key);
      int id = sec.getInt("id");
      String name = sec.getString("name");
      ItemStack icon = BukkitUtils.deserializeItemStack(sec.getString("icon"));
      CosmeticRarity rarity = CosmeticRarity.fromName(sec.getString("rarity"));
      boolean buyable = sec.getBoolean("buyable");
      int price = sec.getInt("price");
      String sound = sec.getString("sound").toUpperCase();
      float volume = (float) sec.getDouble("volume");
      float pitch = (float) sec.getDouble("pitch");

      SkyWarsDeathCry cry = new SkyWarsDeathCry(id, name, icon, rarity, buyable, price, sound, volume, pitch);
      if (!cry.isValid()) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(),
            () -> LOGGER.log(LostLevel.WARNING, "Invalid Sound \"" + sound + "\" on DeathCry \"" + key + "\""));
        continue;
      }

      CosmeticServer.SKYWARS.addCosmetic(cry);
    }
  }

  public static void createNew(Object[] arr) {
    // 0 = name
    // 1 = key
    // 2 = sound
    // 3 = volume
    // 4 = pitch
    // 5 = price
    // 6 = rarity
    // 7 = buyable
    int id = 1;
    String key = (String) arr[1];
    String sound = ((Sound) arr[2]).name();
    float volume = (float) arr[3];
    float pitch = (float) arr[4];
    int price = (int) arr[5];
    CosmeticRarity rarity = (CosmeticRarity) arr[6];
    boolean buyable = (boolean) arr[7];
    CONFIG.createSection(key);
    ConfigurationSection sec = CONFIG.getSection(key);

    Cosmetic c = CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_DEATHCRY).stream().filter(cosmetic -> cosmetic.getId() == 1).findAny().orElse(null);
    while (c != null) {
      id++;
      int copyId = id;
      c = CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_DEATHCRY).stream().filter(cosmetic -> cosmetic.getId() == copyId).findAny().orElse(null);
    }
    sec.set("id", id);
    sec.set("name", (String) arr[0]);
    sec.set("price", price);
    sec.set("rarity", rarity.name());
    sec.set("buyable", buyable);
    sec.set("sound", sound);
    sec.set("volume", volume);
    sec.set("pitch", pitch);
    sec.set("icon", "BARRIER : 1 : display=" + arr[0] + " : lore=&7Change that on deathcries.yml\\n ");
    CONFIG.save();
    
    CosmeticServer.SKYWARS.addCosmetic(new SkyWarsDeathCry(id, (String) arr[0],
        BukkitUtils.deserializeItemStack("BARRIER : 1 : display=" + arr[0] + " : lore=&7Change that on deathcries.yml\\n "), rarity, buyable, price, sound, volume, pitch));
  }
}
