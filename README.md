# FOCUSBUDDY
The focus app that helps you be productive as ever!
## Intro to FocusBuddy
Ever found yourself struggling to focus? Ever thought, "Man, I really want to work on this task, but I just....can't leave Youtube Shorts...What do I do? Work on this important task, or learn about the Dancing Plague of 1518?" 
Ever wish you could just shut off the distracting apps and..._work?_

Well...

Now you can!

FocusBuddy is an app that can:
+ Get a list of apps you want to block from you
+ Monitor the apps you open
+ Block an app if it's on the block list
  and much more!

## Implementation
This repo contains all the needed code for the app. Just copy it all into a new project in Android Studio or download it using the APK. Then, turn the prerequisites on for the app, and you're ready to start using!

### Device Prerequisites
+ Dev Mode on **(only if you're not using the APK)**
+ Accessibility allowances on (so FocusBuddy can see what you're doing and block accordingly)
+ Notification allowances on for FocusBuddy (optional, but if you want to set timers, FocusBuddy notifies you when they're done) 

## How to Use
### Select your Apps
+ Use the **top button** to open a list of your apps
+ Use the **search bar** or **alphabetized list** to find your desired apps
+ Select your desired apps
### Set your Timer (optional)
+ If you'd like to set a **timer**, use the Yes button on the next page (No button will automatically start your session and you have to end it manually) 
+ Use the scroll wheels to select a time (minimum of **1 second**, maximum of **5 hours, 59 minutes, and 59 seconds**)
+ Save your timer
+ On the homescreen, there will be a **countdown** showing how long you have left on your timer
+ Once the timer is finished, you'll get a notification
### Ending Your Session
+ Use the **bottom button** on your homepage to end any session, timed or not
### Security and User Friction
+ To enter the application, a user must enter the phone password or biometrics. This ensures security and privacy if you're loaning your phone to someone else.
+ To end a session using the bottom button, the password must be entered again. This is because it gives the user a chance to think about ending their session.
