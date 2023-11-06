#!/usr/bin/env bash

DELAY=0.5
adb shell locksettings set-pin 1111
adb shell am start -a android.settings.SECURITY_SETTINGS
sleep $DELAY
adb shell input tap 274 1600
adb shell input text 1111
adb shell input keyevent 66
sleep $DELAY
adb shell input tap 1100 2200
sleep $DELAY
adb -e emu finger touch 1
sleep $DELAY
adb -e emu finger touch 1
sleep $DELAY
adb -e emu finger touch 1
sleep $DELAY
adb shell input keyevent 4
sleep $DELAY
adb shell input keyevent 4
sleep $DELAY
adb shell input keyevent 4
sleep $DELAY
adb shell input keyevent 4
sleep $DELAY
adb shell input keyevent 4
sleep $DELAY
adb shell input keyevent 4
