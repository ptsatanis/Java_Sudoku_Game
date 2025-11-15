/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ece326.hw3;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


/**
 *
 * @author Tsatanis Panagiotis
 */
public class SudokuFrame extends JFrame {
    int Width,Height;
    static JPanel SudokuGrid;
    static JButton[][] SudokuPanel;
    JButton[] Digits;
    JButton Erase, Undo, ShowSolution;
    JCheckBox Verify;
    JPanel ButtonPanel;
    JMenu Difficulties;
    JMenuBar Bar;
    JMenuItem Easy, Intermediate, Expert;
    int[][] SudokuSolution = null;
    LinkedList<String> UndoRow,UndoCol;
    static int EmptyCells;
    Color[][] InitialBackgrounds;
    static int CollisionRow = - 1;//no collision
    static int CollisionCol = - 1;//no collision
    
    public static final int LENGTH = 9;
    
    public SudokuFrame() {}
    
    public SudokuFrame(int width, int height) {
        super("Begin Sudoku!");
        
        this.Difficulties = new JMenu("New Game");
        EmptyCells = ( LENGTH * LENGTH );
        
        SudokuFrame.SudokuPanel = new JButton[LENGTH][LENGTH];
        
        this.UndoRow = new LinkedList<String>();
        this.UndoCol = new LinkedList<String>();
        
        this.Easy = new JMenuItem("Easy");
        this.Easy.setBackground(Color.LIGHT_GRAY);
        this.Easy.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UndoImplementation.storeCurrentFrame(SudokuPanel);
                initSudoku(0);
            }
            
        } );
        
        this.Intermediate = new JMenuItem("Intermediate");
        this.Intermediate.setBackground(Color.LIGHT_GRAY);
        this.Intermediate.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UndoImplementation.storeCurrentFrame(SudokuPanel);
                initSudoku(1);
            }
            
        } );
        
        this.Expert = new JMenuItem("Expert");
        this.Expert.setBackground(Color.LIGHT_GRAY);
        this.Expert.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UndoImplementation.storeCurrentFrame(SudokuPanel);
                initSudoku(2);
            }
            
        } );
        
        this.Difficulties.add(this.Easy);
        this.Difficulties.add(this.Intermediate);
        this.Difficulties.add(this.Expert);
        
        
        this.Difficulties.setVisible(true);
        this.Difficulties.setLayout( new FlowLayout() );
        
        this.Bar = new JMenuBar();
        final JFrame thisFrame = this;
       
        this.Bar.add(this.Difficulties,BorderLayout.NORTH);
        this.setJMenuBar(this.Bar);
        
        this.setHeight(height);
        this.setWidth(width);
        
        this.setLayout( new BorderLayout() );
        this.setPreferredSize(new Dimension( width, height ));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        
        SudokuFrame.SudokuGrid = new JPanel( new GridLayout(LENGTH/3, LENGTH/3, LENGTH/3 + 1, LENGTH/3 + 1) );
        
        JPanel[][] PanelArray = new JPanel[LENGTH/3][LENGTH/3];
        
        for(int row = 0; row < LENGTH; row++) {
            for(int col = 0; col < LENGTH; col++) {
                SudokuFrame.SudokuPanel[row][col] = new JButton();
                SudokuFrame.SudokuPanel[row][col].setPreferredSize(new Dimension(40,20));
                SudokuFrame.SudokuPanel[row][col].setMargin(new Insets(0,0,0,0));
                SudokuFrame.SudokuPanel[row][col].setBackground(Color.WHITE);
                
                final JButton button = SudokuPanel[row][col];
                SudokuFrame.SudokuPanel[row][col].addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        button.setSelected(true);
                        markSameButtons(button);
                    }
                    
                    @Override
                    public void mouseEntered(MouseEvent e) {}
                    @Override
                    public void mouseReleased(MouseEvent e) {}
                    @Override
                    public void mousePressed(MouseEvent e) {}
                    @Override
                    public void mouseExited(MouseEvent e) {}
                } );
                
                
                SudokuFrame.SudokuPanel[row][col].addKeyListener( new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {}

                    @Override
                    public void keyPressed(KeyEvent e) {}

                    @Override
                    public void keyReleased(KeyEvent e) {
                        JButton SelectedButton = getSelected();
                        
                        int frow = findRowOfButton(SelectedButton);
                        int fcol = findColOfButton(SelectedButton, frow);
                        
                                
                        if( ( e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE )
                                && SelectedButton != null ) {
                            UndoImplementation.storeCurrentFrame(SudokuPanel);
                            
                            if( InitialBackgrounds[frow][fcol] != Color.LIGHT_GRAY && ( CollisionRow + CollisionCol ) < 0) {
                                SelectedButton.setText("");
                                SelectedButton.setBackground(Color.WHITE);
                                EmptyCells++;
                                resetBackground();
                            }
                            else if ( CollisionRow == frow && CollisionCol == fcol ) {
                                SelectedButton.setText("");
                                SelectedButton.setBackground(Color.WHITE);
                                EmptyCells++;
                                CollisionRow = - 1;
                                CollisionCol = - 1;
                                resetBackground();
                            }
                            
                        }
                        else if( ( CollisionRow + CollisionCol ) < 0 && SelectedButton != null
                                && InitialBackgrounds[frow][fcol] != Color.LIGHT_GRAY ) {
                            UndoImplementation.storeCurrentFrame(SudokuPanel);
                            int number = Character.getNumericValue( e.getKeyChar() );

                            SelectedButton.setText(String.valueOf(number));
                            UndoRow.addLast( String.valueOf(frow) );
                            UndoCol.addLast( String.valueOf(fcol) ); 
                            EmptyCells--;
                        
                            if( markForbiddenEntry(frow, fcol, String.valueOf(number)) == false ) {
                                    
                                if( EmptyCells == 0 && isSolved()) {
                                    StartEndGame(false);
                                    JOptionPane.showMessageDialog(thisFrame, "YOU WIN!");
                                }
                            }
                            else {
                                CollisionRow = frow;
                                CollisionCol = fcol;
                            }
                            
                        }
                        if( SelectedButton != null )
                            SelectedButton.setSelected(false);
                    }
                } );
                
            }
            
        }
        
        for(int i = 0; i < LENGTH/3; i++) {
            for(int j = 0; j < LENGTH/3; j++) {
                PanelArray[i][j] = new JPanel( new GridLayout(LENGTH/3, LENGTH/3) );
            }
        }
        
        
        for(int row = 0; row < LENGTH; row++) {
            for(int col = 0; col < LENGTH; col++) {
                int findPanelToPlaceRow = findPanelToPlace(row);
                int findPanelToPlaceCol = findPanelToPlace(col);
                PanelArray[findPanelToPlaceRow][findPanelToPlaceCol].add(SudokuFrame.SudokuPanel[row][col]);
            }
        }
        
        for(int i = 0; i < LENGTH/3; i++) {
            for(int j = 0; j < LENGTH/3; j++) {
                SudokuFrame.SudokuGrid.add(PanelArray[i][j]);
            }
        }
        
        this.add(SudokuFrame.SudokuGrid);
        SudokuFrame.SudokuGrid.setVisible(true);
        
        
        this.add(SudokuFrame.SudokuGrid, BorderLayout.LINE_START);
            
        this.Digits = new JButton[LENGTH];
            
        for(int i = 0; i < LENGTH; i++) {
            this.Digits[i] = new JButton(String.valueOf(i + 1));
                
                
            final int tmp = i + 1;

            this.Digits[i].addActionListener( new ActionListener () {
                @Override
                public void actionPerformed(ActionEvent e) {
                    UndoImplementation.storeCurrentFrame(SudokuPanel);
                    int number = Integer.parseInt(e.getActionCommand());
                    JButton SelectedButton = getSelected();
                    
                    int frow = findRowOfButton(SelectedButton);
                    int fcol = findColOfButton(SelectedButton, frow);

                    if( number == tmp && SelectedButton != null && ( CollisionRow + CollisionCol ) < 0 && 
                                InitialBackgrounds[frow][fcol] != Color.LIGHT_GRAY ) {
                        
                        SelectedButton.setText(String.valueOf(number));
                        UndoRow.addLast( String.valueOf(frow) );
                        UndoCol.addLast( String.valueOf(fcol) );
                        EmptyCells--;
                        
                        if( markForbiddenEntry(frow, fcol, String.valueOf(number)) == false ) {
                            
                            if( EmptyCells == 0 && isSolved()) {
                                StartEndGame(false);
                                JOptionPane.showMessageDialog(thisFrame, "YOU WIN!");
                            }
                        }
                        else if( ( CollisionRow + CollisionCol ) < 0 ) {
                            CollisionRow = frow;
                            CollisionCol = fcol;
                        }
                        
                    }
                    if( SelectedButton != null )
                        SelectedButton.setSelected(false);
                }

            });
            
        }
        
        
        ImageIcon EraserIcon = new ImageIcon("eraser.png");
        Image EraserImage = EraserIcon.getImage().getScaledInstance(17, 17, Image.SCALE_SMOOTH);
        this.Erase = new JButton( new ImageIcon(EraserImage) );
        
        this.Erase.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UndoImplementation.storeCurrentFrame(SudokuPanel);
                JButton SelectedButton = getSelected();
                
                int frow = findRowOfButton(SelectedButton);
                int fcol = findColOfButton(SelectedButton, frow);
                
                if( SelectedButton != null && InitialBackgrounds[frow][fcol] != Color.LIGHT_GRAY &&
                       ( CollisionRow + CollisionCol ) < 0 ) {
                    SelectedButton.setText("");
                    SelectedButton.setBackground(Color.WHITE);
                    EmptyCells++;
                    resetBackground();
                }
                else if ( CollisionRow == frow && CollisionCol == fcol && SelectedButton != null ) {
                    SelectedButton.setText("");
                    SelectedButton.setBackground(Color.WHITE);
                    EmptyCells++;
                    CollisionRow = - 1;
                    CollisionCol = - 1;
                    resetBackground();
                }
                if( SelectedButton != null )
                    SelectedButton.setSelected(false);
            }
        } );

        
        this.add(this.Erase);
        
        
        ImageIcon UndoIcon = new ImageIcon("undo.png");
        Image UndoImage = UndoIcon.getImage().getScaledInstance(17, 17, Image.SCALE_SMOOTH);
        this.Undo = new JButton( new ImageIcon( UndoImage ) );
        
        this.Undo.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UndoImplementation.UndoHandler();
            }
        } );
        
        
        this.Verify = new JCheckBox("Verify against solution",false);
        this.Verify.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UndoImplementation.storeCurrentFrame(SudokuPanel);
                if( Verify.isSelected() &&  ( CollisionRow + CollisionCol ) < 0) {
                    for(int row = 0; row < LENGTH; row++) {
                        for(int col = 0; col < LENGTH; col++) {
                            if( !String.valueOf(SudokuSolution[row][col]).equals(SudokuPanel[row][col].getText()) && 
                                    SudokuPanel[row][col].getBackground() != Color.RED && 
                                    InitialBackgrounds[row][col] != Color.LIGHT_GRAY ) {
                                
                                SudokuPanel[row][col].setBackground(Color.BLUE);
                            }
                        }
                    }
                }
                else if( CollisionRow + CollisionCol < 0 ) {
                    for(int row = 0; row < LENGTH; row++) {
                        for(int col = 0; col < LENGTH; col++) {
                            if( SudokuPanel[row][col].getBackground() == Color.BLUE) {
                                SudokuPanel[row][col].setBackground(Color.WHITE);
                            }
                        }
                    }
                }
            }
        } );
        
        ImageIcon ShowSolutionIcon = new ImageIcon("rubik.png");
        Image ShowSolutionImage = ShowSolutionIcon.getImage().getScaledInstance(17, 17, Image.SCALE_SMOOTH);
        this.ShowSolution = new JButton( new ImageIcon( ShowSolutionImage ) );
        
        this.ShowSolution.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StartEndGame(false);
            }
        } );
        
        
        this.ButtonPanel = new JPanel();
        
        this.ButtonPanel.setLayout( new GridLayout(5,8) );
        
        
        for(int i = 0; i < LENGTH; i++) {
            this.ButtonPanel.add(this.Digits[i]);
        }
        this.ButtonPanel.add(this.Erase);
        
        this.ButtonPanel.add(this.Undo);
        this.ButtonPanel.add(this.Verify);
        this.ButtonPanel.add(this.ShowSolution);
        this.add(this.ButtonPanel,BorderLayout.SOUTH);
        
        
    }
    
    public final int findPanelToPlace(int i) {
        if( i < 3 )
            return(0);
        else if( i < 6 )
            return(1);
        else
            return(2);
    }
    
    /*Starts a new Game if StartGame = true
    else ends the current one*/
    public void StartEndGame(boolean StartGame) {
        for(int row = 0; row < LENGTH; row++) {
            for(int col = 0; col < LENGTH; col++) {
                SudokuPanel[row][col].setText( (StartGame) ? "" : String.valueOf(SudokuSolution[row][col]) );
                
                if(StartGame) {
                    SudokuPanel[row][col].setBackground(Color.WHITE);
                }
                else {
                    SudokuPanel[row][col].setBackground(this.InitialBackgrounds[row][col]);
                }
                
                SudokuPanel[row][col].setEnabled(StartGame);
            }
            Digits[row].setEnabled(StartGame);
        }
        Erase.setEnabled(StartGame);
        Undo.setEnabled(StartGame);
        ShowSolution.setEnabled(StartGame);
        Verify.setSelected(false);
        Verify.setEnabled(StartGame);
    }
    
    /*Checks if sudoku has been solved successfully by the player.*/
    public boolean isSolved() {
        for(int row = 0; row < LENGTH; row++) {
            for(int col = 0; col < LENGTH; col++) {
                if( !SudokuFrame.SudokuPanel[row][col].getText().equals(String.valueOf(this.SudokuSolution[row][col])) ) {
                    return(false);
                }
            }
        }
        return(true);
    }
    
    /*Initializes sudoku board depending on the difficulty given.*/
    public void initSudoku(int difficulty) {
        URL url;
        
        this.StartEndGame(true);
        
        try {
            switch (difficulty) {
                case 0:
                    url = new URL( "http://gthanos.inf.uth.gr/~gthanos/sudoku/exec.php?difficulty=easy" );
                    break;
                case 1:
                    url = new URL( "http://gthanos.inf.uth.gr/~gthanos/sudoku/exec.php?difficulty=intermediate" );
                    break;
                default:
                    url = new URL( "http://gthanos.inf.uth.gr/~gthanos/sudoku/exec.php?difficulty=expert" );
                    break;
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()), LENGTH);
            String buffer;
            
            if( this.SudokuSolution == null && this.InitialBackgrounds == null ) {
                this.SudokuSolution = new int[LENGTH][LENGTH];
                this.InitialBackgrounds = new Color[LENGTH][LENGTH];
            }
            
            for(int row = 0; ( buffer = in.readLine() ) != null && row < LENGTH; row++) {
                for(int col = 0; col < LENGTH; col++) {
                    if( buffer.charAt(col) != '.' ) {
                        SudokuFrame.SudokuPanel[row][col].setText(String.valueOf(buffer.charAt(col)));
                        SudokuFrame.SudokuPanel[row][col].setBackground(Color.LIGHT_GRAY);
                        this.InitialBackgrounds[row][col] = Color.LIGHT_GRAY;
                        this.SudokuSolution[row][col] = Character.getNumericValue(buffer.charAt(col));
                        EmptyCells--;
                    }
                    else {
                        SudokuFrame.SudokuPanel[row][col].setText("");
                        this.InitialBackgrounds[row][col] = Color.WHITE;
                    }
                }
            }
            this.SudokuSolver(0, 0);
        } 
        catch (MalformedURLException ex) {}
        catch (IOException ex) {}
        
    }
    
    /*Checks if an entry is legal.
    It is used for the solution of the solved sudoku.*/
    public boolean isAllowed(int row, int col, int number) {
        
        for(int i = 0; i < LENGTH; i++) {
            if( this.SudokuSolution[row][i] == number || this.SudokuSolution[i][col] == number )
                return(false);
        }
        
        int SquareRow = row - (row%3);
        int SquareCol = col - (col%3);
        
        for(int i = 0; i < LENGTH/3; i++) {
            for(int j = 0; j < LENGTH/3; j++) {
                if( this.SudokuSolution[SquareRow + i][SquareCol + j] == number )
                    return(false);
            }
        }
        
        return(true);
    }
    
    /*Checks if an entry given by the user is legal.
    If not marks red anywhere that a collision is detected.*/
    public boolean markForbiddenEntry(int row, int col, String number) {
        boolean CollisionDetected = false;
        
        for(int i = 0; i < LENGTH; i++) {
            if( number.equals(SudokuFrame.SudokuPanel[row][i].getText()) && i != col ) {
                SudokuFrame.SudokuPanel[row][i].setBackground(Color.RED);
                SudokuFrame.SudokuPanel[row][col].setBackground(Color.RED);
                CollisionDetected = true;
            }
            
            if( number.equals(SudokuFrame.SudokuPanel[i][col].getText()) && i != row ) {
                SudokuFrame.SudokuPanel[i][col].setBackground(Color.RED);
                SudokuFrame.SudokuPanel[row][col].setBackground(Color.RED);
                CollisionDetected = true;
            }
        }
        
        int SquareRow = row - (row%3);
        int SquareCol = col - (col%3);
        
        for(int i = 0; i < LENGTH/3; i++) {
            for(int j = 0; j < LENGTH/3; j++) {
                
                if( number.equals(SudokuFrame.SudokuPanel[SquareRow + i][SquareCol + j].getText()) && 
                        ( SquareRow + i ) != row && ( SquareCol + j ) != col ) {
                    SudokuFrame.SudokuPanel[SquareRow + i][SquareCol + j].setBackground(Color.RED);
                    SudokuFrame.SudokuPanel[row][col].setBackground(Color.RED);
                    CollisionDetected = true;
                }
            }
        }
        
        return(CollisionDetected);
    }
    
    /*Solves the solved sudoku.*/
    public boolean SudokuSolver(int row, int col) {
        
        if( col == LENGTH ) {
            if( row == ( LENGTH - 1 ) ) {
                return(true);
            }
            row++;
            col = 0;
        }
        
        
        if( this.SudokuSolution[row][col] != 0 ) {
            return( this.SudokuSolver(row, col + 1) );
        }
        
        for( int number = 1; number <= 9; number++) {
            
            if(this.isAllowed(row, col, number) == true) {
                this.SudokuSolution[row][col] = number;
                
                if (this.SudokuSolver(row, col + 1) == true)
                    return(true);
            }
            this.SudokuSolution[row][col] = 0;
        }
        
        return(false);
    }
    
    public final void setHeight(int height) {
        this.Height = height;
    }
    
    public final void setWidth(int width) {
        this.Width = width;
    }
    
    @Override
    public int getHeight() {
        return(this.Height);
    }
    
    @Override
    public int getWidth() {
        return(this.Width);
    }
    
    /*Finds the most recently clicked button
    on the sudoku board.*/
    public JButton getSelected() {

        JButton res = null;
        boolean flag = false;
        for(int row = 0; row < LENGTH; row++) {
            for(int col = 0; col < LENGTH; col++) {
                if(SudokuFrame.SudokuPanel[row][col].isSelected() && !flag) {
                    res = SudokuFrame.SudokuPanel[row][col];
                    flag = true;
                }
            }
        }
        return(res);
    }
    
    /*After an action resets the Background with the folllowing color priorities:
    Red > Gray > Blue > White*/
    public void resetBackground() {
        for(int row = 0; row < LENGTH; row++) {
            for(int col = 0; col < LENGTH; col++) {
                if( this.Verify.isSelected() && SudokuFrame.SudokuPanel[row][col].getBackground() != Color.RED) {
                    if( !String.valueOf(this.SudokuSolution[row][col]).equals(SudokuFrame.SudokuPanel[row][col].getText()) ) {
                        SudokuFrame.SudokuPanel[row][col].setBackground(Color.BLUE);
                    }
                    else {
                        SudokuFrame.SudokuPanel[row][col].setBackground(this.InitialBackgrounds[row][col]);
                    }
                }
                else if( ( CollisionRow + CollisionCol ) < 0 ) {
                    SudokuFrame.SudokuPanel[row][col].setBackground(this.InitialBackgrounds[row][col]);
                }
                
            }
        }
    }

    /*Marks buttons that have the same text as the JButton b.
    Before that it resets the board background.*/
    public void markSameButtons(JButton b) {
        resetBackground();
        for(int row = 0; row < LENGTH; row++) {
            for(int col = 0; col < LENGTH; col++) {
                if( b.getText().equals( SudokuFrame.SudokuPanel[row][col].getText() ) &&
                    !SudokuFrame.SudokuPanel[row][col].getText().equals("") && 
                        ( CollisionRow + CollisionCol ) < 0 ) {
                    SudokuFrame.SudokuPanel[row][col].setBackground( new Color(255, 255, 200) );
                }
            }
        }
    }
    
    //Finds the row where the given button is.
    public int findRowOfButton(JButton Button) {
        int row;
        
        if(Button == null)
            return(-1);
        
        OUTER_LOOP:
        for(row = 0; row < LENGTH; row++) {
            for(int col = 0; col < LENGTH; col++) {
                if(Button.hashCode() == SudokuFrame.SudokuPanel[row][col].hashCode()) {
                    break OUTER_LOOP;
                }
            }
        }
        return(row);
    }
    
    //Finds the column where the given button is.
    public int findColOfButton(JButton Button, int row) {
        int col;
        
        if( row == - 1 )
            return(-1);
        
        for(col = 0; col < LENGTH && Button.hashCode() != SudokuFrame.SudokuPanel[row][col].hashCode() ; col++) {}
        
        return(col);
    }
    
   
}
