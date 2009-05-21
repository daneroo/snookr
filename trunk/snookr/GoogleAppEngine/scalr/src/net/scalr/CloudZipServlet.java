/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.scalr;

import com.google.gson.reflect.TypeToken;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.servlet.ServletOutputStream;
import net.scalr.dao.CloudMapDAO;
import net.scalr.dao.CloudZipDAO;
import org.apache.commons.fileupload.util.Streams;

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
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Scalr zip upload with multipart post handling";
    }// </editor-fold>

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
            Set<String> groups = dao.getAllGroups();
            sos.println("There are " + groups.size() + " CloudMap groups");
            for (String group : groups) {
                sos.println(" " + group + " --> entries:" + "...");
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
                String jsonManifest = new CloudMapDAO().makeManifest(name);
                if (manifest) {
                    sos.println(jsonManifest);
                } else if (verify) {
                    sos.println("Should have verified: -=-=-=-=-");
                    sos.println(jsonManifest);
                    sos.println("-=-=-=-=-=-=-=-=-=-");
                } else { // content
                    sos.println("fetched name=" + name);
                    //sos.write(clm.getContent());
                    sos.println("--- Content goes here -manifest for now- -=-=-=-");
                    sos.println(jsonManifest);
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
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();

            if (ServletFileUpload.isMultipartContent(request)) {
                handleMultipart(out, request);
            } else {
                String message = "IGNORING: Post is not multipart";
                log.warning(message);
                out.println(message);
            }
        } catch (Exception ex) {
            log.warning("Upload Post threw exception: " + ex.getMessage());
            throw new ServletException(ex);
        }
    }

    /* Handle multipart post:
     *  Ignore Form Fields
     *  Treat File Items as zip uploads
     *   -using item.getName() == original File Name as name key
     *   -NOT item.getName() which is the form field name
     */
    private void handleMultipart(PrintWriter out, HttpServletRequest request) throws IOException, FileUploadException {
        ServletFileUpload upload = new ServletFileUpload();
        upload.setFileSizeMax(MAXPOSTSIZE);
        //out.println("Servlet CloudZip POST response (sum)");
        FileItemIterator iterator = upload.getItemIterator(request);
        while (iterator.hasNext()) {
            FileItemStream item = iterator.next();
            InputStream stream = item.openStream();
            if (item.isFormField()) {
                String value = Streams.asString(stream);
                String message = "IGNORING Form field: " + item.getFieldName() + " length: " + value.length() + " value: " + value;
                log.warning(message);
            } else { // File Item
                //String message = "File field: " + item.getFieldName() + " name: " + item.getName();
                CloudZipDAO dao = new CloudZipDAO();
                String name = item.getName();
                //String jsonManifest = dao.updateWithStream(name, stream);
                String jsonManifest = dao.updateWithStreamAndTimeout(name, stream);
                out.println(jsonManifest);
            }
        }
    }

    // unused
    private List<Map<String, String>> decodeManifest(String jsonManifest) {
        List<Map<String, String>> manifestList = null;
        Type listType = new TypeToken<List<Map<String, String>>>() {
        }.getType();

        manifestList = new JSON().decode(jsonManifest, listType);
        return manifestList;
    }

}
