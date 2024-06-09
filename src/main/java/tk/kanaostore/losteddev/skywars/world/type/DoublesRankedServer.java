package tk.kanaostore.losteddev.skywars.world.type;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.api.event.game.SkyWarsGameEndEvent;
import tk.kanaostore.losteddev.skywars.api.event.game.SkyWarsGameStartEvent;
import tk.kanaostore.losteddev.skywars.api.event.player.SkyWarsPlayerDeathEvent;
import tk.kanaostore.losteddev.skywars.api.event.player.SkyWarsPlayerDeathEvent.SkyWarsDeathCause;
import tk.kanaostore.losteddev.skywars.api.event.player.SkyWarsPlayerJoinEvent;
import tk.kanaostore.losteddev.skywars.api.event.player.SkyWarsPlayerQuitEvent;
import tk.kanaostore.losteddev.skywars.api.event.player.SkyWarsPlayerWatchEvent;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsState;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsTeam;
import tk.kanaostore.losteddev.skywars.cosmetics.Cosmetic;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticServer;
import tk.kanaostore.losteddev.skywars.cosmetics.CosmeticType;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsCage;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsDeathCry;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsKit;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.nms.NMS;
import tk.kanaostore.losteddev.skywars.nms.Sound;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.rank.Rank;
import tk.kanaostore.losteddev.skywars.ranked.Ranked;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsChest;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsMode;
import tk.kanaostore.losteddev.skywars.ui.server.ScanCallback;
import tk.kanaostore.losteddev.skywars.utils.FontUtils;
import tk.kanaostore.losteddev.skywars.utils.PlayerUtils;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;
import tk.kanaostore.losteddev.skywars.world.WorldRegeneration;
import tk.kanaostore.losteddev.skywars.world.WorldServer;

@SuppressWarnings("deprecation")
public class DoublesRankedServer extends WorldServer<SkyWarsTeam> {

  private List<UUID> players, spectators;
  private Map<String, Integer> kills;

  public DoublesRankedServer(String yaml, ScanCallback callback) {
    super(yaml, callback);

    this.kills = new HashMap<>();
    this.players = new ArrayList<>();
    this.spectators = new ArrayList<>();
  }

