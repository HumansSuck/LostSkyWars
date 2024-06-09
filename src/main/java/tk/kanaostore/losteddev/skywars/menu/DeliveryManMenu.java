package tk.kanaostore.losteddev.skywars.menu;

import java.util.ArrayList;
import java.util.Calendar;
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
import org.bukkit.inventory.meta.ItemMeta;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.database.player.DeliveryContainer;
import tk.kanaostore.losteddev.skywars.delivery.Delivery;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigAction;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu.ConfigItem;
import tk.kanaostore.losteddev.skywars.menu.api.UpdatablePlayerMenu;
import tk.kanaostore.losteddev.skywars.nms.Sound;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;
import tk.kanaostore.losteddev.skywars.utils.TimeUtils;

@SuppressWarnings("deprecation")
public class DeliveryManMenu extends UpdatablePlayerMenu {

  private static final ConfigMenu config = ConfigMenu.getByName("deliveryman");

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
          Delivery delivery = deliveries.get(item);
          if (delivery != null) {
            DeliveryContainer dc = account.getContainers("account").get("deliveries").getDelivery();
            if (dc.get(delivery.getId()) > System.currentTimeMillis()) {
              Sound.ANVIL_LAND.play(player, 1.0f, 1.0f);
              player.sendMessage(StringUtils.formatColors(config.getAsString("warn-claimed").replace("{time}", TimeUtils.getTimeUntil(dc.get(delivery.getId())))));
            } else if (!delivery.hasPermission(player)) {
              player.sendMessage(StringUtils.formatColors(config.getAsString("warn-cant")));
              player.closeInventory();
            } else {
              Sound.LEVEL_UP.play(player, 1.0f, 1.0f);
              dc.put(delivery.getId(), System.currentTimeMillis() + delivery.getDays());
              delivery.listRewards().forEach(dr -> dr.apply(account));
              player.sendMessage(delivery.getClaim());
            }
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

  private Map<ItemStack, Delivery> deliveries;
  private Map<ItemStack, ConfigAction> actions;

  public DeliveryManMenu(Player player) {
    super(player, config.getTitle(), config.getRows());
    this.deliveries = new HashMap<>();
    this.actions = new HashMap<>();

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

    DeliveryContainer dc = account.getContainers("account").get("deliveries").getDelivery();
    for (Delivery delivery : Delivery.listDeliveries()) {
      String stack = delivery.getIcon();
      if (dc.get(delivery.getId()) > System.currentTimeMillis() || !delivery.hasPermission(player)) {
        stack = stack.replace("{color}", "&c");
      } else {
        stack = stack.replace("{color}", "&a");
      }

      stack = stack.replace("{month}", config.getAsString("m" + (Calendar.getInstance().get(Calendar.MONTH) + 1)));
      String lore = config.getAsString("lore-can");
      if (!delivery.hasPermission(player)) {
        lore = config.getAsString("lore-cant");
      } else if (dc.get(delivery.getId()) > System.currentTimeMillis()) {
        lore = config.getAsString("lore-claimed").replace("{next}", TimeUtils.getTimeUntil(dc.get(delivery.getId()))).replace("\\n", "\n");
      }
      stack = stack.replace("{lore}", lore);

      ItemStack icon = BukkitUtils.deserializeItemStack(stack);
      if (dc.get(delivery.getId()) > System.currentTimeMillis()) {
        if (icon.getType().name().contains("STORAGE_MINECART")) {
          icon.setType(Material.MINECART);
          icon.setDurability((short) 0);
        }
        ItemMeta meta = icon.getItemMeta();
        List<String> lores = new ArrayList<>();
        if (meta.getLore() != null) {
          lores.addAll(meta.getLore());
        }
        if (lores.size() > 0) {
          while (lores.size() != lore.split("\n").length) {
            lores.remove(0);
          }
        }
        meta.setLore(lores);
        icon.setItemMeta(meta);
      }
      this.setItem(delivery.getSlot(), icon);
      deliveries.put(icon, delivery);
    }

    this.actions.clear();
    for (Map.Entry<Integer, ConfigItem> entry : config.getItems().entrySet()) {
      if (entry.getKey() >= 0 && entry.getKey() < (config.getRows() * 9)) {
        String stack = entry.getValue().getStack();

        ItemStack item = BukkitUtils.deserializeItemStack(stack);
        this.setItem(entry.getKey(), item);
        this.actions.put(item, entry.getValue().getAction());
      }
    }
  }

  public void cancel() {
    super.cancel();
    HandlerList.unregisterAll(this);
    this.deliveries.clear();
    this.deliveries = null;
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
    if (evt.getPlayer().equals(player) && evt.getInventory().equals(getInventory())) {
      this.cancel();
    }
  }
}
