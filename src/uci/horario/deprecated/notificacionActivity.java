package uci.horario.deprecated;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class notificacionActivity extends Service {

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		Log.v("horario", "onStart");
		String x = intent.getExtras().getString("x");
		Log.v("horario", "notification: " + x);
		Toast.makeText(this.getBaseContext(), x, Toast.LENGTH_SHORT).show();
		super.onStart(intent, startId);
		this.stopSelf();
	}
	@Override
	public void onCreate() {
		Log.v("horario", "onStart");
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
