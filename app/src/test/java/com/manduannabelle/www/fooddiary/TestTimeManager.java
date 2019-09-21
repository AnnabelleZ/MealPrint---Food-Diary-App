package com.manduannabelle.www.fooddiary;

import org.junit.Test;

import java.util.Calendar;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class TestTimeManager{

    private static final Calendar CALENDAR = Calendar.getInstance();

    @Test
    public void testFormatTime12AM() throws Exception {
        String actual = TimeManager.formatTime(CALENDAR, 0, 0);
        String expected = "12:00AM";
        assertEquals(expected, actual);
    }

    @Test
    public void testFormatTime12PM() throws Exception {
        String actual = TimeManager.formatTime(CALENDAR, 12, 0);
        String expected = "12:00PM";
        assertEquals(expected, actual);
    }

    @Test
    public void testFormatTimeMorning() throws Exception {
        String actual = TimeManager.formatTime(CALENDAR, 6, 15);
        String expected = "6:15AM";
        assertEquals(expected, actual);
    }

    @Test
    public void testFormatTimeAfternoon() throws Exception {
        String actual = TimeManager.formatTime(CALENDAR, 18, 45);
        String expected = "6:45PM";
        assertEquals(expected, actual);
    }
}