package tk.kanaostore.losteddev.skywars.level;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ImmutableList;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.cosmetics.Cosmetic;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticServer;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticType;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsSymbol;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;
import tk.kanaostore.losteddev.skywars.utils.FileUtils;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

public class Level {

  private int level;
  private double exp;
  private LevelReward reward;
  private String description;

  public Level(double exp, LevelReward reward, String description) {
    this.level = levels.size() + 1;
    this.exp = exp;
    this.reward = reward;
    this.description = description;
  }
  
  public int getLevel() {
    return this.level;
  }

  public String getLevel(Account account) {
    Cosmetic cosmetic = account.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_SYMBOL, 1);
    if (cosmetic == null || !(cosmetic instanceof SkyWarsSymbol)) {
      return StringUtils.formatNumber(this.level);
    }

    return ((SkyWarsSymbol) cosmetic).getSymbol() + StringUtils.formatNumber(this.level);
  }

  public double getExp() {
    return exp;
  }

  public LevelReward getReward() {
    return reward;
  }

  public String getDescription() {
    return description;
  }

  public double getExperienceUntil(double current) {
    Level level = this.getNext();
    if (level == null) {
      return 0.0;
    }

    return level.getExp() - current;
  }

  public Level getNext() {
    return levels.stream().filter(level -> level.getLevel() == this.level + 1).findFirst().orElse(null);
  }

  public static final LostLogger LOGGER = Main.LOGGER.getModule("Leveling");
  private static List<Level> levels = new ArrayList<>();

  public static void setupLevels() {
    ConfigUtils cu = ConfigUtils.getConfig("levels");
    for (String key : cu.getSection("levels").getKeys(false)) {
      if (!cu.contains("levels." + key + ".reward")) {
        FileUtils.deleteFile(new File("plugins/LostSkyWars/levels.yml"));
        LOGGER.log(LostLevel.WARNING, "Deleted old version from levels.yml. Restart the server.");
        System.exit(0);
        return;
      }

      levels.add(new Level(cu.getDouble("levels." + key + ".exp"), new LevelReward(cu.getString("levels." + key + ".reward")), cu.getString("levels." + key + ".description")));
    }

    LOGGER.log(LostLevel.INFO, "Loaded " + levels.size() + " levels!");
  }

  public static Level getByLevel(int level) {
    return levels.get(level - 1);
  }

  public static List<Level> listLevels() {
    return ImmutableList.copyOf(levels);
  }
}
