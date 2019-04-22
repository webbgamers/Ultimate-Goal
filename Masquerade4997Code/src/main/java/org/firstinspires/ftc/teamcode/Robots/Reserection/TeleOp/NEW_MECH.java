package org.firstinspires.ftc.teamcode.Robots.Reserection.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.Robots.Reserection.ResurrectionSubSystems.MasqCollectorDumper.Positions;

import org.firstinspires.ftc.teamcode.Robots.Reserection.Resurrection;
import org.firstinspires.ftc.teamcode.Robots.Reserection.ResurrectionSubSystems.MasqCollectorDumper;

import Library4997.MasqControlSystems.MasqPID.MasqPIDController;
import Library4997.MasqResources.MasqMath.MasqVector;
import Library4997.MasqWrappers.MasqLinearOpMode;

/**
 * Created by Archishmaan Peyyety on 10/27/18.
 * Project: MasqLib
 */
@TeleOp(name = "NEW_MECH", group = "NFS")
public class NEW_MECH extends MasqLinearOpMode implements Constants {
    private Resurrection resurrection = new Resurrection();
    private boolean scoreState = false;

    private MasqPIDController speedController = new MasqPIDController(0.05, 0.0, 0.00001);
    private double maxAutoPositioningSpeed = 0.7;

    private MasqVector scorePosition = new MasqVector(0, 0);
    private double scoreHeading = 0;
    private MasqVector current = new MasqVector(0, 0);
    MasqPIDController pidController = new MasqPIDController(0.0025, 0, 0.00001);
    Positions p = Positions.TRANSFER;
    @Override
    public void runLinearOpMode()  {
        robotInit();

        resurrection.driveTrain.setClosedLoop(true);
        resurrection.collectorDumper.startPositionThread();
        current = new MasqVector(resurrection.tracker.getGlobalX(), resurrection.tracker.getGlobalY());
        resurrection.collectorDumper.setAutoPosition(MasqCollectorDumper.Positions.HORIZONTAL);
        while (!opModeIsActive()) {
            resurrection.tracker.updateSystem();
            dash.create("X: ", resurrection.tracker.getGlobalX());
            dash.create("Y: ", resurrection.tracker.getGlobalY());
            dash.create("H: ", resurrection.tracker.getHeading());
            dash.create("Encoder thing: ", resurrection.collectorDumper.getCurrentPosition());
            dash.update();
        }
        waitForStart();
        while (opModeIsActive()) {

            if (controller1.leftTriggerPressed()) resurrection.collector.setPower(.5);
            else if (controller1.leftBumper()) resurrection.collector.setPower(-.5);
            else resurrection.collector.setPower(0);

            if (controller1.dPadUp()) scoreState = true;
            else if (controller1.isJoysticksActive()) scoreState = false;

            if (scoreState) gotoScore();
            else resurrection.MECH(controller1);

            if (controller2.y() || controller1.x()) {
                scorePosition = new MasqVector(resurrection.tracker.getGlobalX(), resurrection.tracker.getGlobalY());
                scoreHeading = resurrection.tracker.getHeading();
            }

            controlStateCollectionDump();
            particleDumperControl();
            hangControl();

            resurrection.collectionLift.DriverControl(controller1);
            resurrection.scoreLift.DriverControl(controller2);

            current = new MasqVector(resurrection.tracker.getGlobalX(), resurrection.tracker.getGlobalY());

            resurrection.tracker.updateSystem();
        }
    }
    public void controlStateCollectionDump() {
        if (controller1.leftTriggerPressed() || controller1.leftBumper()) p = Positions.COLLECT;
        else if (Math.abs(resurrection.collectionLift.lift.getPower()) > 0) p = Positions.DOWN;
        else if (!resurrection.collectionLiftSwitch.isPressed()) p = Positions.DOWN;

        if (controller1.dPadUp()) p = Positions.TRANSFER;
        else if (controller1.dPadDown()) p = Positions.DOWN;
        else if (controller1.dPadRight()) p = Positions.HORIZONTAL;
        resurrection.collectorDumper.setPower(pidController.getOutput(resurrection.collectorDumper.getCurrentPosition(), p.getPosition()));

    }
    public void particleDumperControl() {
        if (controller2.b()) resurrection.particleDumper.setPosition(PARTICLE_DUMPER_OUT);
        else if (controller2.rightTriggerPressed() || controller2.rightBumper()) resurrection.particleDumper.setPosition(PARTICLE_DUMPER_PARALLEL);
        else resurrection.particleDumper.setPosition(PARTICLE_DUMPER_IN);
    }
    public void hangControl() {
        if (controller2.leftStickY() < 0 && !resurrection.hangTopSwitch.getState()) resurrection.hang.setPower(-1);
        else if (controller2.leftStickY() > 0 && !resurrection.hangBottomSwitch.getState()) resurrection.hang.setPower(1);
        else resurrection.hang.setBreakMode();


    }
    public void gotoScore() {
        MasqVector displacement = current.displacement(scorePosition);
        double speed = speedController.getOutput(displacement.getMagnitude());
        if (Math.abs(speed) > maxAutoPositioningSpeed) speed = maxAutoPositioningSpeed;
        double pathAngle = 90 - Math.toDegrees(Math.atan2(displacement.getY(), displacement.getX()));
        resurrection.driveTrain.setPowerMECH(pathAngle + resurrection.tracker.getHeading(), speed, scoreHeading);
        dash.create("speed: ", speed);
    }
    public void robotInit() {
        resurrection.setStartOpenCV(false);
        resurrection.mapHardware(hardwareMap);
        resurrection.initializeTeleop();
        resurrection.hang.setClosedLoop(true);
        resurrection.driveTrain.setClosedLoop(true);
        resurrection.particleDumper.setPosition(PARTICLE_DUMPER_OUT);
    }

    //if (!resurrection.collectionLiftSwitch.isPressed()) resurrection.collectionLift.lift.setPower(1);
}
