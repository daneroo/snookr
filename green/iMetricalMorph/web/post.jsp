<%@page contentType="text/xml" pageEncoding="UTF-8"%><%--
    Document   : post
    Created on : Dec 19, 2008, 11:05:04 PM
    Author     : daniel

    invoked with:
    curl -o tmp.xml http://192.168.5.2/iMetrical/feeds.php
    curl -F "value=@tmp.xml;type=text/xml" http://localhost:8080/iMetricalMorph/post.jsp

--%><%@page import="java.util.*,org.apache.commons.fileupload.*,org.apache.commons.fileupload.disk.*,org.apache.commons.fileupload.servlet.ServletFileUpload"%><%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %><?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//iMetrical//DTD RESPONSE 1.0//EN" "http://www.imetrical.com/DTDs/Response-1.0.dtd">
<%
        String value = null;
        long sizeInBytes = 0;


// Check that we have a file upload request
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (isMultipart) {
            // Create a factory for disk-based file items
            FileItemFactory factory = new DiskFileItemFactory();

            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);

            // Parse the request
            List /* FileItem */ items = upload.parseRequest(request);

            // Process the uploaded items
            Iterator iter = items.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();

                String fieldName = item.getFieldName();
                if (!"value".equals(fieldName)) {
                    continue;
                }

                if (item.isFormField()) {
                    value = item.getString();
                    sizeInBytes = value.getBytes().length;
                } else {
                    sizeInBytes = item.getSize();

                                       String fileName = item.getName();
                    String contentType = item.getContentType();
                    boolean isInMemory = item.isInMemory();
                    // Process a file upload in memory
                    byte[] data = item.get();
                    //applicationScope.set
                    try {
                        value = new String(data);
                    } catch (Exception e) {
                    }

                }
            }
        }
        if (value != null) {
            application.setAttribute("value", value);
%><rsp stat="ok">
    <upload size="<%= sizeInBytes%>" />
    <!-- Generated at <%= new java.util.Date()%> -->
</rsp>
<%

        } else {
%><rsp stat="fail">
    <err code="0" msg="No data was found" />
    <!-- Generated at <%= new java.util.Date()%> -->
</rsp>
<%
        }
%>


