package tk.kanaostore.losteddev.skywars.api.event.game;

import tk.kanaostore.losteddev.skywars.api.event.SkyWarsEvent;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsServer;

public class SkyWarsGameStartEvent extends SkyWarsEvent {
  
  private SkyWarsServer server;
  
  public SkyWarsGameStartEvent(SkyWarsServer server) {
    this.server = server;
  }
  
  public SkyWarsServer getServer() {
    return server;
  }
}
