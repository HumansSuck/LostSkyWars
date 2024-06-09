package tk.kanaostore.losteddev.skywars.hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPCRegistry;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.hook.citizens.DeliveryNPC;
import tk.kanaostore.losteddev.skywars.hook.citizens.DuelsNPC;
import tk.kanaostore.losteddev.skywars.hook.citizens.EmptyDatastore;
import tk.kanaostore.losteddev.skywars.hook.citizens.PlayNPC;
import tk.kanaostore.losteddev.skywars.hook.citizens.RankedNPC;
import tk.kanaostore.losteddev.skywars.hook.citizens.ShopkeeperNPC;
import tk.kanaostore.losteddev.skywars.hook.citizens.StatsNPC;
import tk.kanaostore.losteddev.skywars.menu.DeliveryManMenu;
import tk.kanaostore.losteddev.skywars.menu.PlayDuelsMenu;
import tk.kanaostore.losteddev.skywars.menu.PlayMenu;
import tk.kanaostore.losteddev.skywars.menu.PlayRankedMenu;
import tk.kanaostore.losteddev.skywars.menu.ShopMenu;
import tk.kanaostore.losteddev.skywars.menu.StatsNPCMenu;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsMode;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;

public class CitizensHook {

  public static final LostLogger LOGGER = Main.LOGGER.getModule("CitizensHook");

  private static NPCRegistry registry;

  public static void setupCitizens() {
    LOGGER.log(LostLevel.INFO, "Citizens found, hooking...");

    registry = CitizensAPI.createNamedNPCRegistry("LostSkyWars", new EmptyDatastore());

    PlayNPC.setupPlayNPCs();
    RankedNPC.setupRankedNPCs();
    DuelsNPC.setupDuelsNPCs();
    DeliveryNPC.setupDeliveryNPCs();
    ShopkeeperNPC.setupShopkeeperNPCs();
    if (Main.protocollib) {
      StatsNPC.setupStatsNPCs();
    }

    Bukkit.getPluginManager().registerEvents(new Listener() {
      @EventHandler
      public void onNPCClick(NPCLeftClickEvent evt) {
        Player player = evt.getClicker();
        if (evt.getNPC().data().has("play-npc")) {
          new PlayMenu(player, SkyWarsMode.fromName(evt.getNPC().data().get("play-npc")));
        } else if (evt.getNPC().data().has("ranked-npc")) {
          Account account = Database.getInstance().getAccount(player.getUniqueId());
          if (account != null) {
            if (account.getLevel() >= Language.options$ranked$required$level) {
              new PlayRankedMenu(player);
              return;
            }

            player.sendMessage(Language.options$ranked$required$message);
          }
        } else if (evt.getNPC().data().has("duels-npc")) {
          new PlayDuelsMenu(player);
        } else if (evt.getNPC().data().has("shopkeeper")) {
          new ShopMenu(player);
        } else if (evt.getNPC().data().has("profile")) {
          new StatsNPCMenu(player);
        }
      }

      @EventHandler
      public void onNPCClick(NPCRightClickEvent evt) {
        Player player = evt.getClicker();
        if (evt.getNPC().data().has("play-npc")) {
          new PlayMenu(player, SkyWarsMode.fromName(evt.getNPC().data().get("play-npc")));
        } else if (evt.getNPC().data().has("ranked-npc")) {
          Account account = Database.getInstance().getAccount(player.getUniqueId());
          if (account != null) {
            if (account.getLevel() >= Language.options$ranked$required$level) {
              new PlayRankedMenu(player);
              return;
            }

            player.sendMessage(Language.options$ranked$required$message);
          }
        } else if (evt.getNPC().data().has("duels-npc")) {
          new PlayDuelsMenu(player);
        } else if (evt.getNPC().data().has("deliveryman")) {
          new DeliveryManMenu(player);
        } else if (evt.getNPC().data().has("shopkeeper")) {
          new ShopMenu(player);
        } else if (evt.getNPC().data().has("profile")) {
          new StatsNPCMenu(player);
        }
      }
    }, Main.getInstance());
  }

  public static void destroyCitizens() {
    LOGGER.log(LostLevel.INFO, "Citizens found, destroying NPCs...");

    PlayNPC.listNPCs().forEach(PlayNPC::destroy);
    RankedNPC.listNPCs().forEach(RankedNPC::destroy);
    DuelsNPC.listNPCs().forEach(DuelsNPC::destroy);
    DeliveryNPC.listNPCs().forEach(DeliveryNPC::destroy);
    ShopkeeperNPC.listNPCs().forEach(ShopkeeperNPC::destroy);
    StatsNPC.listNPCs().forEach(StatsNPC::destroy);
  }

  public static NPCRegistry getRegistry() {
    return registry;
  }
}
