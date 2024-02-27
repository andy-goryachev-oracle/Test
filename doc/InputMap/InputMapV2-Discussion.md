# Public InputMap Discussion Points

This document captures discussion around the [InputMap](InputMapV2.md) proposal.



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
