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
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.jfree.data.jdbc.JDBCXYDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;
import service.ReadTEDService;

/** 
 * A demo application showing a dynamically updated chart that displays the 
 * current JVM memory usage. 
 * <p> 
 * IMPORTANT NOTE: THIS DEMO IS DOCUMENTED IN THE JFREECHART DEVELOPER GUIDE. 
 * DO NOT MAKE CHANGES WITHOUT UPDATING THE GUIDE ALSO!! 
 */
public class DBChartApp extends JPanel {


    /** 
     * Creates a new application. 
     * 
     * @param maxAge the maximum age (in milliseconds). 
     */
    public DBChartApp() {
        super(new BorderLayout());
        XYDataset dataset=null;
        try {
            dataset = new JDBCXYDataset("jdbc:mysql://192.168.3.199/ted", "com.mysql.jdbc.Driver", "aviso", null);
            //((JDBCXYDataset)dataset).executeQuery("select stamp,watt from watt where stamp>='2008-08-07 00:00:00' limit 300");
            //((JDBCXYDataset)dataset).executeQuery("select stamp,watt from watt where stamp>='2008-08-08 00:00:00'");
            ((JDBCXYDataset)dataset).executeQuery("select stamp,watt from wattminute order by stamp");
            //((JDBCXYDataset)dataset).executeQuery("select stamp,watt from watthour order by stamp");
            //((JDBCXYDataset)dataset).executeQuery("select stamp,watt from wattday order by stamp");
            //((JDBCXYDataset)dataset).executeQuery("select stamp,watt from watt where stamp>='2008-08-05 00:00:00' and stamp<'2008-08-06 00:00:00'");
        } catch (SQLException ex) {
            Logger.getLogger(DBChartApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBChartApp.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        DateAxis domain = new DateAxis("Time");
        NumberAxis range = new NumberAxis("Watts");
        domain.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        range.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        domain.setLabelFont(new Font("SansSerif", Font.PLAIN, 14));
        range.setLabelFont(new Font("SansSerif", Font.PLAIN, 14));

        XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);

        renderer.setSeriesPaint(0, Color.green);
        renderer.setSeriesPaint(1, Color.red);
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
        JFrame frame = new JFrame("Memory Usage Demo");
        DBChartApp panel = new DBChartApp();
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.setBounds(200, 120, 600, 280);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}