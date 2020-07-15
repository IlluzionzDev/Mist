/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */
package com.illuzionzstudios.mist.config.json;

import com.google.gson.JsonObject;

import java.io.Serializable;

/**
 * Define an object that can be serialized into a
 * {@link com.google.gson.JsonObject} and de-serialized from
 */
public interface JsonSerializable<T> extends Serializable {

    /**
     * @return Object turned into a {@link JsonObject}
     */
    JsonObject serialize();

    /**
     * @return T from a {@link JsonObject}. Usually from {@link #serialize()}
     */
    T deserialize(JsonObject json);

}
