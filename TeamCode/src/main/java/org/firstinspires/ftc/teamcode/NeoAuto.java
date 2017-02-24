package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;

import org.lasarobotics.vision.opmode.LinearVisionOpMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by matt on 10/15/16.
 */
@Autonomous(name = "2856 Button Autonomous")
public class NeoAuto extends LinearVisionOpMode {
    I2cDeviceSynch imu;
    I2cDevice lrs;
    I2cDevice rrs;
    DcMotor m0;
    DcMotor m1;
    DcMotor m2;
    DcMotor m3;
    DcMotor shooter;
    Robot robot;
    ColorSensor csf;
    ColorSensor csb;
    UltrasonicSensor us;
    Servo la;
    ColorSensor bs; // beacon sensor
    int side;

    Float[] forward = new Float[]{1f,0f};
    Float[] backward = new Float[]{-1f,0f};

    @Override
    public void runOpMode() throws InterruptedException {
        initDevices();
        side = getSide();

        if(side == -1) { // on red side, thus using left side of robot
            bs = hardwareMap.colorSensor.get("lbs");
        } else {
            bs = hardwareMap.colorSensor.get("rbs");
        }

        waitForStart();

        straightConst();
        robot.Straight(1.4f, forward, 10, telemetry);
        turnConst();
        if(side == -1) {
            //turn a little to the right
            robot.AngleTurn(-25*side, 10, telemetry);
            //shooter.setPower(1);
            Thread.sleep(1000);
            shooter.setPower(0);
            turnConst();
            robot.Data.PID.turnPrecision = 2; // we don't need precision on this turn, it just needs to be fast
            //robot.AngleTurn((70+25)*side, 10, telemetry);
            robot.AngleTurn((75+25)*side, 10, telemetry);
            robot.Data.PID.turnPrecision = 1;
        } else {
            robot.AngleTurn(0, 10, telemetry); // reset
            //shooter.setPower(1);
            Thread.sleep(1000);
            shooter.setPower(0);
            turnConst();
            robot.Data.PID.turnPrecision = 2; // we don't need precision on this turn, it just needs to be fast
            robot.AngleTurn(70*side, 10, telemetry);
            robot.Data.PID.turnPrecision = 1;
        }

        straightConst();
//        robot.Straight(0.2f, forward, 4, telemetry);
//        lineConst();
//        robot.MoveToLine(forward, csf, .2f, 10, telemetry);

        //begin alignment
        robot.Straight(2.45f, forward, 2, telemetry);



        turnConst();



        //robot.AngleTurn(-60*side, 10, telemetry);
        robot.AngleTurn(-65*side, 10, telemetry);
        robot.AngleTurn(0, 10, telemetry);


        straightConst();


        robot.AlignWithWall(7, forward, 10, telemetry); // 9 worked well, 8 was still to far I think



        turnConst();



        robot.AngleTurn(-11*side, 10, telemetry); // added one degree bias to bring closer to beacon
        robot.AngleTurn(0, 10, telemetry); // reset

        lineConst();
        robot.MoveToLine(forward, csb, 0.2f, 10, telemetry);
        Thread.sleep(100);
        robot.MoveToLine(backward, csb, 0.1f, 10, telemetry);
        telemetry.log();
        telemetry.addData("Beacon 1", robot.getDistance());
        telemetry.update();
        Thread.sleep(2000);



        turnConst();


        if(robot.getDistance() >= 5 && robot.getDistance() < 8) { // ok
            push(10); // turn in further to have a better chance of pressing
        } else if (robot.getDistance() <= 4){ // good
            push(5);
        } else if (robot.getDistance() >= 8) { // extreme, fix
            push(20);
        }

        //straightConst();
        //robot.Straight(0.2f, forward, 10, telemetry);



        turnConst();



        robot.AngleTurn(11*side, 10, telemetry); // 1 DEG BIAS HERE ASWELL



        straightConst();


        robot.Straight(.6f, backward, 10, telemetry);
//        robot.UpdateTarget(30*side);
//        robot.Straight(0.3f, backward, 10, telemetry);



        turnConst();



        robot.AngleTurn(-20*side, 10, telemetry);


        straightConst();


        robot.AlignWithWall(7, backward, 10, telemetry);



        turnConst();



        robot.AngleTurn(10*side, 10, telemetry);
        robot.AngleTurn(0, 10, telemetry); // reset

        lineConst();
        robot.MoveToLine(backward, csb, 0.2f, 10, telemetry);
        Thread.sleep(100);
        robot.MoveToLine(forward, csb, 0.1f, 10, telemetry);
        telemetry.addData("Beacon 2", robot.getDistance());
        telemetry.update();


        turnConst();



        if(robot.getDistance() > 8) {
            push(17); // turn in further to have a better chance of pressing
        } else if (robot.getDistance() <= 6){
            push(0);
        } else {
            push(10);
        }
        robot.AngleTurn(-15f, 2, telemetry);
        robot.Straight(3f, backward, 3, telemetry);
    }


