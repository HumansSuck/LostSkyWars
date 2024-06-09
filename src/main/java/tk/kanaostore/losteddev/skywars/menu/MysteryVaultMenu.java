package tk.kanaostore.losteddev.skywars.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import io.github.losteddev.boxes.api.box.Box;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.hook.boxes.BoxNPC;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigAction;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigItem;
import tk.kanaostore.losteddev.skywars.menu.api.UpdatablePlayerPagedMenu;
import tk.kanaostore.losteddev.skywars.menu.vault.ConfirmOpen;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;

@SuppressWarnings("unchecked")
public class MysteryVaultMenu extends UpdatablePlayerPagedMenu {

  private static final ConfigMenu config = ConfigMenu.getByName("mysteryvault");

  @EventHandler
  public void onInventoryClick(InventoryClickEvent evt) {
    if (evt.getInventory().equals(getCurrentInventory())) {
      evt.setCancelled(true);


      if (evt.getWhoClicked() instanceof Player && evt.getWhoClicked().equals(player)) {
        ItemStack item = evt.getCurrentItem();
        Account account = Database.getInstance().getAccount(player.getUniqueId());
        if (account == null) {
          player.closeInventory();
          return;
        }

        if (evt.getClickedInventory() != null && evt.getClickedInventory().equals(evt.getInventory()) && item != null && item.getType() != Material.AIR) {
          Box box = boxes.get(item);
          if (evt.getSlot() == this.previousPage) {
            this.openPrevious();
          } else if (evt.getSlot() == this.nextPage) {
            this.openNext();
          } else if (box != null) {
            new ConfirmOpen(player, box, npc);
          } else {
            ConfigAction action = actions.get(item);
            if (action != null && !action.getType().equals("NOTHING")) {
              if (action.getType().equals("OPEN")) {
                String menu = action.getValue();
                if (menu.equalsIgnoreCase("closeinv")) {
                  player.closeInventory();
                }
              } else {
                player.closeInventory();
                action.send(player);
              }
            }
          }
        }
      }
    }
  }

  private BoxNPC npc;
  private Map<ItemStack, Box> boxes;
  private Map<ItemStack, ConfigAction> actions;

  public MysteryVaultMenu(Player player, BoxNPC npc) {
    super(player, config.getTitle(), config.getRows());
    this.npc = npc;
    this.boxes = new HashMap<>();
    this.actions = new HashMap<>();
    this.previousPage = config.getAsInt("previous-slot");
    this.nextPage = config.getAsInt("next-slot");
    this.previousStack = config.getAsString("previous-page");
    this.nextStack = config.getAsString("next-page");
    this.onlySlots(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42,
        43);

    this.update();
    this.open();
    this.register(20);
  }

  @Override
  public void update() {
    Account account = Database.getInstance().getAccount(player.getUniqueId());
    if (account == null) {
      player.closeInventory();
      return;
    }

    io.github.losteddev.boxes.player.Account baccount = io.github.losteddev.boxes.database.Database.getInstance().getAccount(player.getUniqueId());
    if (baccount == null) {
      player.closeInventory();
      return;
    }

    List<ItemStack> items = new ArrayList<>();
    for (Box box : (List<Box>) baccount.getBoxes()) {
      ItemStack icon = box.getIcon();
      items.add(icon);
      boxes.put(icon, box);
    }

    if (lastListSize != -1 && lastListSize != items.size()) {
      items.clear();
      new MysteryVaultMenu(player, npc);
      return;
    }

    if (items.size() == 0) {
      this.removeSlotsWith(BukkitUtils.deserializeItemStack(config.getAsString("empty")), config.getAsInt("empty-slot"));
    }

    this.actions.clear();
    for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
      if (entry.getKey() >= 0 && entry.getKey() < (config.getRows() * 9)) {
        String stack = entry.getValue().getStack();

        stack = stack.replace("{last_rewards}", baccount.getLastItems());

        ItemStack item = BukkitUtils.deserializeItemStack(stack);
        this.removeSlotsWith(item, entry.getKey());
        this.actions.put(item, entry.getValue().getAction());
      }
    }

    this.setItems(items);
  }

  public void cancel() {
    super.cancel();
    HandlerList.unregisterAll(this);
    this.boxes.clear();
    this.boxes = null;
    this.actions.clear();
    this.actions = null;
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent evt) {
    if (evt.getPlayer().equals(player)) {
      this.cancel();
    }
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent evt) {
    if (evt.getPlayer().equals(player) && evt.getInventory().equals(getCurrentInventory())) {
      this.cancel();
    }
  }
}
