package tk.kanaostore.losteddev.skywars.hook;

import io.github.losteddev.boxes.api.LostBoxesAPI;
import io.github.losteddev.boxes.api.box.RewardRarity;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.cosmetics.Cosmetic;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticServer;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticType;
import tk.kanaostore.losteddev.skywars.hook.boxes.BoxNPC;
import tk.kanaostore.losteddev.skywars.hook.boxes.CosmeticReward;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;

public class BoxesHook {
  
  public static final LostLogger LOGGER = Main.LOGGER.getModule("BoxesHook");
  
  public static void setupBoxes() {
    LOGGER.log(LostLevel.INFO, "LostBoxes found, hooking...");
    
    for (Cosmetic cosmetic : CosmeticServer.SKYWARS.listCosmetics()) {
      if (cosmetic.getType() == CosmeticType.SKYWARS_SYMBOL) {
        continue;
      }
      if (cosmetic.getMode() == 3 && Language.options$ranked$freekitsandperks) {
        continue;
      }
      
      RewardRarity rarity = RewardRarity.from(cosmetic.getRarity().name().toLowerCase());
      if (rarity != null) {
        LostBoxesAPI.addBoxReward(new CosmeticReward(cosmetic));
      }
    }
    
    BoxNPC.setupBoxNPCs();
  }
  
  public static void destroyBoxes() {
    LOGGER.log(LostLevel.INFO, "LostBoxes found, destroying Vaults...");

    BoxNPC.listNPCs().forEach(BoxNPC::destroy);
  }
}
