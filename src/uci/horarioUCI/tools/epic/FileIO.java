package uci.horarioUCI.tools.epic;

import static uci.horarioUCI.tools.epic.DTools.debug;
import static uci.horarioUCI.tools.epic.DTools.fileIO;
import static uci.horarioUCI.tools.epic.DTools.jsonTools;
import static uci.horarioUCI.tools.epic.DTools.logException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;

/**
 * Clase encargada de E/S de la aplicacion + funcionalidades Json
 * 
 * @author David Alejandro Reyes Milian damilian@estudiantes.uci.cu
 */
public class FileIO {
	// private String workspacePath;

	public BufferedReader fileBufferedReader(String fileName)
			throws IOException {
		return new BufferedReader(new InputStreamReader(new FileInputStream(
				(fileName))));
	}

	public FileInputStream fileReader(String fileName) throws IOException {
		return new FileInputStream(fileName);
	}

	public BufferedWriter fileBufferedWriter(String fileName)
			throws IOException {
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
				fileName)));
	}

	public FileOutputStream fileWriter(String fileName) throws IOException {
		return new FileOutputStream(fileName);
	}

	public static void saveAsJsonFile(Object object, String pathToFile) {
		FileOutputStream out = null;
		try {
			out = fileIO.fileWriter(pathToFile);
			out.write(jsonTools.encode(object).getBytes());
			debug(pathToFile + " saved as json");
		} catch (IOException e) {
			logException(e);
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				logException(e);
			}
		}
	}

	public static Object loadJsonFile(Class<?> objectClass, String pathToFile)
			throws IOException {
		debug("Loading json file: " + pathToFile);
		Object object = null;
		BufferedReader in = null;
		try {
			in = fileIO.fileBufferedReader(pathToFile);
			String jsonString = "", t = "";
			while ((t = in.readLine()) != null)
				jsonString += t;
			debug("Settings loaded");
			object = jsonTools.decodeObject(objectClass, jsonString);
		} catch (IOException e) {
			// :( It's ok we have defaults
			logException(e);
		} catch (NumberFormatException e) {
			// :/ It's ok, defaults save our day
			logException(e);
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				logException(e);
			}
		}
		return object;
	}

	public static void saveAsSerializedObject(Object object, String pathToFile) {
		ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(fileIO.fileWriter(pathToFile));
			out.writeObject(object);
			out.close();
			debug(pathToFile + " saved as serialized object");
		} catch (IOException e) {
			logException(e);
		}
	}

	public static Object loadSerializedObject(String pathToFile) {
		try {
			ObjectInputStream in = new ObjectInputStream(
					fileIO.fileReader(pathToFile));
			debug(pathToFile + " loaded");
			return in.readObject();
		} catch (IOException e) {
			logException(e);
			debug(pathToFile + " doesn't exist");
		} catch (ClassNotFoundException e) {
			logException(e);
		}
		return null;
	}

	public static boolean fileExists(String filePath) {
		return new File(filePath).exists();
	}
}
