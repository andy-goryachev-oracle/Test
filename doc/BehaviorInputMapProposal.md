# Behavior / InputMap Redesign Proposal

Andy Goryachev <andy.goryachev@oracle.com>


## Summary

Currently, it is nearly impossible to customize or extend FX control behavior, as neither BehaviorBase nor InputMap are public.

The current non-public implementation does not allow for an easy customization by the user, such as changing the key bindings, remapping an existing key binding to a different function, customizing the function mapped to a key binding, or reverting such mappings to the initial state dynamically.



## Goals

The goal of this proposal is to introduce an InputMap property in Control in order to address two separate use cases:
1. add ability of applications to customize the keyboard binding in the existing controls such as TextArea or ComboBox
2. add ability of application developers to write a custom third-party control that uses InputMap

The InputMap properties are mutable and can be modified at run time, allowing for dynamic control of key mappings.



## Non-Goals

It is not the goal of this proposal to require behaviors for all existing Controls be made public at the same time since a gradual transition is possible.

It is not the goal to make the concrete behaviors of existing controls public (but nothing precludes us from doing so in the future). 

Neither does this proposal introduce or require an equivalent of Swing Actions.



## Motivation

The main motivation behind this proposal is to provide a mechanism for extending functionality of FX Controls and offering a greater flexibility in working with key bindings and event handlers, including being able to remap keys and functions dynamically.

Equally important, the same mechanism should also be offered to third party controls, especially for the capability to redefine the default functions and key mappings, or to revert custom key mappings back to their default settings.  Historically, going outside of what is provided by JavaFX core (i.e. creating a new component or subclassing one) has always been a rather costly endeavor due to opaque nature of JavaFX, so this proposal tries reduce the pain by providing APIs useful for third party developers. 

Several JBS tickets ask for making the behavior and related classes public and other related functionality:

