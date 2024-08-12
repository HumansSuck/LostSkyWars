package tk.kanaostore.losteddev.skywars.cosmetics.skywars.perks;

import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.event.EventHandler;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.api.event.player.SkyWarsPlayerDeathEvent;
import tk.kanaostore.losteddev.skywars.api.event.player.SkyWarsPlayerDeathEvent.SkyWarsDeathCause;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticRarity;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsPerk;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;

public class VoidMaster extends SkyWarsPerk {

  private int mode;
  private int percentage;

  private static final ConfigUtils CONFIG = ConfigUtils.getConfig("perks");

  public VoidMaster(int mode) {
    super(CONFIG.getInt("voidmaster.id"),
        CONFIG.getString("voidmaster.name"),
        CosmeticRarity.fromName(CONFIG.getString("voidmaster.rarity")),
        CONFIG.getString("voidmaster.permission"),
        BukkitUtils.deserializeItemStack(CONFIG.getString("voidmaster.icon").replace("{percentage}", CONFIG.getInt("voidmaster.percentage") + "%")),
        CONFIG.getInt("voidmaster.price"));
    this.mode = mode;
    
    this.percentage = CONFIG.getInt("voidmaster.percentage");
    
    this.register(Main.getInstance());
  }
  
  @EventHandler
  public void onPlayerDeath(SkyWarsPlayerDeathEvent evt) {
    if (evt.isKilled() && isAbleToUse(evt.getKiller())) {
        if (ThreadLocalRandom.current().nextInt(100) < percentage) {
          evt.getKiller().getInventory().addItem(BukkitUtils.deserializeItemStack("ENDER_PEARL : 1"));
      }
    }
  }
  
  @Override
  public int getMode() {
    return mode;
  }
}
