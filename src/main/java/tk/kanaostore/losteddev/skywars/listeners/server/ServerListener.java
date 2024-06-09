package tk.kanaostore.losteddev.skywars.listeners.server;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsState;
import tk.kanaostore.losteddev.skywars.bungee.Core;
import tk.kanaostore.losteddev.skywars.bungee.CoreMode;
import tk.kanaostore.losteddev.skywars.world.WorldServer;

public class ServerListener implements Listener {

  @EventHandler(priority = EventPriority.MONITOR)
  public void onServerListPing(ServerListPingEvent evt) {
    if (Core.MODE == CoreMode.MULTI_ARENA) {
      if (Language.lobby$motd$enabled) {
        evt.setMotd(Language.lobby$motd$header + "\n" + Language.lobby$motd$footer);
      }
    } else {
      if (Core.MODE == CoreMode.LOBBY) {
        evt.setMotd("LOBBY; ");
      } else {
        WorldServer<?> server = WorldServer.listServers().stream().findFirst().orElse(null);
        if (server == null) {
          evt.setMotd("");
          return;
        }

        evt.setMotd(server.getMode().name() + "_" + server.getType().name() + "; " + server.getConfig().getMapName() + "; " + server.getState().name());
      }
    }
  }

  @EventHandler
  public void onBlockIgnite(BlockIgniteEvent evt) {
    WorldServer<?> server = WorldServer.getByWorldName(evt.getBlock().getWorld().getName());
    if (server == null) {
      evt.setCancelled(true);
    } else if (server.getState() != SkyWarsState.INGAME) {
      evt.setCancelled(true);
    }
  }

  @EventHandler
  public void onBlockBurn(BlockBurnEvent evt) {
    WorldServer<?> server = WorldServer.getByWorldName(evt.getBlock().getWorld().getName());
    if (server == null) {
      evt.setCancelled(true);
    } else if (server.getState() != SkyWarsState.INGAME) {
      evt.setCancelled(true);
    }
  }

  @EventHandler
  public void onLeavesDecay(LeavesDecayEvent evt) {
    evt.setCancelled(true);
  }

  @EventHandler
  public void onEntityExplode(EntityExplodeEvent evt) {
    WorldServer<?> server = WorldServer.getByWorldName(evt.getEntity().getWorld().getName());
    if (server == null) {
      evt.setCancelled(true);
    } else if (server.getState() != SkyWarsState.INGAME) {
      evt.setCancelled(true);
    }
  }

  @EventHandler
  public void onWeatherChange(WeatherChangeEvent evt) {
    evt.setCancelled(evt.toWeatherState());
  }
}
