package tk.kanaostore.losteddev.skywars.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SkyWarsEvent extends Event {
  
  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }
  
  private static final HandlerList HANDLERS = new HandlerList();
  
  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}