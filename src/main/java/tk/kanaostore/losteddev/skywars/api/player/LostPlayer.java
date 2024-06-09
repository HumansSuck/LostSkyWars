package tk.kanaostore.losteddev.skywars.api.player;

import tk.kanaostore.losteddev.skywars.api.server.SkyWarsServer;
import tk.kanaostore.losteddev.skywars.player.Account;

public class LostPlayer {

  private Account a;

  public LostPlayer(Account a) {
    this.a = a;
  }
  
  public void destroy() {
    this.a = null;
  }

  public void addExp(double exp) {
    a.addExp(exp);
  }

  public void setCoins(int coins) {
    a.getContainers("skywars").get("coins").set(coins);
  }

  public void addCoins(int coins) {
    a.addStat("coins", coins);
  }

  public void removeCoins(int coins) {
    a.removeStat("coins", coins);
  }

  public void setSouls(int souls) {
    a.getContainers("skywars").get("souls").set(souls);
  }

  public void addSouls(int souls) {
    a.addStat("souls", souls);
    if (this.getSouls() > a.getContainers("account").get("sw_maxsouls").getAsInt()) {
      this.setSouls(a.getContainers("account").get("sw_maxsouls").getAsInt());
    }
  }

  public void removeSouls(int souls) {
    a.removeStat("souls", souls);
  }

  public int getCoins() {
    return a.getInt("coins");
  }

  public int getSouls() {
    return a.getInt("souls");
  }

  public double getCurrentExp() {
    return a.getExp();
  }

  public int getLevel() {
    return a.getLevel();
  }

  public int getMysteryDusts() {
    return a.getMysteryDusts();
  }
  
  public SkyWarsServer getServer() {
    return a.getServer();
  }
  
  public Account getHandle() {
    return a;
  }
}
