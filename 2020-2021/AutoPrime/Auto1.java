package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
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

@Autonomous(name="Good Auto", group="Good")

public class Auto1 extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftDrive = null;
    private DcMotor rightDrive = null;
    private DcMotor armDrive = null;
    private Servo armServo = null;
    private Servo shooterServo = null;
    private DcMotor intakeDrive = null;
    private DcMotorEx shooterDrive = null;
    
    private final int STATE_RUN_FORWARD1 = 0;
    private final int STATE_RUN_SHOOT3_A = 1;
    private final int STATE_RUN_SHOOT3_B = 2;
    private final int STATE_RUN_INTAKE =   3;
    private final int STATE_RUN_SHOOT1_A = 4;
    private final int STATE_RUN_SHOOT1_B = 5;
    private final int STATE_RUN_FORWARD2 = 6;
    private final int STATE_STOP         = 7;
    
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
        
        leftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        
        
        leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Setup a variable for each drive wheel to save power level for telemetry
            double leftPower;
            double rightPower;
            double armPower;

            switch(mState)
            {
                case(STATE_RUN_FORWARD1):
                    telemetry.addData("State", "Forward1");
                    leftDrive.setPower(1);
                    rightDrive.setPower(1);
                    
                    telemetry.addData("Encoders", "left (%d), right (%d)", 
                    leftDrive.getCurrentPosition(), rightDrive.getCurrentPosition());
                    
                    
                     if(leftDrive.getCurrentPosition()>=2000){
                        count = 0;
                        leftDrive.setPower(0);
                        rightDrive.setPower(0);
                        mState = STATE_RUN_SHOOT3_A;
                     }
                    break;
                case(STATE_RUN_SHOOT3_A):
                    telemetry.addData("State", "Shoot3A");
                    shooterDrive.setVelocity(-200,AngleUnit.DEGREES);                  
                    count++;
                    if (count>800){
                       mState = STATE_RUN_SHOOT3_B;
                       count = 0;
                    }
                    break;
                case(STATE_RUN_SHOOT3_B):
                    telemetry.addData("State", "Shoot3B");
                    shooterDrive.setVelocity(-200,AngleUnit.DEGREES);                  
                    count++;
                    if (count<300){
                     shooterServo.setPosition(.9);
                    }
                    else{
                        if (count<600){
                            shooterServo.setPosition(.2);
                        }
                        else{
                            if (count<900){
                                shooterServo.setPosition(.9);
                            }
                            else{
                                if (count<1200){
                                    shooterServo.setPosition(.2);
                                }
                                else{
                                    if (count<1500){
                                         shooterServo.setPosition(.9);
                                    }
                                    else{
                                        if (count<1800){
                                            shooterServo.setPosition(.2);
                                            shooterDrive.setVelocity(0,AngleUnit.DEGREES); 
                                            mState= STATE_RUN_INTAKE;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                case(STATE_RUN_INTAKE):
                    telemetry.addData("State", "Forward1");
                    intakeDrive.setPower(1);
                    leftDrive.setPower(1);
                    rightDrive.setPower(1);
                    
                     if(leftDrive.getCurrentPosition()>=2600){
                        leftDrive.setPower(0);
                        rightDrive.setPower(0);
                        intakeDrive.setPower(0);
                        mState = STATE_RUN_FORWARD2;
                     }
                    break;
                case(STATE_RUN_FORWARD2):
                   telemetry.addData("State", "Forward1");
                    leftDrive.setPower(1);
                    rightDrive.setPower(1);
                    
                     if(leftDrive.getCurrentPosition()>=3000){
                        leftDrive.setPower(0);
                        rightDrive.setPower(0);
                        mState = STATE_STOP;
                     }
                    break;
                case(STATE_STOP):
//                  Set the motors t0 0.0                    
                    if(true == firstTimeInStop)
                    {
                        firstTimeInStop = false;
                        telemetry.addData("State", "Stop");
                    }
                break;
            }
            telemetry.update();
        }
    }
}
