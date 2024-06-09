package tk.kanaostore.losteddev.skywars.menu.shop.kits;

import java.util.ArrayList;
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
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsKit;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.kits.InsaneSkyWarsKit;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.kits.NormalSkyWarsKit;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.kits.RankedSkyWarsKit;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigAction;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigItem;
import tk.kanaostore.losteddev.skywars.menu.api.PlayerMenu;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

@SuppressWarnings("deprecation")
public class ViewKitMenu extends PlayerMenu {

  private static final ConfigMenu config = ConfigMenu.getByName("viewkit");

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
                if (kit instanceof NormalSkyWarsKit) {
                  new NormalKitsMenu(player);
                } else if (kit instanceof InsaneSkyWarsKit) {
                  new InsaneKitsMenu(player);
                } else if (kit instanceof RankedSkyWarsKit) {
                  new RankedKitsMenu(player);
                }
              } else if (menu.equalsIgnoreCase("buy")) {
                if (kit.has(account)) {
                  player.sendMessage(StringUtils.formatColors(config.getAsString("already-unlocked")));
                  return;
                }
                
                if (account.getInt("coins") < kit.getCoins()) {
                  player.sendMessage(StringUtils.formatColors(config.getAsString("enough-coins")));
                  return;
                }
                
                new ConfirmKitMenu(player, kit, ViewKitMenu.class);
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

  private SkyWarsKit kit;
  private Map<ItemStack, ConfigAction> map = new HashMap<>();

  public ViewKitMenu(Player player, SkyWarsKit kit) {
    super(player, config.getTitle().replace("{kit}", kit.getRawName()), config.getRows());
    this.kit = kit;
    Account account = Database.getInstance().getAccount(player.getUniqueId());

    for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
      if (entry.getKey() >= 0 && entry.getKey() < this.getInventory().getSize()) {
        String stack = entry.getValue().getStack();
        if (stack.equalsIgnoreCase("{kit_icon}")) {
          this.setItem(entry.getKey(), kit.getIcon("§a"));
          this.map.put(this.getItem(entry.getKey()), entry.getValue().getAction());
          continue;
        }

        if (stack.equalsIgnoreCase("{buy}")) {
          if (kit.has(account)) {
            List<String> lore = new ArrayList<>();
            for (String string : config.getAsStringArray("description-unlocked")) {
              lore.add(StringUtils.formatColors(string).replace("{name}", kit.getRawName()).replace("{price}", StringUtils.formatNumber(kit.getCoins())).replace("{rarity}",
                  kit.getRarity().getName()));
            }
            ItemStack item = kit.getIcon("§a", lore.toArray(new String[lore.size()]));
            item.setType(Material.matchMaterial(config.getAsString("buy-material").toUpperCase()));
            item.setDurability((short) config.getAsInt("buy-cant"));
            lore.clear();
            lore = null;
            this.setItem(entry.getKey(), item);
            this.map.put(this.getItem(entry.getKey()), entry.getValue().getAction());
            continue;
          }

          boolean canBuy = kit.hasByPermission(player) && account.getInt("coins") >= kit.getCoins();
          List<String> lore = new ArrayList<>();
          for (String string : config.getAsStringArray("description-locked")) {
            lore.add(StringUtils.formatColors(string).replace("{name}", kit.getRawName()).replace("{price}", StringUtils.formatNumber(kit.getCoins())).replace("{rarity}",
                kit.getRarity().getName()));
          }
          ItemStack item = kit.getIcon(canBuy ? "§a" : "§c", lore.toArray(new String[lore.size()]));
          item.setType(Material.matchMaterial(config.getAsString("buy-material").toUpperCase()));
          item.setDurability((short) config.getAsInt("buy-can"));
          lore.clear();
          lore = null;
          this.setItem(entry.getKey(), item);
          this.map.put(this.getItem(entry.getKey()), entry.getValue().getAction());
          continue;
        }

        // COINS
        stack = stack.replace("{coins}", StringUtils.formatNumber(account.getInt("coins")));

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
    kit = null;
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
