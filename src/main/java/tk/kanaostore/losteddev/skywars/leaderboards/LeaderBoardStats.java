package tk.kanaostore.losteddev.skywars.leaderboards;

import java.util.Arrays;
import java.util.List;
import tk.kanaostore.losteddev.skywars.Language;

public enum LeaderBoardStats {
    KILLS("SELECT * FROM `premium_lostedskywars` ORDER BY `solokills` + `teamkills` DESC LIMIT 100;"),
    WINS("SELECT * FROM `premium_lostedskywars` ORDER BY `solowins` + `teamwins` DESC LIMIT 100;"),
    LEVEL("SELECT * FROM `premium_lostedskywars` ORDER BY `level` DESC LIMIT 100;"),
    RANKED("SELECT * FROM `ranked_lostedskywars` ORDER BY `points` DESC LIMIT 100;");

  private String stats_armorstand;
  private String stats_holograms;
  private String mode_holograms;
  private String sql;

  LeaderBoardStats(String sql) {
    this.sql = sql;
  }

  public String getStatsArmorStand() {
    return stats_armorstand;
  }
  
  public String getStatsHolograms() {
    return stats_holograms;
  }
  
  public String getModeHolograms() {
    return mode_holograms;
  }

  public String getSQL() {
    return sql;
  }
  
  public List<String> getStatsRows() {
    if (this == KILLS) {
      return Arrays.asList("solokills", "teamkills");
    } else if (this == WINS) {
      return Arrays.asList("solowins", "teamwins");
    } else if (this == LEVEL) {
      return Arrays.asList("level");
    }
    
    return Arrays.asList("points");
  }

  public static void translate() {
    KILLS.stats_armorstand = Language.options$leaderboard$armorstand$stats$kills;
    WINS.stats_armorstand = Language.options$leaderboard$armorstand$stats$wins;
    LEVEL.stats_armorstand = Language.options$leaderboard$armorstand$stats$level;
    RANKED.stats_armorstand = Language.options$leaderboard$armorstand$stats$ranked;
    
    KILLS.stats_holograms = Language.options$leaderboard$holograms$stats$kills;
    WINS.stats_holograms = Language.options$leaderboard$holograms$stats$wins;
    LEVEL.stats_holograms = Language.options$leaderboard$holograms$stats$level;
    RANKED.stats_holograms = Language.options$leaderboard$holograms$stats$ranked;
    KILLS.mode_holograms = Language.options$leaderboard$holograms$mode$kills;
    WINS.mode_holograms = Language.options$leaderboard$holograms$mode$wins;
    LEVEL.mode_holograms = Language.options$leaderboard$holograms$mode$level;
    RANKED.mode_holograms = Language.options$leaderboard$holograms$mode$ranked;
  }

  public static LeaderBoardStats fromName(String name) {
    for (LeaderBoardStats type : values()) {
      if (type.name().equalsIgnoreCase(name)) {
        return type;
      }
    }

    return null;
  }
}
