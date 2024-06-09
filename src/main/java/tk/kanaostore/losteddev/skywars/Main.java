package tk.kanaostore.losteddev.skywars;

import static tk.kanaostore.losteddev.skywars.bungee.Core.MODE;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import tk.kanaostore.losteddev.skywars.bungee.Core;
import tk.kanaostore.losteddev.skywars.bungee.CoreLobbies;
import tk.kanaostore.losteddev.skywars.bungee.CoreMode;
import tk.kanaostore.losteddev.skywars.cmd.Commands;
import tk.kanaostore.losteddev.skywars.cosmetics.Cosmetic;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.database.SQLiteDatabase;
import tk.kanaostore.losteddev.skywars.holograms.Holograms;
import tk.kanaostore.losteddev.skywars.hook.BoxesHook;
import tk.kanaostore.losteddev.skywars.hook.CitizensHook;
import tk.kanaostore.losteddev.skywars.hook.PlaceholderAPIHook;
import tk.kanaostore.losteddev.skywars.hook.ProtocolLibHook;
import tk.kanaostore.losteddev.skywars.leaderboards.LeaderBoard;
import tk.kanaostore.losteddev.skywars.level.Level;
import tk.kanaostore.losteddev.skywars.listeners.Listeners;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu;
import tk.kanaostore.losteddev.skywars.nms.NMS;
import tk.kanaostore.losteddev.skywars.rank.Rank;
import tk.kanaostore.losteddev.skywars.rank.TagUtils;
import tk.kanaostore.losteddev.skywars.ranked.Ranked;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsChest.ChestType;
import tk.kanaostore.losteddev.skywars.utils.KanaoUpdater;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;
import tk.kanaostore.losteddev.skywars.utils.MinecraftVersion;
import tk.kanaostore.losteddev.skywars.well.AngelOfDeath;
import tk.kanaostore.losteddev.skywars.well.WellNPC;
import tk.kanaostore.losteddev.skywars.world.WorldServer;

public class Main extends JavaPlugin {

  private static Main instance;
  private static boolean validInit;
  public static boolean citizens = true, lostparties = true, lostboxes = true, vault = true, placeholderapi = true, protocollib = true;
  public static Object economy;
  public static final LostLogger LOGGER = new LostLogger();

  public Main() {
    instance = this;
  }

  @Override
  public void onEnable() {
    saveDefaultConfig();
    if (!this.getConfig().contains("servermode")) {
      this.getConfig().set("servermode", CoreMode.MULTI_ARENA.name());
      saveConfig();
    }
    try {
      Core.MODE = CoreMode.valueOf(this.getConfig().get("servermode").toString());
    } catch (Exception ex) {
    }
    if (!NMS.setupNMS()) {
      this.setEnabled(false);
      LOGGER.log(LostLevel.SEVERE, "This server version (" + MinecraftVersion.getCurrentVersion().getVersion() + ") is not compatible with LostSkyWars.");
      LOGGER.log(LostLevel.INFO, "Compatible builds: v1_8_R3, v1_9_R1, v1_10_R1, v1_11_R1, v1_12_R1");
      return;
    }

    // Hotfix for the spawn protection size problem
    if (Bukkit.getServer().getSpawnRadius() != 0) {
      Bukkit.getServer().setSpawnRadius(0);
    }

    Database.setupDatabase();

    if (MODE != CoreMode.MULTI_ARENA && Database.getInstance() instanceof SQLiteDatabase) {
      this.cancelBungee(false);
      return;
    }

    if (MODE != CoreMode.MULTI_ARENA && Database.getInstance().query("SELECT * FROM `lostskywars_files`") == null) {
      this.cancelBungee(true);
      return;
    }

    if (MODE != CoreMode.MULTI_ARENA && !Bukkit.getServer().spigot().getConfig().getBoolean("settings.bungeecord")) {
      this.setEnabled(false);
      LOGGER.log(LostLevel.WARNING, "Enable BungeeCord mode in spigot.yml 'settings.bungeecord'");
      return;
    }

    Rank.setupRanks();
    Level.setupLevels();
    Ranked.setupRanked();
    if (MODE != CoreMode.LOBBY) {
      ChestType.setupTypes();
    }
    ConfigMenu.setupMenus();
    Language.setupLanguage();

    Holograms.setup(Main.getInstance());
    if (MODE != CoreMode.LOBBY) {
      WorldServer.setupServers();
    }

    if (MODE != CoreMode.MULTI_ARENA) {
      CoreLobbies.setupLobbies();
    }

    Cosmetic.setupCosmetics();

    this.setupVault();
    this.setupPlaceholderAPI();
    this.setupBoxes();
    if (MODE == CoreMode.MULTI_ARENA) {
      this.setupParties();
    } else {
      lostparties = false;
    }
    if (MODE != CoreMode.ARENA) {
      this.setupProtocolLib();
      this.setupCitizens();
    } else {
      protocollib = false;
      citizens = false;
    }

    Commands.setupCommands();
    Listeners.setupListeners();

    if (MODE != CoreMode.ARENA) {
      LeaderBoard.setupLeaderBoards();

      WellNPC.setupWellNPCs();
      AngelOfDeath.setupAngels();
    }

    validInit = true;
    this.getServer().getScheduler().runTask(this, () -> {
      new KanaoUpdater(this, 1);
      LOGGER.log(LostLevel.INFO, "Server Mode: " + Core.MODE.name());
    });
    LOGGER.log(LostLevel.INFO, "The plugin has been enabled!");
  }

