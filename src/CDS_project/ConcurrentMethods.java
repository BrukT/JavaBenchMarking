/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CDS_project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bruk
 * This class is constructed to make the concurrent measurement on different use of locks and semaphores
 */
public class ConcurrentMethods {

    static customData container = new customData();
    static List<Integer> arr = new ArrayList<>();
    static Lock lock = new ReentrantLock();
    static Semaphore permit = new Semaphore(1);
    private static ReentrantLock pauseLock = new ReentrantLock();
    private static Condition unpaused = pauseLock.newCondition();
    private static boolean isPaused;
    static int arraySize = 50;
    static ExecutorService executor;

    public static int lockInALoop(int len) {
        int result = 0;
        for (int i = 0; i < len; i++) {
            lock.lock();
            result += arr.get(i);
            arr.set(i, i + 1);

            lock.unlock();
        }
        return result;
    }

    public synchronized static int lockOutOfLoop(int len) {
        int result = 0;
        for (int i = 0; i < len; i++) {
            result += arr.get(i);
            arr.set(i, i + 1);
        }
        return result;
    }
    static Callable<customData> call = new Callable<customData>() {
        @Override
        public customData call() throws Exception {
            for (int i = 0; i < 2; i++) {
                Future<customData> result = executor.submit(threadCallable);
            }
            return container;
        }
    };

    static Callable<customData> call2 = new Callable<customData>() {
        @Override
        public customData call() throws Exception {
            for (int i = 0; i < 2; i++) {
                Future<customData> result = executor.submit(threadCallable2);
            }
            return container;
        }
    };

    static Callable<customData> threadCallable = () -> {
        container.n = arraySize;
        container.res = lockOutOfLoop(arraySize);
        return container;
    };

    static Callable<customData> threadCallable2 = () -> {
        container.n = arraySize;
        container.res = lockInALoop(arraySize);
        return container;
    };

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < arraySize; i++) {
            arr.add(i);
        }
        executor = Executors.newFixedThreadPool(6);
        NaiveBenchMark benchMark = new NaiveBenchMark();

        //executor = Executors.newFixedThreadPool(2);
        //false is not to show the graph
        benchMark.measure(call2, false);
        //Thread.sleep(2000000);
        executor.shutdown();

    }
}
