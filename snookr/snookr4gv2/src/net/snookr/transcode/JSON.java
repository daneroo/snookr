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
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import net.snookr.model.FSImage;

/**
 *
 * @author daniel
 * http://sites.google.com/site/gson/gson-user-guide#TOC-Using-Gson
 * test:
 *    List of FSImage|FlickrImage 
 *    
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

    public List decodeFSImageListZip(InputStream is) {
        List l = new ArrayList();
        ZipInputStream in = new ZipInputStream(is);
        while (true) {
            try {
                ZipEntry ze = in.getNextEntry();
                if (ze == null) {
                    break;
                }
                if (ze.isDirectory()) {
                    System.err.println("Ignoring directory: " + ze.getName());
                    continue;
                }
                //System.out.println("Reading next entry: " + ze.getName());
                List part = decodeFSImageList(new InputStreamReader(in));
                l.addAll(part);
            } catch (IOException ex) {
                Logger.getLogger(JSON.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
        }
        return l;
    }

    /* This works but keep only as example ZipFile code,
    stream version is OK for now.
    public List decodeFSImageListZip(File f) {
    List l = new ArrayList();

    ZipFile zf;
    try {
    zf = new ZipFile(f);
    Enumeration entries = zf.entries();

    while (entries.hasMoreElements()) {
    try {
    ZipEntry ze = (ZipEntry) entries.nextElement();
    if (ze.isDirectory()) {
    System.err.println("Ignoring directory: " + ze.getName());
    continue;
    }
    //System.out.println("Reading next entry: " + ze.getName());
    List part = decodeFSImageList(new InputStreamReader(zf.getInputStream(ze)));
    l.addAll(part);
    } catch (IOException ex) {
    Logger.getLogger(JSON.class.getName()).log(Level.SEVERE, null, ex);
    break;
    }
    }
    zf.close();
    } catch (ZipException ex) {
    Logger.getLogger(JSON.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
    Logger.getLogger(JSON.class.getName()).log(Level.SEVERE, null, ex);
    }
    return l;


    }*/
    private static final int defaultPartSize = 1000;

    public void encodeZip(List l, OutputStream os, int partSize) {
        try {
            ZipOutputStream out = new ZipOutputStream(os);
            out.setLevel(9);
            for (int sub = 0; sub < l.size(); sub += partSize) {
                List nextPart = l.subList(sub, Math.min(sub + partSize, l.size()));
                String nextPartJson = encode(nextPart);
                // Add ZIP entry to output stream.
                out.putNextEntry(new ZipEntry(String.format("part-%08d.json", sub)));
                // Transfer bytes from to the ZIP file
                out.write(nextPartJson.getBytes());
                // Complete the entry
                out.closeEntry();
            }
            // Complete the ZIP file
            out.close();
        } catch (IOException e) {
        }

    }

    public byte[] encodeZip(List l, int partSize) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (gzipOutter) {
                GZIPOutputStream gzos = new GZIPOutputStream(baos) {

                    {
                        def.setLevel(Deflater.BEST_COMPRESSION);
                    }
                };
                encodeZip(l, gzos, partSize);
                gzos.finish();
                gzos.close();
            } else {
                encodeZip(l, baos, partSize);
            }
            return baos.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(JSON.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List decodeFSImageListZip(byte[] b) {
        if (gzipOutter) {
            ByteArrayInputStream is = new ByteArrayInputStream(b);
            GZIPInputStream gzis = null;
            try {
                gzis = new GZIPInputStream(is);
            } catch (IOException ex) {
                Logger.getLogger(JSON.class.getName()).log(Level.SEVERE, null, ex);
            }
            return decodeFSImageListZip(gzis);
        } else {
            ByteArrayInputStream is = new ByteArrayInputStream(b);
            return decodeFSImageListZip(is);
        }
    }
    static boolean gzipOutter = false;
}
