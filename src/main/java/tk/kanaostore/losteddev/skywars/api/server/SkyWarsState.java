package tk.kanaostore.losteddev.skywars.api.server;

public enum SkyWarsState {
  NONE("None"),
  WAITING("Waiting"),
  STARTING("Starting"),
  INGAME("InGame"),
  ENDED("Ended");
  
  private String name;
  
  SkyWarsState(String name) {
    this.name = name;
  }
  
  public boolean canJoin() {
    return this == WAITING;
  }
  
  public String getName() {
    return name;
  }
}
