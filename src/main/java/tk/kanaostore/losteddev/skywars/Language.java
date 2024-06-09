package tk.kanaostore.losteddev.skywars;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import tk.kanaostore.losteddev.skywars.bungee.Core;
import tk.kanaostore.losteddev.skywars.bungee.CoreMode;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticRarity;
import tk.kanaostore.losteddev.skywars.leaderboards.LeaderBoardStats;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsMode;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsType;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;
import tk.kanaostore.losteddev.skywars.utils.LanguageWriter;
import tk.kanaostore.losteddev.skywars.utils.LostLogger;
import tk.kanaostore.losteddev.skywars.utils.LostLogger.LostLevel;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

@SuppressWarnings("rawtypes")
public class Language {

  public static long scoreboards$animation$update = 1;
  public static List<String> scoreboards$animation$title =
      Arrays.asList("§f§l§6§lS§a§lKYWARS", "§f§lS§6§lK§a§lYWARS", "§f§lSK§6§lY§a§lWARS", "§f§lSKY§6§lW§a§lARS", "§f§lSKYW§6§lA§a§lRS", "§f§lSKYWA§6§lR§a§lS", "§f§lSKYWAR§6§lS§a§l",
          "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§f§lSKYWARS", "§f§lSKYWARS", "§f§lSKYWARS", "§f§lSKYWARS", "§f§lSKYWARS",
          "§f§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§f§lSKYWARS", "§f§lSKYWARS", "§f§lSKYWARS", "§f§lSKYWARS",
          "§f§lSKYWARS", "§f§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS",
          "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS",
          "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS", "§a§lSKYWARS");
  public static String scoreboard$replace$waiting = "Waiting...";
  public static String scoreboard$replace$starting = "Starting in §a{time}s";
  public static List<String> scoreboards$lines$lobby =
      Arrays.asList("", "Your Level: {level}", "", "Solo Kills: §a{solokills}", "Solo Wins: §a{solowins}", "Doubles Kills: §a{teamkills}", "Doubles Wins: §a{teamwins}",
          "Ranked Kills: §a{rankedkills}", "Ranked Wins: §a{rankedwins}", "", "Coins: §6{coins}", "Souls: §b{souls}§7/{maxsouls}", "", "§ewww.example.net");
  public static List<String> scoreboards$lines$waiting =
      Arrays.asList("§7{date} {world}", "", "Players: §a{on}/{max}", "", "{replace}", "", "Map: §a{map}", "Mode: {mode}", "", "§ewww.example.net");
  public static List<String> scoreboards$lines$waiting_duels =
      Arrays.asList("§7{date} §8{world}", "", "Map: §a{map}", "Players: §a{on}/{max}", "", "{replace}", "", "§ewww.example.net");
  public static List<String> scoreboards$lines$ingame = Arrays.asList("§7{date} {world}", "", "Next Event:", "§a{event}", "", "Players left: §a{on}", "", "Kills: §a{kills}", "",
      "Map: §a{map}", "Mode: {mode}", "", "§ewww.example.net");
  public static List<String> scoreboards$lines$ingame_doubles = Arrays.asList("§7{date} {world}", "", "Next Event:", "§a{event}", "", "Players left: §a{on}",
      "Teams left: §a{teams}", "", "Kills: §a{kills}", "", "Map: §a{map}", "Mode: {mode}", "", "§ewww.example.net");
  public static List<String> scoreboards$lines$ingame_duels =
      Arrays.asList("§7{date} §8{world}", "", "Time Left: §a{timeLeft}", "", "Opponent:", "{opponent}", "", "Kit: §a{kit}", "", "Mode: §aSkyWars Duel", "", "§ewww.example.net");
  public static List<String> scoreboards$lines$ingame_duels_doubles = Arrays.asList("§7{date} §8{world}", "", "Time Left: §a{timeLeft}", "", "Opponents:", "{opponent}",
      "{opponent2}", "", "Kit: §a{kit}", "", "Mode: §aSkyWars Duel", "", "§ewww.example.net");

