package javax0.geci.docugen;

import javax0.geci.annotations.Geci;
import javax0.geci.api.Context;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractGeneratorEx;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A snippet collector.
 * <p>
 * This generator collects all the snippets that are between lines
 *
 * <pre>
 *     {@code
 *     //snippet snippetname
 *     }
 * </pre>
 *
 *  <p> and
 *
 * <pre>
 *     {@code
 *     //end snippet
 *     }
 * </pre>
 *
 * <p> The collected snippets get into a Map keyed by the names of the
 * snippets and can be used by other generators. Note that this
 * generator does not touch any source therefore it should not and
 * cannot be used alone.
 */
@Geci("configBuilder configurableMnemonic='snippetCollector' localConfigMethod=\"\"")
public class SnippetCollector extends AbstractGeneratorEx {
    public static final String CONTEXT_SNIPPET_KEY = "snippets";
    private Context ctx = null;
    private Map<String, Snippet> snippets;

    private static class Config {
        private Pattern snippetStart = Pattern.compile("//\\s*snipp?et\\s+(.*)$");
        private Pattern snippetEnd = Pattern.compile("//\\s*end\\s+snipp?et");
    }

    //snippet SnippetCollectorProcessExCode
    @Override
    public void processEx(Source source) throws Exception {
        SnippetBuilder builder = null;
        for (final var line : source.getLines()) {
            final var starter = config.snippetStart.matcher(line);
            if (builder == null && starter.find()) {
                builder = new SnippetBuilder(starter.group(1));
            } else if (builder != null) {
                final var stopper = config.snippetEnd.matcher(line);
                if (stopper.find()) {
                    snippets.put(builder.snippetName(), builder.build());
                    builder = null;
                } else {
                    builder.add(line);
                }
            }
        }
    }
    //end snippet

    @Override
    public boolean activeIn(int phase) {
        return phase == 0;
    }

    @Override
    public void context(Context context) {
        ctx = context;
        snippets = ctx.get(CONTEXT_SNIPPET_KEY, HashMap::new);
    }

    //<editor-fold id="configBuilder">
    private String configuredMnemonic = "snippetCollector";

    public String mnemonic() {
        return configuredMnemonic;
    }

    private final Config config = new Config();

    public static SnippetCollector.Builder builder() {
        return new SnippetCollector().new Builder();
    }

    public class Builder {
        public Builder snippetEnd(java.util.regex.Pattern snippetEnd) {
            config.snippetEnd = snippetEnd;
            return this;
        }

        public Builder snippetStart(java.util.regex.Pattern snippetStart) {
            config.snippetStart = snippetStart;
            return this;
        }

        public Builder mnemonic(String mnemonic) {
            configuredMnemonic = mnemonic;
            return this;
        }

        public SnippetCollector build() {
            return SnippetCollector.this;
        }
    }
    //</editor-fold>
}