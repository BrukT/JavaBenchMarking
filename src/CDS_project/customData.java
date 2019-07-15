/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CDS_project;

/**
 *
 * @author Bruk
 * These class is used as a blue print for the black hole and state object container
 */
public class customData {
    public int n; //input variable to be returned
    public long res; // output
    public customData(){
        int n = 0;
        int res = 0;
    }
    public long getResult(){
        return res;
    }
    public int getN(){
        return n;
    }
}
