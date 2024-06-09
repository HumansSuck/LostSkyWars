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
import tk.kanaostore.losteddev.skywars.well.WellUpgrade;

public class SoulUpgradesMenu extends PlayerMenu {

  private static final ConfigMenu config = ConfigMenu.getByName("wellupgrades");

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

              if (menu.equalsIgnoreCase("soulwin")) {
                WellUpgrade nextW = WellUpgrade.getNextWin(account.getContainers("account").get("sw_soulswin").getAsInt());
                if (nextW != null && coins >= nextW.getPrice()) {
                  account.removeStat("coins", nextW.getPrice());
                  account.getContainers("account").get("sw_soulswin").set(nextW.getAmount());
                  player.sendMessage(StringUtils.formatColors(config.getAsString("buy").replace("{name}", nextW.getName())));
                  new SoulUpgradesMenu(player, back);
                }
              } else if (menu.equalsIgnoreCase("soulmax")) {
                WellUpgrade nextM = WellUpgrade.getNextMax(account.getContainers("account").get("sw_maxsouls").getAsInt());
                if (nextM != null && coins >= nextM.getPrice()) {
                  account.removeStat("coins", nextM.getPrice());
                  account.getContainers("account").get("sw_maxsouls").set(nextM.getAmount());
                  player.sendMessage(StringUtils.formatColors(config.getAsString("buy").replace("{name}", nextM.getName())));
                  new SoulUpgradesMenu(player, back);
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

  public SoulUpgradesMenu(Player player, boolean back) {
    super(player, config.getTitle(), config.getRows());
    Account account = Database.getInstance().getAccount(player.getUniqueId());
    this.back = back;
    int coins = account.getInt("coins");

    for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
      if (entry.getKey() >= 0 && entry.getKey() < this.getInventory().getSize()) {
        String stack = entry.getValue().getStack();
        String action = entry.getValue().getAction().getValue();

        WellUpgrade nextW = WellUpgrade.getNextWin(account.getContainers("account").get("sw_soulswin").getAsInt());
        WellUpgrade nextM = WellUpgrade.getNextMax(account.getContainers("account").get("sw_maxsouls").getAsInt());
        if ((action.equalsIgnoreCase("soulwin") && nextW == null) || (action.equalsIgnoreCase("soulmax") && nextM == null)) {
          this.setItem(entry.getKey(), BukkitUtils.deserializeItemStack(config.getAsString("max-item")));
          continue;
        }

        // WINUPGRADE
        if (nextW != null) {
          stack = stack.replace("{color1}", (coins < nextW.getPrice()) ? "&c" : "&a");
          stack = stack.replace("{name1}", nextW.getName());
          stack = stack.replace("{amount1}", StringUtils.formatNumber(nextW.getAmount()));
          stack = stack.replace("{price1}", StringUtils.formatNumber(nextW.getPrice()));
          stack = stack.replace("{message1}", config.getAsString((coins >= nextW.getPrice()) ? "have-enough-coins" : "dont-have-enough-coins"));
        }

        // MAXUPGRADE
        if (nextM != null) {
          stack = stack.replace("{color2}", (coins < nextM.getPrice()) ? "&c" : "&a");
          stack = stack.replace("{name2}", nextM.getName());
          stack = stack.replace("{amount2}", StringUtils.formatNumber(nextM.getAmount()));
          stack = stack.replace("{price2}", StringUtils.formatNumber(nextM.getPrice()));
          stack = stack.replace("{message2}", config.getAsString((coins >= nextM.getPrice()) ? "have-enough-coins" : "dont-have-enough-coins"));
        }

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
