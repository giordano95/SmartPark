# SmartPark

## Modalities
Our application is ready to face three different types of scenarios, always trying to best assist the user overtime.
* The first way to use our application is also the simplest one. It gives to the user the possibility to **check** the parking lots status and to immediately distinguish between free and occupied ones. Once a free slot has been found, the user just has to reach the lot and park his car.

* The second way consists of giving to the user the possibility to reserve his lot up to one hour before his arrival. The only information asked to the user are his car plate number and the desired arrival time. This makes the user to get access to the **reservation** mode of the application, presented as a recap of his reservation, with also some payment details, with a very intuitive window. 

* Finally, the third way consists in the managing of the last steps done by the user before the payment. This scenario is accessible just to the user having a reservation. The user can **alert** other users that he is going to leave his parking lot in a few moments, giving them the possibility to approach the lot before it will be freed.
To involve the user in exploiting as much as possible this option, bonus points are given to him each time he alerts the others. 

Checks are done to ensure the actual utility of the alert by controlling how much time passes between the moment in which the user has alerted the others and the moment in which he actually pays the parking. If too much time passes between these two moments, no bonus points are given. Bonus points can be used to get shop or parking discounts.

## Specs
* **Android SDK 21**
	* We can install our application on about 97% of the Android devices in the world. This represents a perfect balance between the target number of devices and the quality of the used Java libraries which are compatible with the newest Android versions.
	
*	**Light**
	*  Application size is about 6 Megabytes. This is obviously a great feature for all those devices which have not extensible memories. 

* **Android Native**
	* We coded it in the Android Native programming language and we developed seven different activities.
