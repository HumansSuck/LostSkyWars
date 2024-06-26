package tk.kanaostore.losteddev.skywars.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import com.google.common.collect.ImmutableList;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.bungee.Core;
import tk.kanaostore.losteddev.skywars.bungee.CoreMode;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;

public class ConfigUtils {

  private File file;
  private YamlConfiguration config;

  private ConfigUtils(String path, String name) {
    if (Core.MODE != CoreMode.MULTI_ARENA) {
      if (Core.filesSaved.contains(name)) {
        try {
          String config = Database.getInstance().query("SELECT * FROM `lostskywars_files` WHERE `name` = ?", name).getString("file");
          this.config = YamlConfiguration.loadConfiguration(new InputStreamReader(new ByteArrayInputStream(config.getBytes("UTF-8")), "UTF-8"));
        } catch (Exception e) {
          LOGGER.log(LostLevel.SEVERE, "Unexpected error ocurred loading config " + name + ": ", e);
        }
        
        return;
      }
    }

    this.file = new File(path + "/" + name + ".yml");
    if (!file.exists()) {
      file.getParentFile().mkdirs();
      InputStream in = Main.getInstance().getResource(name + ".yml");
      if (in != null) {
        FileUtils.copyFile(in, file);
      } else {
        try {
          file.createNewFile();
        } catch (IOException e) {
          LOGGER.log(LostLevel.SEVERE, "Unexpected error ocurred creating file " + file.getName() + ": ", e);
        }
      }
    }

    try {
      this.config = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(file), "UTF-8"));
    } catch (IOException e) {
      LOGGER.log(LostLevel.SEVERE, "Unexpected error ocurred creating config " + file.getName() + ": ", e);
    }
  }

  public boolean createSection(String path) {
    this.config.createSection(path);
    return save();
  }

  public boolean set(String path, Object obj) {
    this.config.set(path, obj);
    return save();
  }

  public boolean contains(String path) {
    return this.config.contains(path);
  }

  public Object get(String path) {
    return this.config.get(path);
  }

  public int getInt(String path) {
    return this.config.getInt(path);
  }

  public int getInt(String path, int def) {
    return this.config.getInt(path, def);
  }

  public double getDouble(String path) {
    return this.config.getDouble(path);
  }

  public double getDouble(String path, double def) {
    return this.config.getDouble(path, def);
  }

  public String getString(String path) {
    return this.config.getString(path);
  }

  public boolean getBoolean(String path) {
    return this.config.getBoolean(path);
  }

  public List<String> getStringList(String path) {
    return this.config.getStringList(path);
  }

  public Set<String> getKeys(boolean flag) {
    return this.config.getKeys(flag);
  }

  public ConfigurationSection getSection(String path) {
    return this.config.getConfigurationSection(path);
  }

  public void reload() {
    try {
      this.config = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(file), "UTF-8"));
    } catch (IOException e) {
      LOGGER.log(LostLevel.SEVERE, "Unexpected error ocurred creating config " + file.getName() + ": ", e);
    }
  }

  public boolean save() {
    try {
      config.save(file);
      return true;
    } catch (IOException e) {
      LOGGER.log(LostLevel.SEVERE, "Unexpected error ocurred saving file " + file.getName() + ": ", e);
      return false;
    }
  }

  public File getFile() {
    return file;
  }

  public YamlConfiguration getRawConfig() {
    return config;
  }

  public static final LostLogger LOGGER = Main.LOGGER.getModule("ConfigUtils");
  private static Map<String, ConfigUtils> cache = new HashMap<>();

  public static ConfigUtils getConfig(String name) {
    return getConfig(name, "plugins/LostSkyWars");
  }

  public static ConfigUtils getConfig(String name, String path) {
    if (!cache.containsKey(path + "/" + name)) {
      cache.put(path + "/" + name, new ConfigUtils(path, name));
    }

    return cache.get(path + "/" + name);
  }

  public static void removeConfig(String name) {
    cache.remove("plugins/LostSkyWars/" + name);
  }

  public static void removeConfig(ConfigUtils config) {
    for (Map.Entry<String, ConfigUtils> cu : ImmutableList.copyOf(cache.entrySet())) {
      if (cu.getValue().equals(config)) {
        cache.remove(cu.getKey());
        return;
      }
    }
  }
}
