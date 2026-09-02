@echo off
title BITHM National Bank ATM Simulator
echo Compiling ATM Simulator...
javac -d bin src\atm\*.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)
echo Starting ATM Application GUI...
start javaw -cp bin atm.Main
echo ATM GUI launched successfully!
