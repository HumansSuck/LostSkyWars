package tk.kanaostore.losteddev.skywars.utils;

import static java.lang.Integer.parseInt;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import com.google.common.base.Objects;

public class MinecraftVersion {

  private int major;
  private int minor;
  private int build;
  private int compareId;

  public MinecraftVersion(Server server) {
    this(extractVersion(server));
  }

  public MinecraftVersion(String version) {
    int[] numbers = new int[3];
    try {
      numbers = parseVersion(version);
    } catch (NumberFormatException cause) {
      throw cause;
    }

    this.major = numbers[0];
    this.minor = numbers[1];
    this.build = numbers[2];
    this.compareId = parseInt(String.valueOf(major + "" + minor + "" + build));
  }

  public MinecraftVersion(int major, int minor, int build) {
    this.major = major;
    this.minor = minor;
    this.build = build;
    this.compareId = parseInt(String.valueOf(major + "" + minor + "" + build));
  }

  public boolean lowerThan(MinecraftVersion version) {
    return compareId < version.getCompareId();
  }

  public boolean newerThan(MinecraftVersion version) {
    return compareId > version.getCompareId();
  }

  public boolean inRange(MinecraftVersion latest, MinecraftVersion olded) {
    return (compareId <= latest.getCompareId()) && (compareId >= olded.getCompareId());
  }

  public int getMajor() {
    return major;
  }

  public int getMinor() {
    return minor;
  }

  public int getBuild() {
    return build;
  }

  public int getCompareId() {
    return compareId;
  }

  private int[] parseVersion(String version) {
    String[] elements = version.split("\\.");
    int[] numbers = new int[3];

    if (elements.length <= 1 || version.split("R").length < 1) {
      throw new IllegalStateException("Corrupt MC Server version: " + version);
    }

    for (int i = 0; i < 2; i++) {
      numbers[i] = parseInt(elements[i]);
    }

    numbers[2] = parseInt(version.split("R")[1]);
    return numbers;
  }

  public String getVersion() {
    return String.format("v%s_%s_R%s", major, minor, build);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof MinecraftVersion))
      return false;
    if (obj == this)
      return true;

    MinecraftVersion other = (MinecraftVersion) obj;

    return getMajor() == other.getMajor() && getMinor() == other.getMinor() && getBuild() == other.getBuild();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getMajor(), getMinor(), getBuild());
  }

  @Override
  public String toString() {
    return String.format("%s", getVersion());
  }

  public static String extractVersion(Server server) {
    return extractVersion(server.getClass().getPackage().getName().split("\\.")[3]);
  }

  public static String extractVersion(String version) {
    return version.replace('_', '.').replace("v", "");
  }

  public static MinecraftVersion fromServer(Server server) {
    return new MinecraftVersion(server);
  }

  private static MinecraftVersion currentVersion;

  public static MinecraftVersion getCurrentVersion() {
    if (currentVersion == null) {
      currentVersion = fromServer(Bukkit.getServer());
    }

    return currentVersion;
  }
}
