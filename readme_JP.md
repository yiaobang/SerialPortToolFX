# SerialPortToolFX

## 概要

SerialPortToolFX は、JavaFX を使用して構築されたクロスプラットフォームのオープンソースシリアルポートデバッグツールです。開発者がシリアルポート通信をデバッグおよび管理しやすくするために、ユーザーフレンドリーなインターフェースを提供します。

## ソフトウェアのインターフェース

### ライトテーマ

![light](light.png)

### ダークテーマ

![dark](dark.png)

## 機能特性

- **リアルタイムシリアルポートリストの更新**：利用可能なシリアルポートデバイスを自動検出して表示します。
- **データフォーマットサポート**：ASCII および HEX 形式のシリアルデータ送受信をサポートします。
- **データ統計**：送受信データのバイト数を統計し、分析を容易にします。（カウントをリセットするには、対応する数字をクリックしてください）
- **データ永続化**：シリアル通信データをファイルに保存できます。（対応するオプションボタンを選択する必要があります）
- **複数ウィンドウのサポート**：複数のシリアル通信ウィンドウを同時に開くことができます。
- **シミュレーション応答**：指定されたデータを受信した際に、事前設定されたデータを応答として送信します。（正しく構成された JSON ファイルを読み込む必要があります）

## 依存関係

- [lombok](https://github.com/projectlombok/lombok)
- [javafx](https://github.com/openjdk/jfx)
- [atlantafx](https://github.com/mkpaz/atlantafx)
- [jSerialComm](https://github.com/Fazecast/jSerialComm)
- [gson](https://github.com/google/gson)
- [commons-codec](https://github.com/apache/commons-codec)
- [commons-text](https://github.com/apache/commons-text)

## シリアルポートシミュレーション応答 JSON ファイル説明

### JSON 基本設定例：

```json
{
  "encode": "HEX",
  "packSize": "13",
  "delimiter": ""
}
