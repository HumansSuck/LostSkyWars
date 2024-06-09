package tk.kanaostore.losteddev.skywars.hook;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.hook.protocollib.ProtocolListener;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;

public class ProtocolLibHook {
  
  public static final LostLogger LOGGER = Main.LOGGER.getModule("ProtocolLibHook");
  
  private static final ProtocolListener LISTENER  = new ProtocolListener();
  
  public static void setupProtocolLib() {
    getProtocolManager().addPacketListener(LISTENER);
  }
  
  public static void destroyProtocolLib() {
    getProtocolManager().removePacketListener(LISTENER);
    
    LOGGER.log(LostLevel.INFO, "ProtocolLib found, destroying StatsNPC...");
  }
  
  public static ProtocolManager getProtocolManager() {
    return ProtocolLibrary.getProtocolManager();
  }
}
