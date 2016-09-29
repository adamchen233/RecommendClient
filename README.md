# RecommendClient   v0.01
steam游戏推荐功能

要运行起来需要替换RunTest.java和User2Item.java里的key和targetSteamid变量。
查询主要分为2部分
* 第一部分是根据targetSteamid，调用steam api获取相关信息，包括好友和好友的好友，以及这些人所拥有的游戏以及游戏时间数，这部分耗时不少，因为steam api限调用频率而且每次只能针对一个用户调用，有解决方法但是还没实现
* 第二部分为使用2种CF算法太计算推荐的游戏，默认只用userCf算法，另一个能跑而且比较准，但需要很多时间，之后有机会优化

暂时就酱
