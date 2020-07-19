# app-back-notice
定时系统

## 功能
1. 定时刷新wx的access_token，成功后缓存到redis（等价于一个中控服务器，统一获取和刷新 access_token）
2. 定时同步es与mysql
3. 订阅提醒推送（实现中）
