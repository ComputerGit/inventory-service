package com.at.t.ecommerce.inventory.domain.stock.enums;

public enum RotationPolicy {

	// --- Time-Based Rotation (Standard) ---
    FIFO,  // First In, First Out: The industry standard. Oldest stock is picked first to prevent obsolescence. (Retail, Electronics)
    LIFO,  // Last In, First Out: Newest stock is picked first. Used when stacking heavy goods (gravel, brick) or for specific tax strategies.

    // --- Expiration-Based Rotation (Critical for Safety) ---
    FEFO,  // First Expired, First Out: Ignores arrival date; prioritizes the *earliest expiration date*. Mandatory for Food & Pharma (FDA compliance).
    LEFO,  // Last Expired, First Out: Prioritizes products with the longest shelf life. Rarely used, but valuable for fulfilling long-transit international orders.

    // --- Cost-Based Rotation (Financial Strategy) ---
    HIFO,  // Highest In, First Out: Picks the most expensive inventory first. Used to maximize Cost of Goods Sold (COGS) and lower taxable income.
    LOFO,  // Lowest In, First Out: Picks the cheapest inventory first. Used to maximize on-paper profit margins (rare in physical ops, common in accounting).

    // --- Efficiency-Based Rotation ---
    NEAREST, // Proximity Picking: Disregards age/cost and picks the unit physically closest to the packer to minimize travel time (Amazon/Chaotic Storage).
    BATCH    // Lot/Batch Specific: Strict picking of a specific manufacturer batch, often triggered during recalls or quality control hold.
    
}