  public static boolean options$ranks$tab = true;
  public static boolean options$ranks$chat = true;
  public static boolean options$ranked$freekitsandperks = true;
  public static int options$ranked$required$level = 1;
  public static boolean options$game$default_kit = true;
  public static String options$ranked$required$message = "§cYou need at least SkyWars Level 1 to play ranked SkyWars! Your Level can be seen on your xp bar in lobbies.";
  public static String options$mode$solo = "Solo";
  public static String options$mode$doubles = "Doubles";
  public static String options$type$normal = "§aNormal";
  public static String options$type$insane = "§cInsane";
  public static String options$type$ranked = "§6Ranked";
  public static String options$type$duels = "§6Duels";
  public static String options$rarity$common = "§aCOMMON";
  public static String options$rarity$rare = "§9RARE";
  public static String options$rarity$epic = "§5EPIC";
  public static String options$rarity$legendary = "§6LEGENDARY";
  public static String options$cosmetic$default_kit = "Default";
  public static String options$cosmetic$prefix = "SkyWars ";
  public static String options$cosmetic$kit = "Kit ";
  public static String options$cosmetic$perk = "Perk ";
  public static String options$cosmetic$cage = "Cage ";
  public static String options$cosmetic$deathcry = "Death Cry ";
  public static String options$cosmetic$ballon = "Ballon ";
  public static int options$leaderboard$update_time_minutes = 30;
  public static String options$leaderboard$empty = "§7None";
  public static String options$leaderboard$armorstand$stats$wins = "Wins";
  public static String options$leaderboard$armorstand$stats$kills = "Kills";
  public static String options$leaderboard$armorstand$stats$level = "Levels";
  public static String options$leaderboard$armorstand$stats$ranked = "Points";
  public static String options$leaderboard$holograms$mode$wins = "All Modes";
  public static String options$leaderboard$holograms$mode$kills = "All Modes";
  public static String options$leaderboard$holograms$mode$level = "Worldwide Best";
  public static String options$leaderboard$holograms$mode$ranked = "Ranked Mode";
  public static String options$leaderboard$holograms$stats$wins = "Lifetime Wins";
  public static String options$leaderboard$holograms$stats$kills = "Lifetime Kills";
  public static String options$leaderboard$holograms$stats$level = "SkyWars Levels";
  public static String options$leaderboard$holograms$stats$ranked = "Ranked Rating";
  public static List<String> options$leaderboard$armorstand$lines = Arrays.asList("§7{playerstats} {stats}", "{name}", "§f§lTop {position}");
  public static List<String> options$leaderboard$hologram$lines =
      Arrays.asList("§a10. {10_name} §7- §e{10_playerstats}", "§a9. {9_name} §7- §e{9_playerstats}", "§a8. {8_name} §7- §e{8_playerstats}", "§a7. {7_name} §7- §e{7_playerstats}",
          "§a6. {6_name} §7- §e{6_playerstats}", "§a5. {5_name} §7- §e{5_playerstats}", "§a4. {4_name} §7- §e{4_playerstats}", "§a3. {3_name} §7- §e{3_playerstats}",
          "§a2. {2_name} §7- §e{2_playerstats}", "§a1. {1_name} §7- §e{1_playerstats}", "", "§7{mode}", "§f§l{stats}");

  public static boolean lobby$chat$enabled = true;
  public static int lobby$chat$delay_time = 3;
  public static String lobby$chat$delay_message = "§cYou must wait §a{time}s §cbetween send other message!";
  public static String lobby$chat$format = "§7[§a{level}§7] {display}{color}: {message}";
  public static String lobby$chat$format_ranked = "§7[{league}({points})§7] {colored}{color}: {message}";
  public static String lobby$chat$format_duels = "§a[GAME] §7{colored}{color}: {message}";
  public static String lobby$chat$format_spectator = "§7[SPECTATOR] §7[§a{level}§7] {display}{color}: {message}";

  public static String lobby$visibility$delay = "§cYou must wait §a{time}s §cbetween uses!";
  public static String lobby$visibility$enabled = "§aPlayer visibility enabled!";
  public static String lobby$visibility$disabled = "§cPlayer visibility disabled!";

  public static int lobby$hotbar$profile$slot = 0;
  public static int lobby$hotbar$shop$slot = 1;
  public static int lobby$hotbar$players$slot = 2;
  public static String lobby$hotbar$profile$name = "§aMy Profile §7(Right Click)";
  public static String lobby$hotbar$shop$name = "§aShop §7(Right Click)";
  public static String lobby$hotbar$players$name_i = "§fPlayers: §cHidden §7(Right Click)";
  public static String lobby$hotbar$players$name_v = "§fPlayers: §aVisible §7(Right Click)";

