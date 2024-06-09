package tk.kanaostore.losteddev.skywars.leaderboards.objects;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.rowset.CachedRowSet;
import org.bukkit.Location;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.holograms.Hologram;
import tk.kanaostore.losteddev.skywars.holograms.Holograms;
import tk.kanaostore.losteddev.skywars.holograms.entity.IArmorStand;
import tk.kanaostore.losteddev.skywars.leaderboards.LeaderBoard;
import tk.kanaostore.losteddev.skywars.leaderboards.LeaderBoardStats;
import tk.kanaostore.losteddev.skywars.leaderboards.LeaderBoardType;
import tk.kanaostore.losteddev.skywars.nms.NMS;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;

public class ArmorStandLB extends LeaderBoard {

  private int ranking;

  private IArmorStand stand;
  private Hologram hologram;

  public ArmorStandLB(String id, Location location, LeaderBoardStats stats, int ranking) {
    super(id, location, stats);
    this.ranking = ranking;
    if (!location.getChunk().isLoaded()) {
      location.getChunk().load(true);
    }
  }

  @Override
  public void update() {
    try {
      CachedRowSet rs = Database.getInstance().query(stats.getSQL());
      String value = null;
      if (rs != null) {
        rs.beforeFirst();
        int slot = 1;
        while (rs.next()) {
          if (ranking == slot) {
            int count = 0;
            for (String row : stats.getStatsRows()) {
              count += rs.getInt(row);
            }

            String lastRank = Database.getInstance().query("SELECT `lastRank` FROM `premium_lostedaccount` WHERE `name` = ?", rs.getString("name")).getString("lastRank");
            value = lastRank + rs.getString("name") + " : " + StringUtils.formatNumber(count);
            break;
          }

          slot++;
        }
      }

      if (value == null) {
        value = Language.options$leaderboard$empty + " : 0";
      }

      String name = value.split(" : ")[0];
      if (stand == null) {
        stand = (IArmorStand) NMS.createArmorStand(location, "", null);
        stand.getEntity().setVisible(true);
        stand.getEntity().setBasePlate(false);
        stand.getEntity().setArms(true);
        String armor = "IRON_";
        stand.getEntity().setHelmet(BukkitUtils.deserializeItemStack("SKULL_ITEM:3 : 1 : owner=" + name));
        stand.getEntity().setChestplate(BukkitUtils.deserializeItemStack(armor + "CHESTPLATE : 1"));
        stand.getEntity().setLeggings(BukkitUtils.deserializeItemStack(armor + "LEGGINGS : 1"));
        stand.getEntity().setBoots(BukkitUtils.deserializeItemStack(armor + "BOOTS : 1"));
        double remove = -0.33;
        if (String.valueOf(location.getY()).endsWith("\\.5")) {
          remove -= -0.4;
        }
        stand.getEntity().teleport(location);

        List<String> lines = new ArrayList<>();
        for (String str : Language.options$leaderboard$armorstand$lines) {
          lines.add(str.replace("{playerstats}", value.split(" : ")[1]).replace("{name}", name).replace("{position}", String.valueOf(ranking)).replace("{stats}", stats.getStatsArmorStand()));
        }
        hologram = Holograms.createHologram(location.clone().add(0, remove, 0), lines);
      } else {
        int index = 1;
        for (String str : Language.options$leaderboard$armorstand$lines) {
          hologram.updateLine(index++,
              str.replace("{playerstats}", value.split(" : ")[1]).replace("{name}", name).replace("{position}", String.valueOf(ranking)).replace("{stats}", stats.getStatsArmorStand()));
        }
        stand.getEntity().setHelmet(BukkitUtils.deserializeItemStack("SKULL_ITEM:3 : 1 : owner=" + name));
      }
    } catch (SQLException ex) {
      LOGGER.log(LostLevel.WARNING, "update(): ", ex);
    }
  }

  @Override
  public void destroy() {
    if (stand != null) {
      stand.killEntity();
    }
    Holograms.removeHologram(hologram);

    this.stand = null;
    this.hologram = null;
  }

  @Override
  public LeaderBoardType getType() {
    return LeaderBoardType.ARMORSTAND;
  }

  @Override
  public int getRanking() {
    return ranking;
  }
}
