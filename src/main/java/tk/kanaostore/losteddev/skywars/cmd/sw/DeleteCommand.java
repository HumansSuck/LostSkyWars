package tk.kanaostore.losteddev.skywars.cmd.sw;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsState;
import tk.kanaostore.losteddev.skywars.cmd.SubCommand;
import tk.kanaostore.losteddev.skywars.world.WorldServer;

public class DeleteCommand extends SubCommand {

  public DeleteCommand() {
    super("delete");
  }

  @Override
  public void perform(CommandSender sender, String[] args) {
    if (args.length < 1) {
      sender.sendMessage("§cUse /lsw delete <world>");
      return;
    }

    WorldServer<?> server = WorldServer.getByWorldName(args[0]);
    if (server == null) {
      sender.sendMessage("§5[LostSkyWars] §cThis world does not have an arena");
      return;
    }

    if (server.getState() != SkyWarsState.WAITING) {
      sender.sendMessage("§5[LostSkyWars] §cThis arena is now ingame.");
      return;
    }
    
    WorldServer.removeArena(server);
    sender.sendMessage("§5[LostSkyWars] §aArena deleted.");
  }
  
  @Override
  public void perform(Player player, String[] args) {}

  @Override
  public String getUsage() {
    return "delete <world>";
  }

  @Override
  public String getDescription() {
    return "Delete an Server.";
  }
  
  @Override
  public boolean onlyForPlayer() {
    return false;
  }
}
