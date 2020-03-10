# **MyCanteen** Functionality

For each point prepended by priority:

1. Essential
2. Required, but not essential
3. Optional with higher priority
4. Optional

## Functional requirements

1. Camera devices
    * Setup workflow
        * [1] Register API authorization key on device
        * [2] Enable/disable logging to device
        * [3] Provide setup GUI (optional)
    * [1] Read video stream from integrated camera or USB webcam
    * Analyze video stream
        * [1] Compute number of people entering and leaving the room
        * [3] Log numbers to device
        * [4] Track actors inside canteen (optional)
    * Send camera state information to the server
        * [2] Monitoring starts
        * [2] Monitoring ends
        * Visitor count update (batched)
            * [1] People entering
            * [1] People leaving
            * [4] Send actor IDs if tracking (optional)
        * [4] Send video stream to the main server (optional)
2. Data ingestion endpoint
    * [2] Check for cameras losing connection
    * [3] Set up prediction pipeline (optional)
        * [3] Get expected number of people in the next X minutes
    * [1] Save numbers to database (aggregate by X seconds/minutes)
3. Admin panel
    * [3] Register canteen owner (optional)
        * Username
        * First Name
        * Last Name
        * Password
    * [3] Edit canteen owner (optional)
    * [3] Remove canteen owner (optional)
4. Owner panel
    * Login flow for owners
        * [1] Basic login-password authentication
        * [3] 2FA with OTP (optional)
        * [3] 2FA registration flow (optional)
    * Manage canteens
        * [1] List owned cafes
        * [1] Register canteen
            * [1] Name
            * [1] Capacity
            * [4] Logo (optional)
            * [3] Address (optional)
            * [4] Geo location from address (optional)
            * [4] Regular menu (optional)
            * [3] Opening hours (optional)
            * [4] Fixed menu with prices (optional)
        * [2] Update canteen
        * [1] Delete canteen
    * Manage menus
        * Create menu for day
            * [1] Add entries to the list
                * [3] Add price
                * [2] Tag as vegetarian
                * [3] Add different tags (vegan, halaal, locally sourced etc.) (optional)
                    * [4] Support user-created tags
            * Upload menu photo
                * [3] Recognize entry list from a photo (optional)
        * Browse menus
            * [1] Display options with ratings
                * Stars (integers in range [1..5])
                * Text reviews (optional)
                * [4] Photos of dishes (optional)
                * [3] Prices of dishes (optional)
            * [3] Display photo of menu board if uploaded and no text options (optional)
        * [2] Update menus
        * [1] Delete menus
        * [4] Post menu to Facebook (optional)
        * [4] Export menu as PDF (optional)
    * Manage cameras
        * [1] Register new camera and generate authentication key
        * [2] List cameras
            * Show state (on/off/lost connection)
        * [2] Update camera
            * Reset auth key
        * [1] Unregister camera
    * Browse analytics
        * [1] Plot cafeteria occupancy
            * Break by year/month/week/day/hour
        * [1] Plot review count
            * Break by year/month/week/day/hour
        * [2] Plot different charts together for correlation analysis
        * [4] Popularity of dishes based on canteens owners data (optional)
5. Client app
    * Browse canteens
        * View canteen
            * [2] Open/closed
            * [1] Live number of visitors (relatively to the full capacity)
            * [1] List menu options (see below)
            * [3] Create visitor count widget for mobile devices (optional)
            * [3] Display predicted occupance (optional)
            * [3] Has specified food - based on tags (optional)
            * [3] List historical menus (see below)
        * [2] Filter canteen list
            * [2] Open/closed
            * [2] Minimum rating
            * [3] Has tags (optional)
        * [3] Sort canteen list
            * [3] Rating
            * [4] Distance (GPS-based)
        * [3] Find nearby canteens based on used location (Android, optional)
    * Browse menus
        * [1] Display menu options
        * [1] Rate option
        * [4] Get nutrition values (optional)
    * View most of the statistic data
        * Plot relative cafeteria occupancy
            * [1] Break by year/month/week/day/hour
            * [4] Correlate to specified menu options (optional)
        * [2] Plot different cafeterias occupancy comparison
            * Break by year/month/week/day/hour
        * [3] Plot menu options prices changes (optional)
    * [3] Provide English version (optional)
    * [4] Implement accessibility features (optional)
6. [4] Other prediction pipelines (optional)
    * Get suggestions for menu options based on ratings
    * Get expected line wait time
    * Get expected wait time for dishes based on other users feedback
