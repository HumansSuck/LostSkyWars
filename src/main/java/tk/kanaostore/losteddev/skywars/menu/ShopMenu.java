package tk.kanaostore.losteddev.skywars.menu;

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
import tk.kanaostore.losteddev.skywars.cosmetics.Cosmetic;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticServer;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticType;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsBalloon;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsCage;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsDeathCry;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.level.Level;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigAction;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigItem;
import tk.kanaostore.losteddev.skywars.menu.api.PlayerMenu;
import tk.kanaostore.losteddev.skywars.menu.shop.BalloonsMenu;
import tk.kanaostore.losteddev.skywars.menu.shop.CagesMenu;
import tk.kanaostore.losteddev.skywars.menu.shop.DeathCriesMenu;
import tk.kanaostore.losteddev.skywars.menu.shop.KitsAndPerksMenu;
import tk.kanaostore.losteddev.skywars.menu.shop.SoulWellMenu;
import tk.kanaostore.losteddev.skywars.menu.shop.SymbolsMenu;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

public class ShopMenu extends PlayerMenu {

  private static final ConfigMenu config = ConfigMenu.getByName("shop");

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
              if (menu.equalsIgnoreCase("kitsandperks")) {
                new KitsAndPerksMenu(player);
              } else if (menu.equalsIgnoreCase("soulwell")) {
                new SoulWellMenu(player, true);
              } else if (menu.equalsIgnoreCase("cages")) {
                new CagesMenu(player);
              } else if (menu.equalsIgnoreCase("deathcry")) {
                new DeathCriesMenu(player);
              } else if (menu.equalsIgnoreCase("balloon")) {
                new BalloonsMenu(player);
              } else if (menu.equalsIgnoreCase("symbols")) {
                new SymbolsMenu(player);
              } else if (menu.equalsIgnoreCase("closeinv")) {
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

  private Map<ItemStack, ConfigAction> map = new HashMap<>();

  public ShopMenu(Player player) {
    super(player, config.getTitle(), config.getRows());
    Account account = Database.getInstance().getAccount(player.getUniqueId());

    for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
      if (entry.getKey() >= 0 && entry.getKey() < this.getInventory().getSize()) {
        String stack = entry.getValue().getStack();

        // Kits
        int max = CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_KIT).size(),
            amount = (int) CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_KIT).stream().filter(cosmetic -> cosmetic.has(account)).count(),
            percentage = (int) ((100.0 * amount) / max);
        Cosmetic c = account.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_KIT, 1);
        stack = stack.replace("{kits_has}", String.valueOf(amount));
        stack = stack.replace("{kits_max}", String.valueOf(max));
        stack = stack.replace("{kits_percentage}", percentage + "%");

        // Cages
        max = CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_CAGE).size();
        amount = (int) CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_CAGE).stream().filter(cosmetic -> cosmetic.has(account)).count();
        percentage = (int) ((100.0 * amount) / max);
        c = account.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_CAGE, 1);
        stack = stack.replace("{cages_has}", String.valueOf(amount));
        stack = stack.replace("{cages_max}", String.valueOf(max));
        stack = stack.replace("{cages_percentage}", percentage + "%");
        stack = stack.replace("{cages_current}", c == null || !(c instanceof SkyWarsCage) ? "Glass" : ((SkyWarsCage) c).getRawName());
        
        // DeathCries
        max = CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_DEATHCRY).size();
        amount = (int) CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_DEATHCRY).stream().filter(cosmetic -> cosmetic.has(account)).count();
        percentage = (int) ((100.0 * amount) / max);
        c = account.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_DEATHCRY, 1);
        stack = stack.replace("{cries_has}", String.valueOf(amount));
        stack = stack.replace("{cries_max}", String.valueOf(max));
        stack = stack.replace("{cries_percentage}", percentage + "%");
        stack = stack.replace("{cries_current}", c == null || !(c instanceof SkyWarsDeathCry) ? config.getAsString("empty") : ((SkyWarsDeathCry) c).getRawName());
        
        // Balloons
        max = CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_BALLON).size();
        amount = (int) CosmeticServer.SKYWARS.getByType(CosmeticType.SKYWARS_BALLON).stream().filter(cosmetic -> cosmetic.has(account)).count();
        percentage = (int) ((100.0 * amount) / max);
        c = account.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_BALLON, 1);
        stack = stack.replace("{balloons_has}", String.valueOf(amount));
        stack = stack.replace("{balloons_max}", String.valueOf(max));
        stack = stack.replace("{balloons_percentage}", percentage + "%");
        stack = stack.replace("{balloons_current}", c == null || !(c instanceof SkyWarsBalloon) ? config.getAsString("empty") : ((SkyWarsBalloon) c).getRawName());
        
        // Symbols
        Level level = Level.getByLevel(account.getLevel());
        double currentExp = account.getExp();
        double needExp = level.getNext() == null ? 0.0 : level.getNext().getExp();
        stack = stack.replace("{exp}", StringUtils.formatPerMil(currentExp));
        stack = stack.replace("{nextExp}", needExp != 0.0 ? StringUtils.formatPerMil(needExp) : "Max");
        stack = stack.replace("{level}", level.getLevel(account));
        stack = stack.replace("{progressBar}", "§8[ " + account.makeProgressBar(true) + " §8]");
        stack = stack.replace("{nextLevel}", needExp != 0.0 ? level.getNext().getLevel(account) : level.getLevel(account));
        stack = stack.replace("{display}", player.getDisplayName());
        
        // Stats
        stack = stack.replace("{souls}", account.getFormatted("souls"));

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
