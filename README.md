# Wallser
A wallpaper browsing and downloading app

Download the <a href="https://drive.google.com/file/d/0B3RSnegdFaaFQzNHMkx5N01KS2M/view?usp=sharing">current apk-release</a>

## API Key Set-up

1. Register on <a href="https://unsplash.com/">unsplash.com</a>

2. Get your new api key by selecting your <a href="https://unsplash.com/oauth/applications">applications</a>

3. Make a constants.xml file in res\values directory

4. Place your api key as an string item. 

   > Line of code to put in constants.xml
   
   `<item name="unsplash_api_key" type="string">9e208c068221e7xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx</item>`
   
   
## Screens

### Main Screen
<img src="https://github.com/AbhirojPanwar/Wallser/blob/master/Wallser%20Screenshots/device-2017-03-26-172038.png" alt="Main Screen" width="180" height="300"/>

### Sort Dialog
>To order the images based on latest and popular,Network call is then made to Unsplash API based on choice.
<img src="https://github.com/AbhirojPanwar/Wallser/blob/master/Wallser%20Screenshots/device-2017-03-26-172115.png" alt="sort Dialog" width="180" height="300"/>

### Image Screen
<img src="https://github.com/AbhirojPanwar/Wallser/blob/master/Wallser%20Screenshots/imageactivity.png" alt="Image Screen" width="180" height="300"/>

### Image Screen (Image added to favorites)
<img src="https://github.com/AbhirojPanwar/Wallser/blob/master/Wallser%20Screenshots/add%20image%20to%20favorites.png" alt="Image Screen (add to favorites)" width="180" height="300"/>

### Favorites Screen
> Images marked as favorite are added to this fragment
<img src="https://github.com/AbhirojPanwar/Wallser/blob/master/Wallser%20Screenshots/favorites.png" alt="Favorites Screen" width="180" height="300"/>

### No Favorites Screen
> When there are no favorite images present in local storage, this empty view is set.
<img src="https://github.com/AbhirojPanwar/Wallser/blob/master/Wallser%20Screenshots/no%20favorites.png" alt="No Favorites Screen" width="180" height="300"/>

### No Connectivity Screen
> Set this empty view incase app can not connect to unsplash API.
<img src="https://github.com/AbhirojPanwar/Wallser/blob/master/Wallser%20Screenshots/no%20net.png" alt="No Favorites Screen" width="180" height="300"/>

## Contributions
> Contributions are welcome. Fork it, clone it or download it!

> For feature suggestion, either drop a mail or raise an issue. Any way is better.
