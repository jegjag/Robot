#include <Arduino.h>
#include "config.hpp"
#include "command.hpp"
#include "motors.hpp"

using namespace Protocol;

void setup()
{
	Serial.begin(Config::SERIAL_SPEED);
	Serial.setTimeout(Config::SERIAL_TIMEOUT);

	Motors::MOTOR_A.init();
	Motors::MOTOR_B.init();
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
	if(atoi(str[0]) == -1)	return;
	Command cmd = Protocol::getCommand(str);

	// Flip if required
	#ifdef MOTOR_FLIP_A
	if(cmd.motor == Motor::A)	flipDirection(&cmd);
	#endif

	#ifdef MOTOR_FLIP_B
	if(cmd.motor == Motor::B)	flipDirection(&cmd);
	#endif

	switch(cmd.motor)
	{
		case Motor::A:	Motors::MOTOR_A.move(cmd); break;
		case Motor::B:	Motors::MOTOR_B.move(cmd); break;
	}
}
