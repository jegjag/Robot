#ifndef ROBOT_MOTORS
#define ROBOT_MOTORS

#include <Arduino.h>
#include "command.hpp"

namespace Motors
{
	class Motor
	{
	public:
		Motor
		(
			unsigned char speedPin,
			unsigned char directionPin,
			unsigned char brakePin
		)
		{
			_speedPin = speedPin;
			_directionPin = directionPin;
			_brakePin = brakePin;
		}

		void init();
		void move(Protocol::Command cmd);
	private:
		unsigned char _speedPin;
		unsigned char _directionPin;
		unsigned char _brakePin;
	};

	const Motor MOTOR_A = Motor
	(
		3,	// Speed pin
		12,	// Direction pin
		9	// Brake pin
	);

	const Motor MOTOR_B = Motor
	(
		11,	// Speed pin
		13,	// Direction pin
		8	// Brake pin
	);
}

#endif
