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

### • Change password

### • Enable 2FA

### • Register new canteen owner

Post-condition: New canteen owner account is created

#### Basic path:

1. Admin enters "Add canteen owner" page.
2. Website displays form with a field for username and submit button.
3. Admin fills in username and clicks the submit button.
4. Website displays username and generated one-time password. This password will never be revealed again.

#### Exception paths:

* If username already exists, panel will show form from (2), with an error message.

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
5. If user logged in for the first time, they will be redirected to password change page. Otherwise, website shows default canteen owner panel page.

#### Exception paths:

* If username-password pair or OTP code are incorrect, flow goes back to (1). Website will show error message above the form.

### • Change password

### • Enable 2FA

### • View canteen list

### • View canteen details

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

### • Register new camera in the system

Post-condition: New camera device is registered and ready to transfer data.

#### Basic path:

1. Canteen owner enters "Register new device" page.
2. Website displays form with a dropdown and submit button. Dropdown lists owned canteens.
3. User selects the canteen and clicks the submit button.
4. Website displays new device name and generated API key. This key will never be revealed again.
5. User performs device setup.

### • Run camera device setup

### • View registered devices

#### Basic path:

1. Canteen owner enters "Devices" page.
2. Website displays a list of managed devices. Each row contains canteen name, device name, device status (on/off/lost connection) and edit/delete buttons.

#### Exception paths:

* If no devices are registered, in (2) website displays an empty state page with a link to "Register new device page".

### • Reset camera authentication key

Post-condition: Selected camera device has new API key.

#### Basic path:

1. Canteen owner enters "Devices" page.
2. User clicks the edit button next to selected device entry.
3. Website shows confirmation page for API key reset with a submit button.
4. User clicks the submit button.
5. Website displays device name and new API key. This key will never be revealed again.

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

### Use app settings

Pre-condition: Concrete canteen screen was opened by user and user didn't go back to initial or search screen

Post-condition: User is back in the concrete canteen screen

#### Possible path:
  1. User clicks on special menu button and selects settings
  2. User can change the application theme, language, turn on/off application notifications (if any available) and get some information about the whole system of canteens
  3. User goes back and the settings are applied for app
