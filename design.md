**Technologies Used**

I chose to use the following technologies in this project:

 - Firebase Auth UI 
 - Firebase Cloud 
 - Firestore Fragments 
 - Camera API 
 - Glide 4.0

**Design Choices**

I chose to use Firebase Auth UI for user registration and secure authentication. The fact that it had its own UI meant I didn’t have to design one from scratch and it is easily customisable.

Firebase Cloud Firestore was used to store user account info (additional to what is stored in Firebase Authentication), posts, photos and comments. I was able to make use of subcollections here by creating a collection called “comments” for each post that was commented on.

Fragments were used to implement user profile functionality. It was also planned to use fragments for finding friends and viewing profiles of other users so that the same fragments could be reused.

The Camera API is used for taking photos and adding them to posts. This was also to be implemented in the Edit Profile fragment so a user could change their profile photo however I encountered difficulty here in trying to get onActivityResult() to be called from inside the fragment.

Finally, I used Glide 4.0 which is an open source image loading framework for Android that I used to display images retrieved from Firebase such as photos in posts and user profile photos.

**Lessons Learned**

If I were to attempt the project again I would probably rethink using a fragment for the Edit Profile section. When implementing the camera API here so that a user could change their profile photo I spent a lot of time trying to get onActivityResult() to be called. A similar implementation in the New Post activity worked well but seems to behave differently in a fragment and although I spent a lot of time researching the issue and trying different workarounds I was not able to resolve it.

Furthermore, it took a long time at the beginning of the project to become familiar with the camera API and successfully implement it in the New Post activity. I also encountered a permissions issue whereby photos taken using the app can only be stored in the app folder on the device the permission that would have allowed them to be stored on shared storage (i.e. the gallery) was deprecated in API level 28. I have not been able to find a successful alternative approach for this. Overall, I feel that the time spent on the camera API may have outweighed the benefit.

As Android is a new API for me and much of the course material was self-directed I found myself in a situation where “I didn’t know what I didn’t know”. Google release a new version of Android each year and with each release there are deprecations of older classes and methods. Quite a lot of my time was spent on Stack Overflow and YouTube looking for explanations and tutorials but often if I tried to implement a similar solution to what I found in my research the IDE would alert me to the fact that that particular method or class had been deprecated. I would then endeavour to find the appropriate current implementation in the Android documentation but there may or may not be examples of how to use a new implementation. All of this resulted in a lot of trial and error and a lot of frustration.

I understand that the project was intended as a group effort, however the difficulty in joining the class as a returning student and the fact that I would be relying on people I don’t know to determine an enormous part of my grade leads me to believe that were I to re-attempt the project I would still request to do so individually.
