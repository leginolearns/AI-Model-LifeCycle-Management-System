@echo off
cd /d "%~dp0"
where python >nul 2>nul || (echo Python 3 is required. Install Python and enable Add Python to PATH. & pause & exit /b 1)
python -m pip install --upgrade pip
python -m pip install -r requirements.txt
if errorlevel 1 (echo Python ML dependencies could not be installed. & pause & exit /b 1)
echo Machine-learning dependencies installed.
pause
