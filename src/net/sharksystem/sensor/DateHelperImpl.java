package net.sharksystem.sensor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelperImpl implements DateHelper {

    private final SimpleDateFormat DATE_FORMAT;

    DateHelperImpl(SimpleDateFormat format){
        this.DATE_FORMAT = format;
    }

    @Override
    public String dateToString(Date date) {
        return DATE_FORMAT.format(date);
    }

    @Override
    public Date StringToDate(String dateString) throws ParseException {
        return this.DATE_FORMAT.parse(dateString);
    }
}
