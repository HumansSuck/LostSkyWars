package tk.kanaostore.losteddev.skywars.bungee.server.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import tk.kanaostore.losteddev.skywars.bungee.server.ServerManager;
import tk.kanaostore.losteddev.skywars.bungee.server.ServerType;
import tk.kanaostore.losteddev.skywars.bungee.server.balancer.server.BungeeServer;
import tk.kanaostore.losteddev.skywars.bungee.server.balancer.server.SkyWarsServer;
import tk.kanaostore.losteddev.skywars.bungee.server.balancer.type.MostConnection;

public class ServerListener implements Listener {
  
  private ServerManager manager;
  
  public ServerListener() {
    this.manager = ServerManager.getManager();
  }
  
  @EventHandler
  public void onPluginMessage(PluginMessageEvent evt) {
    if (evt.getTag().equals("LostSWAPI") && evt.getSender() instanceof Server
        && evt.getReceiver() instanceof ProxiedPlayer) {
      ByteArrayDataInput in = ByteStreams.newDataInput(evt.getData());

      String subChannel = in.readUTF();
      if (subChannel.startsWith("Lobby")) {
        ProxiedPlayer player = (ProxiedPlayer) evt.getReceiver();
        
        BungeeServer server = this.manager.getBalancer(ServerType.LOBBY).next();
        if (server != null) {
          player.connect(server.getServerInfo());
        } else {
          player.sendMessage(TextComponent.fromLegacyText("§cCould not find a available lobby!"));
        }
      } else if (subChannel.startsWith("Count")) {
        ServerType type = null;
        try {
          type = ServerType.valueOf(in.readUTF().toUpperCase());
        } catch (Exception ex) {
          return;
        }
        
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Count");
        out.writeUTF(type.name());
        out.writeInt(this.manager.getBalancer(type).getTotalNumber());
        ((Server) evt.getSender()).sendData("LostSWAPI", out.toByteArray());
      } else if (subChannel.startsWith("Play")) {
        ProxiedPlayer player = (ProxiedPlayer) evt.getReceiver();
        ServerType type = null;
        try {
          type = ServerType.valueOf(in.readUTF().toUpperCase());
        } catch (Exception ex) {
          return;
        }
        String mapFilter = in.readUTF();
        
        SkyWarsServer server = (SkyWarsServer) this.manager.getBalancer(type).next();
        if (!mapFilter.equals("all")) {
          Map<String, BungeeServer> map = new HashMap<>();
          for (BungeeServer s : manager.getBalancer(type).getList()) {
            SkyWarsServer mgs = (SkyWarsServer) s;
            if (mgs.getMap().equalsIgnoreCase(mapFilter)) {
              map.put(mgs.getServerInfo().getName(), mgs);
            }
          }
          
          MostConnection<BungeeServer> most = new MostConnection<>();
          most.addAll(map);
          server = (SkyWarsServer) most.next();
          most.destroy();
        }
        
        if (server != null) {
          player.connect(server.getServerInfo());
        }
      } else if (subChannel.startsWith("MapSelector")) {
        ServerType type = null;
        try {
          type = ServerType.valueOf(in.readUTF().toUpperCase());
        } catch (Exception ex) {
          return;
        }
        
        Map<String, Integer> set = new HashMap<>();
        List<BungeeServer> servers = manager.getBalancer(type).getList();
        for (BungeeServer server : servers) {
          if (server instanceof SkyWarsServer) {
            SkyWarsServer mg = (SkyWarsServer) server;
            String map = mg.getMap();
            if (map != null) {
              int current = set.get(map) != null ? set.get(map) : 0;
              set.put(map, current + (server.canBeSelected() ? 1 : 0));
            }
          }
        }
        
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("MapSelector");
        out.writeUTF(type.name());
        for (Entry<String, Integer> entry : set.entrySet()) {
          out.writeUTF(entry.getKey() + " : " + entry.getValue());
        }
        set.clear();
        set = null;
        ((Server) evt.getSender()).sendData("LostSWAPI", out.toByteArray());
      }
    }
  }
}
