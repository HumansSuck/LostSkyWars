package tk.kanaostore.losteddev.skywars.database;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import javax.sql.rowset.CachedRowSet;
import tk.kanaostore.losteddev.skywars.bungee.Core;
import tk.kanaostore.losteddev.skywars.bungee.CoreDatabase;
import tk.kanaostore.losteddev.skywars.database.player.StatsContainer;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;

public abstract class Database {

  public abstract Map<String, StatsContainer> loadStats(UUID id, String table, String name);

  public abstract void saveStats(UUID id, String table, Map<String, StatsContainer> map);

  public abstract Account loadAccount(UUID id, String name);
  
  public abstract Account loadOffline(String name);

  public abstract Account unloadAccount(UUID id);

  public abstract Account getAccount(UUID id);

  public abstract Collection<Account> listAccounts();

  public abstract CachedRowSet query(String query, Object... vars);
  
  private static Database instance;
  public static final LostLogger LOGGER = Core.getCoreLogger().getModule("Database");

  public static void setupDatabase() {
    CoreDatabase config = Core.getCoreDatabase();
    String type = config.getName();
    if (type.equalsIgnoreCase("MySQL")) {
      instance = new MySQLDatabase();
    } else {
      instance = new SQLiteDatabase();
    }

    try {
      Class.forName("org.bukkit.Bukkit");
      tk.kanaostore.losteddev.skywars.database.BukkitLoader.start();
    } catch (Exception ex) {}
  }

  public static Database getInstance() {
    return instance;
  }
}
