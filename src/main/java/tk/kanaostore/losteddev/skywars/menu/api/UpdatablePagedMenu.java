package tk.kanaostore.losteddev.skywars.menu.api;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import tk.kanaostore.losteddev.skywars.Main;

public abstract class UpdatablePagedMenu extends PagedMenu implements Listener {
  
  private BukkitTask task;
  
  public UpdatablePagedMenu(String name) {
    this(name, 21);
  }
  
  public UpdatablePagedMenu(String name, int rows) {
    super(name, rows);
  }
  
  public void register(long updateEveryTicks) {
    Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    this.task = new BukkitRunnable() {
      @Override
      public void run() {
        update();
      }
    }.runTaskTimer(Main.getInstance(), 0, updateEveryTicks);
  }
  
  public void cancel() {
    this.task.cancel();
    this.task = null;
  }
  
  public abstract void update();
}
