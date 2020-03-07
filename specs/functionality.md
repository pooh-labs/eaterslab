# **MyCanteen** System Functionalities

1. Camera input from Raspberry Pi
    * Read video stream from camera or USB webcam
    * Compute number of people entering and leaving the room
    * Send camera state information to the server
        * Monitoring starts
        * Monitoring ends
        * Person entering
        * Person leaving
    * Anonymousely track people positions in canteen  (optional)
    * Send video stream to the main server (optional)
    * Give access to simple setup graphical interface (optional)  
2. Admin panel
    * Manage canteens
        * Create canteen with properties (name, address, constant menu, opening hours)
        * Update canteen data
        * Delete canteen
    * Manage menus
        * List current menu options (text version if available, then photos)
        * Option rating (visible for everyone)
        * Admin upload menu photos
        * Text extraction from images (optional)
        * View historical menu options
        * Get suggestions for future menu creation based on canteens owners data (optional)
    * Manage cameras
        * Register new camera and generate authentication key
        * List assigned cameras with state (on/off/lost connection)
        * Update and remove camera
    * Browse historical data
        * Plot histogram with number of people
            * Break by year/month/week/day/hour
        * Popularity of concrete dishes based on canteens owners data (optional)
    * 2FA with OTP (optional)
3. Client App [Adroid and Web]
    * View the current canteen status
        * closed/open
        * has specified food - based on canteens owners data (optional)
    * Get the predictions of canteen occupance (optional)
        * Get number of people in the room (relatively to the full capacity)
            * Now/in last year/month/week/day/hour
        * Get expected number of people in the next minutes (optional)
        * Get expected wait time in the line (optional)
        * Get expected wait time for some dishes based on other users feedback (optional)
    * Get predictions of periodical menu changes (optional)
    * View most of the statistics data
    * Review canteens and dishes
    * Get information about eaten food (calories and nutritional values) (optional)
    * Do review of eaten food and visited canteens
