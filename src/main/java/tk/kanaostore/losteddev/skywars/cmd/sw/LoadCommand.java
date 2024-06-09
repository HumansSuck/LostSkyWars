package tk.kanaostore.losteddev.skywars.cmd.sw;

import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.cmd.SubCommand;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;

@SuppressWarnings("deprecation")
public class LoadCommand extends SubCommand {

  public static final LostLogger LOGGER = Main.LOGGER.getModule("LoadCommand");

  public LoadCommand() {
    super("load");
  }

  @Override
  public void perform(CommandSender sender, String[] args) {
    if (args.length == 0) {
      sender.sendMessage("§cUse /lsw load <world-name>");
      return;
    }

    if (Bukkit.getWorld(args[0]) != null) {
      sender.sendMessage("§5[LostSkyWars] §cWorld already loaded.");
      return;
    }

    File map = new File(args[0]);
    if (!map.exists() || !map.isDirectory()) {
      sender.sendMessage("§5[LostSkyWars] §cWorld folder not found.");
      return;
    }

    try {
      sender.sendMessage("§5[LostSkyWars] §aLoading...");
      Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
        WorldCreator wc = WorldCreator.name(map.getName());
        World world = wc.createWorld();
        world.setTime(0l);
        world.setStorm(false);
        world.setThundering(false);
        world.setAutoSave(false);
        world.setAnimalSpawnLimit(0);
        world.setWaterAnimalSpawnLimit(0);
        world.setKeepSpawnInMemory(false);
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("mobGriefing", "false");
        sender.sendMessage("§5[LostSkyWars] §aWorld loaded successfully!");
      });
    } catch (Exception ex) {
      sender.sendMessage("§5[LostSkyWars] §cError loading \"" + map.getName() + "\" (see the console)");
      LOGGER.log(LostLevel.WARNING, "Cannot load world \"" + map.getName() + "\": ", ex);
    }
  }
  
  @Override
  public void perform(Player player, String[] args) {}

  @Override
  public String getUsage() {
    return "load <world-name>";
  }

  @Override
  public String getDescription() {
    return "Load a world of server folder.";
  }
  
  @Override
  public boolean onlyForPlayer() {
    return false;
  }
}
