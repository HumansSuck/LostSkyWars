package tk.kanaostore.losteddev.skywars.menu.shop.well;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.inventory.ItemStack;
import tk.kanaostore.losteddev.skywars.cosmetics.Cosmetic;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsType;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

public class RollList {

  private RollSoulWellMenu menu;
  private List<Integer> slots;

  public RollList(RollSoulWellMenu menu, List<Integer> slots) {
    this.menu = menu;
    this.slots = slots;
  }

  public void tick(int ticks) {
    if (ticks <= 90) {
      if (ticks <= 60 || ticks % 5 == 0) {
        for (int i = slots.size() - 1; i > 0; i--) {
          menu.setItem(slots.get(i), menu.getItem(slots.get(i - 1)));
        }

        menu.setItem(slots.get(0), menu.getRewardsList().get(ThreadLocalRandom.current().nextInt(menu.getRewardsList().size())).getIcon());
      }
    }

    if (ticks == 100) {
      ItemStack reward = menu.getItem(slots.get(2));
      for (int slot : slots) {
        if (slots.get(2) != slot) {
          menu.remove(slot);
        }
      }
      Cosmetic c = menu.getItemMap().get(reward);
      menu.getAccount().getPlayer().sendMessage(StringUtils.formatColors(RollSoulWellMenu.config.getAsString("received").replace("{name}",
          c.getRarity().getColor() + c.getRawName() + " (" + c.getType().getName() + " - " + SkyWarsType.fromIndex(c.getMode()).getName() + ")")));
      if (!c.has(menu.getAccount())) {
        c.give(menu.getAccount());
      } else {
        int coins = RollSoulWellMenu.config.getAsInt("coins-" + c.getRarity().name().toLowerCase());
        menu.getAccount().addStat("coins", coins);
        menu.getAccount().getPlayer()
            .sendMessage(StringUtils.formatColors(RollSoulWellMenu.config.getAsString("already-have")
                .replace("{name}", c.getRarity().getColor() + c.getRawName() + " (" + c.getType().getName() + " - " + SkyWarsType.fromIndex(c.getMode()).getName() + ")")
                .replace("{coins}", StringUtils.formatNumber(coins))));
      }
    }
  }
}
