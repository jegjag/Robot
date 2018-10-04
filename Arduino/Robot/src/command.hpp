#ifndef ROBOT_COMMAND
#define ROBOT_COMMAND

#include <Arduino.h>

/*
 * Saves precious bytes of memory when not defined.
 * Best to leave defined unless you really need a tiny bit of extra memory.
 */
#define DEBUG_MESSAGES

namespace Protocol
{
	// Protocol Enums
	enum class Motor		: char { A = 0, B = 1 };
	enum class Direction	: char { FORWARD = 1, BACKWARD = 0 };

	struct Command
	{
		Motor		motor;
		Direction	direction;
		int			amount;
	};

	Command getCommand(String command);
}

#endif
