/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package green.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author daniel
 */
public interface Handler {
    public Object[] get(ResultSet rs,int cols) throws SQLException;
}
