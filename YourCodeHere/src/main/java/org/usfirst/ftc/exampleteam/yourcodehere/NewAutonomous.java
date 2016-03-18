package org.usfirst.ftc.exampleteam.yourcodehere;

import android.media.Image;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.qualcomm.robotcore.hardware.*;
import org.swerverobotics.library.*;
import org.swerverobotics.library.interfaces.*;
import com.qualcomm.ftcrobotcontroller.CustomSettingsActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * A skeletal example of a do-nothing first OpMode. Go ahead and change this code
 * to suit your needs, or create sibling OpModes adjacent to this one in the same
 * Java package.
 */
@TeleOp(name="New 2856 Autonomous")
public class NewAutonomous extends SynchronousOpMode {

	public NewIMU Robot;
	public String side = "";

	void SelectSide()
	{
		// Retrieve file.
		File file = new File("/sdcard/Pictures","prefs");
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
		side = text.toString();
	}

	public String TakePicture()
	{
		String ImageSide = "";

		try {
			Trigger.takeImage();
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// get the image here
		int[] Array = Trigger.IsaacDetermineSides();

		//blue on left
		if (Array[0] < Array[1])
		{
			ImageSide = "left";
		}
		// blue on right
		else if (Array[0] > Array[1])
		{
			ImageSide = "right";
		}

		// if we want red, flip it
		if (this.side == "red")
		{
			if(ImageSide == "right")
			{
				ImageSide = "left";
			}
			else if(ImageSide == "left")
			{
				ImageSide = "right";
			}
		}

		telemetry.log.add("color on " + ImageSide + " side");

		return ImageSide;
	}

	@Override
	public void main() throws InterruptedException {
		DcMotor LeftMotor = hardwareMap.dcMotor.get("left_drive");
		DcMotor RightMotor = hardwareMap.dcMotor.get("right_drive");
		DcMotor BackBrace = hardwareMap.dcMotor.get("back_brace");
		Servo climberDeploy = hardwareMap.servo.get("climber_deploy");
		Servo blockConveyer = hardwareMap.servo.get("block_conveyor");
		ColorSensor colorSensor = hardwareMap.colorSensor.get("color_sensor");
		Servo leftWing = hardwareMap.servo.get("left_wing");
		Servo rightWing = hardwareMap.servo.get("right_wing");
		Servo rightGate = hardwareMap.servo.get("right_ramp");
		Servo leftGate = hardwareMap.servo.get("left_ramp");
		Servo hangLock = hardwareMap.servo.get("hang_stop");
		Servo hangingControl = this.hardwareMap.servo.get("hang_adjust");
		DcMotor blockCollector = this.hardwareMap.dcMotor.get("block_collector");
		UltrasonicSensor ultrasonicSensor = this.hardwareMap.ultrasonicSensor.get("ultrasonic_sensor");

		//we don't know which one to reverse yet
		LeftMotor.setDirection(DcMotor.Direction.REVERSE);

		telemetry.log.add("starting imu setup");
		idle();

		//setup the PID stuff
		Robot = new NewIMU(LeftMotor, RightMotor, colorSensor, ultrasonicSensor, hardwareMap, telemetry, this);

		telemetry.log.add("imu setup");
		idle();

		//select the side
		SelectSide();

		//wait for start
		waitForStart();

		//initialize the servos
		blockConveyer.setPosition(.55);
		climberDeploy.setPosition(.5);
		leftGate.setPosition(0);
		rightGate.setPosition(1);
		leftWing.setPosition(.3);
		rightWing.setPosition(.6);
		hangLock.setPosition(.72);
		hangingControl.setPosition(.6);

		//this is the initial rotation; it will be referenced through the entire program
		double InitialRotation = Robot.Rotation();

		Robot.Power = 1f;

		Robot.Straight(1, 3);

		Robot.Stop();

		//maximize turning power
		Robot.TurningPower = 1f;

		//turn so that blocks go away
		int RedFirstTurnOffset = 11;
		int BlueFirstTurnOffset = 3;

		if (side.equals("blue")) {
			Robot.TurnToAngle((float)InitialRotation + 45 - BlueFirstTurnOffset, "Left", 3);
		}
		else
		{
			Robot.TurnToAngle((float)InitialRotation -45 + RedFirstTurnOffset, "Right", 3);
		}

		//set the robot to stop when it hits the white line
		Robot.StopAtLight = true;

		// move backwards, the encoder count is arbitrary
		Robot.Straight(10f, 15);
		Robot.Stop();

		//prevent the robot from stopping at the light again
		Robot.StopAtLight = false;

		if (side.equals("blue")) {
			Robot.Turn(-50, "Right", 2);
		}
		else if (side.equals("red"))
		{
			Robot.Turn(50, "Left", 2);
		}

		if (side.equals("blue")) {
			//get straight
			Robot.TurnToAngle((float) InitialRotation + 90, "Right", 5);
		}
		else if (side.equals("red"))
		{
			Robot.TurnToAngle((float) InitialRotation - 90, "Left", 5);
		}

		// go until we're close
		Robot.StopAtUltrasonic = true;
		Robot.Straight(2, 2);
		Robot.StopAtUltrasonic = false;
		Robot.Stop();

		//we need to somehow straighten here

		// start scoring the climbers
		climberDeploy.setPosition(0);
		Thread.sleep(4000);
		climberDeploy.setPosition(.5);
//
//		//turn so that we're straight
//		Robot.TurnToAngle((float) InitialRotation + 90, "Right", 1);
//
//		//reverse
//		Robot.Straight(-1.0f);
//
//		//line up with the light before we go in
//		Robot.StopAtLight = true;
//		Robot.Turn(20, "Left", 2);
//		Robot.Turn(-40, "Left", 2);
//		Robot.StopAtLight = false;
//
//		Thread.sleep(2000);
//
//		// determine the offset based on the side
//		int FinalTurnOffset = 0;
//
//		if (ImageSide == "left")
//		{
//			FinalTurnOffset = -25;
//		}
//		else if (ImageSide == "right")
//		{
//			FinalTurnOffset = 0;
//		}
//
//		Robot.Stop();
//
//		Thread.sleep(2000);
//
//		//turn to the appropriate angle
//		Robot.TurnToAngle((float) InitialRotation + 90 + FinalTurnOffset, "Left", 3);
//		Robot.Stop();
//
//		//put the back brace down
//		int BackBraceInitialPosition = BackBrace.getCurrentPosition();
//		while(Math.abs(BackBrace.getCurrentPosition() - BackBraceInitialPosition) < 1500)
//		{
//			BackBrace.setPower(-1);
//		}
//		BackBrace.setPower(0);
//
//		// reverse into the button and hit it
//		Robot.Straight(2.0f, 2);
//		Robot.Stop();

		idle();
	}

	public void RunIdle() throws InterruptedException
	{
		idle();
	}
}
