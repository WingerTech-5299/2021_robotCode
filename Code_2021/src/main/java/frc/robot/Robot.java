// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.kinematics.*;
import edu.wpi.first.networktables.*;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import com.ctre.phoenix.motorcontrol.can.*;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.MecanumDrive;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
   
  WPI_TalonSRX leftControllerF = new WPI_TalonSRX(11);
  WPI_TalonSRX rightControllerF = new WPI_TalonSRX(12);
  WPI_TalonSRX leftControllerB = new WPI_TalonSRX(13);
  WPI_TalonSRX rightControllerB = new WPI_TalonSRX(14);
  WPI_VictorSPX intakeController = new WPI_VictorSPX(15);

  Double leftEncoderB = leftControllerB.getSelectedSensorPosition();
  Double rightEncoderB = rightControllerB.getSelectedSensorPosition();
  
  Faults _faults = new Faults();

  //ErrorCode = leftControllerB.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 10);

  XboxController Xbox = new XboxController(1);
  Joystick joy = new Joystick(0);

  Double btnIntakeReverse = Xbox.getRawAxis(2);
  Double btnIntake = Xbox.getRawAxis(3);

  Double btnDriveFB = Xbox.getRawAxis(5);
  Double btnDriveSpin = Xbox.getRawAxis(0);
  Double btnDriveLR = Xbox.getRawAxis(4);  

  MecanumDrive drive = new MecanumDrive(leftControllerF, leftControllerB, rightControllerF, rightControllerB);

  NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
  Double tv = table.getEntry("tv").getDouble(0);
  Double tx = table.getEntry("tx").getDouble(0);
  Double ty = table.getEntry("ty").getDouble(0);
  Double ta = table.getEntry("ta").getDouble(0);
  Double ts = table.getEntry("ts").getDouble(0);

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */

  @Override
  public void robotInit() {

    leftControllerB.setSensorPhase(true);
    rightControllerB.setSensorPhase(true);
  }




  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {

  }

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {

    intakeController.setInverted(true);

    table.getEntry("pipeline").setNumber(0);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {

    drive.feed();

    //rightEncoderB = rightControllerB.getSelectedSensorPosition();
    //leftEncoderB = leftControllerB.getSelectedSensorPosition();

    tv = table.getEntry("tv").getDouble(0);
    ty = table.getEntry("ty").getDouble(0);
    ta = table.getEntry("ta").getDouble(0);
    ts = table.getEntry("ts").getDouble(0);
    
    intakeController.set(1);

    if (tv == 0){

      drive.driveCartesian(0, 1, 0);
      Timer.delay(1);
      if (tv == 1){
        drive.driveCartesian(0, 0, 0);
        return;
      }else{
        drive.driveCartesian(0, 0, 0.3);
        Timer.delay(0.5);
        if (tv == 1){
          drive.driveCartesian(0, 0, 0);
          return;
        }else{
          drive.driveCartesian(0, 0, -0.3);
          Timer.delay(0.8);
          if (tv == 1){
            drive.driveCartesian(0, 0, 0);
          }
        }
      }
    }


    if (tv == 1){

      while (Math.abs(tx) > 0.1){

        if(tx > 0){

          drive.driveCartesian(0.5, 0, 0.04);
        }else{

          drive.driveCartesian(-0.5, 0 ,-0.03);
        }
      }
    }else{

      drive.driveCartesian(0,0,0);
    }

    double wheelCircumference = 0.1524 * Math.PI;
    double ballDistance = 0.451 * Math.abs(Math.tan(tx));
    double wheelTurnsToBall = ballDistance / wheelCircumference;

    /*
    while (ballDistance > 0){

      drive.driveCartesian(0, 0.5, 0);
      intakeController.set(0.5);
      tx = table.getEntry("tx").getDouble(0); 
    }
    */
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
    
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {

    btnDriveFB = Xbox.getRawAxis(5);
    btnDriveSpin = Xbox.getRawAxis(0);
    btnDriveLR = Xbox.getRawAxis(4);
    btnIntakeReverse = Xbox.getRawAxis(3);
    btnIntake = Xbox.getRawAxis(2);

   


    if (btnIntake > 0.1){

      intakeController.set(0.7 * btnIntake);

    }else if (btnIntakeReverse > 0.1){

      intakeController.set(-0.7 * btnIntakeReverse);

    }else if (btnIntake < 0.1){

      intakeController.set(0);

    }else if (btnIntakeReverse < 0.1){

      intakeController.set(0);

    }

    drive.driveCartesian(0.6*btnDriveLR, -0.6*btnDriveFB, 0.6*btnDriveSpin);  
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

}
