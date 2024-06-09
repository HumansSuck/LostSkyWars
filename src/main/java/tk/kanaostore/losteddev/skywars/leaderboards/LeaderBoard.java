package tk.kanaostore.losteddev.skywars.leaderboards;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.leaderboards.objects.ArmorStandLB;
import tk.kanaostore.losteddev.skywars.leaderboards.objects.HologramLB;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;

public abstract class LeaderBoard {

  protected String id;
  protected Location location;
  protected LeaderBoardStats stats;

  public LeaderBoard(String id, Location location, LeaderBoardStats stats) {
    this.id = id;
    this.stats = stats;
    this.location = location;
  }

  public abstract void update();

  public abstract void destroy();

  public abstract LeaderBoardType getType();

  public abstract int getRanking();

  public String getId() {
    return id;
  }

  public Location getLocation() {
    return location;
  }

  public LeaderBoardStats getStats() {
    return stats;
  }

  private static List<LeaderBoard> leaderboards = new ArrayList<>();
  public static final LostLogger LOGGER = Main.LOGGER.getModule("LeaderBoard");

  public static void setupLeaderBoards() {
    ConfigUtils cu = ConfigUtils.getConfig("leaderboards");
    if (!cu.contains("boards")) {
      cu.createSection("boards");
    }

    for (String key : cu.getSection("boards").getKeys(false)) {
      String id = key;
      LeaderBoardType type = LeaderBoardType.fromName(cu.getString("boards." + key + ".type"));
      if (type == null) {
        continue;
      }
      LeaderBoardStats stats = LeaderBoardStats.fromName(cu.getString("boards." + key + ".stats"));
      if (stats == null) {
        continue;
      }

      int ranking = cu.getInt("boards." + key + ".ranking");
      String serialized = cu.getString("boards." + key + ".location");
      leaderboards.add(LeaderBoard.fromType(id, BukkitUtils.deserializeLocation(serialized), stats, type, ranking));
    }

    new BukkitRunnable() {
      @Override
      public void run() {
        Database.getInstance().listAccounts().forEach(Account::save);
        leaderboards.forEach(LeaderBoard::update);
      }
    }.runTaskTimer(Main.getInstance(), 0, Language.options$leaderboard$update_time_minutes * 1200);
  }

  public static void add(LeaderBoard board) {
    leaderboards.add(board);
    board.update();

    ConfigUtils cu = ConfigUtils.getConfig("leaderboards");
    cu.set("boards." + board.getId() + ".type", board.getType().name());
    cu.set("boards." + board.getId() + ".stats", board.getStats().name());
    cu.set("boards." + board.getId() + ".ranking", board.getRanking());
    cu.set("boards." + board.getId() + ".location", BukkitUtils.serializeLocation(board.getLocation()));
  }

  public static void remove(LeaderBoard board) {
    leaderboards.remove(board);
    board.destroy();

    ConfigUtils cu = ConfigUtils.getConfig("leaderboards");
    cu.set("boards." + board.getId(), null);
  }

  public static LeaderBoard getById(String id) {
    return leaderboards.stream().filter(leaderboard -> leaderboard.getId().equals(id)).findFirst().orElse(null);
  }

  public static LeaderBoard fromType(String id, Location location, LeaderBoardStats stats, LeaderBoardType type, int ranking) {
    if (type == LeaderBoardType.ARMORSTAND) {
      return new ArmorStandLB(id, location, stats, ranking);
    } else if (type == LeaderBoardType.HOLOGRAM) {
      return new HologramLB(id, location, stats);
    }

    return null;
  }
}
