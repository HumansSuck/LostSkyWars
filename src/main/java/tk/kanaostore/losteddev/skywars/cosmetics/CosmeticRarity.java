package tk.kanaostore.losteddev.skywars.cosmetics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

public enum CosmeticRarity {
  LEGENDARY("§6LEGENDARY", 10),
  EPIC("§5EPIC", 30),
  RARE("§9RARE", 60),
  COMMON("§aCOMMON", 100);
  
  private String name;
  private double percentage;

  CosmeticRarity(String name, double percentage) {
    this.name = name;
    this.percentage = percentage;
  }
  
  public void translate() {
    if (this == LEGENDARY) {
      this.name = Language.options$rarity$legendary;
    } else if (this == EPIC) {
      this.name = Language.options$rarity$epic;
    } else if (this == RARE) {
      this.name = Language.options$rarity$rare;
    } else {
      this.name = Language.options$rarity$common;
    }
  }

  public String getName() {
    return this.name;
  }
  
  public String getColor() {
    return StringUtils.getFirstColor(this.name);
  }
  
  public double getPercentage() {
    return percentage;
  }
  
  public List<Double> getPercentages(int diviser) {
    List<Double> list = new ArrayList<>();
    for (int i = 1; i <= diviser; i++) {
      list.add((percentage / diviser) * i);
    }
    
    Collections.reverse(list);
    return list;
  }
  
  public static CosmeticRarity getRandomRarity(double random) {
    for (CosmeticRarity rarity : values()) {
      if (random <= rarity.getPercentage()) {
        return rarity;
      }
    }
    
    return COMMON;
  }
  
  public static CosmeticRarity fromName(String name) {
    for (CosmeticRarity rarity : values()) {
      if (rarity.name().equalsIgnoreCase(name)) {
        return rarity;
      }
    }
    
    return COMMON;
  }
}