package tk.kanaostore.losteddev.skywars.cmd;

import tk.kanaostore.losteddev.skywars.bungee.Core;
import tk.kanaostore.losteddev.skywars.bungee.CoreMode;

public class Commands {
  
  public static void setupCommands() {
    new StatsCommand();
    if (Core.MODE == CoreMode.MULTI_ARENA) {
      new WatchCommand();
      new JoinCommand();
    }
    new SkyWarsCommand();
  }
}
