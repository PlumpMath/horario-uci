package uci.horarioUCI.tools;

import static uci.horarioUCI.tools.epic.DTools.logException;
import static uci.horarioUCI.tools.epic.DTools.regEXPTools;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Herramienta para obtener informacion del horario de la UCI - Habana, Cuba
 * 
 * @author droid < damilian@estudiantes.uci.cu >
 * @version 0.1.1
 */
public class HorarioUCIParser {
	private final int columnasHorario = 8;
	private final int filasHorario = 7;
	private final String horarioUrl = "http://horario.uci.cu/Default.aspx";
	static final int domingo = 7, lunes = 1, martes = 2, miercoles = 3,
			jueves = 4, viernes = 5, sabado = 6;
	private Element __VIEWSTATE;
	private Element __EVENTVALIDATION;
	public Document pagina;

	public HorarioUCIParser() {
		// System.setProperty("http.keepAlive", "false");
		try {
			pagina = conect().get();
			obtenerDatosValidacion();
		} catch (IOException e) {
			logException(e);
		}
	}

	//
	/**
	 * Obtiene los datos que se envian por post en cada consulta para validar la
	 * peticion
	 */
	private void obtenerDatosValidacion() {
		__VIEWSTATE = pagina.select("#__VIEWSTATE").first();
		__EVENTVALIDATION = pagina.select("#__EVENTVALIDATION").first();
	}

	private Connection conect() {
		Connection c;
		try {
			c = Jsoup.connect(horarioUrl);
		} catch (Exception e) {
			logException(e);
			c = Jsoup.connect(horarioUrl);
		}
		return c;
	}

	/**
	 * Devuelve el listado de semanas publicadas para la facultad seleccionada
	 * 
	 * @param facultad
	 * @return Lista de semanas
	 */
	public String[] obtenerSemanas(String facultad) {
		pagina = null;
		try {
			pagina = conect()
					.data("__EVENTVALIDATION", __EVENTVALIDATION.val())
					.data("__VIEWSTATE", __VIEWSTATE.val())
					.data("ctlHeader$cmbListaFacultades", facultad).post();
			obtenerDatosValidacion();
		} catch (IOException e) {
			logException(e);
			e.printStackTrace();
		}

		Elements semanasSelectOptions = pagina
				.select("#ctlHeader_cmbSemanas option");
		int cantSemanas = semanasSelectOptions.size();
		String[] semanas = new String[cantSemanas];
		for (int i = 0; i < cantSemanas; i++) {
			semanas[i] = semanasSelectOptions.get(i).val();
		}

		return semanas;
	}

	/**
	 * Devuelve el listado de facultades disponibles
	 * 
	 * @return Lista de facultades
	 */
	public String[] obtenerFacultades() {
		pagina = null;
		try {
			pagina = conect()
					.data("__EVENTVALIDATION", __EVENTVALIDATION.val())
					.data("__VIEWSTATE", __VIEWSTATE.val()).post();
			obtenerDatosValidacion();
		} catch (IOException e) {
			logException(e);
		}

		Elements facultadesSelectOptions = pagina
				.select("#ctlHeader_cmbListaFacultades option");
		int cantFacultades = facultadesSelectOptions.size();
		String[] facultades = new String[cantFacultades];
		for (int i = 0; i < cantFacultades; i++)
			facultades[i] = facultadesSelectOptions.get(i).val();
		return facultades;
	}

	/**
	 * Devuelve la lista de brigadas de la facultad seleccionada
	 * 
	 * @param facultad
	 *            Facultad de la que se desea obtener informacion
	 * @return Lista de brigadas
	 */
	public String[] obtenerBrigadas(String facultad) {
		try {
			pagina = conect()
					.data("__EVENTVALIDATION", __EVENTVALIDATION.val())
					.data("__VIEWSTATE", __VIEWSTATE.val())
					.data("ctlHeader$cmbListaFacultades", facultad).post();
			obtenerDatosValidacion();
		} catch (IOException e) {
			logException(e);
		}

		Elements brigadasSelectOptions = pagina
				.select("#ctlToolBar_BrigadaLB option");
		int cantBrigadas = brigadasSelectOptions.size();
		String[] brigadas = new String[cantBrigadas - 1];
		for (int i = 1, ind = 0; i < cantBrigadas; i++) {
			brigadas[ind++] = brigadasSelectOptions.get(i).val();
		}
		return brigadas;
	}

