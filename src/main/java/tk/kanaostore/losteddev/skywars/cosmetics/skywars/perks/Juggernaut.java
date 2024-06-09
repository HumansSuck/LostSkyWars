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

public class Juggernaut extends SkyWarsPerk {

  private int mode;
  private int seconds;

  private static final ConfigUtils CONFIG = ConfigUtils.getConfig("perks");

  public Juggernaut(int mode) {
    super(CONFIG.getInt("juggernaut.id"),
        CONFIG.getString("juggernaut.name"),
        CosmeticRarity.fromName(CONFIG.getString("juggernaut.rarity")),
        CONFIG.getString("juggernaut.permission"),
        BukkitUtils.deserializeItemStack(CONFIG.getString("juggernaut.icon").replace("{time}", CONFIG.getInt("juggernaut.time") + "")),
        CONFIG.getInt("juggernaut.price"));
    this.mode = mode;
    
    this.seconds = CONFIG.getInt("juggernaut.time");
    
    this.register(Main.getInstance());
  }
  
  @EventHandler
  public void onPlayerDeath(SkyWarsPlayerDeathEvent evt) {
    if (evt.isKilled() && isAbleToUse(evt.getKiller())) {
      evt.getKiller().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * seconds, 0));
    }
  }
  
  @Override
  public int getMode() {
    return mode;
  }
}
