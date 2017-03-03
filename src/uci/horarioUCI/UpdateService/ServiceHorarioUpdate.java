package uci.horarioUCI.UpdateService;

import java.util.Timer;
import java.util.TimerTask;
import uci.horarioUCI.tools.SharedPreferencesManager;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ServiceHorarioUpdate extends Service {

	private Timer timer = new Timer();
	private static final long UPDATE_INTERVAL = 1000 * 60 * 60 * 1;// Cada 1h

	/*
	 * Async task hidden no muestra mensajes durante la ejecucion de tareas en
	 * 2do plano
	 */
	private AsyncTaskManHidden asyncTaskMan;
	private static Context context;
	private SharedPreferencesManager pref;
	public static Activity activity;

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		pref = new SharedPreferencesManager(context);
		asyncTaskMan = new AsyncTaskManHidden(context);

		buscarActualizaciones();
	}

	private void buscarActualizaciones() {
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				asyncTaskMan.verificarConexionActualizar(pref.getFacultad(),
						pref.getSemana(), pref.getBrigada());
			}
		}, 0, UPDATE_INTERVAL);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (timer != null)
			timer.cancel();
		Log.i(getClass().getSimpleName(), "Timer Stopped");
	}

	public class MyBinder extends Binder {
		public ServiceHorarioUpdate getService() {
			return ServiceHorarioUpdate.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
