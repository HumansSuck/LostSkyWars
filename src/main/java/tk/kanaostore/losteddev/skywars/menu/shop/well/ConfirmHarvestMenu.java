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
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

public class ConfirmHarvestMenu extends PlayerMenu {

  private static final ConfigMenu config = ConfigMenu.getByName("confirmbuy");

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
              if (menu.equalsIgnoreCase("shop")) {
                new SoulHarvestersMenu(player, back);
              } else if (menu.equalsIgnoreCase("buy")) {
                if (account.getInt("coins") < price) {
                  new SoulHarvestersMenu(player, back);
                  return;
                }

                account.removeStat("coins", price);
                account.addStat("souls", amount);
                if (account.getInt("souls") > account.getContainers("account").get("sw_maxsouls").getAsInt()) {
                  account.getContainers("skywars").get("souls").set(account.getContainers("account").get("sw_maxsouls").getAsInt());
                }
                player.sendMessage(StringUtils.formatColors(config.getAsString("buy").replace("{name}", name)));
                player.closeInventory();
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
  private String name;
  private int price, amount;
  private Map<ItemStack, ConfigAction> map = new HashMap<>();

  public ConfirmHarvestMenu(Player player, String name, int price, int amount, boolean back) {
    super(player, config.getTitle(), config.getRows());
    this.name = name;
    this.amount = amount;
    this.price = price;
    this.back = back;

    for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
      if (entry.getKey() >= 0 && entry.getKey() < this.getInventory().getSize()) {
        String stack = entry.getValue().getStack();

        stack = stack.replace("{name}", name);
        stack = stack.replace("{price}", StringUtils.formatNumber(price));

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
    name = null;
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
