package tk.kanaostore.losteddev.skywars.utils;

import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsState;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.world.type.DuelsServer;

public class PlayerUtils {
  
  public static String replaceAll(Player player, Player player2, String string) {
    String lastColor = StringUtils.getLastColor(player2.getDisplayName());
    if (lastColor.isEmpty()) {
      lastColor = "§7";
    }
    
    Account account = Database.getInstance().getAccount(player2.getUniqueId());
    if (account != null && account.getServer() != null && account.getServer() instanceof DuelsServer) {
      DuelsServer duels = (DuelsServer) account.getServer();
      if (duels.getState() == SkyWarsState.WAITING || duels.getState() == SkyWarsState.STARTING) {
        lastColor += "§k";
      }
    }
    
    return replaceAll(player,
        string.replace("{player2}", player2.getName())
            .replace("{display2}", player2.getDisplayName())
            .replace("{colored2}", lastColor + player2.getName() + (lastColor.contains("§k") ? "§r" : "")));
  }

  public static String replaceAll(Player player, String string) {
    String lastColor = StringUtils.getLastColor(player.getDisplayName());
    if (lastColor.isEmpty()) {
      lastColor = "§7";
    }
    
    Account account = Database.getInstance().getAccount(player.getUniqueId());
    if (account != null && account.getServer() != null && account.getServer() instanceof DuelsServer) {
      DuelsServer duels = (DuelsServer) account.getServer();
      if (duels.getState() == SkyWarsState.WAITING || duels.getState() == SkyWarsState.STARTING) {
        lastColor += "§k";
      }
    }

    return string.replace("{player}", player.getName())
        .replace("{display}", player.getDisplayName())
        .replace("{colored}", lastColor + player.getName() + (lastColor.contains("§k") ? "§r" : ""));
  }
}
