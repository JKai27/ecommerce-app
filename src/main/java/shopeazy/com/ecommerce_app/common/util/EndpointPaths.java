package shopeazy.com.ecommerce_app.common.util;

@SuppressWarnings("squid:S2386") // Public static mutable fields
public final class EndpointPaths {
    private EndpointPaths() {
    }

    // Reusable endpoint literals
    public static final String USERS_WILDCARD = "/api/users/**";
    public static final String PRODUCTS = "/api/products";
    public static final String SELLERS_WILDCARD = "/api/sellers/**";
    public static final String PRODUCT_IMAGES_WILDCARD = "/api/products/*/images/**";

    // Public (no authentication required)
    public static final String[] PUBLIC_ENDPOINTS = {
            "/api/users",
            "/api/auth/login",
            "/api/auth/logout",
            "/api/auth/refresh",
            "/api/sellers/apply",
            "/api/sellers/debug-test-auth",
            PRODUCTS
    };

    public static final String[] USER_ENDPOINTS = {
            "/api/users/me/profile",
    };

    public static final String[] SELLER_ENDPOINTS = {
            "/api/sellers/me/profile",
            PRODUCTS,
            "/api/products/**",
            PRODUCT_IMAGES_WILDCARD
    };

    public static final String[] ADMIN_GET_ENDPOINTS = {
            "/api/users", USERS_WILDCARD, "/api/sellers", "/api/sellers/{sellerId}"
    };

    // Admin-only DELETE
    public static final String[] ADMIN_DELETE_ENDPOINTS = {
            USERS_WILDCARD, SELLERS_WILDCARD, PRODUCTS
    };

    // Updated: Remove SELLERS_WILDCARD and be specific about admin seller endpoints
    public static final String[] ADMIN_PATCH_ENDPOINTS = {
            USERS_WILDCARD,
            "/api/sellers/*/status",
            "/api/sellers/bulk-status",
            "/api/sellers/*/rejection",
            "/api/products/status",
            "/api/products/status/bulk",
            "/api/products/multi-status/bulk"
    };

    public static final String[] ADMIN_PUT_ENDPOINTS = {
            "/api/sellers/approve"
    };
}