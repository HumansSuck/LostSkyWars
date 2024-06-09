package tk.kanaostore.losteddev.skywars.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import com.google.common.collect.ImmutableList;
import tk.kanaostore.losteddev.skywars.bungee.Core;
import tk.kanaostore.losteddev.skywars.bungee.CoreDatabase;
import tk.kanaostore.losteddev.skywars.database.player.StatsContainer;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

public class MySQLDatabase extends Database {

  private String host;
  private String port;
  private String dbname;
  private String username;
  private String password;

  private Connection connection;
  private ExecutorService executor;

  public MySQLDatabase() {
    CoreDatabase config = Core.getCoreDatabase();
    this.host = config.getHost();
    this.port = config.getPort();
    this.dbname = config.getDbname();
    this.username = config.getUser();
    this.password = config.getPassword();

    this.openConnection();
    this.executor = Executors.newCachedThreadPool();
    this.createAccountTable();
    this.createSkyWarsTable();
    this.createRankedTable();
    if (query("SHOW COLUMNS FROM premium_lostedaccount LIKE 'leveling'") == null) {
      update("ALTER TABLE premium_lostedaccount ADD COLUMN leveling TEXT DEFAULT NULL AFTER deliveries;");
    }
    if (query("SHOW COLUMNS FROM premium_lostedskywars LIKE 'deathcry'") == null) {
      update("ALTER TABLE premium_lostedskywars ADD COLUMN deathcry TEXT DEFAULT NULL AFTER cages;");
    }
    if (query("SHOW COLUMNS FROM premium_lostedskywars LIKE 'ballons'") == null) {
      update("ALTER TABLE premium_lostedskywars ADD COLUMN ballons TEXT DEFAULT NULL AFTER deathcry;");
    }
  }

  private void createAccountTable() {
    this.update("CREATE TABLE IF NOT EXISTS premium_lostedaccount (" + "id VARCHAR(36) NOT NULL," + "name VARCHAR(32) NOT NULL," + "lastRank VARCHAR(2) NOT NULL,"
        + "mysterydusts INTEGER NOT NULL," + "sw_maxsouls INTEGER DEFAULT 100," + "sw_wellroll INTEGER DEFAULT 1," + "sw_soulswin INTEGER DEFAULT 0,"
        + "deliveries TEXT DEFAULT NULL," + "leveling TEXT DEFAULT NULL," + "players BOOLEAN NOT NULL," + "gore BOOLEAN NOT NULL,"
        + "PRIMARY KEY(id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_bin;");
  }

  private void createRankedTable() {
    this.update("CREATE TABLE IF NOT EXISTS ranked_lostedskywars (" + "id VARCHAR(36) NOT NULL," + "name VARCHAR(32) NOT NULL," + "kills INTEGER NOT NULL,"
        + "wins INTEGER NOT NULL," + "assists INTEGER NOT NULL," + "deaths INTEGER NOT NULL," + "melee INTEGER NOT NULL," + "bow INTEGER NOT NULL," + "void INTEGER NOT NULL,"
        + "plays INTEGER NOT NULL," + "points INTEGER NOT NULL," + "PRIMARY KEY(id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_bin;");
  }

  private void createSkyWarsTable() {
    this.update("CREATE TABLE IF NOT EXISTS premium_lostedskywars (" + "id VARCHAR(36) NOT NULL," + "name VARCHAR(32) NOT NULL," + "solokills INTEGER NOT NULL,"
        + "solowins INTEGER NOT NULL," + "soloassists INTEGER NOT NULL," + "solodeaths INTEGER NOT NULL," + "solomelee INTEGER NOT NULL," + "solobow INTEGER NOT NULL,"
        + "solovoid INTEGER NOT NULL," + "soloplays INTEGER NOT NULL," + "teamkills INTEGER NOT NULL," + "teamwins INTEGER NOT NULL," + "teamassists INTEGER NOT NULL,"
        + "teamdeaths INTEGER NOT NULL," + "teammelee INTEGER NOT NULL," + "teambow INTEGER NOT NULL," + "teamvoid INTEGER NOT NULL," + "teamplays INTEGER NOT NULL,"
        + "coins INTEGER NOT NULL," + "souls INTEGER NOT NULL," + "level INTEGER NOT NULL," + "exp DOUBLE NOT NULL," + "kits TEXT DEFAULT NULL," + "perks TEXT DEFAULT NULL,"
        + "cages TEXT DEFAULT NULL," + "deathcry TEXT DEFAULT NULL," + "ballons TEXT DEFAULT NULL," + "selected TEXT DEFAULT NULL," + "lastSelected LONG," + "favorites TEXT,"
        + "PRIMARY KEY(id)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_bin;");
  }

