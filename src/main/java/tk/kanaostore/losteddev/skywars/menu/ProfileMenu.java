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
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.level.Level;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigAction;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigItem;
import tk.kanaostore.losteddev.skywars.menu.api.PlayerMenu;
import tk.kanaostore.losteddev.skywars.menu.profile.LevelingMenu;
import tk.kanaostore.losteddev.skywars.menu.profile.SettingsMenu;
import tk.kanaostore.losteddev.skywars.menu.profile.StatisticsMenu;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.rank.Rank;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

public class ProfileMenu extends PlayerMenu {

  private static final ConfigMenu config = ConfigMenu.getByName("profile");

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
              if (menu.equalsIgnoreCase("settings")) {
                new SettingsMenu(player);
              } else if (menu.equalsIgnoreCase("statistics")) {
                new StatisticsMenu(player);
              } else if (menu.equalsIgnoreCase("leveling")) {
                new LevelingMenu(player);
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

  public ProfileMenu(Player player) {
    super(player, config.getTitle(), config.getRows());
    Account account = Database.getInstance().getAccount(player.getUniqueId());

    for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
      if (entry.getKey() >= 0 && entry.getKey() < this.getInventory().getSize()) {
        String stack = entry.getValue().getStack();

        // PLAYER INFO
        stack = stack.replace("{rank}", Rank.getRank(player).getColoredName());
        stack = stack.replace("{dusts}", StringUtils.formatNumber(account.getMysteryDusts()));

        // LEVELING
        Level level = Level.getByLevel(account.getLevel());
        double currentExp = account.getExp();
        double needExp = level.getNext() == null ? 0.0 : level.getNext().getExp();
        double untilNextLevel = level.getExperienceUntil(account.getExp());
        stack = stack.replace("{level}", StringUtils.formatNumber(account.getLevel()));
        stack = stack.replace("{until}", StringUtils.formatNumber(untilNextLevel));
        stack = stack.replace("{level_progress}", account.makeProgressBar(false));
        stack = stack.replace("{level_percentage}", currentExp >= needExp ? "100%" : (int) ((currentExp * 100.0) / needExp) + "%");

        this.setItem(entry.getKey(), BukkitUtils.putProfileOnSkull(player, BukkitUtils.deserializeItemStack(stack)));
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
