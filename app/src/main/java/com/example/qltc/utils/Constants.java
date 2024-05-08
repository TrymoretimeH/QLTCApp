package com.example.qltc.utils;

import com.example.qltc.R;
import com.example.qltc.models.Category;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

public class Constants {
    public static String INCOME = "THU";
    public static String EXPENSE = "CHI";

    public static ArrayList<Category> categories;

    public static int DAILY = 0;
    public static int MONTHLY = 1;
    public static int YEARLY = 2;
    public static int SUMMARY = 3;
    public static int NOTES = 4;

    public static int SELECTED_TAB = 0;
    public static int SELECTED_TAB_STATS = 0;
    public static String SELECTED_STATS_TYPE = Constants.INCOME;

    public static void setCategories() {
        categories = new ArrayList<>();
        categories.add(new Category("Lương", R.drawable.salary));
        categories.add(new Category("Kinh doanh", R.drawable.business));
        categories.add(new Category("Đầu tư", R.drawable.investment));
        categories.add(new Category("Lãi", R.drawable.interest));
        categories.add(new Category("Phí", R.drawable.cost));
        categories.add(new Category("Cho vay", R.drawable.loan));
        categories.add(new Category("Mượn tiền", R.drawable.borrow));
        categories.add(new Category("Ăn uống", R.drawable.food));
        categories.add(new Category("Giải trí", R.drawable.entertainment));
        categories.add(new Category("Xe cộ", R.drawable.traffic));
        categories.add(new Category("Nhà cửa", R.drawable.house));
        categories.add(new Category("Sức khỏe", R.drawable.health));
        categories.add(new Category("Tiện ích", R.drawable.utilities));
        categories.add(new Category("Khác", R.drawable.other));
    }

    public static Category getCategoryDetails(String categoryName) {
        for (Category cat :
                categories) {
            if (cat.getCategoryName().equals(categoryName)) {
                return cat;
            }
        }
        return null;
    }

    public static int getAccountsColor(String accountName) {
        switch (accountName) {
            case "Bank":
                return R.color.bank_color;
            case "Cash":
                return R.color.cash_color;
            case "Card":
                return R.color.card_color;
            default:
                return R.color.default_color;
        }
    }

    public static DecimalFormat getVNFormat() {
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance(new Locale("vi", "VN"));
        DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
        dfs.setGroupingSeparator('.');
        dfs.setDecimalSeparator(',');
        df.setDecimalFormatSymbols(dfs);
        return df;
    }

}
