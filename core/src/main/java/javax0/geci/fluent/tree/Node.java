package javax0.geci.fluent.tree;

import java.util.Map;

public abstract class Node {
    public static final int ONCE = 0x00000001;
    public static final int OPTIONAL = 0x00000002;
    public static final int ZERO_OR_MORE = 0x00000004;
    public static final int ONE_OF = 0x00000008;
    private static final Map<Integer, String> stringMap =
            Map.of(ONCE, "",
                    OPTIONAL, "?",
                    ZERO_OR_MORE, "*",
                    ONE_OF, "{OR}");
    private final int modifier;

    Node(int modifier) {
        this.modifier = modifier;
    }

    @Override
    public String toString() {
        return stringMap.get(modifier);
    }
}
