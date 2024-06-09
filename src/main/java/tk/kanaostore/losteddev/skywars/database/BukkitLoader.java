package tk.kanaostore.losteddev.skywars.database;

import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.Main;

public class BukkitLoader {

  public static void start() {
    org.bukkit.Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), () -> {
      Database.getInstance().listAccounts().stream().filter(account -> account.inLobby() && account.getScoreboard() != null).forEach(account -> account.getScoreboard().update());
    }, 0, 20);

    org.bukkit.Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), () -> {
      Database.getInstance().listAccounts().stream().filter(account -> account.getScoreboard() != null).forEach(account -> account.getScoreboard().scroll());
    }, 0, Language.scoreboards$animation$update);
  }
}
