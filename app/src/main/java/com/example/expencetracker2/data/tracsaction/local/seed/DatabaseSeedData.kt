package com.example.expencetracker2.data.tracsaction.local.seed

import com.example.expencetracker2.data.tracsaction.local.entity.AccountEntity
import com.example.expencetracker2.data.tracsaction.local.entity.MasterCategoryEntity
import com.example.expencetracker2.data.tracsaction.local.entity.SubCategoryEntity

object DatabaseSeedData {

    val MASTER_CATEGORIES = listOf(
        // 🔴 EXPENSE CATEGORIES (ID: 1 to 8)
        MasterCategoryEntity(id = 1, name = "Food & Dining", iconName = "ic_master_food", colorHex = "#FF5722", displayOrder = 1),
        MasterCategoryEntity(id = 2, name = "Travel & Commute", iconName = "ic_master_travel", colorHex = "#2196F3", displayOrder = 2),
        MasterCategoryEntity(id = 3, name = "Shopping & Lifestyle", iconName = "ic_master_shopping", colorHex = "#E91E63", displayOrder = 3),
        MasterCategoryEntity(id = 4, name = "Bills & Utilities", iconName = "ic_master_bills", colorHex = "#9C27B0", displayOrder = 4),
        MasterCategoryEntity(id = 5, name = "Health & Wellness", iconName = "ic_master_health", colorHex = "#4CAF50", displayOrder = 5),
        MasterCategoryEntity(id = 6, name = "Financials & Debt", iconName = "ic_master_finance", colorHex = "#00BCD4", displayOrder = 6),
        MasterCategoryEntity(id = 7, name = "Entertainment", iconName = "ic_master_entertainment", colorHex = "#FFC107", displayOrder = 7),
        MasterCategoryEntity(id = 8, name = "Others", iconName = "ic_master_others", colorHex = "#607D8B", displayOrder = 8),

        // 🟢 INCOME CATEGORIES (ID: 9 to 17)
        MasterCategoryEntity(id = 9, name = "Salary & Wages", iconName = "ic_master_salary", colorHex = "#2E7D32", displayOrder = 9),
        MasterCategoryEntity(id = 10, name = "Business & Freelance", iconName = "ic_master_business", colorHex = "#00897B", displayOrder = 10),
        MasterCategoryEntity(id = 11, name = "Investments & Interest", iconName = "ic_master_investment", colorHex = "#1565C0", displayOrder = 11),
        MasterCategoryEntity(id = 12, name = "Pocket Money & Allowance", iconName = "ic_master_allowance", colorHex = "#8E24AA", displayOrder = 12),
        MasterCategoryEntity(id = 13, name = "Rental & Property", iconName = "ic_master_rental", colorHex = "#D81B60", displayOrder = 13),
        MasterCategoryEntity(id = 14, name = "Cashback & Refunds", iconName = "ic_master_cashback", colorHex = "#F57F17", displayOrder = 14),
        MasterCategoryEntity(id = 15, name = "Gifts & Scholarships", iconName = "ic_master_gift", colorHex = "#00ACC1", displayOrder = 15),
        MasterCategoryEntity(id = 16, name = "Crypto", iconName = "ic_master_sell", colorHex = "#6D4C41", displayOrder = 16),
        MasterCategoryEntity(id = 17, name = "Other Income", iconName = "ic_master_other_income", colorHex = "#455A64", displayOrder = 17)
    )

