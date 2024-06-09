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
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.bungee.Core;
import tk.kanaostore.losteddev.skywars.bungee.CoreLobbies;
import tk.kanaostore.losteddev.skywars.bungee.CoreMode;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigAction;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigItem;
import tk.kanaostore.losteddev.skywars.menu.api.PlayerMenu;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsMode;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsType;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;
import tk.kanaostore.losteddev.skywars.world.WorldServer;

public class PlayDuelsMenu extends PlayerMenu {

  private static final ConfigMenu config = ConfigMenu.getByName("playduels");

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
              if (menu.equalsIgnoreCase("playsolo")) {
                if (Core.MODE == CoreMode.MULTI_ARENA) {
                  WorldServer<?> server = WorldServer.findRandom(SkyWarsMode.SOLO, SkyWarsType.DUELS);
                  if (server != null) {
                    player.sendMessage(Language.lobby$npcs$play$connecting.replace("{world}", server.getWorld().getName()));
                    server.connect(account);
                  }
                } else {
                  CoreLobbies.writeMinigame(player, "SOLO_DUELS", "all");
                }
              } else if (menu.equalsIgnoreCase("playdoubles")) {
                if (Core.MODE == CoreMode.MULTI_ARENA) {
                  WorldServer<?> server = WorldServer.findRandom(SkyWarsMode.DOUBLES, SkyWarsType.DUELS);
                  if (server != null) {
                    player.sendMessage(Language.lobby$npcs$play$connecting.replace("{world}", server.getWorld().getName()));
                    server.connect(account);
                  }
                } else {
                  CoreLobbies.writeMinigame(player, "DOUBLES_DUELS", "all");
                }
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

  public PlayDuelsMenu(Player player) {
    super(player, config.getTitle(), config.getRows());

    int playing_solo = CoreLobbies.SOLO_DUELS, playing_doubles = CoreLobbies.DOUBLES_DUELS;
    if (Core.MODE == CoreMode.MULTI_ARENA) {
      for (WorldServer<?> server : WorldServer.listServers()) {
        if (server.getType().equals(SkyWarsType.DUELS)) {
          if (server.getMode().equals(SkyWarsMode.SOLO)) {
            playing_solo += server.getOnline();
          } else {
            playing_doubles += server.getOnline();
          }
        }
      }
    }

    for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
      if (entry.getKey() >= 0 && entry.getKey() < this.getInventory().getSize()) {
        String stack = entry.getValue().getStack();

        stack = stack.replace("{players_solo}", StringUtils.formatNumber(playing_solo));
        stack = stack.replace("{players_doubles}", StringUtils.formatNumber(playing_doubles));

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