  @Override
  public void onDisable() {
    if (validInit) {
      TagUtils.reset();
      Holograms.close();

      if (citizens) {
        CitizensHook.destroyCitizens();
      }
      if (protocollib) {
        ProtocolLibHook.destroyProtocolLib();
      }
      if (lostboxes) {
        BoxesHook.destroyBoxes();
      }
      WellNPC.listNPCs().forEach(WellNPC::destroy);
      AngelOfDeath.listNPCs().forEach(AngelOfDeath::destroy);

      Database.getInstance().listAccounts().forEach(account -> {
        Database.getInstance().unloadAccount(account.getUniqueId());
        account.save();
        account.destroy();
      });

      HandlerList.unregisterAll(this);
      Bukkit.getScheduler().cancelTasks(this);
    }

    instance = null;
    LOGGER.log(LostLevel.INFO, "The plugin has been disabled!");
  }

  private void cancelBungee(boolean table) {
    this.setEnabled(false);
    if (!table) {
      LOGGER.log(LostLevel.WARNING, "You can't use SQLite for Bungee Mode. Use MySQL instead of SQLite!");
    } else {
      LOGGER.log(LostLevel.WARNING, "Unable to find Table with BungeeFiles.");
      LOGGER.log(LostLevel.WARNING, "Setup your BungeeCord with LostSkyWars first before using that!");
    }
  }

  private void setupBoxes() {
    try {
      Class.forName("io.github.losteddev.boxes.Main");
      BoxesHook.setupBoxes();
    } catch (ClassNotFoundException ex) {
      lostboxes = false;
      LOGGER.log(LostLevel.WARNING, "LostBoxes not found, disabling Mystery Boxes.");
    }
  }

  private void setupParties() {
    try {
      Class.forName("io.github.losteddev.parties.Main");
      LOGGER.log(LostLevel.INFO, "[PartiesHook] LostParties found, hooking...");
    } catch (ClassNotFoundException ex) {
      lostparties = false;
      LOGGER.log(LostLevel.WARNING, "LostParties not found, disabling Parties.");
    }
  }

  private void setupCitizens() {
    try {
      Class.forName("net.citizensnpcs.api.CitizensAPI");
      CitizensHook.setupCitizens();
    } catch (ClassNotFoundException ex) {
      citizens = false;
      LOGGER.log(LostLevel.WARNING, "Citizens not found, disabling NPCs.");
    }
  }

  private void setupProtocolLib() {
    try {
      Class.forName("com.comphenix.protocol.ProtocolLibrary");
      ProtocolLibHook.setupProtocolLib();
    } catch (ClassNotFoundException ex) {
      protocollib = false;
      LOGGER.log(LostLevel.WARNING, "ProtocolLib not found, disabling StatsNPCs.");
    }
  }

  private void setupPlaceholderAPI() {
    try {
      Class.forName("me.clip.placeholderapi.PlaceholderAPI");
      PlaceholderAPIHook.setupPlaceHolderAPI();
    } catch (ClassNotFoundException ex) {
      placeholderapi = false;
      LOGGER.log(LostLevel.WARNING, "PlaceHolderAPI not found, disabling support.");
    }
  }

  private void setupVault() {
    try {
      Class.forName("net.milkbowl.vault.economy.Economy");
      LOGGER.log(LostLevel.INFO, "[VaultHook] Vault found, hooking...");
      try {
        economy = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
      } catch (Exception ex) {
        vault = false;
        LOGGER.log(LostLevel.WARNING, "[VaultHook] EconomyProvider not found, disabling custom Economy.");
      }
    } catch (Exception ex) {
      vault = false;
      LOGGER.log(LostLevel.WARNING, "Vault not found, disabling custom Economy.");
    }
  }

  public static Main getInstance() {
    return instance;
  }
}
