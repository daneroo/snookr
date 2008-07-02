/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jointcompilation;

/**
 *
 * @author daniel
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Hello NB6.1 World");
        
        JavaClass jcl = new JavaClass();
        jcl.run();
        GroovyClass gcl = new GroovyClass();
        gcl.run();
    }

}
