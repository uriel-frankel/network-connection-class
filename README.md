# network-connection-class
Facebook network-connection-class fork

https://github.com/facebookarchive/network-connection-class.git
I did converted old code into kotlin.


# ![Network Connection Class](https://github.com/facebook/network-connection-class/raw/master/docs/images/logo_trans_square.png) Network Connection Class

Network Connection Class is an Android library that allows you to figure out
the quality of the current user's internet connection.  The connection gets
classified into several "Connection Classes" that make it easy to develop
against.  The library does this by listening to the existing internet traffic
done by your app and notifying you when the user's connection quality changes.
Developers can then use this Connection Class information and adjust the application's
behaviour (request lower quality images or video, throttle type-ahead, etc).

Network Connection Class currently only measures the user's downstream bandwidth.
Latency is also an important factor, but in our tests, we've found that bandwidth
is a good proxy for both.

The Network Connection Class library takes care of spikes using a moving average
of the incoming samples, and also applies some hysteresis (both with a minimum
number of samples and amount the average has to cross a boundary before triggering
a bucket change):
![Bandwidth Averaging](https://github.com/facebook/network-connection-class/raw/master/docs/images/bandwidth_averaging.png)

