package tk.kanaostore.losteddev.skywars.well;

import java.util.ArrayList;
import java.util.List;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;

public class WellUpgrade {

  private String name;
  private int requires;
  private int amount;
  private int price;

  public WellUpgrade(String name, int requires, int amount, int price) {
    this.name = name;
    this.requires = requires;
    this.amount = amount;
    this.price = price;
  }

  public String getName() {
    return name;
  }

  public int getRequires() {
    return requires;
  }

  public int getAmount() {
    return amount;
  }

  public int getPrice() {
    return price;
  }

  private static List<WellUpgrade> wins = new ArrayList<>(), maxs = new ArrayList<>();

  public static void setupUpgrades() {
    ConfigUtils cu = ConfigUtils.getConfig("wellupgrades", "plugins/LostSkyWars/menus");

    for (String key : cu.getSection("winupgrades").getKeys(false)) {
      wins.add(new WellUpgrade(cu.getString("winupgrades." + key + ".name"), cu.getInt("winupgrades." + key + ".requires"), cu.getInt("winupgrades." + key + ".amount"),
          cu.getInt("winupgrades." + key + ".price")));
    }

    for (String key : cu.getSection("maxupgrades").getKeys(false)) {
      maxs.add(new WellUpgrade(cu.getString("maxupgrades." + key + ".name"), cu.getInt("maxupgrades." + key + ".requires"), cu.getInt("maxupgrades." + key + ".amount"),
          cu.getInt("maxupgrades." + key + ".price")));
    }
  }

  public static WellUpgrade getNextWin(int current) {
    return wins.stream().filter(up -> up.getRequires() == current).findFirst().orElse(null);
  }

  public static WellUpgrade getNextMax(int current) {
    return maxs.stream().filter(up -> up.getRequires() == current).findFirst().orElse(null);
  }
}
