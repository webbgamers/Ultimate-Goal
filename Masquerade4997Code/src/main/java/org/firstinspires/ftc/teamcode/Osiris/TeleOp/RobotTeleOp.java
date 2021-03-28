package org.firstinspires.ftc.teamcode.Osiris.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorControllerEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.teamcode.Osiris.Robot.Osiris;

import MasqLibrary.MasqResources.MasqLinearOpMode;

import static MasqLibrary.MasqRobot.OpMode.TELEOP;
import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_USING_ENCODER;
import static com.qualcomm.robotcore.hardware.MotorControlAlgorithm.PIDF;
import static org.firstinspires.ftc.teamcode.Osiris.Robot.Constants.*;

/**
 * Created by Keval Kataria on 11/9/2020
 */

@TeleOp(group = "Main")
public class RobotTeleOp extends MasqLinearOpMode {
    private Osiris robot = new Osiris();
    String mode = "GOAL";
    boolean enabled = false;

    @Override
    public void runLinearOpMode() {
        robot.init(TELEOP);

        dash.create("Initialized");
        dash.update();

        waitForStart();
        //robot.claw.raise();

        while(opModeIsActive()) {
            robot.MECH();

            if(!enabled) robot.intake.setPower(gamepad1.left_trigger - gamepad1.right_trigger);

            if(gamepad1.left_bumper) {
                robot.shooter.setPower(SHOOTER_POWER);
                robot.hopper.setPosition(1);
                robot.compressor.setPosition(1);
                robot.claw.close();
                enabled = true;
            }
            else {
                robot.shooter.setPower(0);
                robot.hopper.setPosition(0);
                robot.compressor.setPosition(0);
                enabled = false;
            }

            if(gamepad1.right_bumper && enabled) robot.flicker.setPosition(1);
            else robot.flicker.setPosition(0);

            if(gamepad1.dpad_left)  SHOOTER_POWER -= 0.001;
            else if(gamepad1.dpad_right) SHOOTER_POWER += 0.001;

            robot.claw.driverControl(gamepad1);

            dash.create("Shooter Speed:", SHOOTER_POWER);
            //dash.create("Shooter Mode:", mode);
            dash.create("Rings in Hopper:", robot.getRings());
            dash.update();
        }
    }
}