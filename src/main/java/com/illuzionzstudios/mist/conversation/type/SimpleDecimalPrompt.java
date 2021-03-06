/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */
package com.illuzionzstudios.mist.conversation.type;

import com.illuzionzstudios.mist.conversation.SimplePrompt;
import com.illuzionzstudios.mist.util.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * A prompt that only accepts whole or decimal numbers
 */
@NoArgsConstructor
public class SimpleDecimalPrompt extends SimplePrompt {

    /**
     * The question you can set in the constructor already
     */
    @Setter(value = AccessLevel.PROTECTED)
    private String question = null;

    /**
     * What happens when the number is entered
     */
    @Setter(value = AccessLevel.PROTECTED)
    private Consumer<Double> successAction;

    public SimpleDecimalPrompt(String question, Consumer<Double> successAction) {
        this(question, successAction, true);
    }

    public SimpleDecimalPrompt(String question, Consumer<Double> successAction, final boolean openMenu) {
        super(openMenu);

        this.question = question;
        this.successAction = successAction;
    }

    /**
     * The menu question
     */
    @Override
    protected String getPrompt(final ConversationContext ctx) {
        Valid.checkNotNull(question, "Please either call setQuestion or override getPrompt");

        return "&6" + question;
    }

    /**
     * Return true if input is a valid number
     */
    @Override
    protected boolean isInputValid(final ConversationContext context, final String input) {
        return Valid.isDecimal(input) || Valid.isInteger(input);
    }

    /**
     * Show the message when the input is not a number
     */
    @Override
    protected String getFailedValidationText(final ConversationContext context, final String invalidInput) {
        return "The number must be a whole or a decimal number.";
    }

    /**
     * Parse through
     *
     * @see org.bukkit.conversations.ValidatingPrompt#acceptValidatedInput(org.bukkit.conversations.ConversationContext, java.lang.String)
     */
    @Override
    protected final Prompt acceptValidatedInput(@NotNull final ConversationContext context, @NotNull final String input) {
        return acceptValidatedInput(context, Double.parseDouble(input));
    }

    /**
     * What happens when the number is entered
     *
     * @param context
     * @param input
     * @return the next prompt, or {@link Prompt#END_OF_CONVERSATION} (that is actualy null) to exit
     */
    protected Prompt acceptValidatedInput(final ConversationContext context, final double input) {
        Valid.checkNotNull(question, "Please either call setSuccessAction or override acceptValidatedInput");

        successAction.accept(input);
        return Prompt.END_OF_CONVERSATION;
    }

    /**
     * Show the question with the action to the player
     * @param player
     * @param question
     * @param successAction
     */
    public static void show(final Player player, final String question, final Consumer<Double> successAction) {
        new SimpleDecimalPrompt(question, successAction).show(player);
    }
}

