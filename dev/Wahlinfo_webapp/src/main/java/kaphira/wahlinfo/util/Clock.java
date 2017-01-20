package kaphira.wahlinfo.util;

/**
 * Stopwatch to log the query execution time.
 * @author theralph
 */
public class Clock {
    
    private long startValue;
    private long stopValue;;
    
    
    public void start(){
        startValue = System.currentTimeMillis();
    }
    
    public long stop(){
        stopValue = System.currentTimeMillis();
        return (stopValue - startValue);
    }
    
}
