package tk.kanaostore.losteddev.skywars.listeners.entity;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsServer;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsState;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsTeam;
import tk.kanaostore.losteddev.skywars.cmd.sw.SetLobbyCommand;
import tk.kanaostore.losteddev.skywars.cosmetics.skywars.SkyWarsPerk;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.MinecraftVersion;

@SuppressWarnings("deprecation")
public class EntityListener implements Listener {

  @EventHandler
  public void onPlayerFish(ProjectileHitEvent evt) {
    if (MinecraftVersion.getCurrentVersion().getCompareId() > 183) {
      if (evt.getEntity() != null && evt.getHitEntity() != null && evt.getEntity().getShooter() != evt.getHitEntity() && evt.getEntity() instanceof FishHook) {
        Player player = (Player) evt.getEntity().getShooter();

        SkyWarsServer server = null;
        Account account = Database.getInstance().getAccount(player.getUniqueId());
        if (account == null || (server = account.getServer()) == null || server.getState() != SkyWarsState.INGAME || server.isSpectator(player)) {
          return;
        }

        SkyWarsTeam team = server.getTeam(player);

        Player damaged = null;
        Account account2 = null;
        if (evt.getHitEntity() instanceof Player) {
          damaged = (Player) evt.getHitEntity();
          account2 = Database.getInstance().getAccount(damaged.getUniqueId());
          if (account2 == null || account2.getServer() == null || !account2.getServer().equals(server) || server.isSpectator(damaged) || (team != null && team.hasMember(damaged))
              || damaged.equals(player)) {
            return;
          }

          if (damaged.getNoDamageTicks() < 7) {
            if (!damaged.getLocation().getBlock().getType().name().contains("AIR")) {
              damaged.setNoDamageTicks(0);
            }

            damaged.damage(0.001D, player);
            double kx = evt.getEntity().getLocation().getDirection().getX() / 2.5D, kz = evt.getEntity().getLocation().getDirection().getZ() / 2.5D;
            kx *= 2.0;
            damaged.setVelocity(new Vector(kx, 0.372, kz));
            damaged.setNoDamageTicks(17);
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onEntityDamageByEntity(EntityDamageByEntityEvent evt) {
    if (evt.isCancelled()) {
      return;
    }

    if (evt.getEntity() instanceof Player) {
      Player player = (Player) evt.getEntity();

      SkyWarsServer server = null;
      Account account = Database.getInstance().getAccount(player.getUniqueId());
      if (account == null || (server = account.getServer()) == null || server.getState() != SkyWarsState.INGAME || server.isSpectator(player)) {
        evt.setCancelled(true);
        return;
      }

      SkyWarsTeam team = server.getTeam(player);

      Player damager = null;
      Account account2 = null;
      if (evt.getDamager() instanceof Player) {
        damager = (Player) evt.getDamager();
        account2 = Database.getInstance().getAccount(damager.getUniqueId());
        if (account2 == null || account2.getServer() == null || !account2.getServer().equals(server) || server.isSpectator(damager) || (team != null && team.hasMember(damager))
            || damager.equals(player)) {
          evt.setCancelled(true);
          return;
        }

        if (account.canSeeBlood()) {
          player.playEffect(player.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
        }
        if (account2.canSeeBlood()) {
          damager.playEffect(player.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
        }
      }

      if (evt.getDamager() instanceof Projectile) {
        if (evt.getDamage() == 0.0) {
          evt.setDamage(0.001D);
        }

        Projectile proj = (Projectile) evt.getDamager();
        if (proj.getShooter() instanceof Player) {
          damager = (Player) proj.getShooter();
          account2 = Database.getInstance().getAccount(damager.getUniqueId());
          if (account2 == null || account2.getServer() == null || !account2.getServer().equals(server) || server.isSpectator(damager) || (team != null && team.hasMember(damager))
              || damager.equals(player)) {
            evt.setCancelled(true);
            return;
          }

          if (account.canSeeBlood()) {
            player.playEffect(player.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
          }
          if (account2.canSeeBlood()) {
            damager.playEffect(player.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
          }
        }
      }

      if (damager != null) {
        account.setHit(damager.getUniqueId());
      }
      
      if ((player.getHealth() - evt.getDamage()) <= 0.0 && SkyWarsPerk.isDecisiveStrike(player, server.getType().getIndex())) {
        evt.setDamage(player.getHealth() - 0.5);
        return;
      }
    }
  }

  @EventHandler
  public void onEntityDamage(EntityDamageEvent evt) {
    if (evt.getEntity() instanceof Player) {
      Player player = (Player) evt.getEntity();

      Account account = Database.getInstance().getAccount(player.getUniqueId());
      if (account != null) {
        SkyWarsServer server = account.getServer();
        if (server == null) {
          evt.setCancelled(true);
          if (evt.getCause() == DamageCause.VOID) {
            player.teleport(SetLobbyCommand.getSpawnLocation());
          }
        } else {
          if (server.getState() == SkyWarsState.WAITING || server.getState() == SkyWarsState.ENDED) {
            evt.setCancelled(true);
          } else if (server.isSpectator(player)) {
            evt.setCancelled(true);
          } else if (player.getNoDamageTicks() > 0 && evt.getCause() == DamageCause.FALL) {
            evt.setCancelled(true);
          } else if (evt.getCause() == DamageCause.VOID) {
            evt.setDamage(player.getMaxHealth());
          }
        }
      }
    }
  }

  @EventHandler
  public void onCreatureSpawn(CreatureSpawnEvent evt) {
    evt.setCancelled(evt.getSpawnReason() != SpawnReason.CUSTOM);
  }

  @EventHandler
  public void onFoodLevelChange(FoodLevelChangeEvent evt) {
    evt.setCancelled(true);
    if (evt.getEntity() instanceof Player) {
      Account account = Database.getInstance().getAccount(evt.getEntity().getUniqueId());
      if (account != null) {
        SkyWarsServer server = account.getServer();
        if (server != null) {
          evt.setCancelled(server.getState() != SkyWarsState.INGAME);
          if (!evt.isCancelled()) {
            account.getPlayer().setSaturation(5.0f);
          }
        }
      }
    }
  }
}
