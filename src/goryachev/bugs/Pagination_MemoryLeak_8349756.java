package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.control.Pagination;
import javafx.scene.control.skin.PaginationSkin;
import javafx.stage.Stage;

public class Pagination_MemoryLeak_8349756 extends Application {

    Pagination c;

    @Override
    public void start(Stage stage) {
        c = new Pagination();
        c.setSkin(new PaginationSkin(c));

        for (int i = 0; i < 5000; i++) {
            int mx = 2 + i % 100;
            c.setPageCount(mx);
            c.setCurrentPageIndex(i % mx);
            //            if (i % 100 == 0) {
            //                System.gc(); // This doesn't really matter
            //            }
        }
    }
}