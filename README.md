# fs_korinthias
Kotlin android project to fetch news from FS Korinthias

Check this repo if you want to get an idea of JSOUP usage, Firebase cloud messaging, database and authentication. Also example of Tensorflow lite model inside android.

This project is in __Kotlin__ language and has:

1) JSOUP usage to fetch news directly from the HTML page.
2) Work Manager to search for new articles and schedule Notifications
3) Private messages for members only
4) Firebase Cloud Messaging, database and authentication

also:

5) Activity and TextView animations
6) Databinding
7) BindingAdapters
8) Navigation Component usage
9) MVVM with Coroutines

Smart Replies are contextually relevant, one-touch responses that help the user to reply to an incoming text message (or email) efficiently and effortlessly. Smart Replies have been highly successful across several Google products including Gmail, Inbox and Allo.

The On-device Smart Reply model is targeted towards text chat use cases. It has a completely different architecture from its cloud-based counterparts, and is built specifically for memory constraints devices such as phones & watches. It has been successfully used to provide Smart Replies on Android Wear to all first- & third-party apps.

The on-device model comes with several benefits. It is:

- Faster: The model resides on the device and does not require internet connectivity. Thus, the inference is very fast and has an average latency of only a few milliseconds.
- Resource efficient: The model has a small memory footprint on the device.
- Privacy-friendly: The user data never leaves the device and this eliminates any privacy restrictions.

# Check the python notebook how ML model is created in Keras (Tensorflow backend)
Text is classified in 3 labels as sadness, enthusiasm, neutral. After classification for each new message in chatbox we have 3 smart reply options for user to select. Check below diagram in Tensorboard:
![TensorBoard](https://github.com/farmaker47/fs_korinthias/blob/master/smart_reply_diagram.JPG)

