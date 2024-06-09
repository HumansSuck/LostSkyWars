package tk.kanaostore.losteddev.skywars.cmd.sw;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.cmd.SubCommand;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

public class RemoveCommand extends SubCommand {

  public RemoveCommand() {
    super("remove");
  }

  @Override
  public void perform(CommandSender sender, String[] args) {
    if (args.length == 0) {
      sender.sendMessage(
          " \n§dRemove - Help\n \n§6/lsw remove coins <player> <amount> §f- §7Remove coins from a player.\n§6/lsw remove souls <player> <amount> §f- §7Remove souls from a player.\n ");
      return;
    }

    String type = args[0];
    if (type.equalsIgnoreCase("coins") || type.equalsIgnoreCase("souls")) {
      if (args.length < 3) {
        sender.sendMessage("§cUse /lsw remove " + type.toLowerCase() + " <player> <amount>");
        return;
      }

      Player target = Bukkit.getPlayerExact(args[1]);
      Account account = null;
      boolean save = false;
      if (target == null || (account = Database.getInstance().getAccount(target.getUniqueId())) == null) {
        save = true;
        account = Database.getInstance().loadOffline(args[1]);
      }

      if (account == null) {
        sender.sendMessage("§5[LostSkyWars] §cUser not found!");
        return;
      }

      try {
        if (args[2].startsWith("-") || args[2].equals("0")) {
          throw new NumberFormatException();
        }

        int amount = Integer.parseInt(args[2]);
        account.removeStat(type.toLowerCase(), amount);
        if (account.getInt(type.toLowerCase()) < 0) {
          account.getContainers("skywars").get(type.toLowerCase()).set(0);
        }

        sender.sendMessage(
            "§5[LostSkyWars] §aChanged " + account.getName() + " " + type.toLowerCase() + " to §b" + StringUtils.formatNumber(account.getInt(type.toLowerCase())) + "§a.");

        // Salva usu§rio caso n§o esteja online.
        if (save) {
          account.save();
          account.destroy();
        }
      } catch (NumberFormatException ex) {
        sender.sendMessage("§5[LostSkyWars] §cInvalid amount (amount > 0 & amount < " + Integer.MAX_VALUE + ")");
      }
    } else {
      sender.sendMessage(
          " \n§dRemove - Help\n \n§6/lsw remove coins <player> <amount> §f- §7Remove coins from a player.\n§6/lsw remove souls <player> <amount> §f- §7Remove souls from a player.\n ");
    }
  }

  @Override
  public void perform(Player player, String[] args) {}

  @Override
  public String getUsage() {
    return "remove";
  }

  @Override
  public String getDescription() {
    return "Remove coins/souls.";
  }

  @Override
  public boolean onlyForPlayer() {
    return false;
  }
}
