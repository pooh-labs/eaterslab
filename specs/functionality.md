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
    * Get number of people in the room (relatively to the full capacity)
        * Now/by year/month/28 days/week/7 days/day/hour
    * Get expected number of people in the next X minutes (optional)
    * Get expected wait time in the line (optional)
    * Get expected wait time for some dishes based on other users feedback (optional)
3. Admin panel
    * 2FA with OTP (optional)
    * Manage canteens
        * Create canteen with properties (name, address, constant menu, opening hours)
        * Update canteen data
        * Delete canteen
    * Manage menus (**TODO**: needs work)
        * List current menu options (text version if available, then photos)
        * Option rating (visible for everyone)
        * Admin upload menu photos
        * Text extraction from images (optional)
        * View historical menu options
    * Manage cameras
        * Register new camera and generate authentication key
        * List assigned cameras with state (on/off/lost connection)
        * Update and remove camera
    * Browse historical data
        * Histogram with number of people
            * Break by year/month/28 days/week/7 days/day/hour
        * Popularity of concrete dishes based on canteens owners data (optional)
4. Android app and webclient (**TODO**)
    * View the current canteen status
    * Get the predictions about canteen occupance (optional)
    * Get predictions of periodical menu changes (optional)
    * View most of the statistics data
    * Review canteens and dishes
    * Get information about eaten food (calories and nutritional values) (optional)
    * Get access to administration panel (optional on android)
    
