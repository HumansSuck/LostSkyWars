package tk.kanaostore.losteddev.skywars.menu.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import tk.kanaostore.losteddev.skywars.Main;

public class PlayerMenu extends Menu implements Listener {
  
  protected Player player;
  
  public PlayerMenu(Player player, String title) {
    this(player, title, 6);
  }
  
  public PlayerMenu(Player player, String title, int rows) {
    super(title, rows);
    this.player = player;
  }
  
  public void register() {
    Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
  }
  
  public void open() {
    this.player.openInventory(getInventory());
  }
  
  public Player getPlayer() {
    return player;
  }
}