  public void killLeave(Account account, Account ack) {
    Player player = account.getPlayer();
    Player killer = ack != null ? ack.getPlayer() : null;

    if (killer != null && player.equals(killer)) {
      killer = null;
    }

    SkyWarsDeathCause cause = null;
    if (killer == null) {
      if (player.getLastDamageCause() != null && player.getLastDamageCause().getCause() == DamageCause.VOID) {
        cause = SkyWarsDeathCause.SUICIDE_VOID;
        broadcast(PlayerUtils.replaceAll(player, Language.game$broadcast$ingame$death_messages$suicide$void));
      } else {
        cause = SkyWarsDeathCause.SUICIDE;
        broadcast(PlayerUtils.replaceAll(player, Language.game$broadcast$ingame$death_messages$suicide$normal));
      }
    } else {
      addKills(killer);
      // general
      Ranked.increase(ack, "kills");
      if (player.getLastDamageCause() != null && player.getLastDamageCause().getCause() == DamageCause.VOID) {
        cause = SkyWarsDeathCause.KILLED_VOID;
        Ranked.increase(ack, "void");
        broadcast(PlayerUtils.replaceAll(player, killer, Language.game$broadcast$ingame$death_messages$killed$void));
      } else if (player.getLastDamageCause() != null && player.getLastDamageCause() instanceof EntityDamageByEntityEvent
          && ((EntityDamageByEntityEvent) player.getLastDamageCause()).getDamager() instanceof Arrow) {
        cause = SkyWarsDeathCause.KILLED_BOW;
        Ranked.increase(ack, "bow");
        broadcast(PlayerUtils.replaceAll(player, killer, Language.game$broadcast$ingame$death_messages$killed$bow));
      } else {
        cause = SkyWarsDeathCause.KILLED_MELEE;
        Ranked.increase(ack, "melee");
        broadcast(PlayerUtils.replaceAll(player, killer, Language.game$broadcast$ingame$death_messages$killed$normal));
      }

      int amount = Language.game$rewards$coins_per_kill;
      ack.addStat("coins", amount);
      for (Player teammate : getTeam(killer).getMembers()) {
        Ranked.increase(Database.getInstance().getAccount(teammate.getUniqueId()), "points", Ranked.rewards$points$doubles$per_kill);
        NMS.sendActionBar(teammate, Language.game$player$ranked$action_bar$points.replace("{points}", String.valueOf(Ranked.rewards$points$doubles$per_kill)));
      }
      if (account.getInt("souls") < account.getContainers("account").get("sw_maxsouls").getAsInt()) {
        account.addStat("souls");
      }
    }

    Ranked.increase(account, "deaths");
    Ranked.increase(account, "plays");
    int play = Language.game$rewards$coins_per_play;
    double expPlay = Language.game$rewards$exp_per_play;
    account.addStat("coins", play);
    account.addExp(expPlay);
    SkyWarsDeathCry cry = (SkyWarsDeathCry) account.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_DEATHCRY, 1);
    if (cry != null) {
      cry.getSound().play(player.getLocation(), cry.getVolume(), cry.getPitch());
    }
    Bukkit.getPluginManager().callEvent(new SkyWarsPlayerDeathEvent(this, player, killer, cause));
    // this.broadcastAction(Language.game$broadcast$ingame$action_bar$remaining.replace("{alive}",
    // String.valueOf(this.getAlive())));
    this.updateTags();
    this.check();
  }

  @Override
  public void kill(Account account, Account ack) {
    Player player = account.getPlayer();
    SkyWarsTeam team = getTeam(player);

    if (!isAlive(player) || team == null) {
      account.refreshPlayer();
      return;
    }

    Player killer = ack != null ? ack.getPlayer() : null;

    if (killer != null && player.equals(killer)) {
      killer = null;
    }

    SkyWarsDeathCause cause = null;
    if (killer == null) {
      if (player.getLastDamageCause() != null && player.getLastDamageCause().getCause() == DamageCause.VOID) {
        cause = SkyWarsDeathCause.SUICIDE_VOID;
        broadcast(PlayerUtils.replaceAll(player, Language.game$broadcast$ingame$death_messages$suicide$void));
      } else {
        cause = SkyWarsDeathCause.SUICIDE;
        broadcast(PlayerUtils.replaceAll(player, Language.game$broadcast$ingame$death_messages$suicide$normal));
      }
    } else {
      addKills(killer);
      // general
      Ranked.increase(ack, "kills");
      if (player.getLastDamageCause() != null && player.getLastDamageCause().getCause() == DamageCause.VOID) {
        cause = SkyWarsDeathCause.KILLED_VOID;
        Ranked.increase(ack, "void");
        broadcast(PlayerUtils.replaceAll(player, killer, Language.game$broadcast$ingame$death_messages$killed$void));
      } else if (player.getLastDamageCause() != null && player.getLastDamageCause() instanceof EntityDamageByEntityEvent
          && ((EntityDamageByEntityEvent) player.getLastDamageCause()).getDamager() instanceof Arrow) {
        cause = SkyWarsDeathCause.KILLED_BOW;
        Ranked.increase(ack, "bow");
        broadcast(PlayerUtils.replaceAll(player, killer, Language.game$broadcast$ingame$death_messages$killed$bow));
      } else {
        cause = SkyWarsDeathCause.KILLED_MELEE;
        Ranked.increase(ack, "melee");
        broadcast(PlayerUtils.replaceAll(player, killer, Language.game$broadcast$ingame$death_messages$killed$normal));
      }

      int amount = Language.game$rewards$coins_per_kill;
      ack.addStat("coins", amount);
      for (Player teammate : getTeam(killer).getMembers()) {
        Ranked.increase(Database.getInstance().getAccount(teammate.getUniqueId()), "points", Ranked.rewards$points$doubles$per_kill);
        NMS.sendActionBar(teammate, Language.game$player$ranked$action_bar$points.replace("{points}", String.valueOf(Ranked.rewards$points$doubles$per_kill)));
      }
      if (account.getInt("souls") < account.getContainers("account").get("sw_maxsouls").getAsInt()) {
        account.addStat("souls");
      }
    }

    Ranked.increase(account, "deaths");
    Ranked.increase(account, "plays");
    int play = Language.game$rewards$coins_per_play;
    double expPlay = Language.game$rewards$exp_per_play;
    account.addStat("coins", play);
    account.addExp(expPlay);
    Location returns = team.getLocation();
    Location dieLocation = player.getLocation();
    team.removeMember(player);
    players.remove(player.getUniqueId());
    spectators.add(player.getUniqueId());
    for (Player players : getPlayers(true)) {
      if (isSpectator(players)) {
        player.showPlayer(players);
      } else {
        players.hidePlayer(player);
      }
    }

    final Player killerFinal = killer;
    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
      player.teleport(returns);
      account.refreshPlayer();
      NMS.sendTitle(player, killerFinal != null ? PlayerUtils.replaceAll(killerFinal, Language.game$player$ingame$titles$die$up_killed) : Language.game$player$ingame$titles$die$up,
          killerFinal != null ? PlayerUtils.replaceAll(killerFinal, Language.game$player$ingame$titles$die$bottom_killed) : Language.game$player$ingame$titles$die$bottom, 20, 60,
          20);
      int coinsPerKill = this.getKills(player) * Language.game$rewards$coins_per_kill;
      double expPerKill = this.getKills(player) * Language.game$rewards$exp_per_kill;

      List<String> sb = new ArrayList<>();
      for (String line : Language.game$player$ingame$reward_summary$template.split("\n")) {
        boolean coins = line.contains("{totalCoins}"), exp = line.contains("{totalExp}");
        line = line.replace("{totalCoins}", String.valueOf(play + coinsPerKill));
        line = line.replace("{totalExp}", StringUtils.formatOneDecimal(expPlay + expPerKill));

        if (line.startsWith("{centered}")) {
          line = FontUtils.center(line.replace("{centered}", ""));
        }
        sb.add(line);

        if (coins) {
          if (play > 0) {
            sb.add(Language.game$player$ingame$reward_summary$coins_per_play.replace("{coins}", String.valueOf(play)));
          }
          if (coinsPerKill > 0) {
            sb.add(Language.game$player$ingame$reward_summary$coins_per_kill.replace("{coins}", String.valueOf(coinsPerKill)).replace("{kills}",
                String.valueOf(this.getKills(player))));
          }
        }
        if (exp) {
          if (expPlay > 0.0) {
            sb.add(Language.game$player$ingame$reward_summary$exp_per_play.replace("{exp}", StringUtils.formatOneDecimal(expPlay)));
          }
          if (expPerKill > 0.0) {
            sb.add(Language.game$player$ingame$reward_summary$exp_per_kill.replace("{exp}", StringUtils.formatOneDecimal(expPerKill)).replace("{kills}",
                String.valueOf(this.getKills(player))));
          }
        }
      }

      player.sendMessage(StringUtils.join(sb, "\n"));
      sb.clear();
      sb = null;
    }, 3);

    SkyWarsDeathCry cry = (SkyWarsDeathCry) account.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_DEATHCRY, 1);
    if (cry != null) {
      cry.getSound().play(dieLocation, cry.getVolume(), cry.getPitch());
    }
    Bukkit.getPluginManager().callEvent(new SkyWarsPlayerDeathEvent(this, player, killer, cause));
    // this.broadcastAction(Language.game$broadcast$ingame$action_bar$remaining.replace("{alive}",
    // String.valueOf(this.getAlive())));
    this.updateTags();
    this.check();
  }

  @Override
  public void spectate(Account account, Player target) {
    Player player = account.getPlayer();

    account.setServer(this);
    spectators.add(player.getUniqueId());
    account.refreshPlayer();
    player.teleport(target.getLocation());
    for (Player players : Bukkit.getOnlinePlayers()) {
      if (!players.getWorld().equals(player.getWorld())) {
        player.hidePlayer(players);
        players.hidePlayer(player);
        continue;
      }

      if (!isSpectator(players)) {
        player.showPlayer(players);
        players.hidePlayer(player);
      } else {
        player.showPlayer(players);
        players.showPlayer(player);
      }
    }

    Bukkit.getPluginManager().callEvent(new SkyWarsPlayerWatchEvent(this, player, target));
    this.updateTags();
  }

  @Override
  public void connect(Account account, String... skipParty) {
    Player player = account.getPlayer();
    if (player == null || !getState().canJoin() || players.size() >= getMaxPlayers()) {
      return;
    }

    if (account.getServer() != null && account.getServer().equals(this)) {
      return;
    }

    SkyWarsTeam team = null;
    boolean fullSize = false;
    if (Main.lostparties) {
      if (skipParty.length == 0) {
        io.github.losteddev.parties.api.Party party = io.github.losteddev.parties.api.Party.getPartyByMember(player);
        if (party != null) {
          if (!party.getOwnerName().equalsIgnoreCase(player.getName())) {
            player.sendMessage(Language.lobby$connecting$party$not_leader);
            return;
          }

          if (party.online() + players.size() > getMaxPlayers()) {
            return;
          }

          fullSize = true;
          List<Player> players = party.getPlayers(false);
          Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> players.forEach(member -> {
            Account accounts = Database.getInstance().getAccount(member.getUniqueId());
            if (accounts != null) {
              connect(accounts, "");
            }
          }), 20L);
        }
      } else {
        io.github.losteddev.parties.api.Party party = io.github.losteddev.parties.api.Party.getPartyByMember(player);
        if (party != null) {
          for (SkyWarsTeam t : teams) {
            if (t.canJoin()) {
              boolean find = false;
              for (Player players : party.getPlayers(true)) {
                if (t.hasMember(players)) {
                  find = true;
                  break;
                }
              }

              if (find) {
                t.addMember(player);
                team = t;
              }
            }
          }
        }
      }
    }


    team = team == null ? getAvaibleTeam(player, fullSize ? 2 : 1) : team;
    if (team == null) {
      return;
    }

    if (account.getServer() != null) {
      account.getServer().disconnect(account, account.getServer().isSpectator(player) ? "-play" : "");
    }

    players.add(player.getUniqueId());
    account.setServer(this);

    if (team.getMembers().size() == 1) {
      SkyWarsCage.def(team.getLocation(), true);
    }

    player.teleport(this.getConfig().hasWaitingLobby() ? this.getConfig().getWaitingLocation() : team.getLocation().add(0, 1, 0));
    account.reloadScoreboard();
    account.refreshPlayer();
    for (Player players : Bukkit.getOnlinePlayers()) {
      if (!players.getWorld().equals(player.getWorld())) {
        player.hidePlayer(players);
        players.hidePlayer(player);
        continue;
      }

      if (isSpectator(players)) {
        player.hidePlayer(players);
        players.showPlayer(player);
      } else {
        player.showPlayer(players);
        players.showPlayer(player);
      }
    }

    Bukkit.getPluginManager().callEvent(new SkyWarsPlayerJoinEvent(this, player));
    this.updateTags();
    NMS.sendTitle(player,
        Language.game$player$ingame$title$join$up.replace("{type_color}", StringUtils.getFirstColor(this.getType().getColoredName())).replace("{type}",
            StringUtils.stripColors(this.getType().getColoredName())),
        Language.game$player$ingame$title$join$down.replace("{type_color}", StringUtils.getFirstColor(this.getType().getColoredName())).replace("{type}",
            StringUtils.stripColors(this.getType().getColoredName())));
    this.broadcast(PlayerUtils.replaceAll(player,
        Language.game$broadcast$starting$join.replace("{on}", String.valueOf(this.getOnline())).replace("{max}", String.valueOf(this.getMaxPlayers()))));
    if (getTimer() > Language.game$countdown$full && this.getOnline() == this.getMaxPlayers()) {
      this.setTimer(Language.game$countdown$full);
    }
  }

  @Override
  public void disconnect(Account account) {
    this.disconnect(account, "");
  }

  @Override
  public void disconnect(Account account, String options) {
    Player player = account.getPlayer();
    if (!account.getServer().equals(this)) {
      return;
    }

    SkyWarsTeam team = getTeam(player);
    if (team != null) {
      team.removeMember(player);
      if (this.getState().canJoin() && team.getMembers().isEmpty()) {
        team.destroy();
      }
    }

    boolean alive = players.contains(player.getUniqueId());
    players.remove(player.getUniqueId());
    spectators.remove(player.getUniqueId());

    if (options.equals("-quit")) {
      if (this.getState().canJoin()) {
        this.broadcast(PlayerUtils.replaceAll(player,
            Language.game$broadcast$starting$left.replace("{on}", String.valueOf(this.getOnline())).replace("{max}", String.valueOf(this.getMaxPlayers()))));
      }

      if (alive && state == SkyWarsState.INGAME) {
        List<Account> hitters = account.getLastHitters();
        Account killer = hitters.size() > 0 ? hitters.get(0) : null;
        this.killLeave(account, killer);
        for (Account hitter : hitters) {
          if (hitter != null && (killer == null || !hitter.equals(killer)) && (hitter.getServer() != null && hitter.getServer().equals(this)) && hitter.getPlayer() != null
              && !this.isSpectator(hitter.getPlayer())) {
            Ranked.increase(hitter, "assists");
          }
        }
      }

      account.setServer(null);
      this.check();
      return;
    }

    if (options.equalsIgnoreCase("-play")) {
      account.setServer(null);
      this.updateTags();
      this.check();
      return;
    }

    if (alive && state == SkyWarsState.INGAME) {
      List<Account> hitters = account.getLastHitters();
      Account killer = hitters.size() > 0 ? hitters.get(0) : null;
      this.killLeave(account, killer);
      for (Account hitter : hitters) {
        if (hitter != null && (killer == null || !hitter.equals(killer)) && (hitter.getServer() != null && hitter.getServer().equals(this)) && hitter.getPlayer() != null
            && !this.isSpectator(hitter.getPlayer())) {
          hitter.addStat("teamassists");
        }
      }
    }

    account.setServer(null);
    this.updateTags();

    account.reloadScoreboard();
    account.refreshPlayer();
    account.refreshPlayers();
    if (this.getState().canJoin()) {
      this.broadcast(PlayerUtils.replaceAll(player,
          Language.game$broadcast$starting$left.replace("{on}", String.valueOf(this.getOnline())).replace("{max}", String.valueOf(this.getMaxPlayers()))));
    }

    Bukkit.getPluginManager().callEvent(new SkyWarsPlayerQuitEvent(this, player));
    this.check();
  }

  @Override
  public void start() {
    if (this.getConfig().hasWaitingLobby()) {
      if (this.getState() == SkyWarsState.WAITING) {
        this.setState(SkyWarsState.STARTING);
        this.task.switchTask();

        for (Player player : getPlayers(false)) {
          Account account = Database.getInstance().getAccount(player.getUniqueId());
          if (account == null) {
            player.kickPlayer("§c§lSKY WARS\n \n§cError.");
          } else {
            account.reloadScoreboard();
            account.refreshPlayer();
            player.teleport(this.getTeam(player).getLocation().add(0, 1, 0));
          }
        }
        return;
      }
    }
    this.setState(SkyWarsState.INGAME);
    this.task.switchTask();

    List<String> sb = new ArrayList<>();
    for (String line : Language.game$broadcast$started$tutorial.split("\n")) {
      if (line.startsWith("{centered}")) {
        line = FontUtils.center(line.replace("{centered}", ""));
      }

      sb.add(line);
    }

    this.broadcast(StringUtils.join(sb, "\n"));
    this.broadcast(Language.game$broadcast$started$teaming$doubles);
    this.broadcastTitle(
        Language.game$broadcast$started$title.replace("{type_color}", StringUtils.getFirstColor(this.getType().getColoredName())).replace("{type}",
            StringUtils.stripColors(this.getType().getColoredName().toUpperCase())),
        Language.game$broadcast$started$subtitle.replace("{type_color}", StringUtils.getFirstColor(this.getType().getColoredName())).replace("{type}",
            StringUtils.stripColors(this.getType().getColoredName().toUpperCase())));
    teams.forEach(SkyWarsTeam::destroy);
    chests.forEach(SkyWarsChest::fill);

    for (Player player : getPlayers(false)) {
      kills.put(player.getDisplayName(), 0);
      Account account = Database.getInstance().getAccount(player.getUniqueId());
      if (account == null) {
        player.kickPlayer("§c§lSKY WARS\n \n§cError.");
      } else {
        account.reloadScoreboard();
        account.refreshPlayer();
        Ranked.decrease(account, "points", Ranked.getLeague(account).getFare());
        Cosmetic cosmetic = account.getSelected(CosmeticServer.SKYWARS, CosmeticType.SKYWARS_KIT, this.getType().getIndex());
        if (cosmetic != null && cosmetic instanceof SkyWarsKit) {
          ((SkyWarsKit) cosmetic).apply(player);
        } else {
          if (Language.options$game$default_kit) {
            player.getInventory().addItem(new ItemStack(Material.matchMaterial("WOOD_PICKAXE")), new ItemStack(Material.matchMaterial("WOOD_AXE")),
                new ItemStack(Material.matchMaterial("WOOD_SPADE")));
          }
        }
        player.setNoDamageTicks(80);

        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 100, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 1));
        Sound.PORTAL_TRIGGER.play(player, 1.0F, 1.0F);
      }
    }

    Bukkit.getPluginManager().callEvent(new SkyWarsGameStartEvent(this));
    this.updateTags();
    this.check();
  }

  private void check() {
    if (this.getState() != SkyWarsState.INGAME) {
      return;
    }

    List<SkyWarsTeam> teams = this.getAliveTeams();
    if (teams.size() <= 1) {
      if (teams.size() == 0) {
        this.stop(null);
        Bukkit.getPluginManager().callEvent(new SkyWarsGameEndEvent(this, null));
        return;
      }

      this.setState(SkyWarsState.ENDED);
      SkyWarsTeam winner = teams.get(0);
      this.getPlayers(true).forEach(players -> {
        boolean loser = !winner.hasMember(players);
        if (loser) {
          NMS.sendTitle(players, Language.game$player$ingame$titles$loser$up, Language.game$player$ingame$titles$loser$bottom, 20, 80, 20);
        } else {
          NMS.sendTitle(players, Language.game$player$ingame$titles$winner$up, Language.game$player$ingame$titles$winner$bottom, 20, 80, 20);
        }

        Database.getInstance().getAccount(players.getUniqueId()).getScoreboard().update();
      });
      this.stop(winner);
      for (Player player : winner.getMembers()) {
        Account account = Database.getInstance().getAccount(player.getUniqueId());
        Ranked.increase(account, "wins");
        Ranked.increase(account, "points", Ranked.rewards$points$doubles$per_win);
        NMS.sendActionBar(player, Language.game$player$ranked$action_bar$points.replace("{points}", String.valueOf(Ranked.rewards$points$doubles$per_win)));

        if (this.isAlive(player)) {
          this.players.remove(player.getUniqueId());
          this.spectators.add(player.getUniqueId());
          account.refreshPlayer();
          Ranked.increase(account, "plays");

          int amount = Language.game$rewards$coins_per_win, play = Language.game$rewards$coins_per_play;
          double expAmount = Language.game$rewards$exp_per_win, expPlay = Language.game$rewards$exp_per_play;

          // PLAY
          account.addStat("coins", play);
          account.addExp(expPlay);
          // WIN
          account.addStat("coins", amount);
          for (int i = 0; i < account.getContainers("account").get("sw_soulswin").getAsInt(); i++) {
            if (account.getInt("souls") < account.getContainers("account").get("sw_maxsouls").getAsInt()) {
              account.addStat("souls");
            }
          }

          account.addExp(expAmount);
          Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            int coinsPerKill = this.getKills(player) * Language.game$rewards$coins_per_kill;
            double expPerKill = this.getKills(player) * Language.game$rewards$exp_per_kill;

            List<String> sb = new ArrayList<>();
            for (String line : Language.game$player$ingame$reward_summary$template.split("\n")) {
              boolean coins = line.contains("{totalCoins}"), exp = line.contains("{totalExp}");
              line = line.replace("{totalCoins}", String.valueOf(play + amount + coinsPerKill));
              line = line.replace("{totalExp}", StringUtils.formatOneDecimal(expPlay + expAmount + expPerKill));

              if (line.startsWith("{centered}")) {
                line = FontUtils.center(line.replace("{centered}", ""));
              }
              sb.add(line);

              if (coins) {
                if (play > 0) {
                  sb.add(Language.game$player$ingame$reward_summary$coins_per_play.replace("{coins}", String.valueOf(play)));
                }
                if (coinsPerKill > 0) {
                  sb.add(Language.game$player$ingame$reward_summary$coins_per_kill.replace("{coins}", String.valueOf(coinsPerKill)).replace("{kills}",
                      String.valueOf(this.getKills(player))));
                }
                sb.add(Language.game$player$ingame$reward_summary$coins_per_win.replace("{coins}", String.valueOf(amount)));
              }
              if (exp) {
                if (expPlay > 0.0) {
                  sb.add(Language.game$player$ingame$reward_summary$exp_per_play.replace("{exp}", StringUtils.formatOneDecimal(expPlay)));
                }
                if (expPerKill > 0.0) {
                  sb.add(Language.game$player$ingame$reward_summary$exp_per_kill.replace("{exp}", StringUtils.formatOneDecimal(expPerKill)).replace("{kills}",
                      String.valueOf(this.getKills(player))));
                }
                sb.add(Language.game$player$ingame$reward_summary$exp_per_win.replace("{exp}", StringUtils.formatOneDecimal(expAmount)));
              }
            }

            player.sendMessage(StringUtils.join(sb, "\n"));
            sb.clear();
            sb = null;
          }, 20);
        }
      }

      Bukkit.getPluginManager().callEvent(new SkyWarsGameEndEvent(this, winner));
    }
  }

  @Override
  public void stop(SkyWarsTeam winner) {
    this.setState(SkyWarsState.ENDED);

    List<String> sb = new ArrayList<>();
    List<String> keys = kills.keySet().stream().sorted(Comparator.comparing(parent -> kills.get(parent), Comparator.reverseOrder())).collect(Collectors.toList());
    while (keys.size() < 3) {
      keys.add("§7None");
    }

    for (String line : Language.game$player$ingame$leader_board$template.split("\n")) {
      line = line.replace("{winner}", winner == null ? "§7None" : "§a" + winner.getAlphabeticalTag());

      line = line.replace("{top1}", keys.get(0));
      line = line.replace("{top2}", keys.get(1));
      line = line.replace("{top3}", keys.get(2));

      line = line.replace("{kills_top1}", String.valueOf(this.getKills(keys.get(0))));
      line = line.replace("{kills_top2}", String.valueOf(this.getKills(keys.get(1))));
      line = line.replace("{kills_top3}", String.valueOf(this.getKills(keys.get(2))));
      if (line.startsWith("{centered}")) {
        line = FontUtils.center(line.replace("{centered}", ""));
      }

      sb.add(line);
    }
    this.broadcast(StringUtils.join(sb, "\n"));
    sb.clear();
    sb = null;

    this.getTask().switchTask(winner != null ? winner.getMembers().toArray(new Player[winner.getSize()]) : new Player[] {});
  }

  @Override
  public void reset() {
    this.kills.clear();
    this.players.clear();
    this.spectators.clear();
    this.getTask().cancel();
    this.teams.forEach(SkyWarsTeam::reset);
    this.chests.forEach(SkyWarsChest::destroy);
    WorldRegeneration.rollBack(this);
  }

  @Override
  public void broadcast(String message) {
    this.broadcast(message, true);
  }

  @Override
  public void broadcastAction(String message) {
    getPlayers(true).forEach(player -> {
      NMS.sendActionBar(player, message);
    });
  }

  @Override
  public void broadcastTitle(String title, String subtitle) {
    getPlayers(true).forEach(player -> {
      NMS.sendTitle(player, title, subtitle, 0, 60, 0);
    });
  }

  @Override
  public void broadcast(String message, boolean spectators) {
    getPlayers(spectators).forEach(player -> {
      player.sendMessage(StringUtils.formatColors(message));
    });
  }

  @Override
  public void updateScoreboards() {
    getPlayers(true).forEach(player -> {
      if (this.getState() != SkyWarsState.WAITING && this.getState() != SkyWarsState.STARTING && !this.getConfig().getWorldCube().contains(player.getLocation())) {
        if (this.isSpectator(player)) {
          player.teleport(this.getConfig().getWorldCube().getCenterLocation());
        } else if (player.getLocation().getY() > 1) {
          NMS.sendTitle(player, Language.game$player$ingame$titles$border$up, Language.game$player$ingame$titles$border$bottom, 0, 20, 0);
          player.damage(1.0D);
        }
      }

      Database.getInstance().getAccount(player.getUniqueId()).getScoreboard().update();
    });
  }

  private void updateTags() {
    if (this.getState().canJoin()) {
      for (Player player : getPlayers(true)) {
        Scoreboard scoreboard = player.getScoreboard();

        for (Player players : getPlayers(true)) {
          Team team = scoreboard.getTeam(players.getUniqueId().toString().replace("-", "").substring(0, 16));
          if (team == null) {
            team = scoreboard.registerNewTeam(players.getUniqueId().toString().replace("-", "").substring(0, 16));
          }

          team.setPrefix(StringUtils.getLastColor(Rank.getRank(players).getPrefix()));
          if (!team.hasEntry(players.getName())) {
            team.addEntry(players.getName());
          }
        }
      }

      return;
    }

    for (Player player : getPlayers(true)) {
      Scoreboard scoreboard = player.getScoreboard();

      for (Player players : getPlayers(true)) {
        if (isSpectator(players)) {
          Team team = scoreboard.getTeam(players.getUniqueId().toString().replace("-", "").substring(0, 16));
          if (team != null) {
            team.unregister();
          }

          team = scoreboard.getTeam("spec");
          if (team == null) {
            team = scoreboard.registerNewTeam("spec");
            team.setPrefix("§7");
          }

          if (!team.hasEntry(players.getName())) {
            team.addEntry(players.getName());
          }
        } else {
          Team team = scoreboard.getTeam(players.getUniqueId().toString().replace("-", "").substring(0, 16));
          if (team == null) {
            SkyWarsTeam st = getTeam(players);
            team = scoreboard.registerNewTeam(players.getUniqueId().toString().replace("-", "").substring(0, 16));
            team.setPrefix(Ranked.getTag(players) + (st.hasMember(player) ? "§a" : "§c"));
            team.addEntry(players.getName());
          }
        }
      }
    }
  }

  @Override
  public String getServerName() {
    return config.getWorld().getName();
  }

  @Override
  public SkyWarsMode getMode() {
    return SkyWarsMode.DOUBLES;
  }

  @Override
  public boolean isAlive(Player player) {
    return players.contains(player.getUniqueId());
  }

  @Override
  public boolean isSpectator(Player player) {
    return spectators.contains(player.getUniqueId());
  }

  public void addKills(Player player) {
    kills.put(player.getDisplayName(), getKills(player) + 1);
  }

  @Override
  public int getKills(Player player) {
    return kills.get(player.getDisplayName()) != null ? kills.get(player.getDisplayName()) : 0;
  }

  private int getKills(String name) {
    return kills.get(name) != null ? kills.get(name) : 0;
  }

  @Override
  public int getOnline() {
    return players.size() + spectators.size();
  }

  @Override
  public int getMaxPlayers() {
    return super.getMaxPlayers() * 2;
  }

  @Override
  public int getAlive() {
    return players.size();
  }

  public List<Player> getPlayers(boolean spectators) {
    List<Player> players = new ArrayList<>(spectators ? this.spectators.size() + this.players.size() : this.players.size());
    this.players.stream().filter(id -> Bukkit.getPlayer(id) != null).forEach(id -> players.add(Bukkit.getPlayer(id)));
    if (spectators) {
      this.spectators.stream().filter(id -> Bukkit.getPlayer(id) != null).forEach(id -> players.add(Bukkit.getPlayer(id)));
    }

    return players;
  }
}
