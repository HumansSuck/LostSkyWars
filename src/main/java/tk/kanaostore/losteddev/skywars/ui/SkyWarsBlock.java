package tk.kanaostore.losteddev.skywars.ui;

import org.bukkit.Material;

public class SkyWarsBlock {
  
  private Material material;
  private byte data;

  public SkyWarsBlock(Material material, byte data) {
    this.material = material;
    this.data = data;
  }

  public Material getMaterial() {
    return material;
  }

  public byte getData() {
    return data;
  }

  @Override
  public String toString() {
    return "SkyWarsBlock{material=" + material + ", data=" + data + "}";
  }
}
