/* -------------------- 
 * TEDServiceDemo.java 
 * -------------------- 
 */
package chartapp;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.jdbc.JDBCXYDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataset;
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
    public AnalyzeChart() {
        super(new BorderLayout());

        XYDataset dbdataset = getDBDataset();
        TimeSeries fromdb = copyFirstTimeSeries(dbdataset);

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(fromdb);

        //makeMinMaxDiff(dataset, fromdb);
        extractEnergy(dataset, fromdb);

        DateAxis domain = new DateAxis("Time");
        NumberAxis range = new NumberAxis("Watts");
        domain.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        range.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        domain.setLabelFont(new Font("SansSerif", Font.PLAIN, 14));
        range.setLabelFont(new Font("SansSerif", Font.PLAIN, 14));

        XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);

        renderer.setSeriesPaint(0, Color.green);
        renderer.setSeriesPaint(1, Color.red);
        renderer.setSeriesPaint(2, Color.blue);
        renderer.setSeriesPaint(3, Color.magenta);
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
        JFreeChart chart = new JFreeChart("Power over Time",
                new Font("SansSerif", Font.BOLD, 24), plot, true);
        chart.setBackgroundPaint(Color.white);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 4, 4, 4),
                BorderFactory.createLineBorder(Color.lightGray)));
        add(chartPanel);
    }

    private void extractEnergy(TimeSeriesCollection dataset, TimeSeries fromdb) {
        TimeSeries remaining = new TimeSeries("Remaining Noise", Millisecond.class);
        TimeSeries extracted = new TimeSeries("Extracted", Millisecond.class);

        int n = fromdb.getItemCount();
        for (int i = 0; i < n; i++) {
            TimeSeriesDataItem di = fromdb.getDataItem(i);
            RegularTimePeriod ti = di.getPeriod(); //should be a MilliSecond ?
            double yi = di.getValue().doubleValue();
            remaining.add(ti, yi);
        }
        int extractionIteration = 1;
        while (extractionIteration < 100) {
            /*
             * Each extraction round finds maximal energy step function
             * characterized by start,stop,maxW
             *  where remaining(t)>w for all t in (start,stop)
             */
            double maxE = 0;
            double maxW = 0;
            int maxStart = 0;
            int maxStop = 0;
            long maxDurationMS = 0;

            for (int start = 0; start < n; start++) {

                double maxWForStart = remaining.getDataItem(start).getValue().doubleValue();
                long startTimeMS = remaining.getDataItem(start).getPeriod().getFirstMillisecond();

                for (int stop = start; stop < n; stop++) {
                    maxWForStart = Math.min(maxWForStart, remaining.getDataItem(stop).getValue().doubleValue());
                    long stopTimeMS = remaining.getDataItem(stop).getPeriod().getFirstMillisecond();
                    double maxEForStartStop = (stopTimeMS - startTimeMS) * maxWForStart;
                    if (maxEForStartStop > maxE) {
                        maxStart = start;
                        maxStop = stop;
                        maxDurationMS = stopTimeMS - startTimeMS;
                        maxW = maxWForStart;
                        maxE = maxEForStartStop;
                    //System.out.println("    New MaxE = " + (maxE / 1000 / 60 / 60 / 1000) + " kwh");
                    }
                }
            }
            System.out.println("it:" + extractionIteration + " MaxE = " + (maxE / 1000 / 60 / 60 / 1000) + " kwh @ " + maxW + "w x " + (maxDurationMS / 1000.0) + "s");
            TimeSeries eventSeries = new TimeSeries("Iteration "+extractionIteration, Millisecond.class);
            for (int i = maxStart; i <= maxStop; i++) {
                TimeSeriesDataItem di = remaining.getDataItem(i);
                RegularTimePeriod ti = di.getPeriod(); //should be a MilliSecond ?
                double yi = di.getValue().doubleValue();
                remaining.addOrUpdate(ti, yi - maxW);

                double exi = 0;
                try {
                    exi = extracted.getDataItem(ti).getValue().doubleValue();
                } catch (NullPointerException npe) {
                }
                extracted.addOrUpdate(ti, exi + maxW);

                eventSeries.add(ti, maxW);

            }

            if (extractionIteration<10) {
                dataset.addSeries(eventSeries);
            }
            extractionIteration++;
        }
        dataset.addSeries(remaining);
        dataset.addSeries(extracted);
    }

    private void makeMinMaxDiff(TimeSeriesCollection dataset, TimeSeries fromdb) {
        TimeSeries maxWatt = new TimeSeries("Max Watts", Millisecond.class);
        TimeSeries minWatt = new TimeSeries("Min Watts", Millisecond.class);

        int n = fromdb.getItemCount();
        for (int i = 0; i < n; i++) {
            TimeSeriesDataItem di = fromdb.getDataItem(i);
            RegularTimePeriod ti = di.getPeriod(); //should be a MilliSecond ?
            double yi = di.getValue().doubleValue();
            //System.out.println("(" + ti.getClass().getName() + ") t=" + ti + " y = " + yi);
            double mx = 0;
            double mn = 0;
            for (int j = i; j < i + 5 && j < n; j++) {
                double yj = fromdb.getDataItem(j).getValue().doubleValue();
                mx = Math.max(Math.max(mx, yj - yi), 0);
                mn = Math.min(Math.min(mn, yj - yi), 0);
            }
            if (mx < 300) {
                mx = 0;
            }
            maxWatt.add(ti, mx);
            if (mn > -300) {
                mn = 0;
            }
            minWatt.add(ti, mn);
        }

        dataset.addSeries(maxWatt);
        dataset.addSeries(minWatt);
    }

    private TimeSeries copyFirstTimeSeries(XYDataset dbdataset) {

        TimeSeries fromdb = new TimeSeries("DB Watts", Millisecond.class);
        for (int series = 0; series < 1; series++) {
            int n = dbdataset.getItemCount(series);
            for (int i = 0; i < n; i++) {
                Number xi = dbdataset.getX(series, i);
                Millisecond mi = new Millisecond(new Date(xi.longValue()));
                double yi = dbdataset.getYValue(series, i);
                //System.out.println("m=" + mi + " X Y = (" + xi + "," + yi);
                fromdb.add(mi, yi);
            }
        }
        return fromdb;
    }

    private XYDataset getDBDataset() {
        XYDataset dbdataset = null;
        try {
            dbdataset = new JDBCXYDataset("jdbc:mysql://192.168.3.204/ted", "com.mysql.jdbc.Driver", "aviso", null);
            //((JDBCXYDataset) dbdataset).executeQuery("select stamp,watt from watt where stamp>='2008-08-07 00:00:00' limit 300");
            ((JDBCXYDataset) dbdataset).executeQuery("select stamp,watt from watt where stamp>='2008-08-11 08:00:00'");
            //((JDBCXYDataset) dbdataset).executeQuery("select stamp,watt from watt where stamp>='2008-08-07 09:30:00' and stamp<'2008-08-07 09:45:00'");
        //((JDBCXYDataset) dbdataset).executeQuery("select stamp,watt from watt where stamp>='2008-08-10 00:00:00' and stamp<'2008-08-11 00:00:00'");
        } catch (SQLException ex) {
            Logger.getLogger(AnalyzeChart.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AnalyzeChart.class.getName()).log(Level.SEVERE, null, ex);
        }

        return dbdataset;
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