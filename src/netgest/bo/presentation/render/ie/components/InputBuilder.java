/*Enconding=UTF-8*/
package netgest.bo.presentation.render.ie.components;

import java.io.IOException;
import java.io.PrintWriter;

import netgest.bo.presentation.render.HTMLCommon;
import netgest.bo.presentation.render.PageController;
import netgest.bo.presentation.render.elements.Input;
import netgest.bo.runtime.boRuntimeException;

/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class InputBuilder {
    private static final char[] INPUT = "<INPUT".toCharArray();
    private static final char[] TYPE = " type='".toCharArray();
    private static final char[] NAME = " name='".toCharArray();
    private static final char[] VALUE = " value=".toCharArray();
    private static final char[] INPUT_END = "</INPUT>".toCharArray();

    /**
     *
     * @Company Enlace3
     * @since
     */
    public InputBuilder() {
    }

    public static void writeInput(PrintWriter out, Input input,
        PageController control) throws IOException, boRuntimeException {
        if (input.getCode() != null) {
            out.write(input.getCode().toCharArray());

            return;
        }

        out.write(INPUT);

        if ((input.getName() != null) && !"".equals(input.getName())) {
            out.write(NAME);
            out.write(input.getName().toCharArray());
            out.write(HTMLCommon.SYMBOL_PLICA);
        }

        if ((input.getType() != null) && !"".equals(input.getType())) {
            out.write(TYPE);
            out.write(input.getType().toCharArray());
            out.write(HTMLCommon.SYMBOL_PLICA);
        }

        if ((input.getValue() != null) && !"".equals(input.getValue())) {
            out.write(VALUE);
            String v = input.getValue();
            if(v != null && v.indexOf("'") > -1)
            {
                out.write(HTMLCommon.SYMBOL_QUOTE);
                out.write(input.getValue().toCharArray());
                out.write(HTMLCommon.SYMBOL_QUOTE);
            }
            else
            {
                out.write(HTMLCommon.SYMBOL_PLICA);
                out.write(input.getValue().toCharArray());
                out.write(HTMLCommon.SYMBOL_PLICA);
            }
        }
        out.write(HTMLCommon.SYMBOL_GT);
        out.write(HTMLCommon.UTIL_NEW_LINE);
        out.write(INPUT_END);
    }
}
