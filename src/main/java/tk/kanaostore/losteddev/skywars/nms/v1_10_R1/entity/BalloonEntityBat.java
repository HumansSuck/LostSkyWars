package tk.kanaostore.losteddev.skywars.nms.v1_10_R1.entity;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import net.minecraft.server.v1_10_R1.DamageSource;
import net.minecraft.server.v1_10_R1.EntityBat;
import net.minecraft.server.v1_10_R1.NBTTagCompound;
import tk.kanaostore.losteddev.skywars.nms.BalloonEntity;

public class BalloonEntityBat extends EntityBat implements BalloonEntity {
  
  public BalloonEntityBat(Location location, BalloonEntityLeash leash) {
    super(((CraftWorld) location.getWorld()).getHandle());
    
    super.setInvisible(true);
    this.setLeashHolder(leash, true);
    
    this.setPosition(location.getX(), location.getY(), location.getZ());
  }
  
  @Override
  public void kill() {
    this.dead = true;
  }
  
  @Override
  public void m() {}
  
  @Override
  public boolean isInvulnerable(DamageSource damagesource) {
    return true;
  }

  @Override
  public void setCustomName(String s) {}

  @Override
  public void setCustomNameVisible(boolean flag) {}

  @Override
  public void die() {}

  @Override
  public boolean damageEntity(DamageSource damagesource, float f) {
    return false;
  }

  @Override
  public void setInvisible(boolean flag) {}

  public void a(NBTTagCompound nbttagcompound) {}

  public void b(NBTTagCompound nbttagcompound) {}

  public boolean c(NBTTagCompound nbttagcompound) {
    return false;
  }

  public boolean d(NBTTagCompound nbttagcompound) {
    return false;
  }

  public void f(NBTTagCompound nbttagcompound) {}
}
