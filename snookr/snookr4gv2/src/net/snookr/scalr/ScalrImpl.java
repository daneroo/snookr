/*
 *  javadoc: http://hc.apache.org/httpclient-3.x/apidocs/
 * Use the legacy httpclient 3.x library to multipart post:
 * commons-httpclient-3.1.jar ( commons-codec-1.3.jar,commons-logging-1.1.1.jar )
 *
 * Decided NOT to use the newer httpcore-4.0.jar, httpclient-4.0-beta2.jar from
 *    not ready yet: http://hc.apache.org/ apache http components
 */
package net.snookr.scalr;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

/**
 *
 * @author daniel
 */
public class ScalrImpl {

    /* Convinience to return as string */
    static final int MAXRETURNBODYLENGTH = 10000000;

    public String postMultipart(String postURL, Map params) {
        byte[] response = postMultipart(postURL, params, MAXRETURNBODYLENGTH);
        log("Body length in bytes: "+response.length);

        //String asString = new String(response, UTF8);
        String asString = new String(response);
        return asString;
    }

    /* Convert params:Map to Part array
     */
    private Part[] paramsToPartList(Map params) throws FileNotFoundException {
        Part[] parts = new Part[params.size()];
        List<Part> partsList = new ArrayList<Part>();
        for (Object key : params.keySet()) {
            Object value = params.get(key);
            String keyString = String.valueOf(key);
            if (value instanceof byte[]) {
                partsList.add(new FilePart(keyString, new ByteArrayPartSource(keyString, (byte[]) value)));
            } else if (value instanceof File) {
                partsList.add(new FilePart(keyString, (File) value));
            } else {
                String valueString = String.valueOf(value);
                partsList.add(new StringPart(keyString, valueString));
            }
        }
        // Generified toArray invocation replaces the original array if sizes don't match
        parts = partsList.toArray(parts);
        return parts;
    }

    private byte[] postMultipart(String postURL, Map params, int maxReturnLength) {
        File f = new File("/Users/daniel/small.txt");
        PostMethod filePost = new PostMethod(postURL);
        try {

            //filePost.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE,false);
            filePost.setContentChunked(true);

            Part[] parts = paramsToPartList(params);
            filePost.setRequestEntity(
                    new MultipartRequestEntity(parts, filePost.getParams()));
            HttpClient client = new HttpClient();

            client.getHttpConnectionManager().
                    getParams().setConnectionTimeout(5000);

            int status = client.executeMethod(filePost);
            if (status == HttpStatus.SC_OK) {
                //String body = filePost.getResponseBodyAsString();
                return filePost.getResponseBody(maxReturnLength);
            } else {
                log("Upload failed, response=" + HttpStatus.getStatusText(status));
            }
        } catch (Exception ex) {
            log("ERROR: " + ex.getClass().getName() + " " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            filePost.releaseConnection();
        }
        return null;
    }

    private void log(String m) {
        System.err.println(m);
    }
}
