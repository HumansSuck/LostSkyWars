package tk.kanaostore.losteddev.skywars.listeners.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.bungee.Core;
import tk.kanaostore.losteddev.skywars.bungee.CoreMode;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.listeners.Listeners;
import tk.kanaostore.losteddev.skywars.nms.NMS;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.rank.Rank;
import tk.kanaostore.losteddev.skywars.rank.TagUtils;
import tk.kanaostore.losteddev.skywars.utils.PlayerUtils;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;
import tk.kanaostore.losteddev.skywars.world.WorldServer;

public class PlayerJoinListener extends Listeners {

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent evt) {
    Player player = evt.getPlayer();
    evt.setJoinMessage(null);

    TagUtils.sendTeams(player);
    Account account = Database.getInstance().getAccount(player.getUniqueId());
    if (Core.MODE == CoreMode.ARENA) {
      if (WorldServer.listServers().size() > 0) {
        WorldServer.listServers().stream().findFirst().get().connect(account);
      }
    } else {
      account.reloadScoreboard();
      account.refreshPlayer();
      account.refreshPlayers();
    }

    if (Language.lobby$tablist$enabled) {
      NMS.sendTabHeaderFooter(player, Language.lobby$tablist$header, Language.lobby$tablist$footer);
    }

    Rank rank = Rank.getRank(player);
    if (rank != null && rank.getOnJoin() != null) {
      Database.getInstance().listAccounts().stream()
        .filter(a -> a.inLobby())
        .forEach(a -> a.getPlayer().sendMessage(PlayerUtils.replaceAll(player, StringUtils.formatColors(rank.getOnJoin()))));
    }
  }
}
