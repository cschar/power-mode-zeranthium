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


<h3> Lizard </h3>

```
val1: weight for spawn chance (1-1000)
```

```json
"LIZARD": {
      "tableData": [
        {"customPath":"./lizard",
          "alpha":1,
          "val1":2,
          "scale":0.4,
          "enabled":true,
          "speedRate":3},
        {"customPath":"./lizard2", ...},
        {"customPath":"./lizard3", ...}
      ]
    }
```

<h3> Multi Layer </h3>

```
val2: max particles 1-10
```

```json
"MULTI_LAYER": {
      "moveWithCaretEnabled": true,
      "moveSpeed": 0.1,
      "tableData": [
        {"customPath":"./MULTI_LAYER/layer1",
         "alpha":1,"scale":1.0,
         "val2":10, 
         "enabled":false, "speedRate":2, "isCyclic": true},
        {"customPath":"./MULTI_LAYER/layer2", ... }
        {"customPath":"./MULTI_LAYER/layer3", ... }
      ]
    }
```

<h3> Multi Layer Chance</h3>

```
val1: chance of spawn 1-1000
fromBottom: used as a flag to spawn from bottom or top
```

```json
"MULTI_LAYER_CHANCE": {
      "tableData": [
        {"customPath":"./MULTI_LAYER/layer1",
         "alpha":1,"scale":1.0,
         "val1":1000, 
         "enabled":false, "speedRate":2, "fromBottom": true},
        {"customPath":"./MULTI_LAYER/layer2", ... }
        {"customPath":"./MULTI_LAYER/layer3", ... }
      ]
    }
```

<h3> Linker </h3>

```
val2: Repeat every N links
val1: Offset to start on links
```

```json
"LINKER":{
      "tracerEnabled": false,
      "isCyclicEnabled" false,
      "distanceToCenter": 290,
      "tableData":[
        {
          "defaultPath": "./linker1/",
          "val2": 100,
          "isCyclic": false,
          "alpha": 1,
          "val1": 1,
          "scale": 0.3,
          "enabled": true,
          "speedRate": 2
        }
      ]
    }
```
<h3> Droste </h3>

```
val1: Offset for each new expanding layer (10-400)
```

```json

"DROSTE": {
      "tableData": [
        {"customPath":"./DROSTE/layer1",
         "alpha":1, "scale":1.0,
         "val1":20,
          "enabled":true,"speedRate":2, "isCyclic": true},
        {"customPath":"./DROSTE/layer2", ...}
      ]
    },

```


<h3> copyPasteVOID </h3>
TODO
<h3> Locked layer </h3>

```
val2: Screen position 
Stretch -> 0
Top/Right -> 1 
Top/Left -> 2
Bot/Right -> 3
Bot/Left -> 4
```

```json
"LOCKED_LAYER": {
      "tableData": [
        {"customPath":"./lockedlayer1",
          "alpha":1, "scale":1.0,
          "val2": 3,
          "enabled":true,"speedRate":2, "isCyclic": true},
        {"customPath":"./lockedlayer2", ...}
      ]
    }
```

<h3> Lantern </h3>

```
val3: max length of links per sprite type
val2: Repeat every N links
val1: Offset to start on links
```

```json
"LANTERN":{
      "maxParticles": 40, 
      "tracerEnabled": false,
      "isCyclicEnabled": false,
      "tableData":[
        {
          "defaultPath": "./lantern1/",
          
          "isCyclic": false,
          "alpha": 1,
          "val1": 1,
          "val2": 2,
          "val3": 12,
          "scale": 0.3,
          "enabled": true,
          "speedRate": 2
        }
      ]
    }
```

<h3> Tap Anim </h3>

```
val1: yoffset  from caret
val2: xoffset  from caret
```

```json

"TAP_ANIM": {
      "tableData": [
        {"customPath":"./TAP_ANIM/layer1", "alpha":1,
           "val1":15,
           "val2":-30,
           "scale":0.8,
          "enabled":true,"speedRate":4, "isCyclic": true}
      ]
    }

```


<h3> Sound </h3>

```json
 "SOUND": {
      "tableData": [
        {"weight": 20, "customPath": "./SOUND/sound1.mp3"},
        {"weight": 20, "customPath": "./SOUND/sound2.mp3"},
        {"weight": 20, "customPath": "./SOUND/sound3.mp3"},
        {"weight": 20, "customPath": "./SOUND/sound4.mp3"}
      ]
    }
```
<h3> Action Sound </h3>
Todo



{% include options.md %}