@echo off
title Build QuizMaster Pro
echo Cleaning bin directory...
if not exist bin mkdir bin
echo Compiling Java source files...
javac -d bin -sourcepath src src\com\quizmaster\Main.java
if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)
echo Packaging QuizMasterPro.jar...
jar cfe QuizMasterPro.jar com.quizmaster.Main -C bin .
echo Build complete: QuizMasterPro.jar created successfully.
pause
