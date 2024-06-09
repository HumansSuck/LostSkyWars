package tk.kanaostore.losteddev.skywars.ui;

import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

public enum SkyWarsType {
    NORMAL(1),
    INSANE(2),
    RANKED(3),
    DUELS(4);

  private int index;
  private String name;

  SkyWarsType(int index) {
    this.index = index;
  }

  public String getName() {
    return StringUtils.stripColors(name);
  }

  public int getIndex() {
    return index;
  }

  public String getColoredName() {
    return name;
  }

  public void translate() {
    if (this == NORMAL) {
      this.name = Language.options$type$normal;
    } else if (this == INSANE) {
      this.name = Language.options$type$insane;
    } else if (this == RANKED) {
      this.name = Language.options$type$ranked;
    } else if (this == DUELS) {
      this.name = Language.options$type$duels;
    }
  }

  public static SkyWarsType fromName(String name) {
    for (SkyWarsType mode : SkyWarsType.values()) {
      if (mode.name().equalsIgnoreCase(name)) {
        return mode;
      }
    }

    return null;
  }

  public static SkyWarsType fromIndex(int index) {
    for (SkyWarsType mode : SkyWarsType.values()) {
      if (mode.index == index) {
        return mode;
      }
    }

    return null;
  }
}