  @Override
  public Map<String, StatsContainer> loadStats(UUID id, String table, String name) {
    table = (table.equalsIgnoreCase("ranked_lostedskywars") ? table : "premium_" + table);
    Map<String, StatsContainer> map = new LinkedHashMap<>();
    CachedRowSet rs = this.query("SELECT * FROM `" + table + "` WHERE `id` = ?", id.toString());
    if (rs != null) {
      try {
        for (int collumn = 2; collumn <= rs.getMetaData().getColumnCount(); collumn++) {
          String key = rs.getMetaData().getColumnName(collumn);
          if (key.equals("name")) {
            String oldName = rs.getString(key);
            if (!oldName.equals(name)) {
              this.execute("UPDATE `" + table + "` SET `name` = ? WHERE `id` = ?", name, id.toString());
            }

            continue;
          }

          map.put(key, new StatsContainer(rs.getObject(key)));
        }
      } catch (SQLException ex) {
        LOGGER.log(LostLevel.SEVERE, "Could not loadStats(\"" + name + "\"): ", ex);
      }

      return map;
    }

    if (table.equals("premium_lostedskywars")) {
      map.put("solokills", new StatsContainer(0));
      map.put("solowins", new StatsContainer(0));
      map.put("soloassists", new StatsContainer(0));
      map.put("solodeaths", new StatsContainer(0));
      map.put("solomelee", new StatsContainer(0));
      map.put("solobow", new StatsContainer(0));
      map.put("solovoid", new StatsContainer(0));
      map.put("soloplays", new StatsContainer(0));
      map.put("teamkills", new StatsContainer(0));
      map.put("teamwins", new StatsContainer(0));
      map.put("teamassists", new StatsContainer(0));
      map.put("teamdeaths", new StatsContainer(0));
      map.put("teammelee", new StatsContainer(0));
      map.put("teambow", new StatsContainer(0));
      map.put("teamvoid", new StatsContainer(0));
      map.put("teamplays", new StatsContainer(0));
      map.put("coins", new StatsContainer(0));
      map.put("souls", new StatsContainer(0));
      map.put("level", new StatsContainer(1));
      map.put("exp", new StatsContainer(0.0D));
      map.put("kits", new StatsContainer("{}"));
      map.put("perks", new StatsContainer("{}"));
      map.put("cages", new StatsContainer("{}"));
      map.put("deathcry", new StatsContainer("{}"));
      map.put("ballons", new StatsContainer("{}"));
      map.put("selected", new StatsContainer("0:0:0 : 0"));
      map.put("lastSelected", new StatsContainer(0L));
      map.put("favorites", new StatsContainer("[]"));
    } else if (table.equals("ranked_lostedskywars")) {
      map.put("kills", new StatsContainer(0));
      map.put("wins", new StatsContainer(0));
      map.put("assists", new StatsContainer(0));
      map.put("deaths", new StatsContainer(0));
      map.put("melee", new StatsContainer(0));
      map.put("bow", new StatsContainer(0));
      map.put("void", new StatsContainer(0));
      map.put("plays", new StatsContainer(0));
      map.put("points", new StatsContainer(0));
    } else {
      map.put("lastRank", new StatsContainer("&7"));
      map.put("mysterydusts", new StatsContainer(0));
      map.put("sw_maxsouls", new StatsContainer(100));
      map.put("sw_wellroll", new StatsContainer(1));
      map.put("sw_soulswin", new StatsContainer(0));
      map.put("deliveries", new StatsContainer("{}"));
      map.put("leveling", new StatsContainer("[]"));
      map.put("players", new StatsContainer(true));
      map.put("gore", new StatsContainer(true));
    }
    List<Object> list = new ArrayList<>();
    list.add(id.toString());
    list.add(name);
    list.addAll(map.values().stream().map(sc -> sc.get()).collect(Collectors.toList()));
    this.execute("INSERT INTO `" + table + "` VALUES (?, ?, " + StringUtils.repeat("?, ", map.size() - 1) + "?)", list.toArray(new Object[list.size()]));
    list.clear();
    list = null;
    return map;
  }

