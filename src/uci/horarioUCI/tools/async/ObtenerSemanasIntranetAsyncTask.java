package uci.horarioUCI.tools.async;

import uci.horarioUCI.VistaPrincipalActivity;
import uci.horarioUCI.tools.HorarioUCIParser;
import android.os.AsyncTask;

public class ObtenerSemanasIntranetAsyncTask extends
		AsyncTask<Void, String, String[]> {
	private VistaPrincipalActivity activity;

	public ObtenerSemanasIntranetAsyncTask(VistaPrincipalActivity activity) {
		this.activity = activity;
	}

	String facultad = activity.pref.getFacultad();

	@Override
	protected String[] doInBackground(Void... params) {
		HorarioUCIParser horarioUCIParser = new HorarioUCIParser();
		return horarioUCIParser.obtenerSemanas(facultad);
	}

	@Override
	protected void onPostExecute(String[] result) {
		if (result == null) {
			activity.toastShort("No se pueden obtener los datos en este momento. Int�ntelo m�s tarde");
			activity.progressDialog.dismiss();
			super.onPostExecute(result);
			return;
		}
		activity.semanas = result;
		activity.actualizarSpinner(activity.spinnerSemanas, activity.semanas);
		activity.progressDialog.dismiss();
		activity.obtenerBrigadasIntranet(facultad);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		activity.showDialogMessage("Cargando semanas ...");
	}
}