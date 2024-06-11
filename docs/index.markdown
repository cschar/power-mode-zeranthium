---
# Feel free to add content and custom Front Matter to this file.
# To modify the layout, see https://jekyllrb.com/docs/themes/#overriding-theme-defaults

layout: home
---


get more stuff here:
<h1> zeranthium-extras -----> PreConfigured Themes </h1>

Assets can be found in the zeranthium-extras repo.
You can download the assets using this command:
```bash

git clone --depth=1 https://github.com/powermode-zeranthium/zeranthium-extras-vol1
git clone --depth=1 https://github.com/powermode-zeranthium/zeranthium-extras-vol2
git clone --depth=1 https://github.com/powermode-zeranthium/zeranthium-extras-vol3

```


<h2> zeranthium-extras ---> chicken Theme </h2>
<img width="500" src="https://user-images.githubusercontent.com/296551/67953118-e6737980-fbc4-11e9-8bd8-57e9349a5500.png">

<h2> zeranthium-extras ---> fire1 Theme </h2>
<img width="400" src="https://user-images.githubusercontent.com/296551/67990855-8c4ad680-fc0d-11e9-9ac1-66bcc0f959c9.png">




<h1> Customizing your own theme pack</h1>
basically you need a folder with a manifest.json file,
with sibling folders containing the assets the manifest.json file will point to:

```bash
   my_theme_folder
    ----manifest.json
    ----SOUND
         ----sound1.mp3
         ----sound2.mp3
         ----sound3.mp3
    ----LOCKED_LAYER
         ----layer_foo
              ----- 000.png
              ----- 001.png
                    ...
              ----- 099.png
    ----MULTI_LAYER
         ----layer_bar
              ----- 000.png
              ----- 001.png
                    ...
              ----- 099.png

```

Once the manifest.json file is created with the assets, from inside the plugin settings you can open
that custom pack file. The plugin will autoload all configurations specified in the manifest.

Here is an example manifest.json file for a theme
that provides settings for SOUND, MULTI_LAYER, and LOCKED_LAYER options.
See <a href="#settings"> individual settings </a> section for Option specific details.

```json
{
  "configsToLoad": ["SOUND","MULTI_LAYER", "LOCKED_LAYER"],
  "configSettings": {
    "SOUND": {
      "tableData": [
        {"weight": 20, "customPath": "./SOUND/sound1.mp3"},
        {"weight": 20, "customPath": "./SOUND/sound2.mp3"},
        {"weight": 20, "customPath": "./SOUND/sound3.mp3"},
        {"weight": 20, "customPath": "./SOUND/sound4.mp3"}
      ]
    },
    "LOCKED_LAYER": {
      "tableData": [
        {"customPath":"./LOCKED_LAYER/layer_foo", "alpha":1, "offset":20,
          "scale":1.0, "val2": 3,  "enabled":true,
          "speedRate":2, "isCyclic": true}
      ]
    },
    "MULTI_LAYER": {
      "tableData": [
        {"customPath":"./MULTI_LAYER/layer_bar", "alpha":1, "offset":20,
         "scale":1.0, "enabled":false,"speedRate":2, "isCyclic": true}
      ]
    }
  }
}
```



<h1 id="settings"> Option Settings</h1>


{% include options.md %}

<h3> Action Sound </h3>
Todo
