/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ece326.hw3;


/**
 *
 * @author Tsatanis Panagiotis
 */
public class Homework3 {
    
    public static final int WIDTH = 500;
    public static final int HEIGHT = 500;
    
    public static void main(String args[]) {
        SudokuFrame f = new SudokuFrame(WIDTH, HEIGHT);
        f.pack();
        f.setVisible(true);
    }
}
