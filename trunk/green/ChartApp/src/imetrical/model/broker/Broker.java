package imetrical.model.broker;

import imetrical.model.broker.IntHandler;
import imetrical.model.broker.Handler;
import imetrical.model.broker.ObjectHandler;
import java.sql.*;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Broker {

    Logger log = Logger.getLogger(Broker.class.getName());
    static Broker instance;
    private static final String DBDRIVER = "com.mysql.jdbc.Driver";
    //private static final String DBURL = "jdbc:mysql://127.0.0.1/ted";
    //private static final String DBURL = "jdbc:mysql://192.168.5.2/ted";
    //private static final String DBURL = "jdbc:mysql://192.168.3.200/ted?serverTimezone=GMT&useTimezone=true&useJDBCCompliantTimezoneShift=true";
    private static final String DBURL = "jdbc:mysql://192.168.3.200/ted";
    private static final String DBUSER = "aviso";
    private static final String DBPASSWORD = null;

    public static Broker instance() {
        if (instance == null) {
            instance = new Broker();
        }
        return instance;
    }

    Broker() /* throws ServletException */ {
    }

    /*
     * see for DBCP Usage...
     * http://svn.apache.org/viewvc/commons/proper/dbcp/trunk/doc/ManualPoolingDriverExample.java?view=markup
     * 
     */
    private Connection getConnection() {
        try {
            Class.forName(DBDRIVER);
            return DriverManager.getConnection(DBURL, DBUSER, DBPASSWORD);
        //return DriverManager.getConnection(DBURL, DBUSER, DBPASSWORD);
        } catch (SQLException ex) {
            Logger.getLogger(Broker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Broker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int getOneInt(String sql, int defaultValue) {
        int value = defaultValue;
        try {
            Vector<Object[]> v = getObjects(sql, 1, new IntHandler());
            value = (Integer) v.get(0)[0];
        } catch (Exception e) {
        }
        return value;
    }

    public String getOneString(String sql, String defaultValue) {
        String value = defaultValue;
        try {
            Vector<Object[]> v = getObjects(sql, 1, new StringHandler());
            value = (String) v.get(0)[0];
        } catch (Exception e) {
        }
        return value;

    }

    public Vector<Object[]> getObjects(String sqlQuery, int maxRows) {
        return getObjects(sqlQuery, maxRows, new ObjectHandler());
    }

    public Vector<Object[]> getObjects(String sqlQuery, int maxRows, Handler handler) {
        //System.err.println("broker getObjects sql: " + sqlQuery);
        Vector<Object[]> vector = new Vector<Object[]>();
        Statement stmt = null;
        ResultSet rs = null;
        Connection con = null;
        try {
            con = getConnection();
            stmt = con.createStatement();
            if (maxRows > 0) {
                stmt.setMaxRows(maxRows);
            }
            rs = stmt.executeQuery(sqlQuery);
            ResultSetMetaData rsmd = rs.getMetaData();
            int cols = rsmd.getColumnCount();
            while (rs.next()) {
                Object array[] = handler.get(rs, cols);
                vector.add(array);
            }
        } catch (SQLException e) {
            log.severe(e.getMessage());
            vector = null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException s1) {
                    log.severe(s1.getMessage());
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException s2) {
                    log.severe(s2.getMessage());
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException s3) {
                    log.severe(s3.getMessage());
                }
            }
        }
        //System.err.println("broker getObjects return size: " + vector.size());
        return vector;
    }


    // this is meant to get one String from a query
    // get the String value of the first col in the frst row.
    // as in :
    // String date = Broker.instance().getOneString("select max(stamp) from event","default");
    public String getOneStringOld(String sql, String defaultValue) {
        String value = defaultValue;
        Statement stmt = null;
        ResultSet rs = null;
        Connection con = null;
        try {
            con = getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                value = rs.getString(1);
                break;
            }
        } catch (SQLException e) {
            log.severe(e.getMessage());
            return value;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException s1) {
                    log.severe(s1.getMessage());
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException s2) {
                    log.severe(s2.getMessage());
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException s3) {
                    log.severe(s3.getMessage());
                }
            }
        }
        return value;
    }

    public int execute(String sql) {
        return execute(sql, null);
    }

    public int execute(String sql, Object[] params) {
        int rowcount = -1;
        //log.debug("migratesql "+sql);
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement(sql);
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
            }
            rowcount = stmt.executeUpdate();
        } catch (SQLException sqle) {
            log.severe(sqle.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqle2) {
                    log.severe(sqle2.getMessage());
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException sqle3) {
                    log.severe(sqle3.getMessage());
                }
            }
        }
        return rowcount;
    }
}