	/**
	 * Devuelve la lista de brigadas de la facultad seleccionada
	 * 
	 * @param facultad
	 * @param semana
	 * @return Lista de brigadas
	 */
	public String[] obtenerBrigadas(String facultad, String semana) {
		pagina = null;
		try {
			pagina = conect()
					.data("__EVENTVALIDATION", __EVENTVALIDATION.val())
					.data("__VIEWSTATE", __VIEWSTATE.val())
					.data("ctlHeader$cmbListaFacultades", facultad).post();
			obtenerDatosValidacion();
			pagina = null;
			pagina = conect()
					.data("__EVENTVALIDATION", __EVENTVALIDATION.val())
					.data("__VIEWSTATE", __VIEWSTATE.val())
					.data("ctlHeader$cmbListaFacultades", facultad)
					.data("ctlHeader$cmbSemanas", semana)
					.data("ctlToolBar$AoLB", "seleccione")
					.data("ctlToolBar$BrigadaLB", "seleccione")
					.data("ctlToolBar$ActividadLB", "seleccione")
					.data("ctlToolBar$ProfesorLB", "seleccione")
					.data("ctlToolBar$LocalLB", "seleccione").post();
			obtenerDatosValidacion();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Elements brigadasSelectOptions = pagina
				.select("#ctlToolBar_BrigadaLB option");
		int cantBrigadas = brigadasSelectOptions.size();
		String[] brigadas = new String[cantBrigadas];
		for (int i = 1, x = 0; i < cantBrigadas; i++) {
			brigadas[x++] = brigadasSelectOptions.get(i).val();
		}

		return brigadas;
	}

	/**
	 * Obtiene el horario de la $semana para la $brigada de la $facultad
	 * especificadas
	 * 
	 * @param facultad
	 * @param semana
	 * @param brigada
	 * @return Horario como un arreglo bidimencional
	 */
	public String[][] obtenerHorario(String facultad, String semana,
			String brigada) {
		System.out.println(facultad + " " + semana + " " + brigada);
		pagina = null;
		try {
			pagina = conect()
					.data("__EVENTVALIDATION", __EVENTVALIDATION.val())
					.data("__VIEWSTATE", __VIEWSTATE.val())
					.data("ctlHeader$cmbListaFacultades", facultad).post();
			obtenerDatosValidacion();
			pagina = null;
			pagina = conect()
					.data("__EVENTVALIDATION", __EVENTVALIDATION.val())
					.data("__VIEWSTATE", __VIEWSTATE.val())
					.data("ctlHeader$cmbListaFacultades", facultad)
					.data("ctlHeader$cmbSemanas", semana)
					.data("ctlToolBar$AoLB", "seleccione")
					.data("ctlToolBar$BrigadaLB", "seleccione")
					.data("ctlToolBar$ActividadLB", "seleccione")
					.data("ctlToolBar$ProfesorLB", "seleccione")
					.data("ctlToolBar$LocalLB", "seleccione").post();
			obtenerDatosValidacion();
			pagina = conect()
					.data("__EVENTVALIDATION", __EVENTVALIDATION.val())
					.data("__VIEWSTATE", __VIEWSTATE.val())
					.data("ctlHeader$cmbListaFacultades", facultad)
					.data("ctlHeader$cmbSemanas", semana)
					.data("ctlToolBar$AoLB", "seleccione")
					.data("ctlToolBar$BrigadaLB", brigada)
					.data("ctlToolBar$ActividadLB", "seleccione")
					.data("ctlToolBar$ProfesorLB", "seleccione")
					.data("ctlToolBar$LocalLB", "seleccione").post();
		} catch (IOException e) {
			logException(e);
			e.printStackTrace();
		}

		Element tablaHorario = pagina.select("#TABLE1").first();

		if (tablaHorario == null)
			return null;

		String[][] horario = new String[filasHorario][columnasHorario];

		Element celda = null;
		for (int i = 0; i < filasHorario; i++) {
			for (int j = 0; j < columnasHorario; j++) {
				celda = tablaHorario.select("tr").get(i).select("td").get(j);
				horario[i][j] = celda.text();
				celda = null;
			}
		}
		return horario;
	}

	/**
	 * Devuelve el horario para el dia especificado dentro del horario
	 * 
	 * @param dia
	 *            buscado
	 * @param horario
	 *            sobre el que se realiza la busqueda
	 * @return Lista de {@link Clase}s del dia
	 */
	public ArrayList<Clase> obtenerHorarioDelDia(int dia, String[][] horario) {
		ArrayList<Clase> horarioDelDia = new ArrayList<Clase>();
		for (int i = 1; i < filasHorario; i++) {
			String turno = horario[i][dia];
			if (!turno.equals(""))
				horarioDelDia.add(new Clase(i + "", turno));
		}
		return horarioDelDia;
	}

	/**
	 * Devuelve la cantidad de turnos de clase del dia especificado
	 * 
	 * @param dia
	 * @param horario
	 * @return cantidad de turnos
	 */
	public int obtenerCantidadDeTurnosDelDia(int dia, String[][] horario) {
		return obtenerHorarioDelDia(dia, horario).size();
	}

	/**
	 * Entidad que encapsula el momento del dia y los datos de un turno de
	 * clases. Los turnos comienzan en: 1
	 * 
	 * @author droid < damilian@estudiantes.uci.cu >
	 */
	public class Clase {
		public String turno;
		public String datos;

		public Clase(String turno, String datos) {
			this.turno = turno;
			this.datos = datos;
		}

		public String obtenerProfesor() {
			return regEXPTools.parsearClase(datos)[1];
		}
	}

}
