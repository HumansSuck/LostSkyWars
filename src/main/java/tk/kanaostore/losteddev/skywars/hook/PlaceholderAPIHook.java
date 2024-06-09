package tk.kanaostore.losteddev.skywars.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.api.LostSkyWarsExpansion;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;

public class PlaceholderAPIHook {

  public static final LostLogger LOGGER = Main.LOGGER.getModule("PlaceholderAPIHook");

  public static void setupPlaceHolderAPI() {
    PlaceholderAPI.registerExpansion(new LostSkyWarsExpansion());
    LOGGER.log(LostLevel.INFO, "PlaceholderAPI found, hooking...");
  }
}
