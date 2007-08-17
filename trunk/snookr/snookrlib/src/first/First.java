/*
 * First.java
 *
 * Created on August 17, 2007, 12:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package first;

/**
 *
 * @author daniel
 */
public class First {
    
    /** Creates a new instance of First */
    public First() {
    }
    
    public static void main(String args[]) {
        System.out.println("There were args.length="+args.length+" arguments passed");
        for (int i=0;i<args.length;i++) {
            System.out.println("arg "+i+": "+args[i]);
        }
    }
    
    
}
