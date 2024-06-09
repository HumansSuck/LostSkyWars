package tk.kanaostore.losteddev.skywars.cosmetics.skywars.perks;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticRarity;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsPerk;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;

public class EnderMastery extends SkyWarsPerk {

  private int mode;
  private int percentage;

  private static final ConfigUtils CONFIG = ConfigUtils.getConfig("perks");

  public EnderMastery(int mode) {
    super(CONFIG.getInt("endermastery.id"), CONFIG.getString("endermastery.name"), CosmeticRarity.fromName(CONFIG.getString("endermastery.rarity")),
        CONFIG.getString("endermastery.permission"),
        BukkitUtils.deserializeItemStack(CONFIG.getString("endermastery.icon").replace("{percentage}", CONFIG.getInt("endermastery.percentage") + "%")),
        CONFIG.getInt("endermastery.price"));
    this.mode = mode;

    this.percentage = CONFIG.getInt("endermastery.percentage");

    this.register(Main.getInstance());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerTeleport(PlayerTeleportEvent evt) {
    if (evt.getCause() == TeleportCause.ENDER_PEARL) {
      Account account = Database.getInstance().getAccount(evt.getPlayer().getUniqueId());
      if (account != null && account.getServer() != null && account.getServer().getType().getIndex() == mode) {
        evt.setCancelled(true);
        double damage = 5.0;

        if (isAbleToUse(evt.getPlayer())) {
          damage = damage - ((percentage * damage) / 100.0);
        }

        evt.getPlayer().teleport(evt.getTo());
        if (damage > 0.0) {
          evt.getPlayer().damage(damage);
        }
      }
    }
  }

  @Override
  public int getMode() {
    return mode;
  }
}
