# Public InputMap (v3) Discussion Points

This document captures discussion around the [InputMap](InputMapV3.md) proposal (in no particular order).



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

Behavior is usually tightly coupled with its skin.  The skin *may* decide to allow for easy extension by publishing
its extension API, which might include such things are getters for its control surfaces, making its behavior
class public and extensible.

If the Skin is not designed with extensibility in mind (as most of the current JavaFX skins are), all bets are off
and in the most general case a new skin with a new behavior must be written.

It might be possible to modify the behavior using the InputMap, as long as the new handlers do not interfere
with the original state or control surfaces (or use the runtime lookup to get access to those).


## Behavior Base

KR: Given the description in the "Role of the Behavior" section, I'll ask the question I asked with the first InputMap proposal: do you really need to add a public BehaviorBase class? And if it does, the input package seems an odd place for it.

AG: This class is intended for the skin developers, as it provides the skin input map and a number of convenience methods.
One does not have to use it, especially if the behavior is stateless.
As far as the package - it deals with the user input, it seems this package is the best place to put it.



## Event Handling Propritization: Control or Node?

KR: Should the event handling priority be addressed at the Node level?

AG: Only Controls have skins, so we are dealing with more than one actor, and that's where we must have a guaranteed priority 
scheme.
Adding prioritization to the Node class would make it a much heavier change, to (in my opinion) a little benefit: I have not seen a use case
which cannot be handled by adding EventHandlers/EventFilters.


