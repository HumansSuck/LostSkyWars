package tk.kanaostore.losteddev.skywars.cosmetics.skywars;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsServer;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsState;
import tk.kanaostore.losteddev.skywars.cosmetics.Cosmetic;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticRarity;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticServer;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticType;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.perks.ArrowRecovery;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.perks.BlazingArrows;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.perks.Bulldozer;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.perks.DecisiveStrike;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.perks.EnderMastery;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.perks.Juggernaut;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.perks.Knowledge;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.perks.LuckyCharm;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.perks.Nourishment;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.perks.VoidMaster;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsType;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;

public abstract class SkyWarsPerk extends Cosmetic implements Listener {

  private String name;
  private String permission;
  private ItemStack icon;
  private int coins;

  public SkyWarsPerk(int id, String name, CosmeticRarity rarity, String permission, ItemStack icon, int coins) {
    super(id, CosmeticServer.SKYWARS, CosmeticType.SKYWARS_PERK, rarity);
    this.name = name;
    this.permission = permission;
    this.icon = icon;
    this.coins = coins;
  }

  public void register(Plugin plugin) {
    CosmeticServer.SKYWARS.addCosmetic(this);
    if (plugin != null) {
      Bukkit.getPluginManager().registerEvents(this, plugin);
    }
  }

  public boolean canBeSold() {
    return coins > 0;
  }

  public boolean isAbleToUse(Player player) {
    if (player == null) {
      return false;
    }

    Account account = Database.getInstance().getAccount(player.getUniqueId());
    SkyWarsServer server = account.getServer();

    boolean able = true;
    if (server == null || server.getState() != SkyWarsState.INGAME || server.isSpectator(player)) {
      able = false;
    }

    if (Language.options$ranked$freekitsandperks ? !has(account) : !hasByPermission(player) || !has(account)) {
      able = false;
    }

    if (server != null && server.getType().getIndex() != this.getMode()) {
      able = false;
    }

    account = null;
    server = null;
    return able;
  }

  @Override
  public boolean has(Account account, int mode) {
    if (Language.options$ranked$freekitsandperks) {
      if (mode == 3) {
        return true;
      }
    }

    return super.has(account, mode);
  }

  @Override
  public boolean canBeFoundInBox(Player player) {
    return Language.options$ranked$freekitsandperks ? this.getMode() != 3 && (!isPermissible() || hasByPermission(player)) : (!isPermissible() || hasByPermission(player));
  }

  public boolean isPermissible() {
    return !this.permission.isEmpty() && !this.permission.equals("none");
  }

  public boolean hasByPermission(Player player) {
    return !isPermissible() || player.hasPermission(this.permission);
  }

  @Override
  public String getName() {
    return Language.options$cosmetic$perk + this.name;
  }

  public String getRawName() {
    return this.name;
  }

  public String getPermission() {
    return this.permission;
  }

  @Override
  public ItemStack getIcon() {
    return this.getIcon("§a");
  }

  public ItemStack getIcon(String colorDisplay, String... lores) {
    ItemStack cloned = icon.clone();
    ItemMeta meta = cloned.getItemMeta();
    meta.addItemFlags(ItemFlag.values());
    meta.setDisplayName(colorDisplay + meta.getDisplayName());
    List<String> list = new ArrayList<>();
    list.addAll(meta.getLore());
    list.addAll(Arrays.asList(lores));
    meta.setLore(list);
    cloned.setItemMeta(meta);
    return cloned;
  }

  public int getCoins() {
    return coins;
  }

  public boolean isAble(SkyWarsType type) {
    return true;
  }

  public static final LostLogger LOGGER = Main.LOGGER.getModule("Perks");
  private static final ConfigUtils CONFIG = ConfigUtils.getConfig("perks");

  public static void setupPerks() {
    // ArrowRecovery
    new ArrowRecovery(1);
    new ArrowRecovery(2);
    if (CONFIG.getBoolean("arrowrecovery.ranked")) {
      new ArrowRecovery(3);
    }

    // Blazingarrows
    new BlazingArrows(1);
    new BlazingArrows(2);
    if (CONFIG.getBoolean("blazingarrow.ranked")) {
      new BlazingArrows(3);
    }

    // Bulldozer
    new Bulldozer(1);
    new Bulldozer(2);
    if (CONFIG.getBoolean("bulldozer.ranked")) {
      new Bulldozer(3);
    }

    // Ender Mastery (50% ender pearl damage)
    checkIfAbsent("endermastery");
    new EnderMastery(1);
    new EnderMastery(2);
    if (CONFIG.getBoolean("endermastery.ranked")) {
      new EnderMastery(3);
    }

    // Juggernaut (regen I for 5 seconds)
    checkIfAbsent("juggernaut");
    new Juggernaut(1);
    new Juggernaut(2);
    if (CONFIG.getBoolean("juggernaut.ranked")) {
      new Juggernaut(3);
    }
    
    // Knowledge (every kill gain 1 EXP Level)
    checkIfAbsent("knowledge");
    new Knowledge(1);
    new Knowledge(2);
    if (CONFIG.getBoolean("knowledge.ranked")) {
      new Knowledge(3);
    }

    // Nourishment (every kill gains full food and saturation)
    checkIfAbsent("nourishment");
    new Nourishment(1);
    new Nourishment(2);
    if (CONFIG.getBoolean("nourishment.ranked")) {
      new Nourishment(3);
    }

    // Lucky Charm (5% chance to get a Golden Apple after a kill)
    checkIfAbsent("luckycharm");
    new LuckyCharm(1);
    new LuckyCharm(2);
    if (CONFIG.getBoolean("luckycharm.ranked")) {
      new LuckyCharm(3);
    }

    // Void Master (10% chance to get an enderpearl after throwing a player in the void)
    checkIfAbsent("voidmaster");
    new VoidMaster(1);
    new VoidMaster(2);
    if (CONFIG.getBoolean("voidmaster.ranked")) {
      new VoidMaster(3);
    }

    // Decisive Strike (10% chance to when you're about to die got 0,5 hearts instead of dying)
    checkIfAbsent("decisivestrike");
    DecisiveStrike st = new DecisiveStrike(1);
    new DecisiveStrike(2);
    if (CONFIG.getBoolean("decisivestrike.ranked")) {
      new DecisiveStrike(3);
    }
    
    decisiveStrike = String.valueOf(st.getId());
  }
  
  private static String decisiveStrike;
  
  public static boolean isDecisiveStrike(Player player, int mode) {
    DecisiveStrike ds = Cosmetic.findFrom(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_PERK, mode, decisiveStrike, DecisiveStrike.class);
    if (ds != null) {
      return ds.isAbleToUse(player) && ThreadLocalRandom.current().nextInt(100) < ds.getPercentage();
    }
    
    return false;
  }

  private static void checkIfAbsent(String key) {
    if (CONFIG.contains(key)) {
      return;
    }

    try {
      FileConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(Main.getInstance().getResource("perks.yml"), "UTF-8"));
      for (String dataKey : config.getConfigurationSection(key).getKeys(false)) {
        CONFIG.set(key + "." + dataKey, config.get(key + "." + dataKey));
      }
    } catch (UnsupportedEncodingException ex) {
      ex.printStackTrace();
    }
  }
}
