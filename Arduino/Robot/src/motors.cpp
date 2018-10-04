#include "motors.hpp"

using namespace Protocol;

void Motors::Motor::init()
{
	pinMode(_directionPin, OUTPUT);
	pinMode(_brakePin, OUTPUT);
}

void Motors::Motor::move(Command cmd)
{
	// If should brake
	analogWrite(_speedPin, cmd.amount);
	if(cmd.amount == 0)				{ digitalWrite(_brakePin, HIGH); return; }
	else							{ digitalWrite(_brakePin, LOW); }

	// Set direction
	switch(cmd.direction)
	{
		case Direction::FORWARD:	digitalWrite(_directionPin, HIGH);	break;
		case Direction::BACKWARD:	digitalWrite(_directionPin, LOW);	break;
	}
}
