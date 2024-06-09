package tk.kanaostore.losteddev.skywars.hook.citizens;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;

public class EmptyDatastore implements NPCDataStore {

  @Override
  public void storeAll(NPCRegistry arg0) {}

  @Override
  public void store(NPC arg0) {}

  @Override
  public void saveToDiskImmediate() {}

  @Override
  public void saveToDisk() {}

  @Override
  public void loadInto(NPCRegistry arg0) {}

  private int current = 0;

  @Override
  public int createUniqueNPCId(NPCRegistry arg0) {
    return current++;
  }

  @Override
  public void clearData(NPC arg0) {}

  public void reloadFromSource() {}
}
