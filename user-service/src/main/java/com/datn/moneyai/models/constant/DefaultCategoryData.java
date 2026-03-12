package com.datn.moneyai.models.constant;

import java.util.List;

public final class DefaultCategoryData {

    // Chặn khởi tạo (Private Constructor) để chuẩn Clean Code
    private DefaultCategoryData() {
        throw new UnsupportedOperationException("Class này chỉ chứa hằng số, không được khởi tạo!");
    }

    // Record định nghĩa cấu trúc dữ liệu (Dành cho Java 14+)
    public record CategoryData(String type, String name, String icon, String colorCode) {}

    // 1. DANH SÁCH DANH MỤC MẶC ĐỊNH (Map chuẩn 100% từ Frontend)
    public static final List<CategoryData> DEFAULT_CATEGORIES = List.of(
            // ===== EXPENSE (KHOẢN CHI) =====
            new CategoryData("EXPENSE", "Ăn uống", "UtensilsCrossed", "text-orange-500"),
            new CategoryData("EXPENSE", "Chi tiêu hàng ngày", "ShoppingBag", "text-green-500"),
            new CategoryData("EXPENSE", "Quần áo", "Shirt", "text-blue-500"),
            new CategoryData("EXPENSE", "Mỹ phẩm", "Sparkles", "text-pink-500"),
            new CategoryData("EXPENSE", "Phí giao lưu", "Users", "text-yellow-500"),
            new CategoryData("EXPENSE", "Y tế", "HeartPulse", "text-teal-500"),
            new CategoryData("EXPENSE", "Giáo dục", "BookOpen", "text-orange-600"),
            new CategoryData("EXPENSE", "Tiền điện", "Zap", "text-cyan-500"),
            new CategoryData("EXPENSE", "Đi lại", "Train", "text-gray-600"),
            new CategoryData("EXPENSE", "Phí liên lạc", "Phone", "text-indigo-500"),
            new CategoryData("EXPENSE", "Tiền nhà", "Home", "text-amber-600"),

            // ===== INCOME (KHOẢN THU) =====
            new CategoryData("INCOME", "Tiền lương", "Wallet", "text-green-500"),
            new CategoryData("INCOME", "Tiền phụ cấp", "Briefcase", "text-orange-500"),
            new CategoryData("INCOME", "Tiền thưởng", "Gift", "text-red-500"),
            new CategoryData("INCOME", "Thu nhập phụ", "Coins", "text-teal-500"),
            new CategoryData("INCOME", "Đầu tư", "TrendingUp", "text-blue-500"),
            new CategoryData("INCOME", "Thu nhập tạm thời", "PiggyBank", "text-pink-500")
    );

    // 2. TẤT CẢ ICON CÓ SẴN (Dùng để Validate khi user Thêm/Sửa danh mục)
    public static final List<String> AVAILABLE_ICONS = List.of(
            "UtensilsCrossed", "ShoppingBag", "Shirt", "Sparkles", "Users",
            "HeartPulse", "BookOpen", "Zap", "Train", "Phone", "Home",
            "Wallet", "PiggyBank", "Gift", "Coins", "TrendingUp", "Briefcase",
            "Car", "Coffee", "Music", "Gamepad2", "Scissors", "Dumbbell",
            "Baby", "Dog", "Plane", "ShoppingCart", "Building", "Star"
    );

    // 3. TẤT CẢ MÀU CÓ SẴN (Dùng để Validate khi user Thêm/Sửa danh mục)
    public static final List<String> AVAILABLE_COLORS = List.of(
            "text-orange-500", "text-green-500", "text-blue-500", "text-pink-500",
            "text-yellow-500", "text-teal-500", "text-orange-600", "text-cyan-500",
            "text-gray-600", "text-indigo-500", "text-amber-600", "text-red-500",
            "text-purple-500", "text-lime-600", "text-amber-800", "text-rose-500"
    );
}