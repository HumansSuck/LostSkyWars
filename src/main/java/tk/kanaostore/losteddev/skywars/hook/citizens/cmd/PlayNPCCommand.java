package tk.kanaostore.losteddev.skywars.hook.citizens.cmd;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.bungee.Core;
import tk.kanaostore.losteddev.skywars.bungee.CoreMode;
import tk.kanaostore.losteddev.skywars.cmd.SubCommand;
import tk.kanaostore.losteddev.skywars.hook.citizens.DuelsNPC;
import tk.kanaostore.losteddev.skywars.hook.citizens.PlayNPC;
import tk.kanaostore.losteddev.skywars.hook.citizens.RankedNPC;
import tk.kanaostore.losteddev.skywars.mojang.InvalidMojangException;
import tk.kanaostore.losteddev.skywars.mojang.Mojang;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsMode;

public class PlayNPCCommand extends SubCommand {

  public PlayNPCCommand() {
    super("playnpc");
  }

  @Override
  public void perform(CommandSender sender, String[] args) {}

  @Override
  public void perform(Player player, String[] args) {
    if (args.length == 0) {
      if (Core.MODE == CoreMode.MULTI_ARENA) {
        player.sendMessage(
            " \n§dPlayNPC - Help\n \n§6/lsw playnpc add <id> <solo/doubles/ranked/duels> §f- §7Spawn an PlayNPC.\n§6/lsw playnpc remove <id> <unranked/ranked/duels> §f- §7Remove an PlayNPC.\n§6/lsw playnpc setskin <solo/doubles/ranked/duels> <player> §f- §7Set PlayNPC mode skin.\n ");
      } else {
        player.sendMessage(
            " \n§dPlayNPC - Help\n \n§6/lsw playnpc add <id> <solo/doubles/ranked/duels> §f- §7Spawn an PlayNPC.\n§6/lsw playnpc remove <id> <unranked/ranked/duels> §f- §7Remove an PlayNPC.\n ");
      }
      return;
    }

    String action = args[0];
    if (action.equalsIgnoreCase("add")) {
      if (args.length < 3) {
        player.sendMessage("§cUse /lsw playnpc add <id> <solo/doubles/ranked/duels>");
        return;
      }

      if (args[2].equalsIgnoreCase("ranked") || args[2].equalsIgnoreCase("duels")) {
        if (args[2].equalsIgnoreCase("duels")) {
          DuelsNPC npc = DuelsNPC.getById(args[1]);
          if (npc != null) {
            player.sendMessage("§5[LostSkyWars] §cAlready exists an DuelsNPC with id \"" + args[1] + "\"!");
            return;
          }

          Location location = player.getLocation().getBlock().getLocation().clone().add(0.5, 0, 0.5);
          location.setYaw(player.getLocation().getYaw());
          location.setPitch(player.getLocation().getPitch());
          DuelsNPC.add(args[1], location);
          player.sendMessage("§5[LostSkyWars] §aDuelsNPC added successfully!");
          return;
        }

        RankedNPC npc = RankedNPC.getById(args[1]);
        if (npc != null) {
          player.sendMessage("§5[LostSkyWars] §cAlready exists an RankedNPC with id \"" + args[1] + "\"!");
          return;
        }

        Location location = player.getLocation().getBlock().getLocation().clone().add(0.5, 0, 0.5);
        location.setYaw(player.getLocation().getYaw());
        location.setPitch(player.getLocation().getPitch());
        RankedNPC.add(args[1], location);
        player.sendMessage("§5[LostSkyWars] §aRankedNPC added successfully!");
        return;
      }

      PlayNPC npc = PlayNPC.getById(args[1]);
      if (npc != null) {
        player.sendMessage("§5[LostSkyWars] §cAlready exists an PlayNPC with id \"" + args[1] + "\"!");
        return;
      }

      SkyWarsMode mode = SkyWarsMode.fromName(args[2]);
      if (mode == null) {
        player.sendMessage("§cUse /lsw playnpc add <id> <solo/doubles/ranked/duels>");
        return;
      }

      Location location = player.getLocation().getBlock().getLocation().clone().add(0.5, 0, 0.5);
      location.setYaw(player.getLocation().getYaw());
      location.setPitch(player.getLocation().getPitch());
      PlayNPC.add(args[1], location, mode);
      player.sendMessage("§5[LostSkyWars] §aPlayNPC added successfully!");
    } else if (action.equalsIgnoreCase("remove")) {
      if (args.length < 3) {
        player.sendMessage("§cUse /lsw playnpc remove <id> <unranked/ranked/duels>");
        return;
      }

      if (args[2].equalsIgnoreCase("ranked") || args[2].equalsIgnoreCase("duels")) {
        if (args[2].equalsIgnoreCase("duels")) {
          DuelsNPC npc = DuelsNPC.getById(args[1]);
          if (npc == null) {
            player.sendMessage("§5[LostSkyWars] §cCannot found an DuelsNPC with id \"" + args[1] + "\"!");
            return;
          }

          DuelsNPC.remove(npc);
          player.sendMessage("§5[LostSkyWars] §aDuelsNPC removed successfully!");
          return;
        }

        RankedNPC npc = RankedNPC.getById(args[1]);
        if (npc == null) {
          player.sendMessage("§5[LostSkyWars] §cCannot found an RankedNPC with id \"" + args[1] + "\"!");
          return;
        }

        RankedNPC.remove(npc);
        player.sendMessage("§5[LostSkyWars] §aRankedNPC removed successfully!");
        return;
      }

      PlayNPC npc = PlayNPC.getById(args[1]);
      if (npc == null) {
        player.sendMessage("§5[LostSkyWars] §cCannot found an PlayNPC with id \"" + args[1] + "\"!");
        return;
      }

      PlayNPC.remove(npc);
      player.sendMessage("§5[LostSkyWars] §aPlayNPC removed successfully!");
    } else if (action.equalsIgnoreCase("setskin") && Core.MODE == CoreMode.MULTI_ARENA) {
      if (args.length < 3) {
        player.sendMessage("§cUse /lsw playnpc setskin <solo/doubles/ranked> <player>");
        return;
      }

      if (args[1].equalsIgnoreCase("ranked") || args[1].equalsIgnoreCase("duels")) {
        String skin = args[2];
        player.sendMessage("§5[LostSkyWars] §aFetching " + skin + "'s skin.");
        try {
          String id = Mojang.getUUID(skin);
          if (id == null) {
            player.sendMessage("§5[LostSkyWars] §cCannot fetch " + skin + "'s skin, make sure " + skin + " is a premium username.");
            return;
          }

          String prop = Mojang.getSkinProperty(id);
          if (prop == null) {
            player.sendMessage("§5[LostSkyWars] §cCannot fetch " + skin + "'s skin, make sure " + skin + " is a premium username.");
            return;
          }

          if (args[1].equalsIgnoreCase("ranked")) {
            Language.lobby$npcs$ranked$skin_value = prop.split(" : ")[1];
            Language.lobby$npcs$ranked$skin_signature = prop.split(" : ")[2];
          } else {
            Language.lobby$npcs$duels$skin_value = prop.split(" : ")[1];
            Language.lobby$npcs$duels$skin_signature = prop.split(" : ")[2];
          }

          Language.reload();
          if (args[1].equalsIgnoreCase("ranked")) {
            RankedNPC.listNPCs().forEach(RankedNPC::spawn);
          } else {
            RankedNPC.listNPCs().forEach(RankedNPC::spawn);
          }
          player.sendMessage("§5[LostSkyWars] §aSkin fetched and applied successfully!");
        } catch (InvalidMojangException ex) {
          player.sendMessage("§5[LostSkyWars] §cCannot fetch " + skin + "'s skin, make sure " + skin + " is a premium username.");
        }
      } else {
        SkyWarsMode mode = SkyWarsMode.fromName(args[1]);
        if (mode == null) {
          player.sendMessage("§cUse /lsw playnpc setskin <solo/doubles/ranked> <player>");
          return;
        }

        String skin = args[2];
        player.sendMessage("§5[LostSkyWars] §aFetching " + skin + "'s skin.");
        try {
          String id = Mojang.getUUID(skin);
          if (id == null) {
            player.sendMessage("§5[LostSkyWars] §cCannot fetch " + skin + "'s skin, make sure " + skin + " is a premium username.");
            return;
          }

          String prop = Mojang.getSkinProperty(id);
          if (prop == null) {
            player.sendMessage("§5[LostSkyWars] §cCannot fetch " + skin + "'s skin, make sure " + skin + " is a premium username.");
            return;
          }

          if (mode.equals(SkyWarsMode.SOLO)) {
            Language.lobby$npcs$play$solo$skin_value = prop.split(" : ")[1];
            Language.lobby$npcs$play$solo$skin_signature = prop.split(" : ")[2];
          } else if (mode.equals(SkyWarsMode.DOUBLES)) {
            Language.lobby$npcs$play$team$skin_value = prop.split(" : ")[1];
            Language.lobby$npcs$play$team$skin_signature = prop.split(" : ")[2];
          }

          Language.reload();
          PlayNPC.listNPCs().stream().filter(npc -> npc.getMode().equals(mode)).forEach(PlayNPC::spawn);
          player.sendMessage("§5[LostSkyWars] §aSkin fetched and applied successfully!");
        } catch (InvalidMojangException ex) {
          player.sendMessage("§5[LostSkyWars] §cCannot fetch " + skin + "'s skin, make sure " + skin + " is a premium username.");
        }
      }
    } else {
      if (Core.MODE == CoreMode.MULTI_ARENA) {
        player.sendMessage(
            " \n§dPlayNPC - Help\n \n§6/lsw playnpc add <id> <solo/doubles/ranked/duels> §f- §7Spawn an PlayNPC.\n§6/lsw playnpc remove <id> <unranked/ranked/duels> §f- §7Remove an PlayNPC.\n§6/lsw playnpc setskin <solo/doubles/ranked/duels> <player> §f- §7Set PlayNPC mode skin.\n ");
      } else {
        player.sendMessage(
            " \n§dPlayNPC - Help\n \n§6/lsw playnpc add <id> <solo/doubles/ranked/duels> §f- §7Spawn an PlayNPC.\n§6/lsw playnpc remove <id> <unranked/ranked/duels> §f- §7Remove an PlayNPC.\n ");
      }
    }
  }

  @Override
  public String getUsage() {
    return "playnpc";
  }

  @Override
  public String getDescription() {
    return "Manage play NPCs.";
  }

  @Override
  public boolean onlyForPlayer() {
    return true;
  }
}
