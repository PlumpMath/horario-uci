package uci.horarioUCI.tools;

import java.util.Date;

public class CalendarEvent implements Comparable<CalendarEvent> {

	private String title;
	private Date begin, end;
	private boolean allDay;
	private int id;

	public CalendarEvent() {
	}

	public CalendarEvent(String title, Date begin, Date end, boolean allDay,
			int id) {
		setTitle(title);
		setBegin(begin);
		setEnd(end);
		setAllDay(allDay);
		setId(id);
	}

	private void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public int getId() {
		return id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getBegin() {
		return begin;
	}

	public void setBegin(Date begin) {
		this.begin = begin;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public boolean isAllDay() {
		return allDay;
	}

	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}

	@Override
	public String toString() {
		return getTitle() + " " + getBegin() + " " + getEnd() + " "
				+ isAllDay() + " " + id;
	}

	@Override
	public int compareTo(CalendarEvent other) {
		// -1 = less, 0 = equal, 1 = greater
		return getBegin().compareTo(other.begin);
	}

}
