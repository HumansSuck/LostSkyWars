package tk.kanaostore.losteddev.skywars.cosmetics.skywars.perks;

import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.api.event.player.SkyWarsPlayerDeathEvent;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticRarity;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsPerk;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;

public class Bulldozer extends SkyWarsPerk {

  private int mode;
  private int seconds;

  private static final ConfigUtils CONFIG = ConfigUtils.getConfig("perks");

  public Bulldozer(int mode) {
    super(CONFIG.getInt("bulldozer.id"),
        CONFIG.getString("bulldozer.name"),
        CosmeticRarity.fromName(CONFIG.getString("bulldozer.rarity")),
        CONFIG.getString("bulldozer.permission"),
        BukkitUtils.deserializeItemStack(CONFIG.getString("bulldozer.icon").replace("{time}", CONFIG.getInt("bulldozer.time") + "")),
        CONFIG.getInt("bulldozer.price"));
    this.mode = mode;
    
    this.seconds = CONFIG.getInt("bulldozer.time");
    
    this.register(Main.getInstance());
  }
  
  @EventHandler
  public void onPlayerDeath(SkyWarsPlayerDeathEvent evt) {
    if (evt.isKilled() && isAbleToUse(evt.getKiller())) {
      evt.getKiller().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * seconds, 0));
    }
  }
  
  @Override
  public int getMode() {
    return mode;
  }
}
