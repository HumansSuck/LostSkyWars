package tk.kanaostore.losteddev.skywars.cosmetics.skywars.perks;

import org.bukkit.event.EventHandler;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.api.event.player.SkyWarsPlayerDeathEvent;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticRarity;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsPerk;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;

public class Nourishment extends SkyWarsPerk {

  private int mode;

  private static final ConfigUtils CONFIG = ConfigUtils.getConfig("perks");

  public Nourishment(int mode) {
    super(CONFIG.getInt("nourishment.id"),
        CONFIG.getString("nourishment.name"),
        CosmeticRarity.fromName(CONFIG.getString("nourishment.rarity")),
        CONFIG.getString("nourishment.permission"),
        BukkitUtils.deserializeItemStack(CONFIG.getString("nourishment.icon")),
        CONFIG.getInt("nourishment.price"));
    this.mode = mode;
    
    this.register(Main.getInstance());
  }
  
  @EventHandler
  public void onPlayerDeath(SkyWarsPlayerDeathEvent evt) {
    if (evt.isKilled() && isAbleToUse(evt.getKiller())) {
      evt.getKiller().setFoodLevel(20);
      evt.getKiller().setSaturation(5.0F);
    }
  }
  
  @Override
  public int getMode() {
    return mode;
  }
}
