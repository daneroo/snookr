<%-- 
    Document   : feeds
    Created on : Dec 19, 2008, 10:47:42 PM
    Author     : daniel
--%><%@page contentType="text/xml" pageEncoding="UTF-8"%><%

        try {
            Object val = application.getAttribute("value");
            if (val != null) {
                out.println(val);
            }
        } catch (Exception e) {
%><?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//iMetrical//DTD OBSFEEDS 1.0//EN" "http://www.imetrical.com/DTDs/ObservationFeeds-1.0.dtd">
<feeds>
   <!-- Empty feed: no content set yet -->
   <!-- Generated at <%= new java.util.Date()%> -->
</feeds>
<%        }

%>
