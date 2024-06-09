package tk.kanaostore.losteddev.skywars.ui;

import tk.kanaostore.losteddev.skywars.Language;

public enum SkyWarsMode {
    SOLO(1),
    DOUBLES(2);

  private String name;
  private int teamSize;
  
  SkyWarsMode(int teamSize) {
    this.teamSize = teamSize;
  }

  public String getName() {
    return name;
  }
  
  public int getTeamSize() {
    return teamSize;
  }

  public void translate() {
    if (this == SOLO) {
      this.name = Language.options$mode$solo;
    } else if (this == DOUBLES) {
      this.name = Language.options$mode$doubles;
    }
  }

  public static SkyWarsMode fromName(String name) {
    for (SkyWarsMode mode : SkyWarsMode.values()) {
      if (mode.name().equalsIgnoreCase(name)) {
        return mode;
      }
    }

    return null;
  }
}
