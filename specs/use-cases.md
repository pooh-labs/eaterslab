# Use cases

## Admin Panel related

### Log in to the panel

Same as "Log in to the panel", but with Admin role.

### Register new canteen owner

### Delete canteen owner

## Canteen owner Panel related

### Log in to the panel

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

### Register new canteen

Pre-condition: Canteen owner is logged in.

### Delete canteen

Pre-condition: Canteen owner is logged in.

### Change canteen information / resize canteen

Pre-condition: Canteen owner is logged in.

### **TODO**: More manage canteens use cases

### Upload today's menu

Pre-condition: Canteen owner is logged in.

### Remove uploaded menu

Pre-condition: Canteen owner is logged in.

### Change menu item

Pre-condition: Canteen owner is logged in.

### Remove / hide / report a user's menu item review

Pre-condition: Canteen owner is logged in.

**TODO**: More manage menus use cases

### Register new camera

Pre-condition: Canteen owner is logged in.

### Check camera state (on/off/lost connection)

Pre-condition: Canteen owner is logged in.

### Change camera name

Pre-condition: Canteen owner is logged in.

### Reset camera authentication key

Pre-condition: Canteen owner is logged in.

### Unregister camera

Pre-condition: Canteen owner is logged in.

### View cafeteria occupancy broken by period

Pre-condition: Canteen owner is logged in.

### View average menu option price broken by period

Pre-condition: Canteen owner is logged in.

### View multiple analytics datasets broken by period

Pre-condition: Canteen owner is logged in.

## **TODO**: Android app related
