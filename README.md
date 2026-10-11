# Achievement App

一个用于查看和管理 **PlayStation Network (PSN)** 游戏库与奖杯（成就）的 Android 应用。基于 NPSSO 登录，拉取玩家资料、游戏列表、奖杯统计与逐款游戏的奖杯详情，并缓存在本地。

## 功能特性

- **NPSSO 登录认证**：通过 Sony 账号 OAuth 流程获取令牌，自动刷新 `access_token`
- **概览页**：玩家头像、等级、在线状态、PS Plus 会员、四等级奖杯汇总与升级进度
- **游戏页**：游戏库列表，展示封面、游玩时长、最近游玩时间与奖杯进度
- **奖杯页**：奖杯总览统计 + 按游戏聚合的奖杯列表，点击进入游戏详情
- **游戏详情**：顶部总览（进度条、各等级 获得/总数）+ 逐条奖杯（官方 PSN 图标、稀有度、解锁时间）
- **我的页**：账号信息、奖杯统计与升级进度
- **白金殿堂**：记录达成白金的游戏
- **本地缓存**：Room 持久化，支持下拉刷新同步
- **离线友好**：数据本地存储，登录后浏览不依赖实时网络

## 技术栈

| 分类 | 技术 |
| --- | --- |
| 语言 | Kotlin |
| UI | Jetpack Compose + Material 3 |
| 架构 | MVVM + 单向数据流 (StateFlow) |
| 依赖注入 | Hilt (Dagger) |
| 导航 | Navigation Compose |
| 本地数据库 | Room |
| 网络 | Retrofit + OkHttp |
| 序列化 | Kotlinx Serialization |
| 图片加载 | Coil |
| 键值存储 | DataStore Preferences |
| 登录 | Chrome Custom Tabs / WebView |

## 环境要求

- JDK **17**
- Android SDK **compileSdk 34 / targetSdk 34**
- **minSdk 26** (Android 8.0)
- Android Studio（建议 Iguana 或更新）或命令行 Gradle

## 构建与运行

### 方式一：Android Studio

1. 用 Android Studio 打开项目根目录
2. 等待 Gradle 同步完成
3. 连接真机或启动模拟器
4. 点击 Run ▶ 运行 `app`

### 方式二：命令行

```bash
# Windows（需指定 JDK 17）
export JAVA_HOME="<你的 JDK 17 路径>"
./gradlew assembleDebug      # 构建
./gradlew installDebug       # 构建并安装到已连接设备
```

构建产物位于 `app/build/outputs/apk/debug/`。

## NPSSO Token 获取

应用登录走标准 OAuth 流程。若需手动获取 NPSSO：

1. 访问 `https://ca.account.sony.com/api/v1/ssocookie`
2. 登录你的 PSN 账号
3. 复制返回的 `NPSSO` 值

## 项目结构

```
app/src/main/java/com/achievement/app/
├── AchievementApp.kt          # Application，Hilt 入口
├── MainActivity.kt            # 唯一 Activity，承载 Compose 导航
├── di/                        # Hilt 模块（Database / Network）
├── domain/
│   ├── model/                 # 领域模型 (Player, Game, Trophy, Achievement…)
│   └── util/                  # 常量与时间工具
├── data/
│   ├── remote/                # API 接口、DTO、鉴权拦截器
│   ├── local/                 # Room 数据库、DAO、Entity、DataStore
│   ├── mapper/                # DTO/Entity ↔ 领域模型 转换
│   └── repository/            # 仓库层，聚合网络与本地数据
├── viewmodel/                 # 各页面 ViewModel，暴露 UiState
└── ui/
    ├── theme/                 # 颜色、字体、主题
    ├── navigation/            # NavGraph 与路由
    ├── components/            # 复用组件 (PlayerCard, GameCard, TrophyItemCard)
    └── screens/               # 页面 (login/home/games/trophy/gamedetail/profile)
```

## 数据流

```
UI (Compose) → ViewModel (StateFlow) → Repository → { Retrofit API, Room DB }
```

Repository 优先读本地缓存，下拉刷新时从 PSN API 拉取最新数据并写回 Room。

## 注意事项

- NPSSO Token 有效期较短，请及时使用；应用会自动刷新 `access_token`
- 奖杯图标资源位于 `app/src/main/res/drawable-nodpi/`（白金/金/银/铜四张透明 PNG）
- 游戏详情页的奖杯图标使用 PSN 官方远程图标（Coil 加载）
- 本项目仅供个人学习与数据查看使用，请遵守 Sony 相关服务条款

## License

未指定。
