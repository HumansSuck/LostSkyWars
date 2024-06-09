package tk.kanaostore.losteddev.skywars.menu.shop.well;

import java.util.HashMap;
import java.util.List;
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

public class SoulWellSettingsMenu extends PlayerMenu {

  private static final ConfigMenu config = ConfigMenu.getByName("wellsettings");

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
              int rolls = account.getContainers("account").get("sw_wellroll").getAsInt();

              if (menu.equalsIgnoreCase("increase")) {
                if (rolls < 5) {
                  account.getContainers("account").get("sw_wellroll").set(rolls + 1);
                  new SoulWellSettingsMenu(player, back);
                }
              } else if (menu.equalsIgnoreCase("decrease")) {
                if (rolls > 1) {
                  account.getContainers("account").get("sw_wellroll").set(rolls - 1);
                  new SoulWellSettingsMenu(player, back);
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

  public SoulWellSettingsMenu(Player player, boolean back) {
    super(player, config.getTitle(), config.getRows());
    Account account = Database.getInstance().getAccount(player.getUniqueId());
    int rolls = account.getContainers("account").get("sw_wellroll").getAsInt();
    this.back = back;

    for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
      if (entry.getKey() >= 0 && entry.getKey() < this.getInventory().getSize()) {
        String stack = entry.getValue().getStack();

        this.setItem(entry.getKey(), BukkitUtils.deserializeItemStack(stack));
        this.map.put(this.getItem(entry.getKey()), entry.getValue().getAction());
      }
    }

    List<Integer> slots = config.getAsIntegerList("slots");
    for (int i = 0; i < 5; i++) {
      this.setItem(slots.get(i),
          BukkitUtils.deserializeItemStack(config.getAsString(rolls > i ? "full" : "empty").replace("{wells}", "" + rolls).replace("{s}", rolls > 1 ? "s" : "")));
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
