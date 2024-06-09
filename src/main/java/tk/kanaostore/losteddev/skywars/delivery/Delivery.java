package tk.kanaostore.losteddev.skywars.delivery;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import com.google.common.collect.ImmutableList;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

public class Delivery {
  
  private int id;
  private int days;
  private int slot;
  private String permission;
  private List<DeliveryReward> rewards;
  private String icon;
  private String claim;
  
  public Delivery(int days, int slot, String permission, List<DeliveryReward> rewards, String icon, String claim) {
    this.id = deliveries.size();
    this.days = days;
    this.slot = slot;
    this.permission = permission;
    this.rewards = rewards;
    this.icon = icon;
    this.claim = StringUtils.formatColors(claim);
  }
  
  public int getId() {
    return id;
  }
  
  public long getDays() {
    return TimeUnit.DAYS.toMillis(days);
  }
  
  public int getSlot() {
    return slot;
  }
  
  public boolean hasPermission(Player player) {
    return permission.equalsIgnoreCase("none") || player.hasPermission(permission);
  }
  
  public List<DeliveryReward> listRewards() {
    return rewards;
  }
  
  public String getIcon() {
    return icon;
  }
  
  public String getClaim() {
    return claim;
  }
  
  private static List<Delivery> deliveries = new ArrayList<>();
  
  public static void setupDeliveries() {
    ConfigUtils cu = ConfigUtils.getConfig("deliveries");
    
    ConfigurationSection section = cu.getSection("deliveries");
    for (String key :  section.getKeys(false)) {
      ConfigurationSection delivery = section.getConfigurationSection(key);
      
      int days = delivery.getInt("days");
      int slot = delivery.getInt("slot");
      String permission = delivery.getString("permission");
      List<DeliveryReward> rewards = new ArrayList<>();
      for (String parse : delivery.getStringList("rewards")) {
        rewards.add(new DeliveryReward(parse));
      }
      String icon = delivery.getString("icon");
      String claim = delivery.getString("claim");
      
      deliveries.add(new Delivery(days, slot, permission, rewards, icon, claim));
    }
  }
  
  public static List<Delivery> listDeliveries() {
    return ImmutableList.copyOf(deliveries);
  }
}
