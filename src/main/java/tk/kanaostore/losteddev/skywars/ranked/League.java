package tk.kanaostore.losteddev.skywars.ranked;

import static tk.kanaostore.losteddev.skywars.ranked.Ranked.CONFIG;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

public class League {

  private String name;
  private int points;
  private int fare;

  public League(String name, int points, int fare) {
    this.name = StringUtils.formatColors(name);
    this.points = points;
    this.fare = fare;
  }

  public String getName() {
    return name;
  }

  public int getPoints() {
    return points;
  }

  public int getFare() {
    return fare;
  }

  private static List<League> leagues = new ArrayList<>();

  static void setupLeagues() {
    ConfigurationSection section = CONFIG.getSection("leagues");
    section.getKeys(false).forEach(key -> leagues.add(new League(section.getString(key + ".name"), section.getInt(key + ".points"), section.getInt(key + ".fare"))));
    Collections.sort(leagues, (l1, l2) -> Integer.compare(l2.getPoints(), l1.getPoints()));
  }
  
  static List<League> listLeagues() {
    return leagues;
  }
}
