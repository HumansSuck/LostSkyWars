package tk.kanaostore.losteddev.skywars.listeners.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import tk.kanaostore.losteddev.skywars.cmd.sw.BuildCommand;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.listeners.Listeners;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.rank.TagUtils;

public class PlayerQuitListener extends Listeners {

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent evt) {
    Player player = evt.getPlayer();
    evt.setQuitMessage(null);

    TagUtils.reset(player.getName());
    Account account = Database.getInstance().unloadAccount(player.getUniqueId());
    if (account != null) {
      if (account.getServer() != null) {
        account.getServer().disconnect(account, "-quit");
      }
      
      account.save();
      account.destroy();
    }
    
    BuildCommand.remove(player);
  }
}
