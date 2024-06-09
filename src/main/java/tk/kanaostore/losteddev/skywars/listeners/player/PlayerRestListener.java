package tk.kanaostore.losteddev.skywars.listeners.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsServer;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsState;
import tk.kanaostore.losteddev.skywars.cmd.sw.BuildCommand;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.world.WorldServer;

@SuppressWarnings("deprecation")
public class PlayerRestListener implements Listener {

  @EventHandler
  public void onPlayerDropItem(PlayerDropItemEvent evt) {
    Account account = Database.getInstance().getAccount(evt.getPlayer().getUniqueId());
    if (account != null) {
      SkyWarsServer server = account.getServer();
      if (server == null) {
        evt.setCancelled(true);
      } else {
        if (server.getState() != SkyWarsState.INGAME || server.isSpectator(evt.getPlayer())) {
          evt.setCancelled(true);
        }
      }
    }
  }

  @EventHandler
  public void onPlayerPickupItem(PlayerPickupItemEvent evt) {
    Account account = Database.getInstance().getAccount(evt.getPlayer().getUniqueId());
    if (account != null) {
      SkyWarsServer server = account.getServer();
      if (server == null) {
        evt.setCancelled(true);
      } else {
        if (server.getState() != SkyWarsState.INGAME || server.isSpectator(evt.getPlayer())) {
          evt.setCancelled(true);
        }
      }
    }
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent evt) {
    Account account = Database.getInstance().getAccount(evt.getPlayer().getUniqueId());
    if (account != null) {
      SkyWarsServer server = account.getServer();
      if (server == null) {
        evt.setCancelled(!BuildCommand.isBuilder(evt.getPlayer()));
      } else {
        if (server.getState() != SkyWarsState.INGAME || server.isSpectator(evt.getPlayer())) {
          evt.setCancelled(true);
        } else {
          WorldServer<?> ws = (WorldServer<?>) server;
          if (ws.getConfig().isBalloon(BukkitUtils.serializeLocation(evt.getBlock().getLocation()))) {
            evt.setCancelled(true);
          } else if (!ws.getConfig().getWorldCube().contains(evt.getBlock().getLocation())) {
            evt.setCancelled(true);
          }
        }
      }
    }
  }

  @EventHandler
  public void onBlockPlace(BlockPlaceEvent evt) {
    Account account = Database.getInstance().getAccount(evt.getPlayer().getUniqueId());
    if (account != null) {
      SkyWarsServer server = account.getServer();
      if (server == null) {
        evt.setCancelled(!BuildCommand.isBuilder(evt.getPlayer()));
      } else {
        if (server.getState() != SkyWarsState.INGAME || server.isSpectator(evt.getPlayer())) {
          evt.setCancelled(true);
        } else if (!((WorldServer<?>) server).getConfig().getWorldCube().contains(evt.getBlock().getLocation())) {
          evt.setCancelled(true);
        }
      }
    }
  }
}
