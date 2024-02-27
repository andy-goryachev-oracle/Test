# Public InputMap

Andy Goryachev
<andy.goryachev@oracle.com>


## Summary

Add the **InputMap** property in the Control class which enables customization of controls with the user key mappings and event handlers, and to guarantee specific order of event handlers invocation and priority of user mappings over those created by the skin.

Add **SkinInputMap** and **BehaviorBase** classes to simplify creation of skins for exising and new controls.



## Goals

The goals of this proposal are:

- allow for customization of a control behavior by changing the existing or adding new key mappings
- support dynamic modification of key mappings
- allow for accessing the default functionality even when it was overwritten by the user
- allow for reverting customization to the default implementation
- guarantee priorities between the user and the skin event handlers and key mappings
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

Another problem encountered by the developers is undetermined order of event dispatching to the handlers added by the user and by the skin: since the order or invocation is determined by the order an event handler is added to the control, the default skin always has priority (which many consider to be incorrect).  The order reverses after the skin is replaced, which is unexpected.

The proposed solution will benefit:

- application developers, by providing a mechanism for controlling key bindings
- custom controls developers, by providing a clear integration point and convenient public APIs
- skin developers, by simplifying skin's event handlers management



## Description

A new property, represented by the **InputMap** class, is added to the Control class.  The InputMap provides prioritized storage of the event handlers as well as key mappings, added by the user as well as the skin.

Before we delve into more details, it might help to clarify the roles of parts that constitute a Control.



### JavaFX Model-View-Controller Paradigm

JavaFX controls is supposed to follow the classic Model-View-Controller (MVC) paradigm [0].

In reality, JavaFX does not exhibit a strict adherence to MVC pattern, for example, Control represents both the model (by hosting various properties) and a part of the View (by being a Region and a part of the scene graph).  The table below summarizes the role of each part:

|Entity    |Role       |Description      |
|:---------|:----------|-----------------|
|Control   |Model      |A class that provides various properties that map into the "Model" in the MVC pattern.
|Skin      |View       |The skin provides a visual representation of the control, serving a role of "View" in the MVC pattern.
|Behavior  |Controller |The behavior reacts to the user input and updates the "Model" by modifying the control's properties and/or calling public methods in the control.
|Input Map |           |Serves as an integration point between Control and Skin/Behavior by providing a repository for the event handlers and the key mappings.  Guarantees the order in which events are being dispatched to the registered event handlers.


#### Role of the Control

We might think of Control as a façade, which exposes its properties and methods which enable the user to interact with the control.

Control is also a Rectangle, and therefore is a part of the scene graph and participates in the event handling.



#### Role of the Skin

The skin creates the visual representation of the control by adding one or mode Nodes to the Control (which is a Rectangle).  These Nodes correspond to various visual elements and may also accept user input such as mouse events.

The skin adds listeners to the Control properties and to the events coming from its control surfaces.  These listeners handle input events and modify the Control state by updating the Control's properties through the internal behavior class. 


#### Role of the Behavior

Behavior is an internal implementation detail, created by the Skin, for the purpose of handling the user input by converting it to updates of the Control's properties, either directly or via Control's public methods.

Most of the behavior implementations maintain some kind of state required to properly interpret the user input.  For example, the mouse listeners in the text controls use the state of the SHIFT key in their mouse listeners to generate either a caret change or a selection change behavior.

It is important to mention that the behavior is usually tightly coupled with its skin and cannot be easily separated (by some kind of common API).  A different skin may provide completely different visual control surfaces and require completely different gestures, which explains why the behavior is still an internal class created by its skin.

Finally, some Controls require no separate behavior class (ButtonBar), or the behavior can be implemented as a singleton if the state is fully captured by the control or its skin (TabPane).  Please refer to [1] for more information.



#### Role of the InputMap

The **InputMap** serves as an integration point between the Control and its Skin.  The InputMap provides an ordered repository of event handlers, guaranteeing the order in which handers are invoked.  It also stores key mappings with a similar guarantee that the user-defined mappings always take precedence over mappings created by the skin, regardless of when the skin was created or replaced.



### Organization

Most of the new classes concentrate in the new package **javafx.scene.control.input**:

- BehaviorBase
- EventCriteria
- FunctionHandler
- FunctionHandlerConditional
- FunctionTag
- InputMap
- KeyBinding
- SkinInputMap

