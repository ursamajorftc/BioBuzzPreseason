package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all linear slide motors together so they move as one mechanism.
 * Automatically keeps the slides locked when stopped and calculates motor power using PIDF.
 */
public class Slides {
    private final List<DcMotorEx> motorArray = new ArrayList<>();
    private PIDFController slidesPidf;

    private int targetPosition = 0;
    private double lastPower = 0;

    /**
     * Set up the slide motors.
     * Put this in your OpMode's init section.
     *
     * @param hardwareMap The robot's hardware map from your OpMode.
     * @param motorNames  The exact names of the slide motors from the Driver Station app config.
     *                    Example: new Slides(hardwareMap, "leftSlide", "rightSlide");
     *                    Note: Encoder Readings are only from the first motor.
     */
    public Slides(HardwareMap hardwareMap, String... motorNames) {
        for (String motorName : motorNames) {
            motorArray.add(hardwareMap.get(DcMotorEx.class, motorName));
        }

        slidesPidf = new PIDFController(0, 0, 0, 0);

        for (DcMotorEx motor : motorArray) {
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
        resetEncoderPosition();
    }

    /**
     * Sets which direction each motor spins. Use this if one motor needs to spin backward
     * because it is mounted facing the opposite direction.
     *
     * @param directions Directions matching the order of motor names passed into the constructor.
     *                   Example: setMotorOrientation(DcMotorEx.Direction.FORWARD, DcMotorEx.Direction.REVERSE);
     */
    public void setMotorOrientation(DcMotorEx.Direction... directions) {
        if (directions.length != motorArray.size()) {
            throw new IllegalArgumentException(
                    "Direction count (" + directions.length + ") must match motor count (" + motorArray.size() + ")"
            );
        }
        for (int i = 0; i < motorArray.size(); i++) {
            motorArray.get(i).setDirection(directions[i]);
        }
    }

    /**
     * Forces all slide motors to run at a specific speed manually.
     *
     * @param power Motor speed between -1.0 (full reverse) and 1.0 (full forward).
     */
    public void setPower(double power) {
        if (Math.abs(power - lastPower) < 0.005 && power != 0) return;
        for (DcMotorEx motor : motorArray) {
            motor.setPower(power);
        }
        lastPower = power;
    }

    /**
     * Tunes how smoothly and accurately the slides reach their target height.
     *
     * @param p Proportional term. Higher values push harder when far from the target.
     * @param i Integral term. Fixes small stubborn errors over time. Usually 0.
     * @param d Derivative term. Acts like a brake to prevent overshooting the target.
     * @param f Feedforward term. Extra force applied to fight gravity.
     */
    public void setPidfConstants(double p, double i, double d, double f) {
        slidesPidf.setPIDF(p, i, d, f);
    }

    /**
     * Tells the slides where to go in encoder ticks.
     * Note: This only sets the destination—you MUST keep calling moveToTarget() in your loop to actually move.
     *
     * @param position Target height in encoder ticks.
     */
    public void setTargetPosition(int position) {
        targetPosition = position;
        slidesPidf.setSetPoint(position);
    }

    /**
     * Calculates required motor power and moves the slides toward the target height.
     * MUST be called repeatedly inside your OpMode's loop() method.
     */
    public void moveToTarget() {
        if (motorArray.isEmpty()) return;

        double currentPosition = motorArray.get(0).getCurrentPosition();
        double power = slidesPidf.calculate(currentPosition);

        setPower(power);
    }

    /**
     * Stops all slide motors immediately.
     */
    public void stop() {
        setPower(0);
    }

    /**
     * Gets current slide height.
     *
     * @return The current position of the first motor in encoder ticks.
     */
    public double getPosition() {
        return motorArray.isEmpty() ? 0 : motorArray.get(0).getCurrentPosition();
    }

    /**
     * Resets current physical position of the slides to 0 ticks.
     * Run this only when slides are physically down at bottom limit.
     */
    public void resetEncoderPosition() {
        for (DcMotorEx motor : motorArray) {
            motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        }
    }

    /**
     * Checks if slides reached target destination within an acceptable margin of error.
     *
     * @param tolerance How many ticks away from target counts as "close enough" (e.g., 10 or 20 ticks).
     * @return True if slides are within tolerance of target height, false otherwise.
     */
    public boolean isAtTarget(double tolerance) {
        return Math.abs(getPosition() - targetPosition) <= tolerance;
    }
}