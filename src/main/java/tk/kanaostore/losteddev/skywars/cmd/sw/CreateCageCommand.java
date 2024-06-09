package tk.kanaostore.losteddev.skywars.cmd.sw;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.cmd.SubCommand;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticRarity;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsCage;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

public class CreateCageCommand extends SubCommand implements Listener {

  private static final Map<Player, Object[]> CREATING = new HashMap<>();

  public CreateCageCommand() {
    super("createcage");
    Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
  }
  
  @Override
  public void perform(CommandSender sender, String[] args) {}

  @Override
  public void perform(Player player, String[] args) {
    if (args.length < 1) {
      player.sendMessage("§cUse /lsw createcage <name>");
      return;
    }

    String name = StringUtils.join(args, 0, "");

    TextComponent component = new TextComponent(" \nCreating a new Cage \"" + name + "\"\n");
    component.setColor(ChatColor.AQUA);

    // Second Line
    TextComponent s1 = new TextComponent("Text on chat the ");
    s1.setColor(ChatColor.GRAY);
    TextComponent s2 = new TextComponent("Permission");
    s2.setColor(ChatColor.GOLD);
    s2.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText("§7If you don't needs a permission uses none")));
    TextComponent s3 = new TextComponent(" or ");
    s3.setColor(ChatColor.GRAY);
    TextComponent s4 = new TextComponent("Click Here");
    s4.setColor(ChatColor.RED);
    s4.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "lswcage:cancel"));
    TextComponent s5 = new TextComponent(" to cancel!\n ");
    s5.setColor(ChatColor.GRAY);

    s1.addExtra(s2);
    s1.addExtra(s3);
    s1.addExtra(s4);
    s1.addExtra(s5);
    component.addExtra(s1);

    player.spigot().sendMessage(component);

    // 0 = name
    // 1 = key
    // 2 = array
    // 3 = permission
    // 4 = rarity
    Object[] arr = new Object[5];
    arr[0] = name;
    arr[1] = name.replace(" ", "").toLowerCase();
    arr[2] = SkyWarsCage.createCage(player.getLocation());

    CREATING.put(player, arr);
  }

  @Override
  public String getUsage() {
    return "createcage <name>";
  }

  @Override
  public String getDescription() {
    return "Create a new SkyWars Cage.";
  }
  
  @Override
  public boolean onlyForPlayer() {
    return true;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onAsyncPlayerChat(AsyncPlayerChatEvent evt) {
    Player player = evt.getPlayer();
    if (CREATING.containsKey(player)) {
      evt.setCancelled(true);
      if (evt.getMessage().equals("lswcage:cancel")) {
        CREATING.remove(player);
        player.sendMessage("§5[LostSkyWars] §aCage creation cancelled!");
        return;
      }

      String message = evt.getMessage();
      for (int time = 3; time < 5; time++) {
        if (CREATING.get(player)[time] == null) {
          if (time == 3) {
            // permission
            String permission = message.toLowerCase();

            CREATING.get(player)[time] = permission;
            TextComponent component = new TextComponent(" \nCreating a new Cage \"" + CREATING.get(player)[0] + "\"\n");
            component.setColor(ChatColor.AQUA);

            // Second Line
            TextComponent s1 = new TextComponent("Text on chat the ");
            s1.setColor(ChatColor.GRAY);
            TextComponent s2 = new TextComponent("Rarity");
            s2.setColor(ChatColor.GOLD);
            s2.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Rarities: §fCOMMON, RARE, EPIC, LEGENDARY")));
            TextComponent s3 = new TextComponent(" or ");
            s3.setColor(ChatColor.GRAY);
            TextComponent s4 = new TextComponent("Click Here");
            s4.setColor(ChatColor.RED);
            s4.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "lbwskin:cancel"));
            TextComponent s5 = new TextComponent(" to cancel!\n ");
            s5.setColor(ChatColor.GRAY);

            s1.addExtra(s2);
            s1.addExtra(s3);
            s1.addExtra(s4);
            s1.addExtra(s5);
            component.addExtra(s1);

            player.spigot().sendMessage(component);
          } else if (time == 4) {
            // rarity
            CosmeticRarity rarity = CosmeticRarity.fromName(message);
            if (rarity == null || (rarity == CosmeticRarity.COMMON && !message.equalsIgnoreCase("common"))) {
              player.sendMessage("§5[LostSkyWars] §cUse a valid Rarity for that Cage.");
              return;
            }

            CREATING.get(player)[time] = rarity;
            SkyWarsCage.createNew(CREATING.remove(player));
            player.sendMessage("§5[LostSkyWars] §aCage created!");
          }

          break;
        }
      }
    }
  }
}
