package com.example.spaceinvaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SpaceInvaders extends SurfaceView implements SurfaceHolder.Callback, GestureDetector.OnGestureListener,Runnable {
    SurfaceHolder surfaceHolder;
    public SpaceInvaders(Context context, AttributeSet attr) {
        super(context,attr);
        surfaceHolder=this.getHolder();
        surfaceHolder.addCallback(this);
        setFocusable(true);
    }

    //-----------------------------------------------------------------------------------------GestureDetector
    @Override
    public boolean onDown(MotionEvent e) {
        switch (MainFlag){
            case 0:
                float sx = e.getX();
                float sy = e.getY();
                if ((sx>300 && sx<800) && (sy>1350 && sy<1550)){
                    init();
                    MainFlag = 2;
                }
                break;
            case 1:
                float rx = e.getX();
                float ry = e.getY();
                if ((rx>300 && rx<800) && (ry>1350 && ry<1550)){
                    MainFlag = 3;
                }
                if ((rx>300 && rx<800) && (ry>1650 && ry<1850)){
                    MainFlag = 0;
                }
        }
        return false;
    }


    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    Boolean okmove = false;
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (okmove) {
            MainX -= distanceX;
            MainY -= distanceY;
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    //-----------------------------------------------------------------------------SurfaceView
    int MainFlag = 0;
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        Thread MainThread = new Thread(this);
        MainThread.start();
    }
    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        MainFlag=-1;
        imter.interrupt();
        enemymv.interrupt();
        thunder.interrupt();
        trackball.interrupt();
        CircleATK.interrupt();
    }
    //-----------------------------------------------------------------------------------------------------------BasicSetover
    //--------------------------------------------------------------------------MainThread
    int timestamp=0;
    int loadingcount = 0;
    @Override
    public void run() {
        while (MainFlag!=-1){
            switch (MainFlag){
                case 0:
                    //start
                    DrawStart();
                    break;
                case 1:
                    //retry
                    timestamp=0;
                    loadingcount=0;
                    imter.interrupt();
                    enemymv.interrupt();
                    thunder.interrupt();
                    trackball.interrupt();
                    CircleATK.interrupt();
                    DrawRetry();
                    break;
                case 2:
                    //ingame
                    Ingame();
                    timestamp++;
                    anion=true;
                    break;
                case 3:
                    drawloading();
                    loadingcount++;
                    timestamp=0;
                    break;
            }
        }

    }
    //---------------------------------------------MainFunction
    Canvas mcanvas;
    int okgame=0;
    int StartFlag=0;
    private void DrawStart(){
        mcanvas=surfaceHolder.lockCanvas();
        mcanvas.drawColor(Color.BLACK);
        Paint titlea = new Paint();
        titlea.setColor(Color.WHITE);
        titlea.setTextSize(120);
        titlea.setFakeBoldText(true);
        mcanvas.drawText("SPACE",200,550,titlea);
        Paint titleb = new Paint();
        titleb.setColor(Color.WHITE);
        titleb.setTextSize(120);
        titleb.setFakeBoldText(true);
        mcanvas.drawText("INVADER",400,700,titleb);
        Paint rect = new Paint();
        rect.setColor(Color.WHITE);
        Paint text = new Paint();
        text.setTextSize(80);
        text.setColor(Color.BLACK);
        mcanvas.drawRect(300,1350,800,1550,rect);
        mcanvas.drawText("START",(float)430,(float)1475,text);
        surfaceHolder.unlockCanvasAndPost(mcanvas);
    }
    private void DrawRetry(){
        mcanvas = surfaceHolder.lockCanvas();
        mcanvas.drawColor(Color.BLACK);
        Paint rect = new Paint();
        rect.setColor(Color.GRAY);
        Paint text = new Paint();
        text.setTextSize(80);
        text.setColor(Color.BLACK);
        Paint gmovr = new Paint();
        gmovr.setColor(Color.WHITE);
        gmovr.setTextSize(100);

        mcanvas.drawRect(300,1350,800,1550,rect);
        mcanvas.drawText("Retry",(float)460,(float)1475,text);
        mcanvas.drawRect(300,1650,800,1850,rect);
        mcanvas.drawText("Back to title",(float)340,(float)1775,text);
        if (!winflag){
            mcanvas.drawText("GAME OVER",(float)280,(float)575,gmovr);
        }else {
            mcanvas.drawText("Congratulation",(float)220,(float)575,gmovr);
            mcanvas.drawText("全都結束了嗎..",(float)250,(float)875,gmovr);
        }
        if (sect!=0){
            Paint tim = new Paint();
            tim.setColor(Color.WHITE);
            tim.setTextSize(80);
            if (winflag){
                if (mint!=0){
                    mcanvas.drawText("使用時間 "+mint+" 分 "+sect+" 秒",200,1000,tim);
                }else {
                    mcanvas.drawText("使用時間 "+sect+" 秒",250,1000,tim);
                }
            }else {
                if (mint!=0){
                    mcanvas.drawText("存活時間 "+mint+" 分 "+sect+" 秒",200,1000,tim);
                }else {
                    mcanvas.drawText("存活時間 "+sect+" 秒",250,1000,tim);
                }
            }
        }
        surfaceHolder.unlockCanvasAndPost(mcanvas);
    }
    private void Ingame(){
        //initial
        mcanvas = surfaceHolder.lockCanvas();
        mcanvas.drawColor(Color.BLACK);
        //Background
        PaintBackground();
        //wake the thread of enemy movement process
        //on first run
        if (timestamp>=30 && StartFlag==0){
            if (okgame==2){
                animation.interrupt();
//                new Thread(enemymv).start();
                new Thread(CircleATK).start();
                new Thread(trackball).start();
                new Thread(thunder).start();
                new Thread(imter).start();
                animation.interrupt();
                StartFlag=1;
                BarrrageON = true;
                winflag=false;


            }else if (okgame==0){
                new Thread(animation).start();
                okgame=1;
            }else {}

        }

        Drawthunder();

        //Enemy
        DrawEnemy();
        BossLife();
        //MainCharactor
        DrawMainCharactor();
        if (okgame==2 && timestamp>60) {
            PlrBullet();
        }
        //Attack from enemy
        DrawCircleBarrage();
        DrawLazer();
        DrawTracker();
        //
        Paint hitb = new Paint();
        hitb.setColor(Color.LTGRAY);
        mcanvas.drawCircle(MainX,MainY,8,hitb);

        //end
        surfaceHolder.unlockCanvasAndPost(mcanvas);
    }

    private void init(){
        lifbar=1050;
        EnX=500;
        EnY=-300;
        MainX=550;
        MainY=2400;
        BackX = new ArrayList();
        BackY = new ArrayList();
        PlrbulletX = new ArrayList();
        PlrbulletY = new ArrayList();
        StartFlag=0;
        CircleBarrageX = new ArrayList();
        CircleBarrageY = new ArrayList();
        CircleBarragedX = new ArrayList();
        CircleBarragedY = new ArrayList();
        plrlife=3;
        okgame=0;
        timestamp=0;
        op=false;
        BarrrageON = false;
        LazerX = new ArrayList();
        LazerY = new ArrayList();
        LazerdY = new ArrayList();
        types=2;
        tt=0;
        sect=0;
        mint=0;
        oktrack=false;
        tracker=false;
        TrackerX = new ArrayList();
        TrackerY = new ArrayList();
        TrackerdX = new ArrayList();
        TrackerdY = new ArrayList();
        Madness=false;
        thunderStatus = new ArrayList();
    }

    private void drawloading(){
        mcanvas = surfaceHolder.lockCanvas();
        mcanvas.drawColor(Color.BLACK);
        Paint text = new Paint();
        text.setColor(Color.WHITE);
        text.setTextSize(80);
        if (loadingcount%5==0) {
            mcanvas.drawText("LOADING..", 350, 900, text);
        }else{
            mcanvas.drawText("LOADING....", 350, 900, text);
        }
        surfaceHolder.unlockCanvasAndPost(mcanvas);
        if (isgameover() && loadingcount>50){
            init();
            MainFlag = 2;
        }
    }
    //----------------------------------gameoversetting
    private Boolean isgameover(){
        if (isCircleATKover && isTrackerover && isthunderover){
            return true;
        }else {
            return false;
        }
    }

    int mint=0;
    int sect=0;
    private void ongameover(){
        BarrrageON=false;
        okgame=0;
        anion=false;
        if (CircleBarrageX.size()!=0){CircleBarrageX.clear();}
        if (CircleBarrageY.size()!=0){CircleBarrageY.clear();}
        if (CircleBarragedX.size()!=0){CircleBarragedX.clear();}
        if (CircleBarragedY.size()!=0){CircleBarragedY.clear();}

        mint = tt/60;
        sect = tt%60;
    }

    //--------------------------------------------------------------------------PaintingFunction
    //-----------------------------------------------Background
    List BackX;
    List BackY;
    private void PaintBackground(){
        float rnd;
        //Generate Background List
        while (BackX.size()<=50){
            rnd = (float) (Math.random()*1200);
            BackX.add(rnd);
        }
        while (BackY.size()<=50){
            rnd = (float) (Math.random()*2000);
            BackY.add(rnd-1000);
        }

        //process Background event
        for (int i=0; i<BackX.size();i++){
            float temp =(Float) BackY.get(i)+100;
            if (temp>2500){
                BackY.set(i,temp-2500);
            } else {
                BackY.set(i,temp);
            }
            Paint dust = new Paint();
            dust.setColor(Color.WHITE);
            mcanvas.drawCircle((Float)BackX.get(i),(Float)BackY.get(i),2,dust);
        }
//        Paint timstp = new Paint();
//        timstp.setTextSize(30);
//        timstp.setColor(Color.WHITE);
//        mcanvas.drawText(String.valueOf(timestamp),0,40,timstp);
    }

    //-----------------------------------------------PLAYER
    float MainX=550,MainY=2400;
    private void DrawMainCharactor(){
        if (okgame==2) {
            if (MainX + 30 > 1080) {
                MainX = 1050;
            } else if (MainX - 30 < 0) {
                MainX = 30;
            }
            if (MainY + 30 > 1950) {
                MainY = 1900;
            } else if (MainY - 30 < 0) {
                MainY = 30;
            }
        }
        Paint MainCh = new Paint();

        MainCh.setColor(Color.WHITE);
        MainCh.setStyle(Paint.Style.STROKE);
        MainCh.setStrokeWidth(10);
        Path path = new Path();
        path.moveTo(MainX,MainY-30);
        path.lineTo(MainX+30,MainY);
        path.lineTo(MainX,MainY+30);
        path.lineTo(MainX-30,MainY);
        path.lineTo(MainX,MainY-30);

        Paint hitpoint = new Paint();
        switch (plrlife) {
            case 3:
                hitpoint.setColor(Color.BLUE);
                break;
            case 2:
                hitpoint.setColor(Color.MAGENTA);
                break;
            case 1:
                hitpoint.setColor(Color.RED);
                break;

        }
        mcanvas.drawPath(path,MainCh);
        mcanvas.drawCircle(MainX,MainY,20,hitpoint);
    }

    List PlrbulletX;
    List PlrbulletY;
    private void PlrBullet(){
        Paint bullet = new Paint();
        bullet.setColor(Color.WHITE);
        //generate bullet
        if (timestamp%14==0){
            PlrbulletX.add(MainX);
            PlrbulletY.add(MainY-30);
        }

        //bullet movement
        for (int i =0;i<PlrbulletY.size();i++){
            PlrbulletY.set(i,(Float)PlrbulletY.get(i)-30);
            //bullet destroy
            if ((float)PlrbulletY.get(i)<=-10){
                PlrbulletX.remove(i);
                PlrbulletY.remove(i);
            }
        }
        //Draw bullet
        for (int i=0 ;i<PlrbulletY.size();i++) {
            float px= (float) PlrbulletX.get(i);
            float py= (float) PlrbulletY.get(i);
            mcanvas.drawRect(px-8,py-16,px+8,py+16,bullet);
        }
        bosshit();
    }

    Boolean winflag = false;
    private void bosshit() {
        //Boss size 140+120 w/h
        //hitbox x>enx+50 && x<enx+50 && y<eny+45 && y<eny+45
        for (int i=0;i<PlrbulletX.size();i++){
            float x = (float) PlrbulletX.get(i);
            float y = (float) PlrbulletY.get(i);
            if ((x>EnX && x<EnX+140) && (y>EnY && y< EnY+120)){
                lifbar-=4;
                PlrbulletX.remove(i);
                PlrbulletY.remove(i);
                if (lifbar<0){
                    winflag=true;
                    MainFlag=1;
                    ongameover();
                }else if (lifbar<550){
                    types=4;
                    Madness=true;
                }else if (lifbar<600){
                    types=5;
                }else if (lifbar<900){
                    types=4;
                }
            }
        }
    }

    int plrlife;


    //-----------------------------------------------Enemy
    float EnX=500,EnY=300;
    private void DrawEnemy(){
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.invader);
        mcanvas.drawBitmap(bitmap,(Float)EnX,(Float)EnY,p);
    }

    float lifbar=1100;
    private void BossLife(){
        Paint Lif = new Paint();
        Lif.setColor(Color.RED);
        mcanvas.drawRect(0,0,lifbar,8,Lif);
    }

    //---------------------------------------------------------------------------Opening Animation Thread

    Boolean op = false;
    Boolean anion = false;
    Thread animation = new Thread(new Runnable() {
        @Override
        public void run() {
            while (anion) {
                try {
                    if (!op) {
                        opening();
                        animation.sleep(20);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    private void opening() {

        if (MainY > 1500) {
            MainY = MainY - 10;
            timestamp = 0;
        }else if (EnY<300){
            timestamp = 0;
            EnY = EnY +10;
        }else {
            okgame=2;
            anion=false;
//            okfire=true;
            okmove=true;
            op = true;
        }

    }

    //----------------------------------------------------------------------------Enemymovement Thread

    Boolean moving = false;
    Boolean enemThreadclose=false;

    Boolean Bossmov = false;
    Thread enemymv = new Thread(new Runnable() {
        @Override
        public void run() {
            enemThreadclose=false;
            while (okgame==2){
                if (timestamp==40){
                    for (int i=0;i<3;i++){
                        try {
                            movementv2();
                            enemymv.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (Bossmov){
                        try {
                            for (int i=0;i<=3;i++){
                                movementv2();
                                enemymv.sleep(10);
                            }
                            Bossmov=false;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                }
            }
            enemThreadclose=true;
        }
    });
    private void movementv2(){
        while (true) {
            int Direction = (int) Math.floor(Math.random() * 7);
            int mx=1, my=1;
            switch (Direction) {
                case 0:
                    mx = 1;
                    my = 1;
                    break;
                case 1:
                    mx = -1;
                    my = 1;
                    break;
                case 2:
                    mx = 1;
                    my = -1;
                    break;
                case 3:
                    mx = -1;
                    my = -1;
                    break;
                case 4:
                    mx = -1;
                    my = 0;
                    break;
                case 5:
                    mx = 1;
                    my = 0;
                    break;
                case 6:
                    mx = 0;
                    my = 1;
                    break;
//                case 6:
//                    mx = 0;
//                    my = 1;
//                    break;
//                case 7:
//                    mx = 0;
//                    my = -1;
//                    break;
            }
            int astep = 50;
            if ((((astep*mx)+EnX)>30)&&(((astep*mx)+EnX)<900) && (((astep*my)+EnY)>30)&&(((astep*my)+EnY)<500)){
                EnX+=(astep*mx);
                EnY+=(astep*my);
                break;
            }
        }
    }

    //----------------------------------------------------------------------------------------BarrageAttack
    //-------------------------------------------------------Thread
    int types = 2;
    Boolean BarrrageON = false;
    Boolean isCircleATKover = true;
    Thread CircleATK = new Thread(new Runnable() {
        @Override
        public void run() {
            isCircleATKover = false;
            while (okgame==2){
                try {

                    if ((timestamp%110==0)&&(!moving)&&BarrrageON){
                        int ATKtype = (int) Math.floor(Math.random() * types);
                        if (Madness){
                            tracker=true;
                        }
                        switch (ATKtype){
                            case 0:
                                for (int i=0;i<=20;i++){
                                    CircleBarragev3();
                                    CircleATK.sleep(20);
                                }
                                break;
                            case 1:
                                for (int i=0;i<=10;i++){
                                    FrontLazer();
                                    CircleATK.sleep(10);
                                }
                                break;
                            case 2:
                                for (int i=0;i<=5;i++){
                                    CircleBarrage();
                                    CircleATK.sleep(900);
                                }
                                break;
                            case 3:
                                for (int i=0;i<=5;i++){
                                    SquareBarrage();
                                    CircleATK.sleep(900);
                                }
                                break;
                            case 4:
                                for (int i=0;i<=5;i++){
                                    Square2Barrage();
                                    CircleATK.sleep(900);
                                }
                                break;
                            case 5:
                                tracker=true;
                                CircleATK.sleep(3000);
                                break;
                        }
                        if (Madness){
                            tracker=true;
                        }

                        CircleATK.sleep(500);
                        for (int i=0;i<3;i++){
                            movementv2();
                            CircleATK.sleep(20);
                        }


                        CircleATK.sleep(1400);
                        Bossmov=true;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            isCircleATKover = true;
        }
    });


    //-----------------------------------------CircleType1
    List CircleBarrageX;
    List CircleBarrageY;
    List CircleBarragedX;
    List CircleBarragedY;
    private void CircleBarragev3(){
        float angle = (float) (2*Math.PI)/360;
        for (int i = 0; i <36000; i+=2250) {
            float cotane = (float) ((float) angle *(i*(0.01)));
            CircleGenerate(cotane,i);
        }
    }
    private void CircleGenerate(double angle,int ind){
        float EnXmidX = EnX+50;
        float EnYmidY = EnY+40;

        int distance = 70;
        double mathX=Math.cos(angle);
        double mathY=Math.sin(angle);

        float DistanceX = Float.valueOf(Math.round(EnXmidX+(mathX*distance)));
        float DistanceY = Float.valueOf(Math.round(EnYmidY+(mathY*distance)));
        CircleBarrageX.add(DistanceX);
        CircleBarrageY.add(DistanceY);

        float speed=3.48f;
        if (ind%4500==0){
            speed = 4.f;
        }
        float DividX = Float.valueOf((float) Math.round((mathX*speed)));
        float DividY = Float.valueOf((float) Math.round((mathY*speed)));
        CircleBarragedX.add(DividX);
        CircleBarragedY.add(DividY);
    }
    private void DrawCircleBarrage(){
        Paint bu = new Paint();
        bu.setColor(Color.WHITE);
        for (int i=0;i<CircleBarrageX.size();i++){
            mcanvas.drawCircle((float)CircleBarrageX.get(i),(float)CircleBarrageY.get(i),40,bu);

            CircleBarrageX.set(i,(float)CircleBarrageX.get(i)+(float)CircleBarragedX.get(i));
            CircleBarrageY.set(i,(float)CircleBarrageY.get(i)+(float)CircleBarragedY.get(i));
            if ((float)CircleBarrageX.get(i)>1200 || (float)CircleBarrageY.get(i)>2500){
                CircleBarrageX.remove(i);
                CircleBarrageY.remove(i);
                CircleBarragedX.remove(i);
                CircleBarragedY.remove(i);
            }else {
                float bx = (float) CircleBarrageX.get(i);
                float by = (float) CircleBarrageY.get(i);
                if (Math.sqrt(Math.pow(MainX - bx, 2) + Math.pow(MainY - by, 2)) <= (30 + 6)) {
                    plrlife--;
                    CircleBarrageX.remove(i);
                    CircleBarrageY.remove(i);
                    CircleBarragedX.remove(i);
                    CircleBarragedY.remove(i);

                    if (plrlife == 0) {
                        ongameover();
                        MainFlag = 1;

                    }
                }
            }
        }
    }
    //-----------------------------------------CircleType2
    private void CircleBarrage(){
        double angle = (2*Math.PI)/3600;
        for (int i=0; i<3600;i+=225){
            double cotane = angle*i;
            CircleGenerate(cotane);
        }
    }
    private void CircleGenerate(double angle){
        float EnXmidX = EnX+50;
        float EnYmidY = EnY+40;

        int distance = 70;
        double mathX=Math.cos(angle);
        double mathY=Math.sin(angle);

        float DistanceX = Float.valueOf(Math.round(EnXmidX+(mathX*distance)));
        float DistanceY = Float.valueOf(Math.round(EnYmidY+(mathY*distance)));
        CircleBarrageX.add(DistanceX);
        CircleBarrageY.add(DistanceY);

        float speed=3f;
        float DividX = Float.valueOf((float) Math.round((mathX*speed)));
        float DividY = Float.valueOf((float) Math.round((mathY*speed)));
        CircleBarragedX.add(DividX);
        CircleBarragedY.add(DividY);
    }

    //-----------------------------------------CircleType3
    private void SquareBarrage(){
        double angle = (2*Math.PI)/3600;
        for (int i=0; i<=3600;i+=225){
            double cotane = angle*i;
            SquareGenerate(cotane,i);
        }
    }
    private void SquareGenerate(double angle,int ind){
        float EnXmidX = EnX+50;
        float EnYmidY = EnY+40;

        int distance = 70;
        double mathX=Math.cos(angle);
        double mathY=Math.sin(angle);

        float DistanceX = Float.valueOf(Math.round(EnXmidX+(mathX*distance)));
        float DistanceY = Float.valueOf(Math.round(EnYmidY+(mathY*distance)));
        CircleBarrageX.add(DistanceX);
        CircleBarrageY.add(DistanceY);

        float speed=3f;
        if (ind==450||ind==1350||ind==2250||ind==3150){
            speed = 4f;
        }

        float DividX = Float.valueOf((float) Math.round((mathX*speed)));
        float DividY = Float.valueOf((float) Math.round((mathY*speed)));
        CircleBarragedX.add(DividX);
        CircleBarragedY.add(DividY);
    }
    //------------------------------------------CircleType4

    private void Square2Barrage(){
        double angle = (2*Math.PI)/3600;
        for (int i=0; i<3600;i+=225){
            double cotane = angle*i;
            Square2Generate(cotane,i);
        }
    }
    private void Square2Generate(double angle,int ind){
        float EnXmidX = EnX+50;
        float EnYmidY = EnY+40;

        int distance = 40;
        double mathX=Math.cos(angle);
        double mathY=Math.sin(angle);

        float DistanceX = Float.valueOf(Math.round(EnXmidX+(mathX*distance)));
        float DistanceY = Float.valueOf(Math.round(EnYmidY+(mathY*distance)));
        CircleBarrageX.add(DistanceX);
        CircleBarrageY.add(DistanceY);

        float speed=3f;
        if (ind==450||ind==1350||ind==2250||ind==3150){
            speed = 3.5f;
        }
        if (ind%900==0||ind==0){
            speed = 3.7f;
        }

        float DividX = Float.valueOf((float) Math.round((mathX*speed)));
        float DividY = Float.valueOf((float) Math.round((mathY*speed)));
        CircleBarragedX.add(DividX);
        CircleBarragedY.add(DividY);
    }

    //-----------------------------------------Front Lazer

    List LazerX;
    List LazerY;
    List LazerdY;
    private void FrontLazer(){
        float angle = (float) (2*Math.PI)/360;
        for (int i = 70; i <110; i+=1) {
            float cotane = (float) ((float) angle *(i));
            LazerGenerate(cotane);
        }
    }
    private void LazerGenerate(float angle){
        float EnXmidX = EnX+50;
        float EnYmidY = EnY+40;

        int distance = 70;
        double mathX=Math.cos(angle);
        double mathY=Math.sin(angle);

        float DistanceX = Float.valueOf(Math.round(EnXmidX+(mathX*distance)));
        float DistanceY = Float.valueOf(Math.round(EnYmidY+(mathY*distance)));
        LazerX.add(DistanceX);
        LazerY.add(DistanceY);

        float speed=15f;
        float DividY = Float.valueOf((float) Math.round((mathY*speed)));
        LazerdY.add(DividY);
    }
    private void DrawLazer(){
        Paint Lazer = new Paint();
        Lazer.setColor(Color.LTGRAY);
        Paint out = new Paint();
        out.setColor(Color.WHITE);
        for (int i=0;i<LazerX.size();i++){
            mcanvas.drawCircle((float)LazerX.get(i),(float)LazerY.get(i),30,Lazer);


            LazerY.set(i,(float)LazerY.get(i)+(float)LazerdY.get(i));
            if ((float)LazerY.get(i)>2500){
                LazerX.remove(i);
                LazerY.remove(i);
                LazerdY.remove(i);
            }else {
                float bx = (float)LazerX.get(i);
                float by = (float)LazerY.get(i);
                if (Math.sqrt(Math.pow(MainX-bx,2)+Math.pow(MainY-by,2))<=(30+6)){
                    plrlife--;
                    LazerX.remove(i);
                    LazerY.remove(i);
                    LazerdY.remove(i);

                    if (plrlife<0){
                        ongameover();
                        MainFlag=1;
                    }
                }
            }
        }
    }

    //------------------------------------------------------------------------------PlayerTraceBullet

    Boolean Madness = false;
    Boolean isTrackerover=true;
    Boolean tracker=false;
    Thread trackball = new Thread(new Runnable() {
        @Override
        public void run() {
            isTrackerover=false;
            while (okgame==2){
                if ((tracker&&!oktrack)){
                    try {
                        float Mx = MainX;
                        float My = MainY;
                        trackerGenerate1(Mx,My);
                        trackball.sleep(2000);
                        oktrack=true;
                        trackball.sleep(3000);
                        oktrack=false;
                        tracker=false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            isTrackerover=true;
        }
    });
    List TrackerX;
    List TrackerY;
    List TrackerdX;
    List TrackerdY;
    private void trackerGenerate1(float x,float y){
        float angle = (float) (2*Math.PI)/360;
        for (int i = 0; i <36000; i+=4500) {
            float cotane = (float) ((float) angle *(i*(0.01)));
            trackerGenerate2(cotane,x,y);
        }
    }
    private void trackerGenerate2(float angle,float x , float y){

        int distance = 200;
        double mathX=Math.cos(angle);
        double mathY=Math.sin(angle);

        float DistanceX = Float.valueOf(Math.round(x-(mathX*distance)));
        float DistanceY = Float.valueOf(Math.round(y-(mathY*distance)));
        TrackerX.add(DistanceX);
        TrackerY.add(DistanceY);

        float speed=5f;
        float DividX = Float.valueOf((float) Math.round((mathX*speed)));
        float DividY = Float.valueOf((float) Math.round((mathY*speed)));
        TrackerdX.add(DividX);
        TrackerdY.add(DividY);
    }
    Boolean oktrack=false;
    private void DrawTracker(){
        Paint bu = new Paint();
        bu.setColor(Color.RED);
        Paint out = new Paint();
        out.setColor(Color.WHITE);
        for (int i=0;i<TrackerX.size();i++){
            mcanvas.drawCircle((float)TrackerX.get(i),(float)TrackerY.get(i),24,out);
            mcanvas.drawCircle((float)TrackerX.get(i),(float)TrackerY.get(i),20,bu);


            if (oktrack) {
                TrackerX.set(i, (float) TrackerX.get(i) + (float) TrackerdX.get(i));
                TrackerY.set(i, (float) TrackerY.get(i) + (float) TrackerdY.get(i));
            }
            if ((float)TrackerX.get(i)>1200 || (float)TrackerY.get(i)>2500||!tracker){
                TrackerX.remove(i);
                TrackerY.remove(i);
                TrackerdX.remove(i);
                TrackerdY.remove(i);
            }else {
                float bx = (float)TrackerX.get(i);
                float by = (float)TrackerY.get(i);
                if (Math.sqrt(Math.pow(MainX-bx,2)+Math.pow(MainY-by,2))<=(10+6)){
                    plrlife--;
                    TrackerX.remove(i);
                    TrackerY.remove(i);
                    TrackerdX.remove(i);
                    TrackerdY.remove(i);

                    if (plrlife<0){
                        ongameover();
                        MainFlag=1;

                    }
                }
            }
        }

    }

    //------------------------------------------------------------------------------------ThunderThread

    Boolean isthunderover=true;
    Thread thunder = new Thread(new Runnable() {
        @Override
        public void run() {
            isthunderover=false;
            while (okgame==2){
                if (Madness){
                    try {
                        float mx = MainX;
                        Genthunder(0,mx);
                        thunder.sleep(1000);
                        Genthunder(1,mx);
                        thunder.sleep(500);
                        Genthunder(2,mx);
                        thunder.sleep(1000);
                        thunderStatus.clear();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
            isthunderover=true;
        }
    });

    List thunderStatus;
    private void Genthunder(int step,float x){
        switch (step){
            case 0:
                thunderStatus.clear();
                thunderStatus.add(0);
                thunderStatus.add(x);
                break;
            case 1:
                thunderStatus.clear();
                thunderStatus.add(1);
                thunderStatus.add(x);
                break;
            case 2:
                thunderStatus.clear();
                thunderStatus.add(2);
                thunderStatus.add(x);
                break;
        }
    }
    private void Drawthunder(){
        Paint light = new Paint();
        if(thunderStatus.size()!=0){
            float x = (float) thunderStatus.get(1);
            switch ((int)thunderStatus.get(0)){
                case 0:
                    light.setColor(Color.RED);
                    mcanvas.drawRect(x-30,-10,x+30,2500,light);
                    break;
                case 1:
                    light.setColor(Color.WHITE);
                    mcanvas.drawRect(x-10,-10,x+10,2500,light);
                    break;
                case 2:
                    light.setColor(Color.YELLOW);
                    mcanvas.drawRect(x-30,-10,x+30,2500,light);
                    if ((MainX>x-30) && (MainX<x+30)){
                        plrlife--;
                        if (plrlife==0){
                            ongameover();
                            MainFlag = 1;
                        }
                    }
                    break;
            }
        }
    }



    //-----------------------------------------------------------------------------------------------------------TimerThread

    int tt = 0;
    Thread imter = new Thread(new Runnable() {
        @Override
        public void run() {
            while (okgame==2){
                tt++;
                try {
                    imter.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });


}
