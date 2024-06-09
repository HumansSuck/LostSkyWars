package tk.kanaostore.losteddev.skywars.leaderboards;

public enum LeaderBoardType {
  HOLOGRAM,
  ARMORSTAND;
  
  public static LeaderBoardType fromName(String name) {
    for (LeaderBoardType type : values()) {
      if (type.name().equalsIgnoreCase(name)) {
        return type;
      }
    }
    
    return null;
  }
}
