/*
 * FSImageDAO.java
 *
 * Created on August 28, 2007, 2:45 AM
 *
 * Leave as groovy source because of dependancy on Database.groovy (till grovy-1.1 compiler!
 */

package net.snookr.db;

import net.snookr.model.FSImage;

/**
 *
 * @author daniel
 */
public class FSImageDAO {
    
    //TODO Proper Dependancy injection
    static Database db;
    static void setDatabase(Database aDatabase) {
        db = aDatabase;
    }
    static Database getDatabase() {
        return db;
    }
    
    /** Creates a new instance of FSImageDAO */
    public FSImageDAO() {
    }
    
    FSImage fetchForPrimaryKey(String fileName) {
        return (FSImage)getDatabase().fetchUniqueByValue(FSImage.class,"fileName",fileName);
    }

    List fetchForMD5(String md5) {
        return getDatabase().fetchByValue(FSImage.class,"md5",(Object)md5);
    }

}
