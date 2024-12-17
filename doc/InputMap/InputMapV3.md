# Public InputMap (Draft, v3)

Andy Goryachev

<andy.goryachev@oracle.com>

15 November 2024



## Summary

Add the **InputMap** property in the Control class, allowing the application code to customize controls
by adding, removing, or modifying the key mappings, at compile time or dynamically.

Add the **SkinInputMap** and an optional **BehaviorBase** classes to simplify development of skins for existing
and custom controls.

In the absence of a generic solution to the priority inversion problem [6],
provide a mechanism for guaranteed prioritization
of the event handlers and key mappings between application and the skin.



## Goals

The goals of this proposal are:

- allow for customization of a control behavior by changing the existing, or adding new key mappings
- support dynamic modification of key mappings
- allow for reverting customization to the default implementation
- allow for accessing the default functionality even when it was overwritten by the application
- allow for *gradual* migration of the existing controls to use the new InputMap
- support stateful and stateless (fully static) behavior implementations
- guarantee priorities between the application and the skin event handlers and key mappings


## Non-Goals

It is not the goal of this proposal:

- to require specific base class for the behavior implementations
- to allow for complete decoupling of the skin from the behavior
- to make the legacy internal InputMap class public
- to require making the behaviors public



## Motivation

Historically, going outside of what is provided by JavaFX core, such as customizing an existing component or creating a new one, has always been an arduous endeavor due to rather opaque nature of JavaFX.  Neither BehaviorBase, InputMap, nor KeyBinding classes used by the skins are public; the controls and their skins lack public APIs suitable for behavior customization.  Even a simple customization, such as remapping or adding key bindings, is nearly impossible.

Another problem encountered by the developers is undetermined order of event dispatching to the handlers added by the application and by the skin: since the order or invocation is determined by the order an event handler is added to the control, the default skin always has priority (which many consider to be incorrect).  The order reverses after the skin is replaced, which is unexpected,
as described in [6].

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

We propose to think of the `Control` as a public _façade_, which exposes its properties and methods that enable the application
to interact with the control.

Control is also a Region, and therefore is a part of the scene graph and participates in the event handling.
We can think of this Region as a container that holds the `Skin`.



#### Role of the Skin

The `Skin` creates a visual representation of the control by providing the `Nodes` which represent various surfaces
that render the control content and/or accept user input events.

The skin adds listeners to the Control properties and to the events directed at the `Control` or its input surfaces.
These listeners process the input events and effect a change of the `Control` state by updating its properties,
resulting in updating the visuals.

While it is possible to make all the listeners a part of the `Skin`, they typically organized in a separate
class known as "behavior".



#### Role of the Behavior

Behavior is an internal implementation detail, created by the `Skin`, for the purpose of handling the user input by converting it to updates of the Control's properties, either directly or via APIs published by the `Control`.

Most of the behavior implementations maintain some kind of state required to properly interpret the user input.
For example, mouse events are processed differently depending on whether the `shift` key was pressed or not.

It is important to mention that the behavior is usually tightly coupled with its skin and cannot be easily separated,
or, more specifically, is difficult to separate without creating a public API.  This may or may not be feasible:
for instance, a different skin may provide completely different visual surfaces and require completely different gestures.

We propose to solve this problem by introducing an **InputMap**, which provides a link that ties the `Skin` and the corresponding  behavior.


#### Role of the InputMap

The **InputMap** serves as an integration point between the Control and its Skin.  The InputMap provides an ordered repository of event handlers, guaranteeing the order in which handers are invoked.  It also stores key mappings with a similar guarantee that the application mappings always take precedence over mappings created by the skin, regardless of when the skin was created or replaced.

The **InputMap** also allows the application to change the behavior by changing the key mappings.  To enable this,
the `Control` must declare a set of function identifiers (represented by the **FunctionTag** class).
The **InputMap** provides a two-stage mechanism for registering key bindings to the function identifier,
and a function identifier to the actual code that implements the particular function.

This mechanism allows the application to control the key bindings separately from the implementation.
For example, a different key might be assigned to an existing function, or a different function may be assigned to
the existing key bindings (which might be platform-specific), or a function can be disabled altogether.


### Examples

The following examples illustrate common real-world scenarios made possible by the **InputMap**.



#### Using the InputMap by the Application

An application can use the InputMap feature to:

- map a key to a custom function
- dynamically redefine an existing function while keeping existing key bindings
- map a new key to an existing function
- invoke the default function after it being redefined


##### Map a Key to a Custom Function

Application requirements call for a special key binding to bring up an auto-completion popup.

The following code sample uses the `InputMap.registerKey()` method to map the new key to the new function,
either at compile time or dynamically:

```java
    // build and shows the auto-completion popup
    void showAutoCompletionPopup() { /* ... */ }

    // map shortcut-SPACE to show auto completion popup
    control.getInputMap().registerKey(KeyBinding.shortcut(KeyCode.SPACE), () -> {
        showAutoCompletionPopup();
    });
    // or
    control.getInputMap().registerKey(KeyBinding.shortcut(KeyCode.SPACE), this::showAutoCompletionPopup);
```

This mechanism provides a simpler way for application to add a key mapping to a Control as compared with
adding a key event handler.  The application mapping takes precedence over any existing mappings created by the skin.


##### Redefine an Existing Function While Keeping Existing Key Bindings

Application requirements call for an existing `shortcut-C` binding to copy the text content in a specific format,
rather than that supported by the control.

The following code sample uses the `InputMap.registerFunction()` method to change the behavior without
altering the existing key bindings, and without subclassing:

```java
    // provides alternative 'copy' implementation
    private void customCopy() { /* ... */ }

    // remap existing 'copy' keys to the new function
    getInputMap().registerFunction(Tag.COPY, this::customCopy);
```

