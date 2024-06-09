package tk.kanaostore.losteddev.skywars.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

@SuppressWarnings("deprecation")
public abstract class LostScoreboard {

  private Player player;
  private Objective objective;
  private Scoreboard scoreboard;
  private ScoreboardScroller scroller;

  private String display;
  private boolean health, healthTab;

  private VirtualTeam[] teams = new VirtualTeam[15];

  public LostScoreboard() {}

  public void scroll() {
    if (scroller != null) {
      display(scroller.next());
    }
  }

  public void update() {}

  public void updateHealth() {
    if ((healthTab || health) && scoreboard != null) {
      for (Player player : Bukkit.getServer().getOnlinePlayers()) {
        int level = (int) player.getHealth();
        if (healthTab) {
          Objective objective = scoreboard.getObjective("healthPL");
          if (objective != null) {
            objective.getScore(player.getName()).setScore(level);
          }
        }

        if (health) {
          Objective objective = scoreboard.getObjective("healthBN");
          if (objective != null && objective.getScore(player.getName()).getScore() == 0) {
            objective.getScore(player.getName()).setScore(level);
          }
        }
      }
    }
  }

  public LostScoreboard add(int line) {
    return add(line, "");
  }

  public LostScoreboard add(int line, String name) {
    if (line > 15 || line < 1) {
      return this;
    }

    VirtualTeam team = getOrCreate(line);
    team.setValue(name);
    if (scoreboard != null) {
      team.update();
    }
    return this;
  }

  public LostScoreboard remove(int line) {
    if (line > 15 || line < 1) {
      return this;
    }

    VirtualTeam team = teams[line - 1];
    if (team != null) {
      team.destroy();
      teams[line - 1] = null;
    }

    return this;
  }

  public LostScoreboard to(Player player) {
    Player lastPlayer = this.player;
    this.player = player;
    if (scoreboard != null) {
      if (lastPlayer != null) {
        lastPlayer.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
      }

      player.setScoreboard(scoreboard);
    }

    lastPlayer = null;
    return this;
  }

  public LostScoreboard display(String display) {
    this.display = StringUtils.translateAlternateColorCodes('&', display);
    if (objective != null) {
      objective.setDisplayName(this.display.substring(0, Math.min(this.display.length(), 32)));
    }

    return this;
  }

  public LostScoreboard scroller(ScoreboardScroller ss) {
    this.scroller = ss;
    return this;
  }

  public LostScoreboard health() {
    this.health = !health;
    if (scoreboard != null) {
      if (!health) {
        scoreboard.getObjective("healthBN").unregister();
      } else {
        Objective health = this.scoreboard.registerNewObjective("healthBN", "health");
        health.setDisplayName("§c❤");
        health.setDisplaySlot(DisplaySlot.BELOW_NAME);
      }
    }

    return this;
  }

  public LostScoreboard healthTab() {
    this.healthTab = !healthTab;
    if (scoreboard != null) {
      if (!healthTab) {
        scoreboard.getObjective("healthPL").unregister();
      } else {
        Objective health = this.scoreboard.registerNewObjective("healthPL", "dummy");
        health.setDisplaySlot(DisplaySlot.PLAYER_LIST);
      }
    }

    return this;
  }

  public LostScoreboard build() {
    this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    this.objective = scoreboard.registerNewObjective(getObjectiveName(), "dummy");
    this.objective.setDisplayName(
        this.display == null ? "" : this.display.substring(0, Math.min(this.display.length(), 32)));
    this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    Team t = scoreboard.registerNewTeam("npcs");
    t.setNameTagVisibility(NameTagVisibility.NEVER);

    t.addEntry("§8[NPC] ");

    if (player != null) {
      player.setScoreboard(scoreboard);
    }

    if (health) {
      Objective health = this.scoreboard.registerNewObjective("healthBN", "health");
      health.setDisplayName("§c❤");
      health.setDisplaySlot(DisplaySlot.BELOW_NAME);
    }

    if (healthTab) {
      Objective health = this.scoreboard.registerNewObjective("healthPL", "dummy");
      health.setDisplaySlot(DisplaySlot.PLAYER_LIST);
    }

    for (VirtualTeam team : teams) {
      if (team != null) {
        team.update();
      }
    }

    return this;
  }

  public void destroy() {
    this.objective.unregister();
    this.objective = null;
    if (health) {
      this.scoreboard.getObjective("healthBN").unregister();
    }
    if (healthTab) {
      this.scoreboard.getObjective("healthPL").unregister();
    }
    this.scoreboard = null;
    this.teams = null;
    this.player = null;
    this.display = null;
  }

  public VirtualTeam getTeam(int line) {
    if (line > 15 || line < 1) {
      return null;
    }

    return teams[line - 1];
  }

  public VirtualTeam getOrCreate(int line) {
    if (line > 15 || line < 1) {
      return null;
    }

    if (teams[line - 1] == null) {
      teams[line - 1] = new VirtualTeam(this, "score[" + line + "]", line);
    }

    return teams[line - 1];
  }

  public String getObjectiveName() {
    return "LostScoreboard";
  }

  public Scoreboard getScoreboard() {
    return scoreboard;
  }

  public Objective getObjective() {
    return objective;
  }
}
