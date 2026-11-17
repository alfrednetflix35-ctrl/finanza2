// Se ejecuta después de "npx cap add android".
// 1) Copia el código nativo (escucha de notificaciones) al proyecto Android.
// 2) Registra el servicio y el permiso de lista de apps en AndroidManifest.xml.
const fs = require('fs');
const path = require('path');

const root = path.resolve(__dirname, '..');
const srcDir = path.join(root, 'native', 'java', 'pe', 'misfinanzas', 'app');
const dstDir = path.join(root, 'android', 'app', 'src', 'main', 'java', 'pe', 'misfinanzas', 'app');
const manifestPath = path.join(root, 'android', 'app', 'src', 'main', 'AndroidManifest.xml');

fs.mkdirSync(dstDir, { recursive: true });
for (const f of fs.readdirSync(srcDir)) {
  fs.copyFileSync(path.join(srcDir, f), path.join(dstDir, f));
  console.log('copiado', f);
}

let m = fs.readFileSync(manifestPath, 'utf8');

if (!m.includes('pe.misfinanzas.app.NotifService')) {
  const service =
    '\n        <service\n' +
    '            android:name="pe.misfinanzas.app.NotifService"\n' +
    '            android:exported="true"\n' +
    '            android:label="Mis Finanzas"\n' +
    '            android:permission="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE">\n' +
    '            <intent-filter>\n' +
    '                <action android:name="android.service.notification.NotificationListenerService" />\n' +
    '            </intent-filter>\n' +
    '        </service>\n';
  if (!m.includes('</application>')) throw new Error('No se encontró </application> en el manifest');
  m = m.replace('</application>', service + '    </application>');
}

if (!m.includes('<queries>')) {
  const queries =
    '\n    <queries>\n' +
    '        <intent>\n' +
    '            <action android:name="android.intent.action.MAIN" />\n' +
    '            <category android:name="android.intent.category.LAUNCHER" />\n' +
    '        </intent>\n' +
    '    </queries>\n';
  m = m.replace('</manifest>', queries + '</manifest>');
}

fs.writeFileSync(manifestPath, m);
console.log('AndroidManifest.xml actualizado');
