/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package green.model;

import green.model.Handler;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author daniel
 */
public class IntHandler implements Handler {

    public Object[] get(ResultSet rs, int cols) throws SQLException {
        Object array[] = new Object[cols];
        for (int i = 0; i < cols; i++) {
            array[i] = rs.getInt(i + 1);
        }
        return array;
    }
}