  public static String lobby$npcs$play$solo$skin_value =
      "eyJ0aW1lc3RhbXAiOjE1MTYxMjkzMjM0MzQsInByb2ZpbGVJZCI6IjIzZjFhNTlmNDY5YjQzZGRiZGI1MzdiZmVjMTA0NzFmIiwicHJvZmlsZU5hbWUiOiIyODA3Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85NDk1MmZjZWJjMjM4MTVkNWQzNWE5ZjQzNGEzNWM2Y2RiODdiOTZmYjkzZTY4N2FmZjljZGIxZmE5ZWQzIn19fQ==";
  public static String lobby$npcs$play$solo$skin_signature =
      "a408vXEyIRgDZ+bY5ZW/cmhqd1N0VTKp0mc0Px46mwQ0bJWei7sKmlvh218uaiUmpPTJCLVlWPfimZVa59HDExeZKqaTZIIW8crwqw/KAcgZo8tDzIPg4x7/f0CMju8WL4yfHzLX8ny8ZNeXhP6gKhHyMF7AcL4gYlEBhaE+6gDPw7GHbJM08q38UvGzFQfmduv/0zT013P2uKFyAGgHpmSwlxPmG61vWscYrN55cd6qj8uoXY+OQf1CEQqeWuypm6POED+dYtHl4r1bMZMRCSX6345lM7o6TudlwND2HF+pUe0rpN8vRGelcvhC84yRUrKuyZWVFvA3PHgrNVh8s8BXR1GlbZ4r47PuPnb2jcTF3qFWKuHhcThYI5x1uMnLfYSeAtadt9dT/SiwgCZSk6SUQ1Tt3wSv/eh0ZVnFoEX9tmVEzhQqG8fekaGh5nYp6cyr8Y6jxTM1cnwST6b9slWFU3W2daTOCW4llnfIV2ghhGF6XhwuGxQU89y+4JVoUcdH4eMyEmT8K6526cREAnYC/mSH1vDU2gLVotBAIrfqs2gCIsANaaaO+Bv2T2r/zqazMfw/Dzq/QqzOxQomaHzX2LZrlCO9ozjIPXz5WkiUpMyj9LVxDiQ8ju+9MJTpP9O7cCjJg2go2Np+QvkaSZ38K3uR6Zikgn1rQrAg9i4=";
  public static String lobby$npcs$play$team$skin_value =
      "eyJ0aW1lc3RhbXAiOjE1NTQ1MjUzNjc1NTksInByb2ZpbGVJZCI6IjVkMjRiYTBiMjg4YzQyOTM4YmExMGVjOTkwNjRkMjU5IiwicHJvZmlsZU5hbWUiOiIxbnYzbnQxdjN0NGwzbnQiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzMyNjlmNzdjNjdjOGIwMDE3MTkwOWZlYmE2ZjAxMzBmOTkwY2ZhNDA2MjZkZjE3ZWY1NTQwNWFiYzE3YTFlMTEifX19";
  public static String lobby$npcs$play$team$skin_signature =
      "DvyPAp8NVAwNHGATU0TqS9O8zh67u1iv9xkQ0V1c3lf5QNBeWmI/xKdMJ1jiFxEN3NCqNne6bHttsCUcDPxtwSS5M4DXIuOIf/pIlWKxh9SXqyAbeM0w68WOYbo5CWrhrO9q3Vgt/ahufDJl/HTiLCX1J02EFcQWF16EEke8cTOi0C6zPYz5a6AE4dMTH1SWHXGyF+xaD7YBioODQcODbkwr3Ku8s12zqIjLRRUwwj1KHTqVOlSyr46atb30KMWmlHSSTv4Q+dAUYn/g94kDoElyiyBuYTC6/VQJZJSFVZl44L+DP5y5kxtcU6anfRJa45ZnkBcPwgnOUWFPmWxaSNZz0ZnT6deJrsR4sZtFzSvXU7x4ThNNVr13+uS71gi7G8fkkCKZoYSGS/5f1AMNbI2RjfuYLEwrRJcqTIcAGIt2h9cLOQD6cTpl2/IiO4Obs/ckVN28KiZHQksyK+5ASSvNafNQMoFLOUgMrw39RuVQhhcxEid2iBMGipaw7Ri6OHky+GuAp+YkgGEjNV9CslP4JIDpLm8f0ecbv8OPkbpphk2FrQo6RUJmjWnmSiLdkGG3rCTnGNAjKEHdYxlKqmIBaajLpreriBB+xL5dXXNGPY55ZSZSsNfaJKNBQzyRdpmeQVz48P0hipCckwQ+Q68r5pWwqAyDsbtXzGR+Fmg=";
  public static String lobby$npcs$ranked$skin_value =
      "eyJ0aW1lc3RhbXAiOjE1Njg2MDQ1NDM3MjMsInByb2ZpbGVJZCI6IjA3NGI4MDlkZjE0MjRiNjM5YjM4ZDRiNWZkODViODI5IiwicHJvZmlsZU5hbWUiOiJzZW5hbG9sIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81NTM1NjU0OTcyNTMwZGRiZTdjMGUxMWIyMjM2Y2MzZDIzYmQzMGNjMzJhMDhhMzFiNWIwMzFmZDQ1NDY0MWU2In19fQ==";
  public static String lobby$npcs$ranked$skin_signature =
      "GXWTnXIW27vDoGiIQjSlCQXfs56fj9sT5JSeMYB2T+Aj0zTOiQz3LaQEUBG98fPza3WQ5GLxDGa3jMXkK8Rx5DJWr4vuVfwRjtemol5PyOE6r5pjam6qNz0lfVAOW65xqHZjMp//1s+wCk60evfRCOQleQ6xon3iJ1w+tCkP81/TLIxn1g4z9GuPqNfChb4KJv/rr8QM5oMmXyJruDDFb1B4bLg6eMAbeK7H7KkQpEmXpl3aPJUSA4YaJa08CadqNR/LGQiswTfxRKfofk/ivUwJ7WUPVXOYH+Tt9pLSKnnUfH+KO8HGJhnXMSGqJWwPJKnNRE+kcfy+X8Yu3wh0el+u7Fh68XhGLrIh1VJi/hB6oMHKWcH9G+Akbd6ayoVxV7K/ENOetFxakNcom101/d3Ty/Wcxyigm0PE3sayCQv9aguwbZeaHaOdq85VGo1tu17wAFfzun85nLXpDcB0Xm/1+vfL6tk8pp1vBPhlIe7eb56xMTsiZxH1cWhKEL7+wsCMTTgw0RxPsKIsEgcz+N3Yg5l1f/krfBSWzi0sIfVCoVMDr7oLpSfwfHnkjzk3wmu0DsmTJwDWqPq1EW7Ofqm1QqXnwjP37uPx4YtULwntWDNTX05eHB00YYdBDC+Xsk5NHnpOMC+Rk4D/qaGxeAlwt7vyALU/QzLGp50hd4Q=";
  public static String lobby$npcs$duels$skin_value =
      "eyJ0aW1lc3RhbXAiOjE1Njg2MDQ1NDM3MjMsInByb2ZpbGVJZCI6IjA3NGI4MDlkZjE0MjRiNjM5YjM4ZDRiNWZkODViODI5IiwicHJvZmlsZU5hbWUiOiJzZW5hbG9sIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81NTM1NjU0OTcyNTMwZGRiZTdjMGUxMWIyMjM2Y2MzZDIzYmQzMGNjMzJhMDhhMzFiNWIwMzFmZDQ1NDY0MWU2In19fQ==";
  public static String lobby$npcs$duels$skin_signature =
      "GXWTnXIW27vDoGiIQjSlCQXfs56fj9sT5JSeMYB2T+Aj0zTOiQz3LaQEUBG98fPza3WQ5GLxDGa3jMXkK8Rx5DJWr4vuVfwRjtemol5PyOE6r5pjam6qNz0lfVAOW65xqHZjMp//1s+wCk60evfRCOQleQ6xon3iJ1w+tCkP81/TLIxn1g4z9GuPqNfChb4KJv/rr8QM5oMmXyJruDDFb1B4bLg6eMAbeK7H7KkQpEmXpl3aPJUSA4YaJa08CadqNR/LGQiswTfxRKfofk/ivUwJ7WUPVXOYH+Tt9pLSKnnUfH+KO8HGJhnXMSGqJWwPJKnNRE+kcfy+X8Yu3wh0el+u7Fh68XhGLrIh1VJi/hB6oMHKWcH9G+Akbd6ayoVxV7K/ENOetFxakNcom101/d3Ty/Wcxyigm0PE3sayCQv9aguwbZeaHaOdq85VGo1tu17wAFfzun85nLXpDcB0Xm/1+vfL6tk8pp1vBPhlIe7eb56xMTsiZxH1cWhKEL7+wsCMTTgw0RxPsKIsEgcz+N3Yg5l1f/krfBSWzi0sIfVCoVMDr7oLpSfwfHnkjzk3wmu0DsmTJwDWqPq1EW7Ofqm1QqXnwjP37uPx4YtULwntWDNTX05eHB00YYdBDC+Xsk5NHnpOMC+Rk4D/qaGxeAlwt7vyALU/QzLGp50hd4Q=";
  public static String lobby$npcs$deliveryman$skin_value =
      "eyJ0aW1lc3RhbXAiOjE1NzE1OTI3NjkzMjYsInByb2ZpbGVJZCI6ImU0MDE1NTBiZjNkNzRlMThiMzYxNWQzNDNjNTliMjA3IiwicHJvZmlsZU5hbWUiOiJaZWFsb2NrIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9hNDAyZDY4MGRhNjZjMmQyNTBiNjVlM2ZlNDZhNzE5YzM1MWFjMzQ2NzhkYTM5NjNmNzcxNjVmYWQzZWY2MzhjIiwibWV0YWRhdGEiOnsibW9kZWwiOiJzbGltIn19fX0=";
  public static String lobby$npcs$deliveryman$skin_signature =
      "l8vl8BjvErK++4xnKharcxJv+Z71GqA0MYXxPbCYB2PAoKCxC+gYsIDm7aG2/cKZuz8RcANLxSp2NNMlqSUcMp8lBptVHdLtB7J+NG+QmGRvgZUd5S7wMU3jQu7VH5XIxn5qgZNZ9ef9naa9v3R9A66WmWOGuKjEJaSW4hKuxZ821zjS1QkeESAYgNzyU7EwSb3MSIuHXPvmAaYH7pUZyoZTh2sTqXTYOAZmJD5k6DWxqVuGinl1fbU07WNPJk/8frlgHWhuhtqYc7Zvx2RV9kDsWJ8Spcex6VQmEk+MJNB/VmPjXrke3YQKEF5udq3C9nJstiM6V/VgduYZTSSkTcP3lfcCwcvROYaVpsydCNS6Hn2Zp953lZseK/p3/W8Zic5zhcUbEwrNiPs37ZO9Ktz/O2YZR98djR95zqv+QImXi35mIelYuLdDEUeGZNPC41jHoeCR952T8i4l5ilN0hXoZPLPq+5NV/m4GP180gxGASVTwD4wkDmDtwjo22XT9d7dr92KTBdNndak2slJ5+hRukpNtUcRyJTsCDz8A3E6hFNjS4pIyCe0gSRIrx2xoDBwqV16ELDtrX2yv8ofQd2b6scxMBJ3zUW16mEDrbON/N8OWACYwD8jdtlExETXP7bDhtdotlaIRmd+pW7s1BZmShCKxKjrOiv/ZCMA5X0=";
  public static List<String> lobby$npcs$play$solo$holograms = Arrays.asList("§e§lCLICK TO PLAY!", "§fSolo §7[Normal/Insane]", "§a§l{players} Players");
  public static List<String> lobby$npcs$play$team$holograms = Arrays.asList("§e§lCLICK TO PLAY!", "§fDoubles §7[Normal/Insane]", "§a§l{players} Players");
  public static List<String> lobby$npcs$ranked$holograms = Arrays.asList("§e§lCLICK TO PLAY!", "§fRanked §7[Solo/Doubles]", "§a§l{players} Players");
  public static List<String> lobby$npcs$duels$holograms = Arrays.asList("§e§lCLICK TO PLAY!", "§a[BETA] §6§lDUELS §fSkyWars", "§a§l{players} Players");
  public static List<String> lobby$npcs$well$holograms = Arrays.asList("§bSoul Well", "§e§lRIGHT CLICK");
  public static List<String> lobby$npcs$box$holograms = Arrays.asList("§bMystery Vault", "§e§lRIGHT CLICK");
  public static List<String> lobby$npcs$deliveryman$holograms = Arrays.asList("§bThe Delivery Man", "§e§lRIGHT CLICK");
  public static List<String> lobby$npcs$shopkeeper$holograms = Arrays.asList("§e§lCLICK TO OPEN!", "§cShopkeeper");
  public static List<String> lobby$npcs$statsnpc$holograms =
      Arrays.asList("§6§lYour SkyWars Profile", "Your Level: {level}", "Progress: §b{exp}§7/§a{nextExp}", "Total Wins: §a{wins}", "Total Kills: §a{kills}", "§e§lCLICK FOR STATS!");

