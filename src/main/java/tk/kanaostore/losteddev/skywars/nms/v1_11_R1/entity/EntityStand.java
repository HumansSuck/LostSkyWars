package tk.kanaostore.losteddev.skywars.nms.v1_11_R1.entity;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.EulerAngle;
import net.minecraft.server.v1_11_R1.DamageSource;
import net.minecraft.server.v1_11_R1.EntityArmorStand;
import net.minecraft.server.v1_11_R1.EntityHuman;
import net.minecraft.server.v1_11_R1.EntityPlayer;
import net.minecraft.server.v1_11_R1.EnumHand;
import net.minecraft.server.v1_11_R1.EnumInteractionResult;
import net.minecraft.server.v1_11_R1.ItemStack;
import net.minecraft.server.v1_11_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_11_R1.Vec3D;
import net.minecraft.server.v1_11_R1.Vector3f;
import tk.kanaostore.losteddev.skywars.holograms.HologramLine;
import tk.kanaostore.losteddev.skywars.holograms.entity.IArmorStand;

public class EntityStand extends EntityArmorStand implements IArmorStand {

  public EntityStand(Location toSpawn) {
    super(((CraftWorld) toSpawn.getWorld()).getHandle());
    setArms(false);
    setBasePlate(true);
    setInvisible(true);
    setNoGravity(true);
    setSmall(true);
    a(new NullBoundingBox());
  }

  public boolean isInvulnerable(DamageSource source) {
    return true;
  }
  
  @Override
  public EnumInteractionResult a(EntityHuman human, Vec3D vec3d, EnumHand enumhand) {
      return EnumInteractionResult.PASS;
  }

  @Override
  public boolean c(int i, ItemStack item) {
      return false;
  }

  public void setCustomName(String customName) {}

  public void setCustomNameVisible(boolean visible) {}

  public void A_() {
    this.ticksLived = 0;
    super.A_();
  }

  public void makeSound(String sound, float f1, float f2) {}

  @Override
  public int getId() {
    return super.getId();
  }
  
  @Override
  public void setName(String text) {
    if (text != null && text.length() > 300) {
      text = text.substring(0, 300);
    }

    super.setCustomName(text == null ? "" : text);
    super.setCustomNameVisible(text != null && !text.isEmpty());
  }

  @Override
  public void killEntity() {
    super.die();
  }

  @Override
  public HologramLine getLine() {
    return null;
  }

  @Override
  public ArmorStand getEntity() {
    return (ArmorStand) getBukkitEntity();
  }

  @Override
  public CraftEntity getBukkitEntity() {
    if (bukkitEntity == null) {
      bukkitEntity = new CraftStand(this);
    }

    return super.getBukkitEntity();
  }

  @Override
  public void setLocation(double x, double y, double z) {
    super.setPosition(x, y, z);

    PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(this);

    for (EntityHuman obj : world.players) {
      if (obj instanceof EntityPlayer) {
        EntityPlayer nmsPlayer = (EntityPlayer) obj;

        double distanceSquared = square(nmsPlayer.locX - this.locX) + square(nmsPlayer.locZ - this.locZ);
        if (distanceSquared < 8192.0 && nmsPlayer.playerConnection != null) {
          nmsPlayer.playerConnection.sendPacket(teleportPacket);
        }
      }
    }
  }

  private static double square(double num) {
    return num * num;
  }

  @Override
  public boolean isDead() {
    return dead;
  }

  static class CraftStand extends CraftArmorStand implements IArmorStand {

    public CraftStand(EntityStand entity) {
      super(entity.world.getServer(), entity);
    }
    
    @Override
    public int getId() {
      return ((EntityStand) entity).getId();
    }
    
    @Override
    public void setHeadPose(EulerAngle pose) {
      ((EntityStand) entity).setHeadPose(new Vector3f((float)pose.getX(), (float)pose.getY(), (float)pose.getZ()));
    }
    
    @Override
    public void setLeftArmPose(EulerAngle pose) {
      ((EntityStand) entity).setLeftArmPose(new Vector3f((float)pose.getX(), (float)pose.getY(), (float)pose.getZ()));
    }
    
    @Override
    public void setLeftLegPose(EulerAngle pose) {
      ((EntityStand) entity).setLeftLegPose(new Vector3f((float)pose.getX(), (float)pose.getY(), (float)pose.getZ()));
    }
    
    @Override
    public void setRightLegPose(EulerAngle pose) {
      ((EntityStand) entity).setRightLegPose(new Vector3f((float)pose.getX(), (float)pose.getY(), (float)pose.getZ()));
    }

    @Override
    public void setName(String text) {
      ((EntityStand) entity).setName(text);
    }

    @Override
    public void killEntity() {
      ((EntityStand) entity).killEntity();
    }

    @Override
    public HologramLine getLine() {
      return ((EntityStand) entity).getLine();
    }

    @Override
    public ArmorStand getEntity() {
      return (ArmorStand) this;
    }

    @Override
    public void setLocation(double x, double y, double z) {
      ((EntityStand) entity).setLocation(x, y, z);
    }
  }
}
