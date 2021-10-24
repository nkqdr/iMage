# ImageMosaic
A small Java-Program that takes a collection of images together with a single selected image and then tries to replicate the single image with all of the other images.

## Motivation
I once met a photographer who really liked the style of mosaic images and wanted to display his work in a similar way. He used a software called [TurboMosaic](https://www.turbomosaic.com) for this.
However, he told me that he sometimes had to leave his laptop running for an entire night to create such a high-resolution image, as this program often takes several hours to complete the task. 
So I decided to try a more efficient implementation and started to implement it in Java.
The result can be seen in this repository.

## Comparison
Here are a few comparison images between the results from my program and TurboMosaic. I will also provide all the testing details for these comparisons.

**All tests were run on a 2020 MacBook Pro 13". Same Computer, same collection of 1008 images, same resolutions, same time of day, same battery capacity, same temperature, etc. Of course I kept the conditions as fair as I possibly could.**

>![Brigde comparison](img/MosaicBridgeComparison.png)
>  - TurboMosaic: 5 minutes and 48 seconds
>  - My program: 5 seconds
>  - **Speed-up: 70x**

>![Cliff comparison](img/MosaicCliffComparison.png)
>  - TurboMosaic: 6 minutes and 26 seconds
>  - My program: 6 seconds
>  - **Speed-up: 64x**

>![Dog comparison](img/MosaicDogComparison.png)
>  - TurboMosaic: 8 minutes and 7 seconds
>  - My program: 5.7 seconds
>  - **Speed-up: 85x**

>![Hills comparison](img/MosaicHillsComparison.png)
>  - TurboMosaic: 6 minutes and 58 seconds
>  - My program: 6.3 seconds
>  - **Speed-up: 66x**

**In conclusion, the overall speed-up is at roughly 71x, and my algorithm never needed longer than 10 seconds for images that were around 4K resolution.**

Also, sometimes the TurboMosaic algorithm just places black squares as mosaic images whereas my algorithm never did anything like that.

## How to use
Because there is no User-Interface for this app, you have to pass all needed parameters through command line arguments when executing the main App.java file.

1. Pass the input image with the **-i** flag: 
    - *-i /path/to/your/input/image/image.jpg*
2. Pass the collection of images you want to use with the **-t** flag:
    - *-t /path/to/your/image/collection/MosaicImages* 
3. Pass the path to where the output should be saved with the **-o** flag:
    - *-o /path/to/the/output/location/output.png*
4. Pass the width and height for the individual mosaic images with the **-w** and **-h** flags (in px):
    - *-w 41 -h 41*
   
