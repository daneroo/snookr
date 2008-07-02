/*
Voila
 */

package jointcompilation;

/**
 *
 * @author daniel
 */

public class GroovyClass { //measures things in seconds.
    public void run() { 
        System.out.println("Hello from Groovy Class");
        System.out.println(" --Groovy will now call java");
        new JavaClass().run();

    }
}
