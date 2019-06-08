# SmartPark

This is the repository for SmartPark project deeply described [here](https://www.hackster.io/Marco_Ferraro/smart-park-3d3c49) on Hackster.

## Team
We are three Engineers in Computer Science [Marco Ferraro](https://www.linkedin.com/in/marco-ferraro-86aa53182/), [Davide Giordano](https://www.linkedin.com/in/davide-giordano/) and [Riccardo Pattuglia](https://www.linkedin.com/in/riccardo-pattuglia-3a09ab182/)

## Overview
The idea came out since every day seems more difficult to find parking spots especially inside in shopping centre parkings, and during festivities, when the malls are crowded, and you, after turning around a few times, decide to go back home.

So the main purpose of our project is to provide a map of the parking updated in real time so that you can go straight to the free spot avoiding that someone who comes after you takes a spot you didn't noticed. We implemented a notification system so that you can know if someone is going to leave his place whitin five minutes.
Moreover is possible to reserve a spot so that you can go straight to your reserved spot when you arrive.

## What we have used
 - [Nucleo-F401RE](https://os.mbed.com/platforms/ST-Nucleo-F401RE/)
 - [X-Nucleo-IDW01M1](https://www.st.com/content/st_com/en/products/ecosystems/stm32-open-development-environment/stm32-nucleo-expansion-boards/stm32-ode-connect-hw/x-nucleo-idw01m1.html)
 - [Ultrasonic Sensor HC-SR04](https://cdn.sparkfun.com/datasheets/Sensors/Proximity/HCSR04.pdf)
 - [Android Studio](https://developer.android.com/studio/)
 - [Firebase](https://firebase.google.com)
 
 ## Architecture ##
 
For every parking slot there's and ultrasonic sensor and three LEDs to signal if the spot is either free, occupied or reserved (green, red and yellow). Everything is connected and handeled by the Nucleo board, which sends an HTTPS request to Firebase throught the Wi-Fi module whenever the sensors' logic determines a state change. The Firebase database changes state for the corresponding parking slots and the Android application reads from it to draw the state map.

Whenever an user makes a reservation, data is sent to Firebase to modify the state of the reserved slot and the board recieves the new state, switching on the yellow LED accordingly.

<img src="imgs/Arch.PNG">
 
 ## How it works
The project is divided in two parts: 
- the configuration and building of the IoT device able to check the presence of a car and update the status of the server.
- the Android application to see the parking map and to relies on the ultrasonic sensors, mounted on the ceiling above every parking spot, to check the presence of a car on each spot.
Through the usage of the Wi-FI module, the board sends HTTPS requests to Firebase when the sensor notices a change of the state, the distance detected has changed.

In the Firebase Real-Time Database, every sensor updates the state of the spot and all the Android devices are notified of the change.
The application draws the parking lot map indicating the spots state with different colors.
It is also possible to reserve a spot within the Android application, inserting the car plate and estimated time of arrival.
Whenever you are leaving, you can also notify your departure, so that everybody can be aware of it.
To encourage the users to notify their departure there is a bonus points system to get discounts.


## How to use
In each folder has been included a detailed guide on how to use our code to reproduce each part of the project, and you can also refer to the [blog post](https://www.hackster.io/Marco_Ferraro/smart-park-3d3c49) where you can find more detailed instructions. 

####
## STM board running ##
<img src="https://i.postimg.cc/8c5X1mD8/photo-2019-06-07-19-35-44.jpg" width="280" />

## Android Application
![alt text](https://hackster.imgix.net/uploads/attachments/917811/image_GlNTAMlghy.png)

![alt text](https://hackster.imgix.net/uploads/attachments/917814/image_o9IqSfd1NE.png)
