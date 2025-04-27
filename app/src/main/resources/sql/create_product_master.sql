CREATE TABLE product_master (
    product_id SERIAL PRIMARY KEY,         -- 商品ID（自動採番）
    product_code VARCHAR(50) NOT NULL UNIQUE, -- 商品コード（ユニーク）
    product_name VARCHAR(255) NOT NULL,     -- 商品名
    category_id INTEGER,                    -- カテゴリID（カテゴリマスタ参照）
    manufacturer_name VARCHAR(255),         -- メーカー名
    brand_name VARCHAR(255),                -- ブランド名
    jan_code VARCHAR(13),                   -- JANコード（バーコード）
    unit VARCHAR(20) DEFAULT '個',          -- 単位（個、箱など）
    standard_price NUMERIC(10,2),            -- 標準売価
    purchase_price NUMERIC(10,2),            -- 標準仕入価
    stock_control_flag BOOLEAN DEFAULT TRUE, -- 在庫管理フラグ
    sales_start_date DATE,                   -- 販売開始日
    sales_end_date DATE,                     -- 販売終了日
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 登録日時
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- 更新日時
);

-- もしカテゴリも別テーブルで管理するなら、カテゴリマスタも用意する例
CREATE TABLE category_master (
    category_id SERIAL PRIMARY KEY,
    category_name VARCHAR(255) NOT NULL
);