- [JDK-8091189](https://bugs.openjdk.org/browse/JDK-8091189) Move BehaviorBase into public API
- [JDK-8092211](https://bugs.openjdk.org/browse/JDK-8092211) Promote all skin and behavior classes of the default controls to the public API
- [JDK-8186137](https://bugs.openjdk.org/browse/JDK-8186137) [JavaFX 9] TextFieldSkin, MenuButtonSkinBase - behavior can't be passed

In addition to making the BehaviorBase and the InputMap classes public, this proposal enables a wide range of operations with the key mappings, including runtime remapping and reverting to the default behavior.


## Description

The first public API being introduced is **InputMap**, via Control.getInputMap().  An InputMap maps the user input events to methods in the control's behavior class or methods defined by the user.

The purpose of InputMap is to enable a wide range of operations performed by both the skin and the user:
- map a key binding to a function, either default one or supplied by the user
- un-map a key binding
- map a new function to an existing key binding
- obtain the default behavior function
- ensure that user-defined mappings overwrite default ones and survive a skin change


To achieve that, the InputMap utilizes a two-stage lookup.  First, the key binding (or input even in general) is mapped to a **FunctionTag** - a method identifier declared by the corresponding Control.  Then, if such a mapping exists, the actual function (a Runnable) is obtained and executed.  This approach allow for to customizing the key bindings separately from customizing the behavior.

InputMap provides the following public methods:
- void **registerKey**(KeyBinding, FunctionTag)
- void **registerFunction**(FunctionTag, Runnable)
- Runnable **getFunction**(FunctionTag)
- Runnable **getFunction**(KeyBinding)
- Runnable **getDefaultFunction**(FunctionTag)
- Runnable **getDefaultFunction**(KeyBinding)
- FunctionTag **getFunctionTag**(KeyBinding)
- Set<KeyBinding> **getKeyBindings**()
- void **resetKeyBindings**()
- void **restoreDefaultKeyBinding**(KeyBinding)
- void **restoreDefaultFunction**(FunctionTag)
- void **unbind**(KeyBinding)

The concrete behavior must extend the **BaseBehavior** class.  It is expected that behavior classes are instantiated by the Skin.  The lifecycle of a behavior starts with BaseBehavior.install(Skin) called from Skin.install(), and terminates with BaseBehavior.dispose() called from Skin.dispose().

During installation, the actual behavior registers event mappings that are specific to that behavior.  It is important to note that any user-defined mappings added at the Control level (since InputMap is a property of the Control) take priority over behavior-specific mappings, so a null skin, or changing a skin has no effect on the user-defined mappings.  All mappings added by the install() method will be removed by the dispose().

BaseBehavior provides the following public methods: 
- public void **install**()
- public void **dispose**()

It also provides a number of protected methods intended to be called by the behavior implementation in BehaviorBase.install():
- protected void **registerFunction**(FunctionTag, Runnable)
- protected void **registerKey**(KeyBinding, FunctionTag)
- protected void **registerKey**(KeyCode, FunctionTag)
- protected void **register**(FunctionTag, KeyBinding, Runnable)
- protected void **register**(FunctionTag, KeyCode, Runnable)
- protected void **addHandler**(EventCriteria, boolean, EventHandler)
- protected void **addHandler** (EventType, boolean, EventHandler)
- protected void **addHandler** (EventType, EventHandler)
- protected void **addHandlerLast**(EventType, boolean, EventHandler)
- protected void **addHandlerLast**(EventType, EventHandler)
- protected void **duplicateMapping**(KeyBinding, KeyBinding)
- protected void **setOnKeyEventEnter**(Runnable)
- protected void **setOnKeyEventExit**(Runnable)

Finally, the **Control** base class declares two new methods:
- public InputMap **getInputMap**()
- protected void **execute**(FunctionTag)

For custom skin/component developers, we now can describe the recommended (although not required) structure of programmatic access to the behavior APIs.

Each Control declares public static final FunctionTags serving as descriptors for the functionality provided by the behavior.  Control also declares public methods for each FunctionTag.  Typically, these methods do not implementation of the required functionality, but rather invoke Control.execute(FunctionTag).

The actual functionality is provided by the corresponding Behavior.  A concrete Behavior implementation would declare methods corresponding to the Control's FunctionTags, thus providing the default behavior.

This design might have an unexpected side effect of the basic functionality expressed by the public methods in Control not being available with a null Skin (a way to work around this limitation will be discussed later).  Why Skin is allowed to be null in a graphical user interface component is unclear.



## Alternatives

An application developer that needs a custom control either needs to craft a completely new implementation, or jerry-rig special event filters and handlers, hoping that their custom code won't interfere with the rest of the Control behavior and functionality (accessibility, focus traversal, etc.)



## Risks and Assumptions

The major risk is that the new behaviors for existing controls which extend BehaviorBase might introduce a regression because:
a) no test suite currently exists that exhaustively exercises every key mapping on every platform.
b) no headful tests currently exist that go beyond simple features and try to test system as a whole, complete with focus traversal, popups, and so on.
c) extensive manual testing will be needed
d) default functions may not be available with a null Skin.

The reason the default functions will not be available with a null Skin is that the default functionality is implemented by the control behavior, which is instantiated by the Skin.  A Skin is allowed to be null, so naturally all the key mappings and event handlers will be unregistered.  We don't want to break compatibility with the earlier versions, so these scenarios must include logic to redirect to the existing functionality when the skin is null.



## Dependencies

None.



## Code Example for Application Developers

With the proposed solution, there should be no difference in the way the InputMap and the behavior is coded and used.  We'll attempt to illustrate basic operations using a hypothetical third party control, let's call it RichTextArea:

    public class RichTextArea extends Control

