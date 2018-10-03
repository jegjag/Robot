#ifndef MOTOR_H
#define MOTOR_H

// Motor A pins
#define A_BRAKE 9
#define A_DIR 12
#define A_ANALOG 3

// Motor B pins
#define B_BRAKE 8
#define B_DIR 13
#define B_ANALOG 11

enum Motor
{
	A, B
};

enum Direction
{
	FORWARD, BACKWARD
};

#endif
