package bugs.pkg;

import bugs.BASE;

public class TEST4 extends BASE {
    public void notRelevant() {
        register(this::base);
        register(this::basePublic);
    }
}
