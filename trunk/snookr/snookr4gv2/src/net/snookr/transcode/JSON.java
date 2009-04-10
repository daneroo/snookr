/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.snookr.transcode;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import net.snookr.model.FSImage;

/**
 *
 * @author daniel
 * http://sites.google.com/site/gson/gson-user-guide#TOC-Using-Gson
 * test:
 *    List of FSImage, (Generic or not)
 *    MultiMap Later
 */
public class JSON {

    Gson gson;

    public JSON() {
        //gson = new Gson();
        gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        //gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    public String encode(List l) {
        //String json = gson.toJson(list,listTpe);// not sure what more this does
        //return gson.toJson(l);
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

    /* Testing code */
    public void test() {
        System.out.println("Hello transcode.JSON");
        testPrimitives();
        testObject();
        testFSImageList();
    }

    private void show(String s) {
        System.out.println(s);
    }

    private FSImage randFSImage() {
        int r = 1000000 + new Random().nextInt(1000000);
        FSImage fsima = new FSImage();
        fsima.fileName = "/pathToFile/image" + r + ".jpg";
        fsima.size = new Long(r);
        fsima.md5 = "0e7eded2e5283be3f6b716c71e7e1c1c";
        fsima.lastModified = new Date(new Date().getTime() + r);
        fsima.taken = fsima.lastModified;
        return fsima;
    }

    private void testObject() {
        show("Test FSImage Object");
        FSImage fsima = randFSImage();
        String json = gson.toJson(fsima);
        show("  " + fsima + " \n    -->\n  " + json);

        FSImage fsima2 = gson.fromJson(json, FSImage.class);
        show("decoded --> " + fsima2);
    }

    private void testFSImageList() {
        show("Test FSImage List");
        List list = new ArrayList();
        list.add(randFSImage());
        list.add(randFSImage());
        list.add(randFSImage());

        String json = encode(list);
        show("  list[3]{fsima^3} \n    -->\n  " + json);

        List list2 = decodeFSImageList(json);
        show("decoded --> ");
        for (Object o : list2) {
            show("   - " + o.getClass() + " : " + o);
        }
    }

    private void testPrimitives() {
        show("Test Primitives");
        show("1 --> " + gson.toJson(1));
        show("abcd --> " + gson.toJson("abcd"));
        show("Long(10) --> " + gson.toJson(new Long(10)));
        int[] values = {1};
        show("int[] {1} --> " + gson.toJson(values));
    }

    public static void main(String[] args) {
        new JSON().test();
    }
}