    val POPULAR_CATEGORIES = listOf(

    // ==========================================
    // 1. 🍔 FOOD & DINING (masterCategoryId = 1)
    // =========================================
        SubCategoryEntity(id = 101, masterCategoryId = 1, name = "Swiggy", iconName = "ic_swiggy", colorHex = "#FF5722", isPopular = true),
        SubCategoryEntity(id = 102, masterCategoryId = 1, name = "Zomato", iconName = "ic_zomato", colorHex = "#CB202D", isPopular = true),
        SubCategoryEntity(id = 103, masterCategoryId = 1, name = "Blinkit", iconName = "ic_blinkit", colorHex = "#F7C325", isPopular = true),
        SubCategoryEntity(id = 104, masterCategoryId = 1, name = "Zepto", iconName = "ic_zepto", colorHex = "#7B2CBF", isPopular = true),
        SubCategoryEntity(id = 105, masterCategoryId = 1, name = "Instamart", iconName = "ic_instamart", colorHex = "#FF5722", isPopular = false),
        SubCategoryEntity(id = 106, masterCategoryId = 1, name = "Tea & Coffee", iconName = "ic_coffee", colorHex = "#795548", isPopular = true),
        SubCategoryEntity(id = 107, masterCategoryId = 1, name = "Dining Out", iconName = "ic_restaurant", colorHex = "#E91E63", isPopular = false),

        // ==========================================
        // 2. ✈️ TRAVEL & COMMUTE (masterCategoryId = 2)
        // ==========================================
        SubCategoryEntity(id = 201, masterCategoryId = 2, name = "Petrol", iconName = "ic_petrol", colorHex = "#2196F3", isPopular = true),
        SubCategoryEntity(id = 202, masterCategoryId = 2, name = "Diesel", iconName = "ic_diesel", colorHex = "#607D8B", isPopular = false),
        SubCategoryEntity(id = 203, masterCategoryId = 2, name = "EV Charging", iconName = "ic_ev", colorHex = "#4CAF50", isPopular = false),
        SubCategoryEntity(id = 204, masterCategoryId = 2, name = "Uber", iconName = "ic_uber", colorHex = "#000000", isPopular = true),
        SubCategoryEntity(id = 205, masterCategoryId = 2, name = "Rapido", iconName = "ic_rapido", colorHex = "#FFC107", isPopular = true),
        SubCategoryEntity(id = 206, masterCategoryId = 2, name = "Ola", iconName = "ic_ola", colorHex = "#8BC34A", isPopular = false),
        SubCategoryEntity(id = 207, masterCategoryId = 2, name = "FASTag", iconName = "ic_fastag", colorHex = "#00BCD4", isPopular = true),
        SubCategoryEntity(id = 208, masterCategoryId = 2, name = "Metro & Bus", iconName = "ic_bus", colorHex = "#3F51B5", isPopular = true),

        // ==========================================
        // 3. 🛍️ SHOPPING (masterCategoryId = 3)
        // ==========================================
        SubCategoryEntity(id = 301, masterCategoryId = 3, name = "Amazon", iconName = "ic_amazon", colorHex = "#FF9900", isPopular = true),
        SubCategoryEntity(id = 302, masterCategoryId = 3, name = "Flipkart", iconName = "ic_flipkart", colorHex = "#2874F0", isPopular = true),
        SubCategoryEntity(id = 303, masterCategoryId = 3, name = "Myntra", iconName = "ic_myntra", colorHex = "#E40046", isPopular = true),
        SubCategoryEntity(id = 304, masterCategoryId = 3, name = "DMart", iconName = "ic_dmart", colorHex = "#00833E", isPopular = false),
        SubCategoryEntity(id = 305, masterCategoryId = 3, name = "Electronics", iconName = "ic_gadgets", colorHex = "#9C27B0", isPopular = false),
        SubCategoryEntity(id = 306, masterCategoryId = 3, name = "Clothing", iconName = "ic_clothes", colorHex = "#E91E63", isPopular = false),

        // ==========================================
        // 4. 🧾 BILLS & UTILITIES (masterCategoryId = 4)
        // ==========================================
        SubCategoryEntity(id = 401, masterCategoryId = 4, name = "Mobile Recharge", iconName = "ic_mobile", colorHex = "#00BCD4", isPopular = true),
        SubCategoryEntity(id = 402, masterCategoryId = 4, name = "Electricity", iconName = "ic_electricity", colorHex = "#FFEB3B", isPopular = true),
        SubCategoryEntity(id = 403, masterCategoryId = 4, name = "Wi-Fi Broadband", iconName = "ic_wifi", colorHex = "#3F51B5", isPopular = true),
        SubCategoryEntity(id = 404, masterCategoryId = 4, name = "Water Bill", iconName = "ic_water", colorHex = "#2196F3", isPopular = false),
        SubCategoryEntity(id = 405, masterCategoryId = 4, name = "Gas Cylinder", iconName = "ic_gas", colorHex = "#FF5722", isPopular = true),

        // ==========================================
        // 5. 🏥 HEALTH & WELLNESS (masterCategoryId = 5)
        // ==========================================
        SubCategoryEntity(id = 501, masterCategoryId = 5, name = "Medicines", iconName = "ic_medicine", colorHex = "#4CAF50", isPopular = true),
        SubCategoryEntity(id = 502, masterCategoryId = 5, name = "Gym & Fitness", iconName = "ic_gym", colorHex = "#FF5722", isPopular = true),
        SubCategoryEntity(id = 503, masterCategoryId = 5, name = "Salon", iconName = "ic_salon", colorHex = "#E91E63", isPopular = false),

        // ==========================================
        // 6. 💳 FINANCIALS & DEBT (masterCategoryId = 6)
        // ==========================================
        SubCategoryEntity(id = 601, masterCategoryId = 6, name = "Credit Card Bill", iconName = "ic_credit_card", colorHex = "#F44336", isPopular = true),
        SubCategoryEntity(id = 602, masterCategoryId = 6, name = "House Rent", iconName = "ic_rent", colorHex = "#795548", isPopular = true),
        SubCategoryEntity(id = 603, masterCategoryId = 6, name = "SIP & Investments", iconName = "ic_invest", colorHex = "#4CAF50", isPopular = true),
        SubCategoryEntity(id = 604, masterCategoryId = 6, name = "EMI & Loans", iconName = "ic_emi", colorHex = "#FF9800", isPopular = false),

        // ==========================================
        // 7. 🎬 ENTERTAINMENT (masterCategoryId = 7)
        // ==========================================
        SubCategoryEntity(id = 701, masterCategoryId = 7, name = "Netflix / Prime", iconName = "ic_netflix", colorHex = "#E50914", isPopular = true),
        SubCategoryEntity(id = 702, masterCategoryId = 7, name = "BookMyShow", iconName = "ic_movie", colorHex = "#D32F2F", isPopular = true),
        SubCategoryEntity(id = 703, masterCategoryId = 7, name = "YouTube / Spotify", iconName = "ic_music", colorHex = "#FF0000", isPopular = false),
        SubCategoryEntity(id = 704, masterCategoryId = 7, name = "Gaming", iconName = "ic_game", colorHex = "#673AB7", isPopular = false),

        // ==========================================
        // 8. 📦 OTHERS (masterCategoryId = 8)
        // ==========================================
        SubCategoryEntity(id = 801, masterCategoryId = 8, name = "Gifts", iconName = "ic_gift", colorHex = "#E91E63", isPopular = false),
        SubCategoryEntity(id = 802, masterCategoryId = 8, name = "Maid Salary", iconName = "ic_maid", colorHex = "#009688", isPopular = false),
        SubCategoryEntity(id = 803, masterCategoryId = 8, name = "Misc Daily", iconName = "ic_misc", colorHex = "#607D8B", isPopular = true),

    )

