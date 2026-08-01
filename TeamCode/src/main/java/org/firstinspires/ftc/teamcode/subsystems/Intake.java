package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.utilities.Slides;

public class Intake {
    private Slides slides;
    private DcMotorEx intakeMotor;
    private TouchSensor magnetSwitch;

    public Intake(HardwareMap hardwareMap) {
        //setup intake motor
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");
        //setup slides
        slides = new Slides(hardwareMap, "slideMotor");
        slides.setMotorOrientation(DcMotorSimple.Direction.REVERSE);
        slides.setPidfConstants(100,0,100,0);
        //setup limit switch
        magnetSwitch=hardwareMap.get(TouchSensor.class, "IntakeSwitch");
    }
    public void extend(){
        slides.setTargetPosition(1200);
        slides.moveToTarget();
    }
    public void retract(){
        slides.setTargetPosition(0);
        slides.moveToTarget();
        if (magnetSwitch.isPressed()){
            slides.resetEncoderPosition();
        }
    }

    public void intake(){
        intakeMotor.setPower(1);
    }
    public void stop(){
        intakeMotor.setPower(0);
    }

}

