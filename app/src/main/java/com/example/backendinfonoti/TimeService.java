package com.example.backendinfonoti;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.format.Time;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;


public class TimeService extends Service {
    private boolean isRun;// 线程是否继续的标志
    private Handler handler1; // 显示当前时间线程消息处理器。
    private Handler handler2;// 推送通知栏消息的线程消息处理器。
    private int notificationCounter;// 一个用于计算通知多少的计数器。

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRun = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRun = true;
        final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);// 注册通知管理器
        new Thread(new Runnable() {
            @Override
            // 在Runnable中，如果要让线程自己一直跑下去，必须自己定义while结构
            // 如果这个run()方法读完了，则整个线程自然死亡
            public void run() {
                // 定义一个线程中止标志
                while (isRun) {
                    try {
                        Thread.sleep(2000);// Java中线程的休眠，必须在try-catch结构中，每2s秒运行一次的意思
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!isRun) {
                        break;
                    }
                    Message msg = new Message(); // 在安卓中，不要在线程中直接现实方法，这样app容易崩溃，有什么要搞，扔到消息处理器中实现。
                    handler1.sendMessage(msg);
                }
            }
        }).start();// 默认线程不启动，必须自己start()
        // 不停在接受线程的消息，根据消息的参数，进行处理 ，这里没有传递过来的参数
        handler1 = new Handler(new Handler.Callback() {// 这样写，就不弹出什么泄漏的警告了
            @Override
            public boolean handleMessage(Message msg) {
                // 安卓显示当前时间的方法
                Time time = new Time();
                time.setToNow();
                String currentTime = time.format("%Y-%m-%d %H:%M:%S");
                Toast.makeText(getApplicationContext(),
                        "当前时间为：" + currentTime, Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });
        new Thread(new Runnable() {
            @Override
            // 在Runnable中，如果要让线程自己一直跑下去，必须自己定义while结构
            // 如果这个run()方法读完了，则整个线程自然死亡
            public void run() {
                // 定义一个线程中止标志
                while (isRun) {
                    try {
                        Thread.sleep(1000);// Java中线程的休眠，必须在try-catch结构中
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!isRun) {
                        break;
                    }
                    Message msg = new Message();
                    handler2.sendMessage(msg);
                }
            }
        }).start();// 默认线程不启动，必须自己start()
        handler2 = new Handler(new Handler.Callback() {// 这样写，就不弹出什么泄漏的警告了
            @SuppressWarnings("deprecation")
            @Override
            // 这里notification.setLatestEventInfo，
            // 设置通知标题与内容会被eclipse标志过时，
            // 但新的方法，使用builder去设置通知的方法只能应用于android3.0以上的设备，对于android2.2的设备是无法使用的。
            // 在现时国内有部分设备还是在android2.2的情况下，还是用这条几乎兼容所有版本安卓的“过时”方法吧！
            public boolean handleMessage(Message msg) {
                notificationCounter++;// 计数器+1

//                Notification notification = new NotificationCompat.Builder(MainActivity.this)
//                        .setContentTitle("这是测试通知标题")  //设置标题
//                        .setContentText("这是测试通知内容") //设置内容
//                        .setWhen(System.currentTimeMillis())  //设置时间
//                        .setSmallIcon(R.mipmap.ic_launcher)  //设置小图标  只能使用alpha图层的图片进行设置
//                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))   //设置大图标
//                        .setContentIntent(pi)
//                        //                       .setAutoCancel(true)
//                        .build();
//                manager.notify(1,notification);
                RemoteViews remoteView;
                remoteView = new RemoteViews(getPackageName(), R.layout.widget);
                Notification notification = new Notification();
                notification.contentView = remoteView;
                notification.icon = R.mipmap.ic_launcher;
//                notification = R.drawable.ic_launcher_foreground;// 设置通知图标为app的图标
                notification.flags = Notification.FLAG_AUTO_CANCEL;// 点击通知打开引用程序之后通知自动消失
                notification.tickerText = "显示通知";// 在用户没有拉开标题栏之前，在标题栏中显示的文字
                notification.when = System.currentTimeMillis();// 设置发送时间
                notification.defaults = Notification.DEFAULT_ALL;// 设置使用默认声音、震动、闪光灯
                // 以下三行：在安卓设备任意环境中中，如果点击信息则打开MainActivity
//                Intent intent = new Intent(getApplicationContext(),
//                        MainActivity.class);
//                PendingIntent pendingIntent = PendingIntent
//                        .getActivity(getApplicationContext(), 0,
//                                intent, 0);

//                notification.setLatestEventInfo(
//                        getApplicationContext(), "消息标题", "消息内容，第"
//                                + notificationCounter + "条通知",
//                        pendingIntent);
                notificationManager.notify(notificationCounter,
                        notification);// 要求通知管理器发送这条通知，其中第一个参数是通知在系统的id
                return false;
            }
        });
        return START_STICKY;// 这个返回值其实并没有什么卵用，除此以外还有START_NOT_STICKY与START_REDELIVER_INTENT
    }

}

