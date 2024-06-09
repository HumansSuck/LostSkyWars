package tk.kanaostore.losteddev.skywars.utils;

import java.util.Calendar;

public class TimeUtils {

  public static boolean isNewYear() {
    Calendar cl = Calendar.getInstance();
    return (cl.get(2) == 11 && cl.get(5) == 31) || (cl.get(2) == 0 && cl.get(5) == 1);
  }

  public static boolean isChristmas() {
    Calendar cl = Calendar.getInstance();
    return cl.get(2) == 11 && (cl.get(5) == 24 || cl.get(5) == 25);
  }

  public static int getLastDayOfMonth(int month) {
    Calendar cl = Calendar.getInstance();
    cl.set(2, month - 1);
    return cl.getActualMaximum(5);
  }

  public static int getLastDayOfMonth() {
    return Calendar.getInstance().getActualMaximum(5);
  }

  public static long getExpireIn(int days) {
    Calendar cooldown = Calendar.getInstance();
    cooldown.set(Calendar.HOUR, 24);
    for (int day = 0; day < days - 1; day++) {
      cooldown.add(Calendar.HOUR, 24);
    }
    cooldown.set(Calendar.MINUTE, 0);
    cooldown.set(Calendar.SECOND, 0);

    return cooldown.getTimeInMillis();
  }

  public static String getTimeUntil(long epoch) {
    epoch -= System.currentTimeMillis();
    long ms = epoch / 1000;
    if (ms <= 0) {
      return "Expired";
    }

    String result = "";
    long days = ms / 86400;
    if (days > 0) {
      result += days + "d ";
      ms -= days * 86400;
    }
    long hours = ms / 3600;
    if (hours > 0) {
      result += hours + "h ";
      ms -= hours * 3600;
    }
    long minutes = ms / 60;
    if (minutes > 0) {
      result += minutes + "m ";
      ms -= minutes * 60;
    }
    if (ms > 0) {
      result += ms + "s ";
      ms -= ms;
    }

    return result.substring(0, result.length() - 1);
  }
}
