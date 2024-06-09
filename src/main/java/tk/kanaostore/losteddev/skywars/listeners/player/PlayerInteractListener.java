package tk.kanaostore.losteddev.skywars.listeners.player;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsServer;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsState;
import tk.kanaostore.losteddev.skywars.bungee.Core;
import tk.kanaostore.losteddev.skywars.bungee.CoreLobbies;
import tk.kanaostore.losteddev.skywars.bungee.CoreMode;
import tk.kanaostore.losteddev.skywars.cmd.sw.BalloonsCommand;
import tk.kanaostore.losteddev.skywars.cmd.sw.BuildCommand;
import tk.kanaostore.losteddev.skywars.cmd.sw.ChestCommand;
import tk.kanaostore.losteddev.skywars.cmd.sw.CreateCommand;
import tk.kanaostore.losteddev.skywars.cmd.sw.WaitingLobbyCommand;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.hook.boxes.BoxNPC;
import tk.kanaostore.losteddev.skywars.listeners.Listeners;
import tk.kanaostore.losteddev.skywars.menu.KitSelectorMenu;
import tk.kanaostore.losteddev.skywars.menu.MysteryVaultMenu;
import tk.kanaostore.losteddev.skywars.menu.PlayDuelsMenu;
import tk.kanaostore.losteddev.skywars.menu.PlayMenu;
import tk.kanaostore.losteddev.skywars.menu.PlayRankedMenu;
import tk.kanaostore.losteddev.skywars.menu.ProfileMenu;
import tk.kanaostore.losteddev.skywars.menu.ShopMenu;
import tk.kanaostore.losteddev.skywars.menu.TeleporterMenu;
import tk.kanaostore.losteddev.skywars.menu.shop.SoulWellMenu;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsChest;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsChest.ChestType;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsType;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.well.WellNPC;
import tk.kanaostore.losteddev.skywars.world.WorldServer;

@SuppressWarnings("deprecation")
public class PlayerInteractListener extends Listeners {

  public static Map<String, Long> click = new HashMap<>(), canSeeFlood = new HashMap<>();
  public static final Map<Player, String[]> CREATING = new HashMap<>();
  public static final Map<Player, ChestType> CHEST = new HashMap<>();
  public static final Map<Player, Object[]> WAITING_LOBBY = new HashMap<>();
  public static final Map<Player, Object[]> BALLOONS = new HashMap<>();

  private static final DecimalFormat df = new DecimalFormat("###.#");

  public static void handleClick(Player player, Account account, String display, Cancellable evt) {
    long clickSpam = click.containsKey(player.getName()) ? click.get(player.getName()) : 0;
    if (clickSpam > System.currentTimeMillis()) {
      return;
    }

    click.put(player.getName(), System.currentTimeMillis() + 500);
    if (display.equals(Language.lobby$hotbar$profile$name)) {
      evt.setCancelled(true);
      new ProfileMenu(player);
    } else if (display.equals(Language.lobby$hotbar$shop$name)) {
      evt.setCancelled(true);
      new ShopMenu(player);
    } else if (display.equals(Language.lobby$hotbar$players$name_v) || display.equals(Language.lobby$hotbar$players$name_i)) {
      evt.setCancelled(true);

      long start = canSeeFlood.containsKey(player.getName()) ? canSeeFlood.get(player.getName()) : 0;
      if (start > System.currentTimeMillis()) {
        double time = (start - System.currentTimeMillis()) / 1000.0;
        if (time > 0.1) {
          String timeString = df.format(time).replace(",", ".");
          if (timeString.endsWith("0")) {
            timeString = timeString.substring(0, timeString.lastIndexOf("."));
          }

          player.sendMessage(Language.lobby$visibility$delay.replace("{time}", timeString));
          return;
        }
      }

      canSeeFlood.put(player.getName(), System.currentTimeMillis() + 3000);
      account.setCanSeePlayers(!account.canSeePlayers());
      player.sendMessage(account.canSeePlayers() ? Language.lobby$visibility$enabled : Language.lobby$visibility$disabled);
      account.refreshPlayers();
    }

    display = null;
    player.updateInventory();
  }

