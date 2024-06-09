package tk.kanaostore.losteddev.skywars.cmd.sw;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.cmd.SubCommand;
import tk.kanaostore.losteddev.skywars.leaderboards.LeaderBoard;
import tk.kanaostore.losteddev.skywars.leaderboards.LeaderBoardStats;
import tk.kanaostore.losteddev.skywars.leaderboards.LeaderBoardType;

public class LeaderBoardCommand extends SubCommand {
  
  public LeaderBoardCommand() {
    super("leaderboard");
  }

  @Override
  public void perform(CommandSender sender, String[] args) {}
  
  @Override
  public void perform(Player player, String[] args) {
    if (args.length == 0) {
      player.sendMessage(
          " \n§dLeaderBoard - Help\n \n§6/lsw leaderboard add <id> <armorstand/hologram> <kills/wins/level/ranked> -ranking §f- §7Spawn an LeaderBoard.\n§6/lsw leaderboard remove <id> §f- §7Remove an LeaderBoard.\n ");
      return;
    }

    String arg = args[0];
    if (arg.equalsIgnoreCase("add")) {
      if (args.length < 4) {
        player.sendMessage("§cUse /lsw leaderboard add <id> <armorstand/hologram> <kills/wins/level/ranked> -ranking");
        return;
      }

      String id = args[1];
      if (LeaderBoard.getById(id) != null) {
        player.sendMessage("§5[LostSkyWars] §cAlready exists an LeaderBoard with id \"" + id + "\"!");
        return;
      }

      LeaderBoardType type = LeaderBoardType.fromName(args[2]);
      if (type == null) {
        player.sendMessage("§5[LostSkyWars] §cInvalid LeaderBoard Type.");
        return;
      }

      LeaderBoardStats stats = LeaderBoardStats.fromName(args[3]);
      if (stats == null) {
        player.sendMessage("§5[LostSkyWars] §cInvalid LeaderBoard Stats.");
        return;
      }

      int ranking = 0;
      if (type == LeaderBoardType.ARMORSTAND) {
        if (args.length < 5) {
          player.sendMessage("§cUse /lsw leaderboard add <id> <armorstand> <" + stats.name().toLowerCase() + "> <ranking>");
          return;
        }

        try {
          if (args[4].startsWith("-")) {
            throw new NumberFormatException();
          }

          ranking = Integer.parseInt(args[4]);
          if (ranking < 1 || ranking > 100) {
            throw new NumberFormatException();
          }
        } catch (NumberFormatException ex) {
          player.sendMessage("§5[LostSkyWars] §cInvalid ranking number. (ranking > 0 & ranking < 101)");
          return;
        }
      }
      
      Location location = player.getLocation().clone();
      location.setX(location.getBlock().getLocation().getX() + 0.5);
      location.setZ(location.getBlock().getLocation().getZ() + 0.5);
      LeaderBoard.add(LeaderBoard.fromType(id, location, stats, type, ranking));
      player.sendMessage("§5[LostSkyWars] §aLeaderBoard added successfully!");
    } else if (arg.equalsIgnoreCase("remove")) {
      if (args.length < 2) {
        player.sendMessage("§cUse /lsw leaderboard remove <id>");
        return;
      }
      
      LeaderBoard board = LeaderBoard.getById(args[1]);
      if (board == null) {
        player.sendMessage("§5[LostSkyWars] §cCannot found an LeaderBoard with id \"" + args[1] + "\"!");
        return;
      }
      
      LeaderBoard.remove(board);
      player.sendMessage("§5[LostSkyWars] §aLeaderBoard removed successfully!");
    } else {
      player.sendMessage(
          " \n§dLeaderBoard - Help\n \n§6/lsw leaderboard add <id> <armorstand/hologram> <kills/wins/level/ranked> -ranking §f- §7Spawn an LeaderBoard.\n§6/lsw leaderboard remove <id> §f- §7Remove an LeaderBoard.\n ");
    }
  }

  @Override
  public String getUsage() {
    return "leaderboard";
  }

  @Override
  public String getDescription() {
    return "Manage LeaderBoards.";
  }
  
  @Override
  public boolean onlyForPlayer() {
    return true;
  }
}
