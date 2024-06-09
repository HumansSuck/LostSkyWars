package tk.kanaostore.losteddev.skywars.menu.shop.well;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigAction;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigItem;
import tk.kanaostore.losteddev.skywars.menu.api.PlayerMenu;
import tk.kanaostore.losteddev.skywars.menu.shop.SoulWellMenu;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

public class SoulHarvestersMenu extends PlayerMenu {

  private static final ConfigMenu config = ConfigMenu.getByName("wellharvest");

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
              int coins = account.getInt("coins");

              if (menu.equalsIgnoreCase("pack1") || menu.equalsIgnoreCase("pack2") || menu.equalsIgnoreCase("pack3")) {
                if (coins >= config.getAsInt(menu + "-price")) {
                  new ConfirmHarvestMenu(player, config.getAsString(menu + "-name"), config.getAsInt(menu + "-price"), config.getAsInt(menu + "-amount"), back);
                }
              } else if (menu.equalsIgnoreCase("soulwell")) {
                new SoulWellMenu(player, back);
              }
            } else {
              player.closeInventory();
              action.send(player);
            }
          }
        }
      }
    }
  }

  private boolean back;
  private Map<ItemStack, ConfigAction> map = new HashMap<>();

  public SoulHarvestersMenu(Player player, boolean back) {
    super(player, config.getTitle(), config.getRows());
    Account account = Database.getInstance().getAccount(player.getUniqueId());
    this.back = back;
    int coins = account.getInt("coins");

    for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
      if (entry.getKey() >= 0 && entry.getKey() < this.getInventory().getSize()) {
        String stack = entry.getValue().getStack();

        // PACK 1
        boolean pack1 = coins >= config.getAsInt("pack1-price");
        stack = stack.replace("{color1}", pack1 ? "&a" : "&c");
        stack = stack.replace("{amount1}", StringUtils.formatNumber(config.getAsInt("pack1-amount")));
        stack = stack.replace("{price1}", StringUtils.formatNumber(config.getAsInt("pack1-price")));
        stack = stack.replace("{message1}", config.getAsString(pack1 ? "have-enough-coins" : "dont-have-enough-coins"));

        // PACK 2
        boolean pack2 = coins >= config.getAsInt("pack2-price");
        stack = stack.replace("{color2}", pack2 ? "&a" : "&c");
        stack = stack.replace("{amount2}", StringUtils.formatNumber(config.getAsInt("pack2-amount")));
        stack = stack.replace("{price2}", StringUtils.formatNumber(config.getAsInt("pack2-price")));
        stack = stack.replace("{message2}", config.getAsString(pack2 ? "have-enough-coins" : "dont-have-enough-coins"));

        // PACK 3
        boolean pack3 = coins >= config.getAsInt("pack3-price");
        stack = stack.replace("{color3}", pack3 ? "&a" : "&c");
        stack = stack.replace("{amount3}", StringUtils.formatNumber(config.getAsInt("pack3-amount")));
        stack = stack.replace("{price3}", StringUtils.formatNumber(config.getAsInt("pack3-price")));
        stack = stack.replace("{message3}", config.getAsString(pack3 ? "have-enough-coins" : "dont-have-enough-coins"));

        // COINS
        stack = stack.replace("{coins}", StringUtils.formatNumber(coins));

        this.setItem(entry.getKey(), BukkitUtils.deserializeItemStack(stack));
        this.map.put(this.getItem(entry.getKey()), entry.getValue().getAction());
      }
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
