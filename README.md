# Tsurugi Batch Template
Tsurugiを利用するバッチプログラム作成支援ツールです。

## プロジェクトの説明

- app: プログラムの雛形として利用するテンプレート
  - バッチ処理の主処理をスレッドで実行
- tickets: 雛形をもとに作成したサンプルプログラム
  - 申込（application）の座席数が連続で確保できるように座席（Seat）を割り当てる
  - GUIアプリ（Swing）で座席を表示
- tools: プログラムで利用する entity のソースコード、テーブルの DDL 作成支援ツール
  - エクセルで作成したテーブル定義から、DDLおよびJavaのエンティティモデルモデルに加え、TgParameterMapping、TgResultMapping、toValuesName() のコードを出力します。

`テンプレート(app)、チケット(tickets)は tools に依存しています。流用する際には依存関係を定義するか同一プロジェクトにコピーして利用してください。`

## ビルド

```shell
cd tsurugi-batch-template
./gradlew build
```

app, tickets, tools にそれぞれ tar ファイルと zip ファイルファ作成されます。

各プロジェクトの詳細はそれぞれの README.md を参照してください。

## 実行
### tickets
```shell
cd work # 任意のディレクトリを作成して移動
tar xf ~/github/tsurugi-batch-template/tickets/build/distributions/tickets-0.1.0.tar

# GUI起動
./tickets-0.1.0/bin/run_gui.sh 
```

実行シェル一覧

- run_batch_prepare.sh: 座席を準備するバッチを実行
  - --rowSeat 160 160
- run_batch_assign.sh: 座席を割り当てるバッチを実行
  - --rowSeat 160 160 --threadSize 16
- run_batch_show.sh: 座席を表示するバッチを実行
  - --rowSeat 160 160
- run_batch.sh: ファンクションを指定してバッチを実行
- run_gui.sh: GUIを起動