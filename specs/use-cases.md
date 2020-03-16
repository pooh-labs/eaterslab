# Use cases

There are three user roles in the system:
* Admin: Manages canteen owners
* Canteen owner: Third-party business owner, manages canteens and owns devices
* (Unregistered) user: Browses public data

## Admin Panel related

### • Log in to the panel

Same as "Log in to the panel", but with Admin role.

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
5. Website shows default admin panel website.

#### Exception paths:

* If username-password pair or OTP code are incorrect, flow goes back to (1). Website will show error message above the form. 

### • Register new canteen

### • Delete canteen

### • Change canteen information / resize canteen

### **TODO**: More manage canteens use cases

### • Upload today's menu

### • Remove uploaded menu

### • Change menu item

### • Remove / hide / report a user's menu item review

### **TODO**: More manage menus use cases

### • Register new camera

### • Check camera state (on/off/lost connection)

### • Change camera name

### • Reset camera authentication key

### • Unregister camera

### • View cafeteria occupancy broken by period

### • View average menu option price broken by period

### • View multiple analytics datasets broken by period

## **TODO**: Android app related
