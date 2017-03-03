package uci.horarioUCI.UpdateService;

import java.util.List;

import uci.horarioUCI.tools.NotificationTools;
import uci.horarioUCI.tools.SharedPreferencesManager;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class WifiReceiver extends BroadcastReceiver {

	private AsyncTaskManHidden asyncTaskMan;
	private SharedPreferencesManager pref;

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager conMan = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = conMan.getActiveNetworkInfo();
		if (netInfo != null
				&& netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			Log.d("WifiReceiver", "Have Wifi Connection");
			if (isRunning(context)) {
				// new NotificationTools().showNotification(context,
				// "HorarioUCI",
				// "Actualizando horario...");
				try {
					pref = new SharedPreferencesManager(context);
					asyncTaskMan = new AsyncTaskManHidden(context);
					asyncTaskMan.verificarConexionActualizar(
							pref.getFacultad(), pref.getSemana(),
							pref.getBrigada());
				} catch (Exception e) {
					Log.e("WifiReceiver", e.getMessage());
				}
			} else {
				Log.d("WifiReceiver",
						"Have Wifi Connection but service is inactive");
				Log.d("WifiReceiver", "Restarting service ... .. .");
				Toast.makeText(context,
						"Restarting HorarioUCI-Update Service.",
						Toast.LENGTH_SHORT).show();
				context.startService(new Intent(context,
						ServiceHorarioUpdate.class));
			}

		} else
			Log.d("WifiReceiver", "Don't have Wifi Connection");
	}

	private boolean isRunning(Context context) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningServices = manager
				.getRunningServices(Integer.MAX_VALUE);
		for (RunningServiceInfo service : runningServices) {
			if (ServiceHorarioUpdate.class.getName().equals(
					service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
};