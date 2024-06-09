package tk.kanaostore.losteddev.skywars.database.player;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@SuppressWarnings("unchecked")
public class DeliveryContainer {

  private StatsContainer update;
  private JSONObject value;

  public DeliveryContainer(StatsContainer update, String value) {
    this.update = update;
    try {
      this.value = (JSONObject) new JSONParser().parse(value);
    } catch (Exception ex) {
      throw new IllegalArgumentException("\"" + value + "\" is not a JSONObject: ", ex);
    }
  }

  public void put(int id, long time) {
    this.value.put(String.valueOf(id), String.valueOf(time));
    this.update.set(this.value.toString());
  }
  
  public long get(int id) {
    return Long.valueOf(this.value.containsKey(String.valueOf(id)) ? this.value.get(String.valueOf(id)).toString() : "0");
  }
}