    private void straightConst() {
        robot.Data.PID.PTuning = 10f;
        robot.Data.PID.ITuning = 5f;
        robot.Data.PID.DTuning = 0f;
    }

    private void turnConst() {
        robot.Data.PID.PTuning = 7f;
        robot.Data.PID.ITuning = 5f;
        robot.Data.PID.DTuning = 0f;
    }

    private void lineConst() {
        robot.Data.PID.PTuning = 4f;
        robot.Data.PID.ITuning = 5f;
        robot.Data.PID.DTuning = 0f;
    }

    private void initDevices() {
        imu = hardwareMap.i2cDeviceSynch.get("imu");
        m0 = hardwareMap.dcMotor.get("m0");
        m1 = hardwareMap.dcMotor.get("m1");
        m2 = hardwareMap.dcMotor.get("m2");
        m3 = hardwareMap.dcMotor.get("m3");
        shooter = hardwareMap.dcMotor.get("shooter");
        shooter.setDirection(DcMotor.Direction.REVERSE);
        csf = hardwareMap.colorSensor.get("csf");
        csb = hardwareMap.colorSensor.get("csb");
        la = hardwareMap.servo.get("la");
        lrs = hardwareMap.i2cDevice.get("lrs");
        rrs = hardwareMap.i2cDevice.get("rrs");
        robot = new Robot(imu, m0, m1, m2, m3, lrs, rrs, telemetry);
    }

    public void push(int degrees) {
        if (side == -1) { // on red side
            if(bs.blue() > bs.red()) {
                telemetry.addData("color", "BLUE > RED");
                robot.Straight(0.2f, forward, 10, telemetry);
            } else {
                telemetry.addData("color", "RED > BLUE");
            }
        } else { // on blue side
            if(bs.blue() > bs.red()) {
                telemetry.addData("color", "BLUE > RED");
            } else {
                telemetry.addData("color", "RED > BLUE");
                robot.Straight(0.2f, forward, 10, telemetry);
            }
        }
        telemetry.update();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(degrees >= 20) {
            robot.Straight(0.1f, backward, 10, telemetry);
        }



        // out first then turn into it... worked well with turning first too
        la.setPosition(1.0);
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        robot.AngleTurn(-degrees*side, 5, telemetry);




        la.setPosition(0);
        robot.AngleTurn(degrees*side, 5, telemetry);
    }

    private int getSide() {
        int s;
        // Retrieve file.
        File file = new File("/sdcard/Pictures", "prefs");
        StringBuilder text = new StringBuilder();
        // Attempt to load line from file into the buffer.
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            // Ensure that the first line is not null.
            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            // Close the buffer reader
            br.close();
        }
        // Catch exceptions... Or don't because that would require effort.
        catch (IOException e) {
        }

        // Provide in a more user friendly form.
        String sideText = text.toString();
        if(sideText.equals("red")) {
            s = -1;
        } else if (sideText.equals("blue")) {
            s = 1;
        } else { //this should never happen
            s = 1;
        }
        return s;
    }

}
