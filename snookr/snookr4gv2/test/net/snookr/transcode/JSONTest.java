/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.snookr.transcode;

import com.google.gson.Gson;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.snookr.model.FSImage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author daniel
 */
public class JSONTest {

    public JSONTest() {
    }

    private FSImage makeFSImage(int r) {
        FSImage fsima = new FSImage();
        fsima.fileName = "/pathToFile/image" + (r + 1000000) + ".jpg";
        fsima.size = new Long(r + 1000000);
        fsima.md5 = "0e7eded2e5283be3f6b716c71e7e1c1c";
        fsima.lastModified = new Date(0 + r * 3600 * 1000);
        fsima.taken = fsima.lastModified;
        return fsima;
    }
    private final String knownFSImageList12JSON = "[{\"fileName\":\"/pathToFile/image1000001.jpg\",\"size\":1000001,\"md5\":\"0e7eded2e5283be3f6b716c71e7e1c1c\",\"lastModified\":\"1969-12-31 20:00:00\",\"taken\":\"1969-12-31 20:00:00\"},{\"fileName\":\"/pathToFile/image1000002.jpg\",\"size\":1000002,\"md5\":\"0e7eded2e5283be3f6b716c71e7e1c1c\",\"lastModified\":\"1969-12-31 21:00:00\",\"taken\":\"1969-12-31 21:00:00\"}]";

    private List makeFSImageList() {
        List list = new ArrayList();
        list.add(makeFSImage(1));
        list.add(makeFSImage(2));
        return list;
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of encode method, of class JSON.
     */
    @Test
    public void testEncode_List() {
        System.out.println("encode");
        List l = makeFSImageList();
        JSON instance = new JSON();
        String expResult = knownFSImageList12JSON;
        String result = instance.encode(l);
        assertEquals(expResult, result);
    }

    /**
     * Test of encode method, of class JSON.
     */
    @Test
    public void testEncode_List_Appendable() {
        System.out.println("encode");
        List l = makeFSImageList();
        StringBuffer sb = new StringBuffer();
        Appendable writer = sb;
        JSON instance = new JSON();
        instance.encode(l, writer);
        String expResult = knownFSImageList12JSON;
        String result = sb.toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of decodeFSImageList method, of class JSON.
     */
    @Test
    public void testDecodeFSImageList_String() {
        System.out.println("decodeFSImageList");
        String json = knownFSImageList12JSON;
        JSON instance = new JSON();
        List expResult = makeFSImageList();
        List result = instance.decodeFSImageList(json);
        assertEquals("Size", expResult.size(), result.size());
        assertEquals("List(0)", expResult.get(0), result.get(0));
        assertEquals("List", expResult, result);
    }

    /**
     * Test of decodeFSImageList method, of class JSON.
     */
    @Test
    public void testDecodeFSImageList_Reader() {
        System.out.println("decodeFSImageList");
        Reader reader = new StringReader(knownFSImageList12JSON);
        JSON instance = new JSON();
        List expResult = makeFSImageList();
        List result = instance.decodeFSImageList(reader);
        assertEquals("Size", expResult.size(), result.size());
        assertEquals("List(0)", expResult.get(0), result.get(0));
        assertEquals("List", expResult, result);
    }

    @Test
    public void testFSImageRoundtrip() {
        System.out.println("FSImage Roundtrip");
        JSON instance = new JSON();
        FSImage fsima = makeFSImage(42);
        String json = instance.getGson().toJson(fsima);
        FSImage fsima2 = instance.getGson().fromJson(json, FSImage.class);
        assertEquals("FSIma", fsima, fsima2);
    }

    @Test
    public void testPrimitives() {
        System.out.println("Test Primitives");
        JSON instance = new JSON();
        Gson gson = instance.getGson();
        assertEquals("int", "1", gson.toJson(1));
        assertEquals("String", "\"abcd\"", gson.toJson("abcd"));
        assertEquals("Long", "10", gson.toJson(new Long(10)));
        assertEquals("int[]", "[1,3,5]", gson.toJson(new int[]{1,3,5}));
    }
}