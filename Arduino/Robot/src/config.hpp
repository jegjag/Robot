#ifndef ROBOT_CONFIG
#define ROBOT_CONFIG

#include <Arduino.h>

// User options
/*
 * Define either of these to flip one of the motor directions,
 * useful if polarity is flipped when building.
 */
//#define MOTOR_FLIP_A
#define MOTOR_FLIP_B

// General config
namespace Config
{
	// Serial
	const unsigned long SERIAL_SPEED = 9600L;	// 9.6Kb/s
	const unsigned long SERIAL_TIMEOUT = 10L;	// 10ms
}

#endif
