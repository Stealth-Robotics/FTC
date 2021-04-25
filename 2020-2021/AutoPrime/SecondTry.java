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
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;


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

@TeleOp(name="Basic: Linear OpMode2", group="Linear Opmode")

public class SecondTry extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftDrive = null;
    private DcMotor rightDrive = null;
    private DcMotor armDrive = null;
    private Servo armServo = null;
    private Servo shooterServo = null;
    private DcMotor intakeDrive = null;
    private DcMotorEx shooterDrive = null;

    @Override
    public void runOpMode() {
        boolean goFast = true;
        boolean buttonDown = false;
        boolean goFast2 = true;
        boolean buttonDown2 = false;
        double setShooterSpeed=0;
        
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
        
        leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        PIDFCoefficients cof = shooterDrive.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        cof.p = 70;
        cof.i = 18;
        shooterDrive.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,cof);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Setup a variable for each drive wheel to save power level for telemetry
            double leftPower;
            double rightPower;
            double armPower;

            // Choose to drive using either Tank Mode, or POV Mode
            // Comment out the method that's not used.  The default below is POV.

            // POV Mode uses left stick to go forward, and right stick to turn.
            // - This uses basic math to combine motions and is easier to drive straight.
            double drive = -gamepad1.left_stick_y;
            double turn  =  gamepad1.right_stick_x;
            leftPower    = Range.clip(drive + turn, -1.0, 1.0) ;
            rightPower   = Range.clip(drive - turn, -1.0, 1.0) ;

            // Tank Mode uses one stick to control each wheel.
            // - This requires no math, but it is hard to drive forward slowly and keep straight.
            //leftPower  = -gamepad1.left_stick_y ;
            //rightPower = -gamepad1.right_stick_y ;
            
            
            if (false == goFast){
                leftPower/=2;
                rightPower/=2;
            }
            
           if (true == gamepad1.right_stick_button){
               buttonDown = true;
            }
            else{
                if (true == buttonDown){
                    if(true == goFast){
                        goFast=false;
                    }
                    else{
                        goFast = true;
                    }
                    buttonDown = false;
                }
            }
           //Arm
           armPower = -gamepad2.right_stick_y /2;
           if (gamepad2.left_trigger >= 0.2){
                armServo.setPosition(.8);
            }
            else{
                 armServo.setPosition(0);
            }
            //shooter
            
            if (gamepad1.right_bumper ==true){
                shooterServo.setPosition(.9);
            }
            else{
                shooterServo.setPosition(.2);
            }
            
            shooterDrive.setVelocity(setShooterSpeed,AngleUnit.DEGREES);
            if (true == gamepad1.a){
                setShooterSpeed = -230;
            }
            if(true==gamepad1.b){
                setShooterSpeed = 0;
            }
            //intake
            if (gamepad1.left_trigger >= 0.2){
                intakeDrive.setPower(1);
            }
            if (gamepad1.right_trigger >= 0.2){
                intakeDrive.setPower(-1);
            }
             if (gamepad1.left_bumper == true){
                intakeDrive.setPower(0);
            }
            
            // Send calculated power to wheels
            leftDrive.setPower(leftPower);
            rightDrive.setPower(rightPower);
            armDrive.setPower(armPower);
           

            // Show the elapsed game time and wheel power.
            double shooterSpeed = shooterDrive.getVelocity();
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Motors", "shooter (%.2f), right (%.2f)", shooterSpeed, rightPower);
            telemetry.update();
        }
    }
}
