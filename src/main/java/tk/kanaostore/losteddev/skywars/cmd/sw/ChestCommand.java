package tk.kanaostore.losteddev.skywars.cmd.sw;

import static tk.kanaostore.losteddev.skywars.listeners.player.PlayerInteractListener.CHEST;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import tk.kanaostore.losteddev.skywars.cmd.SubCommand;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsChest;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsChest.ChestType;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;
import tk.kanaostore.losteddev.skywars.world.WorldServer;

public class ChestCommand extends SubCommand {

  public ChestCommand() {
    super("chest");
  }

  @Override
  public void perform(CommandSender sender, String[] args) {}
  
  @Override
  public void perform(Player player, String[] args) {
    if (args.length == 0) {
      player.sendMessage("§cUse /lsw chest <type-name>");
      return;
    }

    if (WorldServer.getByWorldName(player.getWorld().getName()) == null) {
      player.sendMessage("§5[LostSkyWars] §cThis world does not have an arena");
      return;
    }

    ChestType type = ChestType.getByName(StringUtils.join(args, " "));
    if (type == null) {
      player.sendMessage("§5[LostSkyWars] §cInvalid ChestType!");
      return;
    }

    CHEST.put(player, type);

    player.getInventory().clear();
    player.getInventory().setArmorContents(null);

    player.getInventory().setItem(3,
        BukkitUtils.deserializeItemStack("BLAZE_ROD : 1 : display=&aMagic Wand : lore=&fLeft click:\n &7Change the current type.\n&fRight click:\n &7Shows the current type."));

    player.getInventory().setItem(5, BukkitUtils.deserializeItemStack("BED : 1 : display=&cCancel"));

    player.updateInventory();

    player.getInventory().setHeldItemSlot(4);
    player.setGameMode(GameMode.CREATIVE);
    player.sendMessage("§5[LostSkyWars] §aUse your hotbar item to change the chest type.");
  }

  @Override
  public String getUsage() {
    return "chest <type-name>";
  }

  @Override
  public String getDescription() {
    return "Change ChestType of a GameChest.";
  }
  
  @Override
  public boolean onlyForPlayer() {
    return true;
  }

  public static void handleClick(Player player, Account account, String display, PlayerInteractEvent evt) {
    if (display.startsWith("§aMagic Wand")) {
      evt.setCancelled(true);
      ChestType type = CHEST.get(player);
      WorldServer<?> server = WorldServer.getByWorldName(player.getWorld().getName());
      if (server == null) {
        return;
      }

      if (evt.getAction() == Action.LEFT_CLICK_BLOCK && evt.getClickedBlock().getType() == Material.CHEST) {
        SkyWarsChest chest = server.getChest(evt.getClickedBlock());
        if (chest == null) {
          player.sendMessage("§5[LostSkyWars] §cInvalid chest!");
          return;
        }
        
        server.changeChest(chest, type);
        player.sendMessage("§5[LostSkyWars] §aChestType changed successfully!");
      } else if (evt.getAction() == Action.RIGHT_CLICK_BLOCK && evt.getClickedBlock().getType() == Material.CHEST) {
        SkyWarsChest chest = server.getChest(evt.getClickedBlock());
        if (chest == null) {
          player.sendMessage("§5[LostSkyWars] §cInvalid chest!");
          return;
        }
        
        player.sendMessage("§5[LostSkyWars] §7Current ChestType: §f" + chest.getChestType());
      } else {
        player.sendMessage("§5[LostSkyWars] §cClick in a chest.");
      }
    } else if (display.startsWith("§cCancel")) {
      evt.setCancelled(true);
      CHEST.remove(player);
      account.refreshPlayer();
      player.sendMessage("§5[LostSkyWars] §aChest mode cancelled.");
    }
  }
}
