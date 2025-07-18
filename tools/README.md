# tools in tsurugi-batch-template
## 概要
データモデル作成支援

- TgData: メインクラス
  - エクセルで定義したテーブル定義からDLLとエンティティのJavaソースを生成します。
    - エクセルのフォーマットは A5SQLMK2 をもとにしています。
  - Entiyu モデルのJavaソースを出力します。
    - 合わせて、`TgParameterMapping`、 `TgResultMapping` 、　`toValuesName()` を生成します。
  - Tsurugi dbへのテーブル作成、サンプルデータの登録を行うことも可能です。

## 準備

### ビルド
```shell
cd tsurugi-batch-template
./gradlew build
```

### データ定義（エクセル）
エクセルの形式はよく利用されている`A5:SQK Mk-2`形式です。
TgDataでは以下を目印として解釈しています。
- シート名
  - 特定のシートを指定する場合に指定します。 `--sheet [シート名]` 指定なしの場合は全シート対象。
- 物理テーブル名
  - B列の「物理テーブル名」に一致したセルの右（C列）をテーブル名とします。小文字のスネークケースを想定しています。
  - DBのテーブル名はそのまま、Javaエンティティのクラス名はパスカルケースに変換します。
- カラム名
  - A列の「カラム情報」に一致した１行次の行からをカラムとします。
  - DBのカラム名はそのまま、エンティティのメンバ名はスネークケースに変換します。
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