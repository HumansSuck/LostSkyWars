package tk.kanaostore.losteddev.skywars.api.event.player;

import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.api.event.SkyWarsEvent;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsServer;

public class SkyWarsPlayerJoinEvent extends SkyWarsEvent {
  
  private SkyWarsServer server;
  private Player player;
  
  public SkyWarsPlayerJoinEvent(SkyWarsServer server, Player player) {
    this.server = server;
    this.player = player;
  }
  
  public SkyWarsServer getServer() {
    return server;
  }
  
  public Player getPlayer() {
    return player;
  }
}
