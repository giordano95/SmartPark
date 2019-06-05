# SmartPark Mbed #

A smart parking device based on MBed OS, and connected to a Firebase database to help users check free parking spots and reserve a spot for a specific time

### Hardware used ###

A board with an external WiFi shield.
    [NUCLEO-F401RE](https://os.mbed.com/platforms/ST-Nucleo-F401RE/) with [X-NUCLEO-IDW04A1](http://www.st.com/content/st_com/en/products/ecosystems/stm32-open-development-environment/stm32-nucleo-expansion-boards/stm32-ode-connect-hw/x-nucleo-idw04a1.html) Wi-Fi expansion board using pins D8 and D2 _(of the Arduino connector)_.

## Getting started with the Wi-Fi API ##

#### Adding connectivity driver

You need to add the driver to your application and configure it to provide default WiFi interface.

```
mbed add wifi-x-nucleo-idw01m1`
```

Then pin names need to be configured as instructed in the drivers README file.

##  Getting started ##

1. Import the repo files.


2. Configure the Wi-Fi shield and settings.
   Edit ```mbed_app.json``` to include the correct Wi-Fi shield, SSID and password:

```json
{
    "config": {
        "wifi-ssid": {
            "help": "WiFi SSID",
            "value": "\"SSID\""
        },
        "wifi-password": {
            "help": "WiFi Password",
            "value": "\"PASSWORD\""
        }
    },
  ...
}
```


3. Compile and generate binary.
    For example, for `GCC`:
    ```
    mbed compile -t GCC_ARM
    ```

4. Open a serial console session with the target platform using the following parameters:
    * **Baud rate:** 9600
    * **Data bits:** 8
    * **Stop bits:** 1
    * **Parity:** None

5. Copy or drag the application `mbed-os-example-wifi.bin` in the folder `~/BUILD/<TARGET NAME>/<PLATFORM NAME>` onto the target board.

1. The serial console should display a similar output to below, indicating a successful Wi-Fi connection:
    ```
    MBed Os Version x.XX.x

    Connecting to {SSID}...
    Success

   ...
    ```

## Troubleshooting

If you have problems, you can review the [documentation](https://os.mbed.com/docs/latest/tutorials/debugging.html) for suggestions on what could be wrong and how to fix it.

### License and contributions

The software is provided under Apache-2.0 license. Contributions to this project are accepted under the same license. Please see contributing.md for more info.

This project contains code from other projects. The original license text is included in those source files. They must comply with our license guide.
