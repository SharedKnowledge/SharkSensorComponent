package net.sharksystem.sensor;

import java.text.ParseException;
import java.util.Date;

public interface DateHelper {

    String dateToString(Date date);
    Date StringToDate(String dateString) throws ParseException;
}
