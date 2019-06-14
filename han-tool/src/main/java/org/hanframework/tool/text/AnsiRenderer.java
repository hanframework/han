package org.hanframework.tool.text;
import static org.hanframework.tool.text.Ansi.Color;
import static org.hanframework.tool.text.Ansi.Attribute;
/**
 * @author liuxin
 * @version Id: AnsiRenderer.java, v 0.1 2019-06-14 09:17
 */

public class AnsiRenderer {
    public static final String BEGIN_TOKEN = "@|";
    private static final int BEGIN_TOKEN_LEN = 2;
    public static final String END_TOKEN = "|@";
    private static final int END_TOKEN_LEN = 2;
    public static final String CODE_TEXT_SEPARATOR = " ";
    public static final String CODE_LIST_SEPARATOR = ",";

    public AnsiRenderer() {
    }

    public static String render(String input) throws IllegalArgumentException {
        StringBuffer buff = new StringBuffer();
        int i = 0;

        while(true) {
            int j = input.indexOf("@|", i);
            if (j == -1) {
                if (i == 0) {
                    return input;
                }

                buff.append(input.substring(i, input.length()));
                return buff.toString();
            }

            buff.append(input.substring(i, j));
            int k = input.indexOf("|@", j);
            if (k == -1) {
                return input;
            }

            j += 2;
            String spec = input.substring(j, k);
            String[] items = spec.split(" ", 2);
            if (items.length == 1) {
                return input;
            }

            String replacement = render(items[1], items[0].split(","));
            buff.append(replacement);
            i = k + 2;
        }
    }

    private static String render(String text, String... codes) {
        Ansi ansi = Ansi.ansi();
        String[] arr$ = codes;
        int len$ = codes.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            String name = arr$[i$];
            AnsiRenderer.Code code = AnsiRenderer.Code.valueOf(name.toUpperCase());
            if (code.isColor()) {
                if (code.isBackground()) {
                    ansi = ansi.bg(code.getColor());
                } else {
                    ansi = ansi.fg(code.getColor());
                }
            } else if (code.isAttribute()) {
                ansi = ansi.a(code.getAttribute());
            }
        }

        return ansi.a(text).reset().toString();
    }

    public static boolean test(String text) {
        return text != null && text.contains("@|");
    }

    public static enum Code {
        BLACK(Color.BLACK),
        RED(Color.RED),
        GREEN(Color.GREEN),
        YELLOW(Color.YELLOW),
        BLUE(Color.BLUE),
        MAGENTA(Color.MAGENTA),
        CYAN(Color.CYAN),
        WHITE(Color.WHITE),
        FG_BLACK(Color.BLACK, false),
        FG_RED(Color.RED, false),
        FG_GREEN(Color.GREEN, false),
        FG_YELLOW(Color.YELLOW, false),
        FG_BLUE(Color.BLUE, false),
        FG_MAGENTA(Color.MAGENTA, false),
        FG_CYAN(Color.CYAN, false),
        FG_WHITE(Color.WHITE, false),
        BG_BLACK(Color.BLACK, true),
        BG_RED(Color.RED, true),
        BG_GREEN(Color.GREEN, true),
        BG_YELLOW(Color.YELLOW, true),
        BG_BLUE(Color.BLUE, true),
        BG_MAGENTA(Color.MAGENTA, true),
        BG_CYAN(Color.CYAN, true),
        BG_WHITE(Color.WHITE, true),
        RESET(Attribute.RESET),
        INTENSITY_BOLD(Attribute.INTENSITY_BOLD),
        INTENSITY_FAINT(Attribute.INTENSITY_FAINT),
        ITALIC(Attribute.ITALIC),
        UNDERLINE(Attribute.UNDERLINE),
        BLINK_SLOW(Attribute.BLINK_SLOW),
        BLINK_FAST(Attribute.BLINK_FAST),
        BLINK_OFF(Attribute.BLINK_OFF),
        NEGATIVE_ON(Attribute.NEGATIVE_ON),
        NEGATIVE_OFF(Attribute.NEGATIVE_OFF),
        CONCEAL_ON(Attribute.CONCEAL_ON),
        CONCEAL_OFF(Attribute.CONCEAL_OFF),
        UNDERLINE_DOUBLE(Attribute.UNDERLINE_DOUBLE),
        UNDERLINE_OFF(Attribute.UNDERLINE_OFF),
        BOLD(Attribute.INTENSITY_BOLD),
        FAINT(Attribute.INTENSITY_FAINT);

        private final Enum n;
        private final boolean background;

        private Code(Enum n, boolean background) {
            this.n = n;
            this.background = background;
        }

        private Code(Enum n) {
            this(n, false);
        }

        public boolean isColor() {
            return this.n instanceof Color;
        }

        public Color getColor() {
            return (Color)this.n;
        }

        public boolean isAttribute() {
            return this.n instanceof Attribute;
        }

        public Attribute getAttribute() {
            return (Attribute)this.n;
        }

        public boolean isBackground() {
            return this.background;
        }
    }
}
