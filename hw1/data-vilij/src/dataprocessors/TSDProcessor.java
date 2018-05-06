package dataprocessors;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import java.util.stream.Stream;

/**
 * The data files used by this data visualization applications follow a tab-separated format, where each data point is
 * named, labeled, and has a specific location in the 2-dimensional X-Y plane. This class handles the parsing and
 * processing of such data. It also handles exporting the data to a 2-D plot.
 * <p>
 * A sample file in this format has been provided in the application's <code>resources/data</code> folder.
 *
 * @author Ritwik Banerjee
 * @see XYChart
 */
public final class TSDProcessor {

    public static class InvalidDataNameException extends Exception {

        private static final String NAME_ERROR_MSG = "All data instance names must start with the @ character.";

        public InvalidDataNameException(String name) {
            super(String.format("Invalid name '%s'." + NAME_ERROR_MSG, name));
        }
    }

    public static class DupeException extends Exception {
        public DupeException() {
            super(String.format("Dupe at:"));
        }
    }

    private Map<String, String>  dataLabels;
    private Map<String, Point2D> dataPoints;
    private ArrayList<Integer>   errorArray = new ArrayList<>();
    private int                  lineOfDupe;
    private String               dupeName;
    private HashSet              uniqueLabels;

    public TSDProcessor() {
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();
        uniqueLabels = new HashSet();
        lineOfDupe = -1;
    }

    /**
     * Processes the data and populated two {@link Map} objects with the data.
     *
     * @param tsdString the input data provided as a single {@link String}
     * @throws Exception if the input string does not follow the <code>.tsd</code> data format
     */
    public void processString(String tsdString) throws Exception {
        AtomicBoolean hadAnError   = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder();
        Stream.of(tsdString.split("\n"))
              .map(line -> Arrays.asList(line.split("\t")))
              .forEach(list -> {
                  try {
                      String   name  = checkedname(list.get(0));
                      if(isDupe(name)){
                          lineOfDupe = dataLabels.size()+1;
                          dupeName = name;
                          throw new DupeException();
                      }
                      String   label = list.get(1);
                      uniqueLabels.add(label);
                      if(label.equals("")) {
                          throw new InvalidDataNameException("");
                      }
                      String[] pair  = list.get(2).split(",");
                      Point2D  point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));
                      dataLabels.put(name, label);
                      dataPoints.put(name, point);
                  } catch (Exception e) {
                      errorMessage.setLength(0);
                      errorMessage.append(e.getClass().getSimpleName()).append(": ").append(e.getMessage());
                      hadAnError.set(true);
                      errorArray.add(dataLabels.size()+1);
                  }
              });
        if (errorMessage.length() > 0)
            throw new Exception(errorMessage.toString());
    }

    public boolean twoNonNulls(){
        int numNonNulls = 0;
        for(Object label : uniqueLabels){
            if(!label.equals("null")){
                numNonNulls++;
            }
        }
        if(numNonNulls == 2){
            return true;
        }else{
            return false;
        }

    }

    public boolean isDupe(String name){
        for(Map.Entry<String,String> entry: dataLabels.entrySet()){
            if(name.equals(entry.getKey())){
                return true;
            }
        }
        return false;
    }

    /**
     * Exports the data to the specified 2-D chart.
     *
     * @param chart the specified chart
     */
    void toChartData(XYChart<Number, Number> chart) {
        Set<String> labels = new HashSet<>(dataLabels.values());
        for (String label : labels) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(label);
            dataLabels.entrySet().stream().filter(entry -> entry.getValue().equals(label)).forEach(entry -> {
                Point2D point = dataPoints.get(entry.getKey());
                series.getData().add(new XYChart.Data<>(point.getX(), point.getY()));
            });
            chart.getData().add(series);
        }
        hoverThingy(chart);
    }

    public void clear() {
        lineOfDupe = -1;
        dupeName = "";
        errorArray.clear();
        dataPoints.clear();
        dataLabels.clear();
        uniqueLabels.clear();
    }

    private String checkedname(String name) throws InvalidDataNameException {
        if (!name.startsWith("@"))
            throw new InvalidDataNameException(name);
        return name;
    }

    public Map<String, Point2D> getDataPoints(){
        return dataPoints;
    }

    public void hoverThingy(XYChart<Number,Number> chart){
        for (XYChart.Series<Number, Number> s : chart.getData()) {
            for (XYChart.Data<Number, Number> d : s.getData()) {
                Tooltip.install(d.getNode(), new Tooltip(
                        s.getName()));
                d.getNode().setOnMouseEntered(event -> d.getNode().setCursor(Cursor.HAND));
                d.getNode().setOnMouseExited(event -> d.getNode().setCursor(Cursor.DEFAULT));
            }
        }
    }

    public ArrayList<Integer> getErrorArray(){
        return errorArray;
    }

    public int getLineOfDupe(){
        return lineOfDupe;
    }

    public String getDupeName(){
        return dupeName;
    }

    public HashSet getUniqueLabels() { return uniqueLabels; }

    public int getInstanceSize() { return dataPoints.size(); }

    public void setLabels(Map<String, String> labels){
        this.dataLabels = labels;
    }
}
