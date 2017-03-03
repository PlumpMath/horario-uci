package uci.horarioUCI.tools;

import uci.horarioUCI.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

@SuppressLint("NewApi")
public class NotificationTools extends Activity {
	public void showNotification(Context context, String title, String message) {
		Notification.Builder mBuilder = new Notification.Builder(context)
				.setSmallIcon(R.drawable.ic_horario_uci).setContentTitle(title)
				.setContentText(message).setTicker(message);
		// ((NotificationManager) context
		// .getSystemService(Context.NOTIFICATION_SERVICE)).notify(0,
		// mBuilder.build());
	}
}
