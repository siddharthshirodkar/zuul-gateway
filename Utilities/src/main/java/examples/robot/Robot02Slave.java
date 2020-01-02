package examples.robot;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Robot02Slave extends JFrame
         implements ActionListener,FocusListener{

  //This main method can be used to run the
  // program in a standalone mode independent of
  // the robot.
  public static void main(String[] args){
    new Robot02Slave();
  }//end main
  //-------------------------------------------//

  public Robot02Slave(){//constructor

    //Prepare the JFrame for use
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
    getContentPane().setLayout(new GridLayout(3,3));
    setBounds(10,10,250,200);
    setTitle("Copyright 2003, R.G.Baldwin");

    //Create eight buttons and add them to the
    // content pane
    for(int cnt = 0; cnt < 8; cnt++){
      getContentPane().add(new JToggleButton("" + (char)('A' + cnt)));
    }//end for loop

    //Add a text field as the ninth component
    getContentPane().add(new JTextField(""));

    //Get a list of the components
    Component[] components = getContentPane().getComponents();

    //Register listeners on each component and
    // give each component a name
    for(int cnt = 0; cnt < components.length; cnt++){
      //Must downcast in order to register an
      // action listener.
      if(components[cnt] instanceof JToggleButton)
        ((JToggleButton)components[cnt]).addActionListener(this);
      else if(components[cnt] instanceof JTextField)
        ((JTextField)components[cnt]).addActionListener(this);
      //end else

      //Register a focus listener on each
      // component
      components[cnt].addFocusListener(this);

      //Give each component a name.  Make the
      // name match the text on the face of the
      // buttons.  Name the text field "I"
      components[cnt].setName("" + (char)('A' + cnt));
    }//end for loop

    //Set the look and feel to Motif
    String plafClassName = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
    try{
       UIManager.setLookAndFeel(plafClassName);
     }catch(Exception ex){ex.printStackTrace();}

     //Cause the new L&F to be applied
     SwingUtilities.updateComponentTreeUI(this);

    //Make the frame visible
    setVisible(true);
  }//end constructor

  //Define the ActionEvent handler that is
  // registered on each of the components.
  public void actionPerformed(ActionEvent e){
    System.out.println("ActionCommand = "  + e.getActionCommand());
  }//end actionPerformed

  //Define the FocusEvent handlers that are
  // registered on each of the components.
  public void focusLost(FocusEvent e){
    System.out.print(e.getComponent().getName() + " lost focus  ");
  }//end focus lost

  public void focusGained(FocusEvent e){
    System.out.println(e.getComponent().getName() + " gained focus");
  }//end focusGained

}//end class definition
