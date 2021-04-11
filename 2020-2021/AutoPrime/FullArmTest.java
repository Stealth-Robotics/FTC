package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@Autonomous(name="Full Arm Test", group="Test")

public class FullArmTest extends OpMode
{
    private final double ARM_SPEED_UP   = -0.3;
    private final double ARM_SPEED_DOWN = 0.3;
    private final double ARM_SPEED_STOP = 0.0;
    private final double ARM_ENCODER_GRAB_POS = -2400;
    
    private DcMotorEx armDrive = null;
    private boolean armGoingUp = false;
    DigitalChannel digitalTouch = null;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        armDrive  = hardwareMap.get(DcMotorEx.class, "ArmMotor");
        digitalTouch = hardwareMap.get(DigitalChannel.class, "ArmTouch2");

        armDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        digitalTouch.setMode(DigitalChannel.Mode.INPUT);
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
    }

    @Override
    public void loop() {
        double armPower = ARM_SPEED_STOP;
        
        if(true == armGoingUp) {
            armPower = ARM_SPEED_UP;
        }
        else {
            armPower = ARM_SPEED_DOWN;
        }
        
        if ((digitalTouch.getState() == false) && (false == armGoingUp)) {
            armPower = ARM_SPEED_STOP;
            armDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            armDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            
            armGoingUp = true;    
        }
        
        if((armDrive.getCurrentPosition() < ARM_ENCODER_GRAB_POS) && (true == armGoingUp)) {
            armPower = ARM_SPEED_STOP;
            armGoingUp = false;
        }        
        
        armDrive.setPower(armPower);
        
        if (digitalTouch.getState() == true) {
            telemetry.addData("Arm Touch", "Is Not Pressed");
        } else {
            telemetry.addData("Arm Touch", "Is Pressed");
        }        
        
        telemetry.addData("Motors", "arm (%d)", 
        armDrive.getCurrentPosition());        
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        armDrive.setPower(ARM_SPEED_STOP);
    }

}
