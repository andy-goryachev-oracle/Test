# Public InputMap (Draft, v3)

Andy Goryachev

<andy.goryachev@oracle.com>


## Summary

Add the **InputMap** property in the Control class which enables customization of controls with the application key mappings and event handlers, and to guarantee specific order of event handlers invocation and priority of application mappings over those created by the skin.

Add **SkinInputMap** and optional **BehaviorBase** classes to simplify creation of skins for existing and new controls.



## Goals

The goals of this proposal are:

- allow for customization of a control behavior by changing the existing or adding new key mappings
- support dynamic modification of key mappings
- allow for accessing the default functionality even when it was overwritten by the application
- allow for reverting customization to the default implementation
- guarantee priorities between the application and the skin event handlers and key mappings
- allow for *gradual* migration of the existing controls to use the new InputMap
- support stateful and stateless (fully static) behavior implementations



## Non-Goals

It is not the goal of this proposal:

- to require specific base class for the behavior implementations
- to allow for complete decoupling of the skin from the behavior
- to make the legacy internal InputMap class public
- to make existing core behaviors public



## Motivation

Historically, going outside of what is provided by JavaFX core, such as customizing an existing component or creating a new one, has always been an arduous endeavor due to rather opaque nature of JavaFX.  Neither BehaviorBase, InputMap, nor KeyBinding classes used by the skins are public; the controls and their skins lack public APIs suitable for behavior customization.  Even a simple customization, such as remapping or adding key bindings, is nearly impossible.

Another problem encountered by the developers is undetermined order of event dispatching to the handlers added by the application and by the skin: since the order or invocation is determined by the order an event handler is added to the control, the default skin always has priority (which many consider to be incorrect).  The order reverses after the skin is replaced, which is unexpected.

The proposed solution will benefit:

- application developers, by providing a mechanism for controlling key bindings
- custom controls developers, by providing a clear integration point and convenient public APIs
- skin developers, by simplifying skin's event handlers management



## Description

A new property, represented by the **InputMap** class, is added to the Control class.  The InputMap provides prioritized storage of the event handlers as well as key mappings, added by the application as well as the skin.

Before we delve into more details, it might help to clarify the roles of parts that constitute a Control.



### JavaFX Model-View-Controller Paradigm

