package tk.kanaostore.losteddev.skywars.cmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.bungee.Core;
import tk.kanaostore.losteddev.skywars.bungee.CoreMode;
import tk.kanaostore.losteddev.skywars.cmd.sw.AODCommand;
import tk.kanaostore.losteddev.skywars.cmd.sw.BalloonsCommand;
import tk.kanaostore.losteddev.skywars.cmd.sw.BuildCommand;
import tk.kanaostore.losteddev.skywars.cmd.sw.ChestCommand;
import tk.kanaostore.losteddev.skywars.cmd.sw.CloneCommand;
import tk.kanaostore.losteddev.skywars.cmd.sw.CreateCageCommand;
import tk.kanaostore.losteddev.skywars.cmd.sw.CreateCommand;
import tk.kanaostore.losteddev.skywars.cmd.sw.DeathCryCommand;
import tk.kanaostore.losteddev.skywars.cmd.sw.DeleteCommand;
import tk.kanaostore.losteddev.skywars.cmd.sw.ForceStartCommand;
import tk.kanaostore.losteddev.skywars.cmd.sw.GiveCommand;
import tk.kanaostore.losteddev.skywars.cmd.sw.LeaderBoardCommand;
import tk.kanaostore.losteddev.skywars.cmd.sw.LoadCommand;
import tk.kanaostore.losteddev.skywars.cmd.sw.RemoveCommand;
import tk.kanaostore.losteddev.skywars.cmd.sw.SetLobbyCommand;
import tk.kanaostore.losteddev.skywars.cmd.sw.TeleportCommand;
import tk.kanaostore.losteddev.skywars.cmd.sw.UnloadCommand;
import tk.kanaostore.losteddev.skywars.cmd.sw.WaitingLobbyCommand;
import tk.kanaostore.losteddev.skywars.cmd.sw.WellNPCCommand;
import tk.kanaostore.losteddev.skywars.hook.boxes.cmd.BoxNPCCommand;
import tk.kanaostore.losteddev.skywars.hook.citizens.cmd.DeliveryNPCCommand;
import tk.kanaostore.losteddev.skywars.hook.citizens.cmd.PlayNPCCommand;
import tk.kanaostore.losteddev.skywars.hook.citizens.cmd.ShopkeeperNPCCommand;
import tk.kanaostore.losteddev.skywars.hook.citizens.cmd.StatsNPCCommand;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;

public class SkyWarsCommand extends Command {

  private List<SubCommand> commands = new ArrayList<>();

  public SkyWarsCommand() {
    super("lsw");
    this.setAliases(Arrays.asList("lostsw", "lostskywars"));

    try {
      SimpleCommandMap simpleCommandMap = (SimpleCommandMap) Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap").invoke(Bukkit.getServer());
      simpleCommandMap.register(this.getName(), "lostskywars", this);
    } catch (ReflectiveOperationException ex) {
      Main.LOGGER.log(LostLevel.SEVERE, "Cannot register command: ", ex);
    }

    if (Core.MODE != CoreMode.ARENA) {
      commands.add(new SetLobbyCommand());
      commands.add(new BuildCommand());
      if (Main.citizens) {
        commands.add(new PlayNPCCommand());
        commands.add(new DeliveryNPCCommand());
        commands.add(new ShopkeeperNPCCommand());
        if (Main.protocollib) {
          commands.add(new StatsNPCCommand());
        }
      }
      if (Main.lostboxes) {
        commands.add(new BoxNPCCommand());
      }
      commands.add(new WellNPCCommand());
      commands.add(new AODCommand());
    }
    if (Core.MODE == CoreMode.MULTI_ARENA) {
      commands.add(new CreateCageCommand());
      commands.add(new DeathCryCommand());
    }
    if (Core.MODE != CoreMode.ARENA) {
      commands.add(new LeaderBoardCommand());
    }
    commands.add(new GiveCommand());
    commands.add(new RemoveCommand());
    if (Core.MODE != CoreMode.LOBBY) {
      commands.add(new ForceStartCommand());
      commands.add(new LoadCommand());
      commands.add(new UnloadCommand());
      commands.add(new TeleportCommand());
      commands.add(new CreateCommand());
      commands.add(new ChestCommand());
      commands.add(new BalloonsCommand());
      commands.add(new WaitingLobbyCommand());
    }
    if (Core.MODE == CoreMode.MULTI_ARENA) {
      commands.add(new CloneCommand());
      commands.add(new DeleteCommand());
    }
  }

  @Override
  public boolean execute(CommandSender sender, String label, String[] args) {
    if (!sender.hasPermission("lostskywars.cmd.skywars")) {
      sender.sendMessage("§5[LostSkyWars v" + Main.getInstance().getDescription().getVersion() + "] §acreated by §bLostedDev§a.");
      return true;
    }

    if (args.length == 0) {
      sendHelp(sender, 1);
      return true;
    }

    try {
      sendHelp(sender, Integer.parseInt(args[0]));
    } catch (NumberFormatException ex) {
      SubCommand subCommand = commands.stream().filter(sc -> sc.getName().equalsIgnoreCase(args[0])).findFirst().orElse(null);
      if (subCommand == null) {
        sendHelp(sender, 1);
        return true;
      }

      List<String> list = new ArrayList<>();
      list.addAll(Arrays.asList(args));
      list.remove(0);
      if (subCommand.onlyForPlayer()) {
        if (!(sender instanceof Player)) {
          sender.sendMessage("§5[LostSkyWars] §cThis command can be used only by players.");
          return true;
        }

        subCommand.perform((Player) sender, list.toArray(new String[list.size()]));
        return true;
      }

      subCommand.perform(sender, list.toArray(new String[list.size()]));
    }

    return true;
  }

  private void sendHelp(CommandSender sender, int page) {
    List<SubCommand> commands = this.commands.stream().filter(subcommand -> sender instanceof Player || !subcommand.onlyForPlayer()).collect(Collectors.toList());
    Map<Integer, StringBuilder> pages = new HashMap<>();

    int pagesCount = (commands.size() + 5) / 6;
    for (int index = 0; index < commands.size(); index++) {
      int currentPage = (index + 6) / 6;
      if (!pages.containsKey(currentPage)) {
        pages.put(currentPage, new StringBuilder(" \n§dHelp - " + currentPage + "/" + pagesCount + "\n \n"));
      }

      pages.get(currentPage).append("§6/lsw " + commands.get(index).getUsage() + " §f- §7" + commands.get(index).getDescription() + "\n");
    }

    StringBuilder sb = pages.get(page);
    if (sb == null) {
      sender.sendMessage("§5[LostSkyWars] §cPage not found.");
      return;
    }

    sb.append(" ");
    sender.sendMessage(sb.toString());
  }
}
