package uci.horario.deprecated;

import uci.horarioUCI.R;
import uci.horarioUCI.R.drawable;
import uci.horarioUCI.R.string;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class alarmChecker extends Service {

	public static final int APP_ID_NOTIFICATION = 0;
	private NotificationManager mManager;
	private String x;
	private int code;

	/**
	 * prepara y lanza la notificacion
	 */
	@SuppressWarnings("deprecation")
	private void Notificar() {
		Log.v("horario", "Notificando para: " + x);
		Log.v("horario", "Code: " + code);
		x = x.length() == 0 ? "Evento importante en 15 min!" : x
				+ " en 15 min!";

		// Prepara la actividad que se abrira cuando el usuario pulse sobre la
		// notificacion
		Intent intentNot = new Intent(this, notificacionActivity.class);
		intentNot.putExtra("x", x);
		// Prepara la notificacion
		@SuppressWarnings("deprecation")
		Notification notification = new Notification(R.drawable.ic_horario_uci,
				"HorarioUCI", System.currentTimeMillis());

		notification.setLatestEventInfo(this, getString(R.string.app_name), x,
				PendingIntent.getService(this.getBaseContext(), 0, intentNot,
						PendingIntent.FLAG_CANCEL_CURRENT));

		// sonido
		notification.defaults |= Notification.DEFAULT_SOUND;
		// vibración
		notification.defaults |= Notification.DEFAULT_VIBRATE;

		// luz mediante LED
		notification.defaults |= Notification.DEFAULT_LIGHTS;

		// La notificación se detendrá cuando el usuario pulse en ella
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		// Intenta establecer el color y el parpadeo de la bombilla lED
		try {
			notification.ledARGB = 0xff00ff00;
			notification.ledOnMS = 300;
			notification.ledOffMS = 1000;
			notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		} catch (Exception ex) {
			// Nothing
		}

		// Lanza la notificación
		mManager.notify(APP_ID_NOTIFICATION, notification);
		this.stopSelf();
	}

	@Override
	public void onCreate() {
		mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Override
	public void onDestroy() {
		Log.v("horario", "Destroying Alarm Service");
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		x = intent.getExtras().getString("x");
		code = intent.getExtras().getInt("code");
		Notificar();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}