  public static String lobby$npcs$play$connecting = "§aSending you to {world}!\n \n ";
  public static String lobby$npcs$box$already_in_use = "§cOnly one player can use the Mystery Vault at a time!";
  public static String lobby$npcs$box$duplicate = "§aYou've received §b{frags} Mystery Dusts for already have {prefix} {item}§a!";

  public static boolean lobby$speed$enabled = true;
  public static int lobby$speed$level = 1;
  public static boolean lobby$jump_boost$enabled = false;
  public static int lobby$jump_boost$level = 1;

  public static boolean lobby$motd$enabled = true;
  public static String lobby$motd$header = "            §aExample Network §c[1.8-1.13]";
  public static String lobby$motd$footer = "        §aCome here and play the new SkyWars!";

  public static boolean lobby$tablist$enabled = true;
  public static String lobby$tablist$header = "§bYou are playing on §a§lMC.EXAMPLE.NET\n ";
  public static String lobby$tablist$footer = " \n§aRanks, Boosters & MORE! §c§lSTORE.EXAMPLE.NET";

  public static String lobby$connecting$party$not_leader = "§cOnly the Party leader can find a match.";

  public static boolean game$regen$world_reload = true;
  public static int game$regen$blocks_per_tick = 20000;
  public static int game$rewards$coins_per_play = 5;
  public static int game$rewards$coins_per_kill = 5;
  public static int game$rewards$coins_per_win = 50;
  public static double game$rewards$exp_per_play = 1.0;
  public static double game$rewards$exp_per_kill = 5.0;
  public static double game$rewards$exp_per_win = 10.0;

