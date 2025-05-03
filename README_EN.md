# SerialPortToolFX

- [**日本語(Copilotを使用して翻訳する)**](README_JP.md) | [**简体中文**](README.md)

## Overview

SerialPortToolFX is a cross-platform open-source serial port debugging tool built using JavaFX. It aims to provide a user-friendly interface to facilitate developers in debugging and managing serial port communications.

## Software Interface

### Light Theme

![light](img/light.png)

### Dark Theme

![daek](img/dark.png)

## Features

- **Real-time Serial Port List Updates**：Automatically detects and displays available serial port devices.
- **Data Format Support**：Supports sending and receiving serial data in ASCII and HEX formats.
- **Data Statistics**：Tracks the number of bytes sent and received, making analysis easier. (Click on the respective number to reset the count)
- **Data Persistence**：Supports saving serial communication data to files. (Must enable the corresponding option)
- **Multi-Window Support**：Allows multiple serial communication windows to be opened simultaneously.
- **Simulated Reply**：Sends pre-set data as a reply upon receiving specified data (requires proper configuration and loading of a JSON file).

## Dependencies

- [lombok](https://github.com/projectlombok/lombok)
- [javafx](https://github.com/openjdk/jfx)
- [atlantafx](https://github.com/mkpaz/atlantafx)
- [jSerialComm](https://github.com/Fazecast/jSerialComm)
- [gson](https://github.com/google/gson)
- [commons-codec](https://github.com/apache/commons-codec)
- [commons-text](https://github.com/apache/commons-text)

## JSON File for Simulated Reply

### Basic JSON Configuration Example:

```json
{
  "encode": "HEX",
  "packSize": "13",
  "delimiter": ""
}
```

### JSON Configuration Requirements and Details:

1. All keys and values in the JSON must be of string type.
2. JSON must have a single level; nested structures are not allowed.
3. The encode field in the JSON specifies the encoding format
    - Supported parameters (case-insensitive): **HEX  ASCII**
    - If encode is set to HEX, all key-value pairs will be decoded as hexadecimal.
    - If encode is set to ASCII, all key-value pairs will be decoded as ASCII.
4. The packSize and delimiter fields in the JSON are used to determine the completeness of serial messages:
    - packSize uses the length of the data packet to determine completeness.
    - delimiter uses specific symbols to determine completeness.
    - **If both packSize and delimiter are specified, delimiter will be ignored unless packSize is invalid (non-numeric).**
    - **Both packSize and delimiter cannot be empty.**
5. If a serial terminator is used as the criterion, it is recommended not to include the terminator in the content, as it may cause the simulated reply function to fail.

### Example JSON Configurations:

1. ASCII + Data Packet Size

   ```json
   {
     "encode": "ASCII",
     "packSize": "5",
     "delimiter": "",
     "abcde":"ABCDE",
      "12345":"12345"
   }
   ```

2. ASCII + Terminator

   ```json
   {
     "encode": "ASCII",
     "packSize": "",
     "delimiter": "\r\n",
     "A\r\n":"B\r\n",
      "AAAA\r\n":"BAAA\r\n"
   }
   ```

3. HEX + Data Packet Size

   ```json
   {
     "encode": "HEX",
     "packSize": "5",
     "delimiter": "",
     "F1 F2 F3 F4 F5":"01 02 03 04 05",
     "F0 F2 F3 F4 F5":"01 02 03 04 05"
   }
   ```

4. HHEX + Terminator

   ```json
   {
     "encode": "HEX",
     "packSize": "",
     "delimiter": "0D 0A",
     "01 02 0D 0A":"01 02 0D 0A",
     "01 03 0D 0A":"01 03 05 0D 0A"
   }
   ```
   
   ## Build and Package
   
   ### Environment Requirements
   
   - java22+
   - Gradle (compatible with the Java version)
   
   ### Commands
   
   ```powershell
   gradle clean
   gradle jpackageimage
   ```
   
   

