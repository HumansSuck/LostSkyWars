package tk.kanaostore.losteddev.skywars.cosmetics;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsBalloon;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsCage;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsDeathCry;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsPerk;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsSymbol;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.kits.InsaneSkyWarsKit;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.kits.NormalSkyWarsKit;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.kits.RankedSkyWarsKit;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;

public abstract class Cosmetic {

  private int id;
  private CosmeticServer server;
  private CosmeticType type;
  private CosmeticRarity rarity;

  public Cosmetic(int id, CosmeticServer server, CosmeticType type, CosmeticRarity rarity) {
    this.id = id;
    this.server = server;
    this.type = type;
    this.rarity = rarity;
  }

  public void give(Account account) {
    this.give(account, this.getMode());
  }

  public void give(Account account, int mode) {
    account.getContainers(this.getServer().name().toLowerCase()).get(this.getType().getStats()).getCosmetics(type).add(String.valueOf(this.getId()), mode);
  }

  public boolean has(Account account) {
    return this.has(account, this.getMode());
  }

  public boolean has(Account account, int mode) {
    return account.getContainers(this.getServer().name().toLowerCase()).get(this.getType().getStats()).getCosmetics(type).contains(String.valueOf(this.getId()), mode);
  }

  public boolean selected(Account account) {
    return this.selected(account, 1);
  }

  public boolean selected(Account account, int mode) {
    Cosmetic c = account.getSelected(this.getServer(), this.getType(), mode);
    return c != null && c.equals(this);
  }

  public int getId() {
    return id;
  }

  public CosmeticServer getServer() {
    return server;
  }

  public CosmeticType getType() {
    return type;
  }

  public CosmeticRarity getRarity() {
    return rarity;
  }
  
  public int getMode() {
    return 1;
  }
  
  public abstract String getName();
  
  public abstract String getRawName();
  
  public abstract int getCoins();

  public abstract boolean canBeFoundInBox(Player player);
  
  public abstract ItemStack getIcon();

  public static final LostLogger LOGGER = Main.LOGGER.getModule("Cosmetics");

  public static void setupCosmetics() {
    // cages
    SkyWarsCage.setupCages();

    // kits
    NormalSkyWarsKit.setupKits();
    InsaneSkyWarsKit.setupKits();
    RankedSkyWarsKit.setupKits();
    
    // perks
    SkyWarsPerk.setupPerks();
    
    // deathcries
    SkyWarsDeathCry.setupDeathCries();
    
    // ballons
    SkyWarsBalloon.setupBallons();
    
    // symbols
    SkyWarsSymbol.setupSymbols();

    int size = 0;
    for (CosmeticServer server : CosmeticServer.values()) {
      size += server.listCosmetics().size();
    }
    LOGGER.log(LostLevel.INFO, "Loaded " + size + " cosmetics!");
  }

  public static Cosmetic findFrom(CosmeticServer server, CosmeticType type, int index, String id) {
    return findFrom(server, type, index, id, Cosmetic.class);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Cosmetic> T findFrom(CosmeticServer server, CosmeticType type, int index, String id, Class<T> clazz) {
    for (Cosmetic cosmetic : server.listCosmetics()) {
      if (cosmetic.getType().equals(type) && cosmetic.getMode() == index && id.equals(String.valueOf(cosmetic.getId()))) {
        return (T) cosmetic;
      }
    }

    return null;
  }
}
