# Sales Batch
更新性能を確認するためのバッチです。
## 処理内容
1. SalesDetailテーブルからレコードを取得。
2. 取得したレコードの商品ID、年月日をキーに、DailySalesテーブルからレコードを取得。
3. SalesDetailの数量、金額をDailySalesの数量、金額に加算し、更新。
4. 1～3を繰り返し。

## 実装
- トランザクションはタスククラスで実装。
- 1タスクで処理する商品は10000件。
- タスクには処理する商品の最小ID、最大IDを渡す。重複処理を排除するため。
- 同時実行数はパラメータで指定可能。
- 作成するデータの年月日時分はプログラム固定です。

## ビルド
```shell
./gradlew clean build
```
## 実行

- データ作成
  - sales＿detailとdaily_salesにデータを登録します。tasknum × pertask 件数登録されます。以下の例では1000 × 10000 = 1000万件登録されます。
  ```shell
  java -cp sales-0.1.0-all.jar jp.gr.java_conf.nkzw.tbt.sales.batch.SalesBatch \
    --endpoint ipc:tsurugi \
    --threadsize 16 \
    --pertask 10000 \
    --tasknum 1000 \
    --mode insert
  ```

- 更新実行
  - sales_detailのデータを元にdaily_salesの数量、金額を更新します。以下の例では1000 × 10000 = 1000万件更新されます。
  - 用意したデータ以下になるようにしてください。tasknum と pertask をデータ作成時と同じにすれば全件処理します。
  ```shell
  java -cp sales-0.1.0-all.jar jp.gr.java_conf.nkzw.tbt.sales.batch.SalesBatch \
    --endpoint ipc:tsurugi \
    --threadsize 16 \
    --pertask 10000 \
    --tasknum 1000 \
    --mode update
  ```

- パラメータ
  - endpoint: Tsurugiのエンドポイント
  - threadsize: 同時実行数
  - pertask: 1タスクで処理する商品数
  - tasknum: タスク数
  - mode: insert（データ作成）、update（更新実行）