package tk.kanaostore.losteddev.skywars.bungee.server.balancer.server;

import java.util.Collection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.kanaostore.losteddev.skywars.bungee.server.balancer.elements.LoadBalancerObject;
import tk.kanaostore.losteddev.skywars.bungee.server.balancer.elements.NumberConnection;

public abstract class BungeeServer implements LoadBalancerObject, NumberConnection {
  
  private String serverId;
  private int maxPlayers;
  
  private boolean joinEnabled = true;

  public BungeeServer(String serverId, int maxPlayers) {
    this.serverId = serverId;
    this.maxPlayers = maxPlayers;
  }

  public String getServerId() {
    return serverId;
  }
  
  public Collection<ProxiedPlayer> getPlayers() {
    return getServerInfo().getPlayers();
  }

  public int getOnlinePlayers() {
    return getPlayers().size();
  }

  public int getMaxPlayers() {
    return maxPlayers;
  }

  public boolean isFull() {
    return getPlayers().size() >= maxPlayers;
  }

  public void setJoinEnabled(boolean joinEnabled) {
    this.joinEnabled = joinEnabled;
  }

  public boolean isJoinEnabled() {
    return joinEnabled;
  }

  public ServerInfo getServerInfo() {
    return ProxyServer.getInstance().getServerInfo(serverId);
  }

  @Override
  public boolean canBeSelected() {
    return isJoinEnabled() && !isFull();
  }

  @Override
  public int getActualNumber() {
    return getOnlinePlayers();
  }

  @Override
  public int getMaxNumber() {
    return getMaxPlayers();
  }
}
