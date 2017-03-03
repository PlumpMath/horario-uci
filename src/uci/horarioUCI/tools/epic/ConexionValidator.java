package uci.horarioUCI.tools.epic;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import uci.horarioUCI.VistaPrincipalActivity;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ConexionValidator {
	private VistaPrincipalActivity activity;

	public ConexionValidator(VistaPrincipalActivity activity) {
		this.activity = activity;
	}

	public void hayPing(final Handler handler, final int timeout) {
		// ask fo message '0' (not connected) or '1' (connected) on 'handler'
		// the answer must be send before before within the 'timeout' (in
		// milliseconds)

		new Thread() {
			private boolean responded = false;

			@Override
			public void run() {
				// set 'responded' to TRUE if is able to connect with google
				// mobile (responds fast)
				new Thread() {
					@Override
					public void run() {
						try {
							responded = validarPingIP(activity.ipHorario);
						} catch (Exception e) {
							Log.v("horario", e.getMessage());
						}
					}
				}.start();
				try {
					int waited = 0;
					while (!responded && (waited < timeout)) {
						sleep(100);
						if (!responded) {
							waited += 100;
						}
					}
				} catch (InterruptedException e) {
				} // do nothing
				finally {
					if (!responded) {
						handler.sendEmptyMessage(0);
					} else {
						handler.sendEmptyMessage(1);
					}
				}
			}
		}.start();
	}

	@SuppressLint("HandlerLeak")
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			activity.progressDialog.dismiss();
			if (msg.what != 1) { // code if not connected
				activity.toastShort("La aplicaci�n no tiene acceso a la red.");
				Log.v("horario", "No hay ping");
			} else { // code if connected
				activity.toastShort("La aplicaci�n tiene acceso a la red.");
				Log.v("horario", "Hay ping");
				activity.online = true;
				activity.iniciarSplinners();
			}
			// en este punto se conoce la conectividad de la aplicacion

		}
	};

	private boolean validarPingIP(String IP) {
		String mensaje = "";
		String pingCMD = "/system/bin/ping -c 1 " + IP;
		String Minimo = "", Maximo = "", Media = "";
		try {
			Runtime ejecuta = Runtime.getRuntime();
			Process proceso = ejecuta.exec(pingCMD);

			InputStreamReader entrada = new InputStreamReader(
					proceso.getInputStream());
			BufferedReader buffer = new BufferedReader(entrada);

			String linea = "";
			for (; (linea = buffer.readLine()) != null;) {
				// System.out.println( linea);
				mensaje += linea;

				int inminimo = linea.indexOf("nimo");
				int inmaximo = linea.indexOf("ximo");
				int inmedia = linea.indexOf("Media");

				if (inminimo > 0) {
					Minimo = linea.substring(inminimo + 7, inminimo + 7 + 1);
					Maximo = linea.substring(inmaximo + 7, inmaximo + 7 + 1);
					Media = linea.substring(inmedia + 8, inmedia + 8 + 1);

				}
			}

			buffer.close();
		} catch (Exception e) {
			Log.v("horario", e.getMessage());
		}
		return !mensaje.contains("Unreachable");
	}
}
