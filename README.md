
Notes on plugin Dev:


#Making a configurable UI for the plugin

you need to make this in plugin.xml for configurableUI menu
```
You will need 3 MAIN components for your classes to extend.

ONE : The BaseComponent,   (actual plugin work)
TWO : the ConfigurableBase  (deals with UI to configure plugin)
THREE: ConfigurableUI  hooked up to TWO
```


e.g.:

    public class MyPlugin implements BaseComponent{
    ...
    }
    
    //MyConfigurableUI will be a Swing GUIForm generated by you in intellij
    public class MyConfigurable extends ConfigurableBase<MyConfigurableUI, MyPlugin> {
    ...
    }
    
    //File generated by making a SwingGUI, just need to add 'implements...'
    MyConfigurableUI implements ConfigurableUi<MyPlugin> {
     ... 
     @Override
     public void apply(@NotNull MyPlugin myPlugin) throws ConfigurationException {
         // do stuff with myPlugin and this classes swingUI checkboxes,JTextfields etc..
         myPlugin.setting1 = this.checkbox1
     }
    }

So To tell your plugin to use all these classes...

We put our ONE, BaseComponent in this section:
```xml
    <application-components>
        <component>
            <implementation-class>com.cschar.myPlugin.MyPlugin</implementation-class>
        </component>
    </application-components>
```

we put TWO, our ConfigurableBase in this section:

```xml
 <extensions defaultExtensionNs="com.intellij">
 
        <applicationConfigurable
         groupId="appearance"
         groupWeight="20"
         id="com.your.long.unique.id"
         displayName="menu item name for users 3 3 3"
         instance="path.to.your.Class.using.ConfigurableBase" />
  </extensions>
```

by adding this in your .xml you are using the file com.intellij.openapi.options.ConfigurableEP

we put groupId as "appearance" since in that file, it has been set up that:
```
Appearance & Behavior {@code groupId="appearance"}</dt>
   * <dd>This group contains settings to personalize IDE appearance and behavior:
   * change themes and font size, tune the keymap, and configure plugins and system settings,
   * such as password policies, HTTP proxy, updates and more.
```