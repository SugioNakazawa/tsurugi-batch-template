# Tsurugi Batch Template
Tsurugiでバッチプログラムをさく作成する際の支援ツールです。

## プロジェクトの説明

- app: プログラムの雛形として利用するテンプレート
  - バッチ処理の主処理をスレッドで実行
- tickets: 雛形をもとに作成したサンプルプログラム
  - 申込（application）の座席数が連続で確保できるように座席（Seat）を割り当てる
  - GUIアプリ（Swing）で座席を表示
- tools: プログラムで利用する entity のソースコード、テーブルの DDL 作成支援ツール
  - エクセルで作成したテーブル定義から、DDLおよびJavaのエンティティモデルモデルに加え、TgParameterMapping、TgResultMapping、toValuesName() のコードを出力します。

テンプレート(app)実行にはtoolsプロジェクトが必要です。依存関係を定義するか同じプロジェクトにコピーそて利用してください。

## ビルド

```shell
cd tsurugi-batch-template
./gradlew build
```

app, tickets, tools にそれぞれ tar ファイルと zip ファイルファ作成されます。

各プロジェクトの詳細はそれぞれの README.md を参照してください。