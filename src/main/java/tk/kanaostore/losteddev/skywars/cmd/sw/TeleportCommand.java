package tk.kanaostore.losteddev.skywars.cmd.sw;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.cmd.SubCommand;

public class TeleportCommand extends SubCommand {

  public TeleportCommand() {
    super("teleport");
  }
  
  @Override
  public void perform(CommandSender sender, String[] args) {}

  @Override
  public void perform(Player player, String[] args) {
    if (args.length == 0) {
      player.sendMessage("§cUse /lsw teleport <world-name>");
      return;
    }

    World world = Bukkit.getWorld(args[0]);
    if (world != null) {
      Location location = new Location(world, 0.5, world.getHighestBlockYAt(0, 0), 0.5);
      player.teleport(location);
      player.sendMessage("§5[LostSkyWars] §aTeleported successfully!");
    } else {
      player.sendMessage("§5[LostSkyWars] §cWorld not found.");
    }
  }

  @Override
  public String getUsage() {
    return "teleport <world-name>";
  }

  @Override
  public String getDescription() {
    return "Teleport to an server world.";
  }
  
  @Override
  public boolean onlyForPlayer() {
    return true;
  }
}
