package tk.kanaostore.losteddev.skywars.cmd.sw;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.cmd.SubCommand;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;

public class SetLobbyCommand extends SubCommand {

  private static final ConfigUtils CONFIG = ConfigUtils.getConfig("locations");

  public SetLobbyCommand() {
    super("setlobby");
  }

  @Override
  public void perform(CommandSender sender, String[] args) {}
  
  @Override
  public void perform(Player player, String[] args) {
    player.performCommand("setworldspawn");
    CONFIG.set("lobby", BukkitUtils.serializeLocation(player.getLocation()));
    CONFIG.reload();

    player.sendMessage("§5[LostSkyWars] §aThe lobby was set up successfully!");
  }

  @Override
  public String getUsage() {
    return "setlobby";
  }

  @Override
  public String getDescription() {
    return "Set lobby location of your server.";
  }
  
  @Override
  public boolean onlyForPlayer() {
    return true;
  }

  public static Location getSpawnLocation() {
    if (CONFIG.contains("lobby")) {
      return BukkitUtils.deserializeLocation(CONFIG.getString("lobby"));
    }

    return Bukkit.getWorlds().get(0).getSpawnLocation();
  }
}
