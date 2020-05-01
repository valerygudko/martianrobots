# Martian Robots Service

The surface of Mars can be modelled by a rectangular grid around which robots are able to move according to instructions provided from Earth. The Martian Robots Service is a program that determines each sequence of robot positions and reports the final position of the robot.                                                                     

## Get up and running

1. Clone the project locally

2. Ensure that you have:
   * java installed on your system (1.8)
   * gradle (4.1)

3. Run application using the following:

``` ./gradlew clean bootRun ``` or start the MartianRobotsApplication main class from IDE.

Application does accept the input on ContextRefreshedEvent (and so on application start), so just input grid and robot parameters to the console, as per the following example. 
Application also accepts consecutive sets of input commands after processing output, so stop it when you are done.

## INPUT/OUTPUT:

* Sample input

```
    5 3

    1 1 E
    RFRFRFRF

    3 2 N
    FRRFLLFFRRFLL

    0 3 W
    LLFFFLFLFL


```
Note: 2 consecutive empty lines will trigger the processing of instructions set and output of the list of final positions.

* Sample output

```
    1 1 E

    3 3 N LOST

    2 3 S
```

Note: application is built considering by scent a combination of coordinates and orientation which caused the previous robot to get "off" the edge. 

* Errors:

If your input didn't pass the validation you'll get the error message followed by signal "TRY INPUT AGAIN",
which mean you can try input again, starting from grid coordinates.

Examples of errors:
``` No instructions found ```

```Start position can't be outside of the grid```

```Too long list of instructions```
      
```Parameter is out of range:[0,50]```