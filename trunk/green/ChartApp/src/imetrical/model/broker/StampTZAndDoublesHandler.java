/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imetrical.model.broker;

import imetrical.time.TimeManip;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author daniel
 */
public class StampTZAndDoublesHandler implements Handler {
    public Object[] get(ResultSet rs, int cols) throws SQLException {
        Object array[] = new Object[cols];
        for (int i = 0; i < cols; i++) {
            if (i == 0) {
                array[i] = TimeManip.parseISOTZ(rs.getString(i + 1));
            } else {
                array[i] = rs.getDouble(i + 1);
            }
        }
        return array;
    }
}
