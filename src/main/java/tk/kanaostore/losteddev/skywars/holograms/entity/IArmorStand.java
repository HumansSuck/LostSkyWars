package tk.kanaostore.losteddev.skywars.holograms.entity;

import org.bukkit.entity.ArmorStand;
import tk.kanaostore.losteddev.skywars.holograms.HologramLine;

public interface IArmorStand {
  
  public int getId();

  public void setName(String name);

  public void setLocation(double x, double y, double z);

  public boolean isDead();

  public void killEntity();

  public ArmorStand getEntity();

  public HologramLine getLine();
}
