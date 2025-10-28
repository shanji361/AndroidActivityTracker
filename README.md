# Android Activity Tracker

This is an Android app that monitors and displays **lifecycle events** (e.g., `onCreate`, `onStart`, `onResume`, etc.) in real time. It visually highlights the current lifecycle state and maintains a scrolling log of all transitions using **Jetpack Compose** and **ViewModel**.

---

## Features

- **Real-time Lifecycle Tracking:** Logs every major Android lifecycle event as it happens.  
- **ViewModel Persistence:** Keeps event logs in memory across configuration changes (like screen rotation).  
- **Timestamps:** Each event includes the exact time it occurred.  
- **Status Colors:** Color-coded entries for quick recognition of lifecycle states.  
- **Live Log Display:** Smooth scrolling log built with `LazyColumn`.  
- **Snackbar Notifications:** Optional toast-like notifications on lifecycle transitions (configurable in settings).  

---
## How to Run the App

1. Clone this repository:
   ```bash
   git clone https://github.com/shanji361/AndroidActivityTracker.git
   ```
2. Open the project in Android Studio.

3. Run the app on an emulator or a physical Android device.   
---
