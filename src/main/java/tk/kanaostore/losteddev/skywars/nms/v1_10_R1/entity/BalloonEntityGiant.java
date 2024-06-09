package tk.kanaostore.losteddev.skywars.nms.v1_10_R1.entity;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import net.minecraft.server.v1_10_R1.DamageSource;
import net.minecraft.server.v1_10_R1.EntityGiantZombie;
import net.minecraft.server.v1_10_R1.EnumItemSlot;
import net.minecraft.server.v1_10_R1.MinecraftServer;
import net.minecraft.server.v1_10_R1.NBTTagCompound;
import tk.kanaostore.losteddev.skywars.nms.BalloonEntity;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;

public class BalloonEntityGiant extends EntityGiantZombie implements BalloonEntity {
  
  private Location location;
  private List<String> frames;
  
  public BalloonEntityGiant(Location location, List<String> frames) {
    super(((CraftWorld) location.getWorld()).getHandle());
    this.frames = frames;
    this.location = location.clone();
    
    super.setInvisible(true);
    this.setPosition(location.getX(), location.getY(), location.getZ());
    this.setEquipment(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(BukkitUtils.deserializeItemStack("SKULL_ITEM:3 : 1 : skinvalue=" + frames.get(0))));
  }
  
  @Override
  public void kill() {
    this.dead = true;
  }
  
  private int frame = 0;
  
  @Override
  public void m() {
    this.motY = 0.0;
    this.setPosition(location.getX(), location.getY(), location.getZ());
    if (this.frames == null) {
      this.kill();
      return;
    }
    
    if (this.frame >= this.frames.size()) {
      this.frame = 0;
    }
    
    super.m();
    if (MinecraftServer.currentTick % 10 == 0) {
      this.setEquipment(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(BukkitUtils.deserializeItemStack("SKULL_ITEM:3 : 1 : skinvalue=" + this.frames.get(this.frame++))));
    }
  }
  
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
