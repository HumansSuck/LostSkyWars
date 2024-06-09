package tk.kanaostore.losteddev.skywars.menu;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.menu.api.UpdatablePlayerMenu;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.world.WorldServer;

@SuppressWarnings("deprecation")
public class TeleporterMenu extends UpdatablePlayerMenu {

  private static final ConfigMenu config = ConfigMenu.getByName("teleporter");

  @EventHandler
  public void onInventoryClick(InventoryClickEvent evt) {
    if (evt.getInventory().equals(getInventory())) {
      evt.setCancelled(true);

      if (evt.getWhoClicked() instanceof Player && evt.getWhoClicked().equals(player)) {
        ItemStack item = evt.getCurrentItem();
        Account account = Database.getInstance().getAccount(player.getUniqueId());
        if (account == null) {
          player.closeInventory();
          return;
        }

        if (evt.getClickedInventory() != null && evt.getClickedInventory().equals(evt.getInventory()) && item != null && item.getType() != Material.AIR) {
          Player target = map.get(evt.getSlot());
          if (target != null && target.getWorld().equals(player.getWorld())) {
            player.teleport(target);
            player.closeInventory();
          }
        }
      }
    }
  }

  private WorldServer<?> server;
  private Map<Integer, Player> map = new HashMap<>();

  public TeleporterMenu(Player player, WorldServer<?> server) {
    super(player, config.getTitle(), Math.min(server.getAlive() / 9, 1));
    this.server = server;

    this.update();
    this.open();
    this.register(20);
  }

  @Override
  public void update() {
    int slot = 0;
    for (Player player : server.getPlayers(false)) {
      int hp = (int) ((player.getHealth() * 100) / player.getMaxHealth());
      int food = (int) ((player.getFoodLevel() * 100) / 20);

      this.setItem(slot, BukkitUtils.putProfileOnSkull(player, BukkitUtils.deserializeItemStack(
          config.getAsString("head").replace("{displayName}", player.getDisplayName()).replace("{hp}", String.valueOf(hp)).replace("{food}", String.valueOf(food)))));
      map.put(slot++, player);
    }
  }

  public void cancel() {
    super.cancel();
    map.clear();
    map = null;
    server = null;
    HandlerList.unregisterAll(this);
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent evt) {
    if (evt.getPlayer().equals(player)) {
      this.cancel();
    }
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent evt) {
    if (evt.getPlayer().equals(player) && evt.getInventory().equals(this.getInventory())) {
      this.cancel();
    }
  }
}
