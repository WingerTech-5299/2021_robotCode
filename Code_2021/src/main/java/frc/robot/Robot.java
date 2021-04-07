// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.*;

import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.drive.MecanumDrive;

import edu.wpi.first.wpilibj.XboxController;

public class Robot extends TimedRobot{

  WPI_TalonSRX leftControllerF = new WPI_TalonSRX(11);
  WPI_TalonSRX rightControllerF = new WPI_TalonSRX(12);
  WPI_TalonSRX leftControllerB = new WPI_TalonSRX(13);
  WPI_TalonSRX rightControllerB = new WPI_TalonSRX(14);
  WPI_VictorSPX intakeController = new WPI_VictorSPX(15);

  Double leftEncoderPosition = leftControllerB.getSelectedSensorPosition();
  Double rightEncoderPosition = rightControllerB.getSelectedSensorPosition();

  XboxController Xbox = new XboxController(0);

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
  
  Boolean targetTest;

  Boolean targetFind;

  Double spinSpeed;
  
  int ballCount = 0;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */

   private Double spinAdjust(){

    addPeriodic (() -> {
      Double absDifference = Math.abs(leftControllerB.getSelectedSensorPosition()) - Math.abs(rightControllerB.getSelectedSensorPosition());

      if (absDifference > 1000){
       spinSpeed = 0.5;
      } else {
       spinSpeed = 0.0;
    }

    }, 0.01, 0.005);

    return spinSpeed;
    
  }

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

    rightEncoderPosition = rightControllerB.getSelectedSensorPosition();
    leftEncoderPosition = leftControllerB.getSelectedSensorPosition();

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
    table.getEntry("ledMode").setNumber(3);

  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {

    while (ballCount < 3){

      autoFind(leftControllerB, rightControllerB, drive);

      autoPickUp(rightControllerB, leftControllerB, drive);
      
      ballCount ++;
    }

    drive.feed();

    tv = table.getEntry("tv").getDouble(0);
    tx = table.getEntry("tx").getDouble(0);
    ty = table.getEntry("ty").getDouble(0);
    ta = table.getEntry("ta").getDouble(0);
    
    SmartDashboard.putNumber("LimelightX", tx);
    SmartDashboard.putNumber("LimelightY", ty);
    SmartDashboard.putNumber("LimelightArea", ta);

    intakeController.set(0.6);
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {

    leftControllerB.setSelectedSensorPosition(0);
    rightControllerB.setSelectedSensorPosition(0);
    
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
  public void disabledPeriodic() {
    
    tv = table.getEntry("tv").getDouble(0);
    tx = table.getEntry("tx").getDouble(0);
    ty = table.getEntry("ty").getDouble(0);
    ta = table.getEntry("ta").getDouble(0);

    if (tv == 1){

      targetTest = true;

    } else {

      targetTest = false;

    }
    
    SmartDashboard.putBoolean("Limelight Has Target ", targetTest);
    SmartDashboard.putNumber("LimelightX", tx);
    SmartDashboard.putNumber("LimelightY", ty);
    SmartDashboard.putNumber("LimelightArea", ta);
  }

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  private void autoFind(TalonSRX leftControllerB, TalonSRX rightControllerB, MecanumDrive drive){

    leftControllerB.setSelectedSensorPosition(0);
    rightControllerB.setSelectedSensorPosition(0);

    while (table.getEntry("tv").getDouble(0) == 0){
      leftControllerB.setSelectedSensorPosition(0);
      rightControllerB.setSelectedSensorPosition(0);

      Double  rightEncoderPosition = rightControllerB.getSelectedSensorPosition();
      Double leftEncoderPosition = leftControllerB.getSelectedSensorPosition();

      while ((rightControllerB.getSelectedSensorPosition() > -3000 && leftControllerB.getSelectedSensorPosition() > -3000) || table.getEntry("tv").getDouble(0) == 0){

        rightEncoderPosition = rightControllerB.getSelectedSensorPosition();
        leftEncoderPosition = leftControllerB.getSelectedSensorPosition();

        SmartDashboard.putNumber("EncoderR", rightEncoderPosition);
        SmartDashboard.putNumber("EncoderL", leftEncoderPosition);

      
        drive.driveCartesian(0, 0, -0.5);
        }

      while ((rightControllerB.getSelectedSensorPosition() < 3000 && leftControllerB.getSelectedSensorPosition() < 3000) || (table.getEntry("tv").getDouble(0) == 0)){

          rightEncoderPosition = rightControllerB.getSelectedSensorPosition();
          leftEncoderPosition = leftControllerB.getSelectedSensorPosition();
  
          SmartDashboard.putNumber("EncoderR", rightEncoderPosition);
          SmartDashboard.putNumber("EncoderL", leftEncoderPosition);
  
        
          drive.driveCartesian(0, 0, 0.5);
          }

      while ((rightControllerB.getSelectedSensorPosition() > 0 && leftControllerB.getSelectedSensorPosition() > 0) || table.getEntry("tv").getDouble(0) == 0){
        drive.driveCartesian(0, 0, -0.5);
      }

      leftControllerB.setSelectedSensorPosition(0);
      rightControllerB.setSelectedSensorPosition(0);

      while (table.getEntry("tv").getDouble(0) == 0){
         drive.driveCartesian(0, 0.5, spinAdjust());
      }

      drive.driveCartesian(0, 0, 0);

    }

  }      

  private void autoPickUp(TalonSRX rightControllerB, TalonSRX leftControllerB, MecanumDrive drive){

    while (Math.abs(table.getEntry("tx").getDouble(0)) > 0.2){

      if (table.getEntry("tx").getDouble(0) > 0){
        drive.driveCartesian(0, 0, 0.5);
      } else {
        drive.driveCartesian(0, 0, 0.5);
      }
    }

    Double balldistance = 17.75/Math.tan(table.getEntry("ty").getDouble(0));
    Double unitsToBall = (balldistance/(Math.PI * 6)) * 4096;      

    rightControllerB.setSelectedSensorPosition(0);
    leftControllerB.setSelectedSensorPosition(0);

    while (rightControllerB.getSelectedSensorPosition() < unitsToBall && leftControllerB.getSelectedSensorPosition() < unitsToBall){

      drive.driveCartesian(0, 0.5, spinAdjust());

    }
  
  }

}