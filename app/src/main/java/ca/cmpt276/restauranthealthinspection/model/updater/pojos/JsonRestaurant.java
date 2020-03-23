package ca.cmpt276.restauranthealthinspection.model.updater.pojos;

import java.util.Calendar;
import java.util.Date;

public class JsonRestaurant {
    private String format;
    private String url;
    private Calendar lastModified;
    private Integer position;

    public JsonRestaurant(String format, String url, Date lastModified, Integer position) {
        this.format = format;
        this.url = url;
        this.lastModified = calendarConverter(lastModified);
        this.position = position;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Calendar getLastModified() {
        return lastModified;
    }

    public void setLastModified(Calendar lastModified) {
        this.lastModified = lastModified;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    private static Calendar calendarConverter(Date date) {
        assert date != null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar;
    }
}
