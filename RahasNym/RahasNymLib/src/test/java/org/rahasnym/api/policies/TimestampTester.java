package org.rahasnym.api.policies;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.rahasnym.api.Constants;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: hasini
 * Date: 12/14/14
 * Time: 3:25 PM
 */
public class TimestampTester {
    public static void main(String[] args) throws ParseException, JSONException {
        Date now = new Date();
        Timestamp timestamp = new Timestamp(now.getTime());
        String tm = timestamp.toString();
        System.out.println(timestamp);
        System.out.println(tm);

        Timestamp expTS = new Timestamp(now.getTime() + 1200000);
        String expTSS = expTS.toString();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        Date parsedDate = df.parse(tm);
        Timestamp regainTM = new Timestamp(parsedDate.getTime());
        System.out.println(regainTM);

        Date expDatetime = df.parse(expTSS);
        Timestamp exp = new Timestamp(expDatetime.getTime());

        boolean valid = regainTM.after(exp);
        System.out.println(valid);

        JSONObject idtContent = new JSONObject();
        idtContent.put("commitment", "wereww");
        JSONObject idt = new JSONObject();
        idt.put(Constants.IDT, idtContent);
        String idtString = idt.toString();
        System.out.println(idt.toString());

        JSONObject root = new JSONObject(new JSONTokener(idtString));
        JSONObject j = (JSONObject) root.opt(Constants.IDT);
        System.out.println(j.toString());
    }
}
