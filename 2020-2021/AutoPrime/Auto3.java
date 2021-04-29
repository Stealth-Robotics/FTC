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
import java.util.Stack;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
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

@Autonomous(name="Auto3", group="Linear Opmode")

public class Auto3 extends LinearOpMode {

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
    
    // Adding the IMU to use headings
      // The IMU sensor object
    BNO055IMU imu;

    // State used for updating telemetry
    Orientation angles;
    Acceleration gravity;
    Orientation lastAngles = new Orientation();
    double globalAngle = 0.0;
    
    private final int STATE_RUN_FORWARD1 = 0;
    private final int STATE_RUN_SHOOT3_A = 1;
    private final int STATE_RUN_SHOOT3_B = 2;
    private final int STATE_RUN_INTAKE =   3;
    private final int STATE_RUN_SHOOT1_A = 4;
    private final int STATE_RUN_SHOOT1_B = 5;
    private final int STATE_RUN_FORWARD2 = 6;
    private final int STATE_STOP         = 7;
    
    public void waitFor(int ms){
        ElapsedTime timer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
         while(timer.milliseconds()< ms){
               if(!opModeIsActive()) return;
            }
     }
    public void shoot(int howMany){
        while(howMany > 0){
            shooterServo.setPosition(.7);
            waitFor(300);
            shooterServo.setPosition(.0);
            if (--howMany >0 ){
                waitFor(300);
            }
        }
    }
    public void driveForward(int distance, double speed)
    {
        int right, left;
        left = leftDrive.getCurrentPosition();
        right = rightDrive.getCurrentPosition();
        left += distance;
        right += distance;
        driveTo(left,right,speed);
    }
    public void driveReverse(int distance, double speed)
    {
        int right, left;
        left = leftDrive.getCurrentPosition();
        right = rightDrive.getCurrentPosition();
        left -= distance;
        right -= distance;
        driveTo(left,right,speed);
    }
    public void rotateLeft(double angle, double speed)
    {
        PIDRotate(angle,speed);
    }
    public void rotateRight(double angle,double speed)
    {
        PIDRotate(-angle,speed);
    }
    public void turnLeft(int distance, double speed)
    {
        int right, left;
        left = leftDrive.getCurrentPosition();
        right = rightDrive.getCurrentPosition();
        left -= distance;
        right += distance;
        driveTo(left,right,speed);
    }
    public void turnRight(int distance, double speed)
    {
        int right, left;
        
        left = leftDrive.getCurrentPosition();
        right = rightDrive.getCurrentPosition();
        left += distance;
        right -= distance;
        driveTo(left,right,speed);
    }
    public void driveTo(int left, int right, double speed){
            leftDrive.setTargetPosition(left);
            rightDrive.setTargetPosition(right);
            leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            leftDrive.setPower(speed);
            rightDrive.setPower(speed);
            while(opModeIsActive() && leftDrive.isBusy() && rightDrive.isBusy()){
                //telemetry.addData("driveTo ", leftDrive.getCurrentPosition());
                //telemetry.addData("driveTo ", rightDrive.getCurrentPosition());
                //telemetry.update();
            }
            leftDrive.setPower(0);
            rightDrive.setPower(0);
    }
    public void moveArmTo(int position, double speed)
    {
        armDrive.setTargetPosition(position);
        armDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        armDrive.setPower(speed);
            while(opModeIsActive() && armDrive.isBusy()){
                //telemetry.addData("moveArmTo ", armDrive.getCurrentPosition());
                //telemetry.update();
            }
    }
    private void resetAngle()
    {
        lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        globalAngle = 0;
    }
    private double getAngle()
    {
        // We experimentally determined the Z axis is the axis we want to use for heading angle.
        // We have to process the angle because the imu works in euler angles so the Z axis is
        // returned as 0 to +180 or 0 to -180 rolling back to -179 or +179 when rotation passes
        // 180 degrees. We detect this transition and track the total cumulative angle of rotation.

        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        double deltaAngle = angles.firstAngle - lastAngles.firstAngle;

        if (deltaAngle < -180)
            deltaAngle += 360;
        else if (deltaAngle > 180)
            deltaAngle -= 360;

        globalAngle += deltaAngle;

        lastAngles = angles;

        return globalAngle;
    }
    public double deadBand(double A, double B)
    {
        // if absolute value of A is smaller than B, return zero
        if(A >= 0) return (A > B)?A:0.0;
        return (A<=-B)?A:0.0;
    }
    
