# Overflow Menu Graphic Property in the TabPaneSkin

Andy Goryachev

<andy.goryachev@oracle.com>


## Summary

Introduce a `menuGraphicFactory` property in the `TabPaneSkin` class eliminates the current limitation of this skin
in supporting menu item graphics other than an `ImageView` or `Label` with an `ImageView` graphic.



## Goals

The goals of this proposal are:

- to allow the application developers to customize the overflow menu items' graphic
- retain the backward compatibility with the existing application code
- clarify the behavior of the skin when the property is null (i.e. the current behavior)



## Non-Goals

The following are not the goals of this proposal:

- disable the overflow menu
- configure overflow menu graphic property via CSS
- add this property to the `TabPane` control itself



## Motivation

The existing `TabPaneSkin` does not allow the overflow menu to show graphic other than
an `ImageView` or `Label` with an `ImageView`.

This limitation makes it impossible for the application developer to use other graphic Nodes,
such as `Path` or `Canvas`, or in fact any other types.  The situation becomes even more egregious
when the tabs in the `TabPane` have no text.

Example:

```java
public class TabPaneGraphicFactoryExample {
    public void example() {
        Tab tab1 = new Tab("Tab1");
        tab1.setGraphic(createGraphic(tab1));

        Tab tab2 = new Tab("Tab2");
        tab2.setGraphic(createGraphic(tab2));

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(tab1, tab2);

        TabPaneSkin skin = new TabPaneSkin(tabPane);
        // set overflow menu factory with the same method as was used to create the tabs
        skin.setMenuGraphicFactory(this::createGraphic);
        tabPane.setSkin(skin);
    }

    // creates graphic Nodes for tabs as well as the overflow menu
    private Node createGraphic(Tab tab) {
        switch (tab.getText()) {
        case "Tab1":
            return new Circle(10);
        case "Tab2":
            return new Canvas(10, 10);
        default:
            return null;
        }
    }
}
```


## Description

The proposed solution adds the `menuGraphicFactory` property in the `TabPaneSkin` class:

```java
    /**
     * This property allows to control the graphic for the overflow menu items,
     * by generating graphic {@code Node}s when the menu is shown.
     * <p>
     * When this property is {@code null}, the menu provides only the basic graphic copied from the corresponding
     * {@link Tab} - either an {@link ImageView} or a {@link Label} with an {@link ImageView} as its graphic.
     * <p>
     * Changing this property while the menu is shown has no effect.
     *
     * @since 25
     * @defaultValue null
     */

    public final ObjectProperty<Function<Tab, Node>> menuGraphicFactoryProperty() {

    public final Function<Tab,Node> getMenuGraphicFactory() {

    public final void setMenuGraphicFactory(Function<Tab,Node> f) {
```


## Alternatives

Use `ImageView`-based graphic for tabs, which will be shown in the overflow menu.



## Risks and Assumptions

The risk is minimal, as the proposed solution adds a new property and retains the existing behavior when
this property is not set.

There might be a need to update the application code if the `TabPane` uses a custom skin extended from
the `TabPaneSkin` which declares a property or a method (or methods) with the same signature.



## Dependencies

None.



## References

- [JDK-8353599 TabPaneSkin: add 'menuGraphicFactory' property](https://bugs.openjdk.org/browse/JDK-8353599)
- https://mail.openjdk.org/pipermail/openjfx-dev/2025-April/053306.html
- https://mail.openjdk.org/pipermail/openjfx-dev/2025-April/053338.html