##### Map a New Key to an Existing Function

Application requirements call for a new `shortcut-D` key binding to delete the current paragraph in a text component.

the `InputMap.registerKey()` method to map the new key to the existing, but unmapped, function:

```java
    // shortcut-D deletes the current paragraph
    control.getInputMap().registerKey(KeyBinding.shortcut(KeyCode.D), Tag.DELETE_PARAGRAPH);
```


##### Invoke the Default Function

The **InputMap** allows to change the behavior of the methods which a particular control made public
via the corresponding function tag.  In some cases, it may be useful for the default implementation to be made
available.

For example, application requirements might call for the copy() method to copy the text control in a special format,
unless some run time flag overrides that and the default implementation of copy() should be invoked.

The `Control.executeDefault()` method provides such functionality:

```java
    //  run the default function
    control.executeDefault(Tag.COPY);
```



### Organization

Most of the new classes concentrate in the new package **javafx.scene.control.input**:

- BehaviorBase
- EventCriteria
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
|Highest    |Application |InputMap.addHandler()              |Event handlers set by the application	
|           |Application |InputMap.registerKey()             |Key mappings set by the application	
|           |Skin        |SkinInputMap.registerKey()         |Key mappings set by the skin
|Lowest     |Skin        |SkinInputMap.addHandler()          |Event handlers set by the skin	

For key mappings, the InputMap utilizes a two-stage lookup.  First, the key event is matched to a **FunctionTag** which identifies a function provided either by the skin or the associated behavior (the "default" function), or by the application.  When such a mapping exists, the found function tag is matched to a function registered either by the application or by the skin.  This mechanism allows for customizing the key mappings and the underlying functions independently and separately.

An added benefit of such an independent customization is to enable limited customization of the control behavior without subclassing either the skin or the associated behavior classes.

The InputMap also supports dynamic (that is, at run time) key mapping customization.

The InputMap class provides the following public methods:

- public void **addHandler**(EventType, EventHandler)
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
- public void **removeKeyBindingsFor**(FunctionTag)
- public void **disableKeyBinding**(KeyBinding)



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

Note: alternatively, the `FunctionTag` can be replaced by a String at the expense of some loss of string typization and clarity.


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

- public static KeyBinding.Builder **builder**(KeyCode)
- public static KeyBinding.Builder **builder**(String character)

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

- public boolean **isEventAcceptable**(KeyEvent)
- public boolean **isKeyPressed**()
- public boolean **isKeyReleased**()
- public boolean **isKeyTyped**()

Lastly, sometimes it is useful to create a copy of a KeyBinding with a different key code and the same set of
the modifier key, in which case there is a utility method:

- public KeyBinding **withNewKeyCode**(KeyCode)



### SkinInputMap

This class provides a secondary repository for the event handlers and key mappings created by the skin.  The skin is expected to construct an instance of it attach to the control's main InputMap via InputMap.setSkinInputMap() in Skin.install().

Most of the skins create behaviors that contain state, see the
[Control Class Hierarchy](https://github.com/andy-goryachev-oracle/Test/blob/main/doc/Controls/ControlsClassHierarchy.md)
[1].  
Most frequently used skin input map is therefore SkinInputMap.Stateful, which can be obtained by calling `SkinInputMap.create()`.

For skins with stateless behaviors, a single instance of SkinInputMap.Stateless can be used, obtained via `SkinInputMap.createStateless()`.

The base SkinInputMap class provides the following public methods:

- public static SkinInputMap.Stateful **create**()
- public static <C extends Control> SkinInputMap.Stateless<C> **createStateless**()

- public void **addHandler**(EventCriteria, boolean consume, EventHandler)
- public void **addHandler**(EventType, boolean consume, EventHandler)
- public void **duplicateMapping**(KeyBinding, KeyBinding)
- public Set<KeyBinding> **getKeyBindings**()
- public Set<KeyBinding> **getKeyBindingsFor**(FunctionTag)
- public void **registerKey**(KeyBinding, FunctionTag)
- public void **registerKey**(KeyCode, FunctionTag)

A Stateful variant adds the following methods:

- public void **register**(FunctionTag, KeyBinding, Runnable)
- public void **register**(FunctionTag, KeyCode, Runnable)
- public void **registerFunction**(FunctionTag, BooleanSupplier)
- public void **registerFunction**(FunctionTag, Runnable)

A Stateless variant adds the following methods, which use interfaces FHandler<C> and FHandlerConditional<C>
intended to pass the reference to the source Control to the handling code:

- public void **register**(FunctionTag, KeyBinding, FHandler<C>)
- public void **register**(FunctionTag, KeyCode, FHandler<C>)
- public void **registerFunction**(FunctionTag, FHandler<C>)
- public void **registerFunction**(FunctionTag, FHandlerConditional<C>)



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

In order to fully utilize the capability of the InputMap, a new Control should:

- declare a set of FunctionTags corresponding to the customizable methods
- optionally provide public methods in the new control which delegate to the corresponding FunctionTags using `Control.execute()1
- implement the behavior either in the skin class, or the class created by the skin which extends BehaviorBase, or by a class that provides a stateless behavior
- populate SkinInputMap with the default key mappings and the event handlers



## Alternatives

An application developer that needs a custom control either needs to craft a completely new implementation, or jerry-rig special event filters and handlers, hoping that their custom code won't interfere with the rest of the Control behavior and functionality (accessibility, focus traversal, etc.)

Please also refer to
[Discussion](https://github.com/andy-goryachev-oracle/Test/blob/main/doc/InputMap/InputMapV3-Discussion.md)
[3].



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
- [6] [JDK-8231245](https://bugs.openjdk.org/browse/JDK-8231245) Controls' behavior must not depend on sequence of handler registration
