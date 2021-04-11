package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@Autonomous(name="Arm Motor Test", group="Test")

public class ArmMotorTest extends OpMode
{
    private DcMotorEx armDrive = null;
    private boolean armGoingUp = true;

    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");

        armDrive  = hardwareMap.get(DcMotorEx.class, "ArmMotor");

        armDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
    }

    @Override
    public void loop() {
        double armPower = 0.0;
        if(true == armGoingUp) {
            armPower = -0.2;
        }
        else {
            armPower = 0.2;
        }
        armDrive.setPower(armPower);

        if(armDrive.getCurrentPosition() < -400) {
            armGoingUp = false;
        }

        if(armDrive.getCurrentPosition() > 0) {
            armGoingUp = true;
        }

        telemetry.addData("Motors", "arm (%d)", 
        armDrive.getCurrentPosition());
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }

}
