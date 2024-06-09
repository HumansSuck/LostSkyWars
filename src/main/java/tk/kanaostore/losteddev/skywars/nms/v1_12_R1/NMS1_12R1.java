package tk.kanaostore.losteddev.skywars.nms.v1_12_R1;

import java.lang.reflect.Field;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import net.minecraft.server.v1_12_R1.EntityTypes;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.Blocks;
import net.minecraft.server.v1_12_R1.ChatMessageType;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.MinecraftKey;
import net.minecraft.server.v1_12_R1.PacketPlayOutBlockAction;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle.EnumTitleAction;
import tk.kanaostore.losteddev.skywars.holograms.Hologram;
import tk.kanaostore.losteddev.skywars.holograms.HologramLine;
import tk.kanaostore.losteddev.skywars.holograms.entity.IArmorStand;
import tk.kanaostore.losteddev.skywars.nms.BalloonEntity;
import tk.kanaostore.losteddev.skywars.nms.NMS;
import tk.kanaostore.losteddev.skywars.nms.NMSBridge;
import tk.kanaostore.losteddev.skywars.nms.v1_12_R1.entity.BalloonEntityBat;
import tk.kanaostore.losteddev.skywars.nms.v1_12_R1.entity.BalloonEntityGiant;
import tk.kanaostore.losteddev.skywars.nms.v1_12_R1.entity.BalloonEntityLeash;
import tk.kanaostore.losteddev.skywars.nms.v1_12_R1.entity.EntityArmorStand;
import tk.kanaostore.losteddev.skywars.nms.v1_12_R1.entity.EntityArmorStand.CraftArmorStand;
import tk.kanaostore.losteddev.skywars.nms.v1_12_R1.entity.EntityStand;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;

public class NMS1_12R1 extends NMSBridge {

  public NMS1_12R1() {
    this.registerEntity(65, "LSW-Bat", BalloonEntityBat.class);
    this.registerEntity(8, "LSW-LeashKnot", BalloonEntityLeash.class);
    this.registerEntity(53, "LSW-Giant", BalloonEntityGiant.class);
  }
  
  private void registerEntity(int entityId, String entityName, Class<? extends net.minecraft.server.v1_12_R1.Entity> entityClass) {
    MinecraftKey key = new MinecraftKey(entityName);
    EntityTypes.b.a(entityId, key, entityClass);
    if (!EntityTypes.d.contains(key)) {
      EntityTypes.d.add(key);
    }
  }

  @Override
  public IArmorStand createArmorStand(Location location, String name, HologramLine line) {
    IArmorStand armor = line == null ? new EntityStand(location) : new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle(), line);
    net.minecraft.server.v1_12_R1.Entity entity = (net.minecraft.server.v1_12_R1.Entity) armor;
    armor.setLocation(location.getX(), location.getY(), location.getZ());
    entity.yaw = location.getYaw();
    entity.pitch = location.getPitch();
    armor.setName(name);

    if (addEntity(entity)) {
      return armor;
    }

    return null;
  }
  
  @Override
  public BalloonEntity createBalloonLeash(Location location) {
    BalloonEntityLeash entity = new BalloonEntityLeash(location);
    if (addEntity(entity)) {
      return entity;
    }
    
    return null;
  }
  
  @Override
  public BalloonEntity createBalloonBat(Location location, BalloonEntity leash) {
    BalloonEntityBat entity = new BalloonEntityBat(location, (BalloonEntityLeash) leash);
    if (addEntity(entity)) {
      return entity;
    }
    
    return null;
  }
  
  @Override
  public BalloonEntity createBalloonGiant(Location location, List<String> frames) {
    BalloonEntityGiant entity = new BalloonEntityGiant(location, frames);
    if (addEntity(entity)) {
      return entity;
    }
    
    return null;
  }

  @Override
  public Hologram getHologram(Entity entity) {
    if (entity == null) {
      return null;
    }

    if (!(entity instanceof CraftArmorStand)) {
      return null;
    }

    net.minecraft.server.v1_12_R1.Entity en = ((CraftEntity) entity).getHandle();
    if (!(en instanceof EntityArmorStand)) {
      return null;
    }

    HologramLine e = ((EntityArmorStand) en).getLine();
    return e != null ? e.getHologram() : null;
  }

  @Override
  public boolean isHologramEntity(Entity entity) {
    return this.getHologram(entity) != null;
  }

  private boolean addEntity(net.minecraft.server.v1_12_R1.Entity entity) {
    try {
      return entity.world.addEntity(entity, SpawnReason.CUSTOM);
    } catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }

  @Override
  public void playChestAction(Location location, boolean open) {
    BlockPosition pos = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    PacketPlayOutBlockAction packet = new PacketPlayOutBlockAction(pos, Blocks.ENDER_CHEST, 1, open ? 1 : 0);
    for (Player players : Bukkit.getOnlinePlayers()) {
      ((CraftPlayer) players).getHandle().playerConnection.sendPacket(packet);
    }
  }

  @Override
  public void sendActionBar(Player player, String message) {
    PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a("{\"text\": \"" + message + "\"}"), ChatMessageType.GAME_INFO);
    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
  }

  @Override
  public void sendTitle(Player player, String title, String subtitle) {
    this.sendTitle(player, title, subtitle, 20, 60, 20);
  }

  @Override
  public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
    EntityPlayer ep = ((CraftPlayer) player).getHandle();
    ep.playerConnection.sendPacket(new PacketPlayOutTitle(fadeIn, stay, fadeOut));
    ep.playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\": \"" + title + "\"}")));
    ep.playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, ChatSerializer.a("{\"text\": \"" + subtitle + "\"}")));
  }

  @Override
  public void sendTabHeaderFooter(Player player, String header, String footer) {
    EntityPlayer ep = ((CraftPlayer) player).getHandle();

    PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
    try {
      Field a = packet.getClass().getDeclaredField("a");
      a.setAccessible(true);
      a.set(packet, ChatSerializer.a("{\"text\": \"" + header + "\"}"));
      Field b = packet.getClass().getDeclaredField("b");
      b.setAccessible(true);
      b.set(packet, ChatSerializer.a("{\"text\": \"" + footer + "\"}"));
    } catch (ReflectiveOperationException e) {
      NMS.LOGGER.log(LostLevel.WARNING, "Unexpected error on sendTabHeaderFooter(player, header, footer): ", e);
    }

    ep.playerConnection.sendPacket(packet);
  }
}
