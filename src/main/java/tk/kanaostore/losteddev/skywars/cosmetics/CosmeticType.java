package tk.kanaostore.losteddev.skywars.cosmetics;

import tk.kanaostore.losteddev.skywars.Language;

public enum CosmeticType {

    SKYWARS_KIT(1, "kits", 0, 4),
    SKYWARS_PERK(2, "perks", -1/* no needs */, 3),
    SKYWARS_CAGE(3, "cages", 1),
    SKYWARS_DEATHCRY(4, "deathcry", 2),
    SKYWARS_BALLON(5, "ballons", 3),
    SKYWARS_SYMBOL(6, "noneeds", 4);

  private int uniqueId;
  private String stats;
  private int index, size;

  CosmeticType(int uniqueId, String stats, int index) {
    this(uniqueId, stats, index, 1);
  }

  CosmeticType(int uniqueId, String stats, int index, int size) {
    this.uniqueId = uniqueId;
    this.stats = stats;
    this.index = index;
    this.size = size;
  }

  public String getName() {
    return this == SKYWARS_KIT ? Language.options$cosmetic$prefix + Language.options$cosmetic$kit
        : this == SKYWARS_PERK ? Language.options$cosmetic$prefix + Language.options$cosmetic$perk
            : this == SKYWARS_CAGE ? Language.options$cosmetic$prefix + Language.options$cosmetic$cage
                : this == SKYWARS_DEATHCRY ? Language.options$cosmetic$prefix + Language.options$cosmetic$deathcry
                    : Language.options$cosmetic$prefix + Language.options$cosmetic$ballon;
  }

  public int getUniqueId() {
    return uniqueId;
  }

  public int getIndex() {
    return index;
  }

  public int getSize() {
    return size;
  }

  public String getStats() {
    return stats;
  }

  public static CosmeticType getByUniqueId(int uniqueId) {
    for (CosmeticType type : values()) {
      if (type.uniqueId == uniqueId) {
        return type;
      }
    }

    return null;
  }
}
