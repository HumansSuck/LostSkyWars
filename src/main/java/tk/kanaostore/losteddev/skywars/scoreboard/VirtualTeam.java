package tk.kanaostore.losteddev.skywars.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

public class VirtualTeam {

  private LostScoreboard instance;

  private String name;
  private String prefix;
  private String entry;
  private String suffix;

  private int line;

  protected VirtualTeam(LostScoreboard instance, String team, int line) {
    this.name = team;
    this.line = line;
    this.instance = instance;
  }

  public void destroy() {
    if (instance.getScoreboard() != null) {
      instance.getScoreboard().resetScores(entry);
      Team team = instance.getScoreboard().getTeam(name);
      if (team != null) {
        team.unregister();
      }
    }

    instance = null;
    name = null;
    prefix = null;
    entry = null;
    suffix = null;
    line = -1;
  }

  public void update() {
    Team team = instance.getScoreboard().getTeam(name);
    if (team == null) {
      team = instance.getScoreboard().registerNewTeam(name);
    }

    team.setPrefix(prefix);
    if (!team.hasEntry(entry)) {
      team.addEntry(entry);
    }

    team.setSuffix(suffix);
    instance.getObjective().getScore(entry).setScore(line);
  }

  public void setValue(String text) {
    // Add logging to capture the value being set

    // Truncate the value if it exceeds 32 characters
    if (text.length() > 32) {
      text = text.substring(0, 32);
    }

    text = StringUtils.translateAlternateColorCodes('&', text);

    this.entry = ChatColor.values()[line - 1].toString() + "§r";
    this.prefix = text.substring(0, Math.min(text.length(), 16));
    if (this.prefix.endsWith("§") && this.prefix.length() == 16) {
      this.prefix = this.prefix.substring(0, this.prefix.length() - 1);
      text = text.substring(prefix.length());
    } else {
      text = text.substring(Math.min(text.length(), prefix.length()));
    }

    this.suffix = ChatColor.getLastColors(prefix) + text;
    this.suffix = this.suffix.substring(0, Math.min(16, this.suffix.length()));
    if (this.suffix.endsWith("§")) {
      this.suffix = this.suffix.substring(0, this.suffix.length() - 1);
    }
  }
}
