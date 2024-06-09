package tk.kanaostore.losteddev.skywars.bungee.server.balancer;

import tk.kanaostore.losteddev.skywars.bungee.server.balancer.elements.LoadBalancerObject;

public interface LoadBalancer<T extends LoadBalancerObject> {

  public T next();
}
