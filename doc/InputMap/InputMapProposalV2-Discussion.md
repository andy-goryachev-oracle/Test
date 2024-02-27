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



## Bypass FunctionTag for Simple Key Mappings

Should we allow a simplified API when the app needs to map a new key to a new function, without the need to redefine it at run time, one that does not require a FunctionTag to be declared?  

```java
        // creates a new key binding mapped to an external function
        control.getInputMap().registerKey(KeyBinding.shortcut(KeyCode.W), () -> {
            System.out.println("console!");
        });
```

