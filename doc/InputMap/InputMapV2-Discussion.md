# Public InputMap Discussion Points

This document captures discussion around the [InputMap](InputMapV2.md) proposal (in no particular order).



## Functionality of a Control with a null skin

It is possible to have a null skin.  It is also possible for have some of the control's methods to work without a skin, for example TextInputControl.selectRange().  Moving these methods to the behavior would create a regression.
Possible solution: delegate to behavior if skin is not null, default to the control's legacy implementation otherwise.



## Does FunctionTag constructor need a String argument?

Pros: good for debug output
Cons:
1.	makes declaration longer
2.	IDEs may not use toString() in the Variables view
3.	there is hardly a need to print these



## How to create a new behavior when extending Skin?

Behavior is usually tightly coupled with its skin.  This might pose a problem for custom Skins that try to extend existing skins that instantiate the corresponding behavior in the constructor.
Possible solution: design for a protected createBehavior() method to be invoked from the constructor.



## Do we want to add other shortcuts to KeyBinding?

KeyBinding provides convenience shortcuts for modifier combinations such as **ctrlShift**.  Do we want to add other popular combinations like **altShift** (see TextAreaBehavior)?


## Behavior Base

KR: Given the description in the "Role of the Behavior" section, I'll ask the question I asked with the first InputMap proposal: do you really need to add a public BehaviorBase class? And if it does, the input package seems an odd place for it.

AG: This class is intended for the skin developers, as it provides the skin input map and a number of convenience methods.
One does not have to use it, especially if the behavior is stateless.
As far as the package - it deals with the user input, it seems this package is the best place to put it.


## Common Base Class / Interface for InputMap and SkinInputMap

KR: can these be unified? Or at least have a common interface factored out?

AG: Even though both classes look similar, the context in which they are used is very different.
A base class or a common interface would indicate that there is some context where one can be used in place of another,
and that would be *bad(tm)*.



## FunctionHandler and FunctionHandlerConditional

KR: is this really the best way to do it?

AG: I would estimate about 80% of key mappings use the simple one (FunctionHandler), but we still have substantial
number of mappings that could use the other form where the handler determines whether it wants to handle and consume the event or not.
The goal is to simplify the skin code, and these two interfaces I think do their job well.  The name (FunctionHandlerConditional) 
can be improved though.


## Event Handling Propritization: Control or Node?

KR: Should the event handling priority be addressed at the Node level?

AG: Only Controls have skins, so we are dealing with more than one actor, and that's where we must have a guaranteed priority 
scheme.
Adding prioritization to the Node class would make it a much heavier change, to (in my opinion) a little benefit: I have not seen a use case
which cannot be handled by adding EventHandlers/EventFilters.


