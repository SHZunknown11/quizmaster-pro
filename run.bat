@echo off
title QuizMaster Pro
echo Starting QuizMaster Pro...
java -jar QuizMasterPro.jar
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Running with bin classpath fallback...
    java -cp bin com.quizmaster.Main
)
pause
