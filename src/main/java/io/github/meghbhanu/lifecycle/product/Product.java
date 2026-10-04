package io.github.meghbhanu.lifecycle.product;

import io.github.meghbhanu.lifecycle.domain.CommonTerms;

public sealed interface Product permits Autocallable, BarrierReverseConvertible, FixedCouponNote {
    CommonTerms terms();
}
