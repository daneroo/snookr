/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.snookr.transcode;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import net.snookr.model.FSImage;
import net.snookr.model.FlickrImage;

/**
 *
 * @author daniel
 * Build a zipFile with json encoded List parts
 *  get a map of Lists, with key being a String used as zip filename
 */
public class JSONZip {

    public static Type FSImageListType = new TypeToken<List<FSImage>>() {
    }.getType();
    public static Type FlickrImageListType = new TypeToken<List<FlickrImage>>() {
    }.getType();
    public static final int ZIP_ENCODE_LEVEL = Deflater.BEST_COMPRESSION;
    private Gson gson;
    private boolean pretty = false;

    public JSONZip() {
        GsonBuilder gsb = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
        if (pretty) {
            gsb.setPrettyPrinting();
        }
        gson = gsb.create();
    }

    private byte[] encodePlaceHolder(List list) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        encodePlaceHolder(list, baos);
        return baos.toByteArray();
    }

    private void encodePlaceHolder(List list, OutputStream os) {
        try {
            Writer writer = new OutputStreamWriter(os);
            gson.toJson(list, writer);
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(JSONZip.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private List decodePlaceHolder(byte[] zipBytes, Type listType) {
        ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);
        return decodePlaceHolder(bais, listType);
    }

    private List decodePlaceHolder(InputStream is, Type listType) {
        List list = gson.fromJson(new InputStreamReader(is), listType);
        return list;
    }

    public byte[] encode(Map<String, List> map) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        encode(map, baos);
        return baos.toByteArray();
    }

    public void encode(Map<String, List> map, OutputStream os) {
        ZipOutputStream zipos = new ZipOutputStream(os);
        zipos.setLevel(ZIP_ENCODE_LEVEL);
        try {
            for (Map.Entry<String, List> e : map.entrySet()) {
                String name = e.getKey();
                List part = e.getValue();

                //System.err.println("Zipping: " + name+" sz: "+part.size());

                zipos.putNextEntry(new ZipEntry(name));
                //out.write(encodedJSONBytes);
                encodePlaceHolder(part, zipos);
                zipos.closeEntry();
            }
            zipos.close();
        } catch (IOException ex) {
            Logger.getLogger(JSONZip.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // All lists are expected to Contain  the same typed contained parts
    public Map<String, List> decode(byte[] zipBytes, Type listType) {
        ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);
        return decode(bais, listType);
    }

    public Map<String, List> decode(InputStream is, Type listType) {
        Map<String, List> map = new HashMap<String, List>();
        ZipInputStream zipis = new ZipInputStream(is);
        while (true) {
            try {
                ZipEntry ze = zipis.getNextEntry();
                if (ze == null) {
                    break;
                }
                if (ze.isDirectory()) {
                    System.err.println("Ignoring directory: " + ze.getName());
                    continue;
                }
                //System.out.println("Reading next entry: " + ze.getName());
                String name = ze.getName();
                List part = decodePlaceHolder(zipis, listType);
                map.put(name, part);
            } catch (IOException ex) {
                Logger.getLogger(JSONZip.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
        }
        return map;
    }
}
