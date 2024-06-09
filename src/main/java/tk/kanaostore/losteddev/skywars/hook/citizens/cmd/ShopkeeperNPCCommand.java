package tk.kanaostore.losteddev.skywars.hook.citizens.cmd;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.cmd.SubCommand;
import tk.kanaostore.losteddev.skywars.hook.citizens.ShopkeeperNPC;

public class ShopkeeperNPCCommand extends SubCommand {

  public ShopkeeperNPCCommand() {
    super("shopkeepernpc");
  }
  
  @Override
  public void perform(CommandSender sender, String[] args) {}

  @Override
  public void perform(Player player, String[] args) {
    if (args.length == 0) {
      player.sendMessage(" \n§dShopkeeperNPC - Help\n \n§6/lsw shopkeepernpc add <id> §f- §7Spawn an Shopkeeper.\n§6/lsw shopkeepernpc remove <id> §f- §7Remove an Shopkeeper.\n ");
      return;
    }

    String action = args[0];
    if (action.equalsIgnoreCase("add")) {
      if (args.length < 2) {
        player.sendMessage("§cUse /lsw shopkeepernpc add <id>");
        return;
      }

      ShopkeeperNPC npc = ShopkeeperNPC.getById(args[1]);
      if (npc != null) {
        player.sendMessage("§5[LostSkyWars] §cAlready exists an Shopkeeper with id \"" + args[1] + "\"!");
        return;
      }

      Location location = player.getLocation().getBlock().getLocation().clone().add(0.5, 0, 0.5);
      location.setYaw(player.getLocation().getYaw());
      location.setPitch(player.getLocation().getPitch());
      ShopkeeperNPC.add(args[1], location);
      player.sendMessage("§5[LostSkyWars] §aShopkeeper added successfully!");
    } else if (action.equalsIgnoreCase("remove")) {
      if (args.length < 2) {
        player.sendMessage("§cUse /lsw shopkeepernpc remove <id>");
        return;
      }

      ShopkeeperNPC npc = ShopkeeperNPC.getById(args[1]);
      if (npc == null) {
        player.sendMessage("§5[LostSkyWars] §cCannot found an Shopkeeper with id \"" + args[1] + "\"!");
        return;
      }

      ShopkeeperNPC.remove(npc);
      player.sendMessage("§5[LostSkyWars] §aShopkeeper removed successfully!");
    } else {
      player.sendMessage(" \n§dShopkeeperNPC - Help\n \n§6/lsw shopkeepernpc add <id> §f- §7Spawn an Shopkeeper.\n§6/lsw shopkeepernpc remove <id> §f- §7Remove an Shopkeeper.\n ");
    }
  }

  @Override
  public String getUsage() {
    return "shopkeepernpc";
  }

  @Override
  public String getDescription() {
    return "Manage Shopkeeper NPCs.";
  }
  
  @Override
  public boolean onlyForPlayer() {
    return true;
  }
}
