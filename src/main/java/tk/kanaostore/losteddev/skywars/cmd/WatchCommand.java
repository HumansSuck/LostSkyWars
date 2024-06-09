package tk.kanaostore.losteddev.skywars.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsServer;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsState;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;

public class WatchCommand extends Command {

  public WatchCommand() {
    super("watch");
    
    try {
      SimpleCommandMap simpleCommandMap = (SimpleCommandMap) Bukkit.getServer().getClass()
          .getDeclaredMethod("getCommandMap").invoke(Bukkit.getServer());
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

        if (!player.hasPermission("lostskywars.cmd.watch")) {
          player.sendMessage(Language.command$watch$permission);
          return true;
        }

        if (args.length == 0) {
          player.sendMessage(Language.command$watch$args);
          return true;
        }

        Account acc = null;
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || (acc = Database.getInstance().getAccount(target.getUniqueId())) == null) {
          player.sendMessage(Language.command$watch$user_not_found);
          return true;
        }

        SkyWarsServer server = acc.getServer();
        if (server == null || server.getState() != SkyWarsState.INGAME || server.isSpectator(target)) {
          player.sendMessage(Language.command$watch$user_not_in_match);
          return true;
        }

        player.sendMessage(Language.lobby$npcs$play$connecting.replace("{world}", server.getServerName()));
        server.spectate(account, target);
      }
    }

    return false;
  }
}
