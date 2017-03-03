package uci.horarioUCI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import uci.horario.deprecated.HorarioUCISQLiteHelper;
import uci.horarioUCI.tools.AsyncTaskMan;
import uci.horarioUCI.tools.HorarioUCIParser;
import uci.horarioUCI.tools.SharedPreferencesManager;
import uci.horarioUCI.tools.async.ObtenerBrigadasIntranetAsyncTask;
import uci.horarioUCI.tools.async.ObtenerHorarioIntranetdAsyncTask;
import uci.horarioUCI.tools.async.ObtenerSemanasIntranetAsyncTask;
import uci.horarioUCI.tools.async.VerificarConexionIntranetdAsyncTask;
import uci.horarioUCI.tools.epic.ConexionValidator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class VistaPrincipalActivity extends Activity {

	public static SharedPreferencesManager pref;
	public static ProgressDialog progressDialog;
	public HorarioUCISQLiteHelper db;
	public boolean online;
	public boolean actualizandoBD;
	public Context context;
	public String[] brigadas, semanas;
	public String[][] horarioAsyncTask;
	public static final String ipHorario = "http://horario.uci.cu";
	public ConexionValidator conexionValidator;

	private Spinner spinnerFacultades;
	public Spinner spinnerSemanas;
	public Spinner spinnerBrigadas;
	public Button btnVerDescargarHorario;
	AsyncTaskMan taskMan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		context = this;
		online = false;
		conexionValidator = new ConexionValidator(this);
		setContentView(R.layout.activity_principal);

		// ------------------------------

		// btnVerDescargarHorario = (Button)
		// findViewById(R.id.btnVerDescargarHorario);
		// db = new HorarioUCISQLiteHelper(context);
		// pref = new SharedPreferencesManager(context);
		//
		// brigadas = new String[] {};
		// semanas = new String[] {};
		// horarioAsyncTask = new String[][] {};
		//
		// // log("inicio copia");
		// // db.exportDatabase("horarioUCI");
		//
		// progressDialog = new ProgressDialog(context);
		// progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// progressDialog.setCancelable(false);
		// progressDialog.setMax(100);
		//
		// btnVerDescargarHorario.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // si existe localmente el horario, lo mostramos
		// if (btnVerDescargarHorario.getText().charAt(0) == 'V') {
		// if (!datosValidos(facultad(), semana(), brigada())) {
		// return;
		// }
		// } else {// sino
		// // Si hay conexion lo descargamos de la intranet
		// if (online) {
		// dialogDescargarHorario();
		// }
		// }
		// }
		// });
		//
		// if (pref.PrimerInicio()) {
		// // new CrearBDdAsyncTask().execute();
		// } else
		// iniciarSplinners();

	}

	public void log(String s) {
		Log.v("horario", s);
	}

	boolean datosValidos(String facultad, String semana, String brigada) {
		boolean datosValidos = facultad.charAt(0) == 'F'
				&& !semana.equals("Seleccione la semana")
				&& !brigada.equals("Seleccione la brigada");
		return datosValidos;
	}

	private String facultadSeleccionada() {
		return spinnerFacultades.getSelectedItem().toString();
	}

	private String semanaSeleccionada() {
		return spinnerSemanas.getSelectedItem().toString();
	}

	private String brigadaSeleccionada() {
		return spinnerBrigadas.getSelectedItem().toString();
	}

	public void iniciarSplinners() {
		iniciarSpinnerFacultades();
		iniciarSpinnerSemanasBrigadas();
	}

	void enable(Button button) {
		button.setBackgroundColor(Color.parseColor("#33b5e5"));
		button.setEnabled(true);
	}

	void disable(Button button) {
		button.setBackgroundColor(Color.parseColor("#abe8ff"));
		button.setEnabled(false);
	}

	void iniciarSpinnerSemanasBrigadas() {
		OnItemSelectedListener listener = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				if (!datosValidos(facultadSeleccionada(), semanaSeleccionada(),
						brigadaSeleccionada())) {
					// btnVerDescargarHorario.setAlpha((float) (0.3));
					// btnVerDescargarHorario.setVisibility(-1);
					disable(btnVerDescargarHorario);
					return;
				}
				if (existeHorarioLocal(facultadSeleccionada(),
						semanaSeleccionada(), brigadaSeleccionada())) {
					btnVerDescargarHorario.setText(R.string.ver_horario);
					// btnVerDescargarHorario.setAlpha(1);
					// btnVerDescargarHorario.setVisibility(1);
					enable(btnVerDescargarHorario);
				} else {
					btnVerDescargarHorario
							.setText(R.string.descargar_y_ver_horario);
					if (online)
						// btnVerDescargarHorario.setAlpha(1);
						// btnVerDescargarHorario.setVisibility(1);
						enable(btnVerDescargarHorario);
					else
						// btnVerDescargarHorario.setAlpha((float) (0.3));
						// btnVerDescargarHorario.setVisibility(-1);
						disable(btnVerDescargarHorario);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		};
		spinnerSemanas = (Spinner) findViewById(R.id.spinnerSemanas);
		spinnerBrigadas = (Spinner) findViewById(R.id.spinnerBrigadas);
		spinnerSemanas.setOnItemSelectedListener(listener);
		spinnerBrigadas.setOnItemSelectedListener(listener);
		actualizarSpinner(spinnerSemanas,
				new String[] { "Seleccione la semana" });
		actualizarSpinner(spinnerBrigadas,
				new String[] { "Seleccione la brigada" });
	}

	/**
	 * Inicia el spinner facultades con las facultades obtenidas del horario
	 * (solo online)
	 */
	void iniciarSpinnerFacultades() {
		spinnerFacultades = (Spinner) findViewById(R.id.spinnerFacultades);
		if (online) {
			ArrayAdapter<CharSequence> adapter = ArrayAdapter
					.createFromResource(context, R.array.facultades,
							android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerFacultades.setAdapter(adapter);
			spinnerFacultades
					.setOnItemSelectedListener(spinnerFacultadesOnItemSelectedListener);
		}

		if (actualizandoBD)
			return;

	}

	/**
	 * Accion del evento de seleccion de item en el spinner Facultades. Si se
	 * selecciona una facultad(comienzan con 'F') y el programa esta online se
	 * obtienen las semanas para la facultad seleccionada
	 */
	OnItemSelectedListener spinnerFacultadesOnItemSelectedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// Si se selecciona una facultad
			String facultad = facultadSeleccionada();
			if (facultad.charAt(0) == 'F') {
				if (online)
					obtenerSemanasIntranet(facultad);
				else {
					// Mensaje de falta de conexion!
				}
			}
			// si no no ocurre nada
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

	/**
	 * Actualiza el spinner con los datos especificados
	 * 
	 * @param spinner
	 *            spinner a actualizar
	 * @param datos
	 *            String[] de datos
	 */
	public void actualizarSpinner(Spinner spinner, String[] datos) {
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
				context, android.R.layout.simple_spinner_item, datos);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	/**
	 * Construye y muestra un dialog para confirmar si el horario es guardado en
	 * el dispositivo
	 */
	void dialogDescargarHorario() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage("Desea buscar el horario en la intranet para guardarlo en el dispositivo?");
		builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				obtenerHorarioIntranet();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		});
		// Muestra el dialog
		builder.create().show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.opciones, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_cambiarModo:
			if (online) {
				toastShort("La aplicación no tiene acceso a la red.");
				online = false;
				iniciarSplinners();
			} else {
				online = false;
				new VerificarConexionIntranetdAsyncTask(this).execute();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void showDialogMessage(String mensaje) {
		progressDialog.setMessage(mensaje);
		progressDialog.show();
	}

	// AsyncTasks!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	public void obtenerBrigadasIntranet(String facultad) {
		pref.setBrigada(facultad);
		new ObtenerBrigadasIntranetAsyncTask(this).execute();
	}

	private void obtenerSemanasIntranet(String facultad) {
		pref.setFacultad(facultad);
		new ObtenerSemanasIntranetAsyncTask(this).execute();
	}

	private void obtenerHorarioIntranet() {
		pref.setFacultad(facultadSeleccionada());
		pref.setSemana(semanaSeleccionada());
		pref.setBrigada(brigadaSeleccionada());
		new ObtenerHorarioIntranetdAsyncTask(this).execute();
	}

	public void toastShort(String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	public void toastLarge(String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

}
