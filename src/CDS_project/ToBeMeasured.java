/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CDS_project;

import static java.nio.file.Files.list;
import java.util.ArrayList;
import static java.util.Collections.list;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bruk
 * Measuring class to illustrate how a given user can use the naiveMeasure class
 * default is to graphically show iterations are not avoided by the jvm even if the result is always the same
 */
public class ToBeMeasured {
    static int n=50;
    // black hole and also state object
    static customData container = new customData();
     
    public static long fibbonaci(int n) {
        int prev = 0, next = 1;
        int result = 0;
        for (int i = 0; i < n; i++) {
            result = prev + next;
            prev = next;
            next = result;
        }
        return result;
    } 
    public static int protect(int n){
        return n;
    }
    public static void nothing(){}
    public static int square(int n){
        return n*n;
    }
    static Callable<customData> call = () -> {
        container.n = protect(n); // here the container is added to prohibit character propagation
        container.res = fibbonaci(n);
        return container;
    };
    
    static Callable<customData> call2 = () -> {
        container.n = protect(n); // here the container is added to prohibit character propagation
        container.res = fibbonaci(2*n);
        return container;
    };
    
    static Callable<customData> call3 = () -> {
        container.n = protect(n); // here the container is added to prohibit character propagation
        container.res = fibbonaci(4*n);
        return container;
    };
    
    public static void main(String... args) throws Exception {
        NaiveBenchMark benchMark = new NaiveBenchMark();
        System.out.println("Measuring fibonacci(n) with n= " + n);
        //the second argument is to make the result in graph
        benchMark.measure(call,true);
        System.out.println("Measuring fibonacci(n) with n= " + 2*n);
        benchMark.measure(call2,true);
        System.out.println("Measuring fibonacci(n) with n= " + 4*n);
        benchMark.measure(call3,true);
    }
}

