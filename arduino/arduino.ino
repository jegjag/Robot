#define DATA_RATE 9600
#define TIMEOUT 1000

// Motor A pins
#define A_BRAKE 9
#define A_DIR 12
#define A_ANALOG 3

// Motor B pins
#define B_BRAKE 8
#define B_DIR 13
#define B_ANALOG 11

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

int readLine(char* buf, int bufferSize, String &ret)
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

typedef enum
{
    A, B
} Motor;

typedef enum
{
    FORWARD, BACKWARD
} Direction;

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
        
        if(d == FORWARD)
        {
            digitalWrite(A_DIR, HIGH);
        }
        else if(d == BACKWARD)
        {
            digitalWrite(A_DIR, LOW);
        }
        
        analogWrite(A_ANALOG, spd);
    }
    else if(m == B)
    {
        digitalWrite(B_BRAKE, LOW);
        
        if(d == FORWARD)
        {
            digitalWrite(B_DIR, HIGH);
        }
        else if(d == BACKWARD)
        {
            digitalWrite(B_DIR, LOW);
        }
        
        analogWrite(B_ANALOG, spd);
    }
}

void loop()
{
    int n = readLine(buffer, sizeof(buffer), line);
    
    if(n <= 0)
    {
        return;
    }
    
    // Process line
    // Motor
    Motor motor;
    String line_noMotor;
    if(line.startsWith("A"))
    {
        motor = A;
        line_noMotor = line.replace("A", "");
    }
    else if(line.startsWith("B"))
    {
        motor = B;
        line_noMotor = line.replace("B", "");
    }
    else
    {
        return;
    }
    
    // Direction (Up/Down to not conflict B for backwards)
    Direction d;
    String line_noDir;
    if(line.startsWith("U"))
    {
        d = FORWARD;
        line_noDir = line.replace("U", "");
    }
    else if(line.startsWith("D"))
    {
        d = BACKWARD;
        line_noDir = line.replace("D", "");
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
    int spd = line_noDir.toInt();
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
