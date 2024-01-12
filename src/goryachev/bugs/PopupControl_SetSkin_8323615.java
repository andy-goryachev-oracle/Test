package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Tooltip;
import javafx.scene.control.skin.TooltipSkin;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import goryachev.util.FX;

/**
 * https://bugs.openjdk.org/browse/JDK-8323615
 * Note that 'DISPOSE CALLED' is never printed and 'popupControl.getSkin() == skin2' does not evaluate to true, although we would expect it.
 */
public class PopupControl_SetSkin_8323615 extends Application {
    @Override
    public void start(Stage stage) {
        Tooltip tt = new Tooltip("yo tooltip");
        tt.setId("S1");
        tt.getStyleClass().setAll("test-popup");
        tt.skinProperty().addListener((s, p, c) -> {
            p("skin: " + c);
        });

        CustomSkin skin1 = new CustomSkin(tt);
        tt.setSkin(skin1);

        CustomSkin skin2 = new CustomSkin(tt);
        tt.setSkin(skin2);

        p(tt.getSkin() == skin2);

        Label la = new Label("hover over this label to show the tooltip");
        la.setTooltip(tt);
        la.setMinSize(200, 100);
        la.setOnMousePressed((ev) -> {
            tt.setSkin(new CustomSkin(tt));
        });

        Scene scene = new Scene(new BorderPane(la));
        stage.setScene(scene);
        stage.show();

        FX.runLater(5_000, () -> {
            String css = ".tooltip { -fx-skin:\"" + CustomSkin.class.getName() + "\"; }";
            p(css);
            scene.getStylesheets().add(FX.encodeStylesheet(css));
        });
    }

    private static void p(Object x) {
        System.out.println(x);
    }

    public static class CustomSkin extends TooltipSkin {
        public CustomSkin(Tooltip t) {
            super(t);
        }

        @Override
        public void dispose() {
            p("DISPOSE CALLED");
            super.dispose();
        }
    }
}