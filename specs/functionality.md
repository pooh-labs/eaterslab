# **MyCanteen** System

The MyCanteen system is intended for customers and canteen owners. Before going out for dinner, customers will be able to check the current canteen occupancy, the offered menu and rate the food served. The canteen owner can view the collected information, compare them with historical visit trends, as well as view feedback.

# Functionality

1. Camera input from micro computer RaspberryPi
    * Compute number of people entering and leaving the room [OpenCV]
    * Send number to the main server
    * Send video stream to the main server (optional)
    * Capture waiting line size and predict waiting time for users (optional)
2. Live stats
    * Show relative occupance
        * TODO: Exact presentation
2. History trends
    * Historical data
        * number of clients in selected time ranges
        * TODO: List what charts etc. to show
    * Excepted occupance in the next X minutes (optional)
3. Live menu
    * List current menu options (text version if available, then photos)
    * Option rating (visible for everyone)
4. Admin panel
    * Set canteen capacity
    * Admin upload menu photos
    * Text extraction from images (optional)
