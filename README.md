# SimpleImageEditor
A simple image editor created for the University. It can do basic functionality like alpha blend, chroma keying, jpg and filters.

The editor when is opened appear like this:

![image](https://user-images.githubusercontent.com/31776951/194406957-8fb4ced4-178a-4047-96ab-27b1d06c6d6c.png)

## Menu
Then it have a menu bar where you can find all the implemented functions.
- File
  - Save -> Open a window to save the current image
  - Load -> Open a window to load an image to the current frame
- Compression
  - JPEG -> Apply the JPEG algorithm to the image (useful to show the compression loss)
- Compose
  - Alpha Blend -> Open a window to load an image on the background with fixed alpha 0.8 bg and 0.2 fg
  - Chroma Keying -> Open a window to load an image in the green part of the current one
  - Chroma Keying 3D -> Open a window to load an image in the green part of the current one (better result)
- Filters
  - Blur -> apply the blur kernel to the current image
  - Sharpen -> apply the sharpen kernel to the current image
  - Ridge Detection -> apply the ridge detection kernel to the current image
  - Gaussian Blur -> apply the blur kernel to the current image (better result and control)
  - Gaussian Sharpen -> apply the sharpen kernel to the current image (better result and control)
- Resizes
  - Nearest Neighbor -> Resize the image using the nearest pixel before the resize (0.75 or 1.50)
  - BiLinear -> Resize the image using 4 nearest pixels before the resize (0.75 or 1.50)
  - BiCubic -> Resize the image using 16 nearest points before the resize (0.75 or 1.50)

## Images
This is before the chroma keying
![image](https://user-images.githubusercontent.com/31776951/194411887-112d8991-88c5-47f9-8e90-2ddac1428b66.png)

This is using the normal one and you can see that the green is not completely removed and the image can be seen leaking outside the designed zone into the bricks:
![image](https://user-images.githubusercontent.com/31776951/194423362-ce384e93-0d67-45d1-85c3-fd81beed3001.png)

This is using the 3D one and the image is much more clean
![image](https://user-images.githubusercontent.com/31776951/194423448-b37276a2-7efa-41b5-9e8b-2dbe158cc972.png)




