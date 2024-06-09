package tk.kanaostore.losteddev.skywars.api.server;

import java.util.List;
import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsMode;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsType;

public abstract class SkyWarsServer {

  protected String name;
  protected String ip;
  protected SkyWarsState state;

  public SkyWarsServer() {
    this(null);
  }

  public SkyWarsServer(String name) {
    this(name, null);
  }

  public SkyWarsServer(String name, String ip) {
    this.name = name;
    this.ip = ip;
    this.state = SkyWarsState.NONE;
  }
  
  public void destroy() {
    this.name = null;
    this.ip = null;
    this.state = null;
  }

  public abstract void kill(Account account, Account killer);
  
  public abstract void spectate(Account account, Player target);

  public abstract void connect(Account account, String... skipParty);

  public abstract void disconnect(Account account);

  public abstract void disconnect(Account account, String options);

  public void setState(SkyWarsState state) {
    this.state = state;
  }

  public String getName() {
    return name;
  }

  public String getIp() {
    return ip;
  }

  public SkyWarsState getState() {
    return state;
  }

  public abstract boolean isSpectator(Player player);

  public abstract String getServerName();

  public abstract SkyWarsMode getMode();
  
  public abstract SkyWarsType getType();

  public abstract String getEvent();

  public abstract int getTimer();

  public abstract int getOnline();

  public abstract int getAlive();

  public abstract List<SkyWarsTeam> getAliveTeams();

  public abstract int getMaxPlayers();

  public abstract int getKills(Player player);

  public abstract SkyWarsTeam getTeam(Player player);

  @Override
  public String toString() {
    return "SkyWarsServer{name=" + this.getServerName() + ", mapName=" + this.getName() + ", ip=" + this.getIp() + ", state=" + this.getState().name() + ", online="
        + this.getOnline() + ", max-players=" + this.getMaxPlayers() + "}";
  }
}
