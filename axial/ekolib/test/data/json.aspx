<%@ Page Language="C#" %><%
    /*string kv(string k,string v){
        return "\""+k+"\":\""+v+"\"";
    }*/
    string mime = "default";
    string cs = "default";
    string ct = null;
    if (Request.Params["mime"] != null)
    {
        mime = Request.Params["mime"];
    }
    if (Request.Params["cs"] != null)
    {
        cs = Request.Params["cs"];
    }
    if (mime!="default"){
        if (cs=="default"){
            ct = mime;
        } else {
            ct = mime+"; charset="+cs;
        }
    } else { // default mime
        if (cs!="default"){
            ct = "application/json; charset="+cs;
        }
    }
    if (ct != null){
        Response.AddHeader("Content-type", ct);
    }
    System.Web.HttpUtility.HtmlDecode("&eacute;");
    string eacuteISO = System.Web.HttpUtility.HtmlDecode("&eacute;");
    string eacuteUTF = System.Web.HttpUtility.HtmlDecode("&eacute;");
    string eacuteOK = eacuteUTF;
    if (cs=="ISO-8859-1"){
        eacuteOK = eacuteISO;
    }
    System.Collections.Generic.List<string> dico = new System.Collections.Generic.List<string>();
    dico.Add("\"key\":\"value\"");
    dico.Add("\"mime\":\""+mime+"\"");
    dico.Add("\"charset\":\""+cs+"\"");
    dico.Add("\"eacuteOK\":\""+eacuteOK+"\"");
    dico.Add("\"eacuteISO\":\""+eacuteISO+"\"");
    dico.Add("\"eacuteUTF\":\"" + eacuteUTF + "\"");
    if (Request.Params["input"] != null)
    {
        string inp = Request.Params["input"];
        dico.Add("\"input\":\"" + inp + "\"");
        string hex = "";
        foreach (char c in inp)
        {
            int tmp = c;
            hex += String.Format("{0:x2}", (uint)System.Convert.ToUInt32(tmp.ToString()));
        }
        dico.Add("\"hex\":\"" + hex + "\"");
    }
    string json = "{"+string.Join(",",dico.ToArray())+"}";
    Response.Write(json);
%>