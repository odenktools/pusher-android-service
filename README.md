# Realtime Android Using Pusher

This Sample app for using Pusher on Android platform.

# Features

- Learn how to running Pusher on background service.
- Notification message badges. Not use any third party libraries! (GCM and FCM). Very Cool huuh??
- ```Private Channel``` implementation, in this case you will learn how pusher authorization works
- ```Trigger Channel``` with and without ```server side```

# Requirement

Any pusher on server side, because ```sample app``` implement ```private channel```. You dont have server side? See my work on [Odenktools Laravel Pusher](https://github.com/odenktools/laravel-pusher)

# Building and Running

- Open with your editor ```app/build.gradle```
- change ```PUSHER_API_KEY``` value with your PUSHER APIKEY
- change ```PUSHER_CHANNEL_NAME``` value with any channel name you like
- change ```PUSHER_EVENT_NAME``` value with any event name you like
- change ```PUSHER_END_POINT``` value with your server side ENDPOINT. Example : ```http://yourdomain.com/``` must end with trailingslash
- change ```PUSHER_AUTH_END_POINT``` value with your server side AUTH ENDPOINT. Example : ```pusherauth```. App will combine ```PUSHER_END_POINT``` + ```PUSHER_AUTH_END_POINT```
- change ```PUSHER_TRIGGER_END_POINT``` value with your server side TRIGGER ENDPOINT. Example : ```pushertrigger```. . App will combine ```PUSHER_END_POINT``` + ```PUSHER_TRIGGER_END_POINT```
- change ```PUSHER_SERVICE_NAME``` value with any service name you like

If you like a sample give me star! Happy Coding