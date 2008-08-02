/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chartapp;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import javax.swing.Timer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;

/**
 *
 * @author daniel
 */
public class PieChartDemo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Hello Charts");
        doPieChart();
    }

    private static void doPieChart() {
        // create a dataset...
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Category 1", 43.2);
        dataset.setValue("Category 2", 27.9);
        dataset.setValue("Category3", 79.5);

        // create a chart...
        JFreeChart chart = ChartFactory.createPieChart("Sample Pie Chart", dataset, true, true, false);

        // Get the plot to modify it's attributes
        PiePlot plot = (PiePlot) chart.getPlot();
        // Custom colors
        plot.setSectionPaint("Category 1", new Color(200, 255, 255));
        plot.setSectionPaint("Category 3", new Color(200, 200, 255));
        // outlines
        plot.setSectionOutlinesVisible(true);
        // set expolded
        plot.setExplodePercent("Category 2", 0.30);


        // create and display a frame... 
        ChartFrame frame = new ChartFrame("Test", chart);
        frame.pack();
        frame.setVisible(true);
    }
}
