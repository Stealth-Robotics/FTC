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
//     In the auto program there is a table of them:
//        private ShooterSettings[] mShooterSettings = {
//           new ShooterSettings(-1500,100,1500,100,-1520,1500),  //First ring (3 rings full)
//           new ShooterSettings(-1520,100,1500,100,-1540,1500),  //Second ring (2 rings full)
//           new ShooterSettings(-1540,100,1500,100,    0,   1)
//
//  The first number is how fast the wheel should be spinning to shoot.
//  the second number is the tolerance of the first number.
//  So the first ring will shoot at between -1400 and -1600 ticks per second.
//  if the speed is not reached in 5 tries then it will just fire.
//  The next two are how long to push out the slider and pull in the slider in ms.  
//  So it will extend the servo for 1.5 seconds, and then pull it back in for .1 second.  
//  The reason for the short pull in is that the rest of the shooter state machine will pull it back in.
//  The next number is how fast we want to shoot the next ring.  
//  The last number is how long we will let the shooter wheel spin up to it.  
//  For the first number the shooter will spin up to -1520, and let it take 1.5 seconds to get there.
//
//  So the main code has a state machine within a state machine.  
//  The main state machine is run forward, shoot three, and run more forward.
//
//  The shooter state machine will run for the three rings, it is, rev up, shoot our, 
//  shoot in, speed up, then end.
//
//  The main state machine is in the public void loop().  
//  For shooting there is another function called private void shootThree().
//
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
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.teamcode.*;

//----------------------------------------------------------------------------
//  Name and type
//----------------------------------------------------------------------------
@Autonomous(name="Shoot Three", group="Prod Test")

//----------------------------------------------------------------------------
// Class Declarations
//----------------------------------------------------------------------------
//
// Class Name: ShootThree
//
// Purpose:
//   Shoot three auto
//
//----------------------------------------------------------------------------
public class ShootThree extends OpMode
{
    // ----------------------------------------------------------------------------
    // Class Constants
    // ----------------------------------------------------------------------------
    private final int STATE_MOVE_FORWARD1 = 1;
    private final int STATE_SHOOT3        = 2;
    private final int STATE_MOVE_FORWARD2 = 3;
    private final int STATE_STOP          = 4;

    private final double PID_D = 60;
    private final double PID_I = 18;

    private final double MOTOR_SPEED = 1.0;
    private final double MOTOR_STOP  = 0.0;

    private final double SERVO_OUT = 0.9;
    private final double SERVO_IN  = 0.2;

    private final int ENCODER_FORWARD1 = 2500;
    private final int ENCODER_FORWARD2 = 3200;

    private final int MIN_SHOT = 0;
    private final int MAX_SHOT = 2;
    private final int SHOT_TURN_OFF_MOTOR = 3;

