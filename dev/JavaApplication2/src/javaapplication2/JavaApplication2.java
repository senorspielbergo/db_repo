/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication2;

/**
 *
 * @author theralph
 */
public class JavaApplication2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String pol = ",Frank-Walter,Steinmeier,SPD";
        String[] split = pol.split(",");
        
        System.out.println(split[0]);
        System.out.println(split[1]);
        System.out.println(split[2]);
        System.out.println(split[3]);
    }
    
}
