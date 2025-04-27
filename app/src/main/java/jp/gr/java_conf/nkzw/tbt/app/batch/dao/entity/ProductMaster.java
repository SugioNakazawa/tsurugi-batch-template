package jp.gr.java_conf.nkzw.tbt.app.batch.dao.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProductMaster {

    // 商品ID
    private Integer productId;

    // 商品コード
    private String productCode;

    // 商品名
    private String productName;

    // カテゴリID
    private Integer categoryId;

    // メーカー名
    private String manufacturerName;

    // ブランド名
    private String brandName;

    // JANコード (日本のバーコード規格)
    private String janCode;

    // 単位 (デフォルトは「個」)
    private String unit = "個";

    // 標準価格
    private BigDecimal standardPrice;

    // 仕入価格
    private BigDecimal purchasePrice;

    // 在庫管理フラグ (デフォルトは true)
    private Boolean stockControlFlag = true;

    // 販売開始日
    private LocalDate salesStartDate;

    // 販売終了日
    private LocalDate salesEndDate;

    // 作成日時
    private LocalDateTime createdAt;

    // 更新日時
    private LocalDateTime updatedAt;

    // --- Getter & Setter ---

    // 商品IDの取得と設定
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    // 商品コードの取得と設定
    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    // 商品名の取得と設定
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    // カテゴリIDの取得と設定
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    // メーカー名の取得と設定
    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    // ブランド名の取得と設定
    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    // JANコードの取得と設定
    public String getJanCode() {
        return janCode;
    }

    public void setJanCode(String janCode) {
        this.janCode = janCode;
    }

    // 単位の取得と設定
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    // 標準価格の取得と設定
    public BigDecimal getStandardPrice() {
        return standardPrice;
    }

    public void setStandardPrice(BigDecimal standardPrice) {
        this.standardPrice = standardPrice;
    }

    // 仕入価格の取得と設定
    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    // 在庫管理フラグの取得と設定
    public Boolean getStockControlFlag() {
        return stockControlFlag;
    }

    public void setStockControlFlag(Boolean stockControlFlag) {
        this.stockControlFlag = stockControlFlag;
    }

    // 販売開始日の取得と設定
    public LocalDate getSalesStartDate() {
        return salesStartDate;
    }

    public void setSalesStartDate(LocalDate salesStartDate) {
        this.salesStartDate = salesStartDate;
    }

    // 販売終了日の取得と設定
    public LocalDate getSalesEndDate() {
        return salesEndDate;
    }

    public void setSalesEndDate(LocalDate salesEndDate) {
        this.salesEndDate = salesEndDate;
    }

    // 作成日時の取得 (設定は不可)
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // 更新日時の取得 (設定は不可)
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
