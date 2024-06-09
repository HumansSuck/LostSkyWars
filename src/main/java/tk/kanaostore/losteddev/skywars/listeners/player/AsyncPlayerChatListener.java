package tk.kanaostore.losteddev.skywars.listeners.player;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsServer;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsState;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.level.Level;
import tk.kanaostore.losteddev.skywars.listeners.Listeners;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.rank.Rank;
import tk.kanaostore.losteddev.skywars.ranked.Ranked;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsType;
import tk.kanaostore.losteddev.skywars.utils.PlayerUtils;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

public class AsyncPlayerChatListener extends Listeners {

  private static final Map<String, Long> flood = new HashMap<>();

  private static final DecimalFormat df = new DecimalFormat("###.#");

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent evt) {
    flood.remove(evt.getPlayer().getName());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void AsyncPlayerChat(AsyncPlayerChatEvent evt) {
    if (evt.isCancelled() || !Language.lobby$chat$enabled) {
      return;
    }

    evt.setCancelled(true);

    Player player = evt.getPlayer();
    if (!player.hasPermission("lostskywars.chat.delay")) {
      long start = flood.containsKey(player.getName()) ? flood.get(player.getName()) : 0;
      if (start > System.currentTimeMillis()) {
        double time = (start - System.currentTimeMillis()) / 1000.0;
        if (time > 0.1) {
          String timeString = df.format(time).replace(",", ".");
          if (timeString.endsWith("0")) {
            timeString = timeString.substring(0, timeString.lastIndexOf("."));
          }

          player.sendMessage(Language.lobby$chat$delay_message.replace("{time}", timeString));
          return;
        }
      }

      flood.put(player.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(Language.lobby$chat$delay_time));
    }

    String color = Rank.getRank(player).getPermission().equals("none") ? "§7" : "§f";
    if (player.hasPermission("lostskywars.chat.color")) {
      evt.setMessage(StringUtils.formatColors(evt.getMessage()));
    }

    Account account = Database.getInstance().getAccount(player.getUniqueId());
    SkyWarsServer server = account.getServer();
    if (server != null && server.getType().equals(SkyWarsType.DUELS) && (server.getState() == SkyWarsState.WAITING || server.getState() == SkyWarsState.STARTING)) {
      return;
    }

    String level = Level.getByLevel(account.getLevel()).getLevel(account);
    for (Account accounts : Database.getInstance().listAccounts()) {
      Player players = accounts.getPlayer();
      if (server == null && accounts.getServer() == null) {
        players.sendMessage(PlayerUtils.replaceAll(player, Language.lobby$chat$format.replace("{level}", level).replace("{color}", color).replace("{message}", evt.getMessage())));
      } else if (accounts.getServer() != null && accounts.getServer().equals(server)) {
        if (server.isSpectator(player) && !server.isSpectator(players)) {
          continue;
        }

        if (server.isSpectator(player)) {
          players.sendMessage(
              PlayerUtils.replaceAll(player, Language.lobby$chat$format_spectator.replace("{level}", level).replace("{color}", color).replace("{message}", evt.getMessage())));
        } else {
          if (server.getType() == SkyWarsType.RANKED) {
            players.sendMessage(PlayerUtils.replaceAll(player, Language.lobby$chat$format_ranked.replace("{level}", level).replace("{league}", Ranked.getLeague(account).getName())
                .replace("{points}", StringUtils.formatNumber(Ranked.getPoints(account))).replace("{color}", color).replace("{message}", evt.getMessage())));
          } else if (server.getType() == SkyWarsType.DUELS) {
            players
                .sendMessage(PlayerUtils.replaceAll(player, Language.lobby$chat$format.replace("{level}", level).replace("{color}", color).replace("{message}", evt.getMessage())));
          } else {
            players
                .sendMessage(PlayerUtils.replaceAll(player, Language.lobby$chat$format.replace("{level}", level).replace("{color}", color).replace("{message}", evt.getMessage())));
          }
        }
      }
    }
  }
}
