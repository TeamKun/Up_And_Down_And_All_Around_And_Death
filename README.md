# Up_And_Down_And_All_Around_And_Death
重力MODのデスゲーム用アドオン  
前提に[Up And Down And All Around](https://www.curseforge.com/minecraft/mc-mods/up-and-down-and-all-around)が必要です  

1.12.2 Forge

# 仕様  
* gravity  
    * start 座標 箱のサイズ <3～100> 箱の構成ブロック 構成ブロックのメタデータ 回転速度(秒) <0～> バラバラ回転か[true/false] 
    * stop 停止

開始コマンド例  
/gravity start ~ ~ ~ 10 minecraft:diamond_block 0 30 false

開始コマンドを実行すると箱が生成されて全員がTPされ、１０秒後に回転が開始されます。
