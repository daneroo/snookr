/* Copyright Notice
 * This file contains proprietary information of Sologlobe Logistique Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 1997-2005
 */

package org.galo.json;

import junit.framework.*;
import com.metaparadigm.jsonrpc.JSONSerializer;
import com.metaparadigm.jsonrpc.MarshallException;
import com.metaparadigm.jsonrpc.UnmarshallException;

public class JSONTest extends TestCase  {

    JSONSerializer ser;
    protected void setUp() throws Exception {
        super.setUp();
        ser = new JSONSerializer();
        //ser.setDebug(true);
        ser.registerDefaultSerializers();
        ser.setMarshallClassHints(false);
    }


    public static void myAssertEquals(Object o1,Object o2) {
        if (o1.getClass().isArray() && o2.getClass().isArray()) {
            assertTrue(java.util.Arrays.equals((Object[])o1,(Object[])o2));
            return;
        }
        assertEquals(o1,o2);
    }

    // test Obj ->Json ->Object
    public void roundTrip(Object o, String json) {
        String json2 = toJSON(o,json);
        Object o2 = fromJSON(json2,o);
    }

    public String toJSON(Object o, String expected) {
        String json = null;
        try {
            json = ser.toJSON(o);
        } catch (MarshallException e) {
        }
        System.out.println("json: "+json);
        myAssertEquals(expected,json);
        return json;
    }

    public Object fromJSON(String json,Object expected) {
        Object o = null;
        try {
            o = ser.fromJSON(json);
        } catch (UnmarshallException e) {
        }
        System.out.println("o.class: "+o.getClass().getName());
        System.out.println("o.toString: "+o);
        myAssertEquals(expected,o);
        return o;
    }

    public void testString() {
        String json = "abcd";
        Object o = json;
        roundTrip(o,json);
    }
    public void testUtf8String() {
        String json = "accent aigu \u00e9";
        Object o = json;
        roundTrip(o,json);
    }

    public void testArrayOfString() {
        String[] o = new String[]{"a","b","c"};
        String json = "[\"a\",\"b\",\"c\"]";
        roundTrip(o,json);
    }
    public void testArray2DOfString() {
        Object o = new String[][]{{"a","b"},{"c","d"}};
        String json = "[[\"a\",\"b\"],[\"c\",\"d\"]]";
        toJSON(o,json);
        // myEquals doesn't do 2d arrays yet...
        //roundTrip(o,json);
    }


    public void NOTtestBack() {
        java.util.Hashtable o = new java.util.Hashtable();
        o.put("name1","value1");
        o.put("name2","value2");
        toJSON(o,"");

        String json = "{ \"name1\":\"value1\", \"name2\":\"value2\"}";
        Object o2 =fromJSON(json,o);


        
    }





}

