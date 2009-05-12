/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.scalr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author daniel
 */
public class GZIP {

    public byte[] gzip(byte[] uncompressed) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            // Create the GZIP output stream
            // GZIPOutputStream out = new GZIPOutputStream(bos); NORMAL COMPRESSION
            GZIPOutputStream out = new GZIPOutputStream(bos) {

                {
                    def.setLevel(Deflater.BEST_COMPRESSION);
                }
            };
            out.write(uncompressed, 0, uncompressed.length);
            out.finish();
            out.close();
            bos.close();
        } catch (IOException e) {
            Logger.getLogger(GZIP.class.getName()).log(Level.SEVERE, null, e);
        }
        byte[] compressed = bos.toByteArray();

        return compressed;
    }

    public byte[] gunzip(byte[] compressed) {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
        try {
            GZIPInputStream gzis = new GZIPInputStream(bais, compressed.length);
            byte[] uncompressed = IOUtils.toByteArray(gzis);
            gzis.close();
            bais.close();
            return uncompressed;
        } catch (IOException e) {
            Logger.getLogger(GZIP.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }
}
