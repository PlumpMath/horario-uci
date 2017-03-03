package uci.horarioUCI.tools;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.text.format.DateUtils;

@SuppressLint("NewApi")
public class CalendarManager {
	private static int[] minuto = { 0, 45, 30, 30, 15, 0 };
	private static int[] hora = { 8, 9, 11, 13, 15, 17 };
	long eventId = 0;

	// static String misTurnos = "RP,LP,ESI,CIGS,IA,TSP,MIC,AOS";

	public static long idCalendarioUCI(Context context) {
		System.out.println("idCalendario");
		// < buscar calendario: HorarioUCI
		Cursor cursor = context.getContentResolver().query(CalendarContract.Calendars.CONTENT_URI,
				new String[] { "_id", "name" }, null, null, null);
		cursor.moveToFirst();
		String calName;
		long calId = -1;
		for (int i = 0; i < cursor.getCount(); i++) {
			calName = cursor.getString(1);
			calId = cursor.getLong(0);
			System.out.println("Calendar name: " + calName);
			System.out.println("Calendar id: " + calId);
			if (calName.equals("MiHorarioUCI")) {
				// if existe devuelvo su ID
				System.out.println("Id de calendario UCI: " + calId);
				break;
			}
			cursor.moveToNext();
		}
		cursor.close();
		// si no existe lo creo
		if (calId == -1) {
			calId = crearLocalCalendar(context);
		}
		return calId;
	}

