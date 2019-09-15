package vipjokerstudio.com.telegramchart;

import org.junit.Test;

import vipjokerstudio.com.telegramchart.util.DateUtil;
import vipjokerstudio.com.telegramchart.util.MathUtil;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testMap(){
        final float map = MathUtil.map(6, 0, 10, 2000, 1000);
        System.out.println(map);
        assertEquals(500,map,0.1);
    }

    @Test
    public void testFormatDate(){
        final String formattedDate = DateUtil.formatDate(1542412800000L);
        assertEquals("Date" , "Nov 17",formattedDate);

    }
}