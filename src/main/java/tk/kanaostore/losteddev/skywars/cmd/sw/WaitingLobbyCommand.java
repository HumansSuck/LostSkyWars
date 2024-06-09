package tk.kanaostore.losteddev.skywars.cmd.sw;

import static tk.kanaostore.losteddev.skywars.listeners.player.PlayerInteractListener.WAITING_LOBBY;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import tk.kanaostore.losteddev.skywars.cmd.SubCommand;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsCube;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.world.WorldServer;

public class WaitingLobbyCommand extends SubCommand {

  public WaitingLobbyCommand() {
    super("waitinglobby");
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

    Object[] arr = new Object[4];
    arr[0] = server;
    WAITING_LOBBY.put(player, arr);

    player.getInventory().clear();
    player.getInventory().setArmorContents(null);

    player.getInventory().setItem(1, BukkitUtils.deserializeItemStack("STICK : 1 : display=&aWaiting Border"));
    player.getInventory().setItem(2, BukkitUtils.deserializeItemStack("BEACON : 1 : display=&aWaiting Location"));

    player.getInventory().setItem(5, BukkitUtils.deserializeItemStack("STAINED_CLAY:5 : 1 : display=&aConfirm"));
    player.getInventory().setItem(6, BukkitUtils.deserializeItemStack("BED : 1 : display=&cCancel"));

    player.updateInventory();

    player.getInventory().setHeldItemSlot(4);
    player.setGameMode(GameMode.CREATIVE);
    player.sendMessage("§5[LostSkyWars] §aUse your hotbar item to set the WaitingLobby.");
  }

  @Override
  public String getUsage() {
    return "waitinglobby";
  }

  @Override
  public String getDescription() {
    return "Set WaitingLobby of a Server.";
  }

  public static void handleClick(Player player, Account account, String display, PlayerInteractEvent evt) {
    WorldServer<?> server = (WorldServer<?>) WAITING_LOBBY.get(player)[0];
    if (server == null) {
      evt.setCancelled(true);
      WAITING_LOBBY.remove(player);
      account.refreshPlayer();
      player.sendMessage("§5[LostSkyWars] §aWaiting Lobby cancelled.");
      return;
    }

    if (display.startsWith("§aWaiting Border")) {
      evt.setCancelled(true);
      if (evt.getAction() == Action.LEFT_CLICK_BLOCK) {
        WAITING_LOBBY.get(player)[2] = BukkitUtils.serializeLocation(evt.getClickedBlock().getLocation());
        player.sendMessage("§5[LostSkyWars] §aWaiting Border 1 setted!");
      } else if (evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
        WAITING_LOBBY.get(player)[3] = BukkitUtils.serializeLocation(evt.getClickedBlock().getLocation());
        player.sendMessage("§5[LostSkyWars] §aWaiting Border 2 setted!");
      } else {
        player.sendMessage("§5[LostSkyWars] §cClick in a block.");
      }
    } else if (display.startsWith("§aWaiting Location")) {
      evt.setCancelled(true);
      Location location = player.getLocation().getBlock().getLocation().clone().add(0.5, 0, 0.5);
      location.setYaw(player.getLocation().getYaw());
      location.setPitch(player.getLocation().getPitch());
      WAITING_LOBBY.get(player)[1] = BukkitUtils.serializeLocation(player.getLocation());
      player.sendMessage("§5[LostBedWars] §aWaiting location setted!");
    } else if (display.startsWith("§aConfirm")) {
      evt.setCancelled(true);
      Object[] arr = WAITING_LOBBY.get(player);
      if (arr[1] == null) {
        player.sendMessage("§5[LostSkyWars] §cSet the Waiting Location using the Beacon.");
        return;
      }

      if (arr[2] == null) {
        player.sendMessage("§5[LostSkyWars] §cSet the Waiting Border 1 using the Stick.");
        return;
      }

      if (arr[3] == null) {
        player.sendMessage("§5[LostSkyWars] §cSet the Waiting Border 2 using the Stick.");
        return;
      }

      server.getConfig().setWaitingLobby(new SkyWarsCube(BukkitUtils.deserializeLocation((String) arr[2]), BukkitUtils.deserializeLocation((String) arr[3])),
          BukkitUtils.deserializeLocation((String) arr[1]));

      WAITING_LOBBY.remove(player);
      account.refreshPlayer();
      player.sendMessage("§5[LostSkyWars] §aWaiting Lobby created.");
    } else if (display.startsWith("§cCancel")) {
      evt.setCancelled(true);
      WAITING_LOBBY.remove(player);
      account.refreshPlayer();
      player.sendMessage("§5[LostSkyWars] §aWaiting Lobby cancelled.");
    }
  }
  
  @Override
  public boolean onlyForPlayer() {
    return true;
  }
}
