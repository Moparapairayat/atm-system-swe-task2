@echo off
title BITHM National Bank ATM Simulator
echo Starting BITHM National Bank ATM Simulator...
if exist "ATM_Simulator.jar" (
    start javaw -jar ATM_Simulator.jar
) else (
    javac -d bin src\atm\*.java
    start javaw -cp bin atm.Main
)
echo ATM GUI launched successfully!
