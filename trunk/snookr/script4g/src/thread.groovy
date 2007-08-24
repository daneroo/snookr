// Idea many threads are better than one
// second idea have threads pull jobs as needed.
import net.snookr.util.Spawner;

def doit = { 
    String name = Thread.currentThread().getName()
    println "  --worker ${name} doing $it"
    try { Thread.sleep(100); } catch (Exception e){}
}

totalwork = 100..<200;
int nThreads=10;
Spawner spawner = new Spawner(totalwork,doit,nThreads);

println "Created workers, now spawn ${nThreads} threads and wait";

spawner.run();

println "Done joining,.. now exit"

