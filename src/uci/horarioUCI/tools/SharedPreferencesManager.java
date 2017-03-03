package uci.horarioUCI.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPreferencesManager {
	private final String horarioUCITag = "HorarioUCI.conf";
	private final String facultadTag = "shpref-horarioUCI-facultad";
	private final String semanaTag = "shpref-horarioUCI-semana";
	private final String brigadaTag = "shpref-horarioUCI-brigada";
	private final String horarioTag = "shpref-horarioUCI-horario";
	private final String idAlarmasTag = "shpref-horarioUCI-idAlarmas";
	private final String primerInicioTag = "shpref-horarioUCI-primerInicio";

	private final String ultimaSemanaTag = "shpref-horarioUCI-ultimaSemanaTag";
	private final String ultimaBrigadaTag = "shpref-horarioUCI-ultimaBrigadaTag";
	private final String ultimaFacultadTag = "shpref-horarioUCI-ultimaFacultadTag";

	private final String nulo = "null";
	private SharedPreferences prefs;
	private SharedPreferences.Editor editor;

	public SharedPreferencesManager(Context context) {
		prefs = context.getSharedPreferences(horarioUCITag, 0);
		editor = prefs.edit();
		// db = new HorarioUCISQLiteHelper(context);
	}

	public void addIdAlarma(String id) {
		String idAlarmas = getIdAlarmas();
		idAlarmas += "," + id;
		editor.putString(idAlarmasTag, idAlarmas);
		editor.commit();
		log("Se adiciono la alarma con id: " + id);
	}

	private void log(String string) {
		Log.v("horario", string);
	}

	public void removeIdAlarma(String id) {
		String idAlarmas = getIdAlarmas();
		String idX = "," + id;
		int i;
		if (idAlarmas.contains(idX)) {
			i = idAlarmas.indexOf(idX);
			boolean find = false;
			for (int j = i + 1; !find && j < idAlarmas.length(); j++) {
				if (idAlarmas.charAt(j) == ',') {
					idAlarmas = idAlarmas.substring(0, i)
							+ idAlarmas.substring(j, idAlarmas.length());
					find = true;
				}
			}
			if (!find)
				idAlarmas = idAlarmas.substring(0, i);
		}
		// idAlarmas = idAlarmas.replace(idX, "");
		editor.putString(idAlarmasTag, idAlarmas);
		editor.commit();
		log("Se elimino la alarma con id: " + id);
		log("idAlarmas:  " + idAlarmas);
	}

	public void setFacultad(String entrada) {
		editor.putString(facultadTag, entrada);
		editor.commit();
	}

	public void setSemana(String entrada) {
		editor.putString(semanaTag, entrada);
		editor.commit();
	}

	public void setBrigada(String entrada) {
		editor.putString(brigadaTag, entrada);
		editor.commit();
	}

	public void setHorario(String entrada) {
		editor.putString(horarioTag, entrada);
		editor.commit();
	}

	public void setUltimaFacultad(String index) {
		editor.putString(ultimaFacultadTag, index);
		editor.commit();
	}

	public void setUltimaBrigada(String index) {
		editor.putString(ultimaBrigadaTag, index);
		editor.commit();
	}

	public void setUltimaSemana(String index) {
		editor.putString(ultimaSemanaTag, index);
		editor.commit();
	}

	public void AplicacionIniciada() {
		editor.putBoolean(primerInicioTag, false);
		editor.commit();
	}

	public boolean PrimerInicio() {
		return prefs.getBoolean(primerInicioTag, true);
	}

	public String getFacultad() {
		return prefs.getString(facultadTag, nulo);
	}

	public String getSemana() {
		return prefs.getString(semanaTag, nulo);
	}

	public String getBrigada() {
		return prefs.getString(brigadaTag, nulo);
	}

	public String getHorario() {
		return prefs.getString(horarioTag, nulo);
	}

	public String getUltimaSemana() {
		return prefs.getString(ultimaSemanaTag, nulo);
	}

	public String getUltimaBrigada() {
		return prefs.getString(ultimaBrigadaTag, nulo);
	}

	public String getUltimaFacultad() {
		return prefs.getString(ultimaFacultadTag, nulo);
	}

	public String getIdAlarmas() {
		return prefs.getString(idAlarmasTag, "");
	}

	public String existeAlarma(String ida) {
		String idAlarmas = getIdAlarmas();
		// boolean encontrada = idAlarmas.contains(id);
		// log("La alarma con id: " + id + (encontrada ? " fue" : " no fue")
		// + " encontrada");
		return "";
	}

}
