<h3 id='LockedLayerConfig' > LockedLayerConfig  <a href='#LockedLayerConfig'> {% octicon link height:16 %} </a> </h3>

``` 
val1: unused
```

``` 
val2: screenposition
                  Stretch -> 0
                  Top/Right -> 1
                  Top/Left -> 2
                  Bot/Right -> 3
                  Bot/Left -> 4
```

``` 
val3: unused
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

<h3 id='MultiLayerChanceConfig' > MultiLayerChanceConfig  <a href='#MultiLayerChanceConfig'> {% octicon link height:16 %} </a> </h3>

``` 
val1: chance of spawn 1-1000
```

``` 
val2: unused
```

``` 
val3: unused
```

``` 
fromBottom: used as a flag to spawn from bottom or top
```

```json 
"MULTI_LAYER_CHANCE": {
       "tableData": [
         {"customPath":"./MULTI_LAYER/layer1",
          "alpha":1,"scale":1.0,
          "val1":1000,
          "fromBottom": true
          "enabled":false, "speedRate":2 },
         {"customPath":"./MULTI_LAYER/layer2", ... }
         {"customPath":"./MULTI_LAYER/layer3", ... }
       ]
     }
```

<h3 id='CopyPasteVoidConfig' > CopyPasteVoidConfig  <a href='#CopyPasteVoidConfig'> {% octicon link height:16 %} </a> </h3>

```json 
//TODO
```

<h3 id='DrosteConfig' > DrosteConfig  <a href='#DrosteConfig'> {% octicon link height:16 %} </a> </h3>

``` 
val1: Offset for each new expanding layer (10-400)
```

``` 
val2: 
```

``` 
val3: 
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
     }
```

<h3 id='TapAnimConfig' > TapAnimConfig  <a href='#TapAnimConfig'> {% octicon link height:16 %} </a> </h3>

``` 
val1: yoffset  from caret
```

``` 
val2: xoffset  from caret
```

``` 
val3: unused
```

```json 
"TAP_ANIM": {
         "tableData": [
             {"customPath":"./TAP_ANIM/layer1", "alpha":1,
                 "val1":15,
                 "val2":-30,
                 "scale":0.8,
                 "enabled":true,"speedRate":4, "isCyclic": true
             }
         ]
     }
```

<h3 id='MultiLayerConfig' > MultiLayerConfig  <a href='#MultiLayerConfig'> {% octicon link height:16 %} </a> </h3>

``` 
val1: unused
```

``` 
val2: max particles 1-10
```

``` 
val3: unused
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

<h3 id='LinkerConfig' > LinkerConfig  <a href='#LinkerConfig'> {% octicon link height:16 %} </a> </h3>

``` 
val1: Offset to start on links
```

``` 
val2: Repeat every N links
```

``` 
val3: unused
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

<h3 id='LizardConfig' > LizardConfig  <a href='#LizardConfig'> {% octicon link height:16 %} </a> </h3>

``` 
val1: weight for spawn chance (1-1000)
```

``` 
val2: unused
```

``` 
val3: unused
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

<h3 id='LanternConfig' > LanternConfig  <a href='#LanternConfig'> {% octicon link height:16 %} </a> </h3>

``` 
val1: Offset to start on links
```

``` 
val2: Repeat every N links
```

``` 
val3: max length of links per sprite type
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

<h3 id='SoundConfig' > SoundConfig  <a href='#SoundConfig'> {% octicon link height:16 %} </a> </h3>

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

