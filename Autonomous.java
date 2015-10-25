package com.qualcomm.ftcrobotcontroller.opmodes;

import android.graphics.Color;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.hardware.ColorSensor;

/**
 * Created by Jachzach on 10/21/2015.
 */
//TODO: register colorsensor in hardware, see why threw uncaught exception can't find hardware device "mr"
public class Autonomous extends Hardware
{
    public Autonomous()
    {
    }


    private int v_state = 1;

    private boolean isLeft;

    private ColorSensor sensorRGB;

  //  hardwareMap.logDevices();

    // hsvValues is an array that will hold the hue, saturation, and value information.
    float hsvValues[] = {0F,0F,0F};

    // values is a reference to the hsvValues array.
    final float values[] = hsvValues;

    private boolean teamColorBlue = true;

    ReadBeacon readBeacon1 = new ReadBeacon();
    MoveArm moveArm1 = new MoveArm();
    PressButton pressButton1 = new PressButton();

    @Override public void start ()
    {
        //TODO - have phone ask red or blue during init and save as variable
        //Hardware start method
        super.start();
        reset_drive_encoders();
        try
        {
            sensorRGB = hardwareMap.colorSensor.get ("mr");
        }
        catch (Exception p_exception)
        {
            m_warning_message ("color sensor");
            DbgLog.msg (p_exception.getLocalizedMessage ());

            sensorRGB = null;
        }
        // sensorRGB = hardwareMap.colorSensor.get("mr");
    } // start

    @Override public void loop ()

    {
        if (readBeacon1.action()){}
        else if (moveArm1.action()){}
        else if (pressButton1.action()){}

        //----------------------------------------------------------------------
        //
        // State: Initialize (i.e. state_0).
        //
        switch (v_state)
        {

            //
            // Drive forward until the encoders exceed the specified values.
            //
            case 1:
                //
                // Tell the system that motor encoders will be used.  This call MUST
                // be in this state and NOT the previous or the encoders will not
                // work.  It doesn't need to be in subsequent states.
                //
               // run_using_encoders ();
                v_state++;
                break;

            //
            // sense le color
            //
            case 2: //TODO - make variable isLeft and use to assign push_beacon
                if (hsvValues[0] > 10)
                {
                    if(teamColorBlue)
                    {
                        push_beacon(true);
                    }
                    else
                    {
                        push_beacon(false);
                    }
                } else
                {
                    if(teamColorBlue)
                    {
                        push_beacon(false);
                    }
                    else
                    {
                        push_beacon(true);
                    }
                }
                break;

            //
            // Perform no action - stay in this case until the OpMode is stopped.
            // This method will still be called regardless of the state machine.
            //
            default:
                //
                // The autonomous actions have been accomplished (i.e. the state has
                // transitioned into its final state.
                //
                break;
        }

        //
        // Send telemetry data to the driver station.
        //
        color();
        update_telemetry(); // Update common telemetry
        telemetry.addData("18", "State: " + v_state);

    } // loop


    void drive ()
    {
        run_using_encoders();
        set_drive_power(1.0f, 1.0f);
        if (have_drive_encoders_reached (2880, 2880))
        {
            reset_drive_encoders ();
            set_drive_power (0.0f, 0.0f);
            v_state++;
        }
    }

    void pause ()
    {
        if (have_drive_encoders_reset())
        {
            v_state++;
        }
    }

    public void color()
    {

        sensorRGB.enableLed(false);

        // convert the RGB values to HSV values.
        //Color.RGBToHSV((sensorRGB.red() * 8), (sensorRGB.green() * 8), (sensorRGB.blue() * 8), hsvValues);
        Color.RGBToHSV(sensorRGB.red() * 8, sensorRGB.green() * 8, sensorRGB.blue() * 8, hsvValues);

        // send the info back to driver station using telemetry function.
        telemetry.addData("Clear", sensorRGB.alpha());
        telemetry.addData("Red  ", sensorRGB.red());
        telemetry.addData("Green", sensorRGB.green());
        telemetry.addData("Blue ", sensorRGB.blue());
        telemetry.addData("Hue", hsvValues[0]);


    }

    private class ReadBeacon
    {
        int state;

        // -1 = has not been run
        // 0 = is running
        // 1 = is done
        ReadBeacon()
        {
            state = -1;

        }

        boolean action()
        {


            if (state == 1) {
                return false;
            }

            color();

            if (hsvValues[0] > 10)
            {
                if(teamColorBlue)
                {
                    isLeft = true;
                }
                else
                {
                    isLeft = false;
                }
            } else
            {
                if(teamColorBlue)
                {
                    isLeft = false;
                }
                else
                {
                    isLeft = true;
                }
            }

            state = 1;

            return true;

        }
    }
    private class MoveArm {
        int state;
        MoveArm(){
            state = -1;
        }
        boolean action(){
            if (state == 1) {
                return false;
            }
            return true;
        }
    }
    private class PressButton {
        int state;
        PressButton() {
            state = -1;
        }
        boolean action(){
            if (state == 1){
                return false;
            }
            return true;
        }
    }
    //read beacon
        //move arm
        //move forward to press
    }



