package uci.horarioUCI.tools.async;

import uci.horarioUCI.VistaPrincipalActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class VerificarConexionIntranetdAsyncTask extends
		AsyncTask<Void, String, Boolean> {
	private VistaPrincipalActivity activity;
	private Context context;

	public VerificarConexionIntranetdAsyncTask(VistaPrincipalActivity activity) {
		this.activity = activity;
		context = activity.context;
	}

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
			activity.log("Hay conexion.");
			try {
				activity.conexionValidator.hayPing(
						activity.conexionValidator.handler, 1000);
			} catch (Exception e) {
				Log.v("horario", e.getMessage());
				e.printStackTrace();
			}
		} else {
			activity.log("No hay conexion");
			activity.log("Se inici� en modo Offline.");
			activity.iniciarSplinners();
			VistaPrincipalActivity.progressDialog.dismiss();
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		VistaPrincipalActivity.progressDialog = new ProgressDialog(context);
		VistaPrincipalActivity.progressDialog
				.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		VistaPrincipalActivity.progressDialog.setCancelable(false);
		VistaPrincipalActivity.progressDialog.setMax(100);
		VistaPrincipalActivity.progressDialog
				.setMessage("Comprobando conexion ...");
		VistaPrincipalActivity.progressDialog.show();
	}
}