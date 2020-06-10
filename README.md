# https://github.com/maxim-saplin/dopetest_kotlin/releases/download/APK/maxim_saplin.dopetest_kotlin.apk

Galaxy Note 10, Snapdragon 855. Behaviour is odd. At the begining of the test the counter starts at ~2000 dopes and gradually falls to ~300 in 1 minute giving average at around 500. It can drop to 130 few minutes latter. No ideas of the cause.
Android scehdulues Handler.post callbacks accroding to screen refresh rate (60Hz by default, ~16ms per frame), there's a trick in the loop's callback which does UI mutation in while loop and keeps track of 16ms elapsed time in loop to end it and schedule another callback.
 
![UI](https://github.com/maxim-saplin/dopetest_kotlin/blob/master/Screenshot_20200610-191125.jpg?raw=true)
