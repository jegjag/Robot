#include <Arduino.h>
#include "config.h"
#include "command.hpp"

using namespace Protocol;

void setup()
{
	Serial.begin(SERIAL_SPEED);
	Serial.setTimeout(SERIAL_TIMEOUT);
}

#if defined(MOTOR_FLIP_A) || defined(MOTOR_FLIP_B)
inline void flipDirection(Command* cmd)
{
	switch(cmd->direction)
	{
		case Direction::FORWARD:	cmd->direction = Direction::BACKWARD;
		case Direction::BACKWARD:	cmd->direction = Direction::FORWARD;
	}
}
#endif

void loop()
{
	// Get command
	String str = Serial.readString();
	if(str != -1)
	{
		Command cmd = getCommand(str);

		// Flip if required
		#ifdef MOTOR_FLIP_A
		if(cmd.motor == Motor::A)	{ flipDirection(&cmd); }
		#endif

		#ifdef MOTOR_FLIP_B
		if(cmd.motor == Motor::B)	{ flipDirection(&cmd); }
		#endif
	}
}
