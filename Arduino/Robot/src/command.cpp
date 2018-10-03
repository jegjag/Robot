#include "command.hpp"

using namespace Protocol;

/*
 * Protocol Definition (Must be in order):
 * Motor: <byte/bool> 0: A, 1: B
 * Direction: <byte/bool> 0: Backwards, 1: Forwards
 * Speed: <unsigned byte/int> 0-255
 * '\n': End command
 */
Command Protocol::getCommand(String command)
{
	const char* cmd = command.c_str();
	Command c;

	#ifdef DEBUG_MESSAGES
	char buffer[50];	// 50 should be exactly enough to store the string.
	#endif

	switch(cmd[0])
	{
		case 0:
			c.motor = Motor::A;
			break;
		case 1:
			c.motor = Motor::B;
			break;
		default:
			#ifdef DEBUG_MESSAGES
			sprintf(buffer, "Invalid motor \'%d\' specified.\n", atoi(cmd[0]));
			Serial.print(buffer);
			#endif

			c.motor = Motor::A;	// Default to A to avoid exception
			break;
	}

	Direction direction;
	switch(cmd[1])
	{
		case 0:
			c.direction = Direction::BACKWARD;
			break;
		case 1:
			c.direction = Direction::FORWARD;
			break;
		default:
			#ifdef DEBUG_MESSAGES
			sprintf("Invalid direction \'%d\' specified.\n", atoi(cmd[1]));
			Serial.print(buffer);
			#endif

			c.direction = Direction::FORWARD;	// Default to forward to avoid exception
			break;
	}

	c.amount = atoi(cmd[2]);

	return c;
}
