package tk.kanaostore.losteddev.skywars.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import tk.kanaostore.losteddev.skywars.Language;
import tk.kanaostore.losteddev.skywars.Main;
import tk.kanaostore.losteddev.skywars.api.server.SkyWarsState;
import tk.kanaostore.losteddev.skywars.ui.SkyWarsBlock;
import tk.kanaostore.losteddev.skywars.ui.server.ScanCallback;
import tk.kanaostore.losteddev.skywars.utils.BukkitUtils;
import tk.kanaostore.losteddev.skywars.utils.ConfigUtils;

@SuppressWarnings("deprecation")
public class WorldRegeneration {

  private static BukkitTask task;
  private static List<WorldServer<?>> queue = new ArrayList<>();

  public static void rollBack(WorldServer<?> server) {
    if (queue.contains(server)) {
      return;
    }

    queue.add(server);
    if (task == null) {
      task = new BukkitRunnable() {

        int count = 0;
        WorldServer<?> rollbacking = null;
        Iterator<Block> iterator = null;

        @Override
        public void run() {
          count = 0;
          if (rollbacking != null && iterator != null) {
            if (Language.game$regen$world_reload) {
              rollbacking.getConfig().reload();
              rollbacking.setState(SkyWarsState.WAITING);
              rollbacking.setTimer(Language.game$countdown$start + 1);
              rollbacking.getTask().reset();
              rollbacking = null;
              iterator = null;
            } else {
              while (iterator.hasNext() && count < Language.game$regen$blocks_per_tick) {
                rollbacking.resetBlock(iterator.next());
                count++;
              }

              if (!iterator.hasNext()) {
                rollbacking.setState(SkyWarsState.WAITING);
                rollbacking.setTimer(Language.game$countdown$start + 1);
                rollbacking.getTask().reset();
                rollbacking = null;
                iterator = null;
              }
            }

            return;
          }

          if (!queue.isEmpty()) {
            rollbacking = queue.get(0);
            iterator = rollbacking.getConfig().getWorldCube().iterator();
            queue.remove(0);
          } else {
            cancel();
            task = null;
          }
        }
      }.runTaskTimer(Main.getInstance(), 0, 1);
    }
  }

  public static void scan(WorldServer<?> server, ConfigUtils config, ScanCallback callback) {
    new BukkitRunnable() {
      int count = 0;
      List<String> blocks = new ArrayList<>();
      Iterator<Block> iterator = server.getConfig().getWorldCube().iterator();

      @Override
      public void run() {
        while (iterator.hasNext() && count < 50000) {
          Block block = iterator.next();
          if (block.getType() != Material.AIR) {
            String location = BukkitUtils.serializeLocation(block.getLocation());
            server.getBlocks().put(location, new SkyWarsBlock(block.getType(), block.getData()));
            blocks.add(location + " : " + block.getType().name() + ", " + block.getData());
          }

          count++;
        }

        count = 0;
        if (!iterator.hasNext()) {
          cancel();
          config.set("data", blocks);
          server.setState(SkyWarsState.WAITING);
          if (callback != null) {
            callback.finish();
          }
        }
      }
    }.runTaskTimer(Main.getInstance(), 0, 1);
  }
}
