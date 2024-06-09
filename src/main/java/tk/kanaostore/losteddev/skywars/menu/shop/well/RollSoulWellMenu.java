package tk.kanaostore.losteddev.skywars.menu.shop.well;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import tk.kanaostore.losteddev.skywars.cosmetics.Cosmetic;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticServer;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticType;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.menu.ConfigMenu;
import tk.kanaostore.losteddev.skywars.menu.api.UpdatablePlayerMenu;
import tk.kanaostore.losteddev.skywars.menu.shop.SoulWellMenu;
import tk.kanaostore.losteddev.skywars.nms.Sound;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;

@SuppressWarnings("deprecation")
public class RollSoulWellMenu extends UpdatablePlayerMenu {

  protected static final ConfigMenu config = ConfigMenu.getByName("wellroll");

  @EventHandler
  public void onInventoryClick(InventoryClickEvent evt) {
    if (evt.getInventory().equals(this.getInventory())) {
      evt.setCancelled(true);
    }
  }

  private boolean back;
  private Account account;
  private List<RollList> list;
  private List<Cosmetic> rewards;
  private Map<ItemStack, Cosmetic> itemMap;

  public RollSoulWellMenu(Player player, boolean back, int rolls) {
    super(player, config.getTitle(), config.getRows());
    this.back = back;
    this.account = Database.getInstance().getAccount(player.getUniqueId());
    this.list = new ArrayList<>(rolls);
    this.rewards = new ArrayList<>();
    this.itemMap = new HashMap<>();
    for (Cosmetic cosmetic : CosmeticServer.SKYWARS.listCosmetics()) {
      if (cosmetic.getType() == CosmeticType.SKYWARS_KIT || cosmetic.getType() == CosmeticType.SKYWARS_PERK || cosmetic.getType() == CosmeticType.SKYWARS_CAGE) {
        if (cosmetic.canBeFoundInBox(player)) {
          rewards.add(cosmetic);
          itemMap.put(cosmetic.getIcon(), cosmetic);
        }
      }
    }

    for (List<Integer> roll : rolls == 1 ? roll1 : rolls == 2 ? roll2 : rolls == 3 ? roll3 : rolls == 4 ? roll4 : roll5) {
      this.getSlots().removeAll(roll);
      list.add(new RollList(this, roll));
      int mid = roll.get(2);
      if (mid != 18) {
        this.getSlots().remove((Object) (mid - 1));
        this.setItem(mid - 1, BukkitUtils.deserializeItemStack(config.getAsString("glass")));
      }
      if (mid != 26) {
        this.getSlots().remove((Object) (mid + 1));
        this.setItem(mid + 1, BukkitUtils.deserializeItemStack(config.getAsString("glass")));
      }
    }

    this.getSlots()
        .forEach(slot -> this.setItem(slot, BukkitUtils.deserializeItemStack("STAINED_GLASS_PANE:" + (ThreadLocalRandom.current().nextInt(6) + 1) + " : 1 : display=&r ")));

    this.open();
    this.register(2);
  }

  private int ticks;

  @Override
  public void update() {
    if (!this.getInventory().getViewers().contains(player)) {
      player.openInventory(getInventory());
    }

    if (ticks <= 90) {
      if (ticks <= 60 || ticks % 5 == 0) {
        Sound.NOTE_PLING.play(player, 0.4f, 4.0f);
        Sound.NOTE_BASS_GUITAR.play(player, 0.2f, 2.0f);
        Sound.NOTE_STICKS.play(player, 1.0f, 1.0f);
      }
      this.getSlots().forEach(slot -> this.getItem(slot).setDurability((short) (ThreadLocalRandom.current().nextInt(6) + 1)));
    }

    if (ticks == 100) {
      account.removeStat("souls", list.size() * 10);
      this.removeAll();
      Sound.LEVEL_UP.play(player, 1.0f, 1.0f);
    }
    list.forEach(list -> list.tick(ticks));

    if (ticks == 120) {
      this.cancel();
      new SoulWellMenu(player, back);
      return;
    }

    ticks++;
  }

  @Override
  public void cancel() {
    super.cancel();
    this.list.clear();
    this.list = null;
    this.rewards.clear();
    this.rewards = null;
    HandlerList.unregisterAll(this);
  }


  protected Account getAccount() {
    return account;
  }

  protected Map<ItemStack, Cosmetic> getItemMap() {
    return itemMap;
  }

  protected List<Cosmetic> getRewardsList() {
    return rewards;
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent evt) {
    if (evt.getPlayer().equals(player)) {
      this.cancel();
    }
  }

  private static List<List<Integer>> roll1 = Arrays.asList(Arrays.asList(4, 13, 22, 31, 40)),
      roll2 = Arrays.asList(Arrays.asList(3, 12, 21, 30, 39), Arrays.asList(5, 14, 23, 32, 41)),
      roll3 = Arrays.asList(Arrays.asList(2, 11, 20, 29, 38), Arrays.asList(4, 13, 22, 31, 40), Arrays.asList(6, 15, 24, 33, 42)),
      roll4 = Arrays.asList(Arrays.asList(1, 10, 19, 28, 37), Arrays.asList(3, 12, 21, 30, 39), Arrays.asList(5, 14, 23, 32, 41), Arrays.asList(7, 16, 25, 34, 43)),
      roll5 = Arrays.asList(Arrays.asList(0, 9, 18, 27, 36), Arrays.asList(2, 11, 20, 29, 38), Arrays.asList(4, 13, 22, 31, 40), Arrays.asList(6, 15, 24, 33, 42),
          Arrays.asList(8, 17, 26, 35, 44));
}
