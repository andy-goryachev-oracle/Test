## Summary

Adding the `SkinInputMap` class and an optional `BehaviorBase` utility class aimed to simplify development of skins
for existing and custom controls.


## Problem

While the `Skin` is a public API, there is no convenient mechanism for registering the skin's event handlers.
Even worse, changing the skin after the application added its own event handlers changes the invocation order
of handlers, leading to [3].

Developers of custom skins have to invent their own mechanisms to register and unregister handlers,
use their own implementation of platform detectors (`isWindows()`).  There is also no convenient public API
for adding key event handlers / key bindings.



## Solution

The first step in solving the problem was introduction of the `InputMap` incubator in jfx24 [0].
The next step is to provide a similar public API for skins, in the form of the `SkinInputMap`.

The purpose of the `SkinInputMap` class is to provide convenient APIs for skins to register event handlers
and key bindings which will be registered with the `Control`'s `InputMap` at the time of `Skin.install()`.
The `SkinInputMap` also guarantees that these handlers and key bindings will be invoked at lower priority
than the application handlers and bindings, regardless of the order of skin initialization.

As an added bonus, the `BehaviorBase` provides convenience methods that simplify implementation of
custom (and standard) skins.

For complete picture that includes migration of a representative subset of simple and complex `Control`s,
please refer to the InputMap proposal [1] and draft pull request [2].


## Specification

### SkinInputMap

This class provides a secondary repository for the event handlers and key mappings created by the skin.
The skin constructs an instance of this class and then registers it with the control by calling
`InputMap.setSkinInputMap()` inside `Skin.install()`.

Most skins create stateful behavior implementaions, see the
[Control Class Hierarchy](https://github.com/andy-goryachev-oracle/Test/blob/main/doc/Controls/ControlsClassHierarchy.md) .  
Most frequently used skin input map is therefore SkinInputMap.Stateful, which can be obtained by calling `SkinInputMap.create()`.

For skins with stateless behaviors, a single instance of SkinInputMap.Stateless can be used, obtained via `SkinInputMap.createStateless()`.

The base SkinInputMap class provides the following public methods:

- public static SkinInputMap.Stateful **create**()
- public static <C extends Control> SkinInputMap.Stateless<C> **createStateless**()
- public void **addHandler**(EventCriteria, EventHandler)
- public void **addHandler**(EventType, EventHandler)
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

This convenience class is intended to simplify creation of stateful behaviors, by maintaining an instance of `SkinInputMap` and adding helpful methods for registering key mappings and event handlers.  It enables easy integration of the default functionality into its owning `Skin` and its `install()` method:

```java
    @Override
    public void install() {
        super.install();
        setSkinInputMap(behavior.getSkinInputMap());
    }
```

Note: alternatively, the skin input map registration/deregistration can be moved to `Control.setSkin()`,
making the whole process automated.  Going this route necessitates adding a public `Skin.getSkinInputMap()` method.

`BehaviorBase` provides the following public methods:

- public final SkinInputMap.Stateful **getSkinInputMap**()
- public final void **traverseDown**()
- public final void **traverseLeft**()
- public final void **traverseNext**()
- public final void **traversePrevious**()
- public final void **traverseRight**()
- public final void **traverseUp**()

It also provides a number of protected methods intended to be called by the behavior implementation in `BehaviorBase.getSkinInputMap()`:

- protected final void **addHandler**(EventCriteria, EventHandler)
- protected final void **addHandler**(EventType, EventHandler)
- protected final void **duplicateMapping**(KeyBinding, KeyBinding)
- protected final C **getControl**()
- protected final boolean **isLinux**()
- protected final boolean **isMac**()
- protected final boolean **isWindows**()
- protected void **populateSkinInputMap**()
- protected final void **register**(FunctionTag, KeyBinding, BooleanSupplier)
- protected final void **register**(FunctionTag, KeyBinding, Runnable)
- protected final void **register**(FunctionTag, KeyCode, Runnable)
- protected final void **registerFunction**(FunctionTag, BooleanSupplier)
- protected final void **registerFunction**(FunctionTag, Runnable)
- protected final void **registerKey**(KeyBinding, FunctionTag)
- protected final void **registerKey**(KeyCode, FunctionTag)



#### Stateless (Static) Behaviors

A number of Controls have behavior classes that require no state: examples are `DateCell`, `TabPane`, and a few more [2].
For these situations, a single static `SkinInputMap` instance might be sufficient, eliminating the need for per-instance behavior objects.

This example illustrates the use of a static behavior in the context of `TabPaneSkin`:

```java
    @Override
    public void install() {
        super.install();
        // install stateless behavior
        TabPaneBehavior.install(getSkinnable());
    }
```

The stateless behavior is implemented in the `TabPaneBehavior` in [2]
(provided here for illustration purposes only, as it is not part of the public API):

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

### Alternatives

The boilerplate can be further reduced by moving the registration of the skin input map inside of `Control.setSkin()`
instead of having to manually invoke `InputMap.setSkinInputMap(SkinInputMap)`.  This will require adding
a new public API: `Skin.getSkinInputMap()`.


## References

- [0] [Public InputMap (Incubator)](https://bugs.openjdk.org/browse/JDK-8343646)
- [1] https://github.com/andy-goryachev-oracle/Test/blob/main/doc/InputMap/InputMapV3.md
- [2] [8314968: Public InputMap](https://github.com/openjdk/jfx/pull/1495)
- [3] [JDK-8231245](https://bugs.openjdk.org/browse/JDK-8231245) Controls' behavior must not depend on sequence of handler registration
