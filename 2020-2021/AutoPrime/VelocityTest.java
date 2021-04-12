package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
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
@Autonomous(name="Velocity Test", group="Test")

public class VelocityTest extends OpMode
{
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotorEx shooterDrive = null;

    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");

        shooterDrive  = hardwareMap.get(DcMotorEx.class, "ShooterMotor");
        shooterDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooterDrive.setDirection(DcMotor.Direction.REVERSE);
        PIDFCoefficients cof =     shooterDrive.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        cof.p = 60;
        cof.i = 18;
        shooterDrive.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,cof);

        
        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");
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
        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        double motorVelocity = -1500;
        PIDFCoefficients cof =     shooterDrive.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        shooterDrive.setVelocity(motorVelocity);

        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "shooter (%.2f)", shooterDrive.getVelocity());
        telemetry.addData("PID", "P (%.2f) I (%.2f) D (%.2f)", 
            cof.p,cof.i,cof.d);
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }

}