	// crea un nuevo calendario
	public static long crearLocalCalendar(Context context) {
		System.out.println("crearLocalCalendar");
		ContentValues values = new ContentValues();
		values.put(Calendars.ACCOUNT_NAME, "MiHorarioUCI");
		values.put(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
		values.put(Calendars.NAME, "MiHorarioUCI");
		values.put(Calendars.CALENDAR_DISPLAY_NAME, "MiHorarioUCI");
		values.put(Calendars.CALENDAR_COLOR, Color.rgb(39, 119, 37));
		values.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
		values.put(Calendars.OWNER_ACCOUNT, "some.account@googlemail.com");
		values.put(Calendars.CALENDAR_TIME_ZONE, "Europe/London");
		Uri.Builder builder = CalendarContract.Calendars.CONTENT_URI.buildUpon();
		builder.appendQueryParameter(Calendars.ACCOUNT_NAME, "com.navshiftmanager");
		builder.appendQueryParameter(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
		builder.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true");
		Uri uri = context.getContentResolver().insert(builder.build(), values);

		// Now get the CalendarID :
		return Long.parseLong(uri.getLastPathSegment());
	}

	/*
	 * devuelve lista de eventos del calendario(HorarioUCI)(El mes en los
	 * GregorianCalendar comienza en 0!!!) ej:
	 * Utility.getEventosSemanaCalendario(getApplicationContext(), new
	 * GregorianCalendar(2014, 4, 5).getTimeInMillis());
	 */
	public static CalendarEvent[] getEventosSemanaCalendario(Context context, long fechaLunesSemana) {
		// Create a builder to define the time span
		Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when")
				.buildUpon();
		// definir intervalo de tiempo (lunes - domingo)
		ContentUris.appendId(builder, fechaLunesSemana);
		ContentUris.appendId(builder, fechaLunesSemana + (DateUtils.DAY_IN_MILLIS * 7));

		// cursor para buscar todos los eventos en el calendario
		Cursor eventCursor = context.getContentResolver().query(builder.build(),
				new String[] { "title", "begin", "end", "allDay", "event_id" },
				"calendar_id=" + idCalendarioUCI(context), null, "startDay ASC, startMinute ASC");
		int cantEventos = eventCursor.getCount();
		System.out.println("eventCursor count=" + cantEventos);
		CalendarEvent[] events = new CalendarEvent[cantEventos];
		// si existen eventos los guardamos
		for (int i = 0; i < cantEventos; i++) {
			eventCursor.moveToNext();
			events[i] = new CalendarEvent(eventCursor.getString(0),
					new Date(eventCursor.getLong(1)), new Date(eventCursor.getLong(2)),
					!eventCursor.getString(3).equals("0"), eventCursor.getInt(4));
			System.out.println(events[i].getTitle());
			System.out.println(events[i].getId());
		}
		return events;
	}

	// devuelve el CalendarEvent existente para el turno dado
	public static CalendarEvent getEventoCalendario(Context context, long fechaHoraTurno) {
		CalendarEvent evento = null;
		// Create a builder to define the time span
		Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when")
				.buildUpon();
		// definir intervalo de tiempo (lunes - domingo)
		ContentUris.appendId(builder, fechaHoraTurno);
		ContentUris.appendId(builder, fechaHoraTurno + (DateUtils.MINUTE_IN_MILLIS * 90));

		// cursor para buscar todos los eventos en el calendario
		Cursor eventCursor = context.getContentResolver().query(builder.build(),
				new String[] { "title", "begin", "end", "allDay", "event_id" },
				"calendar_id=" + idCalendarioUCI(context), null, "startDay ASC, startMinute ASC");
		int cantEventos = eventCursor.getCount();
		System.out.println("eventCursor count=" + cantEventos);
		if (cantEventos > 0) {
			eventCursor.moveToNext();
			evento = new CalendarEvent(eventCursor.getString(0), new Date(eventCursor.getLong(1)),
					new Date(eventCursor.getLong(2)), !eventCursor.getString(3).equals("0"),
					eventCursor.getInt(4));
		}
		return evento;
	}

	// private static String getDate(long milliSeconds) {
	// SimpleDateFormat formatter = new SimpleDateFormat(
	// "dd/MM/yyyy hh:mm:ss a");
	// Calendar calendar = Calendar.getInstance();
	// calendar.setTimeInMillis(milliSeconds);
	// return formatter.format(calendar.getTime());
	// }

	// annade evento al calendario
	public static long adicionarEventoCalendario(Context context, String title, String addInfo,
			String place, int status, long startDate, boolean needReminder, boolean needMailService) {
		/***************** Event: note(without alert) *******************/
		System.out.println("Addind!!!");
		String eventUriString = "content://com.android.calendar/events";
		ContentValues eventValues = new ContentValues();

		eventValues.put("calendar_id", idCalendarioUCI(context)); // id, We need
																	// to
		// choose from
		// our mobile for primary
		// its 1
		eventValues.put("title", title);
		eventValues.put("description", addInfo);
		eventValues.put("eventLocation", place);

		long endDate = startDate + 1000 * 60 * 60 + 1000 * 60 * 30; // For next
																	// 1:30 hr

		eventValues.put("dtstart", startDate);
		eventValues.put("dtend", endDate);

		eventValues.put("eventTimezone", "GMT -4:00");

		// values.put("allDay", 1); //If it is bithday alarm or such
		// kind (which should remind me for whole day) 0 for false, 1
		// for true
		eventValues.put("eventStatus", status); // This information is
		// sufficient for most
		// entries tentative (0),
		// confirmed (1) or canceled
		// (2):

		// Estos campos los quite porque dan error!!

		// eventValues.put("visibility", 3); // visibility to default (0),
		// confidential (1), private
		// (2), or public (3):
		// eventValues.put("transparency", 0); // You can control whether
		// an event consumes time
		// opaque (0) or transparent
		// (1).
		eventValues.put("hasAlarm", 1); // 0 for false, 1 for true

		Uri eventUri = context.getContentResolver().insert(Uri.parse(eventUriString), eventValues);
		long eventID = Long.parseLong(eventUri.getLastPathSegment());

		if (needReminder) {
			/***************** Event: Reminder(with alert) Adding reminder to event *******************/

			String reminderUriString = "content://com.android.calendar/reminders";

			ContentValues reminderValues = new ContentValues();

			reminderValues.put("event_id", eventID);
			reminderValues.put("minutes", 15); // Default value of the
												// system. Minutes is a
												// integer
			reminderValues.put("method", 1); // Alert Methods: Default(0),
												// Alert(1), Email(2),
												// SMS(3)

			Uri reminderUri = context.getContentResolver().insert(Uri.parse(reminderUriString),
					reminderValues);
		}

		/***************** Event: Meeting(without alert) Adding Attendies to the meeting *******************/

		if (needMailService) {
			String attendeuesesUriString = "content://com.android.calendar/attendees";

			/********
			 * To add multiple attendees need to insert ContentValues multiple
			 * times
			 ***********/
			ContentValues attendeesValues = new ContentValues();

			attendeesValues.put("event_id", eventID);
			attendeesValues.put("attendeeName", "xxxxx"); // Attendees name
			attendeesValues.put("attendeeEmail", "yyyy@gmail.com");// Attendee
																	// E
																	// mail
																	// id
			attendeesValues.put("attendeeRelationship", 0); // Relationship_Attendee(1),
															// Relationship_None(0),
															// Organizer(2),
															// Performer(3),
															// Speaker(4)
			attendeesValues.put("attendeeType", 0); // None(0), Optional(1),
													// Required(2), Resource(3)
			attendeesValues.put("attendeeStatus", 0); // NOne(0), Accepted(1),
														// Decline(2),
														// Invited(3),
														// Tentative(4)

			Uri attendeuesesUri = context.getContentResolver().insert(
					Uri.parse(attendeuesesUriString), attendeesValues);
		}

		return eventID;

	}

	// annade semana completa al calendario
	public static void adicionarSemanaCalendario(Context context, String[][] horario,
			String semanaString) {

		GregorianCalendar fechaLunesSemana = getGCLunesSemana(semanaString);
		System.out.println(fechaLunesSemana.toString());
		borrarSemanaCalendario(context, fechaLunesSemana.getTimeInMillis());
		for (int i = 1; i < 7; i++) {
			GregorianCalendar horaTurno = new GregorianCalendar();
			for (int j = 1; j < 7; j++) {
				if (horario[i][j] == null || horario[i][j].equals(""))
					continue;
				// ----Para validar respecto a turnos predefinidos sustituir el
				// id de arriba
				// if (horario[i][j] == null || horario[i][j].equals("")
				// || !misTurnos.contains(horario[i][j].split(" ")[0]))
				// continue;
				// ----Para validar respecto a turnos predefinidos
				horaTurno.setTimeInMillis(fechaLunesSemana.getTimeInMillis());
				horaTurno.add(GregorianCalendar.DAY_OF_MONTH, j - 1);
				horaTurno.set(GregorianCalendar.HOUR_OF_DAY, hora[i - 1]);
				horaTurno.set(GregorianCalendar.MINUTE, minuto[i - 1]);
				System.out.println(horario[i][j]);
				System.out.println("hora: " + horaTurno.getTime().toGMTString());
				adicionarEventoCalendario(context, horario[i][j], "", "", 0,
						horaTurno.getTimeInMillis(), true, false);
			}
			System.out.println();
		}
	}

	private static GregorianCalendar getGCLunesSemana(String semana) {
		String semanaMod = semana.replace('(', '@');
		int primerDiaSemana = Integer.parseInt(semanaMod.split("@")[1].split("\\.")[0]);
		int mes = Integer.parseInt(semanaMod.split("@")[1].split("\\.")[1]) - 1;
		int ano = Integer.parseInt(semanaMod.split("@")[1].split("\\.")[2].split(" ")[0]);
		return new GregorianCalendar(ano, mes, primerDiaSemana);
	}

	// borra evento del calendario
	@SuppressLint("NewApi")
	private static boolean borrarEventoCalendario(Context context, long eventID) {
		ContentResolver cr = context.getContentResolver();
		// ContentValues values = new ContentValues();
		Uri deleteUri = null;
		deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventID);
		int rows = cr.delete(deleteUri, null, null);
		return rows != 0;
	}

	/*
	 * borra semana completa del calendario a partir de la fecha del lunes de
	 * dicha semana
	 */
	public static void borrarSemanaCalendario(Context context, long fechaLunesSemana) {
		System.out.println(fechaLunesSemana);
		System.out.println(context);
		CalendarEvent[] eventosSemanaCalendario = getEventosSemanaCalendario(context,
				fechaLunesSemana);
		System.out.println(eventosSemanaCalendario.length);
		for (CalendarEvent event : eventosSemanaCalendario)
			borrarEventoCalendario(context, event.getId());
	}

	// annade evento al calendario utilizando el gestor
	public static void addEventToCalendar(Activity activity) {
		Calendar cal = Calendar.getInstance();

		cal.set(Calendar.DAY_OF_MONTH, 29);
		cal.set(Calendar.MONTH, 4);
		cal.set(Calendar.YEAR, 2014);

		cal.set(Calendar.HOUR_OF_DAY, 22);
		cal.set(Calendar.MINUTE, 45);

		Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setType("vnd.android.cursor.item/event");

		intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTimeInMillis());
		intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
				cal.getTimeInMillis() + 60 * 60 * 1000);

		intent.putExtra(Events.ALL_DAY, false);
		intent.putExtra(Events.RRULE, "FREQ=DAILY");
		intent.putExtra(Events.TITLE, "Título de vuestro evento");
		intent.putExtra(Events.DESCRIPTION, "Descripción");
		// intent.putExtra(Events.EVENT_LOCATION, "Calle ....");

		activity.startActivity(intent);
	}

}