    public double clampedKValue(double integrated, double K)
    {
        
        if(K == 0.0) return 0.0;
        if((integrated * K) > 1.0) return 1.0 / K;
        if((integrated * K) < -1.0) return -1.0 / K;
        return integrated;
        
    }
    public double absMax(double value, double absMax)
    {
        if(value >= 0) return (value < absMax)?value:absMax;
        return (value<=-absMax)?-absMax:value;
    }
    private void PIDRotate(double degrees, double maxpower)
    {
        // Establish constants.
        double Kp = .005;
        double Ki = .01;
        double basePower = .19;   // Minimum power for turn determined experimentally
        double leftPower, rightPower;
        double currentError;
        double newOutput;
        double iTerm,pTerm;
        double iError = 0;
        
        resetAngle();
        // Stop using the encoders
        leftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        int count = 0;
        
   while(opModeIsActive() && (deadBand((currentError = degrees - getAngle()), .3) != 0))
        {
            pTerm = absMax(currentError * Kp,1.0); 
            if (Math.abs(pTerm)>=.2){
                iError = 0.0;
            }
            else{
                iError+=currentError;
            }
            iTerm = absMax(iError * Ki,1.0);
            
            newOutput = absMax((pTerm + ((pTerm < 0)?-basePower:basePower)),maxpower);
            
            leftPower = - newOutput;
            rightPower = newOutput;
            leftDrive.setPower(leftPower);
            rightDrive.setPower(rightPower);
            telemetry.addData("rotate degrees ",degrees);
            telemetry.addData("current globalAngle",globalAngle);
            //telemetry.addData("iError",iError);
            //telemetry.addData("iTerm",iTerm);
            telemetry.addData("pTerm",pTerm);
            telemetry.addData("basePower",basePower);
            telemetry.addData("newOutput",newOutput);
           
            telemetry.update();
            count++;
            
        }
        
        leftDrive.setPower(0);
        rightDrive.setPower(0);  
        telemetry.addData("rotate degrees ",degrees);
        telemetry.addData("current globalAngle",globalAngle);
         telemetry.addData("count",count);
        
         
        // Turn encoders back on
        leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        waitFor(100); // Let the motors stop
        
       telemetry.addData("final globalAngle",getAngle());
       telemetry.update();
        // waitFor(1000);
    }
    private void rotateTo(double degrees, double power)
    {
        double  leftPower, rightPower;

        // Turning doesn't use the Encoders. 
        leftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        // restart imu movement tracking.
        resetAngle();
        telemetry.addData("rotate to degrees ",degrees);
        telemetry.addData("current globalAngle",globalAngle);
        telemetry.update();
        waitFor(2000);
        
        // getAngle() returns + when rotating counter clockwise (left) and - when rotating
        // clockwise (right).

        if (degrees < 0)
        {   // turn right.
            leftPower = power;
            rightPower = -power;
        }
        else if (degrees > 0)
        {   // turn left.
            leftPower = -power;
            rightPower = power;
        }
        else return;

        // set power to rotate.
        leftDrive.setPower(leftPower);
        rightDrive.setPower(rightPower);

        // rotate until turn is completed.
        if (degrees < 0)
        {
            // On right turn we have to get off zero first.
            while (opModeIsActive() && getAngle() == 0) {}

            while (opModeIsActive() && getAngle() > degrees) {}
        }
        else    // left turn.
            while (opModeIsActive() && getAngle() < degrees) {}

        // turn the motors off.
        rightDrive.setPower(0);
        leftDrive.setPower(0);
        leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        
        // wait for rotation to stop.
        waitFor(400);
        getAngle();
        telemetry.addData("rotate to degrees ",degrees);
        telemetry.addData("current globalAngle",globalAngle);
        telemetry.update();
        waitFor(2000);
        // reset angle tracking on new heading.
        resetAngle();
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
        armDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        
        
        leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        PIDFCoefficients cof = shooterDrive.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);        
        cof.p = 70;        
        cof.i = 18;        
        shooterDrive.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,cof);
        
        shooterServo.setPosition(0.0);
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
        
        //
        // IMU 
        //
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        // parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = false;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
        
     
        
        
        // Wait for the game to start (driver presses PLAY)
        
        
        waitForStart();
        runtime.reset();
        // Start the logging of measured acceleration
        //imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);

  /*
        // Let's do just some turning tests.
        rotateLeft(160.0,.3);
        waitFor(2000);
      
        rotateRight(160.0,.3);
        waitFor(2000);
        rotateRight(45.0,.3);
        waitFor(2000);

        rotateLeft(180.0,0.4);
        waitFor(2000);
        rotateRight(180.0,0.4);
        waitFor(10000);
    */    


        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Setup a variable for each drive wheel to save power level for telemetry
            double leftPower;
            double rightPower;
            double armPower;
            
            int targetMode = lastReconizedLocation.mDetected;
            
            shooterDrive.setVelocity(-204,AngleUnit.DEGREES);
    
            driveForward(900,.4);
            waitFor(2000);
            shoot(3);
            
            switch(targetMode){
                case RingLocation.TARGET_ZERO_RINGS:     //Target A. Second wobble goal scoring currently in progress
                    driveForward(2550,0.6);             // driveTo(3400,3400,.4); //go forward
                    rotateRight(87.0,0.3);              // driveTo(4050,2650,.4); //turn right 750
                    driveReverse(400,0.4);              // driveTo(3950,2450,.4); //back up while turning slightly
                                                        // driveTo(3850,2350,.4); //back up a little farther
                    moveArmTo(-2400, .4); //drop wobble
                    armServo.setPosition(.8);
                    waitFor(300);
                    
                    driveForward(600,0.5);               // driveTo(4050,2650,.4); //go forward, park on the line
                    rotateLeft(88.0,0.25); // rotete to get ready to reverse to get the other wobble
                    driveReverse(2325,0.6); 
                   
                     
                    armServo.setPosition(0);//pick up wobble
                    waitFor(800);
                    moveArmTo(-1100, .4);
                    
                    
                    driveForward(1900,0.6); 
                    rotateRight(85.0,0.25);   
                    driveReverse(770,0.4); 
                    
                    moveArmTo(-2250, .4); //drop wobble
                    armServo.setPosition(.8);
                    waitFor(200);
                    driveForward(400,0.4);
                     
                    //driveTo(3500,3350,.4); //turn to face 2nd wobble
                    //driveTo(1500,1350,.4); //go to second wobble
                    //armServo.setPosition(0);
                    //waitFor(500);
                    //moveArmTo(0, .3); //bring back up

                    
                    
                break;
                case RingLocation.TARGET_ONE_RING:     //Target B
                    intakeDrive.setPower(1);
                    
                    waitFor(300);
                    rotateRight(10,.3);                              //  driveTo(913, 794,.4); // turn to pick up ring
                    driveForward(1300,.4);                          //driveTo(2239,1947,.4); //drive to second shooting location while intaking
                    rotateLeft(10,0.3);                             // driveTo(1950,2150,.4); //turn to face goals
                    driveReverse(1000,.4);
                    rotateLeft(2,0.3);
                    waitFor(2500);
                    shoot(1);
                    rotateRight(2,0.3);
                    
                    //rotateRight(5,0.3);                             // driveTo(2050, 2050,.4);
                    driveForward(2700,.4);                          // driveTo(3600, 3600,.4); //go forward to the square ready drop of wobble(5039, 5147) 
                    rotateRight(175,.4);                            // driveTo(1800, 5400,.4); //180 turn
                    moveArmTo(-2400, .3); 
                    armServo.setPosition(.8);
                    waitFor(1000);
                    driveForward(300,.4);                           // driveTo(2000, 5600,.4); // drive to line
                    intakeDrive.setPower(0); // no longer needed
                    // shooter angle 6 1/8
                break;
                case RingLocation.TARGET_FOUR_RINGS:
                    intakeDrive.setPower(1);
                    shooterDrive.setVelocity(-220,AngleUnit.DEGREES);
                    
                    waitFor(300); //let intake get up to speed
                    rotateRight(5,.3);                              // driveTo(885, 800,.4); //turn a little
                    driveForward(1000,.4);                          // driveTo(1960, 1875,.5); //go forward to pick up rings
                    rotateLeft(7,.3);                               // driveTo(1675, 2125,.4); //turn to goals
                    
                    waitFor(2000); //wait is so we can load the rings
                    shoot(3);
                    rotateRight(7,.3);                              // driveTo(1725, 2075,.4); //turn back towards rings
                    driveReverse(75,.4);                            // driveTo(1600,2000,.4);//back up 
                    driveForward(600,.4);                           // driveTo(2100, 2500,.4); //go forward to pick up more rings
                    rotateLeft(12,.3);                               // driveTo(2025, 2575,.4); //turn front to aim at goal
                    waitFor(2000); //let last ring finish intaking
                                                                     // Experimental backup driveTo(1625,2175,.4);
                    shoot(3); //shoot more then expected in case 
                    rotateRight(161,.3);
                    driveReverse(2500,.4);                          // driveTo(3600,1300,.4); //turn back to goal 3350 1600
                                                                    // driveTo(1150,-1150,.4); //back up to wobble drop area
                    intakeDrive.setPower(0); //intake is no longer necessary
                    
                    moveArmTo(-2400, .3); //drop wobble goal
                    armServo.setPosition(.8);
                    waitFor(500);
                    driveForward(1600,.4);                          // driveTo(2750,250,.4); //forward to park on line
                break;
            }
            
            return;
        }
    }
    
}
