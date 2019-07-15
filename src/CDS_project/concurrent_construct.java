/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CDS_project;

import static CDS_project.ConcurrentMethods.arraySize;
import static CDS_project.ConcurrentMethods.container;
import static CDS_project.ConcurrentMethods.lock;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bruk
 */
public class concurrent_construct {
    static customData container = new customData();
    static List<Integer> arr = new ArrayList<>();    
    static Lock lock = new ReentrantLock();
    static Semaphore permit = new Semaphore(1);
    private static ReentrantLock pauseLock = new ReentrantLock();
    private static Condition unpaused = pauseLock.newCondition();
    private static boolean isPaused;
    static int arraySize = 50;
    static ExecutorService executor;

    public static int synchCompLock(int len) {
        int result = 0;
        for (int i = 0; i < len; i++) {
            lock.lock();
            result += arr.get(i);
            arr.set(i, i + 1);

            lock.unlock();
        }
        return result;
    }
    public static int synchCompSema(int len) {
        int result = 0;
        for (int i = 0; i < len; i++) {
            try {
                permit.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger("problem here");
            }
            result += arr.get(i);
            arr.set(i, i + 1);

            permit.release();
        }
        return result;
    }
    
    static Callable<customData> call = () -> {
        container.n = arraySize;
        container.res = synchCompLock(arraySize);
        return container;
    };

    static Callable<customData> call2 = () -> {
        container.n = arraySize;
        container.res = synchCompSema(arraySize);
        return container;
    };
    static Callable<customData> assigner = () -> {
        //executor.submit(call);
        executor.submit(call2);
        return container;
    };
    public static void main(String[] args) throws Exception {
        for (int i = 0; i < arraySize; i++) {
            arr.add(i);
        }
        executor = Executors.newFixedThreadPool(2);
        NaiveBenchMark benchMark = new NaiveBenchMark();

        //false is not to show the graph
        //benchMark.measure(call);
        benchMark.measure(assigner);
    }
}
