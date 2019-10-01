package javax0.geci.record;

import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.lexeger.JavaLexed;
import javax0.geci.tools.AbstractFilteredFieldsGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciReflectionTools;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static javax0.geci.lexeger.Lex.of;
import static javax0.geci.lexeger.LexpressionBuilder.group;
import static javax0.geci.lexeger.LexpressionBuilder.identifier;
import static javax0.geci.lexeger.LexpressionBuilder.list;
import static javax0.geci.lexeger.LexpressionBuilder.modifier;
import static javax0.geci.lexeger.LexpressionBuilder.type;
import static javax0.geci.lexeger.LexpressionBuilder.zeroOrMore;

public class Record extends AbstractFilteredFieldsGenerator {

    @Override
    public String mnemonic() {
        return "record";
    }

    public void process(Source source, Class<?> klass, CompoundParams global, Field[] fields, Segment segment) {
        try (final var javaLexed = new JavaLexed(source)) {
            for (final var field : fields) {
                if ((field.getModifiers() & Modifier.FINAL) == 0) {
                    javaLexed.find(list(zeroOrMore(group("modifiers"), modifier(~Modifier.FINAL)), type(group("fieldType")), identifier(field.getName())))
                        .fromStart()
                        .replaceWith(of("final "), javaLexed.group("modifiers"), of(" "), javaLexed.group("fieldType"), of(" " + field.getName()));
                }
            }
            segment.write_r("public " + klass.getSimpleName() + "(");
            var sep = "";
            for (final var field : fields) {
                segment.write(sep + GeciReflectionTools.getGenericTypeName(field.getGenericType()) + " " + field.getName());
                sep = ", ";
            }
            segment.write(") {");
            for (final var field : fields) {
                segment.write("this." + field.getName() + " = " + field.getName() + ";");
            }
            segment.write_l("}");
        }
    }
}
