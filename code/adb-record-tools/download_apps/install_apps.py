import os
import subprocess

SUB_FOLDER = "apks"

def install_app(package_name):
    print("Attempt to install " + package_name)
    subprocess.run(['adb', 'install', package_name])
    print("\n")

def main():
    for file in os.listdir(SUB_FOLDER):
        if file.endswith(".apk"):
            install_app(os.path.join(SUB_FOLDER, file))

main()
