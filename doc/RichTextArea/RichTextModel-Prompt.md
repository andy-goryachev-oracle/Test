# RichTextModel - Prompt

Andy Goryachev

<andy.goryachev@oracle.com>


## Summary

Adding the `prompt` property to the `RichTextArea` to support showing a simple text prompt or complex graphic
when the model is `null` or empty.

When the prompt is shown, it communicates its textual representation via the `AccessibleAttribute.TEXT` attribute,
or, in case of a `Node`, by delegating to that Node's `queryAccessibleAttribute()` method.



## Problem

This feedback for the `RichTextArea` incubator came from an external developer: ability to set prompt text
similarly to `TextArea.setPromptText()`.

Going a bit further (in the spirit of "rich text"), it should be possible to set not only a simple text, but also complex graphic, such as
icon + text or even a button.


## Solution

The solution is to add a `prompt` property of type `Object`, which can be either:

- a `String`
- a `Node`
- a `Supplier<Object>`
- an `Object`
- a `null`

A `String` value set results in the text being shown inside a semi-transparent Label when the prompt is shown.

A `Node` value will be shown directly.

An `Object` value works similarly to the `String` one shown that object's `toString()` value.

A `null` value disables the prompt.

To add more flexibility, `setPrompt(Supplier<Object>)` method enables the lazy initialization of the prompt or the use
of lambda expression.  The value obtained by this method will be interpreted using the same rules as listed above
(including `null`).


## Specification

```java
    /*
     * The prompt to display in the {@code RichTextArea}.  The prompt becomes visible when the model is
     * either {@code null} or empty.
     * <p>
     * For maximum flexibility, this property accepts an {@code Object} which can be either
     * <ul>
     *   <li>a {@code String}
     *   <li>a {@code Node}
     *   <li>a {@link Supplier}
     * </ul>
     * A {@code null} value set or supplied removes the prompt.
     * If the property is set to a {@code Node} value, or a {@code Supplier} that supplies a {@code Node},
     * that node will be used.  For any other type, its {@code toString()} value will be used.
     *
     * @defaultValue An empty String
     * @return the property
     * @since 28
     */
    public final ObjectProperty promptProperty() {

    public final Object getPrompt() {

    public final void setPrompt(Object x) {
    
    /**
     * Sets the value of the property {@link #prompt}.
     * For maximum flexibility, the supplied values can be either
     * <ul>
     *   <li>a {@code String}
     *   <li>a {@code Node}
     *   <li>{@code null}
     * </ul>
     * A {@code null} value supplied removes the prompt.
     *
     * @param sup the prompt value supplier
     * @since 28
     */
    public final void setPrompt(Supplier<Object> sup) {
```

