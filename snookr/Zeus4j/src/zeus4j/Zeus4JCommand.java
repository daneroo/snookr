/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zeus4j;

import java.awt.BorderLayout;
import java.awt.TextArea;
import java.awt.image.BufferedImage;
import unc.gamma.BackBuffer;
import unc.gamma.Lightning;

/**
 *
 * @author daniel
 */
public class Zeus4JCommand {

    public static void log(String msg) {
        System.err.println(msg);
    }

    public static final void main(String[] args) {
        log("Hello Zeus");
        BackBuffer backBuffer = new BackBuffer();
        backBuffer.setSize(512, 512);
        TextArea statusBox = new TextArea(4, 10);
        statusBox.append("Press 'Grow' to begin simulation.\n");
        backBuffer.init();

        // NOT useful
        /*
        Thread drawThread = new Thread(backBuffer);
        drawThread.setPriority(1);
        drawThread.start();
         */

        Lightning lightning = new Lightning(64, 64, 1.0F, backBuffer, statusBox);
        lightning.pauseFlag = false;

        backBuffer.setLightning(lightning);
        // run instead of thread start.
        lightning.staticRun();
        /*
        Thread simThread = new Thread(lightning);
        simThread.setPriority(1);
        simThread.start();
         */

        // Start rendering
        BufferedImage bufferedimage = lightning.drawTree();
        if (bufferedimage == null) {
            System.err.println("drawTree return null BufferdImage");
            return;
        }

        /*
         * TODO create fft and uncomment
        
        BufferedImage bufferedimage1 = fft.convolve2D(bufferedimage, lightningRGB);
        
        System.out.println("render: bi1 is " + ((bufferedimage1 == null) ? "null" : "not null"));
        System.out.println("render: bi1.g is " + ((bufferedimage1.getGraphics() == null) ? "null" : "not null"));
        
        
        backBuffer.backBuffer = bufferedimage1;
        backBuffer.backBufferContext = bufferedimage1.getGraphics();
        statusBox.append("Done.\n");
        statusBox.append("The lightning color can be tweaked using the RGB sliders.\n");
         */
        log("We are done");

    }
}