    // ----------------------------------------------------------------------------
    // Class Attributes
    // ----------------------------------------------------------------------------
    private ElapsedTime mRuntime = new ElapsedTime();
    private DcMotor mLeftDrive = null;
    private DcMotor mRightDrive = null;
    private DcMotorEx mShooterDrive = null;
    private Servo mShooterServo = null;
    private int mState = STATE_MOVE_FORWARD1;
    private int mCurShot = 0;
    private ShooterSettings[] mShooterSettings = {
        new ShooterSettings(-1500,100,1500,100,-1520,1500),  //First ring (3 rings full)
        new ShooterSettings(-1520,100,1500,100,-1540,1500),  //Second ring (2 rings full)
        new ShooterSettings(-1540,100,1500,100,    0,   1)
    };

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
        mLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        mRightDrive = hardwareMap.get(DcMotor.class, "RightMotor");
        mRightDrive.setDirection(DcMotor.Direction.FORWARD);
        mRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        mRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        mRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        mShooterDrive = hardwareMap.get(DcMotorEx.class, "ShooterMotor");
        mShooterDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        mShooterDrive.setDirection(DcMotor.Direction.REVERSE);
        PIDFCoefficients cof = mShooterDrive.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        cof.p = PID_D;
        cof.i = PID_I;
        mShooterDrive.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,cof);
        
        mShooterServo = hardwareMap.get(Servo.class, "Shooter");
    }

    // ----------------------------------------------------------------------------
    // Purpose:
    //  Put camera here eventually
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
    //  Reset things for running
    //
    // Notes:
    // None
    //
    // ----------------------------------------------------------------------------
    @Override
    public void start() {
        mRuntime.reset();
        mState = STATE_MOVE_FORWARD1;
    }

    // ----------------------------------------------------------------------------
    // Purpose:
    //  Run Robot
    //
    // Notes:
    // None
    //
    // ----------------------------------------------------------------------------
    @Override
    public void loop() {
        
        // Make sure the cur shot is in bounds,
        // Service the control loop
        if((mCurShot >= MIN_SHOT)&&(mCurShot <= MAX_SHOT)) {
            mShooterDrive.setVelocity(mShooterSettings[mCurShot].mVelocityIn);
        }
        else {
            mShooterDrive.setVelocity(0);
        }
        
        switch(mState)
        {
            case(STATE_MOVE_FORWARD1):
                mLeftDrive.setPower(MOTOR_SPEED);
                mRightDrive.setPower(MOTOR_SPEED);
                
                if(mLeftDrive.getCurrentPosition()>=ENCODER_FORWARD1){
                    mLeftDrive.setPower(MOTOR_STOP);
                    mRightDrive.setPower(MOTOR_STOP);
                    mState = STATE_SHOOT3;
                }
                break;
            case(STATE_SHOOT3):
                mLeftDrive.setPower(MOTOR_STOP);
                mRightDrive.setPower(MOTOR_STOP);
                shootThree();
                break;
            case(STATE_MOVE_FORWARD2):
                mLeftDrive.setPower(MOTOR_SPEED);
                mRightDrive.setPower(MOTOR_SPEED);
                mCurShot = SHOT_TURN_OFF_MOTOR;
                if(mLeftDrive.getCurrentPosition()>=ENCODER_FORWARD2){
                    mLeftDrive.setPower(MOTOR_STOP);
                    mRightDrive.setPower(MOTOR_STOP);
                    mState = STATE_STOP;
                }
                break;
            case(STATE_STOP):
                mLeftDrive.setPower(MOTOR_STOP);
                mRightDrive.setPower(MOTOR_STOP);
                break;
        }
        
        telemetry.addData("Time", "Run Time: " + mRuntime.toString());
        telemetry.addData("State", "state (%d), cur shot (%d)", mState, mCurShot);    
        telemetry.addData("Encoder", "left (%d)", mLeftDrive.getCurrentPosition());    
        telemetry.addData("Cur State", "0 (%d), 1 (%d), 2 (%d)", 
            mShooterSettings[0].mState,
            mShooterSettings[1].mState,
            mShooterSettings[2].mState);    
    }

    // ----------------------------------------------------------------------------
    // Purpose:
    //  Shoot three
    //
    // Notes:
    // None
    //
    // ----------------------------------------------------------------------------
    private void shootThree() {
        //Check bounds
        if((mCurShot >= MIN_SHOT)&&(mCurShot <= MAX_SHOT)) {    

            switch(mShooterSettings[mCurShot].mState) {
                case(ShooterSettings.STATE_WARMUP):
                    if(true == mShooterSettings[mCurShot].isSpeedGood(
                        mShooterDrive.getVelocity())) {
                        mShooterSettings[mCurShot].mState = ShooterSettings.STATE_OUT;
                        mRuntime.reset();                        
                    }
                    break;
                case(ShooterSettings.STATE_OUT):
                    mShooterServo.setPosition(SERVO_OUT);
                    if(mRuntime.milliseconds() > mShooterSettings[mCurShot].mOutTime) {
                        mShooterSettings[mCurShot].mState = ShooterSettings.STATE_IN;
                        mRuntime.reset();                        
                    }
                    break;
                case(ShooterSettings.STATE_IN):
                    mShooterServo.setPosition(SERVO_IN);
                    if(mRuntime.milliseconds() > mShooterSettings[mCurShot].mInTime) {
                        mShooterSettings[mCurShot].mState = ShooterSettings.STATE_REV_UP;
                        // Switch the control loop to the output speed
                        mShooterSettings[mCurShot].mVelocityIn =
                                mShooterSettings[mCurShot].mVelocityOut;
                        mRuntime.reset();                        
                    }
                    break;
                case(ShooterSettings.STATE_REV_UP):
                    if(mRuntime.milliseconds() > mShooterSettings[mCurShot].mVelocityWait) {
                        mShooterSettings[mCurShot].mState = ShooterSettings.STATE_FINISH;
                    }
                    break;
                case(ShooterSettings.STATE_FINISH):
                    mCurShot++;
                    // if we are in the forth shot, move forward.
                    if(mCurShot >= SHOT_TURN_OFF_MOTOR)
                    {
                        mState = STATE_MOVE_FORWARD2;
                    }
                    mRuntime.reset();                        
                    break;
            }
        }
        else {
            //If the bounds is screwed up go to next state
            mState = STATE_MOVE_FORWARD2;
        }
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
        mLeftDrive.setPower(MOTOR_STOP);
        mRightDrive.setPower(MOTOR_STOP);
        mShooterDrive.setPower(MOTOR_STOP);
    }
}
