/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ece326.hw3;

import java.util.LinkedList;
import javax.swing.JButton;

/**
 *
 * @author Tsatanis Panagiotis
 */
public class UndoImplementation {
    static LinkedList<JButton[][]> FramesList = null;
    static LinkedList<String> CollisionsList = null;
    
    public UndoImplementation() {}
    
    /*Stores the current state of the board before an action
    in a linked list.Also it stores the coordinates of most
    recently enterd button that caused a collision.*/
    public static void storeCurrentFrame(JButton[][] NewFrame) {
        
        if( UndoImplementation.FramesList == null ) {
            UndoImplementation.FramesList = new LinkedList<JButton[][]>();
            UndoImplementation.CollisionsList = new LinkedList<String>();
        }
        
        JButton[][] PrevFrame = new JButton[SudokuFrame.LENGTH][SudokuFrame.LENGTH];
        
        for(int row = 0; row < SudokuFrame.LENGTH; row++) {
            for(int col = 0; col < SudokuFrame.LENGTH; col++) {
                PrevFrame[row][col] = new JButton();
                PrevFrame[row][col].setText(NewFrame[row][col].getText());
                PrevFrame[row][col].setBackground(NewFrame[row][col].getBackground());//na dw an tha bgei
            }
            
        }
        
        UndoImplementation.FramesList.addLast(PrevFrame);
        
        String S = new String();

        S += String.valueOf(SudokuFrame.CollisionRow);

        S += String.valueOf(SudokuFrame.CollisionCol);
        UndoImplementation.CollisionsList.addLast(S);
    }
    
    /*Swaps current board with the most recent one from the list.*/
    public static void UndoHandler() {
        
        if( UndoImplementation.FramesList.isEmpty() )
            return;
        
        JButton[][] LastFrame = UndoImplementation.FramesList.pollLast();
        
        String LastCollisions = UndoImplementation.CollisionsList.pollLast();
        
        SudokuFrame.CollisionRow = Integer.parseInt(LastCollisions.substring(0, LastCollisions.length()/2));
        SudokuFrame.CollisionCol = Integer.parseInt(LastCollisions.substring(LastCollisions.length()/2));
        
        int PrevEmptyCells = 0;
        for(int row = 0; row < SudokuFrame.LENGTH; row++) {
            for(int col = 0; col < SudokuFrame.LENGTH; col++) {
                SudokuFrame.SudokuPanel[row][col].setText(LastFrame[row][col].getText());
                SudokuFrame.SudokuPanel[row][col].setBackground(LastFrame[row][col].getBackground());
                if( SudokuFrame.SudokuPanel[row][col].getText().equals("") )
                    PrevEmptyCells++;
            }
        }
        SudokuFrame.EmptyCells = PrevEmptyCells;
    }
    
}
