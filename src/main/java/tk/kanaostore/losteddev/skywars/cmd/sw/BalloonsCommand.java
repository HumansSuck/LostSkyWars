package tk.kanaostore.losteddev.skywars.cmd.sw;

import static tk.kanaostore.losteddev.skywars.listeners.player.PlayerInteractListener.BALLOONS;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import tk.kanaostore.losteddev.skywars.cmd.SubCommand;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.world.WorldServer;

public class BalloonsCommand extends SubCommand {

  public BalloonsCommand() {
    super("balloons");
  }
  
  @Override
  public void perform(CommandSender sender, String[] args) {}

  @Override
  public void perform(Player player, String[] args) {
    WorldServer<?> server = WorldServer.getByWorldName(player.getWorld().getName());
    if (server == null) {
      player.sendMessage("§5[LostSkyWars] §cThis world does not have an arena");
      return;
    }
    
    int nextId;
    for (nextId = 0; nextId <= server.getTeams().size(); nextId++) {
      if (server.getConfig().getBalloon(nextId) == null) {
        break;
      }
    }
    
    if (nextId == server.getTeams().size()) {
      player.sendMessage("§5[LostSkyWars] §cThis arena already haves all Balloons setted!");
      return;
    }

    Object[] arr = new Object[2];
    arr[0] = nextId;
    BALLOONS.put(player, arr);

    player.getInventory().clear();
    player.getInventory().setArmorContents(null);

    player.getInventory().setItem(2,
        BukkitUtils.deserializeItemStack("FENCE : 1 : display=&aBalloon Fence"));
    player.getInventory().setItem(3,
        BukkitUtils.deserializeItemStack("STAINED_CLAY:5 : 1 : display=&aConfirm"));

    player.getInventory().setItem(5, BukkitUtils.deserializeItemStack("BED : 1 : display=&cCancel"));

    player.updateInventory();

    player.getInventory().setHeldItemSlot(4);
    player.setGameMode(GameMode.CREATIVE);
    player.teleport(server.getTeams().get(nextId).getLocation());
    player.sendMessage("§5[LostSkyWars] §aUse your hotbar item to set the Balloons.");
  }

  @Override
  public String getUsage() {
    return "balloons";
  }

  @Override
  public String getDescription() {
    return "Set Balloons Locations of a Game.";
  }
  
  @Override
  public boolean onlyForPlayer() {
    return true;
  }

  public static void handleClick(Player player, Account account, String display, PlayerInteractEvent evt) {
    Object[] arr = BALLOONS.get(player);
    WorldServer<?> server = WorldServer.getByWorldName(player.getWorld().getName());
    if (server == null) {
      return;
    }
    
    if (display.startsWith("§aBalloon Fence")) {
      evt.setCancelled(true);

      if (evt.getAction().name().contains("BLOCK") && evt.getClickedBlock().getType().name().contains("FENCE")) {
        arr[1] = BukkitUtils.serializeLocation(evt.getClickedBlock().getLocation());
        player.sendMessage("§5[LostSkyWars] §aBalloon location setted!");
      } else {
        player.sendMessage("§5[LostSkyWars] §cClick in a fence.");
      }
    } else if (display.startsWith("§aConfirm")) {
      evt.setCancelled(true);
      if (arr[1] == null) {
        player.sendMessage("§5[LostSkyWars] §cSet the Balloon Location using the Fence.");
        return;
      }
      
      server.getConfig().addBalloon((String) arr[1]);
      arr[1] = null;
      
      int nextId;
      for (nextId = 0; nextId <= server.getTeams().size(); nextId++) {
        if (server.getConfig().getBalloon(nextId) == null) {
          break;
        }
      }
      
      if (nextId == server.getTeams().size()) {
        BALLOONS.remove(player);
        account.refreshPlayer();
        player.sendMessage("§5[LostSkyWars] §aAll Balloons locations setted.");
        return;
      }
      
      player.teleport(server.getTeams().get(nextId).getLocation());
      player.sendMessage("§5[LostSkyWars] §aSet the Balloon of that Island now.");
    } else if (display.startsWith("§cCancel")) {
      evt.setCancelled(true);
      BALLOONS.remove(player);
      account.refreshPlayer();
      player.sendMessage("§5[LostSkyWars] §aBalloons mode cancelled.");
    }
  }
}
