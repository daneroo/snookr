/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imetrical.model.broker;

import chartapp.*;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author daniel
 */
public class ObjectHandler implements Handler {

    public Object[] get(ResultSet rs, int cols) throws SQLException {
        Object array[] = new Object[cols];
        for (int i = 0; i < cols; i++) {
            array[i] = rs.getObject(i + 1);
        }
        return array;
    }
}
