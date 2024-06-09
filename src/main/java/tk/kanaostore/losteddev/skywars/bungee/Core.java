package tk.kanaostore.losteddev.skywars.bungee;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;

public class Core {

  public static CoreMode MODE = CoreMode.MULTI_ARENA;

  public static LostLogger getCoreLogger() {
    try {
      Class.forName("net.md_5.bungee.api.plugin.Plugin");
      return Bungee.LOGGER;
    } catch (Exception ex) {
      return Main.LOGGER;
    }
  }

  private static CoreSender SENDER;
  public static final String[] menusArray = new String[] {"profile", "leveling", "statistics", "settings", "shop", "cells", "play", "playduels", "playranked", "kitsandperks",
      "nkits", "ikits", "rkits", "viewkit", "confirmbuy", "kitselector", "mapselector", "well", "wellsettings", "wellharvest", "wellupgrades", "wellroll", "teleporter",
      "mysteryvault", "confirmvault", "nperks", "iperks", "rperks", "deliveryman", "deathcry", "balloon", "statsnpc", "symbol"};
  public static final List<String> filesSaved = Arrays.asList("profile", "leveling", "statistics", "settings", "shop", "cells", "play", "playduels", "playranked", "kitsandperks",
      "nkits", "ikits", "rkits", "viewkit", "confirmbuy", "kitselector", "mapselector", "well", "wellsettings", "wellharvest", "wellupgrades", "wellroll", "teleporter",
      "mysteryvault", "confirmvault", "nperks", "iperks", "rperks", "deliveryman", "deathcry", "balloon", "statsnpc", "symbol", "normalkits", "insanekits", "rankedkits",
      "balloons", "cages", "chesttypes", "deathcries", "deliveries", "levels", "perks", "ranked", "ranks", "symbols", "lang");

  public static CoreSender getCoreSender() {
    if (SENDER == null) {
      try {
        Object proxyServer = Class.forName("net.md_5.bungee.api.ProxyServer").getDeclaredMethod("getInstance").invoke(null);
        Object commandSender = proxyServer.getClass().getDeclaredMethod("getConsole").invoke(proxyServer);
        Method sendMessage = commandSender.getClass().getDeclaredMethod("sendMessage", Array.newInstance(Class.forName("net.md_5.bungee.api.chat.BaseComponent"), 0).getClass());
        Method fromTextLegacy = Class.forName("net.md_5.bungee.api.chat.TextComponent").getDeclaredMethod("fromLegacyText", String.class);
        SENDER = new CoreSender(commandSender) {
          @Override
          public void sendMessage(String message) {
            try {
              sendMessage.invoke(commandSender, fromTextLegacy.invoke(null, message));
            } catch (Exception ex) {
              ex.printStackTrace();
            }
          }
        };
      } catch (Exception ex) {
        try {
          Object commandSender = Class.forName("org.bukkit.Bukkit").getDeclaredMethod("getConsoleSender").invoke(null);
          Method sendMessage = commandSender.getClass().getDeclaredMethod("sendMessage", String.class);
          SENDER = new CoreSender(commandSender) {
            @Override
            public void sendMessage(String message) {
              try {
                sendMessage.invoke(commandSender, message);
              } catch (Exception ex) {
                ex.printStackTrace();
              }
            }
          };
        } catch (Exception ex2) {
        }
      }
    }

    return SENDER;
  }

  private static CoreDatabase DATABASE;

  public static CoreDatabase getCoreDatabase() {
    if (DATABASE == null) {
      try {
        Class.forName("net.md_5.bungee.api.plugin.Plugin");
        Object bungeeConfig = Class.forName("tk.kanaostore.losteddev.skywars.bungee.utils.BungeeConfig").getDeclaredMethod("getConfig", String.class).invoke(null, "config");
        Method get = bungeeConfig.getClass().getDeclaredMethod("get", String.class);
        DATABASE = new CoreDatabase() {

          @Override
          public String getName() {
            try {
              return get.invoke(bungeeConfig, "database.name").toString();
            } catch (Exception ex) {
              return null;
            }
          }

          @Override
          public String getUser() {
            try {
              return get.invoke(bungeeConfig, "database.username").toString();
            } catch (Exception ex) {
              return null;
            }
          }

          @Override
          public String getPort() {
            try {
              return get.invoke(bungeeConfig, "database.port").toString();
            } catch (Exception ex) {
              return null;
            }
          }

          @Override
          public String getPassword() {
            try {
              return get.invoke(bungeeConfig, "database.password").toString();
            } catch (Exception ex) {
              return null;
            }
          }

          @Override
          public String getHost() {
            try {
              return get.invoke(bungeeConfig, "database.host").toString();
            } catch (Exception ex) {
              return null;
            }
          }

          @Override
          public String getDbname() {
            try {
              return get.invoke(bungeeConfig, "database.dbname").toString();
            } catch (Exception ex) {
              return null;
            }
          }
        };
      } catch (Exception ex) {
        try {
          Object main = Class.forName("tk.kanaostore.losteddev.skywars.Main").getDeclaredMethod("getInstance").invoke(null);
          Object fileConfiguration = Class.forName("org.bukkit.plugin.java.JavaPlugin").getDeclaredMethod("getConfig").invoke(main);
          Method get = Class.forName("org.bukkit.configuration.ConfigurationSection").getDeclaredMethod("get", String.class);
          DATABASE = new CoreDatabase() {

            @Override
            public String getName() {
              try {
                return get.invoke(fileConfiguration, "database.name").toString();
              } catch (Exception ex) {
                return null;
              }
            }

            @Override
            public String getUser() {
              try {
                return get.invoke(fileConfiguration, "database.username").toString();
              } catch (Exception ex) {
                return null;
              }
            }

            @Override
            public String getPort() {
              try {
                return get.invoke(fileConfiguration, "database.port").toString();
              } catch (Exception ex) {
                return null;
              }
            }

            @Override
            public String getPassword() {
              try {
                return get.invoke(fileConfiguration, "database.password").toString();
              } catch (Exception ex) {
                return null;
              }
            }

            @Override
            public String getHost() {
              try {
                return get.invoke(fileConfiguration, "database.host").toString();
              } catch (Exception ex) {
                return null;
              }
            }

            @Override
            public String getDbname() {
              try {
                return get.invoke(fileConfiguration, "database.dbname").toString();
              } catch (Exception ex) {
                return null;
              }
            }
          };
        } catch (Exception ex2) {
        }
      }
    }

    return DATABASE;
  }
}