  public static void handleClickArena(Player player, Account account, SkyWarsServer server, String display, Cancellable evt) {
    long clickSpam = click.containsKey(player.getName()) ? click.get(player.getName()) : 0;
    if (clickSpam > System.currentTimeMillis()) {
      return;
    }

    click.put(player.getName(), System.currentTimeMillis() + 500);
    if (display.equals(Language.game$hotbar$kits$name)) {
      new KitSelectorMenu(player, server.getType().getIndex());
    } else if (display.equals(Language.game$hotbar$quit$name) || display.equals(Language.game$hotbar$quit_spectator$name)) {
      if (Core.MODE == CoreMode.MULTI_ARENA) {
        server.disconnect(account);
      } else {
        CoreLobbies.writeLobby(player);
      }
    } else if (display.equals(Language.game$hotbar$compass$name)) {
      new TeleporterMenu(player, (WorldServer<?>) server);
    } else if (display.equals(Language.game$hotbar$play_again$name)) {
      if (server.getType() == SkyWarsType.RANKED) {
        new PlayRankedMenu(player);
      } else if (server.getType() == SkyWarsType.DUELS) {
        new PlayDuelsMenu(player);
      } else {
        new PlayMenu(player, server.getMode());
      }
    }
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent evt) {
    CREATING.remove(evt.getPlayer());

    click.remove(evt.getPlayer().getName());
    canSeeFlood.remove(evt.getPlayer().getName());
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent evt) {
    Player player = evt.getPlayer();
    ItemStack item = player.getItemInHand();

    Account account = Database.getInstance().getAccount(player.getUniqueId());
    if (account != null) {
      SkyWarsServer server = account.getServer();
      if (server == null) {
        evt.setCancelled(!BuildCommand.isBuilder(player));
        if (CREATING.containsKey(player)) {
          if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            CreateCommand.handleClick(player, account, item.getItemMeta().getDisplayName(), evt);
          }
          return;
        } else if (CHEST.containsKey(player)) {
          if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            ChestCommand.handleClick(player, account, item.getItemMeta().getDisplayName(), evt);
          }
          return;
        } else if (WAITING_LOBBY.containsKey(player)) {
          if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            WaitingLobbyCommand.handleClick(player, account, item.getItemMeta().getDisplayName(), evt);
          }
          return;
        } else if (BALLOONS.containsKey(player)) {
          if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            BalloonsCommand.handleClick(player, account, item.getItemMeta().getDisplayName(), evt);
          }
          return;
        }

        if (evt.getClickedBlock() != null) {
          WellNPC wn = WellNPC.getByLocation(evt.getClickedBlock().getLocation());
          if (wn != null) {
            new SoulWellMenu(player, false);
            return;
          }

          BoxNPC bn = BoxNPC.getByLocation(evt.getClickedBlock().getLocation());
          if (bn != null) {
            new MysteryVaultMenu(player, bn);
            return;
          }
        }

        if (evt.getAction().name().contains("RIGHT") && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
          handleClick(player, account, item.getItemMeta().getDisplayName(), evt);
        }
      } else {
        if (server.getState() != SkyWarsState.INGAME || server.isSpectator(player)) {
          evt.setCancelled(true);
          if (evt.getAction().name().contains("RIGHT") && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            handleClickArena(player, account, server, item.getItemMeta().getDisplayName(), evt);
          }
        } else {
          if (evt.getClickedBlock() != null) {
            Block block = evt.getClickedBlock();

            if (evt.getAction().name().contains("RIGHT")) {
              if (block.getState() instanceof Chest) {
                SkyWarsChest chest = ((WorldServer<?>) server).getChest(block);
                if (chest != null && ((WorldServer<?>) server).getNextEventTime() != 0) {
                  chest.createHologram();
                }
              }

              WorldServer<?> ws = (WorldServer<?>) server;
              if (!ws.getConfig().getWorldCube().contains(block.getLocation()) || !ws.getConfig().getWorldCube().contains(block.getRelative(BlockFace.UP).getLocation())
                  || ws.getConfig().isBalloon(BukkitUtils.serializeLocation(block.getLocation()))) {
                evt.setCancelled(true);
              }
            }
          }
        }
      }
    }
  }
}
