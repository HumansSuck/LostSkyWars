package tk.kanaostore.losteddev.skywars.bungee.server;

public enum ServerType {

  LOBBY, SOLO_NORMAL, SOLO_INSANE, DOUBLES_NORMAL, DOUBLES_INSANE, SOLO_RANKED, DOUBLES_RANKED, SOLO_DUELS, DOUBLES_DUELS;
  
  private static final ServerType[] VALUES = ServerType.values();
  
  public static ServerType fromName(String name) {
    for (ServerType type : VALUES) {
      if (type.name().equalsIgnoreCase(name)) {
        return type;
      }
    }
    
    return null;
  }
}
