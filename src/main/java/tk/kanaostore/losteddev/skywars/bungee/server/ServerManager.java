package tk.kanaostore.losteddev.skywars.bungee.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsState;
import tk.kanaostore.losteddev.skywars.bungee.Bungee;
import tk.kanaostore.losteddev.skywars.bungee.server.balancer.BaseBalancer;
import tk.kanaostore.losteddev.skywars.bungee.server.balancer.server.BungeeServer;
import tk.kanaostore.losteddev.skywars.bungee.server.balancer.server.LobbyServer;
import tk.kanaostore.losteddev.skywars.bungee.server.balancer.server.SkyWarsServer;
import tk.kanaostore.losteddev.skywars.bungee.server.balancer.type.LeastConnection;
import tk.kanaostore.losteddev.skywars.bungee.server.balancer.type.MostConnection;
import tk.kanaostore.losteddev.skywars.bungee.server.listener.ServerListener;
import tk.kanaostore.losteddev.skywars.bungee.utils.BungeeConfig;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

public class ServerManager {

  private Map<String, BungeeServer> activeServers;
  private Map<ServerType, BaseBalancer<BungeeServer>> balancers;

  private ServerManager() {
    this.balancers = new HashMap<>();
    this.activeServers = new HashMap<>();

    for (ServerType type : ServerType.values()) {
      this.balancers.put(type, type == ServerType.LOBBY ? new LeastConnection<>() : new MostConnection<>());
    }
  }

  private final Map<ServerInfo, ServerPing> cache = new HashMap<>();

  public void enable() {
    ProxyServer.getInstance().registerChannel("LostSWAPI");
    ProxyServer.getInstance().getPluginManager().registerListener(Bungee.getInstance(), new ServerListener());
    
    BungeeConfig config = BungeeConfig.getConfig("servers");
    List<String> fetchList = new ArrayList<>();
    fetchList.addAll(config.getStringList("lobbies"));
    fetchList.addAll(config.getStringList("arenas"));

    ProxyServer.getInstance().getScheduler().schedule(Bungee.getInstance(), new Runnable() {

      @Override
      public void run() {
        for (String toFetch : fetchList) {
          ServerInfo info = ProxyServer.getInstance().getServerInfo(toFetch);
          if (info != null) {
            ServerPing ping = cache.get(info);
            if (ping == null) {
              ping = new ServerPing(info.getAddress());
              cache.put(info, ping);
            }

            try {
              ping.fetch();
            } catch (Exception ex) {
              // Down?
            }

            updateActiveServer(info, ping);
          }
        }
      }
    }, 0, 2, TimeUnit.SECONDS);
  }

  public void updateActiveServer(ServerInfo info, ServerPing ping) {
    BungeeServer server = this.activeServers.get(info.getName());
    if (ping.getMotd() == null || !ping.getMotd().contains(";")) {
      if (server != null) {
        server.setJoinEnabled(false);
      }
      return;
    }

    // SOLO_NORMAL; Boletum; STARTING;
    String motd = StringUtils.stripColors(ping.getMotd());
    String[] splitted = motd.split("; ");
    ServerType type = null;
    String map = null;
    SkyWarsState state = null;
    if (splitted.length < 2) {
      type = ServerType.LOBBY;
    } else {
      type = ServerType.valueOf(splitted[0]);
      map = splitted[1];
      state = SkyWarsState.valueOf(splitted[2]);
    }

    if (server == null) {
      if (type == ServerType.LOBBY) {
        server = new LobbyServer(info.getName(), ping.getMax());
      } else {
        server = new SkyWarsServer(info.getName(), ping.getMax(), state);
      }

      activeServers.put(info.getName(), server);
      balancers.get(type).add(info.getName(), server);
    }

    if (server instanceof SkyWarsServer) {
      SkyWarsServer ss = (SkyWarsServer) server;
      ss.setMap(map);
      ss.setState(SkyWarsState.valueOf(splitted[2]));
      ss.setJoinEnabled(!ss.isInProgress() && !server.isFull());
    } else {
      server.setJoinEnabled(!server.isFull());
    }
  }
  
  public BaseBalancer<BungeeServer> getBalancer(ServerType type) {
    return this.balancers.get(type);
  }

  private static final ServerManager INSTANCE = new ServerManager();

  public static ServerManager getManager() {
    return INSTANCE;
  }
}
