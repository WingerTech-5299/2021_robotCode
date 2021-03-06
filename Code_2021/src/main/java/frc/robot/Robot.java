// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.*;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.SPI;

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

  MecanumDrive drive = new MecanumDrive(leftControllerF, leftControllerB, rightControllerF, rightControllerB);

  XboxController Xbox = new XboxController(0);
  Joystick joy = new Joystick(1);

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

    // leftController.configFactoryDefault();
    // rightController.configFactoryDefault();  

    // leftController.setInverted(false);
    // rightController.setInverted(true);
    
    // drive.setRightSideInverted(false);

  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {


    Boolean btnIntakeReverse = joy.getRawButton(1);
    Boolean btnIntake = joy.getRawButton(0);
    Double btnIntakeSpeed = joy.getRawAxis(3);

    if (btnIntake == true){

      intakeController.set(btnIntakeSpeed);

    }else if (btnIntakeReverse = true){

      intakeController.set(-btnIntakeSpeed);

    }else if (btnIntake == false){

      intakeController.set(0);

    }
    
    Double btnDriveFB = Xbox.getRawAxis(5);
    Double btnDriveSpin = Xbox.getRawAxis(0);
    Double btnDriveLR = Xbox.getRawAxis(4);

    drive.driveCartesian(0.5*btnDriveLR, 0.5*btnDriveFB, 0.5*btnDriveSpin);

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