  public static int game$countdown$start = 45;
  public static int game$countdown$full = 10;
  public static int game$countdown$game = 780;
  public static int game$countdown$game_duels = 480;
  public static List<Integer> game$countdown$refills = Arrays.asList(480, 300);

  public static String game$event$start = "Start ";
  public static String game$event$refill = "Refill ";
  public static String game$event$end = "End ";

  public static int game$hotbar$kits$slot = 0;
  public static int game$hotbar$quit$slot = 8;
  public static int game$hotbar$compass$slot = 0;
  public static int game$hotbar$quit_spectator$slot = 8;
  public static int game$hotbar$play_again$slot = 7;
  public static String game$hotbar$kits$name = "§aKit Selector §7(Right Click)";
  public static String game$hotbar$quit$name = "§c§lReturn to Lobby §7(Right Click)";
  public static String game$hotbar$compass$name = "§a§lTeleporter §7(Right Click)";
  public static String game$hotbar$quit_spectator$name = "§c§lReturn to Lobby §7(Right Click)";
  public static String game$hotbar$play_again$name = "§b§lPlay Again §7(Right Click)";

  public static String game$broadcast$starting$join = "{colored} §ahas joined (§b{on}§a/§b{max}§a)!";
  public static String game$broadcast$starting$left = "{colored} §chas left (§b{on}§c/§b{max}§c)!";
  public static String game$broadcast$starting$start = "§aThe game starts in {time} §asecond{s}!";
  public static String game$broadcast$starting$cage = "§aCages open in: {time} §asecond{s}!";
  public static String game$broadcast$starting$selected_kit = "§aSelected Kit: {kit}";
  public static String game$broadcast$starting$title = "§c{time}";
  public static String game$broadcast$starting$subtitle = "§aPrepare to fight!";
  public static String game$broadcast$started$teaming$solo = "§c§lTeaming is not allowed on Solo Mode!";
  public static String game$broadcast$started$teaming$doubles = "§c§lCross-Teaming is not allowed!";
  public static String game$broadcast$started$tutorial =
      "§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬\n{centered}§f§lSkyWars\n \n{centered}§a§lGather resources and equipment on your island\n{centered}§a§lin order to eliminate every other player.\n{centered}§a§lGo to the center island for special chests\n{centered}§a§lwith special items!\n \n§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬";
  public static String game$broadcast$started$tutorial_duels =
      "§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬\n{centered}§f§lSkyWars Duel\n \n{centered}§a§lEliminate your opponents!\n \n{centered}§f§lOpponent{s}: {opponents}\n \n§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬";
  public static String game$broadcast$started$title = "{type_color}{type} MODE";
  public static String game$broadcast$started$subtitle = "";
  public static String game$broadcast$ingame$action_bar$remaining = "§eThere are §c{alive} §eplayers remaining!";
  public static String game$broadcast$ingame$death_messages$suicide$normal = "{colored} §edied.";
  public static String game$broadcast$ingame$death_messages$suicide$void = "{colored} §efell into the void.";
  public static String game$broadcast$ingame$death_messages$killed$normal = "{colored} §ewas killed by {colored2}§e.";
  public static String game$broadcast$ingame$death_messages$killed$void = "{colored} §ewas thrown into the void by {colored2}§e.";
  public static String game$broadcast$ingame$death_messages$killed$bow = "{colored} §ewas shot by {colored2}§e.";
  public static String game$hologram$chest = "§e{time}";
  public static String game$player$ingame$receive_box = "§aYou've received a {stars} Star{s} Mystery Box!";
  public static String game$player$ingame$title$join$up = "§aSkyWars";
  public static String game$player$ingame$title$join$down = "{type_color}{type} Mode";
  public static String game$player$ingame$titles$refill$up = "";
  public static String game$player$ingame$titles$refill$bottom = "§aChests have been refilled!";
  public static String game$player$ingame$titles$die$up = "§c§lYOU DIED!";
  public static String game$player$ingame$titles$die$up_killed = "§c§lYOU DIED!";
  public static String game$player$ingame$titles$die$bottom = "§7You are now a spectator!";
  public static String game$player$ingame$titles$die$bottom_killed = "§7killed by {colored}";
  public static String game$player$ingame$titles$loser$up = "§c§lGAME END";
  public static String game$player$ingame$titles$loser$bottom = "§7You weren't victorious this time";
  public static String game$player$ingame$titles$winner$up = "§c§lYOU WIN";
  public static String game$player$ingame$titles$winner$bottom = "§7You are the last standing";
  public static String game$player$ingame$titles$border$up = "§c§lWARNING";
  public static String game$player$ingame$titles$border$bottom = "§7You are off the world border";
  public static String game$player$ingame$leader_board$template =
      "§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬\n{centered}§f§lSkyWars\n \n{centered}§aWinner §7- {winner}\n \n{centered}§a§l1st Killer §7- {top1} §7- {kills_top1}\n{centered}§6§l2nd Killer §7- {top2} §7- {kills_top2}\n{centered}§c§l3rd Killer §7- {top3} §7- {kills_top3}\n \n§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬";
  public static String game$player$ingame$leader_board$template_duels =
      "§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬\n{centered}§f§lSkyWars Duel\n \n{centered}§aWinner §7- {winner}\n \n{centered}§a§l1st Killer §7- {top1} §7- {kills_top1}\n{centered}§6§l2nd Killer §7- {top2} §7- {kills_top2}\n{centered}§c§l3rd Killer §7- {top3} §7- {kills_top3}\n \n§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬";
  public static String game$player$ingame$reward_summary$coins_per_play = "     §6+{coins} §7For play";
  public static String game$player$ingame$reward_summary$coins_per_kill = "     §6+{coins} §7For killing {kills} players";
  public static String game$player$ingame$reward_summary$coins_per_win = "     §6+{coins} §7For win the game";
  public static String game$player$ingame$reward_summary$exp_per_play = "     §3+{exp} §7For play";
  public static String game$player$ingame$reward_summary$exp_per_kill = "     §3+{exp} §7For killing {kills} players";
  public static String game$player$ingame$reward_summary$exp_per_win = "     §3+{exp} §7For win the game";
  // public static String game$player$ranked$reward_summary$points_per_kill = " §6+{points} §7For
  // killing {kills} players";
  // public static String game$player$ranked$reward_summary$points_per_win = " §6+{points} §7For win
  // the game";
  public static String game$player$ingame$reward_summary$template =
      "§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬\n{centered}§f§lReward Summary\n \n§6Total Earned Coins: {totalCoins}\n§3Total Earned Experience: {totalExp}\n \n§a▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬";
  public static String game$player$ranked$action_bar$points = "§6+{points} League Points!";

