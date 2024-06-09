package tk.kanaostore.losteddev.skywars.listeners.server;

import java.lang.reflect.Field;
import java.util.Map;
import org.bukkit.entity.Player;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import tk.kanaostore.losteddev.skywars.bungee.CoreLobbies;

@SuppressWarnings("unchecked")
public class PluginMessageListener implements org.bukkit.plugin.messaging.PluginMessageListener {

  @Override
  public void onPluginMessageReceived(String channel, Player player, byte[] data) {
    if (channel.equals("LostSWAPI")) {
      ByteArrayDataInput in = ByteStreams.newDataInput(data);
      String subChannel = in.readUTF();

      if (subChannel.equals("Count")) {
        String type = in.readUTF();
        int count = in.readInt();
        try {
          Field f = CoreLobbies.class.getDeclaredField(type);
          f.set(null, count);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      } else if (subChannel.equals("MapSelector")) {
        String type = in.readUTF();
        try {
          Field f = CoreLobbies.class.getDeclaredField(type + "_MAP");
          Map<String, Integer> map = (Map<String, Integer>) f.get(null);
          map.clear();
          while (true) {
            String entry = in.readUTF();
            map.put(entry.split(" : ")[0], Integer.parseInt(entry.split(" : ")[1]));
          }
        } catch (Exception ex) {
          // ended
        }
      }
    }
  }
}
