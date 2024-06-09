package tk.kanaostore.losteddev.skywars.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import tk.kanaostore.losteddev.skywars.bungee.server.ServerManager;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;

public class Bungee extends Plugin {
  
  private static Bungee instance;
  public static final LostLogger LOGGER = new LostLogger();
  
  public Bungee() {
    instance = this;
  }
  
  @Override
  public void onEnable() {
    BungeeFiles.setupFiles();
    ServerManager.getManager().enable();
    
    LOGGER.log(LostLevel.INFO, "The plugin has been enabled!");
  }
  
  @Override
  public void onDisable() {
    instance = null;
    LOGGER.log(LostLevel.INFO, "The plugin has been disabled!");
  }
  
  public static Bungee getInstance() {
    return instance;
  }
}
