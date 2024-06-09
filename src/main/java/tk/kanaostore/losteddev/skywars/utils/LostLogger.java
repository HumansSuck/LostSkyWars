package tk.kanaostore.losteddev.skywars.utils;

import tk.kanaostore.losteddev.skywars.bungee.Core;
import tk.kanaostore.losteddev.skywars.bungee.CoreSender;

public class LostLogger {

  private String prefix;
  private CoreSender sender;

  public LostLogger() {
    this.prefix = "[Losted:SW] ";
    this.sender = Core.getCoreSender();
  }

  public LostLogger(String prefix) {
    this.prefix = prefix;
    this.sender = Core.getCoreSender();
  }

  public void info(String message) {
    this.log(LostLevel.INFO, message);
  }

  public void warning(String message) {
    this.log(LostLevel.WARNING, message);
  }

  public void severe(String message) {
    this.log(LostLevel.SEVERE, message);
  }

  public void log(LostLevel level, String message) {
    this.log(level, message, null);
  }

  public void log(LostLevel level, String message, Throwable throwable) {
    StringBuilder result = new StringBuilder(this.prefix + message);
    if (throwable != null) {
      result.append("\n" + throwable.getMessage());
      for (StackTraceElement ste : throwable.getStackTrace()) {
        if (ste.toString().contains("tk.kanaostore.losteddev.skywars")) {
          result.append("\n" + ste.toString());
        }
      }
    }

    this.sender.sendMessage(level.format(result.toString()));
  }

  public LostLogger getModule(String module) {
    return new LostLogger(this.prefix + "[" + module + "] ");
  }

  public static enum LostLevel {
      INFO("§a"),
      WARNING("§c"),
      SEVERE("§4");

    private String color;

    LostLevel(String color) {
      this.color = color;
    }

    public String format(String message) {
      return this.color + message;
    }
  }
}
