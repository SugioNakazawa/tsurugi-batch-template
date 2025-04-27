package jp.gr.java_conf.nkzw.tbt.app;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

public class ProductMasterCsvGenerator {

    private static final String[] CATEGORY_NAMES = { "青果", "乳製品", "調味料", "飲料", "菓子", "日用品" };
    private static final String[] MANUFACTURERS = { "青森フルーツ", "北海道ミルク", "東京ベーカリー", "日本オイル", "スパイスジャパン", "清流ウォーター",
            "関西エッグ", "スポドリカンパニー" };
    private static final String[] BRANDS = { "ゴールデンアップル", "ミルクファーム", "ふんわりブレッド", "ヘルシーオイル", "本格カレー", "クリアウォーター",
            "たまご倶楽部", "リフレッシュウォーター" };
    private static final String[] UNITS = { "個", "本", "袋", "箱", "パック", "缶" };

    private static final Random random = new Random();

    public static void main(String[] args) {
        String outputPath = "product_master.csv";
        int recordCount = 10000; // 生成する件数

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            // ヘッダー
            writer.write(
                    "product_id,product_code,product_name,category_id,manufacturer_name,brand_name,jan_code,unit,standard_price,purchase_price,stock_control_flag,sales_start_date,sales_end_date,created_at,updated_at\n");

            for (int i = 1; i <= recordCount; i++) {
                String line = generateProductLine(i);
                writer.write(line);
                writer.newLine();
            }

            System.out.println("生成完了: " + outputPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateProductLine(int id) {
        String productCode = String.format("PRD%05d", id);
        String productName = generateProductName();
        int categoryId = random.nextInt(CATEGORY_NAMES.length) + 1;
        String manufacturerName = MANUFACTURERS[random.nextInt(MANUFACTURERS.length)];
        String brandName = BRANDS[random.nextInt(BRANDS.length)];
        String janCode = "490" + String.format("%010d", random.nextInt(1000000000));
        String unit = UNITS[random.nextInt(UNITS.length)];
        BigDecimal standardPrice = BigDecimal.valueOf(100 + random.nextInt(900)).setScale(2);
        BigDecimal purchasePrice = standardPrice.multiply(BigDecimal.valueOf(0.7)).setScale(2,
                BigDecimal.ROUND_HALF_UP);
        boolean stockControlFlag = true;
        LocalDate salesStartDate = LocalDate.of(2025, 1, 1).plusDays(random.nextInt(180));
        String salesEndDate = ""; // 空白
        LocalDateTime createdAt = LocalDateTime.now().minusDays(random.nextInt(30));
        LocalDateTime updatedAt = createdAt.plusDays(random.nextInt(10));

        return String.join(",",
                String.valueOf(id),
                productCode,
                escape(productName),
                String.valueOf(categoryId),
                escape(manufacturerName),
                escape(brandName),
                janCode,
                unit,
                standardPrice.toString(),
                purchasePrice.toString(),
                String.valueOf(stockControlFlag),
                salesStartDate.toString(),
                salesEndDate,
                createdAt.toString().replace('T', ' '),
                updatedAt.toString().replace('T', ' '));
    }

    private static String generateProductName() {
        String[] products = { "りんご", "バナナ", "牛乳", "食パン", "卵", "サラダ油", "しょうゆ", "カレー粉", "水", "スポーツドリンク", "チョコレート",
                "ティッシュ" };
        String[] adjectives = { "新鮮な", "甘い", "濃厚な", "ふわふわの", "上質な", "お得な" };

        return adjectives[random.nextInt(adjectives.length)] + products[random.nextInt(products.length)];
    }

    private static String escape(String text) {
        if (text.contains(",")) {
            return "\"" + text + "\""; // カンマがある場合CSVエスケープ
        }
        return text;
    }
}
