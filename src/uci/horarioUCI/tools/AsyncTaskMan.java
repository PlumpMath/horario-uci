package uci.horarioUCI.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import uci.horario.deprecated.HorarioUCISQLiteHelper;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class AsyncTaskMan {
	private Context context;
	private ProgressDialog pDialog;
	private Boolean online;
	private SharedPreferencesManager pref;
	public HorarioUCISQLiteHelper db;

	public AsyncTaskMan(Context context, ProgressDialog progressDialog) {
		this.context = context;
		this.pDialog = progressDialog;

		pref = new SharedPreferencesManager(context);
		db = new HorarioUCISQLiteHelper(context);
	}

	public void verificarConexionActualizar(String mfacultad, String msemana, String mbrigada) {
		online = false;
		pref.setFacultad(mfacultad);
		pref.setBrigada(mbrigada);
		pref.setSemana(msemana);
		new VerificarConexionIntranetdAsyncTask().execute();
	}

	private void hayPing(final Handler handler, final int timeout) {
		// ask fo message '0' (not connected) or '1' (connected) on
		// 'handler'
		// the answer must be send before before within the 'timeout' (in
		// milliseconds)

		new Thread() {
			private boolean responded = false;

			@Override
			public void run() {
				// set 'responded' to TRUE if is able to connect with google
				// mobile (responds fast)
				new Thread() {
					private String ipHorario = "http://horario.uci.cu";

					@Override
					public void run() {
						try {
							responded = validarPingIP(ipHorario);
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

	private boolean validarPingIP(String IP) {
		String mensaje = "";
		String pingCMD = "/system/bin/ping -c 1 " + IP;
		String Minimo = "", Maximo = "", Media = "";
		try {
			Runtime ejecuta = Runtime.getRuntime();
			Process proceso = ejecuta.exec(pingCMD);

			InputStreamReader entrada = new InputStreamReader(proceso.getInputStream());
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

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			pDialog.dismiss();
			if (msg.what != 1) { // code if not connected
				log("La aplicaci�n no tiene acceso a la red.");
				Log.v("horario", "No hay ping");
			} else { // code if connected
				log("La aplicaci�n tiene acceso a la red.");
				Log.v("horario", "Hay ping");
				actualizarHorarioIntranet();

			}
			// en este punto se conoce la conectividad de la aplicacion

		}
	};

	private void actualizarHorarioIntranet() {
		if (!online) {
			print("Esta en modo offline. Por favor cambie a modo online para utilizar esta opcion.");
			log("No se puede actualizar en modo offline");
		} else {
			log("Inicio de actualizacion");
			new ObtenerHorarioIntranetdAsyncTask().execute();
		}
	}

	private class VerificarConexionIntranetdAsyncTask extends AsyncTask<Void, String, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			boolean bConectado = false;
			ConnectivityManager connec = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo[] redes = connec.getAllNetworkInfo();
			for (int i = 0; i < 2; i++) {
				if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
					bConectado = true;
				}
			}
			return bConectado;
			// return true;
		}

		@Override
		protected void onPostExecute(Boolean hayConexion) {
			super.onPostExecute(hayConexion);
			if (hayConexion) {
				online = true;
				log("Hay conexion.");
				try {
					hayPing(handler, 1000);
				} catch (Exception e) {
					Log.v("horario", e.getMessage());
					e.printStackTrace();
				}
			} else {
				log("No hay conexion");
				log("Se inici� en modo Offline.");
				// iniciarSplinners();
				pDialog.dismiss();
			}
		}

		@Override
		protected void onPreExecute() {
			Looper.prepare();
			super.onPreExecute();
			pDialog = new ProgressDialog(context);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setCancelable(false);
			pDialog.setMax(100);
			pDialog.setMessage("Comprobando conexion ...");
			pDialog.show();
		}
	}

	private class ObtenerHorarioIntranetdAsyncTask extends AsyncTask<Void, String, String[][]> {
		String facultad = pref.getFacultad();
		String semana = pref.getSemana();
		String brigada = pref.getBrigada();

		@Override
		protected String[][] doInBackground(Void... params) {
			try {
				HorarioUCIParser horarioUCIParser = new HorarioUCIParser();
				return horarioUCIParser.obtenerHorario(facultad, semana, brigada);
			} catch (Exception e) {
				log(e.getMessage());
				return new String[][] {};
			}
		}

		@Override
		protected void onPostExecute(String[][] result) {
			super.onPostExecute(result);
			// horarioAsyncTask = result;
			pDialog.dismiss();
			if (result == null) {
				log("No existe horario para la brigada " + brigada + " en la semana: " + semana);
				print("No existe horario para la brigada " + brigada + " en la semana: " + semana);
				return;
			}
			if (result.length == 0) {
				log("No se pueden obtener los datos en este momento. Int�ntelo m�s tarde");
				print("No se pueden obtener los datos en este momento. Int�ntelo m�s tarde");
				return;
			}
			String cadenaHorario = "";
			for (int i = 0; i < result.length; i++) {
				for (int j = 0; j < result[0].length; j++) {
					cadenaHorario += result[i][j] + "&";
				}
				cadenaHorario += "#";
			}
			Date dt = new Date();
			String fecha = convertirFecha(dt);
			db.insertarHorarioBD(facultad, semana, brigada, cadenaHorario, fecha);
			String[] horario = new String[] { semana, facultad, brigada, cadenaHorario, fecha };
			log("Se ha guardado el horario para la brigada " + brigada + " en la semana: " + semana);
			actualizarDatos(horario);
			print("** Se actualizo el horario **");

			// actualizar calendario
			CalendarManager.adicionarSemanaCalendario(context, result, semana);
			print("** Se actualizo el calendario **");

			online = false;
		}

		private void actualizarDatos(String[] horario) {
			semana = horario[0];
			facultad = horario[1];
			brigada = horario[2];
		}

		private String convertirFecha(Date fechaDate) {
			boolean am = Integer.parseInt(fechaDate.toString().split(" ")[3].split(":")[0]) < 12;
			SimpleDateFormat df = new SimpleDateFormat("d-M-yyyy h:mm:ss");
			String fecha = df.format(fechaDate) + (am ? " am" : " pm");
			return fecha;
		}
	}

	private String[][] convertir(String cadena) {
		String[][] resultado = new String[10][10];
		String[] filas = cadena.split("#");
		String[] columnas;
		for (int i = 0; i < filas.length; i++) {
			columnas = filas[i].split("&");
			for (int j = 0; j < columnas.length; j++) {
				resultado[i][j] = columnas[j];
			}
		}
		return resultado;
	}

	private void log(String mensaje) {
		Log.v("horario", mensaje);
	}

	private void print(String s) {
		Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
	}
}
