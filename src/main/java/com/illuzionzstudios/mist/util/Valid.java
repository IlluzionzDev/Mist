/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */
package com.illuzionzstudios.mist.util;

import com.illuzionzstudios.mist.exception.PluginException;
import lombok.experimental.UtilityClass;

/**
 * Util class to check if things are valid
 */
@UtilityClass
public class Valid {

    /**
     * Throws an error if the given object is null
     *
     * @param toCheck Object to check if is null
     */
    public void checkNotNull(Object toCheck) {
        if (toCheck == null)
            throw new PluginException();
    }

    /**
     * Throws an error with a custom message if the given object is null
     *
     * @param toCheck Object to check if is null
     * @param falseMessage Message explaining why it may have been null
     */
    public void checkNotNull(Object toCheck, String falseMessage) {
        if (toCheck == null)
            throw new PluginException(falseMessage);
    }

    /**
     * Throws an error if the given expression is false
     *
     * @param expression Boolean expression to check
     */
    public void checkBoolean(boolean expression) {
        if (!expression)
            throw new PluginException();
    }

    /**
     * Throws an error with a custom message if the given expression is false
     *
     * @param expression Boolean expression to check
     * @param falseMessage Message explaining why it may have been false
     */
    public void checkBoolean(boolean expression, String falseMessage) {
        if (!expression)
            throw new PluginException(falseMessage);
    }

}