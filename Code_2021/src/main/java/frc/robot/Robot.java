// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.*;
import java.sql.Driver;
import java.sql.DriverAction;
import javax.annotation.meta.When;
import javax.swing.plaf.basic.BasicOptionPaneUI.ButtonActionListener;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.*;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PWMTalonSRX;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.Joystick.ButtonType;
import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
<<<<<<< Updated upstream
=======
import edu.wpi.first.wpilibj.drive.MecanumDrive;
>>>>>>> Stashed changes
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.I2C;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  While true(){

  }
   
  PWMTalonSRX leftControllerB = new PWMTalonSRX(0);
  WPI_TalonSRX rightControllerB = new WPI_TalonSRX();
  WPI_TalonSRX leftControllerF = new WPI_TalonSRX();
  WPI_TalonSRX rightControllerF = new WPI_TalonSRX();

  Joystick joy_silv = new Joystick(0);
  XboxController Xbox = new XboxController(1);

  private double forward = 0.0;
  private double turn = 0.0;
  private double backward = 0.0;

  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  private boolean m_limelightHasValidTarget = false;
  private double m_LimelightDriveCommand = 0.0;
  private double m_limelightDriveSCommand = 0.0; //The S signifies side driving (Strafing)
  private double m_limelightSteerCommand = 0.0;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */

  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
  }
<<<<<<< Updated upstream
DifferentialDrive drive = new DifferentialDrive(leftController, rightController);
=======
  MecanumDrive drive = new MecanumDrive(leftControllerF, leftControllerB, rightControllerF, rightControllerB);
>>>>>>> Stashed changes




  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {}

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
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {

    updateLimelightTracking();

    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {

    leftController.configFactoryDefault();
    rightController.configFactoryDefault();  

    leftController.setInverted(false);
    rightController.setInverted(true);
    
    drive.setRightSideInverted(false);

  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {

    forward = +.8* Xbox.getY();  
    turn = +.8 * Xbox.getX();
    backward = +.8* Xbox.getY();

    if (Math.abs(forward) < 0.4) {
     forward = 0;
   }

   if (Math.abs(turn) < 0.4) {
     turn = 0;
   }

   drive.arcadeDrive(forward, turn);

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

  public void updateLimelightTracking(){

    final double STEER_K = 0.0;
    final double DRIVE_K = 0.0;
    final double DRIVES_K = 0.0; //The S signifies side driving (Strafing)

    final double MAX_DRIVE = 0.0;

    NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
    Double tv = table.getEntry("tv").getDouble(0);
    Double tx = table.getEntry("tx").getDouble(0);
    Double ty = table.getEntry("ty").getDouble(0);
    Double ta = table.getEntry("ta").getDouble(0);
    Double ts = table.getEntry("ts").getDouble(0);

    if (tv < 1.0){

      m_limelightHasValidTarget = false;
      m_LimelightDriveCommand = 0.0;
      m_limelightDriveSCommand = 0.0;
      m_limelightSteerCommand = 0.0;
      return;
    }

    m_limelightHasValidTarget = true;

    double DriveS_cmd = tx * DRIVES_K;
    m_limelightDriveSCommand = DriveS_cmd;
  }
}
