package CDS_project;

/**
 * @version 1.0
 * @authors Zewdie Habtie & Bruk Tekalgn
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/*
This custome naive benchmarking framework is not accurate. As the dynamic optimization of 
JIT is really non deterministic.
We measure the performance of "fibbonaci(35)" operations/time = throughput
by repetitively measuring the throughput of a properly warmed code of the "fibbonaci(35)" 
 */
public class NaiveBenchMark {

    private static final int RANGE = 100;
    private static final int REPEAT = 5;
    private static final int WARMPUP = 15;
    private static final int INNERLOOP = 10000;
    private static final int FORK = 1;
    private static List throughPuts = new ArrayList();
    private static double stdev = 0;
    public static boolean in_graph = false;

    /*
    for the ease of calculating the standard deviation we put each throughput 
    of each iteration in a list "throughputs" and we calculated the standard 
    deviation of list.
     */
    public static void recordTimes(double n) {
        throughPuts.add(n);
    }

    //clears the list for every independent fork of the experiment.
    public static void clearTimes() {
        throughPuts.clear();
    }

    //Throughput measurement using Naive microbenchmark method
    public static void test(String name, int range, int warmpup,
            int repeat, int innerloop, Callable callable) throws Exception {
        double average = 0d;
        for (int fork = 0; fork < FORK; fork++) {
            if(!in_graph){
            System.out.println("");
            System.out.printf("#RUN PROGESS: %.3f %% COMPLETED\n", (double) (fork * 100 / FORK));
            System.out.println("#fork " + (fork + 1) + " of " + FORK);
            }
            int max = repeat + warmpup;
            customData temp = new customData();
            for (int x = 0; x < max; x++) {
                double nops = 0;
                double duration = 0;
                Random var = new Random();
                int d = var.nextInt();
                
                System.gc();//discard the unnecessary objects in the heap befor measurement
                //=============================================================================================
                double start = TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
                while (duration < range) {
                    int i;
                    for (i = 0; i < innerloop; i++) {
                        temp = (customData) callable.call();
                        nops++;
                    }
                    duration = TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS) - start;
                    //==============================================================================================
                }
                
                long throughput = (long) (nops / duration);
                boolean warmup = x < warmpup;
                if (!warmup) {
                    recordTimes(throughput);//save the throughput of each iteration into a list
                    average += throughput;
                }
                if(!in_graph){
                    System.out.println((!warmup ? "Iteration "
                            + Integer.toString(x + 1 - warmpup) + " "
                            + "" : "#warmup Iteration  " + Integer.toString(1 + x))
                            + ": " + throughput + " ops/ms " + temp.getResult());
                }
            }
        }

        average = average / (repeat * FORK);//the MEAN throughput
        if (!in_graph) {
            double margin = findMargin(average);
            System.out.printf("%.3f ±(99.9%%) %.3f ops/ms [Average]\n"
                    + "[CI_L, CI_H] = (%.3f,%.3f)", average, margin, average - margin, average + margin);
            System.out.printf(" ,stDev = %.3f\n", stdev);
            System.out.println(throughPuts.size() + " Measurements");
            float average_time = (float) (1 / average);
            System.out.println("Median throughput: " + findMedian());
            System.out.printf("Average execution time :%6.3e ms \n", average_time);
        } else {
            Drawer d = new Drawer();
            d.plot((long) average);
        }
    }

    public static double findMargin(double mean) {
        double sum = 0;
        int number = throughPuts.size();
        for (int i = 0; i < throughPuts.size(); i++) {
            sum += (mean - (double) throughPuts.get(i)) * (mean - (double) throughPuts.get(i));
        }
        stdev = Math.sqrt(sum / number);
        double margin = 3.291 * stdev / (Math.sqrt(number));
        return margin;
    }
    public static double findMedian(){
        Collections.sort(throughPuts);
        int number = throughPuts.size() / 2;
        double median = (double) throughPuts.get(number);
        return median;
    }
    //Measurement for running the methods
    public void measure(Callable call, boolean in_graph) throws Exception {
        this.in_graph = in_graph;
        this.test("fibonacci(35)", RANGE, WARMPUP, REPEAT,
                INNERLOOP, call);
    }
    
    public void measure(Callable call) throws Exception {
        this.in_graph = false;
        this.test("fibonacci(35)", RANGE, WARMPUP, REPEAT,
                INNERLOOP, call);
    }
}
