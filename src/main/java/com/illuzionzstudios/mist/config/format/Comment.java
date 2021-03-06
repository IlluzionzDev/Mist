/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */
package com.illuzionzstudios.mist.config.format;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This represents a comment in a {@link com.illuzionzstudios.mist.config.YamlConfig}
 * Usually declared by the "#" char
 */
@NoArgsConstructor
public class Comment {

    /**
     * A list of strings to display for the comment
     */
    @Getter
    private final List<String> lines = new ArrayList<>();

    /**
     * {@link CommentStyle} to use for this comment instance
     */
    @Getter
    @Setter
    private CommentStyle styling = null;

    //  -------------------------------------------------------------------------
    //  Construct our comment
    //  -------------------------------------------------------------------------

    public Comment(String... lines) {
        this(null, Arrays.asList(lines));
    }

    public Comment(List<String> lines) {
        this(null, lines);
    }

    public Comment(CommentStyle commentStyle, String... lines) {
        this(commentStyle, Arrays.asList(lines));
    }

    public Comment(CommentStyle commentStyle, List<String> lines) {
        this.styling = commentStyle;
        if (lines != null) {
            lines.forEach(s -> this.lines.addAll(Arrays.asList(s.split("\n"))));
        }
    }

    /**
     * This will load a set of {@link String} lines into a {@link Comment} object.
     * Will automatically detect the comment styling so build the right comment
     *
     * @param lines The string lines including styling
     * @return The built {@link Comment} object
     */
    public static Comment loadComment(List<String> lines) {
        CommentStyle style = CommentStyle.parseStyle(lines);
        int linePad = (style.drawBorder ? 1 : 0) + (style.drawSpace ? 1 : 0);
        int prefix = style.commentPrefix.length();
        int suffix = style.commentSuffix.length();
        return new Comment(style, lines.subList(linePad, lines.size() - linePad).stream().map(s -> s.substring(prefix, s.length() - suffix).trim()).collect(Collectors.toList()));
    }

    /**
     * @return Convert comment to lines separated by <char>"\n"</char>
     */
    @Override
    public String toString() {
        return lines.isEmpty() ? "" : lines.stream().collect(Collectors.joining("\n"));
    }

    /**
     * Write our comments to a writer
     *
     * @param output The writer to output to
     * @param offset The offset amount of chars indent
     * @param defaultStyle The default styling for comments
     * @throws IOException If couldn't write comments
     */
    public void writeComment(Writer output, int offset, CommentStyle defaultStyle) throws IOException {
        CommentStyle style = styling != null ? styling : defaultStyle;
        int minSpacing = 0, borderSpacing = 0;
        // first draw the top of the comment
        if (style.drawBorder) {
            // grab the longest line in the list of lines
            minSpacing = lines.stream().max((s1, s2) -> s1.length() - s2.length()).get().length();
            borderSpacing = minSpacing + style.commentPrefix.length() + style.commentSuffix.length();
            // draw the first line
            output.write((new String(new char[offset])).replace('\0', ' ') + (new String(new char[borderSpacing + 2])).replace('\0', '#') + "\n");
            if (style.drawSpace) {
                output.write((new String(new char[offset])).replace('\0', ' ')
                        + "#" + style.spacePrefixTop
                        + (new String(new char[borderSpacing - style.spacePrefixTop.length() - style.spaceSuffixTop.length()])).replace('\0', style.spaceCharTop)
                        + style.spaceSuffixTop + "#\n");
            }
        } else if (style.drawSpace) {
            output.write((new String(new char[offset])).replace('\0', ' ') + "#\n");
        }
        // then the actual comment lines
        for (String line : lines) {
            // todo? should we auto-wrap comment lines that are longer than 80 characters?
            output.write((new String(new char[offset])).replace('\0', ' ') + "#" + style.commentPrefix
                    + (minSpacing == 0 ? line : line + (new String(new char[minSpacing - line.length()])).replace('\0', ' ')) + style.commentSuffix + (style.drawBorder ? "#\n" : "\n"));
        }
        // now draw the bottom of the comment border
        if (style.drawBorder) {
            if (style.drawSpace) {
                output.write((new String(new char[offset])).replace('\0', ' ')
                        + "#" + style.spacePrefixBottom
                        + (new String(new char[borderSpacing - style.spacePrefixBottom.length() - style.spaceSuffixBottom.length()])).replace('\0', style.spaceCharBottom)
                        + style.spaceSuffixBottom + "#\n");
            }
            output.write((new String(new char[offset])).replace('\0', ' ') + (new String(new char[borderSpacing + 2])).replace('\0', '#') + "\n");
        } else if (style.drawSpace) {
            output.write((new String(new char[offset])).replace('\0', ' ') + "#\n");
        }
    }

