package io.github.meghbhanu.lifecycle.product;

public final class ProductDescriptions {

    private ProductDescriptions() {}

    public static String describe(Product product) {
        return switch (product) {
            case Autocallable(var terms, var coupon, var autocall, var capital) ->
                    "Autocallable %s: autocall at %s, coupon barrier %s, capital barrier %s"
                            .formatted(terms.productId(), autocall, coupon, capital);
            case BarrierReverseConvertible(var terms, var capital) ->
                    "BarrierReverseConvertible %s: capital barrier %s"
                            .formatted(terms.productId(), capital);
            case FixedCouponNote(var terms) -> "FixedCouponNote %s"
                    .formatted(terms.productId());
        };
    }
}
