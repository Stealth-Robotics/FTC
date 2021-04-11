//----------------------------------------------------------------------------
//
//  $Workfile: DriveEncoderTest.java$
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
//      This tests the drive motors to ensure everything is good.
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
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

//----------------------------------------------------------------------------
//  Name and type
//----------------------------------------------------------------------------
@Autonomous(name="Drive Encoder Test", group="Test")

//----------------------------------------------------------------------------
// Class Declarations
//----------------------------------------------------------------------------
//
// Class Name: DriveEncoderTest
//
// Purpose:
//   Run the drive motors and make sure they work
//
//----------------------------------------------------------------------------
public class DriveEncoderTest extends OpMode
{
    // ----------------------------------------------------------------------------
    // Class Constants
    // ----------------------------------------------------------------------------
    private final double WAIT_TIME     = 4000;
    private final double POWER_FORWARD = 0.5;
    private final double POWER_REV     = -0.5;
    private final double POWER_STOP    = 0.0;

    // ----------------------------------------------------------------------------
    // Class Attributes
    // ----------------------------------------------------------------------------
    private ElapsedTime mRuntime = new ElapsedTime();
    private boolean mRunLeft = true;
    private boolean mRunForward = true;
    private DcMotor mLeftDrive = null;
    private DcMotor mRightDrive = null;

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
        mLeftDrive  = hardwareMap.get(DcMotor.class, "LeftMotor");
        mLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        mLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        mLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        mRightDrive = hardwareMap.get(DcMotor.class, "RightMotor");
        mRightDrive.setDirection(DcMotor.Direction.FORWARD);
        mRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        mRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
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
        mRunLeft = true;
        mRunForward = true;
        mRuntime.reset();
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
        double motorPower = POWER_STOP;

        if(mRuntime.milliseconds() > WAIT_TIME)
        {
            mLeftDrive.setPower(POWER_STOP);
            mRightDrive.setPower(POWER_STOP);
            mRuntime.reset();
            
            if(true == mRunLeft) {
                mRunLeft = false;
            }
            else {
                mRunLeft = true;
                if(true == mRunForward) {
                    mRunForward = false;
                }
                else {
                    mRunForward = true;
                }
            }
        }

        if(true == mRunForward) {
            motorPower = POWER_FORWARD;
        }
        else {
            motorPower = POWER_REV;
        }

        if(true == mRunLeft) {
            mLeftDrive.setPower(motorPower);
        }
        else {
            mRightDrive.setPower(motorPower);
        }

        if (true == mRunLeft) {
            if (true == mRunForward) {
                telemetry.addData("Runing", "Left Forward");
            } else {
                telemetry.addData("Runing", "Left Rev");
            }        
        } else {
            if (true == mRunForward) {
                telemetry.addData("Runing", "Right Forward");
            } else {
                telemetry.addData("Runing", "Right Rev");
            }        
        }        

        telemetry.addData("Time", "Run Time: " + mRuntime.toString());
        telemetry.addData("Motors", "left (%d), right (%d)", 
        mLeftDrive.getCurrentPosition(), mRightDrive.getCurrentPosition());
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
        mLeftDrive.setPower(POWER_STOP);
        mRightDrive.setPower(POWER_STOP);
    }
}
