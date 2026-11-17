package pe.misfinanzas.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.provider.Settings;
import android.text.TextUtils;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Puente entre la página (JavaScript) y el código de Android.
 * En la página se usa como: Capacitor.Plugins.MisFinanzasNotif.metodo()
 */
@CapacitorPlugin(name = "MisFinanzasNotif")
public class NotifPlugin extends Plugin {

    /** Lista las apps instaladas que tienen icono de lanzador. */
    @PluginMethod
    public void listApps(PluginCall call) {
        try {
            Context ctx = getContext();
            PackageManager pm = ctx.getPackageManager();
            Intent main = new Intent(Intent.ACTION_MAIN, null);
            main.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> infos = pm.queryIntentActivities(main, 0);

            final List<JSONObject> items = new ArrayList<>();
            Set<String> seen = new HashSet<>();
            for (ResolveInfo ri : infos) {
                String pkg = ri.activityInfo.packageName;
                if (pkg.equals(ctx.getPackageName()) || !seen.add(pkg)) continue;
                JSONObject o = new JSONObject();
                o.put("pkg", pkg);
                o.put("name", String.valueOf(ri.loadLabel(pm)));
                items.add(o);
            }
            Collections.sort(items, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject a, JSONObject b) {
                    return a.optString("name").compareToIgnoreCase(b.optString("name"));
                }
            });
            JSArray arr = new JSArray();
            for (JSONObject o : items) arr.put(o);
            JSObject res = new JSObject();
            res.put("apps", arr);
            call.resolve(res);
        } catch (Exception e) {
            call.reject("No se pudo listar las apps: " + e.getMessage());
        }
    }

    @PluginMethod
    public void getMonitored(PluginCall call) {
        JSArray arr = new JSArray();
        for (String p : NotifStore.getMonitored(getContext())) arr.put(p);
        JSObject res = new JSObject();
        res.put("packages", arr);
        call.resolve(res);
    }

    @PluginMethod
    public void setMonitored(PluginCall call) {
        try {
            JSONArray arr = call.getArray("packages");
            Set<String> set = new HashSet<>();
            if (arr != null) {
                for (int i = 0; i < arr.length(); i++) {
                    String p = arr.optString(i, "");
                    if (!p.isEmpty()) set.add(p);
                }
            }
            NotifStore.setMonitored(getContext(), set);
            call.resolve();
        } catch (Exception e) {
            call.reject("No se pudo guardar: " + e.getMessage());
        }
    }

    /** ¿Tiene la app el "Acceso a notificaciones" activado? */
    @PluginMethod
    public void isEnabled(PluginCall call) {
        JSObject res = new JSObject();
        res.put("enabled", listenerEnabled(getContext()));
        call.resolve(res);
    }

    /** Abre la pantalla de Ajustes donde se concede el acceso. */
    @PluginMethod
    public void openSettings(PluginCall call) {
        try {
            Intent i = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(i);
            call.resolve();
        } catch (Exception e) {
            call.reject("No se pudo abrir Ajustes: " + e.getMessage());
        }
    }

    /** Entrega las notificaciones capturadas y vacía la cola. */
    @PluginMethod
    public void drain(PluginCall call) {
        JSONArray q = NotifStore.drain(getContext());
        JSObject res = new JSObject();
        res.put("items", q);
        call.resolve(res);
    }

    private static boolean listenerEnabled(Context ctx) {
        String flat = Settings.Secure.getString(ctx.getContentResolver(), "enabled_notification_listeners");
        if (TextUtils.isEmpty(flat)) return false;
        ComponentName mine = new ComponentName(ctx, NotifService.class);
        for (String s : flat.split(":")) {
            ComponentName cn = ComponentName.unflattenFromString(s);
            if (cn != null && cn.equals(mine)) return true;
        }
        return false;
    }
}