    /**
     * These are the different ways to display a {@link Comment}
     */
    public enum CommentStyle {

        /**
         * # Comment
         */
        SIMPLE(false, false, " ", ""),
        /**
         * #           <br />
         * # Comment   <br />
         * #           <br />
         */
        SPACED(false, true, " ", ""),
        /**
         * ########### <br />
         * # Comment # <br />
         * ########### <br />
         */
        BLOCKED(true, false, " ", " "),
        /**
         * ############# <br />
         * #|¯¯¯¯¯¯¯¯¯|# <br />
         * #| Comment |# <br />
         * #|_________|# <br />
         * ############# <br />
         */
        BLOCKSPACED(true, true, "|\u00AF", '\u00AF', "\u00AF|", "| ", " |", "|_", '_', "_|");

        /**
         * Store the different options in order to draw the comment styling
         */
        final boolean drawBorder, drawSpace;
        final String commentPrefix, spacePrefixTop, spacePrefixBottom;
        final String commentSuffix, spaceSuffixTop, spaceSuffixBottom;
        final char spaceCharTop, spaceCharBottom;

        CommentStyle(boolean drawBorder, boolean drawSpace,
                             String spacePrefixTop, char spaceCharTop, String spaceSuffixTop,
                             String commentPrefix, String commentSuffix,
                             String spacePrefixBottom, char spaceCharBottom, String spaceSuffixBottom) {
            this.drawBorder = drawBorder;
            this.drawSpace = drawSpace;
            this.commentPrefix = commentPrefix;
            this.spacePrefixTop = spacePrefixTop;
            this.spacePrefixBottom = spacePrefixBottom;
            this.commentSuffix = commentSuffix;
            this.spaceSuffixTop = spaceSuffixTop;
            this.spaceSuffixBottom = spaceSuffixBottom;
            this.spaceCharTop = spaceCharTop;
            this.spaceCharBottom = spaceCharBottom;
        }

        CommentStyle(boolean drawBorder, boolean drawSpace, String commentPrefix, String commentSuffix) {
            this.drawBorder = drawBorder;
            this.drawSpace = drawSpace;
            this.commentPrefix = commentPrefix;
            this.commentSuffix = commentSuffix;
            this.spacePrefixTop = this.spacePrefixBottom = "";
            this.spaceCharTop = this.spaceCharBottom = ' ';
            this.spaceSuffixTop = this.spaceSuffixBottom = "";
        }

        /**
         * An easy way to detect what type of styling
         * a comment has
         *
         * @param lines The lines with styling
         * @return The relevant comment styling
         */
        public static CommentStyle parseStyle(List<String> lines) {
            if (lines == null || lines.size() <= 2) {
                return CommentStyle.SIMPLE;
            } else if (lines.size() > 2 && lines.get(0).trim().equals("#") && lines.get(lines.size() - 1).trim().equals("#")) {
                return CommentStyle.SPACED;
            }
            boolean hasBorders = lines.size() > 2 && lines.get(0).trim().matches("^##+$") && lines.get(lines.size() - 1).trim().matches("^##+$");
            if (!hasBorders) {
                // default return
                return CommentStyle.SIMPLE;
            }
            // now need to figure out if this is blocked or not
            final String replace = ("^#"
                    + CommentStyle.BLOCKSPACED.spacePrefixTop + CommentStyle.BLOCKSPACED.spaceCharTop + "+"
                    + CommentStyle.BLOCKSPACED.spaceSuffixTop + "#$").replace("|", "\\|");
            if (lines.size() > 4 && lines.get(1).trim().matches(replace)
                    && lines.get(1).trim().matches(replace)) {
                return CommentStyle.BLOCKSPACED;
            }
            return CommentStyle.BLOCKED;
        }
    }

}
