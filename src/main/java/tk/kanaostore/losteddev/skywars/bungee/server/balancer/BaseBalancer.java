package tk.kanaostore.losteddev.skywars.bungee.server.balancer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tk.kanaostore.losteddev.skywars.bungee.server.balancer.elements.LoadBalancerObject;

public abstract class BaseBalancer<T extends LoadBalancerObject> implements LoadBalancer<T> {

  private Map<String, T> objects;
  protected List<T> nextObj;

  public BaseBalancer() {
    objects = new HashMap<>();
    nextObj = new ArrayList<>();
  }

  public BaseBalancer(Map<String, T> map) {
    addAll(map);
  }
  
  public void destroy() {
    this.objects.clear();
    this.nextObj.clear();
  }

  public void add(String id, T obj) {
    objects.put(id, obj);
    update();
  }

  public T get(String id) {
    return objects.get(id);
  }

  public void remove(String id) {
    objects.remove(id);
    update();
  }

  public void addAll(Map<String, T> map) {
    if (objects != null)
      objects.clear();
    objects = map;
    update();
  }

  public List<T> getList() {
    return nextObj;
  }

  public void update() {
    if (nextObj != null)
      nextObj.clear();
    nextObj = new ArrayList<>();
    nextObj.addAll(objects.values());
  }

  public abstract int getTotalNumber();
}
