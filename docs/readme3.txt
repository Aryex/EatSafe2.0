Supported languages:
   - English (Default)
   - French (Canada)

 Notes:
    - When testing on one of the devices, upon app installation,
      clicking the "cancel" button still results in updated data
      (it downloaded the data in the background even though
      "cancel is pressed").

      Note that it only happens on ONE device that we tested on,
      the other devices do NOT have this problem.

    - The app camera is locked by default upon installation
      (to unlock the camera, disable location permission)

    - Note that the user can only cancel an update within 1.5 seconds
      of the app updating, or else the data will be read to disk. The
      user then will not be able to cancel (since cancelling during
      a write operation result in corrupted data)

    - For searching restaurants with hazard levels of the most recent
      inspection, the default level is to display "All" hazard levels, in
      this case, "Low", "Medium (Moderate)", and "High". "All" does NOT
      mean that the inspection have all three hazard levels. Keep in mind
      that "All", as default, displays restaurants with low hazard,
      restaurants with medium hazard, and restaurants with high hazard
      (not restaurants with all three hazards combined).