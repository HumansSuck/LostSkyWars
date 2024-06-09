package tk.kanaostore.losteddev.skywars.listeners.player;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsServer;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.listeners.Listeners;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.ranked.Ranked;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsMode;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsType;

public class PlayerDeathListener extends Listeners {

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent evt) {
    Player player = evt.getEntity();
    evt.setDeathMessage(null);

    Account account = Database.getInstance().getAccount(player.getUniqueId());
    if (account != null) {
      SkyWarsServer server = account.getServer();
      if (server == null) {
        evt.setDroppedExp(0);
        player.setHealth(20.0);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> account.refreshPlayer(), 3);
      } else {        
        player.setHealth(20.0);
        List<Account> hitters = account.getLastHitters();
        Account killer = hitters.size() > 0 ? hitters.get(0) : null;
        server.kill(account, killer);
        for (Account hitter : hitters) {
          if (hitter != null && (killer == null || !hitter.equals(killer)) && (hitter.getServer() != null && hitter.getServer().equals(server)) && hitter.getPlayer() != null
              && !server.isSpectator(hitter.getPlayer())) {
            if (server.getType().equals(SkyWarsType.RANKED)) {
              Ranked.increase(hitter, "assists");
            } else {
              hitter.addStat((server.getMode().equals(SkyWarsMode.SOLO) ? "solo" : "team") + "assists");
            }
          }
        }
      }
    }
  }
}
