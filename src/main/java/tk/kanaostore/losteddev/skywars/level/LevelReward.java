package tk.kanaostore.losteddev.skywars.level;

import org.bukkit.Bukkit;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.player.Account;

public class LevelReward {
  
  private RewardType type;
  private String value;
  
  public LevelReward(String parse) {
    if (parse == null) {
      parse = "";
    }
    String[] splitter = parse.split(":");
    
    RewardType type = RewardType.from(splitter[0]);
    if (type == null || splitter.length < 2) {
      this.type = RewardType.COMMAND;
      this.value = "tellraw {player} Invalid reward \"" + parse + "\"";
      return;
    }
    
    this.type = type;
    this.value = splitter[1];
    if (type != RewardType.COMMAND) {
      try {
        Integer.parseInt(this.value);
      } catch (NumberFormatException ex) {
        this.value = "tellraw {player} Invalid reward \"" + parse + "\"";
      }
    }
  }
  
  public void apply(Account account) {
    if (type == RewardType.COINS) {
      account.addStat("coins", Integer.parseInt(this.value));
    } else if (type == RewardType.SOULS) {
      account.addStat("souls", Integer.parseInt(this.value));
      if (account.getInt("souls") > account.getContainers("account").get("sw_maxsouls").getAsInt()) {
        account.getContainers("skywars").get("souls").set(account.getContainers("account").get("sw_maxsouls").getAsInt());
      }
    } else if (type == RewardType.MYSTERY_BOX) {
      if (Main.lostboxes) {
        for (int i = 0; i < Integer.parseInt(this.value); i++) {
          io.github.losteddev.boxes.api.box.Box box = io.github.losteddev.boxes.api.LostBoxesAPI.randomBox(7);
          io.github.losteddev.boxes.database.Database.getInstance().getAccount(account.getUniqueId()).addBox(box);
        }
      } else {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + account.getName() + " LostBoxes not found!");
      }
    } else if (type == RewardType.MYSTERY_DUST) {
      account.addMysteryDusts(Integer.parseInt(this.value));
    } else {
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.value.replace("{player}", account.getName()));
    }
  }
  
  static enum RewardType {
    COINS,
    SOULS,
    MYSTERY_BOX,
    MYSTERY_DUST,
    COMMAND;
    
    public static RewardType from(String name) {
      for (RewardType type : values()) {
        if (type.name().equalsIgnoreCase(name)) {
          return type;
        }
      }
      
      return null;
    }
  }
}
