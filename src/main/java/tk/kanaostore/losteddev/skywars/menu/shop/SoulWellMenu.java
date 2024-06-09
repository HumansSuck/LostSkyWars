package tk.kanaostore.losteddev.skywars.menu.shop;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu;
import tk.kanaostore.losteddev.skywars.menu.ShopMenu;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigAction;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigItem;
import tk.kanaostore.losteddev.skywars.menu.api.PlayerMenu;
import tk.kanaostore.losteddev.skywars.menu.shop.well.RollSoulWellMenu;
import tk.kanaostore.losteddev.skywars.menu.shop.well.SoulHarvestersMenu;
import tk.kanaostore.losteddev.skywars.menu.shop.well.SoulUpgradesMenu;
import tk.kanaostore.losteddev.skywars.menu.shop.well.SoulWellSettingsMenu;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

public class SoulWellMenu extends PlayerMenu {

  private static final ConfigMenu config = ConfigMenu.getByName("well");

  @EventHandler
  public void onInventoryClick(InventoryClickEvent evt) {
    if (evt.getInventory().equals(getInventory())) {
      evt.setCancelled(true);

      if (evt.getWhoClicked() instanceof Player && evt.getWhoClicked().equals(player)) {
        ItemStack item = evt.getCurrentItem();
        Account account = Database.getInstance().getAccount(player.getUniqueId());
        if (account == null) {
          player.closeInventory();
          return;
        }

        if (evt.getClickedInventory() != null && evt.getClickedInventory().equals(evt.getInventory()) && item != null && item.getType() != Material.AIR) {
          ConfigAction action = map.get(item);
          if (action != null && !action.getType().equals("NOTHING")) {
            if (action.getType().equals("OPEN")) {
              String menu = action.getValue();
              if (menu.equalsIgnoreCase("roll")) {
                if (account.getInt("souls") < (10 * account.getContainers("account").get("sw_wellroll").getAsInt())) {
                  player.sendMessage(StringUtils.formatColors(config.getAsString("dont-have-enough-souls")));
                  return;
                }

                new RollSoulWellMenu(player, back, account.getContainers("account").get("sw_wellroll").getAsInt());
              } else if (menu.equalsIgnoreCase("harvesters")) {
                if (account.getInt("souls") >= account.getContainers("account").get("sw_maxsouls").getAsInt()) {
                  player.sendMessage(StringUtils.formatColors(config.getAsString("max-souls")));
                  return;
                }

                new SoulHarvestersMenu(player, back);
              } else if (menu.equalsIgnoreCase("upgrades")) {
                new SoulUpgradesMenu(player, back);
              } else if (menu.equalsIgnoreCase("settings")) {
                new SoulWellSettingsMenu(player, back);
              } else if (menu.equalsIgnoreCase("closeinv")) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> player.closeInventory());
              }
            } else {
              player.closeInventory();
              action.send(player);
            }
          } else if (evt.getSlot() == backItem) {
            new ShopMenu(player);
          }
        }
      }
    }
  }

  private int backItem = -1;
  private boolean back;
  private Map<ItemStack, ConfigAction> map = new HashMap<>();

  public SoulWellMenu(Player player, boolean back) {
    super(player, config.getTitle(), config.getRows());
    Account account = Database.getInstance().getAccount(player.getUniqueId());
    int cost = account.getContainers("account").get("sw_wellroll").getAsInt() * 10;
    this.back = back;

    for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
      if (entry.getKey() >= 0 && entry.getKey() < this.getInventory().getSize()) {
        String stack = entry.getValue().getStack();

        // ROLL
        stack = stack.replace("{color}", cost > account.getInt("souls") ? "&c" : "&a");
        stack = stack.replace("{cost}", StringUtils.formatNumber(cost));
        stack = stack.replace("{message}", cost > account.getInt("souls") ? config.getAsString("dont-have-enough-souls") : config.getAsString("have-enough-souls"));

        this.setItem(entry.getKey(), BukkitUtils.deserializeItemStack(stack));
        this.map.put(this.getItem(entry.getKey()), entry.getValue().getAction());
      }
    }

    if (back) {
      this.backItem = config.getAsInt("back-slot");
      this.setItem(backItem, BukkitUtils.deserializeItemStack(config.getAsString("back")));
    }

    this.open();
    this.register();
  }

  public void cancel() {
    map.clear();
    map = null;
    HandlerList.unregisterAll(this);
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent evt) {
    if (evt.getPlayer().equals(player)) {
      this.cancel();
    }
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent evt) {
    if (evt.getPlayer().equals(player) && evt.getInventory().equals(this.getInventory())) {
      this.cancel();
    }
  }
}
