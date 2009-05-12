/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.scalr;

import com.google.gson.reflect.TypeToken;
import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import java.io.InputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.ServletOutputStream;
import net.scalr.dao.CloudZipDAO;
import net.scalr.model.CloudMap;
import net.scalr.model.CloudZip;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author daniel
 *   push this with something like:
 *     curl -m 30  -F "value=@filesystem.json.gz;type=application/octet-stream"  http://localhost:8080/upload
 */
public class CloudZipServlet extends HttpServlet {

    static final int MAXPOSTSIZE = 10 * 1024 * 1024;
    private static final Logger log =
            Logger.getLogger(CloudZipServlet.class.getName());

    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // hmm not quite
        response.setContentType("text/plain;charset=UTF-8");
        Map<String, String[]> params = request.getParameterMap();
        String names[] = params.get("name");
        boolean manifest = params.get("manifest") != null;
        boolean verify = params.get("verify") != null;
        boolean delete = params.get("delete") != null;

        ServletOutputStream sos = response.getOutputStream();
        /* validation
         *    names==null (or names.length==0) get List
         */
        log.warning("Servlet CloudMapServlet GET response");
        CloudZipDAO dao = new CloudZipDAO();
        if (names == null || names.length < 1) {
            sos.println("--no key: return map keys (manifest)");
            List<String> list = dao.getAllKeys();
            sos.println("List has " + list.size() + " CloudZip entries");
            for (String name : list) {
                sos.println(" " + name + " --> entries:" + "...");
            }

        } else if (names.length > 1) {
            sos.println("error: multiple key names not supported");
        } else { // names.length==1
            String name = names[0];
            if (delete) {
                log.warning("  Deleting name=" + name);
                dao.delete(name);
                sos.println("deleted name=" + name);
            } else {
                log.warning("  Fetching name=" + name);
                CloudZip cz = dao.get(name);
                if (manifest) {
                    sos.println("fetched name=" + cz.getName());
                    sos.println("manifest: -=-=-=-=-");
                    sos.println(cz.getManifest());
                    sos.println("-=-=-=-=-=-=-=-=-=-");
                } else if (verify) {
                    sos.println("fetched name=" + cz.getName());
                    sos.println("verified: -=-=-=-=-");
                    String jsonManifest = makeManifest(cz.getEntries());
                    sos.println(jsonManifest);
                    sos.println("-=-=-=-=-=-=-=-=-=-");
                } else { // content
                    //sos.write(clm.getContent());
                    sos.println("--- Content goes here -manifest for now- -=-=-=-");
                    sos.println(cz.getManifest());
                    sos.println("-=-=-=-=-=-=-=-=-=-");
                }
            }
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            log.warning("Servlet CloudZipServlet POST");
            boolean fullEcho = false;
            if (fullEcho) {
                // OutputStream, so headers not sent to response
                dumpHeaders(request, null);
                echoRequest(request, response);
            } else {
                //Process upload params
                response.setContentType("text/plain");
                PrintWriter out = response.getWriter();

                //dumpHeaders(request, out);
                if (ServletFileUpload.isMultipartContent(request)) {
                    handleMultipart(out, request);
                } else {
                    String message = "IGNORING: Post is not multipart";
                    log.warning(message);
                    out.println(message);
                }
            }
        } catch (Exception ex) {
            log.warning("Upload Post threw exception: " + ex.getMessage());
            throw new ServletException(ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Scalr upload with multipart handling";
    }// </editor-fold>

    /* dump headers to Logs, and writer, if write param not null*/
    private void dumpHeaders(HttpServletRequest request, PrintWriter writer) {
        String message = "POST request headers";
        for (Enumeration headers = request.getHeaderNames(); headers.hasMoreElements();) {
            String headerName = (String) headers.nextElement();
            message = message + "\n" + "  Header[" + headerName + "] = " + request.getHeader(headerName);
        }
        log.warning(message);
        if (writer != null) {
            writer.println(message);
        }

    }

    private void handleMultipart(PrintWriter out, HttpServletRequest request) throws IOException, FileUploadException {
        ServletFileUpload upload = new ServletFileUpload();
        upload.setFileSizeMax(MAXPOSTSIZE);
        out.println("Servlet CloudZip POST response (sum)");
        FileItemIterator iterator = upload.getItemIterator(request);
        while (iterator.hasNext()) {
            FileItemStream item = iterator.next();
            InputStream stream = item.openStream();
            if (item.isFormField()) {
                String value = Streams.asString(stream);
                String message = "Form field: " + item.getFieldName() + " length: " + value.length() + " value: " + value;
                out.println(message);
            } else { // File Item
                boolean inMem = true;
                CloudZip zip = null;
                if (inMem) { // get the array first into memory
                    byte[] content = IOUtils.toByteArray(stream);
                    //String md5sum = MD5.digest(content);
                    //String message = "File field: " + item.getFieldName() + " name: " + item.getName() + " length: " + content.length + " md5: " + md5sum;
                    //log.warning(message);
                    //out.println(message);
                    InputStream is = new ByteArrayInputStream(content);
                    Map<String, byte[]> zipMap = expandZipStream(is);
                    is.close();
                    String name = item.getName();
                    zip = new CloudZip(name, zipMap);
                } else {
                    //String message = "File field: " + item.getFieldName() + " name: " + item.getName();
                    //log.warning(message);
                    //out.println(message);
                    Map<String, byte[]> zipMap = expandZipStream(stream);
                    String name = item.getName();
                    zip = new CloudZip(name, zipMap);
                }

                /*for (Map.Entry<String, byte[]> e : zip.getEntries().entrySet()) {
                String name = e.getKey();
                byte[] innercontent = e.getValue();
                out.println("  -" + name + ": length: " + innercontent.length + " md5: " + MD5.digest(innercontent));
                }
                 */
                Map<String, byte[]> zipEntries = zip.getEntries();
                String jsonManifest = makeManifest(zipEntries);
                //out.println(jsonManifest);
                zip.setManifest(jsonManifest);
                CloudZipDAO dao = new CloudZipDAO();
                dao.createOrReplace(zip);
                List<Map<String, String>> manifestList = decodeManifest(zip.getManifest());
                for (Map<String, String> m : manifestList) {
                    out.println("  +" + m.get("name") + ": length: " + m.get("length") + " md5: " + m.get("md5"));

                }
            }
        }
    }

    /* This assumes that getWriter has not/will not be called
     */
    private void echoRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        IOUtils.copy(request.getInputStream(), response.getOutputStream());
    }

