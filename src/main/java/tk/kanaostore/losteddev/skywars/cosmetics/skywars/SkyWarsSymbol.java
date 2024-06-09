package tk.kanaostore.losteddev.skywars.cosmetics.skywars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tk.kanaostore.losteddev.skywars.cosmetics.Cosmetic;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticRarity;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticServer;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticType;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;

public class SkyWarsSymbol extends Cosmetic {

  private String name;
  private String symbol;
  private ItemStack icon;
  private int level;

  public SkyWarsSymbol(int id, String name, String symbol, ItemStack icon, int level) {
    super(id, CosmeticServer.SKYWARS, CosmeticType.SKYWARS_SYMBOL, CosmeticRarity.COMMON);
    this.name = name;
    this.symbol = symbol;
    this.icon = icon;
    this.level = level;
  }

  public String getSymbol() {
    return this.symbol;
  }
  
  public int getLevel() {
    return this.level;
  }

  @Override
  public void give(Account account, int mode) {}

  @Override
  public boolean has(Account account, int mode) {
    return account.getLevel() >= this.level;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getRawName() {
    return name;
  }

  @Override
  public int getCoins() {
    return 0;
  }

  @Override
  public boolean canBeFoundInBox(Player player) {
    return false;
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

  private static final ConfigUtils CONFIG = ConfigUtils.getConfig("symbols");

  public static void setupSymbols() {
    for (String key : CONFIG.getKeys(false)) {
      int id = CONFIG.getInt(key + ".id");
      String name = CONFIG.getString(key + ".name");
      int level = CONFIG.getInt(key + ".level");
      String symbol = CONFIG.getString(key + ".symbol");
      ItemStack icon = BukkitUtils.deserializeItemStack(CONFIG.getString(key + ".icon").replace("{symbol}", symbol));

      CosmeticServer.SKYWARS.addCosmetic(new SkyWarsSymbol(id, name, symbol, icon, level));
    }
  }
}