    val REGULAR_CATEGORIES = listOf(
        SubCategoryEntity(id = 151, masterCategoryId = 1, name = "Food", iconName = "ic_food", colorHex = "#FF5722", isDefault = true),
        SubCategoryEntity(id = 152, masterCategoryId = 1, name = "Grocery", iconName = "ic_grocery", colorHex = "#4CAF50", isDefault = true),

        // Master 2: Travel & Commute (251+)
        SubCategoryEntity(id = 251, masterCategoryId = 2, name = "Travel", iconName = "ic_travel", colorHex = "#2196F3", isDefault = true),

        // Master 3: Shopping & Lifestyle (351+)
        SubCategoryEntity(id = 351, masterCategoryId = 3, name = "Shopping", iconName = "ic_shopping_bags", colorHex = "#00BCD4", isDefault = true),
        SubCategoryEntity(id = 352, masterCategoryId = 3, name = "Self Care", iconName = "ic_personal_care", colorHex = "#EC407A", isDefault = true),

        // Master 4: Bills & Utilities (451+)
        SubCategoryEntity(id = 451, masterCategoryId = 4, name = "Bills", iconName = "ic_bills", colorHex = "#FFC107", isDefault = true),
        SubCategoryEntity(id = 452, masterCategoryId = 4, name = "Home", iconName = "ic_home", colorHex = "#795548", isDefault = true),

        // Master 5: Health & Wellness (551+)
        SubCategoryEntity(id = 551, masterCategoryId = 5, name = "Fitness", iconName = "ic_fitness", colorHex = "#4CAF50", isDefault = true),

        // Master 6: Financials & Debt (651+)
        SubCategoryEntity(id = 652, masterCategoryId = 6, name = "Invest", iconName = "ic_investments", colorHex = "#FFB300", isDefault = true),
        SubCategoryEntity(id = 653, masterCategoryId = 6, name = "Insurance", iconName = "ic_insurance", colorHex = "#4CAF50", isDefault = true),
        SubCategoryEntity(id = 654, masterCategoryId = 6, name = "Taxes & Fees", iconName = "ic_taxes", colorHex = "#78909C", isDefault = true),

        // Master 7: Entertainment (751+)
        SubCategoryEntity(id = 751, masterCategoryId = 7, name = "Premium", iconName = "ic_subscriptions", colorHex = "#D81B60", isDefault = true),
        SubCategoryEntity(id = 752, masterCategoryId = 7, name = "Events", iconName = "ic_party", colorHex = "#E91E63", isDefault = true),

        // Master 8: Others (851+)
        SubCategoryEntity(id = 453, masterCategoryId = 8, name = "Repairs", iconName = "ic_repairs", colorHex = "#607D8B", isDefault = true),
        SubCategoryEntity(id = 851, masterCategoryId = 8, name = "Education", iconName = "ic_education", colorHex = "#1E88E5", isDefault = true)
    )


    val seedAccountsList = listOf(
        AccountEntity(id = 1, name = "Cash", balance = 0.0, icon = "ic_cash", isPrimary = true, accountType = "CASH", linkedBankId = null),
        AccountEntity(id = 2, name = "Bank Account", balance = 0.0, icon = "ic_bank", isPrimary = false, accountType = "BANK", linkedBankId = null),
        AccountEntity(id = 3, name = "Debit Card", balance = 0.0, icon = "ic_debit_card", isPrimary = false, accountType = "DEBIT CARD", linkedBankId = 2L),
        AccountEntity(id = 4, name = "Credit Card", balance = 0.0, icon = "ic_credit_card", isPrimary = false, accountType = "CREDIT CARD", linkedBankId = null),
        AccountEntity(id = 5, name = "UPI", balance = 0.0, icon = "ic_upi", isPrimary = false, accountType = "UPI", linkedBankId = 2L),
        AccountEntity(id = 6, name = "Wallet", balance = 0.0, icon = "ic_wallet", isPrimary = false, accountType = "WALLET", linkedBankId = null),
    )

}
