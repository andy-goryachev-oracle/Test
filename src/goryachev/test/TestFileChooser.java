package goryachev.test;

import java.text.DecimalFormat;
import java.text.MessageFormat;

public class TestFileChooser {
    private boolean listViewWindowsStyle;
    double baseFileSize = 1000.0;
    String kiloByteString = "{0} KB";
    String megaByteString = "{0} MB";
    String gigaByteString = "{0} GB";
    private static final long MEBI = 1_000_000L;
    private static final long MEGA = 1024L * 1024L;
    private static final long GIBI = 1_000_000_000L;
    
    public static void main(String[] args) {
        t(0, "0 KB");
        t(1, "1 KB");
        t(999, "1 KB");
        t(1000, "1 KB");
        t(1001, "1 KB");
        t(1023, "1 KB");
        t(1024, "1 KB");
        t(1025, "1 KB");
        t(1026, "1 KB");
        
        t(1449, "1.4 KB");
        t(1450, "1.4 KB");
        t(1451, "1.5 KB");
        t(1500-1, "1.5 KB");
        t(1500, "1.5 KB");
        t(1500+1, "1.5 KB");
        
        t(MEBI-1, "1 MB");
        t(MEBI, "1 MB");
        t(MEBI+1, "1 MB");
        t(MEGA-1, "1 MB");
        t(MEGA, "1 MB");
        t(MEGA+1, "1 MB");
        
        t(GIBI-1, "1 GB");
        t(GIBI, "1 GB");
        t(GIBI+1, "1 GB");
        
        t(GIBI + 500_000_000L, "1.5 GB");
        t(GIBI + 600_000_000L, "1.6 GB");
        
        System.err.println("OK");
    }
    
    public static void t(long len, String expected) {
        String res = new TestFileChooser().format(len);
        if (!expected.equals(res)) {
            System.err.println("expected=" + expected + " got=" + res + " for len=" + len);
        }
    }
    
    public String format(long len) {
        String text;
        if (listViewWindowsStyle) {
            if (len == 0) {
                text = MessageFormat.format(kiloByteString, len);
            } else {
                len /= 1000L;
                text = MessageFormat.format(kiloByteString, len + 1);
            }
        } else if (len < 1000L) {
            text = MessageFormat.format(kiloByteString, (len==0 ? 0L : 1L));
        } else {
            double kbVal = formatToDoubleValue(len);
            len = (long)kbVal;
            if (kbVal < baseFileSize) {
                text = MessageFormat.format(kiloByteString, kbVal);
            } else {
                double mbVal = formatToDoubleValue(len);
                len = (long)mbVal;
                if (mbVal < baseFileSize) {
                    text = MessageFormat.format(megaByteString, mbVal);
                } else {
                    double gbVal = formatToDoubleValue(len);
                    text = MessageFormat.format(gigaByteString, gbVal);
                }
            }
        }
        return text;
    }
    
    public double formatToDoubleValue(long len) {
        DecimalFormat df = new DecimalFormat("0.0");
        double val = len/baseFileSize;
        return  Double.valueOf(df.format(val));
    }
}