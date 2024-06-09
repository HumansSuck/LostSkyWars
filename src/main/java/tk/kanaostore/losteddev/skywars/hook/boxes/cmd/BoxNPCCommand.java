package tk.kanaostore.losteddev.skywars.hook.boxes.cmd;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.cmd.SubCommand;
import tk.kanaostore.losteddev.skywars.hook.boxes.BoxNPC;

public class BoxNPCCommand extends SubCommand {

  public BoxNPCCommand() {
    super("boxnpc");
  }
  
  @Override
  public void perform(CommandSender sender, String[] args) {}

  @Override
  public void perform(Player player, String[] args) {
    if (args.length == 0) {
      player.sendMessage(" \n§dBoxNPC - Help\n \n§6/lsw boxnpc add <id> §f- §7Spawn an MysteryVault.\n§6/lsw boxnpc remove <id> §f- §7Remove an MysteryVault.\n ");
      return;
    }

    String action = args[0];
    if (action.equalsIgnoreCase("add")) {
      if (args.length < 2) {
        player.sendMessage("§cUse /lsw boxnpc add <id>");
        return;
      }

      BoxNPC npc = BoxNPC.getById(args[1]);
      if (npc != null) {
        player.sendMessage("§5[LostSkyWars] §cAlready exists an MysteryVault with id \"" + args[1] + "\"!");
        return;
      }

      Location location = player.getLocation().getBlock().getLocation().clone().add(0.5, 0, 0.5);
      location.setYaw(player.getLocation().getYaw());
      location.setPitch(player.getLocation().getPitch());
      BoxNPC.add(args[1], location);
      player.sendMessage("§5[LostSkyWars] §aMysteryVault added successfully!");
    } else if (action.equalsIgnoreCase("remove")) {
      if (args.length < 2) {
        player.sendMessage("§cUse /lsw boxnpc remove <id>");
        return;
      }

      BoxNPC npc = BoxNPC.getById(args[1]);
      if (npc == null) {
        player.sendMessage("§5[LostSkyWars] §cCannot found an MysteryVault with id \"" + args[1] + "\"!");
        return;
      }

      BoxNPC.remove(npc);
      player.sendMessage("§5[LostSkyWars] §aMysteryVault removed successfully!");
    } else {
      player.sendMessage(" \n§dBoxNPC - Help\n \n§6/lsw boxnpc add <id> §f- §7Spawn an Mystery Vault.\n§6/lsw boxnpc remove <id> §f- §7Remove an MysteryVault.\n ");
    }
  }

  @Override
  public String getUsage() {
    return "boxnpc";
  }

  @Override
  public String getDescription() {
    return "Manage Mystery Vaults.";
  }
  
  @Override
  public boolean onlyForPlayer() {
    return true;
  }
}
