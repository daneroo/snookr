/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.snookr.transcode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author daniel
 * Build a zipFile with json encoded List parts
 *  get a map of Lists, with key being a String used as zip filename
 */
public class JSONZip {

    private static final int ZIP_ENCODE_LEVEL = Deflater.BEST_COMPRESSION;
    private JSON json;
    public static final List DELETION_MARKER_EMPTYLIST = new ArrayList();
    public static final List PRESERVE_MARKER_EMPTYLIST = new ArrayList();

    public JSONZip() {
        this(new JSON());
    }

    public JSONZip(JSON json) {
        this.json = json;
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
                ZipEntry ze = new ZipEntry(name);

                //String directive = ((part.size() % 2) == 0) ? "ADD_OR_REPLACE" : "DELETE";
                String directive = "ADD_OR_REPLACE";
                if (part == DELETION_MARKER_EMPTYLIST) {
                    directive = "DELETE";
                } else if (part == PRESERVE_MARKER_EMPTYLIST) {
                    directive = "PRESERVE";
                }
                ze.setComment(directive);
                ze.setExtra(directive.getBytes());
                //System.err.println("set Comment: " + ze.getComment());
                zipos.putNextEntry(ze);
                json.encode(part, zipos);
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
                if (ze.getComment() != null) {
                    System.err.println("Comment: " + ze.getComment());
                }
                if (ze.getExtra() != null) {
                    System.err.println("Extra: " + new String(ze.getExtra()));
                }
                if (ze.isDirectory()) {
                    System.err.println("Ignoring directory: " + ze.getName());
                    continue;
                }
                //System.out.println("Reading next entry: " + ze.getName());
                String name = ze.getName();
                List part = json.decode(zipis, listType);
                map.put(name, part);
            } catch (IOException ex) {
                Logger.getLogger(JSONZip.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
        }
        return map;
    }
}
