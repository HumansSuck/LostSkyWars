package tk.kanaostore.losteddev.skywars.bungee.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.ImmutableList;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;
import tk.kanaostore.losteddev.skywars.bungee.Bungee;
import tk.kanaostore.losteddev.skywars.utils.FileUtils;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;

public class BungeeConfig {

  private File file;
  private Configuration config;

  private BungeeConfig(String path, String name) {
    this.file = new File(path + "/" + name + ".yml");
    if (!file.exists()) {
      file.getParentFile().mkdirs();
      InputStream in = Bungee.getInstance().getResourceAsStream(name + ".yml");
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
      this.config = YamlConfiguration.getProvider(YamlConfiguration.class).load(new InputStreamReader(new FileInputStream(file), "UTF-8"));
    } catch (IOException e) {
      LOGGER.log(LostLevel.SEVERE, "Unexpected error ocurred creating config " + file.getName() + ": ", e);
    }
  }

  public boolean createSection(String path) {
    this.config.set(path, new HashMap<>());
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

  public Collection<String> getKeys(boolean flag) {
    return this.config.getKeys();
  }

  public Configuration getSection(String path) {
    return this.config.getSection(path);
  }

  public void reload() {
    try {
      this.config = YamlConfiguration.getProvider(YamlConfiguration.class).load(new InputStreamReader(new FileInputStream(file), "UTF-8"));
    } catch (IOException e) {
      LOGGER.log(LostLevel.SEVERE, "Unexpected error ocurred creating config " + file.getName() + ": ", e);
    }
  }

  public boolean save() {
    try {
      YamlConfiguration.getProvider(YamlConfiguration.class).save(this.config, this.file);
      return true;
    } catch (IOException e) {
      LOGGER.log(LostLevel.SEVERE, "Unexpected error ocurred saving file " + file.getName() + ": ", e);
      return false;
    }
  }

  public File getFile() {
    return file;
  }

  public Configuration getRawConfig() {
    return config;
  }

  public static final LostLogger LOGGER = Bungee.LOGGER.getModule("BungeeConfig");
  private static Map<String, BungeeConfig> cache = new HashMap<>();

  public static BungeeConfig getConfig(String name) {
    return getConfig(name, "plugins/LostSkyWars");
  }

  public static BungeeConfig getConfig(String name, String path) {
    if (!cache.containsKey(path + "/" + name)) {
      cache.put(path + "/" + name, new BungeeConfig(path, name));
    }

    return cache.get(path + "/" + name);
  }

  public static void removeConfig(String name) {
    cache.remove("plugins/LostSkyWars/" + name);
  }

  public static void removeConfig(BungeeConfig config) {
    for (Map.Entry<String, BungeeConfig> cu : ImmutableList.copyOf(cache.entrySet())) {
      if (cu.getValue().equals(config)) {
        cache.remove(cu.getKey());
        return;
      }
    }
  }
  
  public static Collection<BungeeConfig> listConfigs() {
    return cache.values();
  }
}
