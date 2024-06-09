package tk.kanaostore.losteddev.skywars.hook.citizens.cmd;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.cmd.SubCommand;
import tk.kanaostore.losteddev.skywars.hook.citizens.StatsNPC;

public class StatsNPCCommand extends SubCommand {

  public StatsNPCCommand() {
    super("statsnpc");
  }
  
  @Override
  public void perform(CommandSender sender, String[] args) {}

  @Override
  public void perform(Player player, String[] args) {
    if (args.length == 0) {
      player.sendMessage(" \n§dStatsNPC - Help\n \n§6/lsw statsnpc add <id> §f- §7Spawn an StatsNPC.\n§6/lsw statsnpc remove <id> §f- §7Remove an StatsNPC.\n ");
      return;
    }

    String action = args[0];
    if (action.equalsIgnoreCase("add")) {
      if (args.length < 2) {
        player.sendMessage("§cUse /lsw statsnpc add <id>");
        return;
      }

      StatsNPC npc = StatsNPC.getById(args[1]);
      if (npc != null) {
        player.sendMessage("§5[LostSkyWars] §cAlready exists an StatsNPC with id \"" + args[1] + "\"!");
        return;
      }

      Location location = player.getLocation().getBlock().getLocation().clone().add(0.5, 0, 0.5);
      location.setYaw(player.getLocation().getYaw());
      location.setPitch(player.getLocation().getPitch());
      StatsNPC.add(args[1], location);
      player.sendMessage("§5[LostSkyWars] §aStatsNPC added successfully!");
    } else if (action.equalsIgnoreCase("remove")) {
      if (args.length < 2) {
        player.sendMessage("§cUse /lsw statsnpc remove <id>");
        return;
      }

      StatsNPC npc = StatsNPC.getById(args[1]);
      if (npc == null) {
        player.sendMessage("§5[LostSkyWars] §cCannot found an StatsNPC with id \"" + args[1] + "\"!");
        return;
      }

      StatsNPC.remove(npc);
      player.sendMessage("§5[LostSkyWars] §aStatsNPC removed successfully!");
    } else {
      player.sendMessage(" \n§dStatsNPC - Help\n \n§6/lsw statsnpc add <id> §f- §7Spawn an StatsNPC.\n§6/lsw statsnpc remove <id> §f- §7Remove an StatsNPC.\n ");
    }
  }

  @Override
  public String getUsage() {
    return "statsnpc";
  }

  @Override
  public String getDescription() {
    return "Manage Stats NPCs.";
  }
  
  @Override
  public boolean onlyForPlayer() {
    return true;
  }
}
