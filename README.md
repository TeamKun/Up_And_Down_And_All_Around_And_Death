# Up_And_Down_And_All_Around_And_Death

重力MODのデスゲーム用アドオン  
前提に[Up And Down And All Around](https://www.curseforge.com/minecraft/mc-mods/up-and-down-and-all-around)が必要です

1.12.2 Forge

# 仕様

* gravity
    * start 座標 箱のサイズ <3～100> 箱の構成ブロック 構成ブロックのメタデータ 回転速度(秒) <0～> バラバラ回転か[true/false]
    * stop 停止
    * change 変更
        * rotedspeed 回転までの時間（秒）
        * holespeed 足場が消える頻度（デフォルトの倍を指定)
        * randomdirection 全員バラバラに回転するか固定かどうか(true/false)

開始コマンド例  
/gravity start ~ ~ ~ 10 minecraft:diamond_block 0 30 false

変更コマンド例  
/gravity change rotedspeed 19 (19秒ごとに回転)  
/gravity change holespeed 1.5 (デフォルトの1.5倍の頻度で足場消滅)  
/gravity change randomdirection true (バラバラ回転)

開始コマンドを実行すると箱が生成されて全員がTPされ、１０秒後に回転し始めます。  
実行中は落下ダメージが無効化され回転（ランダムで重力が変更）します。  
最後の１人になったら終了です。
