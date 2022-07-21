# power-mode-zeranthium

[![Build](https://github.com/cschar/power-mode-zeranthium/actions/workflows/runTest.yaml/badge.svg)](https://github.com/cschar/power-mode-zeranthium/actions/workflows/runTest.yaml)
[![Version](https://img.shields.io/jetbrains/plugin/v/13176-power-mode--zeranthium.svg)](https://plugins.jetbrains.com/plugin/13176-power-mode--zeranthium)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/13176-power-mode--zeranthium.svg)](https://plugins.jetbrains.com/plugin/13176-power-mode--zeranthium)


<!-- Plugin description -->
<h1>Power Mode - Zeranthium </h1>
<img src="https://user-images.githubusercontent.com/296551/63473991-68b52400-c445-11e9-84dc-e2e5269729d8.png" />
<br/>
<img src="https://user-images.githubusercontent.com/296551/86540558-4ea4f880-bed4-11ea-830b-5e3bf25e0cb1.gif" />
<br/>

<h2> Get Into your Zone... </h2>

<p> See the <a href="https://cschar.github.io/power-mode-zeranthium/"> Docs page </a> for more info </p>
<h2>Featuring:</h2>


<ul>
  <li>- Basic Particle Effects </li>
  <li>- Vines </li>
  <li>- 'Lizards' (swarms to anchors)</li>
  <li>- MultiLayer w/ spawn chance</li>
  <li>- Editor Shake </li>
  <li>- Weird square effect (MOMA)</li>
  <li>- Anchor type effects where your code structure influences particle behaviour</li>
  <li>- Customizable Anchor Types (Bracket/Brace/Parenthesis/Colon)</li>
  <li>- MultiLayer w/ caret tracking</li>
  <li>- Linker Chains w/ caret tracking</li>
  <li>- Sound panel (Tabbed Panel)  (MP3)</li>
  <li>- Play 2x Song w/ 2 Hotkeys to trigger each one (MP3)</li>
  <li>- Droste effect</li>
  <li>- CopyPasteVoid effect (pasting in displays a customizable image & color fade behind the paste shape)</li>
  <li>- Background Loading of Assets</li>
  <li>- Max 500 *.png or 1.0GB of data per Image Collection</li>
  <li>- LOAD Custom Config packs -- Support DROSTE/LIZARD/SOUND/MULTI_LAYER/LOCKED_LAYER</li>
  <li>- clone zeranthium-extras repo and select config pack to load</li>
  <li>- Action Key Sound Options (Copy/Paste/Backspace/Del/Enter)</li>
  <li>- Locked layer that stretches or pins to corner</li>
  <li>- Supports multiple editors</li>
  <li>- 'Lantern' Particles</li>
  <li>- 'Tap Animation' -- keypress controls how fast it animates</li>
  <li>- ON/OFF hotkey to quickly toggle Zeranthium</li>
</ul>
<h2>Getting Started: </h2>
<ol>
    <li> Settings can be found in Preferences > Appearance > Power Mode - Zeranthium </li>
    <li> Everything is pretty much customizable -- just point your table paths to your own custom images/sounds (PNG/MP3)
     and eventually build up a nice visual package with multiple effects/layering. </li>
    <li> To install pre-configured themes clone the
        <a href="https://github.com/cschar/zeranthium-extras"> zeranthium-extras </a> repo.
        You can also make your own (see docs site)
    </li>
    <li> You may need to
 <a href="https://www.jetbrains.com/help/idea/increasing-memory-heap.html"> increase memory heap size </a>
     if your custom assets are large in size.
    </li>
</ol>


<p>Based on Power Mode from Baptiste Mesta on Github.</p>
<p>Inspired by Power Mode II by axaluss.</p>


<!-- Plugin description end -->

Open Source Libraries Used: \
https://github.com/rkalla/imgscalr  (APACHE 2.0)\
https://mvnrepository.com/artifact/javazoom/jlayer/1.0.1 (LGPL)

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "zeranthium"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/cschar/power-mode-zeranthium/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
