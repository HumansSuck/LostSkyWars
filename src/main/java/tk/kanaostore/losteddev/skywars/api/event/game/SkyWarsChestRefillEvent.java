package tk.kanaostore.losteddev.skywars.api.event.game;

import tk.kanaostore.losteddev.skywars.api.event.SkyWarsEvent;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsServer;

public class SkyWarsChestRefillEvent extends SkyWarsEvent {
  
  private SkyWarsServer server;
  
  public SkyWarsChestRefillEvent(SkyWarsServer server) {
    this.server = server;
  }
  
  public SkyWarsServer getServer() {
    return server;
  }
}
