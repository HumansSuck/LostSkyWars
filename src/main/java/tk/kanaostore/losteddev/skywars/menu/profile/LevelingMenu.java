package tk.kanaostore.losteddev.skywars.menu.profile;

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
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.level.Level;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigAction;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigItem;
import tk.kanaostore.losteddev.skywars.menu.ProfileMenu;
import tk.kanaostore.losteddev.skywars.menu.api.PagedPlayerMenu;
import tk.kanaostore.losteddev.skywars.nms.Sound;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

public class LevelingMenu extends PagedPlayerMenu {

  private static final ConfigMenu config = ConfigMenu.getByName("leveling");

  @EventHandler
  public void onInventoryClick(InventoryClickEvent evt) {
    if (evt.getInventory().equals(getCurrentInventory())) {
      evt.setCancelled(true);

      if (evt.getWhoClicked() instanceof Player && evt.getWhoClicked().equals(player)) {
        ItemStack item = evt.getCurrentItem();
        Account account = Database.getInstance().getAccount(player.getUniqueId());
        if (account == null) {
          player.closeInventory();
          return;
        }

        if (evt.getClickedInventory() != null && evt.getClickedInventory().equals(evt.getInventory()) && item != null && item.getType() != Material.AIR) {
          Level level = this.level.get(item);
          if (evt.getSlot() == this.previousPage) {
            this.openPrevious();
          } else if (evt.getSlot() == this.nextPage) {
            this.openNext();
          } else if (level != null) {
            if (account.getLevel() >= level.getLevel()) {
              if (account.isLeveled(level.getLevel())) {
                Sound.VILLAGER_NO.play(player, 1.0f, 1.0f);
                player.sendMessage(StringUtils.formatColors(config.getAsString("claimed-warn")));
                return;
              }

              Sound.LEVEL_UP.play(player, 1.0f, 1.0f);
              account.addLeveling(level.getLevel());
              level.getReward().apply(account);
              player.sendMessage(StringUtils.formatColors(config.getAsString("claim-warn").replace("{level}", String.valueOf(level.getLevel()))));
              new LevelingMenu(player);
            } else {
              Sound.VILLAGER_NO.play(player, 1.0f, 1.0f);
              player.sendMessage(StringUtils.formatColors(config.getAsString("locked-warn")));
            }
          } else {
            ConfigAction action = actions.get(item);
            if (action != null && !action.getType().equals("NOTHING")) {
              if (action.getType().equals("OPEN")) {
                String menu = action.getValue();
                if (menu.equalsIgnoreCase("profile")) {
                  new ProfileMenu(player);
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
  }

  private Map<ItemStack, ConfigAction> actions = new HashMap<>();
  private Map<ItemStack, Level> level = new HashMap<>();

  public LevelingMenu(Player player) {
    super(player, config.getTitle(), config.getRows());
    this.previousPage = config.getAsInt("previous-slot");
    this.nextPage = config.getAsInt("next-slot");
    this.previousStack = config.getAsString("previous-page");
    this.nextStack = config.getAsString("next-page");
    this.onlySlots(config.getAsIntegerList("rewards-slots"));
    Account account = Database.getInstance().getAccount(player.getUniqueId());

    List<ItemStack> items = new ArrayList<>();
    for (Level level : Level.listLevels()) {
      ItemStack item = null;
      if (account.getLevel() >= level.getLevel()) {
        item = BukkitUtils.deserializeItemStack(config.getAsString(account.isLeveled(level.getLevel()) ? "claimed" : "unlocked")
            .replace("{level}", String.valueOf(level.getLevel())).replace("{description}", String.valueOf(level.getDescription())));
      } else {
        item = BukkitUtils.deserializeItemStack(
            config.getAsString("locked").replace("{level}", String.valueOf(level.getLevel())).replace("{description}", String.valueOf(level.getDescription())));
      }

      items.add(item);
      this.level.put(item, level);
    }

    for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
      if (entry.getKey() >= 0 && entry.getKey() < (config.getRows() * 9)) {
        String stack = entry.getValue().getStack();

        // LEVELING
        Level level = Level.getByLevel(account.getLevel());
        double currentExp = account.getExp();
        double needExp = level.getNext() == null ? 0.0 : level.getNext().getExp();
        double untilNextLevel = level.getExperienceUntil(account.getExp());
        stack = stack.replace("{level}", StringUtils.formatNumber(account.getLevel()));
        stack = stack.replace("{until}", StringUtils.formatNumber(untilNextLevel));
        stack = stack.replace("{level_progress}", this.makeProgressBar(currentExp, needExp));
        stack = stack.replace("{level_percentage}", currentExp >= needExp ? "100%" : (int) ((currentExp * 100.0) / needExp) + "%");

        ItemStack item = BukkitUtils.deserializeItemStack(stack);
        this.removeSlotsWith(item, entry.getKey());
        this.actions.put(item, entry.getValue().getAction());
      }
    }

    this.setItems(items);

    this.open();
    this.register();
  }

  private String makeProgressBar(double currentExp, double needExp) {
    StringBuilder progressBar = new StringBuilder();
    double percentage = currentExp >= needExp ? 100.0 : ((currentExp * 100.0) / needExp);
    for (double d = 2.5; d <= 100.0; d += 2.5) {
      progressBar.append(percentage >= d ? "§3|" : "§8|");
    }

    return progressBar.toString();
  }

  public void cancel() {
    actions.clear();
    actions = null;
    level.clear();
    level = null;
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
    if (evt.getPlayer().equals(player) && evt.getInventory().equals(this.getCurrentInventory())) {
      this.cancel();
    }
  }
}
