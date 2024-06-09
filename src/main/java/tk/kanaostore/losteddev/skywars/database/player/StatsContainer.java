package tk.kanaostore.losteddev.skywars.database.player;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticServer;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticType;

public class StatsContainer {

  private Object value;

  public StatsContainer(Object value) {
    this.value = value;
  }

  public void set(Object value) {
    this.value = value;
  }

  public void addInt(int amount) {
    this.value = getAsInt() + amount;
  }
  
  public void addDouble(double amount) {
    this.value = getAsDouble() + amount;
  }

  public void removeInt(int amount) {
    this.value = getAsInt() - amount;
  }
  
  public void removeDouble(double amount) {
    this.value = getAsDouble() - amount;
  }
  
  public Object get() {
    return value;
  }

  public int getAsInt() {
    return Integer.parseInt(this.getAsString());
  }
  
  public long getAsLong() {
    return Long.valueOf(this.getAsString());
  }
  
  public double getAsDouble() {
    return Double.parseDouble(this.getAsString());
  }

  public String getAsString() {
    return value.toString();
  }
  
  public boolean getAsBoolean() {
    return Boolean.valueOf(this.getAsString());
  }
  
  public JSONArray getAsJsonArray() {
    try {
      return (JSONArray) new JSONParser().parse(this.getAsString());
    } catch (Exception ex) {
      throw new IllegalArgumentException("\"" + value + "\" is not a JsonArray: ", ex);
    }
  }
  
  public CosmeticContainer getCosmetics(CosmeticType type) {
    return new CosmeticContainer(this, type, this.getAsString());
  }
  
  public DeliveryContainer getDelivery() {
    return new DeliveryContainer(this, this.getAsString());
  }
  
  public SelectedContainer getSelected(CosmeticServer server) {
    return new SelectedContainer(server, this.getAsString());
  }
}
