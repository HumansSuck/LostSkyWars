package tk.kanaostore.losteddev.skywars.ranked;

import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

public class Ranked {

  public static int rewards$points$solo$per_win;
  public static int rewards$points$solo$per_kill;
  public static int rewards$points$doubles$per_win;
  public static int rewards$points$doubles$per_kill;

  public static void decrease(Account account, String stats) {
    increase(account, stats, 1);
  }

  public static void decrease(Account account, String stats, int amount) {
    for (int i = 0; i < amount; i++) {
      if (account.getContainers("ranked").get(stats).getAsInt() == 0) {
        break;
      }
      
      account.getContainers("ranked").get(stats).removeInt(1);
    }
  }
  
  public static void increase(Account account, String stats) {
    increase(account, stats, 1);
  }

  public static void increase(Account account, String stats, int amount) {
    account.getContainers("ranked").get(stats).addInt(amount);
  }

  public static String getTag(Player player) {
    Account account = Database.getInstance().getAccount(player.getUniqueId());
    return StringUtils.getFirstColor(getLeague(account).getName()) + "[" + StringUtils.formatNumber(getPoints(account)) + "] ";
  }

  public static int getPoints(Account account) {
    return account.getContainers("ranked").get("points").getAsInt();
  }
  
  public static int getInt(Account account, String key) {
    return account.getContainers("ranked").get(key).getAsInt();
  }
  
  public static String getFormatted(Account account, String key) {
    return StringUtils.formatNumber(account.getContainers("ranked").get(key).getAsInt());
  }

  public static League getLeague(Account account) {
    int points = getPoints(account);

    for (League league : League.listLeagues()) {
      if (points >= league.getPoints()) {
        return league;
      }
    }

    return League.listLeagues().get(League.listLeagues().size() - 1);
  }

  static final ConfigUtils CONFIG = ConfigUtils.getConfig("ranked");

  public static void setupRanked() {
    rewards$points$solo$per_kill = CONFIG.getInt("rewards.points.solo.per-kill");
    rewards$points$solo$per_win = CONFIG.getInt("rewards.points.solo.per-win");
    rewards$points$doubles$per_kill = CONFIG.getInt("rewards.points.doubles.per-kill");
    rewards$points$doubles$per_win = CONFIG.getInt("rewards.points.doubles.per-win");

    League.setupLeagues();
  }
}
