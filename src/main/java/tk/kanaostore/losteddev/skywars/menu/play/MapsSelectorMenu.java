package tk.kanaostore.losteddev.skywars.menu.play;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
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
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigAction;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigItem;
import tk.kanaostore.losteddev.skywars.menu.PlayMenu;
import tk.kanaostore.losteddev.skywars.menu.PlayRankedMenu;
import tk.kanaostore.losteddev.skywars.menu.api.UpdatablePlayerPagedMenu;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsMode;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsType;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.world.WorldServer;

@SuppressWarnings("unchecked")
public class MapsSelectorMenu extends UpdatablePlayerPagedMenu {

  private static final ConfigMenu config = ConfigMenu.getByName("mapselector");

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
          ConfigAction action = actions.get(item);
          if (action != null && !action.getType().equals("NOTHING")) {
            if (action.getType().equals("OPEN")) {
              String menu = action.getValue();
              if (menu.equalsIgnoreCase("random")) {
                if (Core.MODE == CoreMode.MULTI_ARENA) {
                  WorldServer<?> server = WorldServer.findRandom(mode, type);
                  if (server != null) {
                    player.sendMessage(Language.lobby$npcs$play$connecting.replace("{world}", server.getWorld().getName()));
                    server.connect(account);
                  }
                } else {
                  CoreLobbies.writeMinigame(player, mode.name() + "_" + type.name(), "all");
                }
              } else if (menu.equalsIgnoreCase("favorites")) {
                if (can) {
                  List<String> favorites = new ArrayList<>();
                  for (Object object : account.getContainers("skywars").get("favorites").getAsJsonArray()) {
                    if (object instanceof String) {
                      boolean canPlay = map.containsKey((String) object) && !map.get((String) object).isEmpty();
                      if (Core.MODE != CoreMode.MULTI_ARENA) {
                        canPlay = getBungeeMap().containsKey((String) object) && getBungeeMap().get((String) object) > 0;
                      }

                      if (canPlay) {
                        favorites.add((String) object);
                      }
                    }
                  }

                  if (favorites.isEmpty()) {
                    return;
                  }

                  if (Core.MODE == CoreMode.MULTI_ARENA) {
                    for (WorldServer<?> server : this.map.get(favorites.get(ThreadLocalRandom.current().nextInt(favorites.size())))) {
                      if (server.getState().canJoin() && server.getAlive() < server.getMaxPlayers()) {
                        player.sendMessage(Language.lobby$npcs$play$connecting.replace("{world}", server.getWorld().getName()));
                        player.closeInventory();
                        account.updateLastSelected();
                        server.connect(account);
                        break;
                      }
                    }
                  } else {
                    CoreLobbies.writeMinigame(player, mode.name() + "_" + type.name(), favorites.get(ThreadLocalRandom.current().nextInt(favorites.size())));
                  }

                  favorites.clear();
                }
              } else if (menu.equalsIgnoreCase("play")) {
                if (type == SkyWarsType.RANKED) {
                  new PlayRankedMenu(player);
                } else {
                  new PlayMenu(player, mode);
                }
              } else if (menu.equalsIgnoreCase("closeinv")) {
                player.closeInventory();
              }
            } else {
              player.closeInventory();
              action.send(player);
            }
          } else if (evt.getSlot() == previousPage) {
            openPrevious();
          } else if (evt.getSlot() == nextPage) {
            openNext();
          } else {
            String mapName = maps.get(item);
            if (mapName != null) {
              if (evt.isRightClick()) {
                if (account.isFavoriteMap(mapName)) {
                  account.removeFavoriteMap(mapName);
                } else {
                  account.addFavoriteMap(mapName);
                }
                return;
              }

              if (can) {
                if (Core.MODE == CoreMode.MULTI_ARENA) {
                  for (WorldServer<?> server : this.map.get(mapName)) {
                    if (server.getState().canJoin() && server.getAlive() < server.getMaxPlayers()) {
                      player.sendMessage(Language.lobby$npcs$play$connecting.replace("{world}", server.getWorld().getName()));
                      player.closeInventory();
                      account.updateLastSelected();
                      server.connect(account);
                      break;
                    }
                  }
                } else {
                  CoreLobbies.writeMinigame(player, mode.name() + "_" + type.name(), mapName);
                }
              }
            }
          }
        }
      }
    }
  }

  private SkyWarsMode mode;
  private SkyWarsType type;
  private boolean tick = true, can = true;
  private Map<ItemStack, String> maps = new HashMap<>();
  private Map<String, List<WorldServer<?>>> map = new HashMap<>();
  private Map<ItemStack, ConfigAction> actions = new HashMap<>();

  public MapsSelectorMenu(Player player, SkyWarsMode mode, SkyWarsType type) {
    super(player, config.getTitle().replace("{mode}", mode.getName()).replace("{type}", type.getName()), config.getRows());
    this.mode = mode;
    this.type = type;
    this.previousPage = 18;
    this.nextPage = 26;
    this.onlySlots(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34);

    this.update();
    this.open();
    this.register(20L);
  }

  @Override
  public void update() {
    Account account = Database.getInstance().getAccount(player.getUniqueId());
    if (account == null) {
      player.closeInventory();
      return;
    }

    if (!player.hasPermission("lostskywars.mapselector.infinite") && !account.canSelectMap()) {
      can = false;
    }

    List<ItemStack> items = new ArrayList<>(), sub = new ArrayList<>();
    if (Core.MODE != CoreMode.MULTI_ARENA) {
      try {
        for (Map.Entry<String, Integer> entry : getBungeeMap().entrySet()) {
          String color = can ? "&a" : "&c";

          ItemStack item;
          if (account.isFavoriteMap(entry.getKey())) {
            item = BukkitUtils.deserializeItemStack(config.getAsString("map").replace("{color}", color).replace("{map}", entry.getKey())
                .replace("{avaiable}", String.valueOf(entry.getValue())).replace("{bukkit_color}", config.getAsString("color-favorite")).replace("{mode}", mode.getName())
                .replace("{type}", type.getName()).replace("{tick}", tick ? " " : "►"));
            items.add(item);
          } else {
            item = BukkitUtils.deserializeItemStack(config.getAsString("map").replace("{color}", color).replace("{map}", entry.getKey())
                .replace("{avaiable}", String.valueOf(entry.getValue())).replace("{bukkit_color}", config.getAsString("color-normal")).replace("{mode}", mode.getName())
                .replace("{type}", type.getName()).replace("{tick}", tick ? " " : "►"));
            sub.add(item);
          }

          maps.put(item, entry.getKey());
        }
      } catch (Exception ex) {
      }
    } else {
      this.map = WorldServer.getAsMap(mode, type);
      for (Map.Entry<String, List<WorldServer<?>>> entry : map.entrySet()) {
        List<WorldServer<?>> ss = entry.getValue();
        if (ss.isEmpty()) {
          continue;
        }

        String color = can ? "&a" : "&c";

        ItemStack item;
        if (account.isFavoriteMap(entry.getKey())) {
          item = BukkitUtils.deserializeItemStack(config.getAsString("map").replace("{color}", color).replace("{map}", entry.getKey())
              .replace("{avaiable}", String.valueOf(ss.size())).replace("{bukkit_color}", config.getAsString("color-favorite")).replace("{mode}", mode.getName())
              .replace("{type}", type.getName()).replace("{tick}", tick ? " " : "►"));
          items.add(item);
        } else {
          item = BukkitUtils.deserializeItemStack(config.getAsString("map").replace("{color}", color).replace("{map}", entry.getKey())
              .replace("{avaiable}", String.valueOf(ss.size())).replace("{bukkit_color}", config.getAsString("color-normal")).replace("{mode}", mode.getName())
              .replace("{type}", type.getName()).replace("{tick}", tick ? " " : "►"));
          sub.add(item);
        }

        maps.put(item, entry.getKey());
      }
    }

    items.addAll(sub);
    sub.clear();
    sub = null;
    if (lastListSize != -1 && lastListSize != items.size()) {
      items.clear();
      items = null;
      new MapsSelectorMenu(player, mode, type);
      return;
    }

    for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
      if (entry.getKey() >= 0 && entry.getKey() < 54) {
        String stack = entry.getValue().getStack();

        stack = stack.replace("{mode}", mode.getName());
        stack = stack.replace("{type}", type.getName());
        stack = stack.replace("{tick}", tick ? " " : "►");

        ItemStack item = BukkitUtils.deserializeItemStack(stack);
        this.removeSlotsWith(item, entry.getKey());
        this.actions.put(item, entry.getValue().getAction());
      }
    }
    this.setItems(items);

    this.tick = !tick;
  }

  public Map<String, Integer> getBungeeMap() {
    try {
      return (Map<String, Integer>) CoreLobbies.class.getDeclaredField(mode.name() + "_" + type.name() + "_MAP").get(null);
    } catch (Exception ex) {
      return new HashMap<>();
    }
  }

  public void cancel() {
    super.cancel();
    map.clear();
    map = null;
    maps.clear();
    maps = null;
    actions.clear();
    actions = null;
    mode = null;
    type = null;
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
