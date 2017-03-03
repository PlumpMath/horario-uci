package uci.horarioUCI.tools.async;

import java.text.SimpleDateFormat;
import java.util.Date;

import uci.horarioUCI.R;
import uci.horarioUCI.VistaPrincipalActivity;
import uci.horarioUCI.tools.HorarioUCIParser;
import android.os.AsyncTask;

public class ObtenerHorarioIntranetdAsyncTask extends

AsyncTask<Void, String, String[][]> {
	private VistaPrincipalActivity activity;

	public ObtenerHorarioIntranetdAsyncTask(VistaPrincipalActivity activity) {
		this.activity = activity;
	}

	String facultad = activity.pref.getFacultad();
	String semana = activity.pref.getSemana();
	String brigada = activity.pref.getBrigada();

	@Override
	protected String[][] doInBackground(Void... params) {
		String[][] horario = new String[][] {};
		horario = new HorarioUCIParser().obtenerHorario(facultad, semana,
				brigada);
		return horario;
	}

	@Override
	protected void onPostExecute(String[][] result) {
		super.onPostExecute(result);
		activity.horarioAsyncTask = result;
		if (!activity.actualizandoBD)
			activity.progressDialog.dismiss();
		if (result == null) {
			activity.log("No existe horario para la brigada " + brigada
					+ " en la semana: " + semana);
			return;
		}
		if (result.length == 0) {
			activity.toastShort("No se pueden obtener los datos en este momento. Inténtelo en otro momento");

			return;
		}
		String cadenaHorario = "";
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result[0].length; j++) {
				cadenaHorario += result[i][j] + "&";
			}
			cadenaHorario += "#";
		}
		if (!activity.actualizandoBD)
			activity.btnVerDescargarHorario.setText(R.string.ver_horario);
		Date dt = new Date();
		boolean am = Integer
				.parseInt(dt.toString().split(" ")[3].split(":")[0]) < 12;
		SimpleDateFormat df = new SimpleDateFormat("d-M-yyyy h:mm:ss");
		String fecha = df.format(dt.getTime()) + (am ? " am" : " pm");
		activity.db.insertarHorarioBD(facultad, semana, brigada, cadenaHorario,
				fecha);
		String[] horario = new String[] { semana, facultad, brigada,
				cadenaHorario, fecha };
		activity.log("Se ha guardado el horario para la brigada " + brigada
				+ " en la semana: " + semana);
		// verHorario(horario);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (!activity.actualizandoBD) {
			activity.showDialogMessage("Buscando horario ...");
		}
	}
}
