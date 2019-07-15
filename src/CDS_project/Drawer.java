/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CDS_project;

/**
 *
 * @author Bruk
 * this class is written to perform graph plot for the
 * measurements to visually show the effects
 */
public class Drawer {

    public void plot(long n) {
        long temp = n / 1000;
        System.out.print(temp + " k ");
        for(long i = 0; i < n; i+=1000){
            System.out.print("\u001B[44m ");
        }
        System.out.println("\u001B[49m"+"");
        System.out.println("The throughput is in ops/ms");
    }
    
}