From the Control class, getInputMap() method exposes the control's InputMap property:

    public InputMap getInputMap () {

In order to use the two-level indirection offered by the InputMap, this countrol would declare a number of FunctionTags, corresponding to the functionality available through this control.  Let's consider one such tag:

    public static final FunctionTag BACKSPACE = new FunctionTag();

In addition to the tag, there is a public method that allows to access this function programmatically:

    public void backspace() {
        execute(BACKSPACE);
    }

Application developers can customize the behavior by redefining the function mapped to any FunctionTag declared by the control, dynamically, and in some cases even without the need to subclass:

	control.getInputMap().registerFunction(BACKSPACE, () -> customBackspaceFunction());

The default function can be reverted to the default value at run time:

	control.getInputMap().restoreDefaultFunction(BACKSPACE);

The InputMap enables the application developer to modify the key mappings for the given function, for example, to map the function to a different key binding:

	control.getInputMap().registerKey(KeyBinding.of(KeyCode.DELETE), BACKSPACE);

or unmap an existing key binding:

	control.getInputMap().unbind(KeyCode.DELETE);

or restore the default key binding:

	control.getInputMap().restoreDefaultKeyBinding(BACKSPACE);



Code Example for Skin / Custom Component Developers

A typical JavaFX control creates its behavior as a part of its Skin.  The Skin constructor creates the behavior, which is then gets installed in Skin.install() (and uninstalled in Skin.dispose()):

	public class RichTextAreaSkin extends SkinBase<RichTextArea> {
	    private final RichTextAreaBehavior behavior;
	
	    public RichTextAreaSkin(RichTextArea control) {
	        super(control);
	        behavior = new RichTextAreaBehavior(control);
	    }
	
	    @Override
	    public void install() {
	        behavior.install();
	    }
	
	    @Override
	    public void dispose() {
	        if (getSkinnable() != null) {
	            behavior.dispose();
	            super.dispose();
	        }
	    }

The behavior extends BehaviorBase class in order to access a set of protected methods which provide behavior-specific functionality of the InputMap not available outside of the Skin:

	public class RichTextAreaBehavior extends BehaviorBase<RichTextArea>

The actual installation of the behavior happens in Skin.install(), whose contract guarantees that, in the case of switching a skin, the previous skin (and its behavior) is completely uninstalled.

The installation hooks up the function tags to their default functions (typically provided by the behavior), maps key bindings to the function tags, adds event handlers, and, optionally, lambdas that get executed before and after a key press:

    // functions
    registerFunction(RichTextArea.BACKSPACE, this::backspace);
    
    // keys
    registerKey(KeyCode.BACK_SPACE, RichTextArea.BACKSPACE);

    setOnKeyEventEnter(() -> setSuppressBlink(true));
    setOnKeyEventExit(() -> setSuppressBlink(false));
    addHandler(KeyEvent.KEY_TYPED, this::handleKeyTyped);
    addHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, this::contextMenuRequested);

It's worth noting that the mappings set by the user or application via public InputMap methods take precedence over those set by the skin/behavior.

Finally, all the skin- and behavior-specific mappings and handlers added in install() get automatically reverted by Skin.dispose() which calls BehaviorBase.dispose().



## Discussion Points

There exists a number of open issues and design variants that might warrant a further discussion:

### Functionality of a Control with a null skin
It is possible to have a null skin.  It is also possible for have some of the control's methods to work without a skin, for example TextInputControl.selectRange().  Moving these methods to the behavior would create a regression.
Possible solution: delegate to behavior if skin is not null, default to the control's legacy implementation otherwise.

### Does FunctionTag constructor need a String argument?
Pros: good for debug output
Cons:
1.	makes declaration longer
2.	IDEs may not use toString() in the Variables view
3.	there is hardly a need to print these

### How to create a new behavior when extending Skin?
Behavior is usually tightly coupled with its skin.  This might pose a problem for custom Skins that try to extend existing skins that instantiate the corresponding behavior in the constructor.
Possible solution: design for a protected createBehavior() method to be invoked from the constructor.

### How to account for platform specific aspects of behavior?
There is enough behavioral differences among the three popular platforms to warrant creation of isWindows(), isMac(), and isLinux() methods in either BehaviorBase or Platform.

### KeyBinding.notForMac()
Simplifies writing key binding initialization code:
 
        registerKey(KeyBinding.with(KeyCode.LEFT).shift().control().notForMac().build(), RichTextArea.SELECT_WORD_LEFT);
        registerKey(KeyBinding.with(KeyCode.LEFT).shift().option().forMac().build(), RichTextArea.SELECT_WORD_LEFT);

These could be replaced by platform-specific if-else blocks.

### Would InputMap.unbind(FunctionTag) be useful?
Pros: symmetry
Cons: registerFunction(FunctionTag, () -> { }) would work just fine
