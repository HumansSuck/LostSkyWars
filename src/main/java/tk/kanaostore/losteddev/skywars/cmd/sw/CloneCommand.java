package tk.kanaostore.losteddev.skywars.cmd.sw;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.cmd.SubCommand;
import tk.kanaostore.losteddev.skywars.ui.server.ScanCallback;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;
import tk.kanaostore.losteddev.skywars.utils.FileUtils;
import tk.kanaostore.losteddev.skywars.world.WorldServer;

public class CloneCommand extends SubCommand {

  public CloneCommand() {
    super("clone");
  }

  @Override
  public void perform(CommandSender sender, String[] args) {
    if (args.length < 2) {
      sender.sendMessage("§cUse /lsw clone <world> <newName>");
      return;
    }

    String worldName = args[1];
    WorldServer<?> server = WorldServer.getByWorldName(args[0]);
    if (server == null) {
      sender.sendMessage("§5[LostSkyWars] §cThis world does not have an arena");
      return;
    }

    if (WorldServer.getByWorldName(worldName) != null) {
      sender.sendMessage("§5[LostSkyWars] §cThis world already is a arena");
      return;
    }

    sender.sendMessage("§5[LostSkyWars] §aCloning the arena...");
    ConfigUtils cu = ConfigUtils.getConfig(worldName, "plugins/LostSkyWars/servers");
    cu.set("name", server.getName());
    cu.set("mode", server.getMode().name().toLowerCase());
    cu.set("type", server.getType().name().toLowerCase());
    cu.set("cube", server.getConfig().getWorldCube().toString().replace(args[0], worldName));
    cu.set("min-players", server.getConfig().getMinPlayers());
    if (server.getConfig().getConfig().contains("waiting-cube")) {
      cu.set("waiting-cube", server.getConfig().getWaitingCube().toString().replace(args[0], worldName));
      cu.set("waiting-lobby", BukkitUtils.serializeLocation(server.getConfig().getWaitingLocation()).replace(args[0], worldName));
    }
    List<String> spawns = new ArrayList<>();
    for (String spawn : server.getConfig().listSpawns()) {
      spawns.add(spawn.replace(args[0], worldName));
    }
    cu.set("spawns", spawns);
    List<String> chests = new ArrayList<>();
    for (String chest : server.getConfig().listChests()) {
      chests.add(chest.replace(args[0], worldName));
    }
    cu.set("chests", chests);
    List<String> balloons = new ArrayList<>();
    for (String balloon : server.getConfig().listBalloons()) {
      balloons.add(balloon.replace(args[0], worldName));
    }
    cu.set("balloons", balloons);

    FileUtils.copyFiles(new File("plugins/LostSkyWars/maps/" + args[0]), new File("plugins/LostSkyWars/maps/" + worldName));

    WorldServer.loadArena(cu.getFile(), new ScanCallback() {

      @Override
      public void finish() {
        sender.sendMessage("§5[LostSkyWars] §aArena cloned.");
      }
    });
  }
  
  @Override
  public void perform(Player player, String[] args) {}

  @Override
  public String getUsage() {
    return "clone <world> <newName>";
  }

  @Override
  public String getDescription() {
    return "Clone an Server.";
  }
  
  @Override
  public boolean onlyForPlayer() {
    return false;
  }
}
