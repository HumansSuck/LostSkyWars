package tk.kanaostore.losteddev.skywars.cmd.sw;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.cmd.SubCommand;
import tk.kanaostore.losteddev.skywars.well.WellNPC;

public class WellNPCCommand extends SubCommand {

  public WellNPCCommand() {
    super("wellnpc");
  }
  
  @Override
  public void perform(CommandSender sender, String[] args) {}

  @Override
  public void perform(Player player, String[] args) {
    if (args.length == 0) {
      player.sendMessage(" \n§dWellNPC - Help\n \n§6/lsw wellnpc add <id> §f- §7Spawn an WellNPC.\n§6/lsw wellnpc remove <id> §f- §7Remove an WellNPC.\n ");
      return;
    }

    String action = args[0];
    if (action.equalsIgnoreCase("add")) {
      if (args.length < 2) {
        player.sendMessage("§cUse /lsw wellnpc add <id>");
        return;
      }

      WellNPC npc = WellNPC.getById(args[1]);
      if (npc != null) {
        player.sendMessage("§5[LostSkyWars] §cAlready exists an WellNPC with id \"" + args[1] + "\"!");
        return;
      }

      Location location = player.getLocation().getBlock().getLocation().clone().add(0.5, 0, 0.5);
      location.setYaw(player.getLocation().getYaw());
      location.setPitch(player.getLocation().getPitch());
      WellNPC.add(args[1], location);
      player.sendMessage("§5[LostSkyWars] §aWellNPC added successfully!");
    } else if (action.equalsIgnoreCase("remove")) {
      if (args.length < 2) {
        player.sendMessage("§cUse /lsw wellnpc remove <id>");
        return;
      }

      WellNPC npc = WellNPC.getById(args[1]);
      if (npc == null) {
        player.sendMessage("§5[LostSkyWars] §cCannot found an WellNPC with id \"" + args[1] + "\"!");
        return;
      }

      WellNPC.remove(npc);
      player.sendMessage("§5[LostSkyWars] §aWellNPC removed successfully!");
    } else {
      player.sendMessage(" \n§dWellNPC - Help\n \n§6/lsw wellnpc add <id> §f- §7Spawn an WellNPC.\n§6/lsw wellnpc remove <id> §f- §7Remove an WellNPC.\n ");
    }
  }

  @Override
  public String getUsage() {
    return "wellnpc";
  }

  @Override
  public String getDescription() {
    return "Manage SoulWell NPCs.";
  }
  
  @Override
  public boolean onlyForPlayer() {
    return true;
  }
}
