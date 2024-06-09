package tk.kanaostore.losteddev.skywars.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.bungee.Core;
import tk.kanaostore.losteddev.skywars.bungee.CoreMode;
import tk.kanaostore.losteddev.skywars.listeners.entity.EntityListener;
import tk.kanaostore.losteddev.skywars.listeners.player.AsyncPlayerChatListener;
import tk.kanaostore.losteddev.skywars.listeners.player.InventoryClickListener;
import tk.kanaostore.losteddev.skywars.listeners.player.PlayerDeathListener;
import tk.kanaostore.losteddev.skywars.listeners.player.PlayerInteractListener;
import tk.kanaostore.losteddev.skywars.listeners.player.PlayerJoinListener;
import tk.kanaostore.losteddev.skywars.listeners.player.PlayerLoginListener;
import tk.kanaostore.losteddev.skywars.listeners.player.PlayerQuitListener;
import tk.kanaostore.losteddev.skywars.listeners.player.PlayerRestListener;
import tk.kanaostore.losteddev.skywars.listeners.server.PluginMessageListener;
import tk.kanaostore.losteddev.skywars.listeners.server.ServerListener;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;

public class Listeners implements Listener {

  public static final LostLogger LOGGER = Main.LOGGER.getModule("Listeners");

  public static void setupListeners() {
    Plugin plugin = Main.getInstance();
    PluginManager pm = Bukkit.getPluginManager();
    
    pm.registerEvents(new EntityListener(), plugin);

    pm.registerEvents(new AsyncPlayerChatListener(), plugin);
    pm.registerEvents(new InventoryClickListener(), plugin);
    pm.registerEvents(new PlayerDeathListener(), plugin);
    pm.registerEvents(new PlayerInteractListener(), plugin);
    pm.registerEvents(new PlayerLoginListener(), plugin);
    pm.registerEvents(new PlayerJoinListener(), plugin);
    pm.registerEvents(new PlayerQuitListener(), plugin);
    pm.registerEvents(new PlayerRestListener(), plugin);
    
    pm.registerEvents(new ServerListener(), plugin);
    
    if (Core.MODE != CoreMode.MULTI_ARENA) {
      Bukkit.getMessenger().registerIncomingPluginChannel(plugin, "LostSWAPI", new PluginMessageListener());
    }
  }
}
