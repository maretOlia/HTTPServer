package statistic;

import java.util.Calendar;


/**
 * Created by W on 11.08.2014.
 */
public class RequestsCounter {
    private int counter;
    private Calendar date;

    public RequestsCounter() {
    }

    public int getCounter() {
        return counter;
    }

    public Calendar getDate() {
        return date;
    }

    public void incrementCounter() {
        counter++;
    }

    public void refreshDate(Calendar date) {
        this.date = date;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RequestsCounter)) return false;

        RequestsCounter that = (RequestsCounter) o;

        if (counter != that.counter) return false;
        if (!date.equals(that.date)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = counter;
        result = 31 * result + date.hashCode();
        return result;
    }
}
