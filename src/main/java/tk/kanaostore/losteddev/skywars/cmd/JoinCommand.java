package tk.kanaostore.losteddev.skywars.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;
import tk.kanaostore.losteddev.skywars.world.WorldServer;

public class JoinCommand extends Command {

  public JoinCommand() {
    super("joingame");

    try {
      SimpleCommandMap simpleCommandMap = (SimpleCommandMap) Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap").invoke(Bukkit.getServer());
      simpleCommandMap.register(this.getName(), "lostskywars", this);
    } catch (ReflectiveOperationException ex) {
      Main.LOGGER.log(LostLevel.SEVERE, "Could not register command: ", ex);
    }
  }

  @Override
  public boolean execute(CommandSender sender, String label, String[] args) {
    if (sender instanceof Player) {
      Player player = (Player) sender;
      Account account = Database.getInstance().getAccount(player.getUniqueId());
      if (account != null) {
        if (account.getServer() != null) {
          return true;
        }
        
        if (!player.hasPermission("lostskywars.cmd.join")) {
          player.sendMessage(Language.command$join_game$permission);
          return true;
        }

        if (args.length == 0) {
          player.sendMessage(Language.command$join_game$args);
          return true;
        }

        WorldServer<?> server = WorldServer.getByWorldName(args[0]);
        if (server == null) {
          player.sendMessage(Language.command$join_game$game_not_found);
          return true;
        }
        
        if (!server.getState().canJoin()) {
          player.sendMessage(Language.command$join_game$game_already_started);
          return true;
        }
        
        if (server.getOnline() < server.getMaxPlayers()) {
          server.connect(account);
        }
      }
    }

    return false;
  }
}
