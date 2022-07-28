package goryachev.research;

import java.util.function.Function;

public class AControl {
    public void setSkin(Function<AControl,ASkin> f) throws IllegalArgumentException {
        ASkin s = f.apply(this);
        if(f == null) {
            throw new IllegalArgumentException();
        }
    }
    
    void test() {
        setSkin(BSkin::new);
    }
    
    class BSkin implements ASkin {
        public BSkin(AControl c) {
            
        }
    }
}
