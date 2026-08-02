package org.firstinspires.ftc.teamcode.teleOps;


import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.Intake;

import java.util.List;

@Disabled
@TeleOp(name = "Test Tele")
public class DemoTeleOp extends OpMode {
    //subsytems
    private Intake intake;


    public void init() {
        intake = new Intake(hardwareMap);
// Get all hubs (Control Hub + Expansion Hub)
        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);

        // Iterate and set mode
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }
    }

    public void loop() {

        if (gamepad1.dpad_up) {
            intake.extend();

        }
        if (gamepad1.dpad_down) {
            intake.retract();
        }
        if (gamepad1.a) {
            intake.intake();
        } else {
            intake.stop();
        }
    }

    public void start() {


    }

}
