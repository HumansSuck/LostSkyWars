package tk.kanaostore.losteddev.skywars.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.api.player.LostPlayer;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsServer;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsState;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.player.Account;

public class LostSkyWarsAPI {
  
  public static void tryWatch(Player watcher, String usernameToWatch) {
    Account account = null;
    Player target = Bukkit.getPlayer(usernameToWatch);
    if (target == null || (account = Database.getInstance().getAccount(target.getUniqueId())) == null) {
      watcher.sendMessage(Language.command$watch$user_not_found);
      return;
    }

    SkyWarsServer server = account.getServer();
    if (server == null || server.getState() != SkyWarsState.INGAME || server.isSpectator(target)) {
      watcher.sendMessage(Language.command$watch$user_not_in_match);
      return;
    }

    watcher.sendMessage(Language.lobby$npcs$play$connecting.replace("{world}", server.getServerName()));
    server.spectate(account, target);
  }
  
  public static LostPlayer getLostPlayer(Player player) {
    if (player == null) {
      return null;
    }
    
    Account account = Database.getInstance().getAccount(player.getUniqueId());
    if (account == null) {
      return null;
    }
    
    return new LostPlayer(account);
  }
}
