package tk.kanaostore.losteddev.skywars.leaderboards.objects;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.rowset.CachedRowSet;
import org.bukkit.Location;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.holograms.Hologram;
import tk.kanaostore.losteddev.skywars.holograms.Holograms;
import tk.kanaostore.losteddev.skywars.leaderboards.LeaderBoard;
import tk.kanaostore.losteddev.skywars.leaderboards.LeaderBoardStats;
import tk.kanaostore.losteddev.skywars.leaderboards.LeaderBoardType;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;

public class HologramLB extends LeaderBoard {

  private Hologram hologram;

  public HologramLB(String id, Location location, LeaderBoardStats stats) {
    super(id, location, stats);
    if (!location.getChunk().isLoaded()) {
      location.getChunk().load(true);
    }
  }

  @Override
  public void update() {
    try {
      CachedRowSet rs = Database.getInstance().query(stats.getSQL());
      Map<Integer, String> map = new HashMap<>();
      for (int i = 0; i < Language.options$leaderboard$hologram$lines.size(); i++) {
        map.put(i + 1, Language.options$leaderboard$hologram$lines.get(i).replace("{mode}", stats.getModeHolograms()).replace("{stats}", stats.getStatsHolograms()));
      }

      int slot = 1;
      if (rs != null) {
        rs.beforeFirst();
        while (rs.next()) {
          int count = 0;
          for (String row : stats.getStatsRows()) {
            count += rs.getInt(row);
          }

          String lastRank = Database.getInstance().query("SELECT `lastRank` FROM `premium_lostedaccount` WHERE `name` = ?", rs.getString("name")).getString("lastRank");
          for (Integer i : map.keySet()) {
            map.put(i, map.get(i).replace("{" + slot + "_name}", lastRank + rs.getString("name")).replace("{" + slot + "_playerstats}", StringUtils.formatNumber(count)));
          }
          slot++;
        }
      }

      while (slot < 100) {
        for (Integer i : map.keySet()) {
          map.put(i, map.get(i).replace("{" + slot + "_name}", Language.options$leaderboard$empty).replace("{" + slot + "_playerstats}", "0"));
        }
        slot++;
      }

      List<String> lines = new ArrayList<>();
      for (int i = 0; i < Language.options$leaderboard$hologram$lines.size(); i++) {
        lines.add(map.get(i + 1));
      }
      if (hologram == null) {
        hologram = Holograms.createHologram(location, lines);
      } else {
        for (int i = 0; i < lines.size(); i++) {
          hologram.updateLine(i + 1, lines.get(i));
        }
      }
    } catch (SQLException ex) {
      LOGGER.log(LostLevel.WARNING, "update(): ", ex);
    }
  }

  @Override
  public void destroy() {
    Holograms.removeHologram(hologram);
    this.hologram = null;
  }

  @Override
  public LeaderBoardType getType() {
    return LeaderBoardType.HOLOGRAM;
  }

  @Override
  public int getRanking() {
    return 0;
  }
}
