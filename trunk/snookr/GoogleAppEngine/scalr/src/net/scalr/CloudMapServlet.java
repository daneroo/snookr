/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.scalr;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletOutputStream;
import net.scalr.dao.CloudMapDAO;
import net.scalr.model.CloudMap;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author daniel
 *   push this with something like:
 *     curl -m 30  -F "value=@filesystem.json.gz;type=application/octet-stream"  http://localhost:8080/upload
 * parameter api:
 *  we need to accomplish
 *    manifest of group
 *    manifest of entry
 *    put (group=defaul,name,content) -> return manifest
 *    put (group=default, {name,content}*) -> return manifest
 *    delete group
 *    delete entry
 *    get (group=default) -> zip of groups contnt
 *    get (group=default, name) -> content
 *  post: either
 *    not nulti-part
 *       "group"->group, "name"->name, "content"->content
 *    multipart
 */
public class CloudMapServlet extends HttpServlet {

    static final int MAXPOSTSIZE = 10 * 1024 * 1024;
    private static final Logger log =
            Logger.getLogger(CloudMapServlet.class.getName());

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
        response.setContentType("text/plain;charset=UTF-8");
        Map<String, String[]> params = request.getParameterMap();
        String names[] = params.get("name");
        boolean manifest = params.get("manifest") != null;
        boolean delete = params.get("delete") != null;

        ServletOutputStream sos = response.getOutputStream();
        // test harness
        boolean testOnly = true;
        if (testOnly) {
            sos.println("invoked CloudMap DAO Test");
            CloudMapDAO tdao = new CloudMapDAO();
            if (params.get("deleteAll") != null) {
                tdao.deleteAll();
            }
            if (params.get("big") != null) {
                sos.println(tdao.testBigRandRW());
            }
            if (params.get("small") != null) {
                sos.println(tdao.testSmallRandRW());
            }
            if (params.get("crud") != null) {
                sos.println(tdao.testCRD());
            }
            return;
        }
        /* validation
         *    names==null (or names.length==0) get List
         */
        log.warning("Servlet CloudMapServlet GET response");
        CloudMapDAO dao = new CloudMapDAO();
        if (names == null || names.length < 1) {
            sos.println("--no key: return map keys (manifest)");
            List<CloudMap> list = dao.getAll();
            sos.println("List has " + list.size() + " CloudMap entries");
            for (CloudMap clm : list) {
                sos.println(" " + clm.getName() + " --> md5:" + MD5.digest(clm.getContent()));
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
                CloudMap clm = dao.get(name);
                if (manifest) {
                    sos.println("fetched name=" + clm.getName() + " md5=" + MD5.digest(clm.getContent()));
                } else { // content
                    sos.write(clm.getContent());
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
        response.setContentType("text/plain;charset=UTF-8");
        ServletOutputStream sos = response.getOutputStream();
        log.warning("Servlet CloudMapServlet POST");

        try {
            Map<String, byte[]> params = getRequestParams(request);
            String name = bytesToString(params.get("name"));
            byte[] content = params.get("content");
            /* validation
             *    names==null (or names.length==0) get List
             */
            CloudMap clm = new CloudMap("default", name, content);
            CloudMapDAO dao = new CloudMapDAO();
            dao.createOrUpdate(clm);
            sos.println("saved  name=" + clm.getName() + " md5=" + MD5.digest(clm.getContent()));
            CloudMap check = dao.get(name);
            sos.println("checked name=" + check.getName() + " md5=" + MD5.digest(check.getContent()));

        } catch (FileUploadException ex) {
            Logger.getLogger(CloudMapServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Scalr CloupMap";
    }// </editor-fold>

    // hook for converting posted "Strings" to bytes...
    private byte[] stringToBytes(String str) {
        return str.getBytes(); // which charset ?
    }

    private String bytesToString(byte[] b) {
        return new String(b); // which charset ?
    }


    // handles both normal post, and delegates for multipart post
    private Map<String, byte[]> getRequestParams(HttpServletRequest request) throws IOException, FileUploadException {
        if (ServletFileUpload.isMultipartContent(request)) {
            log.warning("Multipart POST");
            return getRequestParamsMultipart(request);
        } else {
            log.warning("NOT Multipart POST");
            Map<String, byte[]> params = new HashMap<String, byte[]>();
            Map<String, String[]> servletParamsMap = request.getParameterMap();
            for (Map.Entry<String, String[]> e : servletParamsMap.entrySet()) {
                // extract first parameter!
                String pName = e.getKey();
                String[] pValues = e.getValue();
                String firstValue = pValues[0];
                params.put(pName, stringToBytes(firstValue));
            }
            return params;
        }
    }

    private Map<String, byte[]> getRequestParamsMultipart(HttpServletRequest request) throws IOException, FileUploadException {
        ServletFileUpload upload = new ServletFileUpload();
        upload.setFileSizeMax(MAXPOSTSIZE);

        Map<String, byte[]> params = new HashMap<String, byte[]>();

        FileItemIterator iterator = upload.getItemIterator(request);
        while (iterator.hasNext()) {
            FileItemStream item = iterator.next();
            String name = item.getFieldName();
            byte[] value = null;
            InputStream stream = item.openStream();
            if (item.isFormField()) {
                value = stringToBytes(Streams.asString(stream));
            } else { // File Field : discard item.getName (a.k.a upload file name) for now
                value = IOUtils.toByteArray(stream);
            }
            params.put(name, value);
        }
        return params;
    }
}
