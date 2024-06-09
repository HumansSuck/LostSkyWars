package tk.kanaostore.losteddev.skywars.hook.protocollib;

import java.util.Collection;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.holograms.Holograms;
import tk.kanaostore.losteddev.skywars.holograms.entity.IArmorStand;
import tk.kanaostore.losteddev.skywars.hook.citizens.StatsNPC;
import tk.kanaostore.losteddev.skywars.level.Level;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.ranked.Ranked;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

public class ProtocolListener extends PacketAdapter {

  public ProtocolListener() {
    super(params(Main.getInstance(), PacketType.Play.Server.ENTITY_METADATA, PacketType.Play.Server.PLAYER_INFO));
  }

  @Override
  public void onPacketReceiving(PacketEvent evt) {}

  @Override
  public void onPacketSending(PacketEvent evt) {
    if (!evt.isPlayerTemporary()) {
      PacketContainer packet = evt.getPacket();

      Player player = evt.getPlayer();
      Account account = Database.getInstance().getAccount(player.getUniqueId());

      if (packet.getType() == PacketType.Play.Server.ENTITY_METADATA) {
        packet = evt.getPacket().deepClone();
        int entityId = packet.getIntegers().read(0);
        Entity entity = Holograms.getHologramEntity(entityId);
        if (entity == null || !(entity instanceof IArmorStand)) {
          return;
        }

        IArmorStand stand = (IArmorStand) entity;
        if (stand.getLine() == null || stand.getLine().getHologram() == null) {
          return;
        }

        List<WrappedWatchableObject> list = packet.getWatchableCollectionModifier().read(0);
        WrappedWatchableObject customName = this.getWatchableObjectFromList(list, 2);
        if (customName == null) {
          return;
        }
        
        if (account == null) {
          return;
        }

        String toReplace = customName.getValue().toString();
        Level level = Level.getByLevel(account.getLevel());
        toReplace = toReplace.replace("{level}", level.getLevel(account));
        toReplace = toReplace.replace("{exp}", StringUtils.formatPerMil(account.getExp()));
        toReplace = toReplace.replace("{nextExp}", level.getNext() != null ? StringUtils.formatPerMil(level.getNext().getExp()) : "Max");
        toReplace = toReplace.replace("{wins}", StringUtils.formatNumber(account.getIntegers("solowins", "teamwins") + Ranked.getInt(account, "wins")));
        toReplace = toReplace.replace("{kills}", StringUtils.formatNumber(account.getIntegers("solokills", "teamkills") + Ranked.getInt(account, "kills")));
        customName.setValue(toReplace);
        evt.setPacket(packet);
      } else {
        for (PlayerInfoData data : packet.getPlayerInfoDataLists().read(0)) {
          for (StatsNPC statsNPC : StatsNPC.listNPCs()) {
            if (statsNPC.getNPC().getEntity() != null) {
              if (statsNPC.getNPC().getEntity().getUniqueId().equals(data.getProfile().getUUID()) && statsNPC.getNPC().getEntity().getName().equals(data.getProfile().getName())) {
                Collection<WrappedSignedProperty> propertyCollection = WrappedGameProfile.fromPlayer(player).getProperties().get("textures");
                if (propertyCollection != null) {
                  WrappedSignedProperty propertySigned = propertyCollection.stream().findFirst().orElse(null);
                  if (propertySigned != null) {
                    data.getProfile().getProperties().clear();
                    data.getProfile().getProperties().put("textures", propertySigned);
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private WrappedWatchableObject getWatchableObjectFromList(List<WrappedWatchableObject> list, int index) {
    for (WrappedWatchableObject object : list) {
      if (object.getIndex() == index) {
        return object;
      }
    }

    return null;
  }
}
