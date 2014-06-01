package org.mdpnp.vista.rpc;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.osehra.vista.soa.rpc.codec.RpcCodecUtils;

public class RpcHelper {

    public static Calendar parseDoB(String dobStr) {
        Calendar dob;
        if (dobStr.equals("*SENSITIVE*")) { // TODO: find out about sensitive fields in VistA
            dob = new GregorianCalendar();
            dob.set(1967, 6, 10);
        } else {
            dob = RpcCodecUtils.convertFromFileman(dobStr);
        }
        return dob;
    }

    public static Date dob(String dobStr) {
        return parseDoB(dobStr).getTime();
    }
    
    public static String dobToString(Calendar dob) {
//        return String.format("%1$tY-%1$tm-%1$td", dob);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(dob);
    }

    public static String formatSsn(String raw) {
        StringBuilder sb = new StringBuilder();
        if (raw.length() == 9) {
            sb.append(raw.substring(0, 3));
            sb.append("-");
            sb.append(raw.substring(3, 5));
            sb.append("-");
            sb.append(raw.substring(5));
        }
        return sb.toString();
    }
}
