package uci.horarioUCI.tools.async;

import uci.horarioUCI.VistaPrincipalActivity;
import uci.horarioUCI.tools.HorarioUCIParser;
import uci.horarioUCI.tools.SharedPreferencesManager;
import android.os.AsyncTask;

public class ObtenerBrigadasIntranetAsyncTask extends
		AsyncTask<Void, String, String[]> {
	private SharedPreferencesManager pref;
	private VistaPrincipalActivity activity;

	public ObtenerBrigadasIntranetAsyncTask(VistaPrincipalActivity activity) {
		this.activity = activity;
	}

	@Override
	protected String[] doInBackground(Void... params) {
		try {
			HorarioUCIParser horarioUCIParser = new HorarioUCIParser();
			String brigada = pref.getBrigada();
			return horarioUCIParser.obtenerBrigadas(brigada);
		} catch (Exception e) {
			activity.log(e.getMessage());
		}
		return null;
	}

	@Override
	protected void onPostExecute(String[] result) {
		super.onPostExecute(result);
		activity.brigadas = result;
		activity.actualizarSpinner(activity.spinnerBrigadas, activity.brigadas);
		VistaPrincipalActivity.progressDialog.dismiss();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		activity.showDialogMessage("Cargando brigadas ...");
	}
}