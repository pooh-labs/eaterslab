# Use cases

## Admin Panel related

### Log in to the panel

### Register new canteen owner

### Delete canteen owner

## Owner Panel related

### Log in to the panel

Pre-condition: User is not logged in and enteres any admin panel website.

Post-condition: User is logged in.

#### Basic path:

1. Website displays login form with "Username", "Password" fields and submit button.
2. User enters credentials in the form and clicks the submit button.
3. If 2FA is disabled, go to (5). Otherwise, website shows OTP form with single input and submit button.
4. User enters OTP code and clicks the submit button.
5. Website shows default admin panel website.

#### Exception paths:

* If username-password pair or OTP code are incorrect, flow goes back to (1). Website will show error message above the form.

### **TODO**: Manage canteens use cases

### **TODO**: Manage menus use cases

### Register new camera

### Check camera state (on/off/lost connection)

### Change camera name

### Reset camera authentication key

### Unregister camera

### View cafeteria occupancy broken by period

### View average menu option price broken by period

### View multiple analytics datasets broken by period

## **TODO**: Android app related