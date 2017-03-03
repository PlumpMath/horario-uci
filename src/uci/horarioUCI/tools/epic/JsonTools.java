package uci.horarioUCI.tools.epic;

import java.util.Map;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 
 * Herramientas para codificacion/decodificacion(Json) de objetos.
 * 
 * @author David Alejandro Reyes Milian damilian@estudiantes.uci.cu
 */
public class JsonTools extends DTools {
	private Gson gson = new GsonBuilder().setPrettyPrinting()
			.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();;

	public String encode(Object object) {
		return gson.toJson(object);
	}

	public Object decodeObject(Class<?> objectClass, String jsonString) {
		return gson.fromJson(jsonString, objectClass);
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> decodeMap(String jsonString) {
		return gson.fromJson(jsonString, Map.class);
	}
}
