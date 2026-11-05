package plugin.google.maps;

import android.util.LruCache;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectCache {
  // Thread-safe: objects are added from the cordova thread pool and image callbacks while clear()/onDestroy()
  // copy the keys on the UI thread. With a plain HashSet, keys.toArray() during a put threw
  // ArrayIndexOutOfBoundsException (PluginMarker.clear() when switching map pages fast).
  // Copy it with keys.toArray(new String[0]): a presized array gets null padding if the set shrinks meanwhile.
  // A concurrent set rejects null keys, so the methods below ignore them (LruCache rejects them too).
  public final Set<String> keys = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
  private LruCache<String, Object> objects = new LruCache<String, Object>(10000);

  public boolean containsKey(String key) {
    return key != null && keys.contains(key);
  }

  public void put(String key, Object object) {
    if (key == null) {
      return;
    }
    keys.add(key);
    objects.put(key, object);
  }

  public Object remove(String key) {
    if (key == null) {
      return null;
    }
    keys.remove(key);
    return objects.remove(key);
  }

  public Object get(String key) {
    if (key == null) {
      return null;
    }
    return objects.get(key);
  }

  public int size() {
    return objects.size();
  }


  public void clear() {
    for (String key : keys) {
      objects.remove(key);
    }
    keys.clear();
  }

  public void destroy() {
    objects.evictAll();
  }


}
