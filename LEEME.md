# Mis Finanzas (Android) – cómo obtener tu APK

Esta carpeta es tu app de finanzas convertida en app de Android, con lectura de
notificaciones (Yape, Plin a través de tu banco, etc.).
No tienes que tocar código. Solo subir la carpeta a GitHub y descargar el APK.

## 1. Subir a GitHub (en la computadora, ~5 min)

1. Entra a https://github.com y crea una cuenta gratis (o inicia sesión).
2. Toca **New repository**. Nombre: `mis-finanzas`. Déjalo **Private**. Crea el repositorio.
3. En la pantalla del repositorio vacío toca **uploading an existing file**.
4. Descomprime este zip y **arrastra TODO el contenido de la carpeta** (incluida la
   carpeta oculta `.github`) a la ventana. Toca **Commit changes**.

> Si la carpeta `.github` no se subió (a veces los sistemas ocultan carpetas con punto):
> en el repositorio toca **Add file → Create new file**, escribe como nombre
> `.github/workflows/build.yml` y pega ahí el contenido del archivo `build.yml`
> que viene en este zip. Luego **Commit changes**.

## 2. Compilar

1. En el repositorio ve a la pestaña **Actions**.
2. Si ves un botón verde para habilitar los workflows, tócalo.
3. Elige **Compilar APK → Run workflow** (o espera: se ejecuta solo al subir los archivos).
4. Tarda unos 5–10 minutos. Cuando aparezca el check verde, entra a esa ejecución y
   abajo, en **Artifacts**, descarga **MisFinanzas-apk** (viene en un zip con `app-debug.apk`).

Si sale una **X roja**: abre la ejecución, entra al paso que falló, copia el mensaje de
error y mándamelo. Es normal que el primer intento necesite un ajuste.

## 3. Instalar en el celular

1. Pasa `app-debug.apk` al celular (WhatsApp a ti mismo, Drive, cable…).
2. Ábrelo. Android pedirá permitir **instalar apps desconocidas** para esa app
   (Chrome, Archivos, etc.): acéptalo. Puede salir una advertencia de Play Protect:
   es normal, porque el APK es tuyo y no está en Play Store. Elige **Instalar de todos modos**.
3. Abre **Mis Finanzas**.

## 4. Configurar las notificaciones (una sola vez)

1. Menú (☰) → **Apps vigiladas** → **Dar acceso**. En Ajustes busca **Mis Finanzas** y actívalo.
   (Si Android lo bloquea por ser app instalada fuera de Play Store: Ajustes → Apps →
   Mis Finanzas → menú ⋮ → **Permitir ajustes restringidos**, y vuelve a intentarlo.)
2. Vuelve a la app y marca **Yape** y la app de tu banco (Interbank, BCP, etc.).
   Plin llega por la notificación de la app de tu banco.
   Para los correos de Interbank con asunto **"Constancia de pago Plin"**, marca también
   **Gmail** (o Outlook). Se registran como egreso con el monto y el destinatario. Los demás
   correos se ignoran. Si el monto no aparece en la notificación, queda en **Pendientes**
   para que lo escribas.
3. Desactiva la optimización de batería para Mis Finanzas
   (Ajustes → Apps → Mis Finanzas → Batería → Sin restricciones), para que Android no la cierre.

## Cómo funciona

- Cuando llega una notificación de una app vigilada, la app saca el monto y decide si fue
  ingreso o gasto.
- Modo normal: queda en **Pendientes** y la confirmas con un toque (**Es ingreso / Es gasto**).
- Si activas **Registrar solo, sin preguntarme**, se registra al instante cuando el mensaje es
  claro. Si no lo es, pasa a Pendientes igual.
- Todo se guarda solo en tu teléfono. Si desinstalas la app o borras sus datos, se pierden.

## Si algo no se reconoce

- En **Apps vigiladas** (abajo del todo) está **Últimas notificaciones recibidas**: ahí ves el
  texto completo de cada notificación y qué hizo la app con ella.
- En **Mis palabras** puedes escribir palabras de ingreso, de egreso y de correos
  (por ejemplo: `te yapearon` / `pago exitoso` / `interbank`). Tienen prioridad.
- En **Probar un mensaje** pegas un texto y ves qué entiende, sin registrar nada.
- Las notificaciones se procesan mientras la app está abierta. Si estaba cerrada, se guardan
  y se registran en cuanto la abres.

## Importante

- La detección usa palabras típicas ("te envió", "Yapeaste", "Recibiste"…). Como no vi tus
  notificaciones reales, puede fallar con algunas. Por eso empieza con el modo normal
  (Pendientes). Cuando me pases textos reales (cambia nombres y números) afino la detección.
- Los movimientos se registran con categoría "Ingresos varios" o "Otros gastos". Toca el
  movimiento en **Movimientos** para cambiarla.
- Solo funciona en Android. El APK está firmado con clave de prueba: sirve para uso
  personal, no para subirlo a Play Store.
