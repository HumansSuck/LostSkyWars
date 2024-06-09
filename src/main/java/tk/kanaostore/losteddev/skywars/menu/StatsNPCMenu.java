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
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigAction;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigItem;
import tk.kanaostore.losteddev.skywars.menu.api.PlayerMenu;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.ranked.Ranked;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

public class StatsNPCMenu extends PlayerMenu {

  private static final ConfigMenu config = ConfigMenu.getByName("statsnpc");

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
              if (menu.equalsIgnoreCase("closeinv")) {
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


  public StatsNPCMenu(Player player) {
    super(player, config.getTitle(), config.getRows());
    Account account = Database.getInstance().getAccount(player.getUniqueId());

    for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
      if (entry.getKey() >= 0 && entry.getKey() < this.getInventory().getSize()) {
        String stack = entry.getValue().getStack();

        // Total
        int totalWins = account.getIntegers("solowins", "teamwins") + Ranked.getInt(account, "wins");
        int totalGames = account.getIntegers("soloplays", "teamplays") + Ranked.getInt(account, "plays");
        stack = stack.replace("{wins}", StringUtils.formatNumber(totalWins));
        stack = stack.replace("{kills}", StringUtils.formatNumber(account.getIntegers("solokills", "teamkills") + Ranked.getInt(account, "kills")));
        stack = stack.replace("{melee}", StringUtils.formatNumber(account.getIntegers("solomelee", "teammelee") + Ranked.getInt(account, "melee")));
        stack = stack.replace("{void}", StringUtils.formatNumber(account.getIntegers("solovoid", "teamvoid") + Ranked.getInt(account, "void")));
        stack = stack.replace("{bow}", StringUtils.formatNumber(account.getIntegers("solobow", "teambow") + Ranked.getInt(account, "bow")));
        stack = stack.replace("{assists}", StringUtils.formatNumber(account.getIntegers("soloassists", "teamassists") + Ranked.getInt(account, "assists")));
        stack = stack.replace("{deaths}", StringUtils.formatNumber(account.getIntegers("solodeaths", "teamdeaths") + Ranked.getInt(account, "deaths")));
        stack = stack.replace("{games}", StringUtils.formatNumber(totalGames));
        stack = stack.replace("{losses}", StringUtils.formatNumber(totalGames - totalWins));

        // Solo
        totalWins = account.getInt("solowins");
        totalGames = account.getInt("soloplays");
        stack = stack.replace("{solowins}", account.getFormatted("solowins"));
        stack = stack.replace("{solokills}", account.getFormatted("solokills"));
        stack = stack.replace("{solomelee}", account.getFormatted("solomelee"));
        stack = stack.replace("{solovoid}", account.getFormatted("solovoid"));
        stack = stack.replace("{solobow}", account.getFormatted("solobow"));
        stack = stack.replace("{soloassists}", account.getFormatted("soloassists"));
        stack = stack.replace("{solodeaths}", account.getFormatted("solodeaths"));
        stack = stack.replace("{sologames}", account.getFormatted("soloplays"));
        stack = stack.replace("{sololosses}", StringUtils.formatNumber(totalGames - totalWins));

        // Doubles
        totalWins = account.getInt("teamwins");
        totalGames = account.getInt("teamplays");
        stack = stack.replace("{teamwins}", account.getFormatted("teamwins"));
        stack = stack.replace("{teamkills}", account.getFormatted("teamkills"));
        stack = stack.replace("{teammelee}", account.getFormatted("teammelee"));
        stack = stack.replace("{teamvoid}", account.getFormatted("teamvoid"));
        stack = stack.replace("{teambow}", account.getFormatted("teambow"));
        stack = stack.replace("{teamassists}", account.getFormatted("teamassists"));
        stack = stack.replace("{teamdeaths}", account.getFormatted("teamdeaths"));
        stack = stack.replace("{teamgames}", account.getFormatted("teamplays"));
        stack = stack.replace("{teamlosses}", StringUtils.formatNumber(totalGames - totalWins));

        // Ranked
        totalWins = Ranked.getInt(account, "wins");
        totalGames = Ranked.getInt(account, "plays");
        stack = stack.replace("{rankedwins}", Ranked.getFormatted(account, "wins"));
        stack = stack.replace("{rankedkills}", Ranked.getFormatted(account, "kills"));
        stack = stack.replace("{rankedmelee}", Ranked.getFormatted(account, "melee"));
        stack = stack.replace("{rankedvoid}", Ranked.getFormatted(account, "void"));
        stack = stack.replace("{rankedbow}", Ranked.getFormatted(account, "bow"));
        stack = stack.replace("{rankedassists}", Ranked.getFormatted(account, "assists"));
        stack = stack.replace("{rankeddeaths}", Ranked.getFormatted(account, "deaths"));
        stack = stack.replace("{rankedgames}", Ranked.getFormatted(account, "plays"));
        stack = stack.replace("{rankedlosses}", StringUtils.formatNumber(totalGames - totalWins));
        stack = stack.replace("{points}", Ranked.getFormatted(account, "points"));
        stack = stack.replace("{league}", Ranked.getLeague(account).getName());

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
