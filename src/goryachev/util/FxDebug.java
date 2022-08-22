package goryachev.util;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.stage.Window;

public class FxDebug {
    private static PickResult pick;

    public static void attachNodeDumper(Window w) {
        w.addEventFilter(MouseEvent.ANY, (ev) -> {
            pick = ev.getPickResult();
        });
        w.addEventHandler(KeyEvent.KEY_PRESSED, (ev) -> {
            if (ev.getCode() == KeyCode.BACK_QUOTE) {
                StringBuilder sb = new StringBuilder();
                if (pick != null) {
                    Node n = pick.getIntersectedNode();
                    while (n != null) {
                        dump(sb, n);
                        n = n.getParent();
                    }
                }
                System.err.println(sb);
            }
        });
    }
    
    private static void dump(StringBuilder sb, Node n) {
        sb.append(n);
        sb.append(" ");
        sb.append(n.getPseudoClassStates());
        sb.append("\n");
    }
}
