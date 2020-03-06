# **MyCanteen** System

The MyCanteen system is intended for customers and canteen owners. Before going out for dinner, customers will be able to check the current canteen occupancy, the offered menu and rate the food served. The canteen owner can view the collected information, compare them with historical visit trends, as well as view feedback.

# Functionality

1. Camera input from Raspberry Pi
    * Read video stream from camera or USB webcam
    * Compute number of people entering and leaving the room [OpenCV]
    * Send camera state information to the server
        * Monitoring starts
        * Monitoring ends
        * Person entering
        * Person leaving
    * Send video stream to the main server (optional)
2. Occupance statistics on the server
    * Get number of people in the room (relatively to the max capacity)
        * Now/by year/month/28 days/week/7 days/day/hour
    * Get expected number of people in the next X minutes (optional)
    * Get expected wait time in the line (optional)
3. Admin panel
    * 2FA with OTP (optional)
    * Manage canteens
        * Create canteen with name
        * Update canteen
        * Delete canteen
    * Manage menus (**TODO**: needs work)
        * List current menu options (text version if available, then photos)
        * Option rating (visible for everyone)
        * Admin upload menu photos
        * Text extraction from images (optional)
    * Manage cameras
        * Register new camera and generate authentication key
        * List assigned cameras with state (on/off/lost connection)
        * Update and remove camera
    * Browse historical data and predictions
        * Histogram with number of people
            * Break by year/month/28 days/week/7 days/day/hour
        * **TODO** Include predictions
4. Android app (**TODO**)