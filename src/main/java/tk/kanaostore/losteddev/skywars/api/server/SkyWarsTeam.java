package tk.kanaostore.losteddev.skywars.api.server;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticServer;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticType;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsBalloon;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsCage;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.ballons.Balloon;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsMode;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.world.WorldServer;

public class SkyWarsTeam {

  private int id;
  private WorldServer<?> server;
  private String alphabetical;
  private String location;
  private Balloon ballon;
  private List<UUID> members;

  public SkyWarsTeam(WorldServer<?> server, int spawns, String serialized) {
    this.id = spawns;
    this.server = server;
    this.location = serialized;
    this.alphabetical = alphabet[spawns];
    this.members = new ArrayList<>(server.getMode().getTeamSize());
  }

  public void destroy() {
    SkyWarsCage.remove(this.getLocation(), !server.getMode().equals(SkyWarsMode.SOLO));
    if (server.getState() == SkyWarsState.INGAME) {
      String ballonSerialized = this.server.getConfig().getBalloon(this.id);

      if (ballonSerialized != null && this.isAlive()) {
        SkyWarsBalloon cosmetic = null;
        Account unique = server.getMode().equals(SkyWarsMode.SOLO) ? Database.getInstance().getAccount(this.members.get(0)) : null;
        if (unique == null) {
          List<Account> accounts = new ArrayList<>();
          for (Player member : this.getMembers()) {
            Account account = Database.getInstance().getAccount(member.getUniqueId());
            if (account != null && account.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_BALLON, 1) != null) {
              accounts.add(account);
            }
          }

          if (accounts.size() > 0) {
            unique = accounts.get(ThreadLocalRandom.current().nextInt(accounts.size()));
          }
        }

        if (unique != null) {
          cosmetic = (SkyWarsBalloon) unique.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_BALLON, 1);
        }

        if (cosmetic != null) {
          this.ballon = new Balloon(BukkitUtils.deserializeLocation(ballonSerialized), cosmetic);
        }
      }
    }
  }

  public void reset() {
    this.members.clear();
    if (this.ballon != null) {
      this.ballon.despawn();
      this.ballon = null;
    }
  }

  public void addMember(Player player) {
    this.members.add(player.getUniqueId());
  }

  public void removeMember(Player player) {
    this.members.remove(player.getUniqueId());
  }

  public boolean isAlive() {
    return !this.members.isEmpty();
  }

  public boolean canJoin() {
    return this.canJoin(1);
  }

  public boolean canJoin(int players) {
    return (this.members.size() + players) <= this.server.getMode().getTeamSize();
  }

  public boolean hasMember(Player player) {
    return this.members.contains(player.getUniqueId());
  }

  public int getSize() {
    return this.server.getMode().getTeamSize();
  }

  public String getAlphabetical() {
    return this.alphabetical;
  }

  public String getAlphabeticalTag() {
    return "[" + this.alphabetical + "]";
  }

  public Location getLocation() {
    return BukkitUtils.deserializeLocation(this.getSerializedLocation());
  }

  public String getSerializedLocation() {
    return this.location;
  }

  public List<Player> getMembers() {
    return members.stream().filter(id -> Bukkit.getPlayer(id) != null).map(id -> Bukkit.getPlayer(id)).collect(Collectors.toList());
  }

  private static final String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "W", "X", "Y", "Z"};
}
