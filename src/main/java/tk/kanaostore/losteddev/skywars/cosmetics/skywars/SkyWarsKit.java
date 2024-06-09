package tk.kanaostore.losteddev.skywars.cosmetics.skywars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.cosmetics.Cosmetic;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticRarity;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticServer;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticType;
import tk.kanaostore.losteddev.skywars.player.Account;

public abstract class SkyWarsKit extends Cosmetic {

  private String name;
  private String permission;
  private ItemStack icon;
  private int coins;

  public SkyWarsKit(int id, String name, CosmeticRarity rarity, String permission, ItemStack icon, int coins) {
    super(id, CosmeticServer.SKYWARS, CosmeticType.SKYWARS_KIT, rarity);
    this.name = name;
    this.permission = permission;
    this.icon = icon;
    this.coins = coins;
  }

  public abstract void apply(Player player);

  public abstract ItemStack[] getContents();

  public boolean canBeSold() {
    return coins > 0;
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
    return Language.options$cosmetic$kit + this.name;
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
}
