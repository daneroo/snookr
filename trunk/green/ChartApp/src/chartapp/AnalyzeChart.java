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
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
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
        XYDataset dbdataset = null;
        try {
            dbdataset = new JDBCXYDataset("jdbc:mysql://192.168.3.204/ted", "com.mysql.jdbc.Driver", "aviso", null);
            //((JDBCXYDataset) dbdataset).executeQuery("select stamp,watt from watt where stamp>='2008-08-07 00:00:00' limit 300");
            ((JDBCXYDataset) dbdataset).executeQuery("select stamp,watt from watt where stamp>='2008-08-07 09:00:00'");
            //((JDBCXYDataset) dbdataset).executeQuery("select stamp,watt from watt where stamp>='2008-08-07 09:30:00' and stamp<'2008-08-07 09:45:00'");
        //((JDBCXYDataset) dbdataset).executeQuery("select stamp,watt from watt where stamp>='2008-08-05 00:00:00' and stamp<'2008-08-06 00:00:00'");
        } catch (SQLException ex) {
            Logger.getLogger(AnalyzeChart.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AnalyzeChart.class.getName()).log(Level.SEVERE, null, ex);
        }

        TimeSeries fromdb = new TimeSeries("DB Watts", Millisecond.class);
        TimeSeries maxWatt = new TimeSeries("Max Watts", Millisecond.class);
        TimeSeries minWatt = new TimeSeries("Min Watts", Millisecond.class);

        for (int series = 0; series < 1; series++) {
            int n = dbdataset.getItemCount(series);
            for (int i = 0; i < n; i++) {
                Number xi = dbdataset.getX(series, i);
                Millisecond mi = new Millisecond(new Date(xi.longValue()));
                double yi = dbdataset.getYValue(series, i);
                //System.out.println("m=" + mi + " X Y = (" + xi + "," + yi);
                fromdb.add(mi, yi);
                double mx = 0;
                double mn = 0;
                for (int j = i; j < i + 5 && j < n; j++) {
                    //Number xj = dbdataset.getX(series, j);
                    double yj = dbdataset.getYValue(series, j);
                    mx = Math.max(Math.max(mx, yj - yi), 0);
                    mn = Math.min(Math.min(mn, yj - yi), 0);

                }
                if (mx < 300) {
                    mx = 0;
                }
                maxWatt.add(mi, mx);
                if (mn > -300) {
                    mn = 0;
                }
                minWatt.add(mi, mn);

            }
        //fromdb.addAndOrUpdate(fromdb)
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(fromdb);
        dataset.addSeries(maxWatt);
        dataset.addSeries(minWatt);

        DateAxis domain = new DateAxis("Time");
        NumberAxis range = new NumberAxis("Watts");
        domain.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        range.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        domain.setLabelFont(new Font("SansSerif", Font.PLAIN, 14));
        range.setLabelFont(new Font("SansSerif", Font.PLAIN, 14));

        XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);

        renderer.setSeriesPaint(0, Color.green);
        renderer.setSeriesPaint(1, Color.red);
        renderer.setSeriesPaint(1, Color.blue);
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