package fr.umlv.tatoo.samples.java.javac;

import com.sun.tools.javac.parser.Parser;
import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.util.Context;

public class TatooParserFactory extends ParserFactory {
    public static void preRegister(final Context context) {
        context.put(parserFactoryKey, new Context.Factory<ParserFactory>() {
            public ParserFactory make() {
                return new TatooParserFactory(context);
            }
        });
    }

    protected TatooParserFactory(Context context) {
        super(context);
    }

    @Override
    public Parser newParser(CharSequence input, boolean keepDocComments, boolean keepEndPos, boolean keepLineMap) {
        return new TatooParser(this, input, keepDocComments, keepEndPos, keepLineMap);
    }
}
