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
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsDeathCry;
import tk.kanaostore.losteddev.skywars.nms.Sound;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

public class DeathCryCommand extends SubCommand implements Listener {

  private static final Map<Player, Object[]> CREATING = new HashMap<>();

  public DeathCryCommand() {
    super("deathcry");
    Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
  }
  
  @Override
  public void perform(CommandSender sender, String[] args) {}

  @Override
  public void perform(Player player, String[] args) {
    if (args.length < 1) {
      player.sendMessage("§cUse /lsw deathcry <name>");
      return;
    }

    String name = StringUtils.join(args, 0, "");

    TextComponent component = new TextComponent(" \nCreating a new Death Cry \"" + name + "\"\n");
    component.setColor(ChatColor.AQUA);

    // Second Line
    TextComponent s1 = new TextComponent("Text on chat the ");
    s1.setColor(ChatColor.GRAY);
    TextComponent s2 = new TextComponent("Sound");
    s2.setColor(ChatColor.GOLD);
    s2.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Sounds: " + this.getSounds())));
    TextComponent s3 = new TextComponent(" or ");
    s3.setColor(ChatColor.GRAY);
    TextComponent s4 = new TextComponent("Click Here");
    s4.setColor(ChatColor.RED);
    s4.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "lswcry:cancel"));
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
    // 2 = sound
    // 3 = volume
    // 4 = pitch
    // 5 = price
    // 6 = rarity
    // 7 = buyable
    Object[] arr = new Object[8];
    arr[0] = name;
    arr[1] = name.replace(" ", "").toLowerCase();

    CREATING.put(player, arr);
  }

  private String getSounds() {
    StringBuilder sb = new StringBuilder();

    int count = 5;
    for (Sound sound : Sound.values()) {
      sb.append((count == 5 ? "\n" : "") + (count == 5 ? "" + sound.name() : ", " + sound.name()));
      count++;
      if (count > 5) {
        count = 0;
      }
    }

    return sb.toString();
  }

  @Override
  public String getUsage() {
    return "deathcry <name>";
  }

  @Override
  public String getDescription() {
    return "Create a new SkyWars DeathCry.";
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
      if (evt.getMessage().equals("lswcry:cancel")) {
        CREATING.remove(player);
        player.sendMessage("§5[LostSkyWars] §aDeathCry creation cancelled!");
        return;
      }

      String message = evt.getMessage();
      for (int time = 2; time < 8; time++) {
        if (CREATING.get(player)[time] == null) {
          if (time == 2) {
            // sound
            Sound sound = Sound.valueOf(message.toUpperCase());
            if (sound == null) {
              player.sendMessage("§5[LostSkyWars] §cUse a valid Sound for that Cry.");
              return;
            }

            CREATING.get(player)[time] = sound;
            TextComponent component = new TextComponent(" \nCreating a new Death Cry \"" + CREATING.get(player)[0] + "\"\n");
            component.setColor(ChatColor.AQUA);

            // Second Line
            TextComponent s1 = new TextComponent("Text on chat the sound ");
            s1.setColor(ChatColor.GRAY);
            TextComponent s2 = new TextComponent("Volume");
            s2.setColor(ChatColor.GOLD);
            TextComponent s3 = new TextComponent(" or ");
            s3.setColor(ChatColor.GRAY);
            TextComponent s4 = new TextComponent("Click Here");
            s4.setColor(ChatColor.RED);
            s4.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "lswcry:cancel"));
            TextComponent s5 = new TextComponent(" to cancel!\n ");
            s5.setColor(ChatColor.GRAY);

            s1.addExtra(s2);
            s1.addExtra(s3);
            s1.addExtra(s4);
            s1.addExtra(s5);
            component.addExtra(s1);

            player.spigot().sendMessage(component);
          } else if (time == 3) {
            // volume
            try {
              if (message.startsWith("-")) {
                throw new NumberFormatException();
              }

              float volume = Float.valueOf(message);
              if (volume < 0.1 || volume > 2.0) {
                player.sendMessage("§5[LostSkyWars] §cVolume needs to be higher than 0.0 and lower than 2.1.");
                return;
              }

              CREATING.get(player)[time] = volume;
              TextComponent component = new TextComponent(" \nCreating a new Death Cry \"" + CREATING.get(player)[0] + "\"\n");
              component.setColor(ChatColor.AQUA);

              // Second Line
              TextComponent s1 = new TextComponent("Text on chat the sound ");
              s1.setColor(ChatColor.GRAY);
              TextComponent s2 = new TextComponent("Pitch");
              s2.setColor(ChatColor.GOLD);
              TextComponent s3 = new TextComponent(" or ");
              s3.setColor(ChatColor.GRAY);
              TextComponent s4 = new TextComponent("Click Here");
              s4.setColor(ChatColor.RED);
              s4.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "lswcry:cancel"));
              TextComponent s5 = new TextComponent(" to cancel!\n ");
              s5.setColor(ChatColor.GRAY);

              s1.addExtra(s2);
              s1.addExtra(s3);
              s1.addExtra(s4);
              s1.addExtra(s5);
              component.addExtra(s1);

              player.spigot().sendMessage(component);
            } catch (Exception ex) {
              player.sendMessage("§5[LostSkyWars] §cUse a valid Volume for that Cry.");
            }
          } else if (time == 4) {
            // pitch
            try {
              if (message.startsWith("-")) {
                throw new NumberFormatException();
              }

              float pitch = Float.valueOf(message);
              if (pitch < 0.1 || pitch > 2.0) {
                player.sendMessage("§5[LostSkyWars] §cPitch needs to be higher than 0.0 and lower than 2.1.");
                return;
              }

              CREATING.get(player)[time] = pitch;
              TextComponent component = new TextComponent(" \nCreating a new Death Cry \"" + CREATING.get(player)[0] + "\"\n");
              component.setColor(ChatColor.AQUA);

              // Second Line
              TextComponent s1 = new TextComponent("Text on chat the ");
              s1.setColor(ChatColor.GRAY);
              TextComponent s2 = new TextComponent("Price");
              s2.setColor(ChatColor.GOLD);
              TextComponent s3 = new TextComponent(" or ");
              s3.setColor(ChatColor.GRAY);
              TextComponent s4 = new TextComponent("Click Here");
              s4.setColor(ChatColor.RED);
              s4.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "lswcry:cancel"));
              TextComponent s5 = new TextComponent(" to cancel!\n ");
              s5.setColor(ChatColor.GRAY);

              s1.addExtra(s2);
              s1.addExtra(s3);
              s1.addExtra(s4);
              s1.addExtra(s5);
              component.addExtra(s1);

              player.spigot().sendMessage(component);
            } catch (Exception ex) {
              player.sendMessage("§5[LostSkyWars] §cUse a valid Pitch for that Cry.");
            }
          } else if (time == 5) {
            // price
            try {
              if (message.startsWith("-")) {
                throw new NumberFormatException();
              }

              CREATING.get(player)[time] = Integer.parseInt(message);
              TextComponent component = new TextComponent(" \nCreating a new Death Cry \"" + CREATING.get(player)[0] + "\"\n");
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
              s4.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "lswcry:cancel"));
              TextComponent s5 = new TextComponent(" to cancel!\n ");
              s5.setColor(ChatColor.GRAY);

              s1.addExtra(s2);
              s1.addExtra(s3);
              s1.addExtra(s4);
              s1.addExtra(s5);
              component.addExtra(s1);

              player.spigot().sendMessage(component);
            } catch (Exception ex) {
              player.sendMessage("§5[LostSkyWars] §cUse a valid Price for that Cry.");
            }
          } else if (time == 6) {
            // rarity
            CosmeticRarity rarity = CosmeticRarity.fromName(message);
            if (rarity == null || (rarity == CosmeticRarity.COMMON && !message.equalsIgnoreCase("common"))) {
              player.sendMessage("§5[LostSkyWars] §cUse a valid Rarity for that Cry.");
              return;
            }

            CREATING.get(player)[time] = rarity;
            TextComponent component = new TextComponent(" \nCreating a new Death Cry \"" + CREATING.get(player)[0] + "\"\n");
            component.setColor(ChatColor.AQUA);

            // Second Line
            TextComponent s1 = new TextComponent("Text on chat if the cry is ");
            s1.setColor(ChatColor.GRAY);
            TextComponent s2 = new TextComponent("Buyable");
            s2.setColor(ChatColor.GOLD);
            TextComponent s3 = new TextComponent(" or ");
            s3.setColor(ChatColor.GRAY);
            TextComponent s4 = new TextComponent("Click Here");
            s4.setColor(ChatColor.RED);
            s4.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "lswcry:cancel"));
            TextComponent s5 = new TextComponent(" to cancel!\n ");
            s5.setColor(ChatColor.GRAY);

            s1.addExtra(s2);
            s1.addExtra(s3);
            s1.addExtra(s4);
            s1.addExtra(s5);
            component.addExtra(s1);

            player.spigot().sendMessage(component);
          } else if (time == 7) {
            // buyable
            if (!message.equals("true") && !message.equals("false")) {
              player.sendMessage("§5[LostSkyWars] §cUse true or false to buyable property.");
              return;
            }

            CREATING.get(player)[time] = Boolean.valueOf(message);

            SkyWarsDeathCry.createNew(CREATING.remove(player));
            player.sendMessage("§5[LostSkyWars] §aDeath Cry created!");
          }

          break;
        }
      }
    }
  }
}
