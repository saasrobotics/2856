#pragma config(Hubs,  S1, HTMotor,  HTMotor,  HTMotor,  HTServo)
#pragma config(Sensor, S1,     ,               sensorI2CMuxController)
#pragma config(Motor,  mtr_S1_C1_1,     LeftDriveMotor, tmotorTetrix, openLoop)
#pragma config(Motor,  mtr_S1_C1_2,     RightDriveMotor, tmotorTetrix, openLoop)
#pragma config(Motor,  mtr_S1_C2_1,     Arm,           tmotorTetrix, openLoop)
#pragma config(Motor,  mtr_S1_C2_2,     Shooter,       tmotorTetrix, openLoop)
#pragma config(Motor,  mtr_S1_C3_1,     BlockCollector, tmotorTetrix, openLoop)
#pragma config(Motor,  mtr_S1_C3_2,     motorI,        tmotorTetrix, openLoop)
#pragma config(Servo,  srvo_S1_C4_1,    Left,                 tServoStandard)
#pragma config(Servo,  srvo_S1_C4_2,    Right,                tServoStandard)
//*!!Code automatically generated by 'ROBOTC' configuration wizard               !!*//

#include "JoystickDriver.c"

void MoveLeft(int Power)
{
	motor[LeftDriveMotor] = Power;
	nxtDisplayString(1, "LeftPower: %i", Power);
}

void RaiseServos()
{
	servo[Left] = 240;
}

void LowerServos()
{
	servo[Left] = 70;
}

void MoveRight(int Power)
{
	motor[RightDriveMotor] = -Power;
	nxtDisplayString(2, "RightPower: %i", -Power);
}

void PickupBlocks(int Power)
{
	motor[BlockCollector] = Power;
	nxtDisplayString(3, "PickupPower: %i", Power);
}

void MoveArm(int Power)
{
	motor[Arm] = Power;
	nxtDisplayString(4, "ArmPower: %i", Power);
}

task main()
{
	//stop the debugger from printing
	bDisplayDiagnostics = false;

	while(true)
	{
		//sleep so that it can draw
		sleep(100);

		//erases display
		eraseDisplay();

		//updates each loop
		getJoystickSettings(joystick);

		//moves left side
		if(abs(joystick.joy1_y1) > 10)
		{
			MoveLeft(joystick.joy1_y1 / 2.0);
		}
		else
		{
			MoveLeft(0);
		}

		//moves right side
		if(abs(joystick.joy1_y2) > 10)
		{
			MoveRight((joystick.joy1_y2 / 2.0));
		}
		else
		{
			MoveRight(0);
		}

		//moves block picker-upper
		if(joystick.joy2_y1 > 10)
		{
			PickupBlocks(100);
		}
		else if (joystick.joy2_y1 < -10)
		{
			PickupBlocks(-100);
		}
		else
		{
			PickupBlocks(0);
		}

		//moves arm
		if(abs(joystick.joy2_y2) > 10)
		{
			MoveArm((joystick.joy2_y2 / 1.26));
		}
		else
		{
			MoveArm(0);
		}

		//if the right trigger is pressed, raise the servos
		if(joy1Btn(7) == 1)
		{
			RaiseServos();
			nxtDisplayString(5, "Raise");
		}
		//if the left trigger is pressed, lower the servos
		else if(joy1Btn(8) == 1)
		{
			LowerServos();
			nxtDisplayString(5, "Lower");
		}

		//if the left trigger is pressed, start shooting
		if(joy2Btn(7) == 1)
		{
			nxtDisplayString(6, "Shoot");
			motor[Shooter] = 100;
		}
		//if the right trigger is pressed, stop shooting
		else if (joy2Btn(8) == 1)
		{
			nxtDisplayString(6, "No Shoot");
			motor[Shooter] = 0;
		}

	}
}