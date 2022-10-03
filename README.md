# SpaceXApp
This app was created by Andrés Conde Rodríguez to expand my portfolio.
The APK can be downloaded in [this link](https://drive.google.com/file/d/1NRfdRdKiY0ufzRdQdvZ8NFljj1vyC5gs/view?usp=sharing "SpaceXApp APK")

## What the application does.

This app shows a list with latest launches of the company SpaceX, the details of each launch, 
provide links for more info, and images of the launches and the ships used in the missions, those 
images can be downloaded.
When internet connection is lost the app shows that cannot load info, and when the connection
is restored tha app starts to load the info again.

## How the application works.

First at all the app takes advantage of the MVVM architecture, uses a repository and dependency 
injection with dagger hilt in order to build the layers of the architecture.

All data is obtained from the SpaceX GraphQL API using the Apollo Kotlin client. All the queries are 
contained in the class LaunchRepository.

The app is able to react to the internet connection due to the functions of the kotlin file 
Loading.kt, but the implementation of this feature in the ViewHolders is done with the class 
DownloadingImagesCache.