    private Map<String, byte[]> expandZipStream(InputStream is) {
        // LinkedHashMap preserves insertion order in iteration
        Map<String, byte[]> map = new LinkedHashMap<String, byte[]>();
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
                byte[] content = IOUtils.toByteArray(zipis);
                //String md5sum = MD5.digest(content);
                //System.out.println("Read: " + ze.getName() + " length: " + content.length + " md5: " + md5sum);
                map.put(name, content);
            } catch (IOException ex) {
                log.log(Level.SEVERE, null, ex);
                break;
            }
        }
        return map;
    }

    private String makeManifest(Map<String, byte[]> zipMap) {
        List<Map<String, String>> manifestList = new ArrayList<Map<String, String>>();
        //= new LinkedHashMap<String, String>();
        for (Map.Entry<String, byte[]> e : zipMap.entrySet()) {
            String name = e.getKey();
            byte[] content = e.getValue();
            Map<String, String> manifestEntry = new LinkedHashMap<String, String>();
            manifestEntry.put("name", name);
            manifestEntry.put("md5", MD5.digest(content));
            manifestEntry.put("length", "" + content.length);
            manifestList.add(manifestEntry);
        }
        return new JSON().encode(manifestList);
    }

    private List<Map<String, String>> decodeManifest(String jsonManifest) {
        List<Map<String, String>> manifestList = null;
        Type listType = new TypeToken<List<Map<String, String>>>() {
        }.getType();

        manifestList = new JSON().decode(jsonManifest, listType);
        return manifestList;
    }

    /* Chose List representation instead
     *
    private String makeManifestAsMap(Map<String, byte[]> zipMap) {
    Map<String, String> manifestMap = new LinkedHashMap<String, String>();
    for (Map.Entry<String, byte[]> e : zipMap.entrySet()) {
    String name = e.getKey();
    byte[] content = e.getValue();
    manifestMap.put(name, MD5.digest(content));
    }
    return new JSON().encode(manifestMap);
    }
     */
}
