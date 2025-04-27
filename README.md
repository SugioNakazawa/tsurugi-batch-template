# Tsurugiアプリテンプレート tsurugi-batch-template

- TgData
  - エクセルで定義したテーブル定義からDLLとエンティティのJavaソースを生成します。
  - Javaソースには`TgResultMapping`対応の `RESULT_MAPPING`を生成します。
  - Tsurugiテーブルの作成、サンプルデータの作成します。
- TemplateBatch
  - 多重処理を行うバッチプログラムのサンプル

## TgData
### 手順
- エクセルでデータ定義
- TgDataで読み込んでDDLとエンティティソース作成
- TgDatでデータ生成
- CLI: tgsql で確認

### エクセルでデータ定義
エクセルの形式はよく利用されている`A5:SQK Mk-2`形式です。
TgDataでは以下を目印として解釈しています。
- ソート名: 特定のシート名を指定して処理が可能.(--sheet [シート名])。指定なしは全シート。
- B列で「物理テーブル名」に一致したセルの右（C列）をテーブル名とします。小文字のスネークケースを想定。
- A列で「カラム情報」に一致した１行スキップした行からカラムとします。
- C列を物理名（小文字スネークケース）、D列をデータ型（大文字）として読み込みます。
- C列、D列がからの時にはカラムが終了したと判断します。

ビルド

```shell
./gradlew shadoejar
```

実行

```shell
java -cp app/build/libs/app-0.1.0-all.jar jp.gr.java_conf.nkzw.tbt.tools.TgData \
    --excel app/src/test/resources/data/jp/gr/java_conf/nkzw/tbt/tools/TgData/table_design.xlsx \
    --sheetサンプル  \
    --javaentity  \
    --javapackage jp.gr.java_conf.nkzw.tbt.app.batch.dao.entity  \
    --out out
    --ddl  \
    --silent  \
    --createtable  \
    --generatedata  \
    --datacount 100  \
```

出力ファイル

```shell
tree out 
out
├── jp
│   └── gr
│       └── java_conf
│           └── nkzw
│               └── tbt
│                   └── app
│                       └── batch
│                           └── dao
│                               └── entity
│                                   └── SampleTable.java
└── sql
    └── create_sample_table.sql
```
### DLLとエンティティソース生成

### データ生成

## TemplateBatch