package tk.kanaostore.losteddev.skywars.bungee;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.config.Configuration;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.bungee.utils.BungeeConfig;
import tk.kanaostore.losteddev.skywars.database.MySQLDatabase;
import tk.kanaostore.losteddev.skywars.utils.LanguageWriter;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

@SuppressWarnings("rawtypes")
public class BungeeFiles {

  public static final LostLogger LOGGER = Bungee.LOGGER.getModule("Files");
  private static final BungeeConfig CONFIG = BungeeConfig.getConfig("lang");

  public static void setupFiles() {
    boolean save = false;
    LanguageWriter writer = new LanguageWriter(CONFIG.getFile());
    for (Field field : Language.class.getDeclaredFields()) {
      if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers()) && !field.getName().equals("LOGGER")
          && !field.getName().equals("CONFIG")) {
        String nativeName = field.getName().replace("$", ".").replace("_", "-");

        try {
          Object value = null;

          if (CONFIG.contains(nativeName)) {
            value = CONFIG.get(nativeName);
            if (value instanceof String) {
              value = StringUtils.formatColors((String) value).replace("\\n", "\n");
            } else if (value instanceof List) {
              List l = (List) value;
              List<Object> list = new ArrayList<>(l.size());
              for (Object v : l) {
                if (v instanceof String) {
                  list.add(StringUtils.formatColors((String) v).replace("\\n", "\n"));
                } else {
                  list.add(v);
                }
              }

              l = null;
              value = list;
            }

            field.set(null, value);
            writer.set(nativeName, CONFIG.get(nativeName));
          } else {
            value = field.get(null);
            if (value instanceof String) {
              value = StringUtils.deformatColors((String) value).replace("\n", "\\n");
            } else if (value instanceof List) {
              List l = (List) value;
              List<Object> list = new ArrayList<>(l.size());
              for (Object v : l) {
                if (v instanceof String) {
                  list.add(StringUtils.deformatColors((String) v).replace("\n", "\\n"));
                } else {
                  list.add(v);
                }
              }

              l = null;
              value = list;
            }

            save = true;
            writer.set(nativeName, value);
          }
        } catch (ReflectiveOperationException e) {
          LOGGER.log(LostLevel.WARNING, "Unexpected error on language file: ", e);
        }
      }
    }

    if (save) {
      writer.write();
      LOGGER.info("Lang.yml modified or created.");
      CONFIG.reload();
    }

    // Kits
    BungeeConfig.getConfig("normalkits", "plugins/LostSkyWars/kits");
    BungeeConfig.getConfig("insanekits", "plugins/LostSkyWars/kits");
    BungeeConfig.getConfig("rankedkits", "plugins/LostSkyWars/kits");
    // Menus
    for (String menu : Core.menusArray) {
      BungeeConfig.getConfig(menu, "plugins/LostSkyWars/menus");
    }
    // Others
    BungeeConfig.getConfig("balloons");
    BungeeConfig.getConfig("cages");
    BungeeConfig.getConfig("chesttypes");
    BungeeConfig.getConfig("deathcries");
    BungeeConfig.getConfig("deliveries");
    BungeeConfig.getConfig("levels");
    BungeeConfig.getConfig("perks");
    BungeeConfig.getConfig("ranked");
    BungeeConfig.getConfig("ranks");
    BungeeConfig.getConfig("symbols");

    saveFiles();
  }

  public static void saveFiles() {
    BungeeConfig config = BungeeConfig.getConfig("config");
    String type = config.getString("database.name");
    if (!type.equalsIgnoreCase("MYSQL")) {
      LOGGER.log(LostLevel.WARNING, "ENABLE MYSQL TO USE LOSTSKYWARS BUNGEE MODE!");
      System.exit(0);
    }

    MySQLDatabase database = new MySQLDatabase();
    database.update(
        "CREATE TABLE IF NOT EXISTS `lostskywars_files` (`name` VARCHAR(32) NOT NULL, `file` TEXT, PRIMARY KEY(name)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_bin;");
    LOGGER.log(LostLevel.INFO, "Uploading files to MySQL...");
    for (BungeeConfig toSave : BungeeConfig.listConfigs()) {
      toSave.reload();
      String name = toSave.getFile().getName().replace(".yml", "");
      if (name.equals("config")) {
        continue;
      }
      
      String file = parseConfiguration(toSave.getRawConfig());
      boolean exists = database.query("SELECT * FROM `lostskywars_files` WHERE `name` = ?", name) != null;
      if (exists) {
        database.execute("UPDATE `lostskywars_files` SET `file` = ? WHERE `name` = ?", file, name);
      } else {
        database.execute("INSERT INTO `lostskywars_files` VALUES (?, ?)", name, file);
      }
    }
  }

  private static String parseConfiguration(Configuration config) {
    StringBuilder sb = new StringBuilder();
    for (String key : config.getKeys()) {
      sb.append(parseSection(key, config.get(key), 0));
    }

    return sb.toString();
  }

  private static String parseSection(String key, Object object, int spaces) {
    StringBuilder join = new StringBuilder(repeat(spaces) + key + ":");
    if (object instanceof String) {
      join.append(" '" + object.toString().replace("'", "''") + "'\n");
    } else if (object instanceof Integer) {
      join.append(" " + object + "\n");
    } else if (object instanceof Double) {
      join.append(" " + object + "\n");
    } else if (object instanceof Long) {
      join.append(" " + object + "\n");
    } else if (object instanceof Boolean) {
      join.append(" " + object + "\n");
    } else if (object instanceof List) {
      join.append("\n");
      for (Object obj : (List<?>) object) {
        if (obj instanceof Integer) {
          join.append(repeat(spaces) + "- " + obj.toString() + "\n");
        } else {
          join.append(repeat(spaces) + "- '" + obj.toString().replace("'", "''") + "'\n");
        }
      }
    } else if (object instanceof Configuration) {
      if (((Configuration) object).getKeys().isEmpty()) {
        join.append(" {}\n");
      } else {
        join.append("\n");
        for (String k : ((Configuration) object).getKeys()) {
          join.append(parseSection(k, ((Configuration) object).get(k), spaces + 1));
        }
      }
    }

    return join.toString();
  }

  private static String repeat(int spaces) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < spaces; i++) {
      sb.append(" ");
    }

    return sb.toString();
  }
}
