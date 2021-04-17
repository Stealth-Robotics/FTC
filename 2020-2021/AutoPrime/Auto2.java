/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.ElapsedTime.Resolution;
import org.firstinspires.ftc.teamcode.RingLocation;
import org.firstinspires.ftc.teamcode.RingWebCam;


/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@Autonomous(name="Basic: Linear OpMode3", group="Linear Opmode")

public class Auto2 extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftDrive = null;
    private DcMotor rightDrive = null;
    private DcMotor armDrive = null;
    private Servo armServo = null;
    private Servo shooterServo = null;
    private DcMotor intakeDrive = null;
    private DcMotorEx shooterDrive = null;
    private int singleStep =1;
    private RingWebCam ringWebCam;
    
    private final int STATE_RUN_FORWARD1 = 0;
    private final int STATE_RUN_SHOOT3_A = 1;
    private final int STATE_RUN_SHOOT3_B = 2;
    private final int STATE_RUN_INTAKE =   3;
    private final int STATE_RUN_SHOOT1_A = 4;
    private final int STATE_RUN_SHOOT1_B = 5;
    private final int STATE_RUN_FORWARD2 = 6;
    private final int STATE_STOP         = 7;
    
    public void waitFor(int ms){
        telemetry.addData("waiting ", ms);
            telemetry.update();
            
        ElapsedTime timer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
         while(timer.milliseconds()< ms){
                if(!opModeIsActive()) return;
                telemetry.addData("shooter",shooterDrive.getVelocity(AngleUnit.DEGREES));
                telemetry.update();
            }
            telemetry.addData("done",ms);
            telemetry.update();
    }
    public void shoot(int howMany){
        while(howMany > 0){
            telemetry.addData("shooting ", howMany);
            telemetry.update();
            
            shooterServo.setPosition(.9);
            waitFor(500);
            shooterServo.setPosition(.2);
            waitFor(500);
            howMany--;
        }
    }
    public void driveTo(int left, int right, double speed){
            leftDrive.setTargetPosition(left);
            rightDrive.setTargetPosition(right);
            leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            leftDrive.setPower(speed);
            rightDrive.setPower(speed);
            while(opModeIsActive() && leftDrive.isBusy()){
                telemetry.addData("driveTo ", leftDrive.getCurrentPosition());
                telemetry.addData("driveTo ", rightDrive.getCurrentPosition());
                telemetry.update();
            }
    }
    public void moveArmTo(int position, double speed)
    {
        armDrive.setTargetPosition(position);
        armDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        armDrive.setPower(speed);
            while(opModeIsActive() && armDrive.isBusy()){
                telemetry.addData("driveTo ", armDrive.getCurrentPosition());
                telemetry.update();
            }
    }
    @Override
    public void runOpMode() {
        
        int mState = STATE_RUN_FORWARD1;
        boolean goFast = true;
        boolean buttonDown = false;
        boolean goFast2 = true;
        boolean buttonDown2 = false;
        double setShooterSpeed=0;
        int count = 0;
        boolean firstTimeInStop = true;
        
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        leftDrive  = hardwareMap.get(DcMotor.class, "LeftMotor");
        rightDrive = hardwareMap.get(DcMotor.class, "RightMotor");
        armDrive = hardwareMap.get(DcMotor.class, "ArmMotor");
        armServo = hardwareMap.get(Servo.class, "Arm");
        shooterServo = hardwareMap.get(Servo.class, "Shooter");
        shooterDrive = hardwareMap.get(DcMotorEx.class, "ShooterMotor");
        intakeDrive = hardwareMap.get(DcMotor.class, "IntakeMotor");
        
        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        rightDrive.setDirection(DcMotor.Direction.FORWARD);
        
        leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        
        
        leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        PIDFCoefficients cof = shooterDrive.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);        
        cof.p = 60;        
        cof.i = 18;        
        shooterDrive.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,cof);
        
        shooterServo.setPosition(.2);
        armServo.setPosition(0);
        
        /////////////////////////////////////////////////
        /// Camera
        /////////////////////////////////////////////////
        
        RingLocation lastReconizedLocation = new RingLocation();
        
        ringWebCam = new RingWebCam();
        ringWebCam.setHardwareMap(hardwareMap);
        ringWebCam.init();    
        
        while(false == isStarted()) {
            RingLocation location = ringWebCam.findStack(telemetry);
            if(0 != location.mDetected)
            {
                lastReconizedLocation = location;
                runtime.reset();
            }
            else
            {
                if(runtime.milliseconds() > 500)
                {
                    lastReconizedLocation = new RingLocation();
                }
            }
            telemetry.addData("stack", String.format("%d", lastReconizedLocation.mDetected));
            telemetry.update();
        } 
        
        /////////////////////////////////////////////////
        /// Camera End
        /////////////////////////////////////////////////
        
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Setup a variable for each drive wheel to save power level for telemetry
            double leftPower;
            double rightPower;
            double armPower;
            
            int targetMode = lastReconizedLocation.mDetected;
            
           shooterDrive.setVelocity(-190,AngleUnit.DEGREES);
    
            driveTo(800,800,0.4);
            
            waitFor(6000);
            shoot(3);
            
            switch(targetMode){
                case RingLocation.TARGET_ZERO_RINGS:     //Target A
                    driveTo(3400,3400,.4);
                    driveTo(4050,2650,.4);
                    driveTo(3950,2450,.4);
                    moveArmTo(-2250, .3);
                    armServo.setPosition(.8);
                    waitFor(1000);
                    driveTo(4050,2650,.4);
                break;
                case RingLocation.TARGET_ONE_RING:     //Target B
                    intakeDrive.setPower(1);
                    waitFor(250);
                    driveTo(913, 794,.4);
                    driveTo(2239,1947,.4);
                    waitFor(2000);
                    intakeDrive.setPower(0);
                    shoot(1);
                    driveTo(4921, 4634,.4); 
                    driveTo(5500, 4100,.4);
                    driveTo(5000, 3600,.4);
                    moveArmTo(-2250, .3);
                    armServo.setPosition(.8);
                    waitFor(500);
                    driveTo(5361, 4239,.4);
                    driveTo(4837, 4784,.4);
                    driveTo(3100, 3100,.4);
                
                    //turn on intake
                    //drive to 2500
                    //turn 2800 2200
                    //shoot 1 powershot
                    //turn 3100 1700
                    //drive 5300 3900
                    //turn 6000 3100
                    //drive 4600 1800
                    //release wobble goal
                    //drive 4750 1950
                    //move arm to 0
                    //turn 6000 1100
                    //drive 6600 1800
                    
                    
                break;
                case RingLocation.TARGET_FOUR_RINGS:
                
            }
            
            
            telemetry.addData("driveTo ", leftDrive.getCurrentPosition());
            telemetry.addData("driveTo ", rightDrive.getCurrentPosition());
            telemetry.update();
            return;
        }
    }
    
}