The API surface of the proposed change is fairly large, please refer to the [API Specification](https://cr.openjdk.org/~angorya/InputMapV2/javadoc/) [2] for more detail.




### InputMap

The purpose of this class is to store event handlers and key mappings in order to facilitate the following operations:

- map a key binding to a function, provided either by the user or the skin
- un-map a key binding
- map a new function to an existing key binding
- obtain the default function
- add an event handler at specific priority (applies to user-defined and skin-defined handlers)
- ensure that the user key mappings take priority over mappings created by the skin

The InputMap provides an ordered repository of event handlers, working together with **SkinInputMap** supplied by the skin (or static stateless behavior implementation).  Internally, each handler is added with a specific priority according to this table:

|Priority   |Description   |
|:----------|:-------------|
|USER_HIGH  |Event handlers set by the user	
|USER_KB    |Key mappings supplied by the user	
|SKIN_KB    |Key mappings supplied by the SkinInputMap	
|SKIN_HIGH  |Event handlers set by the SkinInputMap	
|SLIN_LOW   |Event handlers set by the SkinInputMap via addHandlerLast()	
|USER_LOW   |Event handlers set by the user via addHandlerLast()	

For key mappings, the InputMap utilizes a two-stage lookup.  First, the key event is matched to a **FunctionTag** which identifies a function provided either by the skin or the associated behavior (the "default" function), or by the user.  When such a mapping exists, the found function tag is matched to a function registered either by the user or by the skin.  This mechanism allows for customizing the key mappings and the underlying functions independently and separately.

An added benefit of such an independent customization is to enable limited customization of the control behavior without subclassing either the skin or the associated behavior classes.

The InputMap also supports dynamic (that is, at run time) key mapping customization.

The InputMap class provides the following public methods:

- void **addHandler**(EventType, EventHandler)
- void **addHandlerLast**(EventType, EventHandler)
- FunctionHandler **getDefaultFunction**(FunctionTag)
- FunctionHandler **getFunction**(FunctionTag)
- FunctionHandler **getFunction**(KeyBinding)
- FunctionTag **getFunctionTag**(KeyBinding)
- Set<KeyBinding> **getKeyBindingsFor**(FunctionTag);
- void **registerFunction**(FunctionTag, FunctionHandler)
- void **registerFunctionCond**(FunctionTag, FunctionHandlerConditional)
- void **registerKey**(KeyBinding, FunctionTag)
- void **removeHandler**(EventType, EventHandler)
- void **resetKeyBindings**()
- void **restoreDefaultFunction**(FunctionTag)
- void **restoreDefaultKeyBinding**(KeyBinding)
- void **setSkinInputMap**(SkinInputMap)
- void **unbind**(FunctionTag)
- void **unbind**(KeyBinding)



### SkinInputMap

This class provides a secondary repository for the event handlers and key mappings created by the skin.  The skin is expected to construct an instance of it attach to the control's main InputMap via InputMap.setSkinInputMap() in Skin.install().

For skins whose behaviors are stateless, a singleton SkinInputMap can be used.

SkinInputMap class provides the following public methods:

- void **addHandler**(EventCriteria, boolean consume, EventHandler)
- void **addHandler**(EventType, boolean consume, EventHandler)
- void **addHandlerLast**(EventCriteria, boolean consume, EventHandler)
- void **addHandlerLast**(EventType, boolean consume, EventHandler)
- void **duplicateMapping**(KeyBinding, KeyBinding)
- FunctionTag**getFunctionTag**(KeyBinding)
- Set<KeyBinding> **getKeyBindingsFor**(FunctionTag);
- void **register**(FunctionTag, KeyBinding, FunctionHandler)
- void **register**(FunctionTag, KeyCode, FunctionHandler)
- void **registerFunction**(FunctionTag, FunctionHandler)
- void **registerFunction**(FunctionTag, FunctionHandlerConditional)
- void **registerKey**(KeyBinding, FunctionTag)
- void **registerKey**(KeyCode, FunctionTag)



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
- protected void **execute**(FunctionTag)

The use of the InputMap allows the Control to provide public methods for some or all function tags.  These methods allow the user to customize some aspects of behavior without making changes to the public APIs or subclassing.

The following example illustrates a copy() method declared at the control level, which delegates to the function provided by the behavior or the user:

```java
    public void copy() {
        execute(Tags.COPY);
    }
```




### KeyBinding

This immutable class represents either a key pressed, a key typed, or a key released, with zero or mode modifiers such as Ctrl, Shift, Alt, or Meta.  This class is suitable to be put into a map to be matched against a KeyEvent by the InputMap.

Most KeyBindings, corresponding to a KEY_PRESSED event, can be constructed with one of the convenient factory methods:

- static KeyBinding **alt**(KeyCode)
- static KeyBinding **command**(KeyCode)
- static KeyBinding **ctrl**(KeyCode)
- static KeyBinding **ctrlShift**(KeyCode)
- static KeyBinding **of**(KeyCode)
- static KeyBinding **shift**(KeyCode)
- static KeyBinding **shortcut**(KeyCode)

For more complex modifier combinations, or when the key binding corresponds to a KEY_TYPED or KEY_RELEASED event, a builder pattern should be used.  

The Builder can be obtained with either of the two methods:

- static KeyBinding.Builder **with**(KeyCode)
- static KeyBinding.Builder **with**(String character)

The **KeyBinding.Builder** class provides the following methods:

- Builder **alt**()
- Builder **alt**(boolean)
- KeyBinding **build**()
- Builder **command**()
- Builder **command**(boolean)
- Builder **ctrl**()
- Builder **ctrl**(boolean)
- Builder **meta**()
- Builder **meta**(boolean)
- Builder **onKeyReleased**()
- Builder **onKeyTyped**()
- Builder **option**()
- Builder **option**(boolean)
- Builder **shift**()
- Builder **shift**(boolean)
- Builder **shortcut**()
- Builder **shortcut**(boolean)

The following are the public instance methods of the KeyBinding class:

- boolean **isAlt**()
- boolean **isCommand**()
- boolean **isCtrl**()
- boolean **isEventAcceptable**(KeyEvent)
- boolean **isKeyPressed**()
- boolean **isKeyReleased**()
- boolean **isKeyTyped**()
- boolean **isMeta**()
- boolean **isMeta**()
- boolean **isOption**()
- boolean **isShift**()
- boolean **isShortcut**()



### FunctionHandler and FunctionHandlerConditional

These two functional interfaces provide distinction between two lambdas used by SkinInputMap and BehaviorBase: **FunctionHandler** always consumes the matching event, whereas **FunctionHandlerConditional** allows the lambda decide whether the event should be consumed or not.




### BehaviorBase

This convenience class is intended to simplify creation of stateful behaviors, by maintaining an instance of SkinInputMap and adding helpful methods for registering key mappings and event handlers.  It enables easy integration of the default functionality into its owner Skin in the latter's **Skin.install**() method:

```java
    @Override
    public void install() {
        super.install();
        behavior.populateSkinInputMap();
        setSkinInputMap(behavior.getSkinInputMap());
    }

```

BehaviorBase provides the following public methods:

- public SkinInputMap<C> **getSkinInputMap**()
- public void **populateSkinInputMap**()

It also provides a number of protected methods intended to be called by the behavior implementation in BehaviorBase.getSkinInputMap():

- protected void **addHandler**(EventCriteria, boolean consume, EventHandler)
- protected void **addHandler**(EventType, boolean consume, EventHandler)
- protected void **addHandlerLast**(EventCriteria, boolean consume, EventHandler)
- protected void **addHandlerLast**(EventType, boolean consume, EventHandler)
- protected void **duplicateMapping**(KeyBinding, KeyBinding)
- protected void **register**(FunctionTag, KeyBinding, FunctionHandler)
- protected void **register**(FunctionTag, KeyCode, FunctionHandler)
- protected void **registerFunction**(FunctionTag, FunctionHandler)
- protected void **registerKey**(KeyBinding, FunctionTag)
- protected void **registerKey**(KeyCode, FunctionTag)

As well as some additional methods:

- protected <C extends Control> C **getControl**()
- protected boolean **isLinux**()
- protected boolean **isMac**()
- protected boolean **isWindows**()



### Examples



#### Using the InputMap by the Application

An application can use the InputMap feature to:

- adding a new key mapped to a new function (**Discussion is needed**)
- redefine an existing function while keeping the key binding(s) intact
- map an existing function to a new key binding
- obtain the default function after it being redefined


#### Adding a New Key Mapped to a New Function

(To be discussed, see [3])

A simple way of adding a key mapping to a new function, without requiring a FunctionTag:

```java
        // creates a new key binding mapped to an external function
        control.getInputMap().registerKey(KeyBinding.shortcut(KeyCode.W), () -> {
            System.out.println("console!");
        });
```



##### Redefine an Existing Function While Keeping the Key Binding(s) Intact

This example redefines a copy() method in the control without changing the key bindings associated with it (which also might be different between platforms):

```java
    // provides alternative 'copy' implementation
    private void customCopy() { /** ... */ }

    // redefine function keeping existing key mappings
    getInputMap().registerFunction(Tag.COPY, (control) -> customCopy());
```

##### Map an Existing Function to a New Key Binding

This example adds a key binding to the existing function by referencing the associated FunctionTag:

```java
    // map a new key binding
    control.getInputMap().registerKey(KeyBinding.shortcut(KeyCode.W), Tag.COPY);
```


##### Obtain the Default Function

The following code allows to obtain the default function when it has overwritten by the user:

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
        private static final SkinInputMap<TabPane> inputMap = createInputMap();
    
        private static SkinInputMap<TabPane> createInputMap() {
            SkinInputMap<TabPane> m = new SkinInputMap<>();
            m.registerFunction(...);
            ...
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

TBD



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

[0] JavaFX Model-View-Controller (MVC) https://wiki.openjdk.org/display/OpenJFX/UI+Controls+Architecture
[1] Control Class Hierarchy https://github.com/andy-goryachev-oracle/Test/blob/main/doc/Controls/ControlsClassHierarchy.md
[2] API Specification (Javadoc): https://cr.openjdk.org/~angorya/InputMapV2/javadoc/
[3] Discussion: https://github.com/andy-goryachev-oracle/Test/blob/ag.jep.input.map.v2/doc/InputMap/InputMapV2-Discussion.md


