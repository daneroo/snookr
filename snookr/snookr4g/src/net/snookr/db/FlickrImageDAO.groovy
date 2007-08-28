/*
 * FlickrImageDAO.java
 *
 * Created on August 28, 2007, 2:45 AM
 *
 * Leave as groovy source because of dependancy on Database.groovy (till grovy-1.1 compiler!
 */

package net.snookr.db;
import net.snookr.model.FlickrImage;

/**
 *
 * @author daniel
 */
public class FlickrImageDAO {
    
    //TODO Proper Dependancy injection
    static Database db;
    static void setDatabase(Database aDatabase) {
        db = aDatabase;
    }
    static Database getDatabase() {
        return db;
    }
    
    /** Creates a new instance of FlickrImageDAO */
    public FlickrImageDAO() {
    }
    
    FlickrImage fetchForPrimaryKey(String photoid) {
        return (FlickrImage)getDatabase().fetchUniqueByValue(FSImage.class,"photoid",photoid);
    }

    List fetchForMD5(String md5) {
        return getDatabase().fetchByValue(FSImage.class,"md5",(Object)md5);
    }

}
