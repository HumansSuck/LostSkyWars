package tk.kanaostore.losteddev.skywars.cmd.sw;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.cmd.SubCommand;
import tk.kanaostore.losteddev.skywars.well.AngelOfDeath;

public class AODCommand extends SubCommand {

  public AODCommand() {
    super("aod");
  }
  
  @Override
  public void perform(CommandSender sender, String[] args) {}

  @Override
  public void perform(Player player, String[] args) {
    if (args.length == 0) {
      player.sendMessage(" \n§dAngels of Death - Help\n \n§6/lsw aod add <id> §f- §7Spawn an Angel of Death.\n§6/lsw aod remove <id> §f- §7Remove an Angel of Death.\n ");
      return;
    }

    String action = args[0];
    if (action.equalsIgnoreCase("add")) {
      if (args.length < 2) {
        player.sendMessage("§cUse /lsw aod add <id>");
        return;
      }

      AngelOfDeath npc = AngelOfDeath.getById(args[1]);
      if (npc != null) {
        player.sendMessage("§5[LostSkyWars] §cAlready exists an Angel of Death with id \"" + args[1] + "\"!");
        return;
      }

      Location location = player.getLocation().getBlock().getLocation().clone().add(0.5, 2.0, 0.5);
      location.setYaw(player.getLocation().getYaw());
      location.setPitch(player.getLocation().getPitch());
      AngelOfDeath.add(args[1], location);
      player.sendMessage("§5[LostSkyWars] §aAngel of Death added successfully!");
    } else if (action.equalsIgnoreCase("remove")) {
      if (args.length < 2) {
        player.sendMessage("§cUse /lsw aod remove <id>");
        return;
      }

      AngelOfDeath npc = AngelOfDeath.getById(args[1]);
      if (npc == null) {
        player.sendMessage("§5[LostSkyWars] §cCannot found an Angel of Death with id \"" + args[1] + "\"!");
        return;
      }

      AngelOfDeath.remove(npc);
      player.sendMessage("§5[LostSkyWars] §aAngel of Death removed successfully!");
    } else {
      player.sendMessage(" \n§dAngels of Death - Help\n \n§6/lsw aod add <id> §f- §7Spawn an Angel of Death.\n§6/lsw aod remove <id> §f- §7Remove an Angel of Death.\n ");
    }
  }

  @Override
  public String getUsage() {
    return "aod";
  }

  @Override
  public String getDescription() {
    return "Manage Angels of Death.";
  }
  
  @Override
  public boolean onlyForPlayer() {
    return true;
  }
}
