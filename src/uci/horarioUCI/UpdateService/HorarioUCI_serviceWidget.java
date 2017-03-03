package uci.horarioUCI.UpdateService;

import java.util.List;
import uci.horarioUCI.R;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class HorarioUCI_serviceWidget extends AppWidgetProvider {
	private static final String ACTION_cambiarlayout = "a_cambiarlayout";
	public String shprefreg = "HorarioUCIService";

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

	@Override
	public void onEnabled(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(shprefreg,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		if (isRunning(context)) {// Actualizar el widget en el momento
			// de instanciarlo
			editor.putString(shprefreg, "on");
			editor.commit();
			actualizarWidget(context, AppWidgetManager.getInstance(context),
					"on");
		} else {
			editor.putString(shprefreg, "off");
			editor.commit();
			actualizarWidget(context, AppWidgetManager.getInstance(context),
					"off");
		}
		super.onEnabled(context);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		/*
		 * Accedemos al shared preference shprefreg y tratamos de leer, si hay
		 * algun error mensaje tomara el valor "error" lo que indicara que no
		 * existe dentro de los shared preference 'MSG_switch_status' con lo
		 * cual lo creamos y añadimos como valor off
		 */

		SharedPreferences prefs = context.getSharedPreferences(shprefreg,
				Context.MODE_PRIVATE);
		String mensaje = prefs.getString(shprefreg, "error");

		if (mensaje == "error") {

			// si cuando intenta leer es error añade un registro.
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(shprefreg, "off"); // Estado apagado
			editor.commit();

			Log.e(" SharedPreferences error read", "" + mensaje);
			Log.e(" SharedPreferences W -&gt;", "off");
			mensaje = "off";

		} else {
			// No hubo error, ya existia .
			Log.e(" SharedPreferences read ok", "" + mensaje);

		}
		// Actualizamos el widget con el estado leido previamente
		actualizarWidget(context, appWidgetManager, mensaje);

	}

	public static void actualizarWidget(Context context,
			AppWidgetManager appWidgetManager, String value) {

		RemoteViews remoteViews;

		ComponentName thisWidget;

		int lay_id = 0;

		// Asignamos el layout a la variable lay_id segun el parametro recibido
		// por value
		if (value.equals("on")) {
			// ON
			lay_id = R.layout.widget_layout_on;
		}

		if (value.equals("off")) {
			// off
			lay_id = R.layout.widget_layout_off;

		}
		// Vamos a acceder a la vista y cambiar el layout segun lay_id
		remoteViews = new RemoteViews(context.getPackageName(), lay_id);
		// identifica a nuestro widget
		thisWidget = new ComponentName(context, HorarioUCI_serviceWidget.class);

		// Creamos un intent a nuestra propia clase
		Intent intent = new Intent(context, HorarioUCI_serviceWidget.class);
		// seleccionamos la accion ACTION_cambiarlayout
		intent.setAction(ACTION_cambiarlayout);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				intent, 0);

		/*
		 * Equivalente a setOnClickListener de un boton comun lo asocio con el
		 * layout1 ya que al tocar este se ejecutara la accion y con
		 * pendingIntent
		 */

		remoteViews.setOnClickPendingIntent(R.id.layoutWidget, pendingIntent);

		// actualizamos el widget
		appWidgetManager.updateAppWidget(thisWidget, remoteViews);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// Controlamos que la accion recibida sea la nuestra
		if (intent.getAction().equals(ACTION_cambiarlayout)) {
			// Leemos nuevamente SharedPreferences
			SharedPreferences prefs = context.getSharedPreferences(shprefreg,
					Context.MODE_PRIVATE);
			String mensaje = prefs.getString(shprefreg, "error");
			SharedPreferences.Editor editor = prefs.edit();

			String new_status = "";

			Log.e("! :)  status onReceive! ", mensaje);

			/*
			 * Si el estado que leimos es on definimos que el nuevo sea off y lo
			 * grabamos en SharedPreferences realizamos lo mismo con off pero
			 * usando on El valor grabado lo utilizaremos para determinar el
			 * layout a cargar
			 */

			Intent serviceIntent = new Intent(context,
					ServiceHorarioUpdate.class);// Proxy

			if (mensaje.equals("on")) {

				editor.putString(shprefreg, "off");
				editor.commit();
				new_status = "off";

				context.stopService(serviceIntent);// Deteniendo el servicio
				// del proxy
				Toast.makeText(
						context,
						"El servicio de actualizacion de HorarioUCI se detuvo.",
						Toast.LENGTH_SHORT).show();

			} else if (mensaje.equals("off")) {
				context.startService(serviceIntent);// Se inicia el proxy

				editor.putString(shprefreg, "on");
				editor.commit();
				new_status = "on";

				Toast.makeText(
						context,
						"El servicio de actualizacion de HorarioUCI se inici�.",
						Toast.LENGTH_SHORT).show();

			} else
				Toast.makeText(context, "No existe un horario favorito.",
						Toast.LENGTH_SHORT).show();
			// Actualizamos el estado del widget.
			AppWidgetManager widgetManager = AppWidgetManager
					.getInstance(context);
			actualizarWidget(context, widgetManager, new_status);
		}

		super.onReceive(context, intent);
	}

}