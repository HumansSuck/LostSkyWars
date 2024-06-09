package tk.kanaostore.losteddev.skywars.database.player;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticType;

@SuppressWarnings("unchecked")
public class CosmeticContainer {

  private StatsContainer update;
  private JSONObject value;

  public CosmeticContainer(StatsContainer update, CosmeticType type, String value) {
    this.update = update;
    try {
      this.value = (JSONObject) new JSONParser().parse(value);
    } catch (Exception ex) {
      throw new IllegalArgumentException("\"" + value + "\" is not a JSONObject: ", ex);
    }
    
    for (int index = 1; index <= type.getSize(); index++) {
      if (!this.value.containsKey(String.valueOf(index))) {
        this.value.put(String.valueOf(index), new JSONArray());
      }
    }
  }

  public void add(String value) {
    this.add(value, 1);
  }

  public void add(String value, int index) {
    JSONArray array = this.getArray(index);
    if (!array.contains(value)) {
      array.add(value);
    }

    this.update.set(this.value.toString());
  }

  public void remove(String value) {
    this.remove(value, 1);
  }

  public void remove(String value, int index) {
    JSONArray array = this.getArray(index);
    if (array.contains(value)) {
      array.remove(value);
    }

    this.update.set(this.value.toString());
  }

  public boolean contains(String value) {
    return this.contains(value, 1);
  }

  public boolean contains(String value, int index) {
    return this.getArray(index).contains(value);
  }

  public JSONArray getArray(int index) {
    return (JSONArray) this.value.get(String.valueOf(index));
  }
}
