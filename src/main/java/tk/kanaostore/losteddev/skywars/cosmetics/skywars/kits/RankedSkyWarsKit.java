package tk.kanaostore.losteddev.skywars.cosmetics.skywars.kits;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticRarity;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticServer;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsKit;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;

public class RankedSkyWarsKit extends SkyWarsKit {

  private ItemStack[] armor;
  private ItemStack[] content;

  public RankedSkyWarsKit(int id, String name, CosmeticRarity rarity, String permission, ItemStack icon, int coins, ItemStack[] armor, ItemStack[] content) {
    super(id, name, rarity, permission, icon, coins);
    this.armor = armor;
    this.content = content;
  }

  @Override
  public void apply(Player player) {
    player.getInventory().setArmorContents(this.armor);
    player.getInventory().addItem(this.content);
  }

  @Override
  public ItemStack[] getContents() {
    return content;
  }

  public static final LostLogger LOGGER = Main.LOGGER.getModule("RankedKits");

  public static void setupKits() {
    ConfigUtils cu = ConfigUtils.getConfig("rankedkits", "plugins/LostSkyWars/kits");
    Set<String> keys = cu.getKeys(false);
    LOGGER.log(LostLevel.INFO, "Keys: " + keys);

    for (String key : keys) {
      LOGGER.log(LostLevel.INFO, "Processing kit: " + key);

      int id = cu.getInt(key + ".id");
      String name = cu.getString(key + ".name");
      int price = cu.getInt(key + ".price");
      CosmeticRarity rarity = CosmeticRarity.fromName(cu.getString(key + ".rarity"));
      String permission = cu.getString(key + ".permission");
      ItemStack icon = BukkitUtils.deserializeItemStack(cu.getString(key + ".icon"));

      if (icon == null) {
        LOGGER.log(LostLevel.WARNING, "Invalid icon for kit \"" + name + "\"");
        continue;
      }

      List<ItemStack> list = new ArrayList<>();
      for (String armorStr : cu.getStringList(key + ".armor")) {
        ItemStack armorItem = BukkitUtils.deserializeItemStack(armorStr);
        if (armorItem != null) {
          list.add(armorItem);
        } else {
          LOGGER.log(LostLevel.WARNING, "Invalid armor item \"" + armorStr + "\" for kit \"" + name + "\"");
        }
      }

      ItemStack[] armor = list.toArray(new ItemStack[0]);
      if (armor.length != 4) {
        armor = null;
        LOGGER.log(LostLevel.WARNING, "Invalid armor list for kit \"" + name + "\"");
      }

      list.clear();
      for (String contentStr : cu.getStringList(key + ".content")) {
        ItemStack contentItem = BukkitUtils.deserializeItemStack(contentStr);
        if (contentItem != null) {
          list.add(contentItem);
        } else {
          LOGGER.log(LostLevel.WARNING, "Invalid content item \"" + contentStr + "\" for kit \"" + name + "\"");
        }
      }

      ItemStack[] content = list.toArray(new ItemStack[0]);

      LOGGER.log(LostLevel.INFO, "Adding kit: " + name);
      CosmeticServer.SKYWARS.addCosmetic(new RankedSkyWarsKit(id, name, rarity, permission, icon, price, armor, content));
      LOGGER.log(LostLevel.INFO, "Kit added: " + name);
    }
  }

  @Override
  public int getMode() {
    return 3;
  }
}
