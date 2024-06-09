package tk.kanaostore.losteddev.skywars.cmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class SubCommand {
  
  private String name;
  
  public SubCommand(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  public abstract void perform(CommandSender sender, String[] args);
  
  public abstract void perform(Player player, String[] args);
  
  public abstract String getUsage();
  
  public abstract String getDescription();
  
  public abstract boolean onlyForPlayer();
}
