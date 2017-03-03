package uci.horario.deprecated;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class HorarioUCISQLiteHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "horarioUCI.db";
	private static final String DB_PATH_OLD = "/data/data/uci.horarioUCI/databases/";
	private static final String DB_PATH = "//data//uci.horarioUCI//databases//";
	public static final String facultad = "facultad";
	public static final String semana = "semana";
	public static final String brigada = "brigada";
	public static final String horario = "horario";
	public static final String fechaActualizacion = "fechaActualizacion";
	String sqlCreateCleanDatabase = "CREATE TABLE horario (idhorario INTEGER PRIMARY KEY AUTOINCREMENT, semana TEXT, facultad TEXT, brigada TEXT, horario TEXT, fechaActualizacion TEXT)";
	String sqlCreateUpdatedDatabase = "CREATE TABLE horario (idhorario INTEGER PRIMARY KEY AUTOINCREMENT, semana TEXT, facultad TEXT, brigada TEXT, horario TEXT, fechaActualizacion TEXT)";

	Context context;
	private SQLiteDatabase db;

	public HorarioUCISQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, 1);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// db.execSQL(sqlCreateCleanDatabase);
		// log("Creating database");
		// print("Base creada");
		// checkDB();
	}

	void print(String s) {
		Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Log.v("horarioUCI", "Updating database");
		// // Se elimina la versión anterior de la tabla
		// db.execSQL("DROP TABLE IF EXISTS horario");
		// // Se crea la nueva versión de la tabla
		// db.execSQL(sqlCreateCleanDatabase);
	}

	public void open() throws SQLException {
		db = getWritableDatabase();
	}

	@Override
	public synchronized void close() {
		if (db != null)
			db.close();
		super.close();
	}

	public String[] buscarHorarioBD(String facultad, String semana,
			String brigada) {
		open();
		Vector<String> result = new Vector<String>();
		String sql = "select * from horario where brigada = '" + brigada
				+ "' and semana = '" + semana + "' and facultad = '" + facultad
				+ "'";
		Cursor cursor = db.rawQuery(sql, null);
		String[] horario = new String[5];
		while (cursor.moveToNext()) {
			result.add("Semana: " + cursor.getString(1) + " ,Facultad: "
					+ cursor.getString(2) + " ,brigada: " + cursor.getString(3)
					+ " ,horario: " + cursor.getString(4) + " ,fecha: "
					+ cursor.getString(5));
			horario[0] = cursor.getString(1);
			horario[1] = cursor.getString(2);
			horario[2] = cursor.getString(3);
			horario[3] = cursor.getString(4);
			horario[4] = cursor.getString(5);
		}
		cursor.close();
		db.close();
		if (result.size() == 0) {
			print("No existe horario local para estos datos");
			return null;
		}
		return horario;
	}

	public String[] buscarListaSemanasBD(String facultad) {
		open();
		Vector<String> result = new Vector<String>();
		String sql = "select DISTINCT semana from horario where facultad = '"
				+ facultad + "' ORDER BY idhorario;";
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			String semana = cursor.getString(0);
			result.add(semana);
			log(semana);
		}
		cursor.close();
		db.close();
		String[] listaSemanas = new String[] {};
		if (result.size() == 0) {
			log("No existen semanas en la base de datos");
			return null;
		}
		return result.toArray(listaSemanas);
	}

	public String[] buscarListaBrigadasBD(String facultad) {
		open();
		Vector<String> result = new Vector<String>();
		String sql = "select DISTINCT brigada from horario where facultad = '"
				+ facultad + "' ORDER BY brigada ASC";
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			String brigada = cursor.getString(0);
			result.add(brigada);
			log(brigada);

		}
		cursor.close();
		db.close();
		String[] listaBrigadas = new String[] {};
		if (result.size() == 0) {
			log("No existen brigadas en la base de datos");
			return null;
		}
		return result.toArray(listaBrigadas);
	}

	public void insertarHorarioBD(String facultad, String semana,
			String brigada, String horario, String fechaActualizacion) {
		open();
		// Conocer si existe en la bd:
		Vector<String> result = new Vector<String>();
		String sql = "select * from horario where brigada = '" + brigada
				+ "' and semana = '" + semana + "' and facultad = '" + facultad
				+ "'";
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			result.add("horario: " + cursor.getString(4));
		}
		cursor.close();
		boolean existe = result.size() > 0;

		ContentValues values = new ContentValues();
		values.put(HorarioUCISQLiteHelper.semana, semana);
		values.put(HorarioUCISQLiteHelper.facultad, facultad);
		values.put(HorarioUCISQLiteHelper.brigada, brigada);
		values.put(HorarioUCISQLiteHelper.horario, horario);
		values.put(HorarioUCISQLiteHelper.fechaActualizacion,
				fechaActualizacion);
		if (existe) {
			String where = "brigada = '" + brigada + "' and semana = '"
					+ semana + "' and facultad = '" + facultad + "'";
			db.update("horario", values, where, null);
			log("Se actualizo el horario de la brigada " + brigada
					+ " en la semana: " + semana);
		} else {
			db.insert("horario", HorarioUCISQLiteHelper.facultad, values);
			log("Se inserto el horario de la brigada " + brigada
					+ " en la semana: " + semana);
		}
		db.close();
	}

	private void log(String string) {
		Log.v("horario", string);

	}

	public void exportDatabase(String databaseName) {

		try {
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();

			if (sd.canWrite()) {
				log("canWrite");
				String currentDBPath = DB_PATH + DATABASE_NAME;
				String backupDBPath = "horario.db";
				File currentDB = new File(data, currentDBPath);
				File backupDB = new File(sd, backupDBPath);

				if (currentDB.exists()) {
					log("here");
					FileChannel src = new FileInputStream(currentDB)
							.getChannel();
					FileChannel dst = new FileOutputStream(backupDB)
							.getChannel();
					long transferFrom = dst.transferFrom(src, 0, src.size());
					log("Transfer: " + transferFrom);
					src.close();
					dst.close();
					log("here2");
				}
			}
		} catch (Exception e) {
			log(e.getMessage());
		}
	}

	public void importDB() throws IOException {
		open();
		InputStream open = context.getAssets().open("sql.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(open));
		String line = "";
		while ((line = reader.readLine()) != null) {
			log(line);
			db.execSQL(line);
		}
		close();
	}
}
