package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class autoMethods {

    edu.wpi.first.networktables.NetworkTable table = NetworkTableInstance.getDefault().getTable("Limelight");
    

    public void autoFind(TalonSRX leftControllerB, TalonSRX rightControllerB, MecanumDrive drive){

        while (table.getEntry("tv").getDouble(0) == 0){
          leftControllerB.setSelectedSensorPosition(0);
          rightControllerB.setSelectedSensorPosition(0);
    
          Double  rightEncoderPosition = rightControllerB.getSelectedSensorPosition();
          Double leftEncoderPosition = leftControllerB.getSelectedSensorPosition();
    
          while ((rightControllerB.getSelectedSensorPosition() > -7000 && leftControllerB.getSelectedSensorPosition() > -7000) || table.getEntry("tv").getDouble(0) == 0){

            rightEncoderPosition = rightControllerB.getSelectedSensorPosition();
            leftEncoderPosition = leftControllerB.getSelectedSensorPosition();
    
            SmartDashboard.putNumber("EncoderR", rightEncoderPosition);
            SmartDashboard.putNumber("EncoderL", leftEncoderPosition);
    
          
            drive.driveCartesian(0, 0, -0.5);
            }
    
          while ((rightControllerB.getSelectedSensorPosition() < 7000 && leftControllerB.getSelectedSensorPosition() < 7000) || (table.getEntry("tv").getDouble(0) == 0)){

              rightEncoderPosition = rightControllerB.getSelectedSensorPosition();
              leftEncoderPosition = leftControllerB.getSelectedSensorPosition();
      
              SmartDashboard.putNumber("EncoderR", rightEncoderPosition);
              SmartDashboard.putNumber("EncoderL", leftEncoderPosition);
      
            
              drive.driveCartesian(0, 0, 0.5);
              }
    
          drive.driveCartesian(0, 0, 0);
    
        }
    
      }
    
      public void zeroEncoder(){
    
      }
    
      
    
      public void autoPickUp(TalonSRX rightControllerB, TalonSRX leftControllerB, MecanumDrive drive){
    
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
    
        while (rightControllerB.getSelectedSensorPosition() < unitsToBall && leftControllerB.getSelectedSensorPosition() < 0){
    
          drive.driveCartesian(0, 0.5, 0);
    
        }
    
      }
    
    
}
