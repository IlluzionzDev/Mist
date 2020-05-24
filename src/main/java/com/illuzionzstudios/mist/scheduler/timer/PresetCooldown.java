package com.illuzionzstudios.mist.scheduler.timer;

import lombok.Getter;
import lombok.Setter;

/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */

/**
 * A pre set cooldown that can be started when we wish
 */
@Getter
@Setter
public class PresetCooldown extends Cooldown {

    /**
     * The amount of ticks to wait
     */
    private int wait;

    public PresetCooldown(int defaultWait) {
        wait = defaultWait;
    }

    /**
     * Start the timer
     */
    public void go() {
        super.setWait(wait);
    }
}
