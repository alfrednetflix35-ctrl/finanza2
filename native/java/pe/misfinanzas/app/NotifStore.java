package pe.misfinanzas.app;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Almacén simple (SharedPreferences) compartido entre el servicio que escucha
 * notificaciones y el plugin que habla con la página web.
 * - apps vigiladas: paquetes que el usuario eligió
 * - cola: notificaciones capturadas que la página aún no procesó
 */
public class NotifStore {
    private static final String PREFS = "misfinanzas_notif";
    private static final String K_APPS = "monitored";
    private static final String K_QUEUE = "queue";
    private static final String K_RECENT = "recent";
    private static final int MAX_QUEUE = 300;
    private static final int MAX_RECENT = 80;

    private static SharedPreferences sp(Context c) {
        return c.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static synchronized Set<String> getMonitored(Context c) {
        return new HashSet<>(sp(c).getStringSet(K_APPS, new HashSet<String>()));
    }

    public static synchronized void setMonitored(Context c, Set<String> pkgs) {
        sp(c).edit().putStringSet(K_APPS, new HashSet<>(pkgs)).apply();
    }

    /** Agrega una notificación a la cola. Devuelve false si es repetida. */
    public static synchronized boolean push(Context c, JSONObject item) {
        try {
            String key = item.optString("key");
            JSONArray recent = new JSONArray(sp(c).getString(K_RECENT, "[]"));
            for (int i = 0; i < recent.length(); i++) {
                if (key.equals(recent.optString(i))) return false;
            }
            recent.put(key);
            JSONArray trimmed = new JSONArray();
            int start = Math.max(0, recent.length() - MAX_RECENT);
            for (int i = start; i < recent.length(); i++) trimmed.put(recent.optString(i));

            JSONArray q = new JSONArray(sp(c).getString(K_QUEUE, "[]"));
            q.put(item);
            JSONArray qt = new JSONArray();
            int qs = Math.max(0, q.length() - MAX_QUEUE);
            for (int i = qs; i < q.length(); i++) qt.put(q.get(i));

            sp(c).edit().putString(K_RECENT, trimmed.toString()).putString(K_QUEUE, qt.toString()).apply();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Devuelve todo lo pendiente y vacía la cola. */
    public static synchronized JSONArray drain(Context c) {
        try {
            JSONArray q = new JSONArray(sp(c).getString(K_QUEUE, "[]"));
            sp(c).edit().putString(K_QUEUE, "[]").apply();
            return q;
        } catch (Exception e) {
            return new JSONArray();
        }
    }
}
