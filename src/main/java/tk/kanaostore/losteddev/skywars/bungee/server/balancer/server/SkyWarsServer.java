package tk.kanaostore.losteddev.skywars.bungee.server.balancer.server;

import tk.kanaostore.losteddev.skywars.api.server.SkyWarsState;

public class SkyWarsServer extends BungeeServer {
  
  private String map;
  private SkyWarsState state;

  public SkyWarsServer(String serverId, int maxPlayers, SkyWarsState state) {
    super(serverId, maxPlayers);
    this.state = state;
  }
  
  public void setMap(String map) {
    this.map = map;
  }
  
  public void setState(SkyWarsState state) {
    this.state = state;
  }
  
  @Override
  public boolean canBeSelected() {
    return super.canBeSelected() && !isInProgress();
  }
  
  public boolean isInProgress() {
    return state != SkyWarsState.WAITING;
  }
  
  public String getMap() {
    return this.map;
  }
  
  public SkyWarsState getState() {
    return this.state;
  }
}
