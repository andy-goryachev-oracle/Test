package goryachev.bugs;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;

/**
 * TextInputControl: Binding prompt text that contains linebreak causes exception.
 * https://bugs.openjdk.org/browse/JDK-8335587
 */
public class TextInputControl_PromptLinebreaks_8335587 extends Application {

    private static final String PROMPT_NO_LINEBREAKS = "Prompt w/o linebreaks";
    private static final String PROMPT_WITH_LINEBREAKS = "Prompt" + "\n" + "with" + "\n" + "linebreaks";

    @Override
    public void start(final Stage stage) throws Exception {
        final TextField textField = new TextField();

        final TextArea textArea = new TextArea();

        final StringProperty promptProperty = new SimpleStringProperty();

        System.out.println("\n---Setting a prompt without linebreaks: OK for both textfield and textarea");
        setPromptAndReadBack(textField, PROMPT_NO_LINEBREAKS);
        setPromptAndReadBack(textArea, PROMPT_NO_LINEBREAKS);

        System.out.println(
            "\n---Setting a prompt with linebreaks: OK for both textfield and textarea, linebreaks are stripped");
        setPromptAndReadBack(textField, PROMPT_WITH_LINEBREAKS);
        setPromptAndReadBack(textArea, PROMPT_WITH_LINEBREAKS);

        System.out.println("\n---Binding a prompt without linebreaks: OK for both textfield and textarea");
        bindPromptAndReadBack(textField, promptProperty, PROMPT_NO_LINEBREAKS);
        bindPromptAndReadBack(textArea, promptProperty, PROMPT_NO_LINEBREAKS);

        System.out.println(
            "\n---Binding a prompt with linebreaks: BROKEN for both textfield and textarea, causes exceptions");
        bindPromptAndReadBack(textField, promptProperty, PROMPT_WITH_LINEBREAKS);
        bindPromptAndReadBack(textArea, promptProperty, PROMPT_WITH_LINEBREAKS);

        System.exit(0);
    }

    private static void setPromptAndReadBack(final TextInputControl textInput, final String prompt) {
        textInput.setPromptText(prompt);

        System.out.println(textInput.getClass().getSimpleName() //
            + " SET: \"" + prompt + "\" READ BACK: \"" + textInput.getPromptText() + "\"");
    }

    private static void bindPromptAndReadBack(final TextInputControl textInput, final StringProperty promptProperty,
        final String prompt) {
        textInput.promptTextProperty().unbind();

        promptProperty.setValue(prompt);

        try {
            textInput.promptTextProperty().bind(promptProperty);

            System.out.println(textInput.getClass().getSimpleName() + //
                " BOUND TO: \"" + prompt + "\" READ BACK: \"" + textInput.getPromptText() + "\"");
        } catch (final RuntimeException ex) {
            // Exception in thread "JavaFX Application Thread" java.lang.RuntimeException:
            // TextField.promptText : A bound value cannot be set.
            // Exception in thread "JavaFX Application Thread" java.lang.RuntimeException:
            // TextArea.promptText : A bound value cannot be set.
            System.err.println(textInput.getClass().getSimpleName() //
                + " Failed to bind prompt \"" + prompt + "\"");
            ex.printStackTrace();
        } finally {

            // undo binding for next test
            textInput.promptTextProperty().unbind();
        }
    }
}