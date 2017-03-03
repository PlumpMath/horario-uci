package uci.horarioUCI.tools.epic;

import static uci.horarioUCI.tools.epic.DTools.debug;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Valida y parsea operaciones de aplicacion
 * 
 * @author David Alejandro Reyes Milian damilian@estudiantes.uci.cu
 */
public class RegEXPTools {
	private String regExpresionClase = "";
	private Pattern regExpresionClaseCompiled;

	public RegEXPTools() {
		regExpresionClase = "(?<nombreClase>[^ ]*)? ?(?<tipoClase>[^ ]*)? ?(?<lugar>(?:Salón|CASIE|Aula|Lab)_?[^ ]*)? ?(?<profesor>.*)?";
		debug("Expresion regular de clase: " + regExpresionClase);
		regExpresionClaseCompiled = Pattern.compile(regExpresionClase);
	}

	public String[] parsearClase(String expresion) {
		String[] partes = new String[4];
		Matcher matcher = regExpresionClaseCompiled.matcher(expresion);
		if (matcher.matches()) {
			debug("Grupos encontrados: " + matcher.groupCount());
			partes[0] = matcher.group(1);
			// partes[0] = matcher.group("nombreClase");
			debug("Nombre de la clase: " + partes[0]);
			partes[1] = matcher.group(2);
			// partes[1] = matcher.group("tipoClase");
			debug("Tipo de la clase: " + partes[1]);
			partes[2] = matcher.group(3);
			// partes[2] = matcher.group("lugar");
			debug("Lugar : " + partes[2]);
			partes[3] = matcher.group(4);
			// partes[3] = matcher.group("profesor");
			debug("Profesor de la clase: " + partes[3]);
		} else {
			debug("No es una expresion de clase valida: " + expresion);
		}
		return partes;
	}

	/**
	 * Realiza pruebas a la expresion regular para validar errores.
	 */
	public void probarExpresionRegularClase() {
		System.out.println("Regular Expression Tests for:\n"
				+ regExpresionClase);
		Vector<String> pruebas = new Vector<String>();
		pruebas.add("EF2 (CP) Yunelsis");
		pruebas.add("FAGO (S) Aula_201 Miguel Angel");
		pruebas.add("EF2 (CP) Yunelsis Escalona");
		pruebas.add("IE2 (CP) CASIE Pedro");
		pruebas.add("PSCT (C) Aula_303 Angel");
		pruebas.add("P2 (PP) Aula_206 Jussienne");
		pruebas.add("EF3 (CP) Aloy ");
		pruebas.add("IA2 (C) Salón_2 Hector");
		pruebas.add("IA2 (C) Sala_2 Hector");
		pruebas.add("IFHE (L) Lab_208 Marisel");

		boolean matches = false;
		System.out
				.println("Pruebas - expresion regular(los espacios son denotados por *)");
		for (String prueba : pruebas) {
			matches = Pattern.matches(regExpresionClase, prueba);
			System.out.println(matches + (matches ? "  : " : " : ")
					+ prueba.replace(" ", "*"));
			parsearClase(prueba);
		}
	}

	/**
	 * Verifica si el correo con asunto mailSubject es un correo DDT(Distributed
	 * Download Tool)
	 * 
	 * @param mailSubject
	 *            Asunto del correo
	 * @return true si es un correo DDT
	 * 
	 */
	public boolean isValidExpression(String mailSubject) {
		boolean matches = Pattern.matches(regExpresionClase, mailSubject);
		debug((matches ? "Es" : "No es") + " una regExp valida: ("
				+ mailSubject + ")");
		return matches;
	}

}
