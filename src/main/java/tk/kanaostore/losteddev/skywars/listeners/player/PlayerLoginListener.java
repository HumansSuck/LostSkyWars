package tk.kanaostore.losteddev.skywars.listeners.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.listeners.Listeners;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;

public class PlayerLoginListener extends Listeners {

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerLogin(PlayerLoginEvent evt) {
    Player player = evt.getPlayer();

    try {
      Database.getInstance().loadAccount(player.getUniqueId(), player.getName());
    } catch (Exception ex) {
      evt.disallow(PlayerLoginEvent.Result.KICK_OTHER,
          "§c§lSKYWARS\n \n§cCould not load your account.");
      LOGGER.log(LostLevel.SEVERE, "Could not loadAccount(\"" + player.getName() + "\"): ", ex);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerLoginMonitor(PlayerLoginEvent evt) {
    if (evt.getResult() != PlayerLoginEvent.Result.ALLOWED) {
      Database.getInstance().unloadAccount(evt.getPlayer().getUniqueId());
    }
  }
}
