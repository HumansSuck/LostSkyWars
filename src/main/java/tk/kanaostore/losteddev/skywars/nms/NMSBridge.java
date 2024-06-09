package tk.kanaostore.losteddev.skywars.nms;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.holograms.Hologram;
import tk.kanaostore.losteddev.skywars.holograms.HologramLine;
import tk.kanaostore.losteddev.skywars.holograms.entity.IArmorStand;

public abstract class NMSBridge {
    
  public abstract IArmorStand createArmorStand(Location location, String name, HologramLine line);
  
  public abstract BalloonEntity createBalloonLeash(Location location);
  
  public abstract BalloonEntity createBalloonBat(Location location, BalloonEntity leash);
  
  public abstract BalloonEntity createBalloonGiant(Location location, List<String> frames);
  
  public abstract Hologram getHologram(Entity entity);
  
  public abstract boolean isHologramEntity(Entity entity);
  
  public abstract void playChestAction(Location location, boolean open);
  
  public abstract void sendActionBar(Player player, String message);
  
  public abstract void sendTitle(Player player, String title, String subtitle);
  
  public abstract void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut);
  
  public abstract void sendTabHeaderFooter(Player player, String header, String footer);
}