  public static String command$stats$args = "§cUse /stats <username>";
  public static String command$stats$permission = "§cYou must be §bMVP §cor higher to use this command!";
  public static String command$stats$user_not_found = "§cUser not found!";
  public static String command$watch$args = "§cUse /watch <username>";
  public static String command$watch$permission = "§cYou must be §bMVP §cor higher to use this command!";
  public static String command$watch$user_not_found = "§cUser not found!";
  public static String command$watch$user_not_in_match = "§cUser isn't playing a match.";
  public static String command$join_game$permission = "§cYou must be §bMVP §cor higher to use this command!";
  public static String command$join_game$args = "§cUse /joingame <world>";
  public static String command$join_game$game_not_found = "§cInvalid world name.";
  public static String command$join_game$game_already_started = "§cGame already started.";

  public static LostLogger LOGGER;
  private static ConfigUtils CONFIG;

  public static void setupLanguage() {
    LOGGER = Main.LOGGER.getModule("Language");
    CONFIG = ConfigUtils.getConfig("lang");

    boolean save = false;
    LanguageWriter writer = new LanguageWriter(CONFIG.getFile());
    for (Field field : Language.class.getDeclaredFields()) {
      if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers()) && !field.getName().equals("LOGGER")
          && !field.getName().equals("CONFIG")) {
        String nativeName = field.getName().replace("$", ".").replace("_", "-");

        try {
          Object value = null;

          if (CONFIG.contains(nativeName)) {
            value = CONFIG.get(nativeName);
            if (value instanceof String) {
              value = StringUtils.formatColors((String) value).replace("\\n", "\n");
            } else if (value instanceof List) {
              List l = (List) value;
              List<Object> list = new ArrayList<>(l.size());
              for (Object v : l) {
                if (v instanceof String) {
                  list.add(StringUtils.formatColors((String) v).replace("\\n", "\n"));
                } else {
                  list.add(v);
                }
              }

              l = null;
              value = list;
            }

            field.set(null, value);
            writer.set(nativeName, CONFIG.get(nativeName));
          } else {
            value = field.get(null);
            if (value instanceof String) {
              value = StringUtils.deformatColors((String) value).replace("\n", "\\n");
            } else if (value instanceof List) {
              List l = (List) value;
              List<Object> list = new ArrayList<>(l.size());
              for (Object v : l) {
                if (v instanceof String) {
                  list.add(StringUtils.deformatColors((String) v).replace("\n", "\\n"));
                } else {
                  list.add(v);
                }
              }

              l = null;
              value = list;
            }

            save = true;
            writer.set(nativeName, value);
          }
        } catch (ReflectiveOperationException e) {
          LOGGER.log(LostLevel.WARNING, "Unexpected error on language file: ", e);
        }
      }
    }

    if (save && Core.MODE == CoreMode.MULTI_ARENA) {
      writer.write();
      LOGGER.info("Lang.yml modified or created.");
      CONFIG.reload();
    }

    for (CosmeticRarity rarity : CosmeticRarity.values()) {
      rarity.translate();
    }
    for (SkyWarsMode mode : SkyWarsMode.values()) {
      mode.translate();
    }
    for (SkyWarsType type : SkyWarsType.values()) {
      type.translate();
    }
    LeaderBoardStats.translate();
  }

  public static void reload() {
    LanguageWriter writer = new LanguageWriter(CONFIG.getFile());
    for (Field field : Language.class.getDeclaredFields()) {
      if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
        String nativeName = field.getName().replace("$", ".").replace("_", "-");

        try {
          Object value = field.get(null);
          if (value instanceof String) {
            value = StringUtils.deformatColors((String) value).replace("\n", "\\n");
          } else if (value instanceof List) {
            List l = (List) value;
            List<Object> list = new ArrayList<>(l.size());
            for (Object v : l) {
              if (v instanceof String) {
                list.add(StringUtils.deformatColors((String) v).replace("\n", "\\n"));
              } else {
                list.add(v);
              }
            }

            l = null;
            value = list;
          }

          writer.set(nativeName, value);
        } catch (ReflectiveOperationException e) {
          LOGGER.log(LostLevel.WARNING, "Unexpected error on language file: ", e);
        }
      }
    }

    writer.write();
  }
}