  @Override
  public void saveStats(UUID id, String table, Map<String, StatsContainer> map) {
    table = (table.equalsIgnoreCase("ranked_lostedskywars") ? table : "premium_" + table);
    StringBuilder sb = new StringBuilder("UPDATE `" + table + "` SET ");
    List<String> keys = new ArrayList<>(map.keySet());
    for (int slot = 0; slot < keys.size(); slot++) {
      String key = keys.get(slot);
      sb.append("`" + key + "` = ?");
      if (slot + 1 == keys.size()) {
        continue;
      }

      sb.append(", ");
    }

    sb.append(" WHERE `id` = ?");

    List<Object> values = new ArrayList<>();
    values.addAll(map.values().stream().map(sc -> sc.get()).collect(Collectors.toList()));
    values.add(id.toString());
    this.execute(sb.toString(), values.toArray(new Object[values.size()]));

    keys.clear();
    values.clear();
    keys = null;
    values = null;
  }

  private Map<UUID, Account> accounts = new HashMap<>();

  @Override
  public Account loadAccount(UUID id, String name) {
    Account account = accounts.get(id);
    if (account == null) {
      account = new Account(id, name);
      this.accounts.put(id, account);
    }

    return account;
  }

  @Override
  public Account loadOffline(String name) {
    CachedRowSet rs = query("SELECT * FROM `premium_lostedaccount` WHERE LOWER(`name`) = ?", name.toLowerCase());
    if (rs == null) {
      return null;
    }

    try {
      return new Account(UUID.fromString(rs.getString("id")), name);
    } catch (SQLException ex) {
      LOGGER.log(LostLevel.SEVERE, "loadOffline(\"" + name + "\"): ", ex);
      return null;
    }
  }

  @Override
  public Account unloadAccount(UUID id) {
    return accounts.remove(id);
  }

  @Override
  public Account getAccount(UUID id) {
    return accounts.get(id);
  }

  @Override
  public Collection<Account> listAccounts() {
    return ImmutableList.copyOf(accounts.values());
  }

  public void openConnection() {
    try {
      boolean reconnected = true;
      if (this.connection == null) {
        reconnected = false;
      }
      this.connection = DriverManager.getConnection(
          "jdbc:mysql://" + host + ":" + port + "/" + dbname + "?verifyServerCertificate=false&useSSL=false&useUnicode=yes&characterEncoding=UTF-8", username, password);
      if (reconnected) {
        LOGGER.info("Reconnected to MySQL!");
        return;
      }

      LOGGER.info("Connected to MySQL!");
    } catch (SQLException ex) {
      LOGGER.log(LostLevel.SEVERE, "Could not open MySQL connection: ", ex);
    }
  }

  public void closeConnection() {
    if (isConnected()) {
      try {
        connection.close();
      } catch (SQLException e) {
        LOGGER.log(LostLevel.WARNING, "Could not close MySQL connection: ", e);
      }
    }
  }

  public Connection getConnection() {
    if (!isConnected()) {
      this.openConnection();
    }

    return connection;
  }

  public boolean isConnected() {
    try {
      return !(connection == null || connection.isClosed() || !connection.isValid(5));
    } catch (SQLException ex) {
      LOGGER.log(LostLevel.SEVERE, "Could not check MySQL connection: ", ex);
      return false;
    }
  }

  public void update(String sql, Object... vars) {
    try {
      PreparedStatement ps = prepareStatement(sql, vars);
      ps.execute();
      ps.close();
    } catch (SQLException e) {
      LOGGER.log(LostLevel.WARNING, "Could not execute SQL (" + sql + "): ", e);
    }
  }

  public void execute(String sql, Object... vars) {
    executor.execute(() -> {
      update(sql, vars);
    });
  }

  public PreparedStatement prepareStatement(String query, Object... vars) {
    try {
      PreparedStatement ps = getConnection().prepareStatement(query);
      for (int i = 0; i < vars.length; i++) {
        ps.setObject(i + 1, vars[i]);
      }
      return ps;
    } catch (SQLException e) {
      LOGGER.log(LostLevel.WARNING, "Could not Prepare Statement: ", e);
    }

    return null;
  }

  public CachedRowSet query(String query, Object... vars) {
    CachedRowSet rowSet = null;
    try {
      Future<CachedRowSet> future = executor.submit(new Callable<CachedRowSet>() {

        @Override
        public CachedRowSet call() {
          try {
            PreparedStatement ps = prepareStatement(query, vars);

            ResultSet rs = ps.executeQuery();
            CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.populate(rs);
            rs.close();
            ps.close();

            if (crs.next()) {
              return crs;
            }
          } catch (Exception e) {
            LOGGER.log(LostLevel.WARNING, "Could not Execute Query: ", e);
          }

          return null;
        }
      });

      if (future.get() != null) {
        rowSet = future.get();
      }
    } catch (Exception e) {
      LOGGER.log(LostLevel.WARNING, "Could not Call FutureTask: ", e);
    }

    return rowSet;
  }
}
