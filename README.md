# ProcHarvester

> This is the Proof-of-Concept implementation of ProcHarvester, a tool published at ASIACCS 2018.
> See the "[ProcHarvester](https://rspreitzer.github.io/publications/proc/asiaccs-2018-paper-2.pdf)" paper by Spreitzer, Kirchengast, Gruss, and Mangard for more details.

This repository contains a tool to analyze the procfs on Android-based devices for possible information leaks. ProcHarvester relies on the concept of template attacks. Thus, it works without prior knowledge about possible information leaks. 
We used ProcHarvester to analyze information leaks that allow inferring the following events from procfs resources:
* App starts
* Website launches
* Keyboard gestures

## One note before starting
**Warning:** This code is provided as-is. You are responsible for protecting yourself, your property and data, and others from any risks caused by this code. This code may not detect vulnerabilities in your application/OS or device. This code is only for testing purposes. Use it only on test systems which contain no sensitive data.

