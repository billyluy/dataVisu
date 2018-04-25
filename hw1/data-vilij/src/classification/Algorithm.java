package classification;

public interface Algorithm extends Runnable {

    int getMaxIterations();

    int getUpdateInterval();

    boolean tocontinue();
}
