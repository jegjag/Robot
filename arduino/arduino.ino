#include "motor.h"
#include "direction.h"

#define FLIP_A false
#define FLIP_B true

#define DATA_RATE 9600
#define TIMEOUT 1000
#define BUFFER_SIZE 32

void setup()
{
	// Serial setup
	Serial.begin(DATA_RATE);
	Serial.setTimeout(TIMEOUT);

	pinMode(A_DIR, OUTPUT);  // Initialise Motor A
	pinMode(B_DIR, OUTPUT);  // Initialise Motor B

	pinMode(A_BRAKE, OUTPUT);   // Initialise Motor A Brake
	pinMode(B_BRAKE, OUTPUT);   // Initialise Motor B Brake
}

inline int readLine(char* buf, int bufferSize, String &ret)
{
	if(Serial.available() <= 0)
	{
		return -1;
	}

	int n = Serial.readBytesUntil('\n', buf, bufferSize - 1);
	if(n >= 0)
	{
		buf[n] = '\0';
		ret = buf;
	}

	return n;
}

String line;

inline void brake(Motor motor)
{
	// Brake
	if(motor == A)
	{
		digitalWrite(A_BRAKE, HIGH);
	}
	else if(motor == B)
	{
		digitalWrite(B_BRAKE, HIGH);
	}
}

inline void setMotor(Motor m, Direction d, int spd)
{
	if(m == A)
	{
		digitalWrite(A_BRAKE, LOW);
		if(!FLIP_A)
		{
			if(d == FORWARD)
			{
				digitalWrite(A_DIR, HIGH);
			}
			else if(d == BACKWARD)
			{
				digitalWrite(A_DIR, LOW);
			}
		}
		else
		{
			if(d == FORWARD)
			{
				digitalWrite(A_DIR, LOW);
			}
			else if(d == BACKWARD)
			{
				digitalWrite(A_DIR, HIGH);
			}
		}
		analogWrite(A_ANALOG, spd);
	}
	else if(m == B)
	{
		digitalWrite(B_BRAKE, LOW);
		if(!FLIP_B)
		{
			if(d == FORWARD)
			{
				digitalWrite(B_DIR, HIGH);
			}
			else if(d == BACKWARD)
			{
				digitalWrite(B_DIR, LOW);
			}
		}
		else
		{
			if(d == FORWARD)
			{
				digitalWrite(B_DIR, LOW);
			}
			if(d == BACKWARD)
			{
				digitalWrite(B_DIR, HIGH);
			}
		}
		analogWrite(B_ANALOG, spd);
	}
}

char b[BUFFER_SIZE];

void loop()
{
	int n = readLine(b, sizeof(b), line);

	if(n <= 0)
	{
		return;
	}

	// Process line
	// Motor
	Motor motor;
	if(line.startsWith("A"))
	{
		motor = A;
		line.replace("A", "");
	}
	else if(line.startsWith("B"))
	{
		motor = B;
		line.replace("B", "");
	}
	else
	{
		return;
	}

	// Direction (Up/Down to not conflict B for backwards)
	Direction d;
	if(line.startsWith("U"))
	{
		d = FORWARD;
		line.replace("U", "");
	}
	else if(line.startsWith("D"))
	{
		d = BACKWARD;
		line.replace("D", "");
	}
	else if(line.startsWith("S"))
	{
		brake(motor);
		return;
	}
	else
	{
		return;
	}

	// Speed
	int spd = line.toInt();
	if(spd <= 0)
	{
		brake(motor);
		return;
	}
	if(spd > 255)
	{
		spd = 255;
	}

	setMotor(motor, d, spd);
}
