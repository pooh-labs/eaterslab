# **MyCanteen** Functionality

1. Camera devices
    * Setup workflow
        * Register API authorization key on device
        * Enable/disable logging
        * Provide setup GUI (optional)
    * Read video stream from integrated camera or USB webcam
    * Analyze video stream
        * Compute number of people entering and leaving the room
        * Log numbers to device
        * Track actors inside canteen (optional)
    * Send camera state information to the server
        * Monitoring starts
        * Monitoring ends
        * Visitor count update (batched)
            * People entering
            * People leaving
            * Send actor IDs if tracking (optional)
        * Send video stream to the main server (optional)
2. Data ingestion endpoint
    * Check for cameras losing connection
    * Set up prediction pipeline (optional)
        * Get expected number of people in the next X minutes
    * Save numbers to database (aggregate by X seconds/minutes)
3. Admin panel **TODO**
4. Owner panel
    * Login flow for owners
        * 2FA with OTP (optional)
        * 2FA registration flow (optional)
    * Manage canteens
        * List owned cafes
        * Register canteen
            * Name
            * Capacity
            * Logo (optional)
            * Address (optional)
            * Regular menu (optional)
            * Opening hours (optional)
            * Fixed menu with prices (optional)
        * Update canteen
        * Delete canteen
    * Manage menus
        * Create menu for day
            * Add entries to the list
                * Tag as vegetarian
                * Add different tags (vegan, halaal, locally sourced etc.) (optional)
                    * Support user-created tags
            * Recognize entry list from a photo (optional)
                * Upload photo
                * Send photo to external service for analysis
        * Browse menus
            * Display options with ratings
                * Stars (integers in range [1..5])
                * Text reviews (optional)
                * Photos of dishes (optional)
                * Prices of dishes (optional)
            * Display photo of menu board if uploaded and no text options (optional)
        * Update menus
        * Delete menus
        * Post menu to Facebook (optional)
        * Export menu as PDF (optional)
    * Manage cameras
        * Register new camera and generate authentication key
        * List cameras
            * Show state (on/off/lost connection)
        * Update camera
            * Reset auth key
        * Unregister camera
    * Browse analytics
        * Plot cafeteria occupancy
            * Break by year/month/week/day/hour
        * Plot review count
            * Break by year/month/week/day/hour
        * Plot different charts together for correlation analysis
        * Popularity of dishes based on canteens owners data (optional)
5. Client app
    * Browse canteens
        * View canteen
            * Open/closed
            * Live number of visitors (relatively to the full capacity)
                * Create widget for mobile devices (optional)
            * Display predicted occupance (optional)
            * Has specified food - based on tags (optional)
            * List menu options (see below)
            * List historical menus (see below)
        * Filter canteen list
            * Open/closed
            * Minimum rating
            * Has tags (optional)
        * Find nearby canteens based on used location (optional on Android)
    * Browse menus
        * Display menu options
        * Rate option
        * Get nutrition values (optional)
    * View most of the statistics data
        * Plot cafeteria occupancy
            * Break by year/month/week/day/hour
            * Correlated to specified menu options (optional)
        * Plot menu options prices changes (optional)
        * Plot different cafeterias occupancy comparision
            * Break by year/month/week/day/hour
    * Provide English version (optional)
    * Implement accessibility features (optional)
6. Other prediction pipelines (optional)
    * Get suggestions for menu options based on ratings
    * Get expected line wait time
    * Get expected wait time for dishes based on other users feedback
