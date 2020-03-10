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
        * [3] Provide setup GUI
    * [1] Read video stream from integrated camera or USB webcam
    * Analyze video stream
        * [1] Compute number of people entering and leaving the room
        * [3] Log numbers to device
        * [4] Track actors inside canteen
    * Send camera state information to the server
        * [2] Monitoring starts
        * [2] Monitoring ends
        * Visitor count update (batched)
            * [1] People entering
            * [1] People leaving
            * [4] Send actor IDs if tracking
        * [4] Send video stream to the main server
2. Data ingestion endpoint
    * [2] Check for cameras losing connection
    * [3] Set up prediction pipeline
        * [3] Get expected number of people in the next X minutes
    * [1] Save numbers to database (aggregate by X seconds/minutes)
3. Admin panel
    * [3] Register canteen owner
        * Username
        * First Name
        * Last Name
        * Password
    * [3] Edit canteen owner
    * [3] Remove canteen owner
4. Owner panel
    * Login flow for owners
        * [1] Basic login-password authentication
        * [3] 2FA with OTP
        * [3] 2FA registration flow
    * Manage canteens
        * [1] List owned cafes
        * [1] Register canteen
            * [1] Name
            * [1] Capacity
            * [4] Logo
            * [3] Address
            * [4] Geo location from address
            * [4] Regular menu
            * [3] Opening hours
            * [4] Fixed menu with prices
        * [2] Update canteen
        * [1] Delete canteen
    * Manage menus
        * Create menu for day
            * [1] Add entries to the list
                * [3] Add price
                * [2] Tag as vegetarian
                * [3] Add different tags (vegan, halaal, locally sourced etc.)
                    * [4] Support user-created tags
            * Upload menu photo
                * [3] Recognize entry list from a photo
        * Browse menus
            * [1] Display options with ratings
                * Stars (integers in range [1..5])
                * Text reviews
                * [4] Photos of dishes
                * [3] Prices of dishes
            * [3] Display photo of menu board if uploaded and no text options
        * [2] Update menus
        * [1] Delete menus
        * [4] Post menu to Facebook
        * [4] Export menu as PDF
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
        * [4] Popularity of dishes based on canteens owners data
5. Client app
    * Browse canteens
        * View canteen
            * [2] Open/closed
            * [1] Live number of visitors (relatively to the full capacity)
            * [1] List menu options (see below)
            * [3] Create visitor count widget for mobile devices
            * [3] Display predicted occupance
            * [3] Has specified food - based on tags
            * [3] List historical menus (see below)
        * [2] Filter canteen list
            * [2] Open/closed
            * [2] Minimum rating
            * [3] Has tags
        * [3] Sort canteen list
            * [3] Rating
            * [4] Distance (GPS-based)
        * [3] Find nearby canteens based on used location
    * Browse menus
        * [1] Display menu options
        * [1] Rate option
        * [4] Get nutrition values
    * View most of the statistic data
        * Plot relative cafeteria occupancy
            * [1] Break by year/month/week/day/hour
            * [4] Correlate to specified menu options
        * [2] Plot different cafeterias occupancy comparison
            * Break by year/month/week/day/hour
        * [3] Plot menu options prices changes
    * [3] Provide English version
    * [4] Implement accessibility features
6. [4] Other prediction pipelines
    * Get suggestions for menu options based on ratings
    * Get expected line wait time
    * Get expected wait time for dishes based on other users feedback
