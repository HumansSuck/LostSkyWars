package tk.kanaostore.losteddev.skywars.hook.boxes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.material.Chest;
import org.bukkit.material.EnderChest;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.losteddev.boxes.api.box.Box;
import io.github.losteddev.boxes.api.box.BoxReward;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.database.Database;
import tk.kanaostore.losteddev.skywars.holograms.entity.IArmorStand;
import tk.kanaostore.losteddev.skywars.nms.NMS;
import tk.kanaostore.losteddev.skywars.nms.Sound;
import tk.kanaostore.losteddev.skywars.particles.ParticleEffect;
import tk.kanaostore.losteddev.skywars.player.Account;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.PlayerUtils;
import tk.kanaostore.losteddev.skywars.utils.StringUtils;

public class BoxOpener {

  private static final GameProfile chest;

  static {
    chest = new GameProfile(UUID.randomUUID(), null);
    chest.getProperties().put("textures", new Property("textures",
        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTZjYzQ4NmMyYmUxY2I5ZGZjYjJlNTNkZDlhM2U5YTg4M2JmYWRiMjdjYjk1NmYxODk2ZDYwMmI0MDY3In19fQ=="));
  }

  public static void spin(Account account, Box box, Location location, OpeningCallback callback) {
    io.github.losteddev.boxes.database.Database.getInstance().getAccount(account.getUniqueId()).removeBox(box);
    doTheThing(account.getPlayer(), box, location, 7, 10.0, 1.3, callback);
  }

  public static BukkitTask doTheThing(Player player, Box box, Location location, int numberOfArmorstands, double speed, double radius, OpeningCallback callback) {
    Location enderChest = location.clone();
    Location loc = location.getBlock().getLocation().clone().add(0, -1.5, 0);
    UUID id = player.getUniqueId();
    return new BukkitRunnable() {
      List<IArmorStand> armorstands = new ArrayList<>();
      int numOfAS = numberOfArmorstands, ran = 0, endAtRun = -1;
      double PI_2 = Math.PI * 2, rad = radius, spd = speed, currentOffset = 0, asOffset = PI_2 / numOfAS;
      boolean isRandomed = false;
      IArmorStand winHologram;
      boolean winning = false, ended = false;

      @Override
      public void run() {
        double rotate = 0;
        boolean revert = false;
        if (enderChest.getBlock().getState().getData() instanceof EnderChest) {
          EnderChest ender_chest = (EnderChest) enderChest.getBlock().getState().getData();
          if (ender_chest.getFacing() == BlockFace.NORTH) {
            rotate = 180.0;
          } else if (ender_chest.getFacing() == BlockFace.EAST) {
            rotate = -90.0;
            revert = true;
          } else if (ender_chest.getFacing() == BlockFace.SOUTH) {
            rotate = 0.0;
          } else if (ender_chest.getFacing() == BlockFace.WEST) {
            rotate = 90.0;
            revert = true;
          }
        } else if (enderChest.getBlock().getState().getData() instanceof Chest) {
          Chest chest = (Chest) enderChest.getBlock().getState().getData();
          if (chest.getFacing() == BlockFace.NORTH) {
            rotate = 180.0;
          } else if (chest.getFacing() == BlockFace.EAST) {
            rotate = -90.0;
            revert = true;
          } else if (chest.getFacing() == BlockFace.SOUTH) {
            rotate = 0.0;
          } else if (chest.getFacing() == BlockFace.WEST) {
            rotate = 90.0;
            revert = true;
          }
        }

        NMS.playChestAction(enderChest, true);
        Location pLoc = loc.clone().add(0.5, 1, 0.5);
        if (armorstands.size() == numOfAS || ended) {
          ended = true;
          if (endAtRun == -1 && !winning) {
            endAtRun = 80;
            winning = true;
          }

          if (ran >= endAtRun) {
            if (endAtRun == 80) {
              endAtRun = (numOfAS - 1) * 4;
              ParticleEffect.CRIT_MAGIC.display(0.3f, 0.3f, 0.3f, 0.0f, 2, ((ArmorStand) winHologram.getEntity()).getEyeLocation(), 16);
              ParticleEffect.EXPLOSION_NORMAL.display(1.2f, 0.5f, 1.2f, 0.0f, 1, ((ArmorStand) winHologram.getEntity()).getEyeLocation(), 16);
            }
            if (endAtRun <= 0) {
              if (endAtRun == -1) {
                winHologram.getEntity().teleport(winHologram.getEntity().getLocation().getBlock().getLocation().clone().add(0.5, 0, 0.5));
              }
              winHologram.getEntity().teleport(winHologram.getEntity().getLocation().clone().subtract(0, 0.1, 0));
              if (endAtRun == -5) {
                BoxReward random = box.getRandom();
                StringBuilder append = new StringBuilder(random.getRarity().getPrefix() + " §f" + random.getName());
                final String name = append.toString();
                IArmorStand stand = NMS.createArmorStand(winHologram.getEntity().getLocation().clone().add(0, 0.5, 0), "§k" + StringUtils.stripColors(name), null);
                ((ArmorStand) stand.getEntity()).setGravity(false);
                cancel();
                new BukkitRunnable() {
                  int count = 80;

                  @Override
                  public void run() {
                    if (count == 0) {
                      NMS.playChestAction(loc.clone().add(0, 1.5, 0), false);
                      stand.killEntity();
                      winHologram.killEntity();
                      armorstands.clear();
                      callback.finish();
                      this.cancel();
                      return;
                    }

                    NMS.playChestAction(enderChest, true);
                    if (count == 65) {
                      ParticleEffect.FLAME.display(0.0f, 0.0f, 0.0f, 1.0f, 90, ((ArmorStand) winHologram.getEntity()).getEyeLocation(), 16);
                      ParticleEffect.SMOKE_LARGE.display(0.0f, 0.0f, 0.0f, 0.7f, 90, ((ArmorStand) winHologram.getEntity()).getEyeLocation(), 16);
                      ParticleEffect.LAVA.display(0.0f, 0.0f, 0.0f, 1.0f, 25, ((ArmorStand) winHologram.getEntity()).getEyeLocation(), 16);
                      Sound.EXPLODE.play(loc.getWorld(), ((ArmorStand) winHologram.getEntity()).getLocation(), 0.3f, 1.0f);
                      io.github.losteddev.boxes.player.Account user = null;
                      if (Bukkit.getPlayer(id) != null && (user = io.github.losteddev.boxes.database.Database.getInstance().getAccount(id)) != null) {
                        user.addLastItem(name);
                        if (!random.has(player)) {
                          random.give(player);
                          if (!random.getRarity().getBroadcast().isEmpty()) {
                            Database.getInstance().listAccounts().stream().filter(account -> account.inLobby()).forEach(account -> {
                              account.getPlayer().sendMessage(PlayerUtils.replaceAll(player, random.getRarity().getBroadcast()).replace("{prefix}", random.getRarity().getPrefix())
                                  .replace("{item}", random.getName()));
                            });
                          }
                        } else {
                          Account account = Database.getInstance().getAccount(id);
                          if (account != null) {
                            account.addMysteryDusts(random.getRarity().getMysteryFrags());
                            player.sendMessage(Language.lobby$npcs$box$duplicate.replace("{frags}", StringUtils.formatNumber(random.getRarity().getMysteryFrags()))
                                .replace("{prefix}", random.getRarity().getPrefix()).replace("{item}", random.getName()));
                          }
                        }
                      }
                      box.destroy();
                      stand.setName(name);
                    }

                    if (count >= 65 && count % 2 == 0) {
                      Sound.NOTE_PLING.play(loc.getWorld(), ((ArmorStand) winHologram.getEntity()).getLocation(), 0.2f, 1.0f);
                      Sound.NOTE_SNARE_DRUM.play(loc.getWorld(), ((ArmorStand) winHologram.getEntity()).getLocation(), 1.0f, 1.0f);
                    }
                    count--;
                  }
                }.runTaskTimer(Main.getInstance(), 0, 1);
              }
              endAtRun--;
              return;
            }
            if (endAtRun % 4 == 0) {
              IArmorStand random = armorstands.get(new Random().nextInt(armorstands.size()));
              while (random.equals(winHologram)) {
                random = armorstands.get(new Random().nextInt(armorstands.size()));
              }
              random.killEntity();
              Sound.LAVA_POP.play(((ArmorStand) random.getEntity()).getWorld(), ((ArmorStand) random.getEntity()).getLocation(), 1.0f, 1.0f);
              ((ArmorStand) random.getEntity()).getWorld().playEffect(((ArmorStand) random.getEntity()).getLocation(), Effect.SMOKE, 1000);
              armorstands.remove(random);
            }
            endAtRun--;
            return;
          }
        }
        if (armorstands.size() < numOfAS) {
          if (Math.toRadians(currentOffset) >= asOffset * armorstands.size()) {
            IArmorStand a = NMS.createArmorStand(pLoc, "", null);
            ArmorStand hd = ((ArmorStand) a.getEntity());
            hd.setGravity(false);
            hd.setHeadPose(new EulerAngle(0, rotate, 0));
            hd.setHelmet(BukkitUtils.putProfileOnSkull(chest, BukkitUtils.deserializeItemStack("SKULL_ITEM:3")));

            armorstands.add(a);
            return;
          }
        }
        if (armorstands.size() == numOfAS) {
          if (!isRandomed) {
            winHologram = armorstands.get(armorstands.size() - 3);
            isRandomed = true;
          }
        }
        int asNum = 0;
        double cO = Math.toRadians(currentOffset % 360d);
        for (double d = 0; d < PI_2; d += PI_2 / numOfAS) {
          if (asNum >= armorstands.size()) {
            break;
          }
          double a = d + cO, cos = Math.cos(a), sin = Math.sin(a);
          IArmorStand as = armorstands.get(asNum++);
          Location l = pLoc.clone().add((!revert ? cos * rad : 0), -sin * rad, (revert ? cos * rad : 0));
          as.getEntity().teleport(l);
        }
        currentOffset += spd * 1.0;
        ran++;
        if (ran % 5 == 0) {
          Sound.CLICK.play(loc.getWorld(), loc, 0.04f, 1.0f);
          Sound.NOTE_PLING.play(loc.getWorld(), loc, 0.2f, 1.0f);
          Sound.NOTE_BASS_DRUM.play(loc.getWorld(), loc, 1.0f, 1.0f);
        }
      }
    }.runTaskTimer(Main.getInstance(), 0, 1);
  }
}
