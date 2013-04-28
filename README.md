SMS-via-PennyTel
================

An Android app to send SMS messages via Pennytel (based on SMS-via-MNF).

The app is forked from the MNF app that portablejim created, so I can't really take much credit – he did all the hard work. (Actually, pretty much all the work, period.) One consequence of my quick and dirty ripoff is that there may well be references to MNF that come up.

There's a compiled apk in the 'apks' folder that you can download to your phone and install. Or you can build it yourself from the source.

The app works (for me, on my Xperia Ray, running ICS and a SirKay kernel and a bunch of other strange things) but is a bit rough at the moment. It is currently pretty fussy about how you put telephone numbers in – must be in the E164 standard. So, for Oz, '614xxxxxxxx'. No '+', ' ', '-', etc. Just the number. (And that goes for the Caller ID, too, I think.) Also, the result codes probably don't work properly – so if you put in a wrong phone number (for example, you include a '+') the app will probably tell you that it sent ok, but when you check in the PennyTel portal, you'll see that sending failed.

