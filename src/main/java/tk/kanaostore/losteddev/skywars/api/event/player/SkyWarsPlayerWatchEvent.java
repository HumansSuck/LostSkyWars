package tk.kanaostore.losteddev.skywars.api.event.player;

import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.api.event.SkyWarsEvent;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsServer;

public class SkyWarsPlayerWatchEvent extends SkyWarsEvent {
  
  private SkyWarsServer server;
  private Player watcher;
  private Player target;
  
  public SkyWarsPlayerWatchEvent(SkyWarsServer server, Player watcher, Player killer) {
    this.server = server;
    this.watcher = watcher;
    this.target = killer;
  }
  
  public SkyWarsServer getServer() {
    return server;
  }
  
  public Player getWatcher() {
    return watcher;
  }
  
  public Player getTarget() {
    return target;
  }
}
