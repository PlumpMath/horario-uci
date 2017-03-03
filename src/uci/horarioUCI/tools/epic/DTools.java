package uci.horarioUCI.tools.epic;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Agrupa algunas herramientas para facilitar su uso
 * 
 * @author David Alejandro Reyes Milian damilian@estudiantes.uci.cu
 */
public class DTools {

	// private static final String logsPath =
	// DistributedDownloadTool.class.getProtectionDomain()
	// .getCodeSource().getLocation().getPath()
	// + "logs/";
	public static boolean debug = true;
	private static File logFile = new File(global.file_log);
	public static DTools dTools = new DTools();
	private static BufferedWriter output;
	public static FileIO fileIO = new FileIO();
	public static JsonTools jsonTools = new JsonTools();
	public static RegEXPTools regEXPTools = new RegEXPTools();

	/**
	 * Logs objects as error messages
	 * 
	 * @param obj
	 *            Object to be loged as an error
	 */
	public static void logError(Object obj) {
		saveInLogs("logError: " + obj.toString());
		Logger.getLogger(DTools.class.getName()).log(Level.SEVERE,
				obj.toString());
	}

	/**
	 * Guarda una excepcion en el archivo de logs
	 * 
	 * @param e
	 *            Excepcion a guardar en el archivo de logs
	 */
	public static void logException(Exception e) {
		String stackTrace = e.getMessage() + ":\n";
		for (StackTraceElement element : e.getStackTrace())
			stackTrace += element + "\n";
		saveInLogs("logException: " + stackTrace);
		// Logger.getLogger(DTools.class.getName()).log(Level.SEVERE,
		// stackTrace);
	}

	/**
	 * Logs objects as info messages
	 * 
	 * @param obj
	 *            Object to be loged as an info
	 */
	public static void logInfo(Object obj) {
		saveInLogs("logInfo: " + obj.toString());
		Logger.getLogger(DTools.class.getName())
				.log(Level.INFO, obj.toString());
	}

	/**
	 * Imprime obj.toString() en la salida estandar
	 * 
	 * @param obj
	 *            Objeto a imprimir
	 */
	public static void print(Object obj) {
		System.out.println(obj.toString());
	}

	public static void debug(Object obj) {
		boolean empty = obj.toString().equals("");
		String msg = (!empty ? "<" : "") + obj.toString() + (!empty ? ">" : "");
		saveInLogs("debuger: " + msg);
		if (debug)
			System.out.println(msg);
	}

	/**
	 * Guarda un log en el archivo de logs. Si el tamano de este archivo
	 * sobrepasa la medida impuesta se borra inmediatamente.
	 * 
	 * @param log
	 *            Cadena de texto a guardar
	 * 
	 */
	private static void saveInLogs(String log) {
		try {
			// si el archivo de logs > 1 MB es borrado
			if (logFile.length() >= global.const_logsFileSizeMb * 1048576) {
				System.out
						.println("El tamano del archivo de logs excede el limite de "
								+ global.const_logsFileSizeMb
								+ " Mb por lo que sera reiniciado");
				if (logFile.delete())
					System.out.println("Archivo de logs borrado");
				else
					System.out
							.println("No se puede borrar el archivo de logs.");
			}
			output = new BufferedWriter(new FileWriter(global.file_log, true));
			// output = new BufferedWriter(new FileWriter(logsPath + logFile,
			// true));
			output.append(DateFormat.getDateTimeInstance(DateFormat.DEFAULT, 1)
					.format(new Date()) + " - ");
			output.append(log);
			output.newLine();
			output.close();
		} catch (IOException e) {
			logException(e);
		} catch (SecurityException e) {
			logException(e);
		}
	}

	public static void clearLogFile() {
		new File(global.file_log).delete();
		try {
			new File(global.file_log).createNewFile();
			debug("Log file cleared");
		} catch (IOException e) {
			logException(e);
		}
	}
}
