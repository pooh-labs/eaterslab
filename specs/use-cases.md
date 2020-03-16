# Use cases

There are three user roles in the system:
* Admin: Manages canteen owners
* Canteen owner: Third-party business owner, manages canteens and owns devices
* No role (unregistered user): Browses public data

## Admin Panel related

### • Log in to the panel

Pre-condition: Admin is not logged in and enters any admin panel website.

Post-condition: Admin is logged in.

#### Basic path:

1. Website displays login form with "Username", "Password" fields and submit button.
2. User enters credentials in the form and clicks the submit button.
3. If 2FA is disabled, go to (5). Otherwise, website shows OTP form with single input and submit button.
4. User enters OTP code and clicks the submit button.
5. Website shows default admin panel website.

#### Exception paths:

* If username-password pair or OTP code are incorrect, flow goes back to (1). Website will show error message above the form. 

### • Register new canteen owner

### • Delete canteen owner

## Canteen owner Panel related

### • Log in to the panel

Pre-condition: Canteen owner is not logged in and enters any canteen owner panel website.

Post-condition: Canteen owner is logged in.

#### Basic path:

1. Website displays login form with "Username", "Password" fields and submit button.
2. User enters credentials in the form and clicks the submit button.
3. If 2FA is disabled, go to (5). Otherwise, website shows OTP form with single input and submit button.
4. User enters OTP code and clicks the submit button.
5. Website shows default canteen owner panel website.

#### Exception paths:

* If username-password pair or OTP code are incorrect, flow goes back to (1). Website will show error message above the form. 

### • View canteen list

### • Register new canteen

### • Delete canteen

### • Edit canteen information

### • Add today's menu

### • Add today's menu photo

### • Upload today's menu from a photo

### • Remove uploaded menu

### • Change menu item

### • View user's menu item review

### • Post menu to Facebook

### • Export menu to PDF

### • Register new camera

### • Check camera state (on/off/lost connection)

### • Change camera name

### • Reset camera authentication key

### • Unregister camera

### • View cafeteria occupancy broken by period

### • View average menu option price broken by period

### • View multiple analytics datasets broken by period

## Android app related

### • Find user's canteen

Pre-condition: Android app is in the initial screen

Post-condition: Android app is in the concrete canteen screen

#### Possible paths:

* User choose to find canteen based on phone's location
   1. User clicks "Find by your location"
   2. The map screen appears with basic map controls and marked points with the cafeterias (with occupancy data for each canteen available after single click on place)
   3. User can navigate to his location or just select any canteen by double-clicking it on map
   4. User is navigated to main screen of the selected canteen
* User choose to find canteen based on string search with filters
  1. User clicks "Find by name"
  2. Search screen appears with basic filters (occupancy, rating, prices, opened/closed, food tags filters) and user inputs his preferences to search for canteen and clicks search button
  3. Matching cafeterias appears in list on screen and user makes a decission by clicking by any of them
  4. User is navigated to main screen of the selected canteen

#### Exception path: 
  1. After selecting filters there are no matches for user so none of the canteen can be selected and user stays at the search screen or changes his

### • Browse canteen info data

Pre-condition: Concrete canteen screen was opened by user and user didn't go back to initial or search screen

Post-condition: User can see all the information about specified canteen (address, name, current occupancy, average rating of dishes, opening hours, current status: open/closed)

#### Possible path: 
  1. User clicks on navigation menu button or swipe from left to right to open navigation menu
  2. User selects "Place info" item in application navigation menu

### • Browse canteen statistics data

Pre-condition: Concrete canteen screen was opened by user and user didn't go back to initial or search screen

Post-condition: User can see all of the specified canteen statistics data

#### Possible path: 
  1. User clicks on navigation menu button or swipe from left to right to open navigation menu
  2. User selects "Stats and data" item in application navigation menu

### • Browse canteen dishes data

Pre-condition: Concrete canteen screen was opened by user and user didn't go back to initial or search screen

Post-condition: User can see all food served in canteen with names, prices and ratings 

#### Possible path: 
  1. User clicks on navigation menu button or swipe from left to right to open navigation menu
  2. User selects "Menu" item in application navigation menu

### • Rate canteen dishes

Pre-condition: Concrete canteen screen was opened by user and user selected the "Menu" screen in app

Post-condition: User's feedback sent to server and the ratings in menu is updated

#### Possible path: 
  1. User clicks on some dish card item in "Menu" view and rating window appears
  2. User selects his rating by clicking on proper star on rating bar
  3. Rating abr closes and the menu data is updated

### **TODO**: Use app settings