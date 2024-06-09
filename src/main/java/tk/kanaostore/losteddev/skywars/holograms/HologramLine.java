package tk.kanaostore.losteddev.skywars.holograms;

import org.bukkit.Location;
import tk.kanaostore.losteddev.skywars.holograms.entity.IArmorStand;
import tk.kanaostore.losteddev.skywars.nms.NMS;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

public class HologramLine {

  private Location location;
  private IArmorStand armor;
  private String line;
  private Hologram hologram;

  public HologramLine(Hologram hologram, Location location, String line) {
    this.line = StringUtils.formatColors(line);
    this.location = location;
    this.armor = null;
    this.hologram = hologram;
  }

  public void spawn() {
    if (armor == null) {
      this.armor = NMS.createArmorStand(location, line, this);
    }
  }

  public void despawn() {
    if (armor != null) {
      this.armor.killEntity();
      this.armor = null;
    }
  }

  public void setLocation(Location location) {
    if (armor != null) {
      this.armor.setLocation(location.getX(), location.getY(), location.getZ());
    }
  }

  public void setLine(String line) {
    if (this.line.equals(StringUtils.formatColors(line))) {
      this.armor.setName(this.line + "§r");
      this.line = this.line + "§r";
      return;
    }
    
    this.line = StringUtils.formatColors(line);
    if (armor == null) {
      if (hologram.isSpawned()) {
        this.spawn();
      }

      return;
    }

    this.armor.setName(this.line);
    return;
  }

  public Location getLocation() {
    return location;
  }

  public IArmorStand getArmor() {
    return armor;
  }

  public String getLine() {
    return line;
  }

  public Hologram getHologram() {
    return hologram;
  }
}
