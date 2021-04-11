package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DigitalChannel;

@Autonomous(name = "Arm Touch Test", group = "Test")

public class ArmTouchTest extends LinearOpMode {
    DigitalChannel digitalTouch1;  // Hardware Device Object
    DigitalChannel digitalTouch2;  // Hardware Device Object

    @Override
    public void runOpMode() {
        digitalTouch1 = hardwareMap.get(DigitalChannel.class, "ArmTouch1");
        digitalTouch2 = hardwareMap.get(DigitalChannel.class, "ArmTouch2");

        // set the digital channel to input.
        digitalTouch1.setMode(DigitalChannel.Mode.INPUT);
        digitalTouch2.setMode(DigitalChannel.Mode.INPUT);

        // wait for the start button to be pressed.
        waitForStart();

        // while the op mode is active, loop and read the light levels.
        // Note we use opModeIsActive() as our loop condition because it is an interruptible method.
        while (opModeIsActive()) {

            // send the info back to driver station using telemetry function.
            // if the digital channel returns true it's HIGH and the button is unpressed.
            if (digitalTouch1.getState() == true) {
                telemetry.addData("Digital Touch 1", "Is Not Pressed");
            } else {
                telemetry.addData("Digital Touch 1", "Is Pressed");
            }
            if (digitalTouch2.getState() == true) {
                telemetry.addData("Digital Touch 2", "Is Not Pressed");
            } else {
                telemetry.addData("Digital Touch 2", "Is Pressed");
            }

            telemetry.update();
        }
    }
}
