package tk.kanaostore.losteddev.skywars.rank;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;

public class Rank {

  private String name;
  private String coloredName;
  private String prefix;
  private String permission;
  private String onJoin;
  private int boxPercentage;
  private int ordinal;

  public Rank(String name, String coloredName, String prefix, String permission, String onJoin, int boxPercentage) {
    this.name = name;
    this.coloredName = StringUtils.formatColors(coloredName);
    this.prefix = StringUtils.formatColors(prefix);
    this.permission = permission;
    this.onJoin = onJoin;
    this.boxPercentage = boxPercentage;
    this.ordinal = ranks.size() + 1;
  }

  public void apply(Player player) {
    Account account = Database.getInstance().getAccount(player.getUniqueId());
    if (account != null) {
      account.getContainers("account").get("lastRank").set(StringUtils.deformatColors(StringUtils.getLastColor(this.prefix)));
      account = null;
    }
    if (Language.options$ranks$chat) {
      player.setDisplayName(this.prefix + player.getName());
    }
    if (Language.options$ranks$tab) {
      TagUtils.setTag(player.getName(), this.prefix, "", this.ordinal);
    }
  }

  public String getName() {
    return name;
  }

  public String getColoredName() {
    return coloredName;
  }

  public String getPrefix() {
    return prefix;
  }

  public String getPermission() {
    return permission;
  }

  public String getOnJoin() {
    return onJoin.isEmpty() ? null : onJoin;
  }

  public boolean receiveBox() {
    return ThreadLocalRandom.current().nextInt(100) <= boxPercentage;
  }

  public int ordinal() {
    return ordinal;
  }

  public static final LostLogger LOGGER = Main.LOGGER.getModule("Ranks");
  private static final List<Rank> ranks = new ArrayList<>();

  public static void setupRanks() {
    ConfigUtils cu = ConfigUtils.getConfig("ranks");
    for (String key : cu.getSection("ranks").getKeys(false)) {
      if (!cu.contains("ranks." + key + ".onJoin")) {
        cu.set("ranks." + key + ".onJoin", cu.getString("ranks." + key + ".permission").equalsIgnoreCase("none") ? "" : "{display} &6joined the lobby!");
      }
      if (!cu.contains("ranks." + key + ".percentage-box")) {
        cu.set("ranks." + key + ".percentage-box", cu.getString("ranks." + key + ".permission").equalsIgnoreCase("none") ? 7 : 15);
      }

      ranks.add(new Rank(key, cu.getString("ranks." + key + ".name"), cu.getString("ranks." + key + ".prefix"), cu.getString("ranks." + key + ".permission"),
          cu.getString("ranks." + key + ".onJoin"), cu.getInt("ranks." + key + ".percentage-box")));
    }

    if (ranks.isEmpty()) {
      ranks.add(new Rank("Default", "§7Default", "§7", "none", null, 7));
    }

    LOGGER.log(LostLevel.INFO, "Loaded " + ranks.size() + " ranks!");
  }

  public static Rank getRank(Player player) {
    for (Rank rank : ranks) {
      if (player.hasPermission(rank.getPermission()) || rank.getPermission().equals("none")) {
        return rank;
      }
    }

    return ranks.get(ranks.size() - 1);
  }
}
