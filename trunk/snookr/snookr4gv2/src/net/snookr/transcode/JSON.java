/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.snookr.transcode;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import net.snookr.model.FSImage;

/**
 *
 * @author daniel
 * http://sites.google.com/site/gson/gson-user-guide#TOC-Using-Gson
 * test:
 *    List of FSImage, (Generic or not)
 *    MultiMap Later
 */
public class JSON {

    private Gson gson;
    public JSON() {
        this(false);
    }

    public JSON(boolean pretty) { // default false: compact
        GsonBuilder gsb = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
        if (pretty) {
            gsb.setPrettyPrinting();
        }
        gson = gsb.create();
    }

    public Gson getGson() {
        return gson;
    }

    public String encode(List l) {
        StringBuffer sb = new StringBuffer();
        encode(l, sb);
        return sb.toString();
    }

    public void encode(List l, Appendable writer) {
        //String json = gson.toJson(list,listTpe);// not sure what more this does
        gson.toJson(l, writer);
    }

    public List decodeFSImageList(String json) {
        return decodeFSImageList(new StringReader(json));
    }

    public List decodeFSImageList(Reader reader) {
        Type listType = new TypeToken<List<FSImage>>() {
        }.getType();
        List l = gson.fromJson(reader, listType);
        return l;
    }

}
