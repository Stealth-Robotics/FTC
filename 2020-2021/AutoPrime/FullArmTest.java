//----------------------------------------------------------------------------
//
//  $Workfile: FullArmTest.java$
//
//  $Revision: X$
//
//  Project:    FTC 7759 2021
//
//                            Copyright (c) 2021
//                 Cedarcrest High School Team 7759 Auto Prime
//                            All Rights Reserved
//
//  Modification History:
//  $Log:
//  $
//
//  Note:
//      This moves up arm from closed to grabbing.
//
//      When boolean varable armGoingUp is true the arm moves from closed 
//        to grabbing
//  
//      The closed position is known by the limit switch being closed. 
//  
//      The grabbing position is known from the encoder count.
//----------------------------------------------------------------------------
//----------------------------------------------------------------------------
//  Package
//----------------------------------------------------------------------------
package org.firstinspires.ftc.teamcode;

//----------------------------------------------------------------------------
//  Imports
//----------------------------------------------------------------------------
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

//----------------------------------------------------------------------------
//  Name and type
//----------------------------------------------------------------------------
@Autonomous(name="Full Arm Test", group="Test")

//----------------------------------------------------------------------------
// Class Declarations
//----------------------------------------------------------------------------
//
// Class Name: FullArmTest
//
// Purpose:
//   Run the arm from closed to grabbed
//
//----------------------------------------------------------------------------
public class FullArmTest extends OpMode
{
    // ----------------------------------------------------------------------------
    // Class Constants
    // ----------------------------------------------------------------------------
    private final double ARM_SPEED_UP   = -0.3;
    private final double ARM_SPEED_DOWN = 0.3;
    private final double ARM_SPEED_STOP = 0.0;
    private final double ARM_ENCODER_GRAB_POS = -2400;
    
    // ----------------------------------------------------------------------------
    // Class Attributes
    // ----------------------------------------------------------------------------
    private DcMotorEx mArmDrive = null;
    private boolean mArmGoingUp = false;
    DigitalChannel mDigitalTouch = null;

    // ----------------------------------------------------------------------------
    // Purpose:
    //  Setup the hardware
    //
    // Notes:
    // None
    //
    // ----------------------------------------------------------------------------
    @Override
    public void init() {
        mArmDrive  = hardwareMap.get(DcMotorEx.class, "ArmMotor");
        mDigitalTouch = hardwareMap.get(DigitalChannel.class, "ArmTouch2");

        mArmDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        mArmDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        mDigitalTouch.setMode(DigitalChannel.Mode.INPUT);
    }

    // ----------------------------------------------------------------------------
    // Purpose:
    //  Reset anything after start is pushed
    //
    // Notes:
    // None
    //
    // ----------------------------------------------------------------------------
    @Override
    public void init_loop() {
    }

    // ----------------------------------------------------------------------------
    // Purpose:
    //  Reset anything after start is pushed
    //
    // Notes:
    // None
    //
    // ----------------------------------------------------------------------------
    @Override
    public void start() {
        mArmGoingUp = false;
    }

    // ----------------------------------------------------------------------------
    // Purpose:
    //  Run the robot
    //
    // Notes:
    // None
    //
    // ----------------------------------------------------------------------------
    @Override
    public void loop() {
        double armPower = ARM_SPEED_STOP;
        
        if(true == mArmGoingUp) {
            armPower = ARM_SPEED_UP;
        }
        else {
            armPower = ARM_SPEED_DOWN;
        }
        
        if ((mDigitalTouch.getState() == false) && (false == mArmGoingUp)) {
            armPower = ARM_SPEED_STOP;
            mArmDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            mArmDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            
            mArmGoingUp = true;    
        }
        
        if((mArmDrive.getCurrentPosition() < ARM_ENCODER_GRAB_POS) && (true == mArmGoingUp)) {
            armPower = ARM_SPEED_STOP;
            mArmGoingUp = false;
        }        
        
        mArmDrive.setPower(armPower);
        
        if (mDigitalTouch.getState() == true) {
            telemetry.addData("Arm Touch", "Is Not Pressed");
        } else {
            telemetry.addData("Arm Touch", "Is Pressed");
        }        
        
        telemetry.addData("Motors", "arm (%d)", mArmDrive.getCurrentPosition());
    }

    // ----------------------------------------------------------------------------
    // Purpose:
    //  Hit stop
    //
    // Notes:
    // None
    //
    // ----------------------------------------------------------------------------
    @Override
    public void stop() {
        mArmDrive.setPower(ARM_SPEED_STOP);
    }

}
