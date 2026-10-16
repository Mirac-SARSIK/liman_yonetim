@echo off
chcp 65001 > nul
cd /d "%~dp0"
java -jar LimanYonetimSistemi_MSTv3.jar
if %ERRORLEVEL% neq 0 (
    echo.
    echo HATA: Java yuklu degil veya bir sorun olustu.
    echo Lutfen Java 11 veya uzeri yukleyin:
    echo https://www.java.com/tr/download/
    pause
)