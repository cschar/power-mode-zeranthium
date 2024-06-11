

 
<h3> LockedLayerConfig </h3>

``` 
val1: Unused
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
val3: Unused
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


<h3> TapAnimConfig </h3>

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


<h3> LanternConfig </h3>