JavaFX controls is supposed to follow the classic
[Model-View-Controller](https://wiki.openjdk.org/display/OpenJFX/UI+Controls+Architecture)
(MVC) paradigm [0].

In reality, JavaFX does not exhibit a strict adherence to MVC pattern, for example, Control represents both the model (by hosting various properties) and a part of the View (by being a Region and a part of the scene graph).  The table below summarizes the role of each part:

|Entity    |Role       |Description      |
|:---------|:----------|-----------------|
|Control   |Model      |A class that provides various properties that map into the "Model" in the MVC pattern.
|Skin      |View       |The skin provides a visual representation of the control, serving a role of "View" in the MVC pattern.
|Behavior  |Controller |The behavior reacts to the user input and updates the "Model" by modifying the control's properties and/or calling public methods in the control.
|Input Map |           |Serves as an integration point between Control and Skin/Behavior by providing a repository for the event handlers and the key mappings.  Guarantees the order in which events are being dispatched to the registered event handlers.


#### Role of the Control

We might think of Control as a façade, which exposes its properties and methods which enable the user to interact with the control.

Control is also a Region, and therefore is a part of the scene graph and participates in the event handling.



#### Role of the Skin

The skin creates the visual representation of the control by adding one or more Nodes to the Control (which is a Region).  These Nodes correspond to various visual elements and may also accept user input such as mouse events.

The skin adds listeners to the Control properties and to the events coming from its control surfaces.  These listeners handle input events and modify the Control state by updating the Control's properties through the internal behavior class. 


#### Role of the Behavior

Behavior is an internal implementation detail, created by the Skin, for the purpose of handling the user input by converting it to updates of the Control's properties, either directly or via Control's public methods.

Most of the behavior implementations maintain some kind of state required to properly interpret the user input.  For example, the mouse listeners in the text controls use the state of the SHIFT key in their mouse listeners to generate either a caret change or a selection change behavior.

It is important to mention that the behavior is usually tightly coupled with its skin and cannot be easily separated (by some kind of common API).  A different skin may provide completely different visual control surfaces and require completely different gestures, which explains why the behavior is still an internal class created by its skin.

Finally, some Controls require no separate behavior class (ButtonBar), or the behavior can be implemented as a singleton if the state is fully captured by the control or its skin (TabPane).  Please refer to
[Control Class Hierarchy](https://github.com/andy-goryachev-oracle/Test/blob/main/doc/Controls/ControlsClassHierarchy.md)
[1] for more information.



#### Role of the InputMap

The **InputMap** serves as an integration point between the Control and its Skin.  The InputMap provides an ordered repository of event handlers, guaranteeing the order in which handers are invoked.  It also stores key mappings with a similar guarantee that the application mappings always take precedence over mappings created by the skin, regardless of when the skin was created or replaced.



### Organization

Most of the new classes concentrate in the new package **javafx.scene.control.input**:

- BehaviorBase
- EventCriteria
- FunctionHandler
- FunctionTag
- InputMap
- KeyBinding
- SkinInputMap

The API surface of the proposed change is fairly large, please refer to the
[API Specification](https://cr.openjdk.org/~angorya/InputMapV3/javadoc/)
[2] for more detail.



### InputMap

The purpose of this class is to store event handlers and key mappings in order to facilitate the following operations:

- map a key binding to a function, provided either by the application or the skin
- un-map a key binding
- map a new function to an existing key binding
- obtain the default function
- add an event handler at specific priority (applies to application-defined and skin-defined handlers)
- ensure that the application key mappings take priority over mappings created by the skin

The InputMap provides an ordered repository of event handlers, working together with **SkinInputMap** supplied by the skin (or static stateless behavior implementation).  Internally, each handler is added with a specific priority according to this table:

|Priority   |Set By      |Method                             |Description   |
|:----------|:-----------|:----------------------------------|:-------------|
|Highest    |Application |InputMap.addEventHandler()         |Event handlers set by the application	
|           |Application |InputMap.registerKey()             |Key mappings set by the application	
|           |Skin        |SkinInputMap.registerKey()         |Key mappings set by the skin
|           |Skin        |SkinInputMap.addEventHandler()     |Event handlers set by the skin	
|           |Skin        |SkinInputMap.addEventHandlerLast() |Event handlers set by the skin
|Lowest     |Application |InputMap.addEventHandlerLast()     |Event handlers set by the application

For key mappings, the InputMap utilizes a two-stage lookup.  First, the key event is matched to a **FunctionTag** which identifies a function provided either by the skin or the associated behavior (the "default" function), or by the application.  When such a mapping exists, the found function tag is matched to a function registered either by the application or by the skin.  This mechanism allows for customizing the key mappings and the underlying functions independently and separately.

An added benefit of such an independent customization is to enable limited customization of the control behavior without subclassing either the skin or the associated behavior classes.

The InputMap also supports dynamic (that is, at run time) key mapping customization.

The InputMap class provides the following public methods:

- public void **addHandler**(EventType, EventHandler)
- public void **addHandlerLast**(EventType, EventHandler)
- public Set<KeyBinding> **getKeyBindings**()
- public Set<KeyBinding> **getKeyBindingsFor**(FunctionTag);
- public void **register**(KeyBinding, Runnable)
- public void **registerFunction**(FunctionTag, Runnable)
- public void **registerKey**(KeyBinding, FunctionTag)
- public void **removeHandler**(EventType, EventHandler)
- public void **resetKeyBindings**()
- public void **restoreDefaultFunction**(FunctionTag)
- public void **restoreDefaultKeyBinding**(KeyBinding)
- public void **setSkinInputMap**(SkinInputMap)
- public void **unbind**(FunctionTag)
- public void **unbind**(KeyBinding)



### FunctionTag

A function tag is a public identifier of a method that can be mapped to a key binding.

The following example is taken from TabPane:

```java
    public class TabPane extends Control {
        /** Identifiers for methods available for customization via the InputMap. */
        public static final class Tag {
            /** Selects the first tab. */
            public static final FunctionTag SELECT_FIRST_TAB = new FunctionTag();
            /** Selects the last tab. */
            public static final FunctionTag SELECT_LAST_TAB = new FunctionTag();
            /** Selects the left tab: previous in LTR mode, next in RTL mode. */
            public static final FunctionTag SELECT_LEFT_TAB = new FunctionTag();
            /** Selects the next tab. */
            public static final FunctionTag SELECT_NEXT_TAB = new FunctionTag();
            /** Selects the previous tab. */
            public static final FunctionTag SELECT_PREV_TAB = new FunctionTag();
            /** Selects the right tab: next in LTR mode, previous in RTL mode. */
            public static final FunctionTag SELECT_RIGHT_TAB = new FunctionTag();
        }
```



### Control

Two new methods are added to the **Control** class:

- public InputMap **getInputMap**()
- public void **execute**(FunctionTag)
- public void **executeDefault**(FunctionTag)

The use of the InputMap allows the Control to provide public methods for some or all function tags.  These methods allow the application to customize some aspects of behavior without making changes to the public APIs or subclassing.

The following example illustrates a copy() method declared at the control level, which delegates to the function provided by the behavior or the application:

```java
    public void copy() {
        execute(Tags.COPY);
    }
```




### KeyBinding

This immutable class represents either a key pressed, a key typed, or a key released, with zero or mode modifiers such as Ctrl, Shift, Alt, or Meta.  This class is suitable to be put into a map to be matched against a KeyEvent by the InputMap.

Most KeyBindings, corresponding to a KEY_PRESSED event, can be constructed with one of the convenient factory methods:

- public static KeyBinding **alt**(KeyCode)
- public static KeyBinding **command**(KeyCode)
- public static KeyBinding **ctrl**(KeyCode)
- public static KeyBinding **ctrlShift**(KeyCode)
- public static KeyBinding **of**(KeyCode)
- public static KeyBinding **option**(KeyCode)
- public static KeyBinding **shift**(KeyCode)
- public static KeyBinding **shiftShortcut**(KeyCode)
- public static KeyBinding **shortcut**(KeyCode)

For more complex modifier combinations, or when the key binding corresponds to a KEY_TYPED or KEY_RELEASED event, a builder pattern should be used.  

The Builder can be obtained with either of the two methods:

- public static KeyBinding.Builder **with**(KeyCode)
- public static KeyBinding.Builder **with**(String character)

The **KeyBinding.Builder** class provides the following methods:

- public Builder **alt**()
- public Builder **alt**(boolean)
- public KeyBinding **build**()
- public Builder **command**()
- public Builder **command**(boolean)
- public Builder **ctrl**()
- public Builder **ctrl**(boolean)
- public Builder **meta**()
- public Builder **meta**(boolean)
- public Builder **onKeyReleased**()
- public Builder **onKeyTyped**()
- public Builder **option**()
- public Builder **option**(boolean)
- public Builder **shift**()
- public Builder **shift**(boolean)
- public Builder **shortcut**()
- public Builder **shortcut**(boolean)

The following are the public instance methods of the KeyBinding class:

- public boolean **isAlt**()
- public boolean **isCommand**()
- public boolean **isCtrl**()
- public boolean **isEventAcceptable**(KeyEvent)
- public boolean **isKeyPressed**()
- public boolean **isKeyReleased**()
- public boolean **isKeyTyped**()
- public boolean **isMeta**()
- public boolean **isMeta**()
- public boolean **isOption**()
- public boolean **isShift**()
- public boolean **isShortcut**()




### SkinInputMap

This class provides a secondary repository for the event handlers and key mappings created by the skin.  The skin is expected to construct an instance of it attach to the control's main InputMap via InputMap.setSkinInputMap() in Skin.install().

Most of the skins create behaviors that contain state, see
[Control Class Hierarchy](https://github.com/andy-goryachev-oracle/Test/blob/main/doc/Controls/ControlsClassHierarchy.md)
[1].  
Most frequently used skin input map is therefore SkinInputMap.Stateful, which can be obtained by calling `SkinInputMap.create()`.

For skins with stateless behaviors, a single instance of SkinInputMap.Stateless can be used, obtained via `SkinInputMap.createStateless()`.

The base SkinInputMap class provides the following public methods:

- public static SkinInputMap.Stateful **create**()
- public static <C extends Control> SkinInputMap.Stateless<C> **createStateless**()

- public void **addHandler**(EventCriteria, boolean consume, EventHandler)
- public void **addHandler**(EventType, boolean consume, EventHandler)
- public void **addHandlerLast**(EventCriteria, boolean consume, EventHandler)
- public void **addHandlerLast**(EventType, boolean consume, EventHandler)
- public void **duplicateMapping**(KeyBinding, KeyBinding)
- public Set<KeyBinding> **getKeyBindings**()
- public Set<KeyBinding> **getKeyBindingsFor**(FunctionTag)
- public void **registerKey**(KeyBinding, FunctionTag)
- public void **registerKey**(KeyCode, FunctionTag)

A Stateful variant adds the following methods:

- public void **register**(FunctionTag, KeyBinding, Runnable)
- public void **register**(FunctionTag, KeyCode, Runnable)
- public void **registerFunction**(FunctionTag, FunctionHandler)
- public void **registerFunction**(FunctionTag, Runnable)

A Stateless variant adds the following methods, which use interfaces FHandler<C> and FHandlerConditional<C>
intended to pass the reference to the source Control to the handling code:

- public void **register**(FunctionTag, KeyBinding, FHandler<C>)
- public void **register**(FunctionTag, KeyCode, FHandler<C>)
- public void **registerFunction**(FunctionTag, FHandler<C>)
- public void **registerFunction**(FunctionTag, FHandlerConditional<C>)



### FunctionHandler Interface

In addition to accepting a Runnable handler, the InputMap allows for the handler to control whether to consume
the corresponding Event.

A similar functionality is utilized by the SkinInputMap.Stateless with its SkinInputMap.FHandler and
SkinInputMap.FHandlerConditional.


### BehaviorBase

This convenience class is intended to simplify creation of stateful behaviors, by maintaining an instance of SkinInputMap and adding helpful methods for registering key mappings and event handlers.  It enables easy integration of the default functionality into its owner Skin in the latter's **Skin.install**() method:

```java
    @Override
    public void install() {
        super.install();
        setSkinInputMap(behavior.getSkinInputMap());
    }
```

BehaviorBase provides the following public method:

- public SkinInputMap<C> **getSkinInputMap**()

It also provides a number of protected methods intended to be called by the behavior implementation in BehaviorBase.getSkinInputMap():

- protected final void **addHandler**(EventCriteria, boolean consume, EventHandler)
- protected final void **addHandler**(EventType, boolean consume, EventHandler)
- protected final void **addHandlerLast**(EventCriteria, boolean consume, EventHandler)
- protected final void **addHandlerLast**(EventType, boolean consume, EventHandler)
- protected final void **duplicateMapping**(KeyBinding, KeyBinding)
- protected final C **getControl**()
- protected final boolean **isLinux**()
- protected final boolean **isMac**()
- protected final boolean **isWindows**()
- protected void **populateSkinInputMap**()
- protected final void **register**(FunctionTag, KeyBinding, Runnable)
- protected final void **register**(FunctionTag, KeyCode, Runnable)
- protected final void **registerFunction**(FunctionTag, Runnable)
- protected final void **registerKey**(KeyBinding, FunctionTag)
- protected final void **registerKey**(KeyCode, FunctionTag)
- protected final void **traverseDown**()
- protected final void **traverseLeft**()
- protected final void **traverseNext**()
- protected final void **traversePrevious**()
- protected final void **traverseRight**()
- protected final void **traverseUp**()



### Examples

The following examples illustrate common use cases for using the InputMap.

Please also refer to
[Discussion](https://github.com/andy-goryachev-oracle/Test/blob/main/doc/InputMap/InputMapV3-Discussion.md)
[3]



#### Using the InputMap by the Application

An application can use the InputMap feature to:

- add a new key mapped to a function
- redefine an existing function while keeping the key binding(s) intact
- map an existing function to a new key binding
- obtain the default function after it being redefined


##### Adding a New Key Mapped to a Function

A simple way of adding a key mapping to a application-supplied function, without requiring a FunctionTag:

```java
    // the external functionality to be added to the control
    private void externalFunction() { /** ... */ }

    // creates a new key binding mapped to an external function
    control.getInputMap().registerKey(KeyBinding.shortcut(KeyCode.W), () -> {
        externalFunction();
    });
```

This mechanism provides a simpler way for application to add a key mapping to a Control as compared with
adding a key event handler.


##### Redefine an Existing Function While Keeping the Key Binding(s) Intact

This example redefines a copy() method in the control without changing the key bindings associated with it (which also might be different between platforms):

```java
    // provides alternative 'copy' implementation
    private void customCopy() { /** ... */ }

    // redefine function keeping existing key mappings
    getInputMap().registerFunction(Tag.COPY, () -> customCopy());
```

##### Map an Existing Function to a New Key Binding

This example adds a key binding to the existing function by referencing the associated FunctionTag:

```java
    // map a new key binding
    control.getInputMap().registerKey(KeyBinding.shortcut(KeyCode.W), Tag.COPY);
```


##### Obtain the Default Function

The following code allows to obtain the default function when it has overwritten by the application:

```java
    //  obtains the default function handler
    FunctionHandler<?> h = getInputMap().getDefaultFunction(Tag.COPY);
```


#### Stateless (Static) Behaviors

A number of Controls have behavior classes that require no state: examples are DateCell, TabPane, and some other [1].  For these situations, a single static SkinInputMap instance might be sufficient, eliminating the need for per-instance behavior objects.

This example illustrates the use of a static behavior in the context of TabPaneSkin:

```java
    @Override
    public void install() {
        super.install();
        // install stateless behavior
        TabPaneBehavior.install(getSkinnable());
    }

```

The stateless behavior is implemented in the TabPaneBehavior (here for illustration purposes only, as it is not a part of public API):

```java
    public class TabPaneBehavior {
        private static final SkinInputMap.Stateless<TabPane> inputMap = createInputMap();
    
        private static SkinInputMap.Stateless<TabPane> createInputMap() {
            SkinInputMap.Stateless<TabPane> m = SkinInputMap.createStateless();
            // register functions
            m.registerFunction(...);
            // register key bindings
            m.registerKey(...);
            // add mouse handler
            m.addHandler(...);
            return m;
        }
    
        public static void install(TabPane control) {
            control.getInputMap().setSkinInputMap(inputMap);
        }
```



#### Checklist For Custom Controls

In order to fully utilize the capability of the InputMap, a new Control must:

- declare a set of FunctionTags corresponding to the customizable methods
- optionally provide public methods in the new control which delegate to the corresponding FunctionTags using execute()
- implement the behavior either in the skin class, or the class created by the skin which extends BehaviorBase, or by a class that provides a stateless behavior
- populate SkinInputMap with the default key mappings and the event handlers



## Alternatives

An application developer that needs a custom control either needs to craft a completely new implementation, or jerry-rig special event filters and handlers, hoping that their custom code won't interfere with the rest of the Control behavior and functionality (accessibility, focus traversal, etc.)



## Testing

A standard set of unit tests for all the new classes should be developed.

In order to avoid regressions during the process of migration of existing controls to take full advantage of the new InputMap functionality [4] will require development of behavior test suite, both headless and headful [5].




## Risks and Assumptions

Adding a public method to Control class might create incompatibility where application developers also added a method with the same name.

Migration of the existing core Controls to the new BehaviorBase has a risk of regression because:

- no test suite currently exists that exhaustively exercises every key mapping on every platform
- no headful tests currently exist that go beyond simple features and try to test system as a whole, complete with focus traversal, popups, etc.
- extensive manual testing would be needed
- some of the default functions may not be available when the Skin is null



## Dependencies

None.



## References

- [0] JavaFX Model-View-Controller (MVC) https://wiki.openjdk.org/display/OpenJFX/UI+Controls+Architecture
- [1] Control Class Hierarchy https://github.com/andy-goryachev-oracle/Test/blob/main/doc/Controls/ControlsClassHierarchy.md
- [2] API Specification (Javadoc): https://cr.openjdk.org/~angorya/InputMapV3/javadoc/
- [3] Discussion: https://github.com/andy-goryachev-oracle/Test/blob/main/doc/InputMap/InputMapV3-Discussion.md
- [4] [JDK-8314968](https://bugs.openjdk.org/browse/JDK-8314968) Public InputMap
- [5] [JDK-8326869](https://bugs.openjdk.org/browse/JDK-8326869) ☂ Develop Behavior Test Suite
