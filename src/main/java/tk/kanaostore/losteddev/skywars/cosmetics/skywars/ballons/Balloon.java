package tk.kanaostore.losteddev.skywars.cosmetics.skywars.ballons;

import org.bukkit.Location;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsBalloon;
import tk.kanaostore.losteddev.skywars.nms.BalloonEntity;
import tk.kanaostore.losteddev.skywars.nms.NMS;
import tk.kanaostore.losteddev.skywars.utils.MinecraftVersion;

public class Balloon {

  private BalloonEntity leash;
  private BalloonEntity bat;
  private BalloonEntity giant;

  public Balloon(Location location, SkyWarsBalloon balloon) {
    this.leash = NMS.createBalloonLeash(location);
    
    boolean higherThan183 = MinecraftVersion.getCurrentVersion().getCompareId() > 183;
    Location batLocation = location.clone();
    batLocation.setX(batLocation.getX() - (higherThan183 ? 4.5 : 4.0));
    batLocation.setY(batLocation.getY() + (higherThan183 ? 16.0 : 18.0));
    batLocation.setZ(batLocation.getZ() + (higherThan183 ? 7.5 : 5.5));
    this.bat = NMS.createBalloonBat(batLocation, this.leash);
    
    Location giantLocation = location.clone();
    giantLocation.setX(giantLocation.getX() - 2.0);
    giantLocation.setY(giantLocation.getY() + 9.0);
    giantLocation.setZ(giantLocation.getZ() + 3.0);
    
    this.giant = NMS.createBalloonGiant(giantLocation, balloon.getTextures());
  }

  public void despawn() {
    if (this.leash != null) {
      this.leash.kill();
      this.leash = null;
    }
    if (this.bat != null) {
      this.bat.kill();
      this.bat = null;
    }
    if (this.giant != null) {
      this.giant.kill();
      this.giant = null;
    }
  }
}
