# Andruino Bluetooth

Android App that allows the user to connect to an Arduino board using Bluetooth.

## App Link

https://play.google.com/store/apps/details?id=ar.com.lrusso.andruinobluetooth

## Schematic

![alt Schematic](https://raw.githubusercontent.com/lrusso/AndruinoBluetooth/master/Andruino%20Bluetooth/app/src/main/res/drawable-ldpi/schematic1.png)

## Sketch

```
#include <SoftwareSerial.h>

SoftwareSerial bluetooth(10,11);

void setup()
      {
      bluetooth.begin(9600);
      }

void loop()
      {
      int incomingByte = 0;
      String content = "";
      char character;

      while(bluetooth.available())
            {
            character = bluetooth.read();
            content.concat(character);
            }

      if (content!="")
            {
            bluetooth.print(content);
            }
      delay(100);
      }
```
