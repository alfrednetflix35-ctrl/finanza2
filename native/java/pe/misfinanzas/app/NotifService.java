package pe.misfinanzas.app;

import android.app.Notification;
import android.os.Bundle;
import android.os.Parcelable;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import org.json.JSONObject;

import java.util.Set;

/**
 * Escucha las notificaciones del teléfono. Solo guarda las de las apps que el
 * usuario eligió en "Apps vigiladas". Funciona aunque la app esté cerrada.
 */
public class NotifService extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        try {
            if (sbn == null) return;
            String pkg = sbn.getPackageName();
            if (pkg == null || pkg.equals(getPackageName())) return;

            Set<String> monitored = NotifStore.getMonitored(this);
            if (!monitored.contains(pkg)) return;

            Notification n = sbn.getNotification();
            if (n == null) return;
            // Ignora el resumen de grupo (duplica las notificaciones individuales)
            if ((n.flags & Notification.FLAG_GROUP_SUMMARY) != 0) return;

            Bundle ex = n.extras;
            if (ex == null) return;
            String title = str(ex.getCharSequence(Notification.EXTRA_TITLE));
            if (title.isEmpty()) title = str(ex.getCharSequence(Notification.EXTRA_TITLE_BIG));

            // Se junta TODO el texto disponible (texto, texto grande, líneas, subtítulo,
            // mensajes) para no perder el monto ni el destinatario. Gmail pone el asunto
            // en el texto y un resumen del correo en el texto grande o en las líneas.
            StringBuilder sb = new StringBuilder();
            add(sb, str(ex.getCharSequence(Notification.EXTRA_TEXT)));
            add(sb, str(ex.getCharSequence(Notification.EXTRA_BIG_TEXT)));
            CharSequence[] lines = ex.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);
            if (lines != null) for (CharSequence l : lines) add(sb, str(l));
            Parcelable[] msgs = ex.getParcelableArray(Notification.EXTRA_MESSAGES);
            if (msgs != null) {
                for (Parcelable m : msgs) {
                    if (m instanceof Bundle) add(sb, str(((Bundle) m).getCharSequence("text")));
                }
            }
            add(sb, str(ex.getCharSequence(Notification.EXTRA_SUB_TEXT)));
            add(sb, str(ex.getCharSequence(Notification.EXTRA_SUMMARY_TEXT)));
            add(sb, str(ex.getCharSequence(Notification.EXTRA_INFO_TEXT)));
            if (n.tickerText != null) add(sb, str(n.tickerText));
            String text = sb.toString().trim();
            if (title.isEmpty() && text.isEmpty()) return;

            long when = sbn.getPostTime();
            String key = pkg + "|" + when + "|" + (title + "|" + text).hashCode();

            JSONObject o = new JSONObject();
            o.put("key", key);
            o.put("pkg", pkg);
            o.put("title", title);
            o.put("text", text);
            o.put("ts", when);
            NotifStore.push(this, o);
        } catch (Exception ignored) {
        }
    }

    /** Agrega un trozo de texto si aún no está incluido. */
    private static void add(StringBuilder sb, String s) {
        if (s.isEmpty() || sb.indexOf(s) >= 0) return;
        if (sb.length() > 0) sb.append(' ');
        sb.append(s);
    }

    private static String str(CharSequence cs) {
        return cs == null ? "" : cs.toString().trim();
    }
}
