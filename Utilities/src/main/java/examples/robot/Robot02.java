package examples.robot;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Robot02{
  //Create a robot object that will be used to
  // exercise a GUI object.
  Robot robot;

  //The following GUI object will be exercised by
  // the robot.
  Robot02Slave slave = new Robot02Slave();

  //The following text will be entered into the
  // text field on the GUI.
  int keyInput[] = {
      KeyEvent.VK_D,
      KeyEvent.VK_O,
      KeyEvent.VK_N,
      KeyEvent.VK_E
  };//end keyInput array
  //-------------------------------------------//

  public static void main(String args[]){
    System.out.println("Start test program");
    new Robot02();
    System.out.println("\nEnd test program");
  }//end main
  //-------------------------------------------//

  public Robot02(){//constructor
    try{
      robot = new Robot();
    }catch(AWTException e){
      e.printStackTrace();
    }//end catch

    //Make certain that the slave is visible on
    // the screen.
    while(!(slave.isShowing())){
      //loop until slave is showing on the screen
    }//end while loop

    //Get a list of the components on the slave
    Component[] components =
          slave.getContentPane().getComponents();

    //Traverse the focus path from the beginning
    // to the end in the forward direction.
    System.out.println("\nTraverse forward");
    for(int cnt = 0;
           cnt < (components.length - 1); cnt++){
      robot.keyPress(KeyEvent.VK_TAB);
      //Insert delays to cause the focus to
      // traverse the components slowly.
      robot.delay(1000);
    }//end for loop

    //Traverse the focus path from the end to the
    // beginning in the reverse direction.
    System.out.println("\nTraverse backwards");
    //Press the shift key.
    robot.keyPress(KeyEvent.VK_SHIFT);
    for(int cnt = 0;
           cnt < (components.length - 1); cnt++){
      robot.keyPress(KeyEvent.VK_TAB);
      robot.delay(1000);
    }//end for loop
    //Release the shift key.
    robot.keyRelease(KeyEvent.VK_SHIFT);

    //Automatically click each button on the
    // slave GUI.  Then enter text into the text
    // field
    System.out.println("\nClick all components");
    for(int cnt = 0; cnt < components.length;
                                         cnt++){
      //Get, save, and display actual screen
      // location of a component
      Point location =
           components[cnt].getLocationOnScreen();
      System.out.print("Click at: " +location.x + ", " + location.y + "  ");

      //Execute a mouse click on the physical
      // location on the screen where the
      // component resides.
      mouseMoveAndClick(location.x,location.y);

      //If the component is a JTextField object,
      // execute keystrokes that will enter the
      // word "Done" into the text field.  Note
      // the use of upper and lower case.
      if(components[cnt] instanceof JTextField && components[cnt].hasFocus()){
        System.out.println("\nEnter text");
        robot.keyPress(KeyEvent.VK_SHIFT);
        for (int cnt2 = 0;
                 cnt2 < keyInput.length; cnt2++){
          if(cnt2 > 0){
            robot.keyRelease(KeyEvent.VK_SHIFT);
          }//end if
          robot.keyPress(keyInput[cnt2]);
          robot.delay(1000);
        }//end for loop
        //Cause the text field to fire an
        // ActionEvent
        robot.keyPress(KeyEvent.VK_ENTER);
      }//end if
    }//end for loop
  }//end constructor
  //-------------------------------------------//

  public void mouseMoveAndClick( int xLoc, int yLoc){
    robot.mouseMove( xLoc,yLoc );
    robot.mousePress(InputEvent.BUTTON1_MASK);
    robot.mouseRelease(InputEvent.BUTTON1_MASK);
    robot.delay(1000);
  }//end mouseMoveAndClick

}//end class Robot02