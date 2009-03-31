/* --------------------
 * TEDServiceDemo.java
 * --------------------
 */
package chartapp;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;

/**
 * A demo application showing a dynamically updated chart that displays the
 * current JVM memory usage.
 * <p>
 * IMPORTANT NOTE: THIS DEMO IS DOCUMENTED IN THE JFREECHART DEVELOPER GUIDE.
 * DO NOT MAKE CHANGES WITHOUT UPDATING THE GUIDE ALSO!!
 */
public class AnalyzeChart extends JPanel {

    /**
     * Creates a new application.
     *
     * @param maxAge the maximum age (in milliseconds).
     */
    XYItemRenderer renderer;
    TimeSeriesCollection dataset;
    JFreeChart chart;
    public AnalyzeChart() {
        super(new BorderLayout());
        //TimeSeriesCollection dataset = new EnergyEventExtractor().extractEnergyEvents();
        dataset = new EnergyEventCorrelator().correlateEvents();

        DateAxis domain = new DateAxis("Time");
        NumberAxis range = new NumberAxis("Watts");
        domain.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        range.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        domain.setLabelFont(new Font("SansSerif", Font.PLAIN, 14));
        range.setLabelFont(new Font("SansSerif", Font.PLAIN, 14));

        renderer = new XYLineAndShapeRenderer(true, false);

        renderer.setSeriesPaint(0, Color.green);
        renderer.setSeriesPaint(1, Color.red);
        renderer.setSeriesPaint(2, Color.blue);
        renderer.setSeriesPaint(3, Color.magenta);
        renderer.setSeriesPaint(4, Color.yellow);
        // first arg is line thickness
        renderer.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL));
        XYPlot plot = new XYPlot(dataset, domain, range, renderer);
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.lightGray);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        domain.setAutoRange(true);
        domain.setLowerMargin(0.0);
        domain.setUpperMargin(0.0);
        domain.setTickLabelsVisible(true);
        range.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        chart = new JFreeChart("Power over Time",
                new Font("SansSerif", Font.BOLD, 24), plot, true);
        chart.setBackgroundPaint(Color.white);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 4, 4, 4),
                BorderFactory.createLineBorder(Color.lightGray)));
        add(chartPanel);
    }

    public void startTicker() {
        int REFRESH_INTERVAL_MS = 5000;
        new Ticker(REFRESH_INTERVAL_MS).start();
    }

    /**
     * The data generator.
     */
    class Ticker extends Timer implements ActionListener {

        /**
         * Constructor.
         *
         * @param interval the interval (in milliseconds)
         */
        Ticker(int interval) {
            super(interval, null);
            addActionListener(this);
        }

        /**
         * Adds a new free/total memory reading to the dataset.
         *
         * @param event the action event.
         **/
        TimeSeries ts = null;
        public void actionPerformed(ActionEvent event) {
            // call back into Extractor...
            System.out.println("Tick: " + new Date());
            if (true) return;
            
            chart.setTitle("Power@"+new Date());
            int fivesec = (int)(new Date().getTime()/5000);
            int mod2 = fivesec%2;
            renderer.setSeriesPaint(mod2, Color.green);
            renderer.setSeriesPaint(1-mod2, Color.red);

            if (ts==null) {
                int count = dataset.getSeriesCount();
                ts = dataset.getSeries(count-1);
                dataset.removeSeries(ts); // or count-1
            } else {
                dataset.addSeries(ts);
                ts = null;
            }

        }
    }

    /**
     * Entry point for the sample application.
     *
     * @param args ignored.
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Analyze Demo");
        AnalyzeChart panel = new AnalyzeChart();
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.setBounds(200, 120, 800, 600);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}