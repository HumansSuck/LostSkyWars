package tk.kanaostore.losteddev.skywars.database.player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticServer;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticType;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

public class SelectedContainer {

  private Map<Integer, String> map;

  public SelectedContainer(CosmeticServer server, String selected) {
    this.map = new LinkedHashMap<>();
    for (CosmeticType type : CosmeticType.values()) {
      if (type.name().startsWith(server.name()) && type.getIndex() != -1) {
        List<String> list = new ArrayList<>(type.getSize());
        for (int i = 0; i < type.getSize(); i++) {
          list.add("0");
        }
        this.map.put(type.getIndex() + 1, StringUtils.join(list, ":"));
        list.clear();
        list = null;
      }
    }

    for (int i = 0; i < selected.split(" : ").length; i++) {
      String splitted = selected.split(" : ")[i];
      List<String> sb = new ArrayList<>();
      for (int j = 0; j < this.map.get(i + 1).split(":").length; j++) {
        if (splitted.split(":").length <= j) {
          sb.add("0");
          continue;
        }

        sb.add(splitted.split(":")[j]);
      }

      splitted = StringUtils.join(sb, ":");
      this.map.put(i + 1, splitted);
      sb.clear();
      sb = null;
      splitted = null;
    }
  }

  public void set(CosmeticType type, int index, String value) {
    String old = this.map.get(type.getIndex() + 1);
    List<String> append = new ArrayList<>();
    for (int i = 0; i < old.split(":").length; i++) {
      if ((i + 1) == index) {
        append.add(value);
        continue;
      }

      append.add(old.split(":")[i]);
    }

    old = StringUtils.join(append, ":");
    this.map.put(type.getIndex() + 1, old);
    append.clear();
    append = null;
  }

  public String get(CosmeticType type, int index) {
    String returns = this.map.get(type.getIndex() + 1).split(":")[index - 1];
    this.map.clear();
    this.map = null;
    return returns;
  }

  public String build() {
    String returns = StringUtils.join(map.values(), " : ");
    this.map.clear();
    this.map = null;
    return returns;
  }
}
