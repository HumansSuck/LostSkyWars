package tk.kanaostore.losteddev.skywars.cosmetics.skywars.perks;

import org.bukkit.event.EventHandler;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.api.event.player.SkyWarsPlayerDeathEvent;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticRarity;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsPerk;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;

public class Knowledge extends SkyWarsPerk {

  private int mode;
  private int level;

  private static final ConfigUtils CONFIG = ConfigUtils.getConfig("perks");

  public Knowledge(int mode) {
    super(CONFIG.getInt("knowledge.id"),
        CONFIG.getString("knowledge.name"),
        CosmeticRarity.fromName(CONFIG.getString("knowledge.rarity")),
        CONFIG.getString("knowledge.permission"),
        BukkitUtils.deserializeItemStack(CONFIG.getString("knowledge.icon").replace("{level}", CONFIG.getInt("knowledge.level") + "")),
        CONFIG.getInt("knowledge.price"));
    this.mode = mode;
    
    this.level = CONFIG.getInt("knowledge.level");
    
    this.register(Main.getInstance());
  }
  
  @EventHandler
  public void onPlayerDeath(SkyWarsPlayerDeathEvent evt) {
    if (evt.isKilled() && isAbleToUse(evt.getKiller())) {
      evt.getKiller().setLevel(evt.getKiller().getLevel() + this.level);
    }
  }
  
  @Override
  public int getMode() {
    return mode;
  }
}
